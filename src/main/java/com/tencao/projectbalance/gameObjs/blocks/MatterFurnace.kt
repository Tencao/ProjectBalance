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
import com.tencao.projectbalance.gameObjs.ObjRegistry
import com.tencao.projectbalance.gameObjs.tile.BMFurnaceTile
import com.tencao.projectbalance.gameObjs.tile.DMFurnaceTile
import com.tencao.projectbalance.gameObjs.tile.RMFurnaceTile
import com.tencao.projectbalance.utils.Constants
import moze_intel.projecte.api.state.PEStateProps
import moze_intel.projecte.gameObjs.ObjHandler
import moze_intel.projecte.gameObjs.blocks.BlockDirection
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemHandlerHelper
import java.util.*


class MatterFurnace(val isActive: Boolean, val tier: Int): BlockDirection(Material.ROCK) {

    private var isUpdating: Boolean = false

    init {
        when(tier){
            0 -> {
                this.translationKey = "pe_dm_furnace"
            }
            1 -> {
                this.translationKey = "pe_rm_furnace"
            }
            2 -> {
                this.translationKey = "pe_bm_furnace"
            }
        }
        if (isActive){
            this.creativeTab = null
            this.setLightLevel(0.875F)
        }
        else
            this.creativeTab = ObjHandler.cTab
        this.setHardness(1000000F)
    }

    override fun getBlockHardness(state: IBlockState?, world: World?, pos: BlockPos?): Float {
        return when (tier){
            0 -> 1000000F
            1 -> 2000000F
            2 -> 3000000F
            else -> 1000000F
        }
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return when (tier){
            0 -> Item.getItemFromBlock(ObjRegistry.dmFurnaceOff)
            1 -> Item.getItemFromBlock(ObjRegistry.rmFurnaceOff)
            2 -> Item.getItemFromBlock(ObjRegistry.bmFurnaceOff)
            else -> Items.AIR
        }
    }

