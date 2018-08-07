package com.tencao.projectbalance.gameObjs.items.tools

import com.google.common.collect.Multimap
import com.tencao.projectbalance.config.ProjectBConfig
import moze_intel.projecte.api.item.IExtraFunction
import moze_intel.projecte.utils.ItemHelper
import moze_intel.projecte.utils.PlayerHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemSword
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumHand
import net.minecraft.world.World

open class DarkSword : ItemSword, IItemMode, IExtraFunction {

    final override val numCharges: Int
    final override val numModes: Byte
    final override val modes: Array<String>
    val damage: Float

    constructor() : this("dm_sword", PEToolBase.darkMatter, 12F, 2, emptyArray())

    constructor(name: String, material: Item.ToolMaterial, damage: Float, numCharges: Int, modeDesc: Array<String>): super(material){
        this.numCharges = numCharges
        this.modes = modeDesc
        this.numModes = modeDesc.size.toByte()
        this.setNoRepair()
        this.unlocalizedName = "pe_$name"
        this.maxDamage = 0
        this.creativeTab = moze_intel.projecte.gameObjs.ObjHandler.cTab
        this.damage = damage
    }

    override fun getDurabilityForDisplay(stack: ItemStack): Double {
        val charge = getCharge(stack)

        return if (charge == 0) 1.0 else 1.0 - charge.toDouble() / numCharges.toDouble()
    }

    override fun onCreated(stack: ItemStack, world: World, player: EntityPlayer) {
        if (!world.isRemote) {
            stack.tagCompound = NBTTagCompound()
        }
    }

    override fun onUpdate(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
        if (!stack.hasTagCompound()) {
            stack.tagCompound = NBTTagCompound()
        }
    }

    override fun shouldCauseReequipAnimation(oldStack: ItemStack?, newStack: ItemStack?, slotChanged: Boolean): Boolean {
        return !ItemHelper.basicAreStacksEqual(oldStack, newStack)
    }

    override fun showDurabilityBar(stack: ItemStack): Boolean {
        return stack.hasTagCompound()
    }

    override fun isEnchantable(stack: ItemStack): Boolean {
        return ProjectBConfig.tweaks.allowItemEnchants
    }

    /**
     * Returns the amount of damage this item will deal. One heart of damage is equal to 2 damage points.
     */
    override fun getAttackDamage(): Float {
        return damage
    }

    override fun hitEntity(stack: ItemStack, damaged: EntityLivingBase, damager: EntityLivingBase): Boolean {
        PEToolBase.attackWithCharge(stack, damaged, damager, 1.0f)
        return true
    }

    override fun doExtraFunction(stack: ItemStack, player: EntityPlayer, hand: EnumHand?): Boolean {
        return if (player.getCooledAttackStrength(0f) == 1f) {
            PEToolBase.attackAOE(stack, player, false, damage, 0, hand!!)
            PlayerHelper.resetCooldown(player)
            true
        } else {
            false
        }
    }

    override fun getAttributeModifiers(slot: EntityEquipmentSlot, stack: ItemStack?): Multimap<String, AttributeModifier> {
        val charge = getCharge(stack!!)
        val damage = damage + charge

        val multimap = super.getAttributeModifiers(slot, stack)
        multimap.put(SharedMonsterAttributes.ATTACK_SPEED.name, AttributeModifier(Item.ATTACK_SPEED_MODIFIER, "Tool modifier", -2.4, 0))
        return multimap
    }
}
