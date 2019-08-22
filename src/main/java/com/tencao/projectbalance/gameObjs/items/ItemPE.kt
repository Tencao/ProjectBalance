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

package com.tencao.projectbalance.gameObjs.items

import com.tencao.projectbalance.utils.EMCHelper
import moze_intel.projecte.gameObjs.items.ItemPE
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler

class ItemPE: ItemPE() {

    fun consumeFuel(inv: IItemHandler, stack: ItemStack, amount: Long, shouldRemove: Boolean): Boolean {
        if (amount <= 0) {
            return true
        }

        val current = getEmc(stack)

        if (current < amount) {
            val consume = EMCHelper.consumeInvFuel(inv, amount - current)

            if (consume == -1L) {
                return false
            }

            addEmcToStack(stack, consume)
        }

        if (shouldRemove) {
            removeEmc(stack, amount)
        }

        return true
    }
}