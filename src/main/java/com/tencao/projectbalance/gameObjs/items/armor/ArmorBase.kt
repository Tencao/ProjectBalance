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

package com.tencao.projectbalance.gameObjs.items.armor

import moze_intel.projecte.api.item.IItemEmc
import net.minecraft.entity.Entity
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import kotlin.math.min

abstract class ArmorBase(material: ArmorMaterial, renderindex: Int, armorPiece: EntityEquipmentSlot, private val maximumEMC: Long) : ItemArmor(material, renderindex, armorPiece), IItemEmc {

    private val currentEMC: Long = 0

    override fun onUpdate(stack: ItemStack?, world: World?, entity: Entity?, par4: Int, par5: Boolean) {
        if (!stack!!.hasTagCompound()) {
            stack.tagCompound = NBTTagCompound()
        }
    }

    override fun showDurabilityBar(stack: ItemStack): Boolean {
        return stack.hasTagCompound()
    }

    override fun getDurabilityForDisplay(stack: ItemStack): Double {
        return if (getStoredEmc(stack) == 0L) {
            1.0
        } else 1.0 - getStoredEmc(stack) / getMaximumEmc(stack)

    }

    override fun getStoredEmc(stack: ItemStack): Long {
        return getEmc(stack)
    }

    override fun getMaximumEmc(stack: ItemStack): Long {
        return maximumEMC
    }

    override fun addEmc(stack: ItemStack, toAdd: Long): Long {
        val add = min(getMaximumEmc(stack) - getStoredEmc(stack), toAdd)
        setEmc(stack, getEmc(stack) + add)
        return add
    }

    override fun extractEmc(stack: ItemStack, toRemove: Long): Long {
        val sub = min(getStoredEmc(stack), toRemove)
        removeEmc(stack, sub)
        return sub
    }

    fun acceptEMC(stack: ItemStack, toAccept: Long): Long {
        val storedEmc = getStoredEmc(stack)
        val maxEmc = getMaximumEmc(stack)
        var toAdd = min(maxEmc - storedEmc, toAccept)

        return if (storedEmc + toAdd <= maxEmc) {
            addEmc(stack, toAdd)
            toAdd
        } else {
            toAdd = maxEmc - storedEmc
            addEmc(stack, toAdd)
            toAdd
        }
    }


    fun requireEMC(stack: ItemStack): Boolean {
        return getStoredEmc(stack) < getMaximumEmc(stack)
    }

    companion object {

        private fun getEmc(stack: ItemStack): Long {
            if (stack.tagCompound == null) {
                stack.tagCompound = NBTTagCompound()
            }

            return stack.tagCompound!!.getLong("StoredEMC")
        }

        private fun setEmc(stack: ItemStack, amount: Long) {
            if (stack.tagCompound == null) {
                stack.tagCompound = NBTTagCompound()
            }

            stack.tagCompound!!.setLong("StoredEMC", amount)
        }

        fun removeEmc(stack: ItemStack, amount: Long) {
            var result = getEmc(stack) - amount

            if (result < 0) {
                result = 0
            }

            setEmc(stack, result)
        }
    }
}
