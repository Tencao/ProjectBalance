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

import be.bluexin.saomclib.packets.PacketPipeline
import com.tencao.projectbalance.gameObjs.container.inventory.TransmutationInventory
import com.tencao.projectbalance.gameObjs.container.slots.transmutation.*
import com.tencao.projectbalance.handlers.getInternalCooldowns
import com.tencao.projectbalance.network.SearchUpdatePacket
import moze_intel.projecte.utils.EMCHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.ClickType
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand

class TransmutationContainer(invPlayer: InventoryPlayer, val transmutationInventory: TransmutationInventory, hand: EnumHand?) : Container() {
    private val blocked: Int

    init {

        // Transmutation Inventory
        this.addSlotToContainer(SlotInput(transmutationInventory, 0, 43, 23))
        this.addSlotToContainer(SlotInput(transmutationInventory, 1, 34, 41))
        this.addSlotToContainer(SlotInput(transmutationInventory, 2, 52, 41))
        this.addSlotToContainer(SlotInput(transmutationInventory, 3, 16, 50))
        this.addSlotToContainer(SlotInput(transmutationInventory, 4, 70, 50))
        this.addSlotToContainer(SlotInput(transmutationInventory, 5, 34, 59))
        this.addSlotToContainer(SlotInput(transmutationInventory, 6, 52, 59))
        this.addSlotToContainer(SlotInput(transmutationInventory, 7, 43, 77))
        this.addSlotToContainer(SlotLock(transmutationInventory, 8, 158, 50))
        this.addSlotToContainer(SlotConsume(transmutationInventory, 9, 107, 97))
        this.addSlotToContainer(SlotUnlearn(transmutationInventory, 10, 89, 97))
        this.addSlotToContainer(SlotOutput(transmutationInventory, 11, 123, 30))
        this.addSlotToContainer(SlotOutput(transmutationInventory, 12, 140, 13))
        this.addSlotToContainer(SlotOutput(transmutationInventory, 13, 158, 9))
        this.addSlotToContainer(SlotOutput(transmutationInventory, 14, 176, 13))
        this.addSlotToContainer(SlotOutput(transmutationInventory, 15, 193, 30))
        this.addSlotToContainer(SlotOutput(transmutationInventory, 16, 199, 50))
        this.addSlotToContainer(SlotOutput(transmutationInventory, 17, 193, 70))
        this.addSlotToContainer(SlotOutput(transmutationInventory, 18, 176, 87))
        this.addSlotToContainer(SlotOutput(transmutationInventory, 19, 158, 91))
        this.addSlotToContainer(SlotOutput(transmutationInventory, 20, 140, 87))
        this.addSlotToContainer(SlotOutput(transmutationInventory, 21, 123, 70))
        this.addSlotToContainer(SlotOutput(transmutationInventory, 22, 116, 50))
        this.addSlotToContainer(SlotOutput(transmutationInventory, 23, 158, 31))
        this.addSlotToContainer(SlotOutput(transmutationInventory, 24, 139, 50))
        this.addSlotToContainer(SlotOutput(transmutationInventory, 25, 177, 50))
        this.addSlotToContainer(SlotOutput(transmutationInventory, 26, 158, 69))

        //Player Inventory
        for (i in 0..2)
            for (j in 0..8)
                this.addSlotToContainer(Slot(invPlayer, j + i * 9 + 9, 35 + j * 18, 117 + i * 18))

        //Player Hotbar
        for (i in 0..8)
            this.addSlotToContainer(Slot(invPlayer, i, 35 + i * 18, 175))

        blocked = if (hand == EnumHand.MAIN_HAND) inventorySlots.size - 1 - (8 - invPlayer.currentItem) else -1
    }

    override fun canInteractWith(var1: EntityPlayer): Boolean {
        return true
    }

    override fun transferStackInSlot(player: EntityPlayer?, slotIndex: Int): ItemStack {
        val slot = this.getSlot(slotIndex)

        if (slot == null || !slot.hasStack) {
            return ItemStack.EMPTY
        }

        val stack = slot.stack
        val newStack = stack.copy()
        newStack.count = 1

        if (slotIndex <= 7) { //Input Slots
            return ItemStack.EMPTY
        } else if (slotIndex in 11..26) { // Output Slots
            val emc = EMCHelper.getEmcValue(newStack)

            for (i in 1..player!!.getInternalCooldowns().getStackLimit(newStack)) {
                if (transmutationInventory.provider.emc >= emc) {
                    transmutationInventory.removeEmc(emc)
                    player.getInternalCooldowns().setStack(newStack.copy())
                } else break
            }

            if (player.world.isRemote)
                transmutationInventory.updateClientTargets()

        } else if (slotIndex > 26) {
            val emc = EMCHelper.getEmcSellValue(stack)

            if (emc == 0L) {
                return ItemStack.EMPTY
            }

            while (!transmutationInventory.hasMaxedEmc() && stack.count > 0) {
                transmutationInventory.addEmc(emc)
                stack.shrink(1)
            }

            transmutationInventory.handleKnowledge(newStack)

            if (stack.isEmpty) {
                slot.putStack(ItemStack.EMPTY)
            }
        }

        return ItemStack.EMPTY
    }

    override fun slotClick(slot: Int, button: Int, flag: ClickType?, player: EntityPlayer): ItemStack {
        if (player.entityWorld.isRemote && transmutationInventory.getHandlerForSlot(slot) === transmutationInventory.outputs) {
            PacketPipeline.sendToServer(SearchUpdatePacket(transmutationInventory.getIndexFromSlot(slot), getSlot(slot).stack))
        }

        return if (slot == blocked) {
            ItemStack.EMPTY
        } else super.slotClick(slot, button, flag, player)

    }

    override fun canDragIntoSlot(slot: Slot?): Boolean {
        return !(slot is SlotConsume || slot is SlotUnlearn || slot is SlotInput || slot is SlotLock || slot is SlotOutput)
    }
}
