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

import com.tencao.projectbalance.ProjectBCore
import com.tencao.projectbalance.config.ProjectBConfig
import moze_intel.projecte.gameObjs.ObjHandler
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.util.DamageSource
import net.minecraftforge.common.ISpecialArmor
import net.minecraftforge.fml.common.Optional
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import thaumcraft.api.items.IGoggles
import thaumcraft.api.items.IRevealer

@Optional.InterfaceList(value = [(Optional.Interface(iface = "thaumcraft.api.items.IRevealer", modid = "Thaumcraft")), (Optional.Interface(iface = "thaumcraft.api.items.IGoggles", modid = "Thaumcraft"))])
class RMArmor(armorType: EntityEquipmentSlot) : ArmorBase(ArmorMaterial.DIAMOND, 0, armorType, ProjectBConfig.tweaks.RMMaxEMC), ISpecialArmor, IRevealer, IGoggles {
    init {
        this.creativeTab = ObjHandler.cTab
        this.unlocalizedName = "pe_rm_armor_" + armorType.index
        this.maxDamage = 0
    }

    override fun getProperties(player: EntityLivingBase, armor: ItemStack, source: DamageSource, damage: Double, slot: Int): ISpecialArmor.ArmorProperties {
        val shieldedDamage = damage - getUnshieldedDamage(armor, damage)
        val type = (armor.item as RMArmor).armorType
        if (type == EntityEquipmentSlot.HEAD || type == EntityEquipmentSlot.FEET) {
            return ISpecialArmor.ArmorProperties(0, 0.18, shieldedDamage.toInt() * 26)
        }
        return if (type == EntityEquipmentSlot.LEGS) {
            ISpecialArmor.ArmorProperties(0, 0.25, shieldedDamage.toInt() * 26)
        } else ISpecialArmor.ArmorProperties(0, 0.29, shieldedDamage.toInt() * 26)
    }

    override fun getArmorDisplay(player: EntityPlayer, armor: ItemStack, slot: Int): Int {
        val type = (armor.item as RMArmor).armorType
        return if (type == EntityEquipmentSlot.HEAD || type == EntityEquipmentSlot.FEET) 4 else 6
    }

    override fun damageArmor(entity: EntityLivingBase, stack: ItemStack, source: DamageSource, damage: Int, slot: Int) {}

    @SideOnly(Side.CLIENT)
    override fun getArmorTexture(stack: ItemStack, entity: Entity, slot: EntityEquipmentSlot, type: String): String {
        val index = if (this.armorType === EntityEquipmentSlot.LEGS) '2' else '1'
        return ProjectBCore.MODID + ":textures/armor/redmatter_" + index + ".png"
    }

    @Optional.Method(modid = "Thaumcraft")
    override fun showIngamePopups(itemstack: ItemStack, player: EntityLivingBase): Boolean {
        return (itemstack.item as RMArmor).armorType === EntityEquipmentSlot.HEAD
    }

    @Optional.Method(modid = "Thaumcraft")
    override fun showNodes(itemstack: ItemStack, player: EntityLivingBase): Boolean {
        return (itemstack.item as RMArmor).armorType === EntityEquipmentSlot.HEAD
    }

    private fun getDamageCost(damage: Double): Double {
        return damage * ProjectBConfig.tweaks.RMDamagePer
    }

    private fun getUnshieldedDamage(stack: ItemStack, damage: Double): Double {
        val cost = getDamageCost(damage)
        return if (cost >= getStoredEmc(stack)) {
            removeEmc(stack, cost)
            (cost - getStoredEmc(stack)) / ProjectBConfig.tweaks.RMDamagePer
        } else
            0.0
    }

    @SideOnly(Side.CLIENT)
    override fun hasEffect(stack: ItemStack): Boolean {
        return getUnshieldedDamage(stack, 1.0) == 0.0
    }
}