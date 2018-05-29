package com.tencao.projectbalance.gameObjs.blocks

import com.tencao.projectbalance.ProjectBCore
import com.tencao.projectbalance.gameObjs.tile.CondenserMK2Tile
import com.tencao.projectbalance.utils.Constants
import moze_intel.projecte.gameObjs.blocks.CondenserMK2
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class CondenserMK2: CondenserMK2() {

    override fun createTileEntity(world: World, state: IBlockState): TileEntity {
        return CondenserMK2Tile()
    }

    override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!world.isRemote) {
            player.openGui(ProjectBCore, Constants.CONDENSER_MK2_GUI, world, pos.getX(), pos.getY(), pos.getZ())
        }

        return true
    }
}