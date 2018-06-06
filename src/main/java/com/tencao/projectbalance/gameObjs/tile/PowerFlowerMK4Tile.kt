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

import com.tencao.projectbalance.utils.Constants
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates
import moze_intel.projecte.utils.EMCHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import net.minecraftforge.items.wrapper.CombinedInvWrapper

class PowerFlowerMK4Tile : PowerFlowerMK1Tile(Constants.POWER_FLOWER_MK4_MAX, Constants.POWER_FLOWER_MK4_GEN) {

    override fun createAutomationInventory(): IItemHandler {
        val automationInput = object : WrappedItemHandler(input, WrappedItemHandler.WriteMode.IN) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                return if (SlotPredicates.HAS_EMC.test(stack) && !isStackEqualToLock(stack))
                    super.insertItem(slot, stack, simulate)
                else
                    stack
            }
        }
        val automationOutput = WrappedItemHandler(output, WrappedItemHandler.WriteMode.OUT)
        return CombinedInvWrapper(automationInput, automationOutput)
    }

    override fun createInput(): ItemStackHandler {
        return this.StackHandler(42)
    }

    override fun createOutput(): ItemStackHandler {
        return this.StackHandler(42)
    }

    override fun condense() {
        if (this.hasSpace()) {
            for (i in 0 until input.slots) {
                val stack = input.getStackInSlot(i)

                if (stack.isEmpty) {
                    continue
                }

                this.addEMC((EMCHelper.getEmcSellValue(stack) * stack.count).toDouble())
                input.setStackInSlot(i, ItemStack.EMPTY)
                break
            }
        }
        craft()
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        output.deserializeNBT(nbt.getCompoundTag("Output"))
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        var nbt = nbt
        nbt = super.writeToNBT(nbt)
        nbt.setTag("Output", output.serializeNBT())
        return nbt
    }
}
