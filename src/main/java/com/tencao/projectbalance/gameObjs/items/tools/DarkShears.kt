package com.tencao.projectbalance.gameObjs.items.tools

import com.tencao.projectbalance.config.ProjectBConfig
import moze_intel.projecte.utils.ItemHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemShears
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World

open class DarkShears : ItemShears, IItemMode {

    final override val numCharges: Int
    final override val numModes: Byte = 0
    final override val modes: Array<String> = emptyArray()

    constructor() : this("dm_shears", 2)

    constructor(name: String, numCharges: Int): super(){
        this.numCharges = numCharges
        this.setNoRepair()
        this.unlocalizedName = "pe_$name"
        this.maxDamage = 0
        this.creativeTab = moze_intel.projecte.gameObjs.ObjHandler.cTab
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

    override fun onItemRightClick(world: World?, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {
        val stack = player.getHeldItem(hand)
        PEToolBase.shearEntityAOE(stack, player, 0, hand)
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack)
    }
}
