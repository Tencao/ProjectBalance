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

package com.tencao.projectbalance.gameObjs.container

import com.tencao.projectbalance.gameObjs.blocks.MatterFurnace
import com.tencao.projectbalance.gameObjs.container.slots.ValidatedSlot
import com.tencao.projectbalance.gameObjs.tile.BMFurnaceTile
import moze_intel.projecte.api.item.IItemEmc
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IContainerListener
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.function.Predicate

class BMFurnaceContainer(invPlayer: InventoryPlayer, val tile: BMFurnaceTile) : Container() {

    private var lastCookTime: Int = 0
    private var lastBurnTime: Int = 0
    private var lastItemBurnTime: Int = 0

    init {
        val fuel = tile.getFuel()
        val input = tile.getInput()
        val output = tile.getOutput()

        //Fuel
        this.addSlotToContainer(ValidatedSlot(fuel, 0, 65, 53, SlotPredicates.FURNACE_FUEL))

        //Input(0)
        this.addSlotToContainer(ValidatedSlot(input, 0, 65, 17, SlotPredicates.SMELTABLE))

        var counter = input.slots - 1

        //Input storage
        for (i in 0..2)
            for (j in 0..3)
                this.addSlotToContainer(ValidatedSlot(input, counter--, 11 + i * 18, 8 + j * 18, SlotPredicates.SMELTABLE))

        counter = output.slots - 1

        //Output(0)
        this.addSlotToContainer(ValidatedSlot(output, counter--, 125, 35, Predicate{ false }))

        //Output Storage
        for (i in 0..2)
            for (j in 0..3)
                this.addSlotToContainer(ValidatedSlot(output, counter--, 147 + i * 18, 8 + j * 18, Predicate{ false }))

        //Player Inventory
        for (i in 0..2)
            for (j in 0..8)
                this.addSlotToContainer(Slot(invPlayer, j + i * 9 + 9, 24 + j * 18, 84 + i * 18))

        //Player HotBar
        for (i in 0..8)
            this.addSlotToContainer(Slot(invPlayer, i, 24 + i * 18, 142))
    }

    override fun canInteractWith(player: EntityPlayer): Boolean {
        return player.world.getBlockState(tile.pos).block is MatterFurnace
                && player.getDistanceSq(tile.pos.x + 0.5, tile.pos.y + 0.5, tile.pos.z + 0.5) <= 64.0
    }

    override fun addListener(par1IContainerListener: IContainerListener) {
        super.addListener(par1IContainerListener)
        par1IContainerListener.sendWindowProperty(this, 0, tile.furnaceCookTime)
        par1IContainerListener.sendWindowProperty(this, 1, tile.furnaceBurnTime)
        par1IContainerListener.sendWindowProperty(this, 2, tile.currentItemBurnTime)
    }

    override fun detectAndSendChanges() {
        super.detectAndSendChanges()

        for (crafter in this.listeners) {
            if (lastCookTime != tile.furnaceCookTime)
                crafter.sendWindowProperty(this, 0, tile.furnaceCookTime)

            if (lastBurnTime != tile.furnaceBurnTime)
                crafter.sendWindowProperty(this, 1, tile.furnaceBurnTime)

            if (lastItemBurnTime != tile.currentItemBurnTime)
                crafter.sendWindowProperty(this, 2, tile.currentItemBurnTime)
        }

        lastCookTime = tile.furnaceCookTime
        lastBurnTime = tile.furnaceBurnTime
        lastItemBurnTime = tile.currentItemBurnTime
    }

    @SideOnly(Side.CLIENT)
    override fun updateProgressBar(par1: Int, par2: Int) {
        if (par1 == 0)
            tile.furnaceCookTime = par2

        if (par1 == 1)
            tile.furnaceBurnTime = par2

        if (par1 == 2)
            tile.currentItemBurnTime = par2
    }

    override fun transferStackInSlot(player: EntityPlayer?, slotIndex: Int): ItemStack {
        val slot = this.getSlot(slotIndex)

        if (slot == null || !slot.hasStack) {
            return ItemStack.EMPTY
        }

        val stack = slot.stack
        val newStack = stack.copy()

        if (slotIndex <= 26) {
            if (!this.mergeItemStack(stack, 27, 63, false)) {
                return ItemStack.EMPTY
            }
        } else {

            if (TileEntityFurnace.isItemFuel(newStack) || newStack.item is IItemEmc) {
                if (!this.mergeItemStack(stack, 0, 1, false)) {
                    return ItemStack.EMPTY
                }
            } else if (!FurnaceRecipes.instance().getSmeltingResult(newStack).isEmpty) {
                if (!this.mergeItemStack(stack, 1, 14, false)) {
                    return ItemStack.EMPTY
                }
            } else {
                return ItemStack.EMPTY
            }
        }

        if (stack.isEmpty) {
            slot.putStack(ItemStack.EMPTY)
        } else {
            slot.onSlotChanged()
        }

        return newStack
    }

}