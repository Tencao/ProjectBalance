package com.tencao.projectbalance.gameObjs.items.tools

import com.google.common.collect.Multimap
import com.tencao.projectbalance.config.ProjectBConfig
import moze_intel.projecte.api.item.IExtraFunction
import moze_intel.projecte.config.ProjectEConfig
import moze_intel.projecte.utils.ItemHelper
import moze_intel.projecte.utils.PlayerHelper
import net.minecraft.block.BlockDirt
import net.minecraft.block.BlockGrass
import net.minecraft.block.BlockLeaves
import net.minecraft.block.BlockLog
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.EnumAction
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemTool
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World

class RedKatar : ItemTool, IItemMode, IExtraFunction {

    override val numCharges: Int
    override val numModes: Byte
    override val modes: Array<String>
    val damage: Float

    constructor() : this("rm_katar", PEToolBase.redMatter, 23F, 4, arrayOf("pe.katar.mode1", "pe.katar.mode2"))

    constructor(name: String, material: ToolMaterial, damage: Float, numCharges: Int, modeDesc: Array<String>): super(damage, -2.4F, material, mutableSetOf()){
        this.numCharges = numCharges
        this.modes = modeDesc
        this.numModes = modeDesc.size.toByte()
        this.setNoRepair()
        this.translationKey = "pe_$name"
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

    override fun getToolClasses(stack: ItemStack?): MutableSet<String> {
        return mutableSetOf("katar", "sword", "axe", "shears")
    }

    override fun isEnchantable(stack: ItemStack): Boolean {
        return ProjectBConfig.tweaks.allowItemEnchants
    }

    override fun hitEntity(stack: ItemStack, damaged: EntityLivingBase, damager: EntityLivingBase): Boolean {
        PEToolBase.attackWithCharge(stack, damaged, damager, 1.0f)
        return true
    }

    override fun onBlockStartBreak(stack: ItemStack, pos: BlockPos, player: EntityPlayer): Boolean {
        // Shear
        PEToolBase.shearBlock(stack, pos, player)
        return false
    }

    override fun onItemRightClick(world: World?, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {
        val stack = player.getHeldItem(hand)
        if (world!!.isRemote) {
            return ActionResult.newResult(EnumActionResult.SUCCESS, stack)
        }
        val mop: RayTraceResult? = this.rayTrace(world, player, false)
        if (mop != null) {
            if (mop.typeOfHit == RayTraceResult.Type.BLOCK) {
                val state = world.getBlockState(mop.blockPos)
                val blockHit = state.block
                if (blockHit is BlockGrass || blockHit is BlockDirt) {
                    // Hoe
                    PEToolBase.tillAOE(stack, player, world, mop.blockPos, mop.sideHit, 0)
                } else if (blockHit is BlockLog) {
                    // Axe
                    PEToolBase.clearOdAOE(world, stack, player, "logWood", 0, hand)
                } else if (blockHit is BlockLeaves) {
                    // Shear leaves
                    PEToolBase.clearOdAOE(world, stack, player, "treeLeaves", 0, hand)
                }
            }
        } else {
            // Shear
            PEToolBase.shearEntityAOE(stack, player, 0, hand)
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, stack)
    }

    override fun doExtraFunction(stack: ItemStack, player: EntityPlayer, hand: EnumHand?): Boolean {
        return if (player.getCooledAttackStrength(0f) == 1f) {
            PEToolBase.attackAOE(stack, player, getMode(stack).toInt() == 1, ProjectEConfig.difficulty.katarDeathAura, 0, hand)
            PlayerHelper.resetCooldown(player)
            true
        } else {
            false
        }
    }

    override fun getItemUseAction(par1ItemStack: ItemStack?): EnumAction {
        return EnumAction.BLOCK
    }

    override fun getMaxItemUseDuration(par1ItemStack: ItemStack?): Int {
        return 72000
    }


    override fun canHarvestBlock(blockIn: IBlockState): Boolean {
        return harvestSet.contains(blockIn.material)
    }

    override fun getHarvestLevel(stack: ItemStack, toolClass: String, player: EntityPlayer?, blockState: IBlockState?): Int {
        return if (getToolClasses(stack).contains(toolClass)) 4 else -1
    }

    override fun getAttributeModifiers(slot: EntityEquipmentSlot, stack: ItemStack): Multimap<String, AttributeModifier> {
        if (slot != EntityEquipmentSlot.MAINHAND) {
            return super.getAttributeModifiers(slot, stack)
        }

        val multimap = super.getAttributeModifiers(slot, stack)
        multimap.put(SharedMonsterAttributes.ATTACK_SPEED.name, AttributeModifier(Item.ATTACK_SPEED_MODIFIER, "Tool modifier", -2.4, 0))
        return multimap
    }

    companion object {
        val harvestSet: LinkedHashSet<Material> = linkedSetOf(Material.WOOD, Material.WEB, Material.CLOTH, Material.PLANTS, Material.LEAVES, Material.VINE)
    }

}
