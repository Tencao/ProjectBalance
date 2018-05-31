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

import com.tencao.projectbalance.ProjectBCore
import com.tencao.projectbalance.gameObjs.tile.CollectorMK1Tile
import com.tencao.projectbalance.gameObjs.tile.CollectorMK2Tile
import com.tencao.projectbalance.gameObjs.tile.CollectorMK3Tile
import com.tencao.projectbalance.gameObjs.tile.CollectorMK4Tile
import com.tencao.projectbalance.utils.Constants
import moze_intel.projecte.api.item.IItemEmc
import moze_intel.projecte.gameObjs.blocks.BlockDirection
import moze_intel.projecte.utils.MathUtils
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.InventoryHelper
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.items.CapabilityItemHandler

class Collector(private val tier: Int) : BlockDirection(Material.GLASS) {

    init {
        this.unlocalizedName = "pe_collector_MK$tier"
        this.setLightLevel(Constants.COLLECTOR_LIGHT_VALS[tier - 1])
        this.setHardness(0.3f)
    }

    override fun onBlockActivated(world: World?, pos: BlockPos?, state: IBlockState?, player: EntityPlayer?, hand: EnumHand?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val x = pos!!.x
        val y = pos.y
        val z = pos.z

        if (!world!!.isRemote)
            when (tier) {
                1 -> player!!.openGui(ProjectBCore, Constants.COLLECTOR1_GUI, world, x, y, z)
                2 -> player!!.openGui(ProjectBCore, Constants.COLLECTOR2_GUI, world, x, y, z)
                3 -> player!!.openGui(ProjectBCore, Constants.COLLECTOR3_GUI, world, x, y, z)
                4 -> player!!.openGui(ProjectBCore, Constants.COLLECTOR4_GUI, world, x, y, z)
            }
        return true
    }

    override fun hasTileEntity(state: IBlockState?): Boolean {
        return true
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity {
        return when (tier) {
            4 -> CollectorMK4Tile()
            3 -> CollectorMK3Tile()
            2 -> CollectorMK2Tile()
            1 -> CollectorMK1Tile()
            else -> CollectorMK1Tile()
        }
    }

    override fun hasComparatorInputOverride(state: IBlockState?): Boolean {
        return true
    }

    override fun getComparatorInputOverride(state: IBlockState, world: World, pos: BlockPos): Int {
        val tile = world.getTileEntity(pos) as CollectorMK1Tile
        val charging = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)!!.getStackInSlot(CollectorMK1Tile.UPGRADING_SLOT)
        return if (!charging.isEmpty) {
            return if (charging.item is IItemEmc) {
                val itemEmc = charging.item as IItemEmc
                val max = itemEmc.getMaximumEmc(charging)
                val current = itemEmc.getStoredEmc(charging)
                MathUtils.scaleToRedstone(current, max)
            } else {
                val needed = tile.emcToNextGoal
                val current = tile.storedEmc
                MathUtils.scaleToRedstone(current, needed)
            }
        } else {
            MathUtils.scaleToRedstone(tile.storedEmc, tile.maximumEmc)
        }
    }

    override fun isSideSolid(state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing?): Boolean {
        return true
    }

    override fun breakBlock(world: World, pos: BlockPos, state: IBlockState) {
        val ent = world.getTileEntity(pos)
        if (ent != null) {
            val handler = ent.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)
            for (i in 0 until handler!!.slots) {
                if (i != CollectorMK1Tile.LOCK_SLOT && !handler.getStackInSlot(i).isEmpty) {
                    InventoryHelper.spawnItemStack(world, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), handler.getStackInSlot(i))
                }
            }
        }
        super.breakBlock(world, pos, state)
    }
}