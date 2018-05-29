package com.tencao.projectbalance.gameObjs.items

import com.google.common.collect.Lists
import com.tencao.projectbalance.gameObjs.tile.DMPedestalTile
import moze_intel.projecte.api.item.IPedestalItem
import moze_intel.projecte.gameObjs.items.KleinStar
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class KleinStar: KleinStar(), IPedestalItem {

    override fun getPedestalDescription(): List<String> {
        return Lists.newLinkedList()
    }

    override fun updateInPedestal(world: World, pos: BlockPos) {
        if (!world.isRemote) {
            val te = world.getTileEntity(pos)
            if (te is DMPedestalTile) {
                te.scanNearbyPedestals()
            }
        }
    }
}