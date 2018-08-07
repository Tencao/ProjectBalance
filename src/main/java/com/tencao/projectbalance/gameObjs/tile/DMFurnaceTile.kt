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

import com.tencao.projectbalance.gameObjs.blocks.MatterFurnace
import moze_intel.projecte.api.item.IItemEmc
import moze_intel.projecte.api.tile.IEmcAcceptor
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates
import moze_intel.projecte.utils.ItemHelper
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.ISidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntityDropper
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.tileentity.TileEntityHopper
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.wrapper.CombinedInvWrapper
import net.minecraftforge.items.wrapper.InvWrapper
import net.minecraftforge.items.wrapper.SidedInvWrapper

open class DMFurnaceTile(val ticksBeforeSmelt: Int, val efficiencyBonus: Int): TileEmc(64), IEmcAcceptor {

    private val EMC_CONSUMPTION = 16f
    internal val inputInventory = StackHandler(getInvSize())
    internal val outputInventory = StackHandler(getInvSize())
    internal val fuelInv = StackHandler(1)
    private val automationInput = object : WrappedItemHandler(inputInventory, WrappedItemHandler.WriteMode.IN) {
        override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
            return if (SlotPredicates.SMELTABLE.test(stack))
                super.insertItem(slot, stack, simulate)
            else
                stack
        }
    }
    private val automationFuel = object : WrappedItemHandler(fuelInv, WrappedItemHandler.WriteMode.IN) {
        override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
            return if (SlotPredicates.FURNACE_FUEL.test(stack))
                super.insertItem(slot, stack, simulate)
            else
                stack
        }
    }

    private val automationOutput = WrappedItemHandler(outputInventory, WrappedItemHandler.WriteMode.OUT)
    private val automationSides = CombinedInvWrapper(automationFuel, automationOutput)
    private val joined = CombinedInvWrapper(automationInput, automationFuel, automationOutput)
    var furnaceBurnTime: Int = 0
    var currentItemBurnTime: Int = 0
    var furnaceCookTime: Int = 0

    constructor(): this(10, 3)

    open fun getInvSize(): Int {
        return 9
    }

    open fun getOreDoubleChance(): Float {
        return 0.5f
    }

    fun getFuel(): IItemHandler {
        return fuelInv
    }

    private fun getFuelItem(): ItemStack {
        return fuelInv.getStackInSlot(0)
    }

    fun getInput(): IItemHandler {
        return inputInventory
    }

    fun getOutput(): IItemHandler {
        return outputInventory
    }

    override fun hasCapability(cap: Capability<*>, side: EnumFacing?): Boolean {
        return cap === net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side)
    }

    override fun <T> getCapability(cap: Capability<T>, side: EnumFacing?): T? {
        return if (cap === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == null) {
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(joined)
            } else {
                when (side) {
                    EnumFacing.UP -> CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(automationInput)
                    EnumFacing.DOWN -> CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(automationOutput)
                    else -> CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast<T>(automationSides)
                }
            }
        } else super.getCapability<T>(cap, side)
    }

    override fun update() {
        val flag = furnaceBurnTime > 0
        var flag1 = false

        if (furnaceBurnTime > 0) {
            --furnaceBurnTime
        }

        if (!this.getWorld().isRemote) {
            pullFromInventories()
            ItemHelper.compactInventory(inputInventory)

            if (canSmelt() && !getFuelItem().isEmpty && getFuelItem().item is IItemEmc) {
                val itemEmc = getFuelItem().item as IItemEmc
                if (itemEmc.getStoredEmc(getFuelItem()) >= EMC_CONSUMPTION) {
                    itemEmc.extractEmc(getFuelItem(), EMC_CONSUMPTION.toDouble())
                    this.addEMC(EMC_CONSUMPTION.toDouble())
                }
            }

            if (this.storedEmc >= EMC_CONSUMPTION) {
                furnaceBurnTime = 1
                this.removeEMC(EMC_CONSUMPTION.toDouble())
            }

            if (furnaceBurnTime == 0 && canSmelt()) {
                furnaceBurnTime = getItemBurnTime(getFuelItem()) / 8
                currentItemBurnTime = furnaceBurnTime

                if (furnaceBurnTime > 0) {
                    flag1 = true

                    if (!getFuelItem().isEmpty) {
                        val copy = getFuelItem().copy()

                        getFuelItem().shrink(1)

                        if (getFuelItem().isEmpty) {
                            fuelInv.setStackInSlot(0, copy.item.getContainerItem(copy))
                        }
                    }
                }
            }

            if (furnaceBurnTime > 0 && canSmelt()) {
                ++furnaceCookTime

                if (furnaceCookTime == ticksBeforeSmelt) {
                    furnaceCookTime = 0
                    smeltItem()
                    flag1 = true
                }
            }

            if (flag != furnaceBurnTime > 0) {
                flag1 = true
                val block = world.getBlockState(pos).block

                if (!this.getWorld().isRemote && block is MatterFurnace) {
                    block.updateFurnaceBlockState(furnaceBurnTime > 0, world, getPos())
                }
            }

            if (flag1) {
                markDirty()
            }

            ItemHelper.compactInventory(outputInventory)
            pushToInventories()
        }
    }

    fun isBurning(): Boolean {
        return furnaceBurnTime > 0
    }

    private fun pullFromInventories() {
        val tile = this.getWorld().getTileEntity(pos.up())
        if (tile == null || tile is TileEntityHopper || tile is TileEntityDropper)
            return
        var handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN)

        if (handler == null) {
            handler = when (tile) {
                is ISidedInventory -> SidedInvWrapper(tile as ISidedInventory?, EnumFacing.DOWN)
                is IInventory -> InvWrapper(tile as IInventory?)
                else -> return
            }
        }

        for (i in 0 until handler.slots) {
            val extractTest = handler.extractItem(i, Integer.MAX_VALUE, true)
            if (extractTest.isEmpty)
                continue

            val targetInv = if (extractTest.item is IItemEmc || TileEntityFurnace.isItemFuel(extractTest))
                fuelInv
            else
                inputInventory

            val remainderTest = ItemHandlerHelper.insertItemStacked(targetInv, extractTest, true)
            val successfullyTransferred = extractTest.count - remainderTest.count

            if (successfullyTransferred > 0) {
                val toInsert = handler.extractItem(i, successfullyTransferred, false)
                val result = ItemHandlerHelper.insertItemStacked(targetInv, toInsert, false)
                assert(result.isEmpty)
            }
        }
    }

    private fun pushToInventories() {
        // todo push to others
    }

    open fun smeltItem() {
        val toSmelt = inputInventory.getStackInSlot(0)
        val smeltResult = FurnaceRecipes.instance().getSmeltingResult(toSmelt).copy()

        if (world.rand.nextFloat() < getOreDoubleChance() && ItemHelper.getOreDictionaryName(toSmelt).startsWith("ore")) {
            smeltResult.grow(smeltResult.count)
        }

        ItemHandlerHelper.insertItemStacked(outputInventory, smeltResult, false)

        toSmelt.shrink(1)
    }

    private fun canSmelt(): Boolean {
        val toSmelt = inputInventory.getStackInSlot(0)

        if (toSmelt.isEmpty) {
            return false
        }

        val smeltResult = FurnaceRecipes.instance().getSmeltingResult(toSmelt)
        if (smeltResult.isEmpty) {
            return false
        }

        val currentSmelted = outputInventory.getStackInSlot(outputInventory.slots - 1)

        if (currentSmelted.isEmpty) {
            return true
        }
        if (!smeltResult.isItemEqual(currentSmelted)) {
            return false
        }

        val result = currentSmelted.count + smeltResult.count
        return result <= currentSmelted.maxStackSize
    }

    private fun getItemBurnTime(stack: ItemStack): Int {
        val `val` = TileEntityFurnace.getItemBurnTime(stack)
        return `val` * ticksBeforeSmelt / 200 * efficiencyBonus
    }

    fun getCookProgressScaled(value: Int): Int {
        return (furnaceCookTime + if (isBurning() && canSmelt()) 1 else 0) * value / ticksBeforeSmelt
    }

    @SideOnly(Side.CLIENT)
    fun getBurnTimeRemainingScaled(value: Int): Int {
        if (this.currentItemBurnTime == 0)
            this.currentItemBurnTime = ticksBeforeSmelt

        return furnaceBurnTime * value / currentItemBurnTime
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        furnaceBurnTime = nbt.getShort("BurnTime").toInt()
        furnaceCookTime = nbt.getShort("CookTime").toInt()
        inputInventory.deserializeNBT(nbt.getCompoundTag("Input"))
        outputInventory.deserializeNBT(nbt.getCompoundTag("Output"))
        fuelInv.deserializeNBT(nbt.getCompoundTag("Fuel"))
        currentItemBurnTime = getItemBurnTime(getFuelItem())
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        var nbt = nbt
        nbt = super.writeToNBT(nbt)
        nbt.setShort("BurnTime", furnaceBurnTime.toShort())
        nbt.setShort("CookTime", furnaceCookTime.toShort())
        nbt.setTag("Input", inputInventory.serializeNBT())
        nbt.setTag("Output", outputInventory.serializeNBT())
        nbt.setTag("Fuel", fuelInv.serializeNBT())
        return nbt
    }

    override fun acceptEMC(side: EnumFacing, toAccept: Double): Double {
        if (this.storedEmc < EMC_CONSUMPTION) {
            val needed = EMC_CONSUMPTION - this.storedEmc
            val accept = Math.min(needed, toAccept)
            this.addEMC(accept)
            return accept
        }
        return 0.0
    }

}