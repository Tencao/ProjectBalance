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

import com.tencao.projectbalance.gameObjs.tile.DMPedestalTile
import com.tencao.projectbalance.gameObjs.tile.ICraftingGen
import moze_intel.projecte.api.item.IPedestalItem
import moze_intel.projecte.gameObjs.items.Tome
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*

object PedestalEvent {

    private val pedestals = HashMap<DMPedestalTile, LinkedHashSet<BlockPos>>()

    //Removes any blocks from the watcher list
    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onBlockBreak(event: BlockEvent.BreakEvent) {
        pedestals.values.forEach { it -> it.minus(event.pos) }
        pedestals.entries.removeIf { it -> it.value.isEmpty() }
    }

    fun registerTileEntity(tile: DMPedestalTile) {
        pedestals[tile] = tile.getNearbyTiles()
    }

    //Auto adds any blocks placed to the pedestal watch list, prevents researching
    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onBlockPlace(event: BlockEvent.PlaceEvent) {
        val axisAlignedBB = AxisAlignedBB(event.pos)
        if (event.placedBlock.block.hasTileEntity(event.placedBlock)) {
            val tile = event.placedBlock.block.createTileEntity(event.world, event.placedBlock)
            if (tile != null) {
                pedestals.keys.forEach { pedestal ->
                    if (pedestal.doesTileMatch(tile) && pedestal.getEffectBounds().intersects(axisAlignedBB))
                        pedestals[pedestal]!!.plus(event.pos)
                }
                tile.invalidate()
            }
        } else {
            pedestals.keys.forEach { pedestal ->
                if (pedestal.doesBlockMatch(event.placedBlock.block) && pedestal.getEffectBounds().intersects(axisAlignedBB))
                    pedestals[pedestal]!!.plus(event.pos)
            }
        }
    }

    fun getBlockPos(tile: DMPedestalTile): LinkedHashSet<BlockPos> {
        if (!pedestals.containsKey(tile))
            registerTileEntity(tile)
        return pedestals[tile]!!
    }

    fun getTileEntities(world: World, tile: DMPedestalTile): LinkedHashSet<TileEntity> {
        if (!pedestals.containsKey(tile))
            registerTileEntity(tile)
        val tileEntities = LinkedHashSet<TileEntity>()
        pedestals[tile]!!.forEach { pos ->
            if (world.getTileEntity(pos) != null)
                tileEntities.add(world.getTileEntity(pos)!!)
        }
        tileEntities.removeIf({ Objects.isNull(it) })
        return tileEntities
    }

    fun getPedestals(world: World, tile: DMPedestalTile): LinkedHashSet<DMPedestalTile> {
        if (!pedestals.containsKey(tile))
            registerTileEntity(tile)
        val tileEntities = LinkedHashSet<DMPedestalTile>()
        pedestals[tile]!!.forEach { pos ->
            if (world.getTileEntity(pos) is DMPedestalTile)
                tileEntities.add(world.getTileEntity(pos) as DMPedestalTile)
        }
        return tileEntities
    }

    fun removeTileEntity(tile: DMPedestalTile) {
        if (!pedestals.containsKey(tile))
            return
        if (tile.getItem() is IPedestalItem) {
            if (tile.getItem() is Tome)
                pedestals[tile]!!.forEach { craftTile -> (craftTile as ICraftingGen).removeTomeFromCounter(tile) }
        }

        pedestals.remove(tile)
    }
}