    override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!world.isRemote) {
            when (tier){
                0 -> player.openGui(ProjectBCore, Constants.DM_FURNACE_GUI, world, pos.x, pos.y, pos.z)
                1 -> player.openGui(ProjectBCore, Constants.RM_FURNACE_GUI, world, pos.x, pos.y, pos.z)
                2 -> player.openGui(ProjectBCore, Constants.BM_FURNACE_GUI, world, pos.x, pos.y, pos.z)
            }
        }
        return true
    }

    override fun breakBlock(world: World, pos: BlockPos, state: IBlockState) {
        // isUpdating is true if this breakBlock is being called as a result of updateFurnaceBlockState
        // It prevents items from dropping out of the furnace when switching on/off state
        if (!isUpdating) {
            super.breakBlock(world, pos, state)
        }
    }

    fun updateFurnaceBlockState(isActive: Boolean, world: World, pos: BlockPos) {
        val state = world.getBlockState(pos)
        val tile = world.getTileEntity(pos)
        isUpdating = true

        if (isActive) {
            when (tier){
                0 -> world.setBlockState(pos, ObjRegistry.dmFurnaceOn.defaultState.withProperty(PEStateProps.FACING, state.getValue(PEStateProps.FACING)), 3)
                1 -> world.setBlockState(pos, ObjRegistry.rmFurnaceOn.defaultState.withProperty(PEStateProps.FACING, state.getValue(PEStateProps.FACING)), 3)
                2 -> world.setBlockState(pos, ObjRegistry.bmFurnaceOn.defaultState.withProperty(PEStateProps.FACING, state.getValue(PEStateProps.FACING)), 3)
            }
        } else {
            when (tier){
                0 -> world.setBlockState(pos, ObjRegistry.dmFurnaceOff.defaultState.withProperty(PEStateProps.FACING, state.getValue(PEStateProps.FACING)), 3)
                1 -> world.setBlockState(pos, ObjRegistry.rmFurnaceOff.defaultState.withProperty(PEStateProps.FACING, state.getValue(PEStateProps.FACING)), 3)
                2 -> world.setBlockState(pos, ObjRegistry.bmFurnaceOff.defaultState.withProperty(PEStateProps.FACING, state.getValue(PEStateProps.FACING)), 3)
            }
        }

        isUpdating = false

        if (tile != null) {
            tile.validate()
            world.setTileEntity(pos, tile)
        }
    }

    override fun onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState, entLiving: EntityLivingBase, stack: ItemStack) {
        world.setBlockState(pos, state.withProperty(PEStateProps.FACING, entLiving.horizontalFacing.opposite))
    }

    @SideOnly(Side.CLIENT)
    override fun randomDisplayTick(state: IBlockState, world: World, pos: BlockPos, rand: Random) {
        if (isActive) {
            val facing = state.getValue<EnumFacing>(PEStateProps.FACING)
            val f = pos.x.toFloat() + 0.5f
            val f1 = pos.y.toFloat() + 0.0f + rand.nextFloat() * 6.0f / 16.0f
            val f2 = pos.z.toFloat() + 0.5f
            val f3 = 0.52f
            val f4 = rand.nextFloat() * 0.6f - 0.3f

            when (facing) {
                EnumFacing.WEST -> {
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (f - f3).toDouble(), f1.toDouble(), (f2 + f4).toDouble(), 0.0, 0.0, 0.0)
                    world.spawnParticle(EnumParticleTypes.FLAME, (f - f3).toDouble(), f1.toDouble(), (f2 + f4).toDouble(), 0.0, 0.0, 0.0)
                }
                EnumFacing.EAST -> {
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (f + f3).toDouble(), f1.toDouble(), (f2 + f4).toDouble(), 0.0, 0.0, 0.0)
                    world.spawnParticle(EnumParticleTypes.FLAME, (f + f3).toDouble(), f1.toDouble(), (f2 + f4).toDouble(), 0.0, 0.0, 0.0)
                }
                EnumFacing.NORTH -> {
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (f + f4).toDouble(), f1.toDouble(), (f2 - f3).toDouble(), 0.0, 0.0, 0.0)
                    world.spawnParticle(EnumParticleTypes.FLAME, (f + f4).toDouble(), f1.toDouble(), (f2 - f3).toDouble(), 0.0, 0.0, 0.0)
                }
                EnumFacing.SOUTH -> {
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (f + f4).toDouble(), f1.toDouble(), (f2 + f3).toDouble(), 0.0, 0.0, 0.0)
                    world.spawnParticle(EnumParticleTypes.FLAME, (f + f4).toDouble(), f1.toDouble(), (f2 + f3).toDouble(), 0.0, 0.0, 0.0)
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    override fun getPickBlock(state: IBlockState, target: RayTraceResult?, world: World, pos: BlockPos, player: EntityPlayer?): ItemStack {
        return when (tier){
            0 -> ItemStack(Item.getItemFromBlock(ObjRegistry.dmFurnaceOff))
            1 -> ItemStack(Item.getItemFromBlock(ObjRegistry.rmFurnaceOff))
            2 -> ItemStack(Item.getItemFromBlock(ObjRegistry.bmFurnaceOff))
            else -> ItemStack.EMPTY
        }
    }

    override fun hasTileEntity(state: IBlockState): Boolean {
        return true
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        return when (tier){
            0 -> DMFurnaceTile()
            1 -> RMFurnaceTile()
            2 -> BMFurnaceTile()
            else -> null
        }
    }

    override fun hasComparatorInputOverride(state: IBlockState?): Boolean {
        return true
    }

    override fun getComparatorInputOverride(state: IBlockState, world: World, pos: BlockPos): Int {
        val te = world.getTileEntity(pos)
        if (te != null) {
            val inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
            return ItemHandlerHelper.calcRedstoneFromInventory(inv)
        }
        return 0
    }
}