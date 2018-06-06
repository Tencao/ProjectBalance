package com.tencao.projectbalance.gameObjs.container.slots.transmutation

import com.tencao.projectbalance.gameObjs.container.inventory.TransmutationInventory
import moze_intel.projecte.utils.EMCHelper
import net.minecraft.item.ItemStack
import net.minecraftforge.items.SlotItemHandler

class SlotUnlearn(private val inv: TransmutationInventory, par2: Int, par3: Int, par4: Int) : SlotItemHandler(inv, par2, par3, par4) {

    override fun isItemValid(stack: ItemStack): Boolean {
        return !this.hasStack && EMCHelper.doesItemHaveEmc(stack)
    }

    override fun putStack(stack: ItemStack) {
        if (!stack.isEmpty) {
            inv.handleUnlearn(stack.copy())
        }

        super.putStack(stack)
    }

    override fun getSlotStackLimit(): Int {
        return 1
    }
}
