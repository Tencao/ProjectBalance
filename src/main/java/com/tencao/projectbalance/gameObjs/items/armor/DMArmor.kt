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
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class DMArmor(armorPiece: EntityEquipmentSlot) : ArmorBase(ArmorMaterial.DIAMOND, 0, armorPiece, ProjectBConfig.tweaks.DMMaxEMC), ISpecialArmor {
    init {
        this.creativeTab = ObjHandler.cTab
        this.unlocalizedName = "pe_dm_armor_" + armorPiece.index
        this.maxDamage = 0
    }

    override fun getProperties(player: EntityLivingBase, armor: ItemStack, source: DamageSource, damage: Double, slot: Int): ISpecialArmor.ArmorProperties {
        val shieldedDamage = damage - getUnshieldedDamage(armor, damage)
        val type = (armor.item as DMArmor).armorType
        if (type == EntityEquipmentSlot.HEAD || type == EntityEquipmentSlot.FEET) {
            return ISpecialArmor.ArmorProperties(0, 0.15, shieldedDamage.toInt() * 26)
        }
        return if (type == EntityEquipmentSlot.LEGS) {
            ISpecialArmor.ArmorProperties(0, 0.22, shieldedDamage.toInt() * 26)
        } else ISpecialArmor.ArmorProperties(0, 0.26, shieldedDamage.toInt() * 26)
    }

    override fun getArmorDisplay(player: EntityPlayer, armor: ItemStack, slot: Int): Int {
        val type = (armor.item as DMArmor).armorType
        return if (type == EntityEquipmentSlot.HEAD || type == EntityEquipmentSlot.FEET) 4 else 6
    }

    override fun damageArmor(entity: EntityLivingBase, stack: ItemStack, source: DamageSource, damage: Int, slot: Int) {}

    @SideOnly(Side.CLIENT)
    override fun getArmorTexture(stack: ItemStack, entity: Entity, slot: EntityEquipmentSlot, type: String): String {
        val index = if (this.armorType === EntityEquipmentSlot.LEGS) '2' else '1'
        return ProjectBCore.MODID + ":textures/armor/darkmatter_" + index + ".png"
    }


    private fun getDamageCost(damage: Double): Double {
        return damage * ProjectBConfig.tweaks.DMDamagePer
    }

    private fun getUnshieldedDamage(stack: ItemStack, damage: Double): Double {
        val cost = getDamageCost(damage)
        return if (cost >= getStoredEmc(stack)) {
            removeEmc(stack, cost)
            (cost - getStoredEmc(stack)) / ProjectBConfig.tweaks.DMDamagePer
        } else
            0.0
    }

    @SideOnly(Side.CLIENT)
    override fun hasEffect(stack: ItemStack): Boolean {
        return getUnshieldedDamage(stack, 1.0) == 0.0
    }
}
