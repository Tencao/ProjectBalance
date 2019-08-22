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

package com.tencao.projectbalance.handlers

import be.bluexin.saomclib.capabilities.AbstractEntityCapability
import be.bluexin.saomclib.capabilities.Key
import be.bluexin.saomclib.onClient
import be.bluexin.saomclib.onServer
import com.google.common.math.LongMath
import com.tencao.projectbalance.ProjectBCore
import com.tencao.projectbalance.config.ProjectBConfig
import com.tencao.projectbalance.utils.ComplexHelper
import moze_intel.projecte.api.ProjectEAPI
import moze_intel.projecte.utils.Constants
import moze_intel.projecte.utils.EMCHelper
import moze_intel.projecte.utils.ItemHelper
import moze_intel.projecte.utils.PlayerHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemHandlerHelper

class InternalCooldowns: AbstractEntityCapability() {

    private var healCooldown = 0
    private var foodCooldown = 0
    private var healingTimer = 0
    private var foodTimer = 0
    private var requiredTime = 0L
    private var timePassed = 0L
    private var stack = ItemStack.EMPTY

    fun triggerHealCooldown() {
        healCooldown = ProjectBConfig.tweaks.healCooldown * 20
        healingTimer = ProjectBConfig.tweaks.healingDuration * 20
    }

    fun canHeal(): Boolean {
        return healCooldown == 0
    }

    fun isHealing(): Boolean {
        return healingTimer > 0
    }

    fun triggerFoodCooldown() {
        foodCooldown = ProjectBConfig.tweaks.foodCooldown * 20
        foodTimer = ProjectBConfig.tweaks.foodDuration * 20
    }

    fun canFeed(): Boolean {
        return foodCooldown == 0
    }

    fun isFeeding(): Boolean {
        return foodTimer > 0
    }

    fun setStack(stack: ItemStack){
        if (ItemStack.areItemsEqual(stack, this.stack)){
            if ((this.stack.count + stack.count) < this.stack.maxStackSize)
                this.stack.count += stack.count
            else
                this.stack.count = stack.maxStackSize
        }
        else {
            val player = (reference.get() as EntityPlayer)
            if (!this.stack.isEmpty){
                val provider = player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null)!!
                provider.emc = provider.emc + LongMath.checkedMultiply(EMCHelper.getEmcSellValue(this.stack), this.stack.count.toLong())

                if (provider.emc >= Constants.TILE_MAX_EMC || provider.emc < 0) {
                    provider.emc = Constants.TILE_MAX_EMC
                }

                if (!player.entityWorld.isRemote) {
                    PlayerHelper.updateScore(player as EntityPlayerMP, PlayerHelper.SCOREBOARD_EMC, provider.emc.toInt())
                }
            }
            this.stack = stack
            requiredTime = ComplexHelper.getCraftTime(stack)
            timePassed = 0
        }
    }

    fun getStack(): ItemStack{
        return stack
    }

    fun getStackLimit(stack: ItemStack): Int{
        return if (this.stack.isStackable) {
            if (ItemStack.areItemsEqual(stack, this.stack))
                this.stack.maxStackSize - this.stack.count
            else stack.maxStackSize
        }
        else 0
    }

    fun getRequiredTime(): Long{
        return requiredTime
    }

    fun getTimePassed(): Long{
        return timePassed
    }

    /**
     * Used by server sync packet
     */
    @SideOnly(Side.CLIENT)
    fun syncPacket(stack: ItemStack, requiredTime: Long, timePassed: Long){
        this.stack = stack
        this.requiredTime = requiredTime
        this.timePassed = timePassed
    }

    fun tick(){

        if (healCooldown > 0) {
            healCooldown--
        }

        if (healingTimer > 0) {
            healingTimer--
        }

        if (foodCooldown > 0) {
            foodCooldown--
        }

        if (foodTimer > 0) {
            foodTimer--
        }

        if (stack != ItemStack.EMPTY){
            if (requiredTime > 100 && timePassed < requiredTime){
                timePassed++
            }
            else {
                reference.get()!!.world.onServer {
                            if (ItemHelper.hasSpace((reference.get() as EntityPlayer).inventory.mainInventory, stack)) {
                                val inv = (reference.get() as EntityPlayer).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)
                                ItemHandlerHelper.insertItemStacked(inv, ItemHelper.getNormalizedStack(stack.splitStack(1)), false)
                            }
                        }
                reference.get()!!.world.onClient {
                    stack.count--
                }
                timePassed = 0
                if (stack.count == 0 || stack.isEmpty) {
                    stack = ItemStack.EMPTY
                    requiredTime = 0
                }
            }
        }

    }

    override val shouldSyncOnDeath = true
    override val shouldSyncOnDimensionChange = true
    override val shouldRestoreOnDeath = true
    override val shouldSendOnLogin = true

    companion object {
        @CapabilityInject(InternalCooldowns::class)
        lateinit var CAPABILITY: Capability<InternalCooldowns>

        @Key
        val KEY = ResourceLocation(ProjectBCore.MODID, "internal_cooldowns")

    }

    object DummyStorage: Capability.IStorage<InternalCooldowns> {
        override fun readNBT(capability: Capability<InternalCooldowns>, instance: InternalCooldowns, side: EnumFacing?, nbt: NBTBase) {
            val nbtTagCompound = nbt as? NBTTagCompound?: return
            instance.stack = ItemStack(nbtTagCompound.getCompoundTag("stack"))
            instance.requiredTime = nbtTagCompound.getLong("requiredTime")
            instance.timePassed = nbtTagCompound.getLong("timePassed")
            instance.healCooldown = nbtTagCompound.getInteger("healCooldown")
            instance.foodCooldown = nbtTagCompound.getInteger("foodCooldown")
        }

        override fun writeNBT(capability: Capability<InternalCooldowns>, instance: InternalCooldowns, side: EnumFacing?): NBTBase {
            val nbt = NBTTagCompound()
            nbt.setTag("stack", instance.stack.serializeNBT())
            nbt.setLong("requiredTime", instance.requiredTime)
            nbt.setLong("timePassed", instance.timePassed)
            nbt.setInteger("healCooldown", instance.healCooldown)
            nbt.setInteger("foodCooldown", instance.foodCooldown)

            return NBTTagCompound()
        }
    }

}

fun EntityPlayer.getInternalCooldowns() = this.getCapability(InternalCooldowns.CAPABILITY, null)!!