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

import com.tencao.projectbalance.gameObjs.tile.DMPedestalTile
import moze_intel.projecte.config.ProjectEConfig
import moze_intel.projecte.gameObjs.items.rings.Zero
import moze_intel.projecte.utils.WorldHelper
import net.minecraft.entity.Entity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class Zero: Zero() {

    override fun updateInPedestal(world: World, pos: BlockPos) {
        if (!world.isRemote && ProjectEConfig.pedestalCooldown.zeroPedCooldown != -1) {
            val te = world.getTileEntity(pos) as? DMPedestalTile ?: return
            val aabb = te.getEffectBounds()
            WorldHelper.freezeInBoundingBox(world, aabb, null, false)
            val list = world.getEntitiesWithinAABB(Entity::class.java, aabb)
            for (ent in list) {
                if (ent.isBurning) {
                    ent.extinguish()
                }
            }
            te.setActivityCooldown(ProjectEConfig.pedestalCooldown.zeroPedCooldown)
        }
    }
}