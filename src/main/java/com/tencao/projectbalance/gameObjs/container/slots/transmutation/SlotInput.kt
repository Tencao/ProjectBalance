package com.tencao.projectbalance.gameObjs.container.slots.transmutation

import com.tencao.projectbalance.gameObjs.container.inventory.TransmutationInventory
import com.tencao.projectbalance.gameObjs.container.slots.SlotPredicates
import moze_intel.projecte.api.item.IItemEmc
import net.minecraft.item.ItemStack
import net.minecraftforge.items.SlotItemHandler

class SlotInput(private val inv: TransmutationInventory, par2: Int, par3: Int, par4: Int) : SlotItemHandler(inv, par2, par3, par4) {

    override fun isItemValid(stack: ItemStack): Boolean {
        return SlotPredicates.RELAY_INV.invoke(stack)
    }

    override fun putStack(stack: ItemStack) {
        if (stack.isEmpty) {
            return
        }

        super.putStack(stack)

        if (stack.item is IItemEmc) {
            val itemEmc = stack.item as IItemEmc
            val remainingEmc = itemEmc.getMaximumEmc(stack) - itemEmc.getStoredEmc(stack)

            if (inv.provider.emc >= remainingEmc) {
                itemEmc.addEmc(stack, remainingEmc)
                inv.removeEmc(remainingEmc)
            } else {
                itemEmc.addEmc(stack, inv.provider.emc)
                inv.removeEmc(inv.provider.emc)
            }
        }

        inv.handleKnowledge(stack.copy())
    }

    override fun decrStackSize(amount: Int): ItemStack {
        val stack = super.decrStackSize(amount)
        //Decrease the size of the stack
        if (stack.item is IItemEmc) {
            //If it was an EMC storing item then check for updates,
            // so that the right hand side shows the proper items
            inv.checkForUpdates()
        }
        return stack
    }

    override fun getSlotStackLimit(): Int {
        return 1
    }
}
