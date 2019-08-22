package com.tencao.projectbalance.gameObjs.items.tools

import moze_intel.projecte.api.PESounds
import moze_intel.projecte.config.ProjectEConfig
import moze_intel.projecte.utils.ItemHelper
import moze_intel.projecte.utils.MathUtils
import moze_intel.projecte.utils.PlayerHelper
import moze_intel.projecte.utils.WorldHelper
import net.minecraft.block.Block
import net.minecraft.block.BlockRedstoneOre
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.*
import net.minecraft.entity.monster.IMob
import net.minecraft.entity.passive.EntitySheep
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.init.Enchantments
import net.minecraft.init.Items
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.stats.StatList
import net.minecraft.util.*
import net.minecraft.util.math.*
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.common.IShearable
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.EnumHelper
import net.minecraftforge.event.entity.player.UseHoeEvent
import net.minecraftforge.oredict.OreDictionary
import java.util.*
import kotlin.math.pow

object PEToolBase {

    val darkMatter: Item.ToolMaterial = EnumHelper.addToolMaterial("dm", 5, 0, 12F, 9F, 30)!!
    val redMatter: Item.ToolMaterial = EnumHelper.addToolMaterial("rm", 8, 0, 15F, 13F, 50)!!

    /*
    open fun getStrVsBlock(stack: ItemStack, state: IBlockState): Float {
        if ("dm_tools" == this.peToolMaterial) {
            if (canHarvestBlock(state, stack)) {
                return 14.0f + 12.0f * this.getCharge(stack)
            }
        } else if ("rm_tools" == this.peToolMaterial) {
            if (canHarvestBlock(state, stack)) {
                return 16.0f + 14.0f * this.getCharge(stack)
            }
        }
        return 1.0f
    }*/

