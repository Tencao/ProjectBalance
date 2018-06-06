package com.tencao.projectbalance.gameObjs.container.slots.transmutation

import com.tencao.projectbalance.gameObjs.container.inventory.TransmutationInventory
import moze_intel.projecte.utils.EMCHelper
import net.minecraft.item.ItemStack
import net.minecraftforge.items.SlotItemHandler

class SlotConsume(private val inv: TransmutationInventory, par2: Int, par3: Int, par4: Int) : SlotItemHandler(inv, par2, par3, par4) {

    override fun putStack(stack: ItemStack) {
        if (stack.isEmpty) {
            return
        }

        val cache = stack.copy()

        var toAdd = 0.0

        while (!inv.hasMaxedEmc() && stack.count > 0) {
            toAdd += EMCHelper.getEmcSellValue(stack).toDouble()
            stack.shrink(1)
        }

        inv.addEmc(toAdd)
        this.onSlotChanged()
        inv.handleKnowledge(cache)
    }

    override fun isItemValid(stack: ItemStack): Boolean {
        return !inv.hasMaxedEmc() && EMCHelper.doesItemHaveEmc(stack)
    }
}
