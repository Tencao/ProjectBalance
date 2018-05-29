package com.tencao.projectbalance.handlers

import com.tencao.projectbalance.ProjectBCore
import com.tencao.projectbalance.config.ProjectBConfig
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.ICapabilityProvider

class InternalCooldowns(player: EntityPlayerMP?) {

    private var healCooldown = 0
    private var foodCooldown = 0
    private var healingTimer = 0
    private var foodTimer = 0

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

    }

    companion object {
        @CapabilityInject(InternalCooldowns::class)
        lateinit var CAPABILITY: Capability<InternalCooldowns>
        val NAME = ResourceLocation(ProjectBCore.MODID, "internal_cooldowns")

    }

    class Provider(player: EntityPlayerMP) : ICapabilityProvider {
        private val capInstance: InternalCooldowns = InternalCooldowns(player)

        override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
            return capability === CAPABILITY
        }

        override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
            return if (capability === CAPABILITY)
                CAPABILITY.cast(capInstance)
            else
                null
        }
    }
}

fun EntityPlayer.getInternalCooldowns() = this.getCapability(InternalCooldowns.CAPABILITY, null)!!