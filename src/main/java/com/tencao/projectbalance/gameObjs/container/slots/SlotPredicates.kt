package com.tencao.projectbalance.gameObjs.container.slots

import moze_intel.projecte.api.item.IItemEmc
import moze_intel.projecte.utils.EMCHelper
import net.minecraft.item.ItemStack

object SlotPredicates {

    val HAS_EMC: (ItemStack) -> Boolean = { input -> !input.isEmpty && EMCHelper.doesItemHaveEmc(input) }

    // slotrelayklein, slotmercurialklein
    val IITEMEMC: (ItemStack) -> Boolean = { input -> !input.isEmpty && input.item is IItemEmc }

    // slotrelayinput
    val RELAY_INV: (ItemStack) -> Boolean = { input -> IITEMEMC.invoke(input) || HAS_EMC.invoke(input) }

}
