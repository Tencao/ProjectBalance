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

import com.tencao.projectbalance.gameObjs.tile.PowerFlowerMK1Tile
import moze_intel.projecte.gameObjs.container.slots.SlotCondenserLock
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot
import moze_intel.projecte.utils.EMCHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

class PowerFlowerMK2Container(invPlayer: InventoryPlayer, condenser: PowerFlowerMK1Tile) : PowerFlowerMK1Container(invPlayer, condenser) {

    override fun initSlots(invPlayer: InventoryPlayer) {
        this.addSlotToContainer(SlotCondenserLock(tile.lock, 0, 12, 6))

        val input = tile.input
        val output = tile.output

        //Condenser Inventory
        //Inputs
        for (i in 0..6)
            for (j in 0..5)
                this.addSlotToContainer(ValidatedSlot(input, j + i * 6, 12 + j * 18, 26 + i * 18) { s -> SlotPredicates.HAS_EMC.test(s) && !tile.isStackEqualToLock(s) })

        //Outputs
        for (i in 0..6)
            for (j in 0..5)
                this.addSlotToContainer(ValidatedSlot(output, j + i * 6, 138 + j * 18, 26 + i * 18) { s -> false })

        //Player Inventory
        for (i in 0..2)
            for (j in 0..8)
                this.addSlotToContainer(Slot(invPlayer, j + i * 9 + 9, 48 + j * 18, 154 + i * 18))

        //Player Hotbar
        for (i in 0..8)
            this.addSlotToContainer(Slot(invPlayer, i, 48 + i * 18, 212))
    }

    override fun transferStackInSlot(player: EntityPlayer?, slotIndex: Int): ItemStack {
        if (slotIndex == 0) {
            return ItemStack.EMPTY
        }

        val slot = this.getSlot(slotIndex)

        if (slot == null || !slot.hasStack) {
            return ItemStack.EMPTY
        }

        val stack = slot.stack

        if (slotIndex <= 84) {
            if (!this.mergeItemStack(stack, 85, 120, false)) {
                return ItemStack.EMPTY
            }
        } else if (!EMCHelper.doesItemHaveEmc(stack) || !this.mergeItemStack(stack, 1, 42, false)) {
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
