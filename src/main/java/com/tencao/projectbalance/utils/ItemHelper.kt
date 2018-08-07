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

import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack

object ItemHelper {

    fun decreaseStack(stack: ItemStack, inventory: IInventory, amount: Int): Boolean {
        for (i in 0 until inventory.sizeInventory) {
            if (!inventory.getStackInSlot(i).isEmpty && ItemStack.areItemStacksEqual(stack, inventory.getStackInSlot(i))) {
                inventory.decrStackSize(i, amount)
                return true
            }
        }
        return false
    }
}