package com.tencao.projectbalance.gameObjs.container.slots

import moze_intel.projecte.utils.ItemHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler
import java.util.function.Predicate

open class SlotGhost(inv: IItemHandler, slotIndex: Int, xPos: Int, yPos: Int, private val validator: Predicate<ItemStack>) : SlotItemHandler(inv, slotIndex, xPos, yPos) {

    override fun isItemValid(stack: ItemStack): Boolean {
        if (!stack.isEmpty && validator.test(stack)) {
            this.putStack(ItemHelper.getNormalizedStack(stack))
        }

        return false
    }

    override fun canTakeStack(player: EntityPlayer?): Boolean {
        return false
    }

    override fun getSlotStackLimit(): Int {
        return 1
    }
}
