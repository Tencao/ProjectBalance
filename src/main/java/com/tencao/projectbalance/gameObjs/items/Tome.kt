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

import com.google.common.collect.Lists
import com.tencao.projectbalance.events.PedestalEvent
import com.tencao.projectbalance.gameObjs.tile.DMPedestalTile
import com.tencao.projectbalance.gameObjs.tile.ICraftingGen
import moze_intel.projecte.api.item.IPedestalItem
import moze_intel.projecte.gameObjs.items.Tome
import net.minecraft.client.resources.I18n
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World

class Tome: Tome(), IPedestalItem {


    override fun getPedestalDescription(): List<String> {

        val list = Lists.newLinkedList<String>()
        list.add(TextFormatting.BLUE.toString() + I18n.format("pe.tome.pedestal"))
        return list
    }


    override fun updateInPedestal(world: World, pos: BlockPos) {
        if (!world.isRemote) {
            val te = world.getTileEntity(pos)
            if (te is DMPedestalTile) {
                PedestalEvent.getTileEntities(world, te).forEach { craftTile -> (craftTile as ICraftingGen).addTomeToCounter(te) }
            }
        }
    }
}