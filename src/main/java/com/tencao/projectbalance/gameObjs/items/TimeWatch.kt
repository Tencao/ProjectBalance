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

package com.tencao.projectbalance.gameObjs.items

import com.tencao.projectbalance.config.ProjectBConfig
import com.tencao.projectbalance.gameObjs.tile.DMPedestalTile
import com.tencao.projectbalance.gameObjs.tile.IEmcGen
import moze_intel.projecte.config.ProjectEConfig
import moze_intel.projecte.gameObjs.items.TimeWatch
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class TimeWatch: TimeWatch() {

    override fun updateInPedestal(world: World, pos: BlockPos) {
        // Change from old EE2 behaviour (universally increased tickrate) for safety and impl reasons.

        if (!world.isRemote && ProjectEConfig.items.enableTimeWatch) {
            val te = world.getTileEntity(pos)
            if (te is DMPedestalTile) {
                te.nearbyBlocks.forEach { if (world.getTileEntity(it) is IEmcGen &&
                        te.hasRequiredEMC(ProjectBConfig.tweaks.TimeWatchBoostPedestalCost.toLong(), false))
                    (world.getTileEntity(it) as IEmcGen).updateEmc(true)
                }
            }
        }
    }
}