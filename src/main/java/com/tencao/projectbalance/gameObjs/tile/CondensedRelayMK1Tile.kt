/*
 * Copyright (C) 2018
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.tencao.projectbalance.gameObjs.tile

import com.tencao.projectbalance.utils.Constants
import moze_intel.projecte.api.item.IItemEmc
import moze_intel.projecte.api.tile.IEmcProvider
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates
import moze_intel.projecte.utils.EMCHelper
import moze_intel.projecte.utils.ItemHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.world.EnumSkyBlock
import net.minecraftforge.common.BiomeDictionary
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler

open class CondensedRelayMK1Tile internal constructor(sizeInv: Int, maxEmc: Int, private val chargeRate: Int, private val emcGen: Float) : TileEmc(maxEmc), IEmcProvider, IEmcGen {
    private val input: ItemStackHandler
    private val output = this.StackHandler(1)
    private val automationInput: IItemHandler
    private val automationOutput = object : WrappedItemHandler(output, WrappedItemHandler.WriteMode.IN_OUT) {
        override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
            return if (SlotPredicates.IITEMEMC.test(stack))
                super.insertItem(slot, stack, simulate)
            else
                stack
        }

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
            val stack = getStackInSlot(slot)
            if (!stack.isEmpty && stack.item is IItemEmc) {
                val item = stack.item as IItemEmc
                return if (item.getStoredEmc(stack) >= item.getMaximumEmc(stack)) {
                    super.extractItem(slot, amount, simulate)
                } else {
                    ItemStack.EMPTY
                }
            }

            return super.extractItem(slot, amount, simulate)
        }
    }
    private var extraEMCAdded: Boolean = false

    private val charging: ItemStack
        get() = output.getStackInSlot(0)

    private val burn: ItemStack
        get() = input.getStackInSlot(0)

    val sunLevel: Float
        get() {
            if (world.provider.isNether) {
                return 0.0f
            }
            var sunBrightness = limit(Math.cos(world.getCelestialAngleRadians(1.0f).toDouble()).toFloat() * 2.0f + 0.2f, 0.0f, 1.0f)
            if (!BiomeDictionary.hasType(world.getBiome(pos), BiomeDictionary.Type.SANDY)) {
                sunBrightness *= 1.0f - world.getRainStrength(1.0f) * 5.0f / 16.0f
                sunBrightness *= 1.0f - world.getThunderStrength(1.0f) * 5.0f / 16.0f

                sunBrightness = limit(sunBrightness, 0.0f, 1.0f)
            }
            return world.getLightFor(EnumSkyBlock.SKY, pos) / 15.0f * sunBrightness
        }

    val itemChargeProportion: Double
        get() = if (!charging.isEmpty && charging.item is IItemEmc) {
            (charging.item as IItemEmc).getStoredEmc(charging) / (charging.item as IItemEmc).getMaximumEmc(charging)
        } else 0.0

    val inputBurnProportion: Double
        get() {
            if (burn.isEmpty) {
                return 0.0
            }

            return if (burn.item is IItemEmc) {
                (burn.item as IItemEmc).getStoredEmc(burn) / (burn.item as IItemEmc).getMaximumEmc(burn)
            } else burn.count / burn.maxStackSize.toDouble()

        }

    constructor() : this(7, Constants.CONDENSED_RELAY_MK1_MAX, Constants.CONDENSED_RELAY_MK1_OUTPUT, Constants.CONDENSED_RELAY_MK1_GEN)

    init {
        input = object : TileEmc.StackHandler(sizeInv) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                return if (SlotPredicates.RELAY_INV.test(stack))
                    super.insertItem(slot, stack, simulate)
                else
                    stack
            }
        }
        automationInput = WrappedItemHandler(input, WrappedItemHandler.WriteMode.IN)
    }

    override fun hasCapability(cap: Capability<*>, side: EnumFacing?): Boolean {
        return cap === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side)
    }

    override fun <T> getCapability(cap: Capability<T>, side: EnumFacing?): T? {
        return if (cap === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == EnumFacing.DOWN) {
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(automationOutput)
            } else
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast<T>(automationInput)
        } else super.getCapability(cap, side)
    }

    fun getInput(): IItemHandler {
        return input
    }

    fun getOutput(): IItemHandler {
        return output
    }

    override fun update() {
        if (world.isRemote) {
            return
        }

        sendEmc()
        ItemHelper.compactInventory(input)

        val stack = burn

        if (!stack.isEmpty) {
            if (stack.item is IItemEmc) {
                val itemEmc = stack.item as IItemEmc
                var emcVal = itemEmc.getStoredEmc(stack)

                if (emcVal > chargeRate) {
                    emcVal = chargeRate.toDouble()
                }

                if (emcVal > 0 && this.storedEmc + emcVal <= this.maximumEmc) {
                    this.addEMC(emcVal)
                    itemEmc.extractEmc(stack, emcVal)
                }
            } else {
                val emcVal = EMCHelper.getEmcSellValue(stack)

                if (emcVal > 0 && this.storedEmc + emcVal <= this.maximumEmc) {
                    this.addEMC(emcVal.toDouble())
                    burn.shrink(1)
                }
            }
        }

        val chargeable = charging

        if (!chargeable.isEmpty && this.storedEmc > 0 && chargeable.item is IItemEmc) {
            chargeItem(chargeable)
        }

        updateEmc(false)
    }

    override fun updateEmc(isExtra: Boolean) {
        if (isExtra) {
            if (!extraEMCAdded) {
                val emc = updateEmc()
                if (emc > 0f)
                    this.addEMC((emc / 2).toDouble())
                extraEMCAdded = true
            }
        } else {
            extraEMCAdded = false
            val emc = updateEmc()
            if (emc > 0f)
                this.addEMC(emc.toDouble())

            if (this.storedEmc != 0.0) {
                val toSend: Double = if (this.storedEmc < emcGen) this.storedEmc else emcGen.toDouble()
                this.sendToAllAcceptors(toSend)
            }
        }
    }

    private fun updateEmc(): Float {
        return if (!this.hasMaxedEmc()) {
            getSunRelativeEmc(emcGen) / 20.0f
        } else 0f
    }

    private fun getSunRelativeEmc(emc: Float): Float {
        return emc * sunLevel
    }

    private fun sendEmc() {
        if (this.storedEmc == 0.0) return

        if (this.storedEmc <= chargeRate) {
            this.sendToAllAcceptors(this.storedEmc)
        } else {
            this.sendToAllAcceptors(chargeRate.toDouble())
        }
    }

    private fun chargeItem(chargeable: ItemStack) {
        val itemEmc = chargeable.item as IItemEmc
        val starEmc = itemEmc.getStoredEmc(chargeable)
        val maxStarEmc = itemEmc.getMaximumEmc(chargeable)
        var toSend: Double = if (this.storedEmc < chargeRate) this.storedEmc else chargeRate.toDouble()

        if (starEmc + toSend <= maxStarEmc) {
            itemEmc.addEmc(chargeable, toSend)
            this.removeEMC(toSend)
        } else {
            toSend = maxStarEmc - starEmc
            itemEmc.addEmc(chargeable, toSend)
            this.removeEMC(toSend)
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        input.deserializeNBT(nbt.getCompoundTag("Input"))
        output.deserializeNBT(nbt.getCompoundTag("Output"))
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        var nbt = nbt
        nbt = super.writeToNBT(nbt)
        nbt.setTag("Input", input.serializeNBT())
        nbt.setTag("Output", output.serializeNBT())
        return nbt
    }

    override fun provideEMC(side: EnumFacing, toExtract: Double): Double {
        val toRemove = Math.min(currentEMC, toExtract)
        currentEMC -= toRemove
        return toRemove
    }

    companion object {

        fun limit(value: Float, min: Float, max: Float): Float {
            if (java.lang.Float.isNaN(value) || value <= min) {
                return min
            }
            return if (value >= max) {
                max
            } else value
        }
    }
}
