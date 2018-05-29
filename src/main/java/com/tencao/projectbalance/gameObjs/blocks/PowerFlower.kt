package com.tencao.projectbalance.gameObjs.blocks

import com.tencao.projectbalance.ProjectBCore
import com.tencao.projectbalance.gameObjs.tile.PowerFlowerMK1Tile
import com.tencao.projectbalance.gameObjs.tile.PowerFlowerMK2Tile
import com.tencao.projectbalance.gameObjs.tile.PowerFlowerMK3Tile
import com.tencao.projectbalance.gameObjs.tile.PowerFlowerMK4Tile
import com.tencao.projectbalance.utils.Constants
import moze_intel.projecte.PECore
import moze_intel.projecte.gameObjs.blocks.AlchemicalChest
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemHandlerHelper

class PowerFlower(private val tier: Int) : AlchemicalChest() {

    init {
        this.unlocalizedName = "pe_powerflower_MK" + Integer.toString(tier)
        this.setLightLevel(Constants.COLLECTOR_LIGHT_VALS[tier - 1])
    }

    override fun hasTileEntity(state: IBlockState?): Boolean {
        return true
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity {
        return when (tier) {
            4 -> PowerFlowerMK4Tile()
            3 -> PowerFlowerMK3Tile()
            2 -> PowerFlowerMK2Tile()
            1 -> PowerFlowerMK1Tile()
            else -> PowerFlowerMK1Tile()
        }
    }

    override fun onBlockActivated(world: World, pos: BlockPos?, state: IBlockState?, player: EntityPlayer?, hand: EnumHand?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!world.isRemote)
            when (tier) {
                1 -> player!!.openGui(ProjectBCore, Constants.POWERFLOWER1_GUI, world, pos!!.x, pos.y, pos.z)
                2 -> player!!.openGui(ProjectBCore, Constants.POWERFLOWER2_GUI, world, pos!!.x, pos.y, pos.z)
                3 -> player!!.openGui(ProjectBCore, Constants.POWERFLOWER3_GUI, world, pos!!.x, pos.y, pos.z)
                4 -> player!!.openGui(ProjectBCore, Constants.POWERFLOWER4_GUI, world, pos!!.x, pos.y, pos.z)
            }
        return true
    }

    override fun getComparatorInputOverride(state: IBlockState?, world: World, pos: BlockPos?): Int {
        val te = world.getTileEntity(pos!!)
        if (te != null) {
            val inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
            return ItemHandlerHelper.calcRedstoneFromInventory(inv)
        }
        return 0
    }
}