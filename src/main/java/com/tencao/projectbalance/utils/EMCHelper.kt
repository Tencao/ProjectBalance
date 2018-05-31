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

import moze_intel.projecte.api.item.IItemEmc
import moze_intel.projecte.emc.FuelMapper
import moze_intel.projecte.utils.EMCHelper.getEmcValue
import net.minecraftforge.items.IItemHandler
import java.util.LinkedHashMap

object EMCHelper {

    /**
     * Consumes EMC from fuel items or Klein Stars
     * Any extra EMC is discarded !!! To retain remainder EMC use ItemPE.consumeFuel()
     */
    fun consumeInvFuel(inv: IItemHandler, minFuel: Double): Double {
        val map = LinkedHashMap<Int, Int>()
        var metRequirement = false
        var emcConsumed = 0

        for (i in 0 until inv.slots) {
            val stack = inv.getStackInSlot(i)

            if (stack.isEmpty) {
                continue
            } else if (stack.item is IItemEmc) {
                val itemEmc = stack.item as IItemEmc
                if (itemEmc.getStoredEmc(stack) >= minFuel) {
                    itemEmc.extractEmc(stack, minFuel)
                    return minFuel
                }
            } else if (!metRequirement) {
                if (FuelMapper.isStackFuel(stack)) {
                    val emc = getEmcValue(stack)
                    val toRemove = Math.ceil((minFuel - emcConsumed) / emc.toFloat()).toInt()

                    if (stack.count >= toRemove) {
                        map[i] = toRemove
                        emcConsumed += emc * toRemove
                        metRequirement = true
                    } else {
                        map[i] = stack.count
                        emcConsumed += emc * stack.count

                        if (emcConsumed >= minFuel) {
                            metRequirement = true
                        }
                    }

                }
            }
        }

        if (metRequirement) {
            for ((key, value) in map) {
                inv.extractItem(key, value, false)
            }
            return emcConsumed.toDouble()
        }

        return -1.0
    }
}