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
import moze_intel.projecte.api.tile.IEmcAcceptor
import moze_intel.projecte.api.tile.IEmcProvider
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates
import moze_intel.projecte.utils.EMCHelper
import moze_intel.projecte.utils.ItemHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import kotlin.math.min

open class RelayMK1Tile internal constructor(sizeInv: Int, maxEmc: Long, private val chargeRate: Long) : TileEmc(maxEmc), IEmcAcceptor, IEmcProvider {
    private val input by lazy {
        object : TileEmc.StackHandler(sizeInv) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                return if (SlotPredicates.RELAY_INV.test(stack))
                    super.insertItem(slot, stack, simulate)
                else
                    stack
            }
        }
    }
    private val output by lazy { this.StackHandler(1) }
    private val automationInput by lazy { WrappedItemHandler(input, WrappedItemHandler.WriteMode.IN) }
    private val automationOutput by lazy {
        object : WrappedItemHandler(output, WriteMode.IN_OUT) {
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
    }

    private val charging: ItemStack
        get() = output.getStackInSlot(0)

    private val burn: ItemStack
        get() = input.getStackInSlot(0)

    val itemChargeProportion: Double
        get() = if (!charging.isEmpty && charging.item is IItemEmc) {
            (charging.item as IItemEmc).getStoredEmc(charging).toDouble() / (charging.item as IItemEmc).getMaximumEmc(charging)
        } else 0.0

    val inputBurnProportion: Double
        get() {
            if (burn.isEmpty) {
                return 0.0
            }

            return if (burn.item is IItemEmc) {
                (burn.item as IItemEmc).getStoredEmc(burn).toDouble() / (burn.item as IItemEmc).getMaximumEmc(burn)
            } else burn.count / burn.maxStackSize.toDouble()

        }

    constructor() : this(7, Constants.RELAY_MK1_MAX, Constants.RELAY_MK1_OUTPUT)

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
                    emcVal = chargeRate
                }

                if (emcVal > 0 && this.storedEmc + emcVal <= this.maximumEmc) {
                    this.addEMC(emcVal)
                    itemEmc.extractEmc(stack, emcVal)
                }
            } else {
                val emcVal = EMCHelper.getEmcSellValue(stack)

                if (emcVal > 0 && this.storedEmc + emcVal <= this.maximumEmc) {
                    this.addEMC(emcVal)
                    burn.shrink(1)
                }
            }
        }

        val chargeable = charging

        if (!chargeable.isEmpty && this.storedEmc > 0 && chargeable.item is IItemEmc) {
            chargeItem(chargeable)
        }
    }

    private fun sendEmc() {
        if (this.storedEmc == 0L) return

        this.sendToAllAcceptors(min(storedEmc, chargeRate))
    }

    private fun chargeItem(chargeable: ItemStack) {
        val itemEmc = chargeable.item as IItemEmc
        val starEmc = itemEmc.getStoredEmc(chargeable)
        val maxStarEmc = itemEmc.getMaximumEmc(chargeable)
        var toSend: Long = min(this.storedEmc, chargeRate)

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

    override fun acceptEMC(side: EnumFacing, toAccept: Long): Long {
        return if (world.getTileEntity(pos.offset(side)) is RelayMK1Tile) {
            0L // Do not accept from other relays - avoid infinite loop / thrashing
        } else {
            val toAdd = min(maximumEMC - currentEMC, toAccept)
            currentEMC += toAdd
            toAdd
        }
    }

    override fun provideEMC(side: EnumFacing, toExtract: Long): Long {
        val toRemove = min(currentEMC, toExtract)
        currentEMC -= toRemove
        return toRemove
    }
}
