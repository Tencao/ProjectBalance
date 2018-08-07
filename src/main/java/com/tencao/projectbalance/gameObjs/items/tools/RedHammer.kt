package com.tencao.projectbalance.gameObjs.items.tools

import moze_intel.projecte.api.state.PEStateProps
import moze_intel.projecte.api.state.enums.EnumMatterType
import moze_intel.projecte.gameObjs.ObjHandler
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack

class RedHammer : DarkHammer("rm_hammer", PEToolBase.redMatter, 14F, 3) {

    override fun getDestroySpeed(stack: ItemStack, state: IBlockState): Float {
        val block = state.block
        return if (block === ObjHandler.matterBlock && state.getValue(PEStateProps.TIER_PROP) == EnumMatterType.RED_MATTER
                || block === ObjHandler.rmFurnaceOff
                || block === ObjHandler.rmFurnaceOn) {
            1200000.0f
        } else super.getDestroySpeed(stack, state)
    }
}
