package com.tencao.projectbalance.gameObjs.blocks

import com.tencao.projectbalance.events.PedestalEvent
import com.tencao.projectbalance.gameObjs.state.EnumMatterType
import com.tencao.projectbalance.gameObjs.state.PEStateProps
import com.tencao.projectbalance.gameObjs.tile.BMPedestalTile
import com.tencao.projectbalance.gameObjs.tile.DMPedestalTile
import com.tencao.projectbalance.gameObjs.tile.RMPedestalTile
import com.tencao.projectbalance.utils.WorldHelper
import moze_intel.projecte.api.item.IPedestalItem
import moze_intel.projecte.gameObjs.blocks.Pedestal
import net.minecraft.block.Block
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class Pedestal: Pedestal() {

    init {
        this.defaultState = this.blockState.baseState.withProperty<EnumMatterType, EnumMatterType>(PEStateProps.TIER_PROP, EnumMatterType.DARK_MATTER)
    }

    private fun dropItem(world: World, pos: BlockPos) {
        val te = world.getTileEntity(pos)
        if (te is DMPedestalTile) {
            val tile = te as DMPedestalTile?
            val stack = tile!!.getInventory().getStackInSlot(0)
            if (!stack.isEmpty) {
                WorldHelper.spawnEntityItem(world, stack, pos.x.toDouble(), pos.y + 0.8, pos.z.toDouble())
                tile.getInventory().setStackInSlot(0, ItemStack.EMPTY)
            }
            PedestalEvent.removeTileEntity(tile)
        }
    }

    override fun breakBlock(world: World, pos: BlockPos, state: IBlockState) {
        dropItem(world, pos)
        super.breakBlock(world, pos, state)
    }

    override fun onBlockClicked(world: World, pos: BlockPos, player: EntityPlayer) {
        if (!world.isRemote) {
            dropItem(world, pos)
            val state = world.getBlockState(pos)
            world.notifyBlockUpdate(pos, state, state, 8)
        }
    }

    override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!world.isRemote) {
            val te = world.getTileEntity(pos) as? DMPedestalTile ?: return true

            val item = te.getInventory().getStackInSlot(0)
            val stack = player.getHeldItem(hand)

            if (stack.isEmpty
                    && !item.isEmpty
                    && item.item is IPedestalItem) {
                te.setActive(!te.getActive())
                world.notifyBlockUpdate(pos, state, state, 8)
            } else if (!stack.isEmpty && item.isEmpty) {
                te.getInventory().setStackInSlot(0, stack.splitStack(1))
                if (stack.count <= 0) {
                    player.setHeldItem(hand, ItemStack.EMPTY)
                }
                world.notifyBlockUpdate(pos, state, state, 8)
                PedestalEvent.registerTileEntity(te)
            }
        }
        return true
    }

    // [VanillaCopy] Adapted from BlockNote
    override fun neighborChanged(state: IBlockState, world: World, pos: BlockPos, neighbor: Block, neighborPos: BlockPos) {
        val flag = world.isBlockPowered(pos)
        val te = world.getTileEntity(pos)

        if (te is DMPedestalTile) {
            if (te.previousRedstoneState != flag) {
                if (flag && !te.getInventory().getStackInSlot(0).isEmpty
                        && te.getInventory().getStackInSlot(0).item is IPedestalItem) {
                    te.setActive(!te.getActive())
                    world.notifyBlockUpdate(pos, state, state, 11)
                }

                te.previousRedstoneState = flag
            }
        }
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity {
        val type = state.getValue(PEStateProps.TIER_PROP)
        return when (type) {
            EnumMatterType.RED_MATTER -> RMPedestalTile()
            EnumMatterType.BLUE_MATTER -> BMPedestalTile()
            else -> DMPedestalTile()
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        return this.getMetaFromState(state)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(PEStateProps.TIER_PROP).ordinal
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return defaultState.withProperty(PEStateProps.TIER_PROP, EnumMatterType.values()[meta])
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, PEStateProps.TIER_PROP)
    }

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(cTab: CreativeTabs?, list: NonNullList<ItemStack>) {
        for (i in 0..2) {
            list.add(ItemStack(this, 1, i))
        }
    }

}