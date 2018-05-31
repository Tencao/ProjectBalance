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