package com.tencao.projectbalance.gameObjs.items.tools

import com.google.common.collect.Multimap
import com.tencao.projectbalance.config.ProjectBConfig
import moze_intel.projecte.config.ProjectEConfig
import moze_intel.projecte.gameObjs.ObjHandler
import moze_intel.projecte.utils.ItemHelper
import net.minecraft.block.*
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemTool
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult.Type
import net.minecraft.world.World

class RedStar : ItemTool, IItemMode {

    override val numCharges: Int
    override val numModes: Byte
    override val modes: Array<String>
    val damage: Float

    constructor() : this("rm_morning_star", PEToolBase.redMatter, 23F, 4, arrayOf("pe.morningstar.mode1", "pe.morningstar.mode2", "pe.morningstar.mode3", "pe.morningstar.mode4"))

    constructor(name: String, material: ToolMaterial, damage: Float, numCharges: Int, modeDesc: Array<String>): super(damage, -3.0F, material, mutableSetOf()){
        this.numCharges = numCharges
        this.modes = modeDesc
        this.numModes = modeDesc.size.toByte()
        this.setNoRepair()
        this.translationKey = "pe_$name"
        this.maxDamage = 0
        this.creativeTab = ObjHandler.cTab
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

    override fun getToolClasses(stack: ItemStack): MutableSet<String> {
        return mutableSetOf("morning_star", "pickaxe", "chisel", "shovel", "axe")
    }

    override fun isEnchantable(stack: ItemStack): Boolean {
        return ProjectBConfig.tweaks.allowItemEnchants
    }

    override fun hitEntity(stack: ItemStack, damaged: EntityLivingBase, damager: EntityLivingBase): Boolean {
        PEToolBase.attackWithCharge(stack, damaged, damager, 1.0f)
        return true
    }

    override fun onBlockDestroyed(stack: ItemStack, world: World, state: IBlockState?, pos: BlockPos, eLiving: EntityLivingBase): Boolean {
        PEToolBase.digBasedOnMode(stack, world, state!!.block, pos, eLiving)
        return true
    }

    override fun onItemRightClick(world: World?, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {
        val stack = player.getHeldItem(hand)
        if (!world!!.isRemote) {
            if (ProjectEConfig.items.pickaxeAoeVeinMining) {
                PEToolBase.mineOreVeinsInAOE(stack, player, hand)
            }

            val mop = this.rayTrace(world, player, true)

            if (mop == null) {
                return ActionResult.newResult(EnumActionResult.FAIL, stack)
            } else if (mop.typeOfHit == Type.BLOCK) {
                val state = world.getBlockState(mop.blockPos)
                val block = state.block

                if (block is BlockGravel || block is BlockClay) {
                    if (ProjectEConfig.items.pickaxeAoeVeinMining) {
                        PEToolBase.digAOE(stack, world, player, false, 0, hand)
                    } else {
                        PEToolBase.tryVeinMine(stack, player, mop)
                    }
                } else if (ItemHelper.isOre(state)) {
                    if (!ProjectEConfig.items.pickaxeAoeVeinMining) {
                        PEToolBase.tryVeinMine(stack, player, mop)
                    }
                } else if (block is BlockGrass || block is BlockDirt || block is BlockSand) {
                    PEToolBase.digAOE(stack, world, player, false, 0, hand)
                } else {
                    PEToolBase.digAOE(stack, world, player, true, 0, hand)
                }
            }
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, stack)
    }

    override fun canHarvestBlock(blockIn: IBlockState): Boolean {
        return harvestSet.contains(blockIn.material)
    }

    override fun getHarvestLevel(stack: ItemStack, toolClass: String, player: EntityPlayer?, blockState: IBlockState?): Int {
        return if (getToolClasses(stack).contains(toolClass)) 4 else -1
    }

    override fun getDestroySpeed(stack: ItemStack, state: IBlockState): Float {
        val block = state.block
        return if (block === ObjHandler.matterBlock || block === ObjHandler.dmFurnaceOff || block === ObjHandler.dmFurnaceOn || block === ObjHandler.rmFurnaceOff || block === ObjHandler.rmFurnaceOn) {
            1200000.0f
        } else super.getDestroySpeed(stack, state) + 48.0f
    }

    override fun getAttributeModifiers(slot: EntityEquipmentSlot, stack: ItemStack): Multimap<String, AttributeModifier> {
        if (slot != EntityEquipmentSlot.MAINHAND) {
            return super.getAttributeModifiers(slot, stack)
        }

        val multimap = super.getAttributeModifiers(slot, stack)
        multimap.put(SharedMonsterAttributes.ATTACK_SPEED.name, AttributeModifier(Item.ATTACK_SPEED_MODIFIER, "Tool modifier", -3.0, 0))
        return multimap
    }

    companion object {
        val harvestSet: LinkedHashSet<Material> = linkedSetOf(Material.GRASS, Material.GROUND, Material.SAND, Material.SNOW, Material.CLAY, Material.IRON,
                Material.ANVIL, Material.ROCK, Material.WOOD, Material.PLANTS, Material.VINE)
    }
}
