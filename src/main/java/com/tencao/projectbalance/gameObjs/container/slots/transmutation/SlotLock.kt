package com.tencao.projectbalance.gameObjs.container.slots.transmutation

import com.tencao.projectbalance.gameObjs.container.inventory.TransmutationInventory
import com.tencao.projectbalance.gameObjs.container.slots.SlotPredicates
import moze_intel.projecte.api.item.IItemEmc
import moze_intel.projecte.utils.Constants
import moze_intel.projecte.utils.EMCHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.items.SlotItemHandler

class SlotLock(private val inv: TransmutationInventory, par2: Int, par3: Int, par4: Int) : SlotItemHandler(inv, par2, par3, par4) {

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
            val remainEmc = Constants.TILE_MAX_EMC - Math.ceil(inv.provider.emc).toInt()

            if (itemEmc.getStoredEmc(stack) >= remainEmc) {
                inv.addEmc(remainEmc.toDouble())
                itemEmc.extractEmc(stack, remainEmc.toDouble())
            } else {
                inv.addEmc(itemEmc.getStoredEmc(stack))
                itemEmc.extractEmc(stack, itemEmc.getStoredEmc(stack))
            }
        }

        if (EMCHelper.doesItemHaveEmc(stack)) {
            inv.handleKnowledge(stack.copy())
        }
    }

    override fun onTake(player: EntityPlayer?, stack: ItemStack): ItemStack {
        var stack = stack
        stack = super.onTake(player, stack)
        inv.updateClientTargets()
        return stack
    }

    override fun getSlotStackLimit(): Int {
        return 1
    }
}
