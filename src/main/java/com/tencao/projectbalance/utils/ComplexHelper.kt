package com.tencao.projectbalance.utils

import com.tencao.projectbalance.mapper.ComplexityMapper
import moze_intel.projecte.emc.EMCMapper
import moze_intel.projecte.emc.SimpleStack
import net.minecraft.item.ItemStack

object ComplexHelper {

    /**
     * Gets the complexity of the ItemStack, this is determined
     * by how many steps it takes to create the item
     * @param stack The ItemStack to check for
     * @return Returns the complexity level
     */
    fun getComplexity(stack: ItemStack): Int {
        return ComplexityMapper.getComplexityValue(SimpleStack(stack))
    }

    /**
     * Gets the craft time in ticks for the craft to complete
     * @param stack The ItemStack to check for
     * @return Returns the time in ticks for it to be crafted
     */
    fun getCraftTime(stack: ItemStack): Int {
        val simpleStack = SimpleStack(stack)
        return EMCMapper.getEmcValue(simpleStack) * ComplexityMapper.getComplexityValue(simpleStack) / 60 / 60
    }
}