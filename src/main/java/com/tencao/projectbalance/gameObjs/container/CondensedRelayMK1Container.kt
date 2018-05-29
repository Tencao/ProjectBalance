package com.tencao.projectbalance.gameObjs.container

import com.tencao.projectbalance.gameObjs.tile.CondensedRelayMK1Tile
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot
import moze_intel.projecte.network.PacketHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IContainerListener
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class CondensedRelayMK1Container(invPlayer: InventoryPlayer, internal val tile: CondensedRelayMK1Tile) : Container() {
    var kleinChargeProgress = 0.0
    var inputBurnProgress = 0.0
    var emc = 0
    var sunLevel: Int = 0

    init {
        this.initSlots(invPlayer)
    }

    internal open fun initSlots(invPlayer: InventoryPlayer) {
        val input = tile.getInput()
        val output = tile.getOutput()

        //Klein Star charge slot
        this.addSlotToContainer(ValidatedSlot(input, 0, 67, 43, SlotPredicates.RELAY_INV))

        var counter = input.slots - 1
        //Main Relay inventory
        for (i in 0..1)
            for (j in 0..2)
                this.addSlotToContainer(ValidatedSlot(input, counter--, 27 + i * 18, 17 + j * 18, SlotPredicates.RELAY_INV))

        //Burning slot
        this.addSlotToContainer(ValidatedSlot(output, 0, 127, 43, SlotPredicates.IITEMEMC))

        //Player Inventory
        for (i in 0..2)
            for (j in 0..8)
                this.addSlotToContainer(Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 95 + i * 18))

        //Player Hotbar
        for (i in 0..8)
            this.addSlotToContainer(Slot(invPlayer, i, 8 + i * 18, 153))
    }

    override fun addListener(listener: IContainerListener) {
        super.addListener(listener)
        PacketHandler.sendProgressBarUpdateInt(listener, this, 0, tile.storedEmc.toInt())
        PacketHandler.sendProgressBarUpdateInt(listener, this, 1, (tile.itemChargeProportion * 8000).toInt())
        PacketHandler.sendProgressBarUpdateInt(listener, this, 2, (tile.inputBurnProportion * 8000).toInt())
        PacketHandler.sendProgressBarUpdateInt(listener, this, 3, (tile.sunLevel * 16).toInt())
    }

    override fun detectAndSendChanges() {
        super.detectAndSendChanges()

        if (emc != tile.storedEmc.toInt()) {
            for (icrafting in this.listeners) {
                PacketHandler.sendProgressBarUpdateInt(icrafting, this, 0, tile.storedEmc.toInt())
            }

            emc = tile.storedEmc.toInt()
        }

        if (kleinChargeProgress != tile.itemChargeProportion) {
            for (icrafting in this.listeners) {
                PacketHandler.sendProgressBarUpdateInt(icrafting, this, 1, (tile.itemChargeProportion * 8000).toInt())
            }

            kleinChargeProgress = tile.itemChargeProportion
        }

        if (inputBurnProgress != tile.inputBurnProportion) {
            for (icrafting in this.listeners) {
                PacketHandler.sendProgressBarUpdateInt(icrafting, this, 2, (tile.inputBurnProportion * 8000).toInt())
            }

            inputBurnProgress = tile.inputBurnProportion
        }

        if (sunLevel != (tile.sunLevel * 16f).toInt()) {
            for (icrafting in this.listeners) {
                PacketHandler.sendProgressBarUpdateInt(icrafting, this, 0, (tile.sunLevel * 16f).toInt())
            }

            sunLevel = (tile.sunLevel * 16f).toInt()
        }

    }

    @SideOnly(Side.CLIENT)
    override fun updateProgressBar(id: Int, data: Int) {
        when (id) {
            0 -> emc = data
            1 -> kleinChargeProgress = data / 8000.0
            2 -> inputBurnProgress = data / 8000.0
            3 -> sunLevel = data
        }
    }

    override fun transferStackInSlot(player: EntityPlayer?, slotIndex: Int): ItemStack {
        val slot = this.getSlot(slotIndex)

        if (slot == null || !slot.hasStack) {
            return ItemStack.EMPTY
        }

        val stack = slot.stack
        val newStack = stack.copy()

        if (slotIndex < 8) {
            if (!this.mergeItemStack(stack, 8, this.inventorySlots.size, true))
                return ItemStack.EMPTY
            slot.onSlotChanged()
        } else if (!this.mergeItemStack(stack, 0, 7, false)) {
            return ItemStack.EMPTY
        }
        if (stack.isEmpty) {
            slot.putStack(ItemStack.EMPTY)
        } else {
            slot.onSlotChanged()
        }

        return slot.onTake(player, newStack)
    }

    override fun canInteractWith(player: EntityPlayer): Boolean {
        return player.getDistanceSq(tile.pos.x + 0.5, tile.pos.y + 0.5, tile.pos.z + 0.5) <= 64.0
    }
}