    /**
     * Clears the given OD name in an AOE. Charge affects the AOE. Optional per-block EMC cost.
     */
    fun clearOdAOE(world: World, stack: ItemStack, player: EntityPlayer, odName: String, emcCost: Int, hand: EnumHand) {
        if (stack.item !is IItemMode) return
        val tool = stack.item as IItemMode
        val charge = tool.getCharge(stack)
        if (charge == 0 || world.isRemote || ProjectEConfig.items.disableAllRadiusMining) {
            return
        }

        val drops = ArrayList<ItemStack>()

        val scaled1 = 5 * charge
        val scaled2 = 10 * charge

        if (player.foodStats.foodLevel > 0) {
            for (pos in BlockPos.getAllInBox(BlockPos(player).add(-scaled1, -scaled2, -scaled1), BlockPos(player).add(scaled1, scaled2, scaled1))) {
                val state = world.getBlockState(pos)
                val block = state.block

                if (block.isAir(state, world, pos) || Item.getItemFromBlock(block) == Items.AIR) {
                    continue
                }

                val s = ItemStack(block)
                val oreIds = OreDictionary.getOreIDs(s)

                val oreName: String
                oreName = if (oreIds.isEmpty()) {
                    if (block === Blocks.BROWN_MUSHROOM_BLOCK || block === Blocks.RED_MUSHROOM_BLOCK) {
                        "logWood"
                    } else {
                        continue
                    }
                } else {
                    OreDictionary.getOreName(oreIds[0])
                }

                if (odName == oreName) {
                    val blockDrops = WorldHelper.getBlockDrops(world, player, state, stack, pos)

                    if (PlayerHelper.hasBreakPermission(player as EntityPlayerMP, pos)) {
                        player.foodStats.addExhaustion(0.10F)
                        drops.addAll(blockDrops)
                        world.setBlockToAir(pos)
                        if (world.rand.nextInt(5) == 0) {
                            (world as WorldServer).spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), 2, 0.0, 0.0, 0.0, 0.0, *IntArray(0))
                        }
                    }
                }
            }
            WorldHelper.createLootDrop(drops, world, player.posX, player.posY, player.posZ)
            PlayerHelper.swingItem(player, hand)
        }

    }

    /**
     * Tills in an AOE. Charge affects the AOE. Optional per-block EMC cost.
     */
    fun tillAOE(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, sidehit: EnumFacing, emcCost: Int) {
        if (stack.item !is IItemMode) return
        val tool = stack.item as IItemMode
        val charge = tool.getCharge(stack)
        var hasAction = false
        var hasSoundPlayed = false

        if (player.foodStats.foodLevel > 0) {
            for (newPos in BlockPos.getAllInBox(pos.add(-charge, 0, -charge), pos.add(charge, 0, charge))) {
                val state = world.getBlockState(newPos)
                val stateAbove = world.getBlockState(newPos.up())
                val block = state.block
                val blockAbove = stateAbove.block

                if (!stateAbove.isOpaqueCube && (block === Blocks.GRASS || block === Blocks.DIRT)) {
                    if (!hasSoundPlayed) {
                        world.playSound(null, newPos, Blocks.FARMLAND.soundType.stepSound, SoundCategory.BLOCKS, (Blocks.FARMLAND.soundType.getVolume() + 1.0f) / 2.0f, Blocks.FARMLAND.soundType.getPitch() * 0.8f)
                        hasSoundPlayed = true
                    }

                    if (world.isRemote) {
                        return
                    } else {
                        if (MinecraftForge.EVENT_BUS.post(UseHoeEvent(player, stack, world, newPos))) {
                            continue
                        }

                        // The initial block we target is always free
                        if (newPos.x == pos.x && newPos.z == pos.z) {
                            PlayerHelper.checkedReplaceBlock(player as EntityPlayerMP, newPos, Blocks.FARMLAND.defaultState)

                            if ((stateAbove.material === Material.PLANTS || stateAbove.material === Material.VINE) && !blockAbove.hasTileEntity(stateAbove) // Just in case, you never know
                            ) {
                                if (PlayerHelper.hasBreakPermission(player, newPos) && player.foodStats.foodLevel > 0) {
                                    player.foodStats.addExhaustion(0.10F)
                                    world.destroyBlock(newPos.up(), true)
                                }
                            }

                            if (!hasAction) {
                                hasAction = true
                            }
                        }
                    }
                }
            }
            if (hasAction) {
                player.entityWorld.playSound(null, player.posX, player.posY, player.posZ, PESounds.CHARGE, SoundCategory.PLAYERS, 1.0f, 1.0f)
            }
        }
    }

    /**
     * Called by multiple tools' left click function. Charge has no effect. Free operation.
     */
    fun digBasedOnMode(stack: ItemStack, world: World, block: Block, pos: BlockPos, living: EntityLivingBase) {
        if (world.isRemote || living !is EntityPlayer || stack.item !is IItemMode) {
            return
        }
        val tool = stack.item as IItemMode
        val mode = tool.getMode(stack)

        if (mode.toInt() == 0)
        // Standard
        {
            return
        }

        val mop = rayTrace(world, living, false)
        if (mop == null || mop.typeOfHit != RayTraceResult.Type.BLOCK || living.foodStats.foodLevel <= 0) {
            return
        }

        val direction = mop.sideHit
        val hitPos = mop.blockPos
        var box = AxisAlignedBB(hitPos, hitPos)

        if (!ProjectEConfig.items.disableAllRadiusMining) {
            when (mode) {
                1.toByte() // 3x Tallshot
                -> box = AxisAlignedBB(hitPos.offset(EnumFacing.DOWN, 1), hitPos.offset(EnumFacing.UP, 1))
                2.toByte() // 3x Wideshot
                -> when (direction.axis) {
                    EnumFacing.Axis.X -> box = AxisAlignedBB(hitPos.offset(EnumFacing.SOUTH), hitPos.offset(EnumFacing.NORTH))
                    EnumFacing.Axis.Y -> when (living.horizontalFacing.axis) {
                        EnumFacing.Axis.X -> box = AxisAlignedBB(hitPos.offset(EnumFacing.SOUTH), hitPos.offset(EnumFacing.NORTH))
                        EnumFacing.Axis.Z -> box = AxisAlignedBB(hitPos.offset(EnumFacing.WEST), hitPos.offset(EnumFacing.EAST))
                    }
                    EnumFacing.Axis.Z -> box = AxisAlignedBB(hitPos.offset(EnumFacing.WEST), hitPos.offset(EnumFacing.EAST))
                }
                3.toByte() // 3x Longshot
                -> box = AxisAlignedBB(hitPos, hitPos.offset(direction.opposite, 2))
            }

        }

        val drops = ArrayList<ItemStack>()

        for (digPos in WorldHelper.getPositionsFromBox(box)) {
            val state = world.getBlockState(digPos)
            val b = state.block

            if (b !== Blocks.AIR
                    && state.getBlockHardness(world, digPos) != -1f
                    && (stack.canHarvestBlock(state) || ForgeHooks.canToolHarvestBlock(world, digPos, stack))
                    && PlayerHelper.hasBreakPermission(living as EntityPlayerMP, digPos)
                    && living.foodStats.foodLevel > 0) {
                living.foodStats.addExhaustion(0.10F)
                drops.addAll(WorldHelper.getBlockDrops(world, living, state, stack, digPos))
                world.setBlockToAir(digPos)
            }
        }

        WorldHelper.createLootDrop(drops, world, pos)
    }

    /**
     * Carves in an AOE. Charge affects the breadth and/or depth of the AOE. Optional per-block EMC cost.
     */
    fun digAOE(stack: ItemStack, world: World, player: EntityPlayer, affectDepth: Boolean, emcCost: Int, hand: EnumHand) {
        if (world.isRemote || stack.item !is IItemMode || ProjectEConfig.items.disableAllRadiusMining) {
            return
        }
        val tool = stack.item as IItemMode
        if (tool.getCharge(stack) == 0 || player.foodStats.foodLevel <= 0) return

        val mop = this.rayTrace(world, player, false)

        if (mop == null || mop.typeOfHit != RayTraceResult.Type.BLOCK) {
            return
        }

        val box = if (affectDepth)
            WorldHelper.getBroadDeepBox(mop.blockPos, mop.sideHit, tool.getCharge(stack))
        else
            WorldHelper.getFlatYBox(mop.blockPos, tool.getCharge(stack))

        val drops = ArrayList<ItemStack>()

        for (pos in WorldHelper.getPositionsFromBox(box)) {
            val state = world.getBlockState(pos)
            val b = state.block

            if (b !== Blocks.AIR && state.getBlockHardness(world, pos) != -1f
                    && stack.canHarvestBlock(state)
                    && PlayerHelper.hasBreakPermission(player as EntityPlayerMP, pos)
                    && player.foodStats.foodLevel > 0) {
                player.foodStats.addExhaustion(0.10F)
                drops.addAll(WorldHelper.getBlockDrops(world, player, state, stack, pos))
                world.setBlockToAir(pos)
            }
        }

        WorldHelper.createLootDrop(drops, world, mop.blockPos)
        PlayerHelper.swingItem(player, hand)

        if (drops.isNotEmpty()) {
            player.entityWorld.playSound(null, player.posX, player.posY, player.posZ, PESounds.DESTRUCT, SoundCategory.PLAYERS, 1.0f, 1.0f)
        }
    }

    /**
     * Attacks through armor. Charge affects damage. Free operation.
     */
    fun attackWithCharge(stack: ItemStack, damaged: EntityLivingBase, damager: EntityLivingBase, baseDmg: Float) {
        if (damager !is EntityPlayer || stack.item !is IItemMode || damager.getEntityWorld().isRemote || damager.foodStats.foodLevel <= 0) {
            return
        }
        val tool = stack.item as IItemMode

        val dmg = DamageSource.causePlayerDamage(damager)
        val charge = tool.getCharge(stack)
        var totalDmg = baseDmg

        if (charge > 0) {
            damager.foodStats.addExhaustion(0.2F * charge)
            dmg.setDamageBypassesArmor()
            totalDmg += charge.toFloat()
        }

        damaged.attackEntityFrom(dmg, totalDmg)
    }

    /**
     * Attacks in an AOE. Charge affects AOE, not damage (intentional). Optional per-entity EMC cost.
     */
    fun attackAOE(stack: ItemStack, player: EntityPlayer, slayAll: Boolean, damage: Float, emcCost: Int, hand: EnumHand?) {
        if (player.entityWorld.isRemote || stack.item !is IItemMode || player.foodStats.foodLevel <= 0) {
            return
        }
        val tool = stack.item as IItemMode

        val charge = tool.getCharge(stack)
        val factor = 2.5f * charge
        val aabb = player.entityBoundingBox.grow(factor.toDouble())
        val toAttack = player.entityWorld.getEntitiesWithinAABBExcludingEntity(player, aabb)
        val src = DamageSource.causePlayerDamage(player)
        src.setDamageBypassesArmor()
        for (entity in toAttack) {
            if (entity is IMob && player.foodStats.foodLevel > 0) {
                player.foodStats.addExhaustion(0.2F)
                entity.attackEntityFrom(src, damage)
            } else if (entity is EntityLivingBase && slayAll && player.foodStats.foodLevel > 0) {
                player.foodStats.addExhaustion(0.2F)
                entity.attackEntityFrom(src, damage)
            }
        }
        player.entityWorld.playSound(null, player.posX, player.posY, player.posZ, PESounds.CHARGE, SoundCategory.PLAYERS, 1.0f, 1.0f)
        PlayerHelper.swingItem(player, hand)
    }

    /**
     * Called when tools that act as shears start breaking a block. Free operation.
     */
    fun shearBlock(stack: ItemStack, pos: BlockPos, player: EntityPlayer) {
        if (player.entityWorld.isRemote || player.foodStats.foodLevel <= 0) {
            return
        }

        val block = player.entityWorld.getBlockState(pos).block

        if (block is IShearable) {
            val target = block as IShearable

            if (target.isShearable(stack, player.entityWorld, pos) && PlayerHelper.hasBreakPermission(player as EntityPlayerMP, pos) && player.foodStats.foodLevel > 0) {
                val drops = target.onSheared(stack, player.getEntityWorld(), pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack))

                WorldHelper.createLootDrop(drops, player.getEntityWorld(), pos)

                player.foodStats.addExhaustion(0.05F)
                stack.damageItem(1, player)
                player.addStat(StatList.getBlockStats(block)!!, 1)
            }
        }
    }

    /**
     * Shears entities in an AOE. Charge affects AOE. Optional per-entity EMC cost.
     */
    fun shearEntityAOE(stack: ItemStack, player: EntityPlayer, emcCost: Int, hand: EnumHand) {
        if (player.world.isRemote || stack.item !is IItemMode || player.foodStats.foodLevel <= 0) {
            return
        }
        val tool = stack.item as IItemMode
        val charge = tool.getCharge(stack)

        val offset = 2.0.pow((2 + charge).toDouble()).toInt()

        val bBox = player.entityBoundingBox.grow(offset.toDouble(), (offset / 2).toDouble(), offset.toDouble())
        val list = player.world.getEntitiesWithinAABB(Entity::class.java, bBox)

        val drops = ArrayList<ItemStack>()

        for (ent in list) {
            if (ent !is IShearable) {
                continue
            }

            val target = ent as IShearable

            if (target.isShearable(stack, ent.world, BlockPos(ent))) {
                val entDrops = target.onSheared(stack, ent.world, BlockPos(ent), EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack))

                if (entDrops.isNotEmpty() && player.foodStats.foodLevel > 0) {
                    for (drop in entDrops) {
                        drop.grow(drop.count)
                    }
                    player.foodStats.addExhaustion(0.1F)

                    drops.addAll(entDrops)
                }
            }
            if (Math.random() < 0.01) {
                val e = EntityList.createEntityByIDFromName(EntityList.getKey(ent)!!, player.world)

                if (e is EntityLiving) {
                    e.onInitialSpawn(player.world.getDifficultyForLocation(BlockPos(ent)), null)
                }

                if (e is EntitySheep) {
                    e.fleeceColor = EnumDyeColor.values()[MathUtils.randomIntInRange(0, 15)]
                }

                if (e is EntityAgeable) {
                    e.growingAge = -24000
                }
                player.world.spawnEntity(e!!)
            }
        }

        WorldHelper.createLootDrop(drops, player.world, player.posX, player.posY, player.posZ)
        PlayerHelper.swingItem(player, hand)

    }

    /**
     * Scans and harvests an ore vein. This is called already knowing the mop is pointing at an ore or gravel.
     */
    fun tryVeinMine(stack: ItemStack, player: EntityPlayer, mop: RayTraceResult) {
        if (player.world.isRemote || ProjectEConfig.items.disableAllRadiusMining || stack.item !is IItemMode || player.foodStats.foodLevel <= 0) {
            return
        }
        val tool = stack.item as IItemMode

        val aabb = WorldHelper.getBroadDeepBox(mop.blockPos, mop.sideHit, tool.getCharge(stack))
        val target = player.world.getBlockState(mop.blockPos)
        if (target.getBlockHardness(player.world, mop.blockPos) <= -1 || !(stack.canHarvestBlock(target) || ForgeHooks.canToolHarvestBlock(player.entityWorld, mop.blockPos, stack))) {
            return
        }

        val drops = ArrayList<ItemStack>()

        for (pos in WorldHelper.getPositionsFromBox(aabb)) {
            val state = player.world.getBlockState(pos)
            player.foodStats.addExhaustion(0.1F)
            if (isSameOre(target, state)) {
                val count = WorldHelper.harvestVein(player.world, player, stack, pos, state, drops, 0)
                if (count > 0)
                    for (i in 1..count)
                        player.foodStats.addExhaustion(0.1F)
            }
        }

        WorldHelper.createLootDrop(drops, player.world, mop.blockPos)
        if (drops.isNotEmpty()) {
            player.world.playSound(null, player.posX, player.posY, player.posZ, PESounds.DESTRUCT, SoundCategory.PLAYERS, 1.0f, 1.0f)
        }
    }


    /**
     * Mines all ore veins in a Box around the player.
     */
    fun mineOreVeinsInAOE(stack: ItemStack, player: EntityPlayer, hand: EnumHand) {
        if (player.world.isRemote || ProjectEConfig.items.disableAllRadiusMining || stack.item !is IItemMode || player.foodStats.foodLevel <= 0) {
            return
        }
        val tool = stack.item as IItemMode
        val offset = tool.getCharge(stack) + 3
        val box = player.entityBoundingBox.grow(offset.toDouble())
        val drops = ArrayList<ItemStack>()
        val world = player.entityWorld

        for (pos in WorldHelper.getPositionsFromBox(box)) {
            val state = world.getBlockState(pos)
            if (ItemHelper.isOre(state) && state.getBlockHardness(player.entityWorld, pos) != -1f && (stack.canHarvestBlock(state) || ForgeHooks.canToolHarvestBlock(world, pos, stack))) {
                val count = WorldHelper.harvestVein(world, player, stack, pos, state, drops, 0)
                if (count > 0)
                    for (i in 1..count)
                        player.foodStats.addExhaustion(0.1F)
            }
        }

        if (drops.isNotEmpty()) {
            WorldHelper.createLootDrop(drops, world, player.posX, player.posY, player.posZ)
            PlayerHelper.swingItem(player, hand)
        }
    }

    private fun rayTrace(worldIn: World, playerIn: EntityPlayer, useLiquids: Boolean): RayTraceResult? {
        val f = playerIn.rotationPitch
        val f1 = playerIn.rotationYaw
        val d0 = playerIn.posX
        val d1 = playerIn.posY + playerIn.getEyeHeight().toDouble()
        val d2 = playerIn.posZ
        val vec3d = Vec3d(d0, d1, d2)
        val f2 = MathHelper.cos(-f1 * 0.017453292f - Math.PI.toFloat())
        val f3 = MathHelper.sin(-f1 * 0.017453292f - Math.PI.toFloat())
        val f4 = -MathHelper.cos(-f * 0.017453292f)
        val f5 = MathHelper.sin(-f * 0.017453292f)
        val f6 = f3 * f4
        val f7 = f2 * f4
        val d3 = playerIn.getEntityAttribute(EntityPlayer.REACH_DISTANCE).attributeValue
        val vec3d1 = vec3d.add(f6.toDouble() * d3, f5.toDouble() * d3, f7.toDouble() * d3)
        return worldIn.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false)
    }

    private fun isSameOre(target: IBlockState, world: IBlockState): Boolean {
        return if (target.block is BlockRedstoneOre) {
            world.block is BlockRedstoneOre
        } else target === world
    }

}
