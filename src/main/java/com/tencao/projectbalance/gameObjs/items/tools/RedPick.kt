package com.tencao.projectbalance.gameObjs.items.tools

import moze_intel.projecte.api.state.PEStateProps
import moze_intel.projecte.api.state.enums.EnumMatterType
import moze_intel.projecte.gameObjs.ObjHandler
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack

class RedPick : DarkPick("rm_pick", PEToolBase.redMatter, 10F, 3, arrayOf("pe.redpick.mode1", "pe.redpick.mode2", "pe.redpick.mode3", "pe.redpick.mode4")) {

    override fun getDestroySpeed(stack: ItemStack, state: IBlockState): Float {
        val b = state.block
        return if (b === ObjHandler.matterBlock && state.getValue(PEStateProps.TIER_PROP) == EnumMatterType.RED_MATTER
                || b === ObjHandler.rmFurnaceOff
                || b === ObjHandler.rmFurnaceOn) {
            1200000.0f
        } else super.getDestroySpeed(stack, state)
    }
}
