package com.tencao.projectbalance.gameObjs.container.slots.transmutation

import com.tencao.projectbalance.gameObjs.container.inventory.TransmutationInventory
import com.tencao.projectbalance.handlers.getInternalCooldowns
import com.tencao.projectbalance.utils.ComplexHelper
import moze_intel.projecte.utils.EMCHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.items.SlotItemHandler

class SlotOutput(private val inv: TransmutationInventory, par2: Int, par3: Int, par4: Int) : SlotItemHandler(inv, par2, par3, par4) {

    override fun decrStackSize(amount: Int): ItemStack {
        val stack = stack.copy()
        stack.count = amount
        val emcValue = amount * EMCHelper.getEmcValue(stack)
        if (emcValue > inv.provider.emc) {
            //Requesting more emc than available
            //Container expects stacksize=0-Itemstack for 'nothing'
            stack.count = 0
            return stack
        }
        inv.removeEmc(emcValue.toDouble())
        inv.checkForUpdates()
        if (ComplexHelper.getCraftTime(stack) > 100) {
            inv.player.getInternalCooldowns().setStack(stack.copy())
            stack.count = 0
        }

        return stack
    }

    override fun putStack(stack: ItemStack) {}

    override fun isItemValid(stack: ItemStack): Boolean {
        return false
    }

    override fun canTakeStack(player: EntityPlayer?): Boolean {
        return !hasStack || EMCHelper.getEmcValue(stack) <= inv.provider.emc
    }
}
