package com.tencao.projectbalance.gameObjs.items.tools

import com.tencao.projectbalance.config.ProjectBConfig
import moze_intel.projecte.api.state.PEStateProps
import moze_intel.projecte.api.state.enums.EnumMatterType
import moze_intel.projecte.config.ProjectEConfig
import moze_intel.projecte.gameObjs.ObjHandler
import moze_intel.projecte.utils.ItemHelper
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemPickaxe
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World

open class DarkPick : ItemPickaxe, IItemMode {

    final override val numCharges: Int
    final override val numModes: Byte
    final override val modes: Array<String>

    constructor() : this("dm_pick", PEToolBase.darkMatter, 7F, 2, arrayOf<String>("pe.darkpick.mode1", "pe.darkpick.mode2", "pe.darkpick.mode3", "pe.darkpick.mode4"))

    constructor(name: String, material: ToolMaterial, damage: Float, numCharges: Int, modeDesc: Array<String>): super(material){
        this.numCharges = numCharges
        this.modes = modeDesc
        this.numModes = modeDesc.size.toByte()
        this.setNoRepair()
        this.translationKey = "pe_$name"
        this.maxDamage = 0
        this.creativeTab = ObjHandler.cTab
        this.attackDamage = damage + material.attackDamage
        this.attackSpeed = -2.8F
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
        if (world!!.isRemote) {
            return ActionResult.newResult(EnumActionResult.SUCCESS, stack)
        }

        if (ProjectEConfig.items.pickaxeAoeVeinMining) {
            PEToolBase.mineOreVeinsInAOE(stack, player, hand)
        } else {
            val mop = this.rayTrace(world, player, false)
            if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
                if (ItemHelper.isOre(world.getBlockState(mop.blockPos))) {
                    PEToolBase.tryVeinMine(stack, player, mop)
                }
            }
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, stack)
    }

    override fun onBlockDestroyed(stack: ItemStack, world: World, state: IBlockState, pos: BlockPos, eLiving: EntityLivingBase): Boolean {
        PEToolBase.digBasedOnMode(stack, world, state.block, pos, eLiving)
        return true
    }

    override fun getDestroySpeed(stack: ItemStack, state: IBlockState): Float {
        val block = state.block
        return if (block === ObjHandler.matterBlock && state.getValue(PEStateProps.TIER_PROP) == EnumMatterType.DARK_MATTER
                || block === ObjHandler.dmFurnaceOff
                || block === ObjHandler.dmFurnaceOn) {
            1200000.0f
        } else return super.getDestroySpeed(stack, state)
    }
}
