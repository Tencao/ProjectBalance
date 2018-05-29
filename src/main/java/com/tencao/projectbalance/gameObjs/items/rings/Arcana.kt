package com.tencao.projectbalance.gameObjs.items.rings

import com.tencao.projectbalance.config.ProjectBConfig
import moze_intel.projecte.gameObjs.items.ItemPE
import moze_intel.projecte.gameObjs.items.rings.Arcana
import moze_intel.projecte.utils.ItemHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand

class Arcana: Arcana() {

    override fun shootProjectile(player: EntityPlayer, stack: ItemStack, hand: EnumHand?): Boolean {
        val world = player.entityWorld
        if (world.isRemote) return false
        if (ItemHelper.getOrCreateCompound(stack).getByte(ItemPE.TAG_MODE) == 3.toByte() && !ItemPE.consumeFuel(player, stack, ProjectBConfig.tweaks.SWRGEmc.toDouble(), true))
            return false
        return super.shootProjectile(player, stack, hand)
    }
}