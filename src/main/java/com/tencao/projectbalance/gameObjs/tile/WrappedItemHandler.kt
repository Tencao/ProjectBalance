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

package com.tencao.projectbalance.gameObjs.tile

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable

/**
 * IItemHandler implementation for exposure to the public
 * Useful when you want the IItemHandler itself to have full in/out access internally but restricted access in public
 */
open class WrappedItemHandler(private val compose: IItemHandlerModifiable, private val mode: WriteMode) : IItemHandlerModifiable {

    override fun getSlots(): Int {
        return compose.slots
    }

    override fun getStackInSlot(slot: Int): ItemStack {
        return compose.getStackInSlot(slot)
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        return if (mode == WriteMode.IN || mode == WriteMode.IN_OUT)
            compose.insertItem(slot, stack, simulate)
        else
            stack
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        return if (mode == WriteMode.OUT || mode == WriteMode.IN_OUT)
            compose.extractItem(slot, amount, simulate)
        else
            ItemStack.EMPTY
    }

    override fun getSlotLimit(slot: Int): Int {
        return compose.getSlotLimit(slot)
    }

    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        compose.setStackInSlot(slot, stack)
    }

    enum class WriteMode {
        IN,
        OUT,
        IN_OUT,
        NONE
    }
}
