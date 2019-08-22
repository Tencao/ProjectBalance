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
import moze_intel.projecte.PECore
import moze_intel.projecte.config.ProjectEConfig
import moze_intel.projecte.gameObjs.entity.EntityHomingArrow
import moze_intel.projecte.gameObjs.items.rings.ArchangelSmite
import moze_intel.projecte.utils.EMCHelper
import net.minecraft.entity.EntityLiving
import net.minecraft.init.Items
import net.minecraft.init.SoundEvents
import net.minecraft.item.Item
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.common.util.FakePlayerFactory

class ArchangelSmite: ArchangelSmite() {

    override fun updateInPedestal(world: World, pos: BlockPos) {
        if (!world.isRemote && ProjectEConfig.pedestalCooldown.archangelPedCooldown != -1) {
            val te = world.getTileEntity(pos) as? DMPedestalTile ?: return

            if (world.getEntitiesWithinAABB(EntityLiving::class.java, te.getEffectBounds()).isNotEmpty()) {
                for (i in 0..2) {
                    if (te.hasRequiredEMC(EMCHelper.getEmcValue(Items.ARROW), false)) {
                        val arrow = EntityHomingArrow(world, FakePlayerFactory.get(world as WorldServer, PECore.FAKEPLAYER_GAMEPROFILE), 2.0f)
                        arrow.posX = te.centeredX
                        arrow.posY = te.centeredY + 2
                        arrow.posZ = te.centeredZ
                        arrow.motionX = 0.0
                        arrow.motionZ = 0.0
                        arrow.motionY = 1.0
                        arrow.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0f, 1.0f / (Item.itemRand.nextFloat() * 0.4f + 1.2f) + 0.5f)
                        world.spawnEntity(arrow)
                    }
                }
            }
            te.setActivityCooldown(ProjectEConfig.pedestalCooldown.archangelPedCooldown)
        }
    }
}