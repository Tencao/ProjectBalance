package com.tencao.projectbalance.gameObjs.container

import com.tencao.projectbalance.gameObjs.tile.RelayMK2Tile
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

class RelayMK2Container(invPlayer: InventoryPlayer, relay: RelayMK2Tile) : RelayMK1Container(invPlayer, relay) {

    override fun initSlots(invPlayer: InventoryPlayer) {
        val input = tile.getInput()
        val output = tile.getOutput()

        //Burn slot
        this.addSlotToContainer(ValidatedSlot(input, 0, 84, 44, SlotPredicates.RELAY_INV))

        var counter = input.slots - 1
        //Inventory buffer
        for (i in 0..2)
            for (j in 0..3)
                this.addSlotToContainer(ValidatedSlot(input, counter--, 26 + i * 18, 18 + j * 18, SlotPredicates.RELAY_INV))

        //Klein star slot
        this.addSlotToContainer(ValidatedSlot(output, 0, 144, 44, SlotPredicates.IITEMEMC))

        //Main player inventory
        for (i in 0..2)
            for (j in 0..8)
                this.addSlotToContainer(Slot(invPlayer, j + i * 9 + 9, 16 + j * 18, 101 + i * 18))

        //Player hotbar
        for (i in 0..8)
            this.addSlotToContainer(Slot(invPlayer, i, 16 + i * 18, 159))
    }

    override fun transferStackInSlot(player: EntityPlayer?, slotIndex: Int): ItemStack {
        val slot = this.getSlot(slotIndex)

        if (slot == null || !slot.hasStack) {
            return ItemStack.EMPTY
        }

        val stack = slot.stack
        val newStack = stack.copy()

        if (slotIndex < 14) {
            if (!this.mergeItemStack(stack, 14, this.inventorySlots.size, true))
                return ItemStack.EMPTY
            slot.onSlotChanged()
        } else if (!this.mergeItemStack(stack, 0, 13, false)) {
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
