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