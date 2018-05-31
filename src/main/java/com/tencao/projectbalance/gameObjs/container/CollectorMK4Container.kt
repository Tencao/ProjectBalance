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

import com.tencao.projectbalance.gameObjs.tile.CollectorMK1Tile
import com.tencao.projectbalance.gameObjs.tile.CollectorMK4Tile
import moze_intel.projecte.emc.FuelMapper
import moze_intel.projecte.gameObjs.container.slots.SlotGhost
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack


class CollectorMK4Container(invPlayer: InventoryPlayer, collector: CollectorMK4Tile) : CollectorMK1Container(invPlayer, collector) {

    override fun initSlots(invPlayer: InventoryPlayer) {
        val aux = tile.aux
        val main = tile.getInput()

        //Klein Star Slot
        this.addSlotToContainer(ValidatedSlot(aux, CollectorMK1Tile.UPGRADING_SLOT, 158, 58, SlotPredicates.COLLECTOR_INV))

        var counter = main.slots - 1
        //Fuel Upgrade Slot
        for (i in 0..3)
            for (j in 0..3)
                this.addSlotToContainer(ValidatedSlot(main, counter--, 18 + i * 18, 8 + j * 18, SlotPredicates.COLLECTOR_INV))

        //Upgrade Result
        this.addSlotToContainer(ValidatedSlot(aux, CollectorMK1Tile.UPGRADE_SLOT, 158, 13, SlotPredicates.COLLECTOR_INV))

        //Upgrade Target
        this.addSlotToContainer(SlotGhost(aux, CollectorMK1Tile.LOCK_SLOT, 187, 36, SlotPredicates.COLLECTOR_LOCK))

        //Player inventory
        for (i in 0..2)
            for (j in 0..8)
                this.addSlotToContainer(Slot(invPlayer, j + i * 9 + 9, 30 + j * 18, 84 + i * 18))

        //Player hotbar
        for (i in 0..8)
            this.addSlotToContainer(Slot(invPlayer, i, 30 + i * 18, 142))
    }

    override fun transferStackInSlot(player: EntityPlayer?, slotIndex: Int): ItemStack {
        val slot = this.getSlot(slotIndex)

        if (slot == null || !slot.hasStack) {
            return ItemStack.EMPTY
        }

        val stack = slot.stack
        val newStack = stack.copy()

        if (slotIndex <= 18) {
            if (!this.mergeItemStack(stack, 19, 54, false)) {
                return ItemStack.EMPTY
            }
        } else if (slotIndex <= 54) {
            if (!FuelMapper.isStackFuel(stack) || FuelMapper.isStackMaxFuel(stack) || !this.mergeItemStack(stack, 1, 16, false)) {
                return ItemStack.EMPTY
            }
        } else {
            return ItemStack.EMPTY
        }

        if (stack.isEmpty) {
            slot.putStack(ItemStack.EMPTY)
        } else {
            slot.onSlotChanged()
        }

        return slot.onTake(player, stack)
    }
}