/*
 * Copyright (C) 2018
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.tencao.projectbalance.gameObjs.items.rings

import com.tencao.projectbalance.config.ProjectBConfig
import com.tencao.projectbalance.gameObjs.tile.DMPedestalTile
import com.tencao.projectbalance.handlers.getInternalCooldowns
import moze_intel.projecte.api.PESounds
import moze_intel.projecte.config.ProjectEConfig
import moze_intel.projecte.gameObjs.items.ItemPE
import moze_intel.projecte.gameObjs.items.rings.SoulStone
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

class SoulStone: SoulStone() {

    override fun onUpdate(stack: ItemStack, world: World, entity: Entity, par4: Int, par5: Boolean) {
        if (world.isRemote || par4 > 8 || entity !is EntityPlayer) {
            return
        }

        super.onUpdate(stack, world, entity, par4, par5)

        if (ItemHelper.getOrCreateCompound(stack).getBoolean(ItemPE.TAG_ACTIVE)) {
            if (!entity.getInternalCooldowns().isHealing()) {
                stack.tagCompound!!.setBoolean(ItemPE.TAG_ACTIVE, false)
            } else {
                entity.getCapability(InternalTimers.CAPABILITY, null)!!.activateHeal()

                if (entity.health < entity.maxHealth && entity.getCapability(InternalTimers.CAPABILITY, null)!!.canHeal()) {
                    world.playSound(null, entity.posX, entity.posY, entity.posZ, PESounds.HEAL, SoundCategory.PLAYERS, 1.0f, 1.0f)
                    entity.heal(2.0f)
                }
            }
        }
    }

    override fun changeMode(player: EntityPlayer, stack: ItemStack, hand: EnumHand?): Boolean {
        val tag = ItemHelper.getOrCreateCompound(stack)
        if (!tag.getBoolean(ItemPE.TAG_ACTIVE)
                && player.getInternalCooldowns().canHeal()
                && ItemPE.consumeFuel(player, stack, ProjectBConfig.tweaks.SoulStoneEmc.toDouble(), true)) {
            tag.setBoolean(ItemPE.TAG_ACTIVE, true)
            player.getInternalCooldowns().triggerHealCooldown()
        } else
            tag.setBoolean(ItemPE.TAG_ACTIVE, false)
        return true
    }

    override fun updateInPedestal(world: World, pos: BlockPos) {
        if (!world.isRemote && ProjectEConfig.pedestalCooldown.soulPedCooldown != -1) {
            val te = world.getTileEntity(pos) as? DMPedestalTile ?: return
            val players = world.getEntitiesWithinAABB(EntityPlayerMP::class.java, te.getEffectBounds())

            for (player in players) {
                if (player.health < player.maxHealth && te.hasRequiredEMC(ProjectBConfig.tweaks.SoulStonePedestalCost.toDouble(), false)) {
                    world.playSound(null, player.posX, player.posY, player.posZ, PESounds.HEAL, SoundCategory.BLOCKS, 1.0f, 1.0f)
                    player.heal(1.0f) // 1/2 heart
                }
            }

            te.setActivityCooldown(ProjectEConfig.pedestalCooldown.soulPedCooldown)
        }
    }
}