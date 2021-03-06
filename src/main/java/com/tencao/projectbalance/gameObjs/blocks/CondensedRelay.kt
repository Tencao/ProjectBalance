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
import com.tencao.projectbalance.gameObjs.tile.CondensedRelayMK1Tile
import com.tencao.projectbalance.gameObjs.tile.CondensedRelayMK2Tile
import com.tencao.projectbalance.gameObjs.tile.CondensedRelayMK3Tile
import com.tencao.projectbalance.gameObjs.tile.CondensedRelayMK4Tile
import com.tencao.projectbalance.utils.Constants
import moze_intel.projecte.gameObjs.blocks.BlockDirection
import moze_intel.projecte.utils.MathUtils
import moze_intel.projecte.utils.WorldHelper
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.CapabilityItemHandler

class CondensedRelay(private val tier: Int) : BlockDirection(Material.ROCK) {

    init {
        this.translationKey = "pe_condensed_relay_MK$tier"
        this.setLightLevel(Constants.COLLECTOR_LIGHT_VALS[tier - 1])
        this.setHardness(10.0f)
    }

    override fun onBlockActivated(world: World?, pos: BlockPos?, state: IBlockState?, player: EntityPlayer?, hand: EnumHand?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!world!!.isRemote) {
            val x = pos!!.x
            val y = pos.y
            val z = pos.z

            when (tier) {
                1 -> player!!.openGui(ProjectBCore, Constants.CONDENSED_RELAY1_GUI, world, x, y, z)
                2 -> player!!.openGui(ProjectBCore, Constants.CONDENSED_RELAY2_GUI, world, x, y, z)
                3 -> player!!.openGui(ProjectBCore, Constants.CONDENSED_RELAY3_GUI, world, x, y, z)
                4 -> player!!.openGui(ProjectBCore, Constants.CONDENSED_RELAY4_GUI, world, x, y, z)
            }
        }
        return true
    }

    override fun hasTileEntity(state: IBlockState?): Boolean {
        return true
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity {
        return when (tier) {
            1 -> CondensedRelayMK1Tile()
            2 -> CondensedRelayMK2Tile()
            3 -> CondensedRelayMK3Tile()
            4 -> CondensedRelayMK4Tile()
            else -> CondensedRelayMK1Tile()
        }
    }

    override fun hasComparatorInputOverride(state: IBlockState?): Boolean {
        return true
    }

    override fun getComparatorInputOverride(state: IBlockState?, world: World?, pos: BlockPos?): Int {
        val relay = world!!.getTileEntity(pos!!) as CondensedRelayMK1Tile
        return MathUtils.scaleToRedstone(relay.storedEmc, relay.maximumEmc)
    }

    override fun breakBlock(world: World, pos: BlockPos, state: IBlockState) {
        val te = world.getTileEntity(pos)
        WorldHelper.dropInventory(te!!.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN), world, pos)
        super.breakBlock(world, pos, state)
    }
}