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
import moze_intel.projecte.emc.FuelMapper
import moze_intel.projecte.gameObjs.container.slots.SlotGhost
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot
import moze_intel.projecte.network.PacketHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.ClickType
import net.minecraft.inventory.Container
import net.minecraft.inventory.IContainerListener
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class CollectorMK1Container(invPlayer: InventoryPlayer, internal val tile: CollectorMK1Tile) : Container() {
    var sunLevel = 0
    var emc = 0
    var kleinChargeProgress = 0.0
    var fuelProgress = 0.0
    var kleinEmc = 0

    init {
        this.initSlots(invPlayer)
    }

    internal open fun initSlots(invPlayer: InventoryPlayer) {
        val aux = tile.aux
        val main = tile.getInput()

        //Klein Star Slot
        this.addSlotToContainer(ValidatedSlot(aux, CollectorMK1Tile.UPGRADING_SLOT, 124, 58, SlotPredicates.COLLECTOR_INV))

        var counter = main.slots - 1
        //Fuel Upgrade storage
        for (i in 0..1)
            for (j in 0..3)
                this.addSlotToContainer(ValidatedSlot(main, counter--, 20 + i * 18, 8 + j * 18, SlotPredicates.COLLECTOR_INV))

        //Upgrade Result
        this.addSlotToContainer(ValidatedSlot(aux, CollectorMK1Tile.UPGRADE_SLOT, 124, 13, SlotPredicates.COLLECTOR_INV))

        //Upgrade Target
        this.addSlotToContainer(SlotGhost(aux, CollectorMK1Tile.LOCK_SLOT, 153, 36, SlotPredicates.COLLECTOR_LOCK))

        //Player inventory
        for (i in 0..2)
            for (j in 0..8)
                this.addSlotToContainer(Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))

        //Player hotbar
        for (i in 0..8)
            this.addSlotToContainer(Slot(invPlayer, i, 8 + i * 18, 142))
    }

    override fun addListener(listener: IContainerListener) {
        super.addListener(listener)
        PacketHandler.sendProgressBarUpdateInt(listener, this, 0, (tile.sunLevel * 16f).toInt())
        PacketHandler.sendProgressBarUpdateInt(listener, this, 1, tile.storedEmc.toInt())
        PacketHandler.sendProgressBarUpdateInt(listener, this, 2, (tile.itemChargeProportion * 8000).toInt())
        PacketHandler.sendProgressBarUpdateInt(listener, this, 3, (tile.fuelProgress * 8000).toInt())
        PacketHandler.sendProgressBarUpdateInt(listener, this, 4, (tile.itemCharge * 8000).toInt())
    }

    override fun slotClick(slot: Int, button: Int, flag: ClickType?, player: EntityPlayer): ItemStack {
        return if (slot >= 0 && getSlot(slot) is SlotGhost && !getSlot(slot).stack.isEmpty) {
            getSlot(slot).putStack(ItemStack.EMPTY)
            ItemStack.EMPTY
        } else {
            super.slotClick(slot, button, flag, player)
        }
    }

    override fun detectAndSendChanges() {
        super.detectAndSendChanges()

        if (sunLevel != (tile.sunLevel * 16f).toInt()) {
            for (icrafting in this.listeners) {
                PacketHandler.sendProgressBarUpdateInt(icrafting, this, 0, (tile.sunLevel * 16f).toInt())
            }

            sunLevel = (tile.sunLevel * 16f).toInt()
        }

        if (emc != tile.storedEmc.toInt()) {
            for (icrafting in this.listeners) {
                PacketHandler.sendProgressBarUpdateInt(icrafting, this, 1, tile.storedEmc.toInt())
            }

            emc = tile.storedEmc.toInt()
        }

        if (kleinChargeProgress != tile.itemChargeProportion) {
            for (icrafting in this.listeners) {
                PacketHandler.sendProgressBarUpdateInt(icrafting, this, 2, (tile.itemChargeProportion * 8000).toInt())
            }

            kleinChargeProgress = tile.itemChargeProportion
        }

        if (fuelProgress != tile.fuelProgress) {
            for (icrafting in this.listeners) {
                PacketHandler.sendProgressBarUpdateInt(icrafting, this, 3, (tile.fuelProgress * 8000).toInt())
            }

            fuelProgress = tile.fuelProgress
        }

        if (kleinEmc != tile.itemCharge.toInt()) {
            for (icrafting in this.listeners) {
                PacketHandler.sendProgressBarUpdateInt(icrafting, this, 4, tile.itemCharge.toInt())
            }

            kleinEmc = tile.itemCharge.toInt()
        }

    }

    @SideOnly(Side.CLIENT)
    override fun updateProgressBar(id: Int, data: Int) {
        when (id) {
            0 -> sunLevel = data
            1 -> emc = data
            2 -> kleinChargeProgress = data / 8000.0
            3 -> fuelProgress = data / 8000.0
            4 -> kleinEmc = data
        }
    }

    override fun transferStackInSlot(player: EntityPlayer?, slotIndex: Int): ItemStack {
        val slot = this.getSlot(slotIndex)

        if (slot == null || !slot.hasStack) {
            return ItemStack.EMPTY
        }

        val stack = slot.stack
        val newStack = stack.copy()

        if (slotIndex <= 10) {
            if (!this.mergeItemStack(stack, 11, 46, false)) {
                return ItemStack.EMPTY
            }
        } else if (slotIndex <= 46) {
            if (!FuelMapper.isStackFuel(stack) || FuelMapper.isStackMaxFuel(stack) || !this.mergeItemStack(stack, 1, 8, false)) {
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

    override fun canInteractWith(player: EntityPlayer): Boolean {
        return player.getDistanceSq(tile.pos.x + 0.5, tile.pos.y + 0.5, tile.pos.z + 0.5) <= 64.0
    }
}