/*
 * Copyright (C) 2018
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.tencao.projectbalance.utils

import com.tencao.projectbalance.mapper.Graph
import com.tencao.projectbalance.mapper.ItemComponent
import moze_intel.projecte.emc.EMCMapper
import moze_intel.projecte.emc.SimpleStack
import moze_intel.projecte.utils.EMCHelper
import net.minecraft.item.ItemStack

object ComplexHelper {

    /**
     * Gets the complexity of the ItemStack, this is determined
     * by how many steps it takes to create the item
     * @param stack The ItemStack to check for
     * @return Returns the complexity level
     */
    fun getComplexity(stack: ItemStack): Float {
        val simpleStack = SimpleStack(stack)
        val emcCount: Int = EMCMapper.emc.getOrDefault(simpleStack, 1)
        val complexity = Graph[ItemComponent(simpleStack.toItemStack()).makeOutput()].complexity
        return (emcCount / 1000 + 0.5f) + complexity.toFloat()
    }

    /**
     * Gets the craft time in ticks for the craft to complete
     * @param stack The ItemStack to check for
     * @return Returns the time in ticks for it to be crafted
     */
    fun getCraftTime(stack: ItemStack): Long  {
        val emc = EMCHelper.getEmcValue(stack)
        val complexity = getComplexity(stack)
        return ((emc * complexity) / (emc + complexity)).toLong() * 10
    }
}