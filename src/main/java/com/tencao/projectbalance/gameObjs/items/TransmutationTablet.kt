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

import com.tencao.projectbalance.ProjectBCore
import com.tencao.projectbalance.utils.Constants
import moze_intel.projecte.gameObjs.items.TransmutationTablet
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World

class TransmutationTablet: TransmutationTablet() {

    override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {
        if (!world.isRemote) {
            player.openGui(ProjectBCore, Constants.TRANSMUTATION_GUI, world, if (hand == EnumHand.MAIN_HAND) 0 else 1, -1, -1)
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand))
    }
}