package com.tencao.projectbalance.gameObjs.items

import com.tencao.projectbalance.utils.EMCHelper
import moze_intel.projecte.gameObjs.items.ItemPE
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler

class ItemPE: ItemPE() {

    fun consumeFuel(inv: IItemHandler, stack: ItemStack, amount: Double, shouldRemove: Boolean): Boolean {
        if (amount <= 0) {
            return true
        }

        val current = getEmc(stack)

        if (current < amount) {
            val consume = EMCHelper.consumeInvFuel(inv, amount - current)

            if (consume == -1.0) {
                return false
            }

            addEmcToStack(stack, consume)
        }

        if (shouldRemove) {
            removeEmc(stack, amount)
        }

        return true
    }
}