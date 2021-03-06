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
import moze_intel.projecte.gameObjs.items.rings.Ignition
import net.minecraft.entity.EntityLiving
import net.minecraft.util.DamageSource
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class Ignition: Ignition() {

    override fun updateInPedestal(world: World, pos: BlockPos) {
        if (!world.isRemote && ProjectEConfig.pedestalCooldown.ignitePedCooldown != -1) {
            val te = world.getTileEntity(pos) as? DMPedestalTile ?: return
            val list = world.getEntitiesWithinAABB(EntityLiving::class.java, te.getEffectBounds())
            for (living in list) {
                living.attackEntityFrom(DamageSource.IN_FIRE, 3.0f)
                living.setFire(8)
            }

            te.setActivityCooldown(ProjectEConfig.pedestalCooldown.ignitePedCooldown)
        }

    }
}