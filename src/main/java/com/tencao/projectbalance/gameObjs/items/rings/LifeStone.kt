package com.tencao.projectbalance.gameObjs.items.rings

import com.tencao.projectbalance.config.ProjectBConfig
import com.tencao.projectbalance.gameObjs.tile.DMPedestalTile
import com.tencao.projectbalance.handlers.getInternalCooldowns
import moze_intel.projecte.api.PESounds
import moze_intel.projecte.config.ProjectEConfig
import moze_intel.projecte.gameObjs.items.ItemPE
import moze_intel.projecte.gameObjs.items.rings.LifeStone
import moze_intel.projecte.handlers.InternalTimers
import moze_intel.projecte.utils.ItemHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class LifeStone: LifeStone() {

    override fun onUpdate(stack: ItemStack, world: World, entity: Entity, par4: Int, par5: Boolean) {
        if (world.isRemote || par4 > 8 || entity !is EntityPlayer) {
            return
        }

        if (ItemHelper.getOrCreateCompound(stack).getBoolean(ItemPE.TAG_ACTIVE)) {
            if (!entity.getInternalCooldowns().isFeeding() && !entity.getInternalCooldowns().isHealing()) {
                stack.tagCompound!!.setBoolean(ItemPE.TAG_ACTIVE, false)
            } else {
                entity.getCapability(InternalTimers.CAPABILITY, null)!!.activateFeed()
                entity.getCapability(InternalTimers.CAPABILITY, null)!!.activateHeal()

                if (entity.health < entity.maxHealth && entity.getCapability(InternalTimers.CAPABILITY, null)!!.canHeal()) {
                    world.playSound(null, entity.posX, entity.posY, entity.posZ, PESounds.HEAL, SoundCategory.PLAYERS, 1f, 1f)
                    entity.heal(2.0f)
                }

                if (entity.foodStats.needFood() && entity.getCapability(InternalTimers.CAPABILITY, null)!!.canFeed()) {
                    world.playSound(null, entity.posX, entity.posY, entity.posZ, PESounds.HEAL, SoundCategory.PLAYERS, 1f, 1f)
                    entity.foodStats.addStats(2, 10f)
                }
            }
        }
    }

    override fun changeMode(player: EntityPlayer, stack: ItemStack, hand: EnumHand?): Boolean {
        val tag = ItemHelper.getOrCreateCompound(stack)
        if (!tag.getBoolean(ItemPE.TAG_ACTIVE)
                && player.getInternalCooldowns().canFeed()
                && player.getInternalCooldowns().canHeal()
                && ItemPE.consumeFuel(player, stack, (ProjectBConfig.tweaks.BodyStoneEmc + ProjectBConfig.tweaks.SoulStoneEmc).toDouble(), true)) {
            tag.setBoolean(ItemPE.TAG_ACTIVE, true)
            player.getInternalCooldowns().triggerFoodCooldown()
            player.getInternalCooldowns().triggerHealCooldown()
        } else
            tag.setBoolean(ItemPE.TAG_ACTIVE, false)
        return true
    }

    override fun updateInPedestal(world: World, pos: BlockPos) {
        if (!world.isRemote && ProjectEConfig.pedestalCooldown.lifePedCooldown != -1) {
            val te = world.getTileEntity(pos) as? DMPedestalTile ?: return
            val players = world.getEntitiesWithinAABB(EntityPlayerMP::class.java, te.getEffectBounds())

            for (player in players) {
                if (player.health < player.maxHealth && te.hasRequiredEMC(ProjectBConfig.tweaks.SoulStonePedestalCost.toDouble(), false)) {
                    world.playSound(null, player.posX, player.posY, player.posZ, PESounds.HEAL, SoundCategory.BLOCKS, 1f, 1f)
                    player.heal(1.0f) // 1/2 heart
                }
                if (player.foodStats.needFood() && te.hasRequiredEMC(ProjectBConfig.tweaks.BodyStonePedestalCost.toDouble(), false)) {
                    world.playSound(null, player.posX, player.posY, player.posZ, PESounds.HEAL, SoundCategory.BLOCKS, 1f, 1f)
                    player.foodStats.addStats(1, 1f) // 1/2 shank
                }
            }

            te.setActivityCooldown(ProjectEConfig.pedestalCooldown.lifePedCooldown)
        }
    }
}