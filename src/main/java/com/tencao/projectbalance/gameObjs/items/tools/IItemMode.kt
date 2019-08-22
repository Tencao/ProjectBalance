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

package com.tencao.projectbalance.gameObjs.items.tools

import moze_intel.projecte.api.PESounds
import moze_intel.projecte.api.item.IItemCharge
import moze_intel.projecte.api.item.IModeChanger
import moze_intel.projecte.utils.EMCHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.util.text.TextComponentTranslation

interface IItemMode: IModeChanger, IItemCharge {

    val numCharges: Int
    val numModes: Byte
    val modes: Array<String>

    override fun getNumCharges(stack: ItemStack): Int {
        return numCharges
    }

    fun addEmcToStack(stack: ItemStack, amount: Long) {
        setEmc(stack, getEmc(stack) + amount)
    }

    fun getEmc(stack: ItemStack): Long {
        if (stack.tagCompound == null) {
            stack.tagCompound = NBTTagCompound()
        }

        return stack.tagCompound!!.getLong("StoredEMC")
    }

    fun removeEmc(stack: ItemStack, amount: Long) {
        var result = getEmc(stack) - amount

        if (result < 0) {
            result = 0L
        }

        setEmc(stack, result)
    }

    fun setEmc(stack: ItemStack, amount: Long) {
        if (stack.tagCompound == null) {
            stack.tagCompound = NBTTagCompound()
        }

        stack.tagCompound!!.setLong("StoredEMC", amount)
    }

    override fun changeCharge(player: EntityPlayer, stack: ItemStack, hand: EnumHand?): Boolean {
        val currentCharge = getCharge(stack)

        if (player.isSneaking) {
            if (currentCharge > 0) {
                player.entityWorld.playSound(null, player.posX, player.posY, player.posZ, PESounds.UNCHARGE, SoundCategory.PLAYERS, 1.0f, 0.5f + 0.5f / numCharges * currentCharge)
                stack.tagCompound!!.setInteger("Charge", (currentCharge - 1))
                return true
            }
        } else if (currentCharge < numCharges) {
            player.entityWorld.playSound(null, player.posX, player.posY, player.posZ, PESounds.CHARGE, SoundCategory.PLAYERS, 1.0f, 0.5f + 0.5f / numCharges * currentCharge)
            stack.tagCompound!!.setInteger("Charge", (currentCharge + 1))
            return true
        }

        return false
    }

    override fun changeMode(player: EntityPlayer, stack: ItemStack, hand: EnumHand?): Boolean {
        if (numModes == 0.toByte()) {
            return false
        }

        val newMode = (getMode(stack) + 1).toByte()
        stack.tagCompound!!.setByte("Mode", if (newMode > numModes - 1) 0 else newMode)

        val modeName = TextComponentTranslation(modes[getMode(stack).toInt()])
        player.sendMessage(TextComponentTranslation("pe.item.mode_switch", modeName))
        return true
    }

    override fun getCharge(stack: ItemStack): Int {
        return if (stack.hasTagCompound()) stack.tagCompound!!.getInteger("Charge") else 0
    }

    override fun getMode(stack: ItemStack): Byte {
        return if (stack.hasTagCompound()) stack.tagCompound!!.getByte("Mode") else 0
    }

    private fun getUnlocalizedMode(stack: ItemStack): String {
        return modes[stack.tagCompound!!.getByte("Mode").toInt()]
    }

    fun consumeFuel(player: EntityPlayer, stack: ItemStack, amount: Long, shouldRemove: Boolean): Boolean {
        if (amount <= 0) {
            return true
        }

        val current = getEmc(stack)

        if (current < amount) {
            val consume = EMCHelper.consumePlayerFuel(player, amount - current)

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