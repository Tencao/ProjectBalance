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

import com.tencao.projectbalance.gameObjs.tile.CondensedRelayMK4Tile
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

class CondensedRelayMK4Container(invPlayer: InventoryPlayer, relay: CondensedRelayMK4Tile) : CondensedRelayMK1Container(invPlayer, relay) {

    override fun initSlots(invPlayer: InventoryPlayer) {
        val input = tile.getInput()
        val output = tile.getOutput()

        //Burn slot
        this.addSlotToContainer(ValidatedSlot(input, 0, 104, 58, SlotPredicates.RELAY_INV))

        var counter = input.slots - 1
        //Inventory Buffer
        for (i in 0..3)
            for (j in 0..4)
                this.addSlotToContainer(ValidatedSlot(input, counter--, 28 + i * 18, 18 + j * 18, SlotPredicates.RELAY_INV))

        //Klein star charge
        this.addSlotToContainer(ValidatedSlot(output, 0, 164, 58, SlotPredicates.IITEMEMC))

        //Main player inventory
        for (i in 0..2)
            for (j in 0..8)
                this.addSlotToContainer(Slot(invPlayer, j + i * 9 + 9, 26 + j * 18, 113 + i * 18))

        //Player hotbar
        for (i in 0..8)
            this.addSlotToContainer(Slot(invPlayer, i, 26 + i * 18, 171))
    }

    override fun transferStackInSlot(player: EntityPlayer?, slotIndex: Int): ItemStack {
        val slot = this.getSlot(slotIndex)

        if (slot == null || !slot.hasStack) {
            return ItemStack.EMPTY
        }

        val stack = slot.stack
        val newStack = stack.copy()

        if (slotIndex < 22) {
            if (!this.mergeItemStack(stack, 22, this.inventorySlots.size, true))
                return ItemStack.EMPTY
            slot.onSlotChanged()
        } else if (!this.mergeItemStack(stack, 0, 21, false)) {
            return ItemStack.EMPTY
        }
        if (stack.isEmpty) {
            slot.putStack(ItemStack.EMPTY)
        } else {
            slot.onSlotChanged()
        }

        return slot.onTake(player, newStack)
    }
}