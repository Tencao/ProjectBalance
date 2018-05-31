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

import com.google.common.collect.Multimap
import moze_intel.projecte.PECore
import moze_intel.projecte.gameObjs.items.IFlightProvider
import moze_intel.projecte.gameObjs.items.IStepAssister
import moze_intel.projecte.utils.ClientKeyHelper
import moze_intel.projecte.utils.ItemHelper
import moze_intel.projecte.utils.PEKeybind
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*


class GemFeet : GemArmorBase(EntityEquipmentSlot.FEET), IFlightProvider, IStepAssister {

    fun toggleStepAssist(boots: ItemStack, player: EntityPlayer) {
        val value: Boolean = if (ItemHelper.getOrCreateCompound(boots).hasKey("StepAssist")) {
            boots.tagCompound!!.setBoolean("StepAssist", !boots.tagCompound!!.getBoolean("StepAssist"))
            boots.tagCompound!!.getBoolean("StepAssist")
        } else {
            boots.tagCompound!!.setBoolean("StepAssist", false)
            false
        }

        val e = if (value) TextFormatting.GREEN else TextFormatting.RED
        val s = if (value) "pe.gem.enabled" else "pe.gem.disabled"
        player.sendMessage(TextComponentTranslation("pe.gem.stepassist_tooltip").appendText(" ")
                .appendSibling(TextComponentTranslation(s).setStyle(Style().setColor(e))))
    }

    override fun onArmorTick(world: World, player: EntityPlayer, stack: ItemStack) {
        if (!world.isRemote) {
            val playerMP = player as EntityPlayerMP
            playerMP.fallDistance = 0f
        } else {
            if (!player.capabilities.isFlying && PECore.proxy.isJumpPressed) {
                player.motionY += 0.1
            }

            if (!player.onGround) {
                if (player.motionY <= 0) {
                    player.motionY *= 0.90
                }
                if (!player.capabilities.isFlying) {
                    if (player.moveForward < 0) {
                        player.motionX *= 0.9
                        player.motionZ *= 0.9
                    } else if (player.moveForward > 0 && player.motionX * player.motionX + player.motionY * player.motionY + player.motionZ * player.motionZ < 3) {
                        player.motionX *= 1.1
                        player.motionZ *= 1.1
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack?, world: World?, tooltips: MutableList<String>?, flags: ITooltipFlag?) {
        tooltips!!.add(I18n.format("pe.gem.feet.lorename"))
        tooltips.add(I18n.format("pe.gem.stepassist.prompt", ClientKeyHelper.getKeyName(PEKeybind.ARMOR_TOGGLE)))

        val e = if (canStep(stack!!)) TextFormatting.GREEN else TextFormatting.RED
        val s = if (canStep(stack)) "pe.gem.enabled" else "pe.gem.disabled"
        tooltips.add(I18n.format("pe.gem.stepassist_tooltip") + " "
                + e + I18n.format(s))
    }

    private fun canStep(stack: ItemStack): Boolean {
        return stack.tagCompound != null && stack.tagCompound!!.hasKey("StepAssist") && stack.tagCompound!!.getBoolean("StepAssist")
    }

    override fun getAttributeModifiers(slot: EntityEquipmentSlot, stack: ItemStack?): Multimap<String, AttributeModifier> {
        if (slot != EntityEquipmentSlot.FEET) return super.getAttributeModifiers(slot, stack)
        val multimap = super.getAttributeModifiers(slot, stack)
        multimap.put(SharedMonsterAttributes.MOVEMENT_SPEED.name, AttributeModifier(MODIFIER, "Armor modifier", 1.0, 2))
        return multimap
    }

    override fun canProvideFlight(stack: ItemStack, player: EntityPlayerMP): Boolean {
        return player.getItemStackFromSlot(EntityEquipmentSlot.FEET) == stack
    }

    override fun canAssistStep(stack: ItemStack, player: EntityPlayerMP): Boolean {
        return player.getItemStackFromSlot(EntityEquipmentSlot.FEET) == stack && canStep(stack)
    }

    companion object {

        private val MODIFIER = UUID.randomUUID()

        fun isStepAssistEnabled(boots: ItemStack): Boolean {
            return !boots.hasTagCompound() || !boots.tagCompound!!.hasKey("StepAssist") || boots.tagCompound!!.getBoolean("StepAssist")

        }
    }
}
