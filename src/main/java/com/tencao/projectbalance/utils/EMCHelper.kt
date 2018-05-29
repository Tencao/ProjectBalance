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