package com.tencao.projectbalance.gameObjs.items

import com.google.common.collect.Lists
import com.tencao.projectbalance.events.PedestalEvent
import com.tencao.projectbalance.gameObjs.tile.DMPedestalTile
import com.tencao.projectbalance.gameObjs.tile.ICraftingGen
import moze_intel.projecte.api.item.IPedestalItem
import moze_intel.projecte.gameObjs.items.Tome
import net.minecraft.client.resources.I18n
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World

class Tome: Tome(), IPedestalItem {


    override fun getPedestalDescription(): List<String> {

        val list = Lists.newLinkedList<String>()
        list.add(TextFormatting.BLUE.toString() + I18n.format("pe.tome.pedestal"))
        return list
    }


    override fun updateInPedestal(world: World, pos: BlockPos) {
        if (!world.isRemote) {
            val te = world.getTileEntity(pos)
            if (te is DMPedestalTile) {
                PedestalEvent.getTileEntities(world, te).forEach { craftTile -> (craftTile as ICraftingGen).addTomeToCounter(te) }
            }
        }
    }
}