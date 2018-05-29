package com.tencao.projectbalance.gameObjs.items.rings

import com.tencao.projectbalance.handlers.getInternalCooldowns
import moze_intel.projecte.api.PESounds
import moze_intel.projecte.gameObjs.items.ItemPE
import moze_intel.projecte.gameObjs.items.rings.BodyStone
import moze_intel.projecte.handlers.InternalTimers
import moze_intel.projecte.utils.ItemHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.SoundCategory
import net.minecraft.world.World

class BodyStone: BodyStone() {

    override fun onUpdate(stack: ItemStack, world: World, entity: Entity, par4: Int, par5: Boolean) {
        if (world.isRemote || par4 > 8 || entity !is EntityPlayer) {
            return
        }

        super.onUpdate(stack, world, entity, par4, par5)

        if (ItemHelper.getOrCreateCompound(stack).getBoolean(ItemPE.TAG_ACTIVE)) {
            if (entity.getInternalCooldowns().isFeeding()) {
                stack.tagCompound!!.setBoolean(ItemPE.TAG_ACTIVE, false)
            } else {
                entity.getCapability(InternalTimers.CAPABILITY, null)!!.activateFeed()
                if (entity.foodStats.needFood() && entity.getInternalCooldowns().canFeed()) {
                    world.playSound(null, entity.posX, entity.posY, entity.posZ, PESounds.HEAL, SoundCategory.PLAYERS, 1f, 1f)
                    entity.foodStats.addStats(2, 10f)
                }
            }
        }
    }
}