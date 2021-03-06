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
import moze_intel.projecte.emc.FuelMapper
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates
import moze_intel.projecte.utils.EMCHelper
import moze_intel.projecte.utils.ItemHelper
import moze_intel.projecte.utils.WorldHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.world.EnumSkyBlock
import net.minecraftforge.common.BiomeDictionary
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.wrapper.CombinedInvWrapper
import net.minecraftforge.items.wrapper.RangedWrapper
import kotlin.math.cos
import kotlin.math.min

open class CollectorMK1Tile : TileEmc, IEmcProvider, IEmcGen {

    private val input by lazy { this.StackHandler(invSize) }
    private val auxSlots by lazy { this.StackHandler(3) }
    private val toSort by lazy { CombinedInvWrapper(RangedWrapper(auxSlots, UPGRADING_SLOT, UPGRADING_SLOT + 1), input) }
    private val automationInput by lazy {
        object : WrappedItemHandler(input, WriteMode.IN) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                return if (SlotPredicates.COLLECTOR_INV.test(stack))
                    super.insertItem(slot, stack, simulate)
                else
                    stack
            }
        }
    }
    private val automationAuxSlots by lazy {
        object : WrappedItemHandler(auxSlots, WriteMode.OUT) {
            override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
                return if (slot == UPGRADE_SLOT)
                    super.extractItem(slot, amount, simulate)
                else
                    ItemStack.EMPTY
            }
        }
    }

    private val emcGen: Double
    private var unprocessedEMC: Double = 0.0
    private var hasChargeableItem: Boolean = false
    private var hasFuel: Boolean = false
    private var storedFuelEmc: Double = 0.toDouble()
    private var extraEMCAdded: Boolean = false

    val aux: IItemHandler
        get() = auxSlots

    open val invSize: Int
        get() = 8

    private val upgraded
        get() = auxSlots.getStackInSlot(UPGRADE_SLOT)

    private val lock: ItemStack
        get() = auxSlots.getStackInSlot(LOCK_SLOT)

    private val upgrading: ItemStack
        get() = auxSlots.getStackInSlot(UPGRADING_SLOT)

    val sunLevel: Float
        get() {
            if (world.provider.isNether) {
                return 0.0f
            }
            var sunBrightness = limit(cos(world.getCelestialAngleRadians(1.0f).toDouble()).toFloat() * 2.0f + 0.2f, 0.0f, 1.0f)
            if (!BiomeDictionary.hasType(world.getBiome(pos), BiomeDictionary.Type.SANDY)) {
                sunBrightness *= 1.0f - world.getRainStrength(1.0f) * 5.0f / 16.0f
                sunBrightness *= 1.0f - world.getThunderStrength(1.0f) * 5.0f / 16.0f

                sunBrightness = limit(sunBrightness, 0.0f, 1.0f)
            }
            val light = world.getLightFor(EnumSkyBlock.SKY, pos.up())
            return light / 15.0f * sunBrightness
        }

    val emcToNextGoal: Long
        get() = if (!lock.isEmpty) {
            (EMCHelper.getEmcValue(lock) - EMCHelper.getEmcValue(upgrading))
        } else {
            (EMCHelper.getEmcValue(FuelMapper.getFuelUpgrade(upgrading)) - EMCHelper.getEmcValue(upgrading))
        }

    val itemCharge: Long
        get() = if (!upgrading.isEmpty && upgrading.item is IItemEmc) {
            (upgrading.item as IItemEmc).getStoredEmc(upgrading)
        } else -1

    val itemChargeProportion: Double
        get() {
            val charge = itemCharge

            return if (upgrading.isEmpty || charge <= 0 || upgrading.item !is IItemEmc) {
                -1.0
            } else charge.toDouble() / (upgrading.item as IItemEmc).getMaximumEmc(upgrading)

        }

    val fuelProgress: Double
        get() {
            if (upgrading.isEmpty || !FuelMapper.isStackFuel(upgrading)) {
                return 0.0
            }

            val reqEmc: Long

            if (!lock.isEmpty) {
                reqEmc = EMCHelper.getEmcValue(lock) - EMCHelper.getEmcValue(upgrading)

                if (reqEmc < 0) {
                    return 0.0
                }
            } else {
                if (FuelMapper.getFuelUpgrade(upgrading).isEmpty) {
                    auxSlots.setStackInSlot(UPGRADING_SLOT, ItemStack.EMPTY)
                    return 0.0
                } else {
                    reqEmc = EMCHelper.getEmcValue(FuelMapper.getFuelUpgrade(upgrading)) - EMCHelper.getEmcValue(upgrading)
                }

            }

            return if (storedEmc >= reqEmc) {
                1.0
            } else storedEmc / reqEmc.toDouble()

        }

    constructor() : this(Constants.COLLECTOR_MK1_MAX, Constants.COLLECTOR_MK1_GEN)

    constructor(maxEmc: Long, emcGen: Double) : super(maxEmc) {
        this.emcGen = emcGen
    }

    fun getInput(): IItemHandler {
        return input
    }

    override fun hasCapability(cap: Capability<*>, side: EnumFacing?): Boolean {
        return cap === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side)
    }

    override fun <T> getCapability(cap: Capability<T>, side: EnumFacing?): T? {
        return if (cap === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side != null && side.axis.isVertical) {
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(automationAuxSlots)
            } else {
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast<T>(automationInput)
            }
        } else super.getCapability(cap, side)
    }

    override fun update() {
        if (world.isRemote)
            return

        ItemHelper.compactInventory(toSort)
        checkFuelOrKlein()
        updateEmc()
        rotateUpgraded()
    }

    private fun rotateUpgraded() {
        if (!upgraded.isEmpty) {
            if (lock.isEmpty
                    || upgraded.item !== lock.item
                    || upgraded.count >= upgraded.maxStackSize) {
                auxSlots.setStackInSlot(UPGRADE_SLOT, ItemHandlerHelper.insertItemStacked(input, upgraded.copy(), false))
            }
        }
    }

    private fun checkFuelOrKlein() {
        if (!upgrading.isEmpty && upgrading.item is IItemEmc) {
            val itemEmc = upgrading.item as IItemEmc
            if (itemEmc.getStoredEmc(upgrading) != itemEmc.getMaximumEmc(upgrading)) {
                hasChargeableItem = true
                hasFuel = false
            } else {
                hasChargeableItem = false
            }
        } else if (!upgrading.isEmpty) {
            hasFuel = true
            hasChargeableItem = false
        } else {
            hasFuel = false
            hasChargeableItem = false
        }
    }

    private fun updateEmc() {
        if (!this.hasMaxedEmc()) {
            updateEmc(false)
        }

        if (this.storedEmc == 0L) {
            return
        } else if (hasChargeableItem) {
            var toSend: Long = if (this.storedEmc < Constants.RELAY_MK1_OUTPUT) this.storedEmc else Constants.RELAY_MK1_OUTPUT
            val item = upgrading.item as IItemEmc

            val itemEmc = item.getStoredEmc(upgrading)
            val maxItemEmc = item.getMaximumEmc(upgrading)

            if (itemEmc + toSend > maxItemEmc) {
                toSend = maxItemEmc - itemEmc
            }

            item.addEmc(upgrading, toSend)
            this.removeEMC(toSend)
        } else if (hasFuel) {
            if (FuelMapper.getFuelUpgrade(upgrading).isEmpty) {
                auxSlots.setStackInSlot(UPGRADING_SLOT, ItemStack.EMPTY)
            }

            val result = if (lock.isEmpty) FuelMapper.getFuelUpgrade(upgrading) else lock.copy()

            val upgradeCost = EMCHelper.getEmcValue(result) - EMCHelper.getEmcValue(upgrading)

            if (upgradeCost > 0 && this.storedEmc >= upgradeCost) {
                val upgrade = upgraded

                if (upgraded.isEmpty) {
                    this.removeEMC(upgradeCost)
                    auxSlots.setStackInSlot(UPGRADE_SLOT, result)
                    upgrading.shrink(1)
                } else if (ItemHelper.basicAreStacksEqual(result, upgrade) && upgrade.count < upgrade.maxStackSize) {
                    this.removeEMC(upgradeCost)
                    upgraded.grow(1)
                    upgrading.shrink(1)
                }
            }
        } else {
            val toSend: Long = if (this.storedEmc < Constants.RELAY_MK1_OUTPUT) this.storedEmc else Constants.RELAY_MK1_OUTPUT
            this.sendToAllAcceptors(toSend)
            this.sendRelayBonus()
        }
    }

    override fun updateEmc(isExtra: Boolean) {
        val emcToAdd = getSunRelativeEmc(emcGen) / 20.0f
        extraEMCAdded = if (isExtra) {
            if (!extraEMCAdded && emcToAdd > 0.0) {
                emcToAdd / 2
                true
            }
            else return
        } else {
            false
        }
        unprocessedEMC += emcToAdd
        if (unprocessedEMC > 1.0){
            val emc = unprocessedEMC.toLong()
            this.addEMC(emc)
            unprocessedEMC -= emc
        }
    }

    private fun getSunRelativeEmc(emc: Double): Double {
        return emc * sunLevel
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        storedFuelEmc = nbt.getDouble("FuelEMC")
        input.deserializeNBT(nbt.getCompoundTag("Input"))
        auxSlots.deserializeNBT(nbt.getCompoundTag("AuxSlots"))
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        var nbt = nbt
        nbt = super.writeToNBT(nbt)
        nbt.setDouble("FuelEMC", storedFuelEmc)
        nbt.setTag("Input", input.serializeNBT())
        nbt.setTag("AuxSlots", auxSlots.serializeNBT())
        return nbt
    }

    private fun sendRelayBonus() {
        for ((dir, tile) in WorldHelper.getAdjacentTileEntitiesMapped(world, this)) {

            if (tile is RelayMK4Tile) {
                tile.acceptEMC(dir, Constants.RELAY_MK4_BONUS.toDouble())
            } else (tile as? RelayMK3Tile)?.acceptEMC(dir, Constants.RELAY_MK3_BONUS.toDouble())
                    ?: ((tile as? RelayMK2Tile)?.acceptEMC(dir, Constants.RELAY_MK2_BONUS.toDouble()) ?: if (tile is RelayMK1Tile) {
                        tile.acceptEMC(dir, Constants.RELAY_MK1_BONUS.toDouble())
                    })
        }
    }

    override fun provideEMC(side: EnumFacing, toExtract: Long): Long {
        val toRemove = min(currentEMC, toExtract)
        removeEMC(toRemove)
        return toRemove
    }

    companion object {
        const val UPGRADING_SLOT = 0
        const val UPGRADE_SLOT = 1
        const val LOCK_SLOT = 2

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
