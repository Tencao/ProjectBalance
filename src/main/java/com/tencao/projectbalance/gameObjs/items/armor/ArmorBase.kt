package com.tencao.projectbalance.gameObjs.items.armor

import moze_intel.projecte.api.item.IItemEmc
import net.minecraft.entity.Entity
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World

abstract class ArmorBase(material: ItemArmor.ArmorMaterial, renderindex: Int, armorPiece: EntityEquipmentSlot, private val maximumEMC: Double) : ItemArmor(material, renderindex, armorPiece), IItemEmc {

    private val currentEMC: Double = 0.0

    override fun onUpdate(stack: ItemStack?, world: World?, entity: Entity?, par4: Int, par5: Boolean) {
        if (!stack!!.hasTagCompound()) {
            stack.tagCompound = NBTTagCompound()
        }
    }

    override fun showDurabilityBar(stack: ItemStack): Boolean {
        return stack.hasTagCompound()
    }

    override fun getDurabilityForDisplay(stack: ItemStack): Double {
        return if (getStoredEmc(stack) == 0.0) {
            1.0
        } else 1.0 - getStoredEmc(stack) / getMaximumEmc(stack)

    }

    override fun addEmc(stack: ItemStack, toAdd: Double): Double {
        val add = Math.min(getMaximumEmc(stack) - getStoredEmc(stack), toAdd)
        setEmc(stack, getEmc(stack) + add)
        return add
    }

    override fun extractEmc(stack: ItemStack, toRemove: Double): Double {
        val sub = Math.min(getStoredEmc(stack), toRemove)
        removeEmc(stack, sub)
        return sub
    }

    override fun getStoredEmc(stack: ItemStack): Double {
        return getEmc(stack)
    }

    override fun getMaximumEmc(stack: ItemStack): Double {
        return maximumEMC
    }

    fun acceptEMC(stack: ItemStack, toAccept: Double): Double {
        val storedEmc = getStoredEmc(stack)
        val maxEmc = getMaximumEmc(stack)
        var toAdd = Math.min(maxEmc - storedEmc, toAccept)

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

        private fun getEmc(stack: ItemStack): Double {
            if (stack.tagCompound == null) {
                stack.tagCompound = NBTTagCompound()
            }

            return stack.tagCompound!!.getDouble("StoredEMC")
        }

        private fun setEmc(stack: ItemStack, amount: Double) {
            if (stack.tagCompound == null) {
                stack.tagCompound = NBTTagCompound()
            }

            stack.tagCompound!!.setDouble("StoredEMC", amount)
        }

        fun removeEmc(stack: ItemStack, amount: Double) {
            var result = getEmc(stack) - amount

            if (result < 0) {
                result = 0.0
            }

            setEmc(stack, result)
        }
    }
}
