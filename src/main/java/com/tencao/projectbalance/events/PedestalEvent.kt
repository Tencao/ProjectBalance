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

package com.tencao.projectbalance.events

import com.tencao.projectbalance.gameObjs.blocks.Pedestal
import com.tencao.projectbalance.gameObjs.tile.DMPedestalTile
import moze_intel.projecte.utils.WorldHelper
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object PedestalEvent {

    private val pedestals = HashSet<DMPedestalTile>()

    //Removes any blocks from the watcher list
    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onBlockBreak(event: BlockEvent.BreakEvent) {
        if (event.world.getBlockState(event.pos).block is Pedestal)
            pedestals.forEach { it.nearbyPedestals.remove(event.world.getTileEntity(event.pos)) }
        else
            pedestals.forEach { it.nearbyBlocks.remove(event.pos) }
    }

    fun registerTileEntity(tile: DMPedestalTile) {
        pedestals.add(tile)
        WorldHelper.getPositionsFromBox(tile.getEffectBounds()).forEach {
            val tileCheck = tile.world.getTileEntity(it)
            if (tileCheck is DMPedestalTile){
                tileCheck.registerTileEntity(tile)
            }
            tile.registerTileEntity(tileCheck)
            tile.registerBlock(it)
        }
    }

    //Auto adds any blocks placed to the pedestal watch list, prevents researching
    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onBlockPlace(event: BlockEvent.PlaceEvent) {
        val axisAlignedBB = AxisAlignedBB(event.pos)
        pedestals.filter { it.getEffectBounds().intersects(axisAlignedBB) }.forEach {
            it.registerBlock(event.pos)
        }
    }

    fun removeTileEntity(tile: DMPedestalTile) {
        pedestals.forEach { it.nearbyPedestals.remove(tile) }
        pedestals.remove(tile)
    }
}