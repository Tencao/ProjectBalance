package com.tencao.projectbalance.gameObjs.container

import com.tencao.projectbalance.gameObjs.tile.CondenserTile
import moze_intel.projecte.gameObjs.container.slots.SlotCondenserLock
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot
import moze_intel.projecte.network.PacketHandler
import moze_intel.projecte.utils.Constants
import moze_intel.projecte.utils.EMCHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.ClickType
import net.minecraft.inventory.Container
import net.minecraft.inventory.IContainerListener
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class CondenserContainer(invPlayer: InventoryPlayer, internal val tile: CondenserTile) : Container() {
    var displayEmc: Int = 0
    var requiredEmc: Int = 0
    var requiredTime: Int = 0
    var timePassed: Int = 0
    var tomes: Int = 0

    val progressScaled: Int
        get() {
            if (requiredEmc == 0) {
                return 0
            }

            return if (displayEmc >= requiredEmc && requiredTime > 100) {
                timePassed * Constants.MAX_CONDENSER_PROGRESS / requiredTime
            } else displayEmc * Constants.MAX_CONDENSER_PROGRESS / requiredEmc

        }

    init {
        tile.numPlayersUsing++
        this.initSlots(invPlayer)
    }

    internal open fun initSlots(invPlayer: InventoryPlayer) {
        this.addSlotToContainer(SlotCondenserLock(tile.lock, 0, 12, 6))

        val handler = tile.input

        var counter = 0
        //Condenser Inventory
        for (i in 0..6)
            for (j in 0..12)
                this.addSlotToContainer(ValidatedSlot(handler, counter++, 12 + j * 18, 26 + i * 18) { s -> SlotPredicates.HAS_EMC.test(s) && !tile.isStackEqualToLock(s) })

        //Player Inventory
        for (i in 0..2)
            for (j in 0..8)
                this.addSlotToContainer(Slot(invPlayer, j + i * 9 + 9, 48 + j * 18, 154 + i * 18))

        //Player Hotbar
        for (i in 0..8)
            this.addSlotToContainer(Slot(invPlayer, i, 48 + i * 18, 212))
    }

    override fun addListener(listener: IContainerListener) {
        super.addListener(listener)
        PacketHandler.sendProgressBarUpdateInt(listener, this, 0, tile.displayEmc)
        PacketHandler.sendProgressBarUpdateInt(listener, this, 1, tile.requiredEmc)
        PacketHandler.sendProgressBarUpdateInt(listener, this, 2, tile.requiredTime)
        PacketHandler.sendProgressBarUpdateInt(listener, this, 3, tile.timePassed)
        PacketHandler.sendProgressBarUpdateInt(listener, this, 4, tile.tomeProviders.stream().filter({ it -> it.hasRequiredEMC(20.0, true) }).count().toInt())
    }

    override fun detectAndSendChanges() {
        super.detectAndSendChanges()

        if (displayEmc != tile.displayEmc) {
            for (listener in listeners) {
                PacketHandler.sendProgressBarUpdateInt(listener, this, 0, tile.displayEmc)
            }

            displayEmc = tile.displayEmc
        }

        if (requiredEmc != tile.requiredEmc) {
            for (listener in listeners) {
                PacketHandler.sendProgressBarUpdateInt(listener, this, 1, tile.requiredEmc)
            }

            requiredEmc = tile.requiredEmc
        }

        if (requiredTime != tile.requiredTime) {
            for (listener in listeners) {
                PacketHandler.sendProgressBarUpdateInt(listener, this, 2, tile.requiredTime)
            }

            requiredTime = tile.requiredTime
        }

        if (timePassed != tile.timePassed) {
            for (listener in listeners) {
                PacketHandler.sendProgressBarUpdateInt(listener, this, 3, tile.timePassed)
            }

            timePassed = tile.timePassed
        }

        val count = tile.tomeProviders.stream().filter({ it -> it.hasRequiredEMC(20.0, true) }).count().toInt()
        if (tomes != count) {
            for (listener in listeners) {
                PacketHandler.sendProgressBarUpdateInt(listener, this, 3, count)
            }

            tomes = count

        }
    }

    @SideOnly(Side.CLIENT)
    override fun updateProgressBar(id: Int, data: Int) {
        when (id) {
            0 -> displayEmc = data
            1 -> requiredEmc = data
            2 -> requiredTime = data
            3 -> timePassed = data
            4 -> tomes = data
        }
    }

    override fun transferStackInSlot(player: EntityPlayer?, slotIndex: Int): ItemStack {
        val slot = this.getSlot(slotIndex)

        if (slot == null || !slot.hasStack) {
            return ItemStack.EMPTY
        }

        val stack = slot.stack
        val newStack = stack.copy()

        if (slotIndex <= 91) {
            if (!this.mergeItemStack(stack, 92, 127, false)) {
                return ItemStack.EMPTY
            }
        } else if (!EMCHelper.doesItemHaveEmc(stack) || !this.mergeItemStack(stack, 1, 91, false)) {
            return ItemStack.EMPTY
        }

        if (stack.isEmpty) {
            slot.putStack(ItemStack.EMPTY)
        } else
            slot.onSlotChanged()
        return slot.onTake(player, stack)
    }

    override fun canInteractWith(player: EntityPlayer): Boolean {
        return player.getDistanceSq(tile.pos.x + 0.5, tile.pos.y + 0.5, tile.pos.z + 0.5) <= 64.0
    }

    override fun onContainerClosed(player: EntityPlayer) {
        super.onContainerClosed(player)
        tile.numPlayersUsing--
    }

    override fun slotClick(slot: Int, button: Int, flag: ClickType?, player: EntityPlayer): ItemStack {
        return if (slot == 0 && !tile.lock.getStackInSlot(0).isEmpty) {
            if (!player.entityWorld.isRemote) {
                tile.lock.setStackInSlot(0, ItemStack.EMPTY)
                this.detectAndSendChanges()
            }

            ItemStack.EMPTY
        } else
            super.slotClick(slot, button, flag, player)
    }
}
