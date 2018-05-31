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

package com.tencao.projectbalance.gameObjs.blocks

import com.tencao.projectbalance.gameObjs.state.EnumMatterType
import moze_intel.projecte.api.state.PEStateProps
import moze_intel.projecte.gameObjs.ObjHandler
import moze_intel.projecte.gameObjs.blocks.MatterBlock
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class MatterBlock: MatterBlock() {

    override fun canHarvestBlock(world: IBlockAccess, pos: BlockPos, player: EntityPlayer): Boolean {
        val stack = player.getHeldItem(EnumHand.MAIN_HAND)
        val type = world.getBlockState(pos).getValue(PEStateProps.TIER_PROP)

        return if (!stack.isEmpty) {
            if (type == EnumMatterType.RED_MATTER || type == EnumMatterType.BLUE_MATTER) {
                stack.item === ObjHandler.rmPick || stack.item === ObjHandler.rmStar || stack.item === ObjHandler.rmHammer
            } else {
                stack.item === ObjHandler.rmPick || stack.item === ObjHandler.dmPick || stack.item === ObjHandler.rmStar || stack.item === ObjHandler.dmHammer || stack.item === ObjHandler.rmHammer
            }
        } else false

    }

    override fun getSubBlocks(cTab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (i in 0..2) {
            list.add(ItemStack(this, 1, i))
        }
    }
}