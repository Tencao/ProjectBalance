package com.tencao.projectbalance.gameObjs.blocks

import com.tencao.projectbalance.gameObjs.state.EnumMatterType
import moze_intel.projecte.api.state.PEStateProps
import moze_intel.projecte.gameObjs.ObjHandler
import moze_intel.projecte.gameObjs.blocks.MatterBlock
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class MatterBlock: MatterBlock() {

    override fun canHarvestBlock(world: IBlockAccess, pos: BlockPos, player: EntityPlayer): Boolean {
        val stack = player.getHeldItem(EnumHand.MAIN_HAND)
        val type = world.getBlockState(pos).getValue(PEStateProps.TIER_PROP)

        return if (!stack.isEmpty) {
            if (type == EnumMatterType.RED_MATTER || type == EnumMatterType.BLUE_MATTER) {
                stack.item === ObjHandler.rmPick || stack.item === ObjHandler.rmStar || stack.item === ObjHandler.rmHammer
            } else {
                stack.item === ObjHandler.rmPick || stack.item === ObjHandler.dmPick || stack.item === ObjHandler.rmStar || stack.item === ObjHandler.dmHammer || stack.item === ObjHandler.rmHammer
            }
        } else false

    }

    override fun getSubBlocks(cTab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (i in 0..2) {
            list.add(ItemStack(this, 1, i))
        }
    }
}