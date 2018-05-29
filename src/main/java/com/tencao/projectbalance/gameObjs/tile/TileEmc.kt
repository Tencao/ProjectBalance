package com.tencao.projectbalance.gameObjs.tile

import com.google.common.base.Predicates
import com.google.common.collect.Maps
import moze_intel.projecte.api.tile.IEmcAcceptor
import moze_intel.projecte.api.tile.IEmcProvider
import moze_intel.projecte.api.tile.TileEmcBase
import moze_intel.projecte.gameObjs.tiles.RelayMK1Tile
import moze_intel.projecte.utils.Constants
import moze_intel.projecte.utils.WorldHelper
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.ItemStackHandler


abstract class TileEmc : TileEmcBase, ITickable {
    constructor() {
        setMaximumEMC(Constants.TILE_MAX_EMC.toDouble())
    }

    constructor(maxAmount: Int) {
        setMaximumEMC(maxAmount.toDouble())
    }

    override fun getUpdateTag(): NBTTagCompound {
        return writeToNBT(NBTTagCompound())
    }

    override fun shouldRefresh(world: World?, pos: BlockPos?, state: IBlockState, newState: IBlockState): Boolean {
        return state.block !== newState.block
    }

    protected fun hasMaxedEmc(): Boolean {
        return storedEmc >= maximumEmc
    }

    /**
     * The amount provided will be divided and evenly distributed as best as possible between adjacent IEMCAcceptors
     * Remainder or rejected EMC is added back to this provider
     *
     * @param emc The maximum combined emc to send to others
     */
    protected fun sendToAllAcceptors(emc: Double) {
        if (this !is IEmcProvider) {
            // todo move this method somewhere
            throw UnsupportedOperationException("sending without being a provider")
        }


        val tiles = Maps.filterValues(WorldHelper.getAdjacentTileEntitiesMapped(world, this), Predicates.instanceOf(IEmcAcceptor::class.java))

        val emcPer = emc / tiles.size
        for ((key, value) in tiles) {
            if (this is RelayMK1Tile && value is RelayMK1Tile) {
                continue
            }
            val provide = (this as IEmcProvider).provideEMC(key.opposite, emcPer)
            val remain = provide - (value as IEmcAcceptor).acceptEMC(key, provide)
            this.addEMC(remain)
        }
    }

    internal open inner class StackHandler(size: Int) : ItemStackHandler(size) {

        val items: NonNullList<ItemStack>
            get() = stacks

        public override fun onContentsChanged(slot: Int) {
            this@TileEmc.markDirty()
        }
    }
}
