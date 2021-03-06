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

package com.tencao.projectbalance.utils

import moze_intel.projecte.config.ProjectEConfig
import moze_intel.projecte.utils.PlayerHelper
import net.minecraft.block.BlockFlower
import net.minecraft.block.BlockNetherWart
import net.minecraft.block.IGrowable
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.IPlantable
import net.minecraftforge.common.IShearable
import java.util.*

object WorldHelper {

    fun getNearbyGrowth(harvest: Boolean, world: World, pos: BlockPos, player: EntityPlayer?): Int {
        return getNearbyGrowth(harvest, world, BlockPos.getAllInBox(pos.add(-5, -3, -5), pos.add(5, 3, 5)).toSet(), player)
    }

    fun getNearbyGrowth(harvest: Boolean, world: World, pos: Set<BlockPos>, player: EntityPlayer?): Int {
        var times = 0

        val chance = if (harvest) 16 else 32

        for (currentPos in pos) {
            val state = world.getBlockState(currentPos)
            val crop = state.block

            // Vines, leaves, tallgrass, deadbush, doubleplants
            if (crop is IShearable) {
                if (harvest) {
                    times++
                }
            } else if (crop is IGrowable) {
                val growable = crop as IGrowable
                if (!growable.canGrow(world, currentPos, state, false)) {
                    if (harvest && (player == null || PlayerHelper.hasBreakPermission(player as EntityPlayerMP?, currentPos))) {
                        times++
                    }
                } else if (world.rand.nextInt(chance) == 0) {
                    if (ProjectEConfig.items.harvBandGrass || !crop.translationKey.toLowerCase(Locale.ROOT).contains("grass")) {
                        times++
                    }
                }
            } else if (crop is IPlantable) {
                if (world.rand.nextInt(chance / 4) == 0) {
                    for (i in 0 until if (harvest) 8 else 4) {
                        times++
                    }
                }

                if (harvest) {
                    if (crop is BlockFlower) {
                        if (player == null || PlayerHelper.hasBreakPermission(player as EntityPlayerMP?, currentPos)) {
                            times++
                        }
                    }
                    if (crop === Blocks.REEDS || crop === Blocks.CACTUS) {
                        var shouldHarvest = true

                        for (i in 1..2) {
                            if (world.getBlockState(currentPos.up(i)).block !== crop) {
                                shouldHarvest = false
                                break
                            }
                        }

                        if (shouldHarvest) {
                            for (i in (if (crop === Blocks.REEDS) 1 else 0)..2) {
                                if (player != null && PlayerHelper.hasBreakPermission(player as EntityPlayerMP?, currentPos.up(i))) {
                                    times++
                                } else if (player == null) {
                                    times++
                                }
                            }
                        }
                    }
                    if (crop === Blocks.NETHER_WART) {
                        val age = state.getValue(BlockNetherWart.AGE)
                        if (age == 3) {
                            if (player == null || player != null && PlayerHelper.hasBreakPermission(player as EntityPlayerMP?, currentPos)) {
                                times++
                            }
                        }
                    }
                }

            }// All modded
            // Cactus, Reeds, Netherwart, Flower
            // Carrot, cocoa, wheat, grass (creates flowers and tall grass in vicinity),
            // Mushroom, potato, sapling, stems, tallgrass
        }
        return times
    }

    fun growNearbyRandomly(harvest: Boolean, world: World, pos: BlockPos, player: EntityPlayer) {
        growNearbyRandomly(harvest, world, BlockPos.getAllInBox(pos.add(-5, -3, -5), pos.add(5, 3, 5)).toSet(), player)
    }

    fun growNearbyRandomly(harvest: Boolean, world: World, pos: Set<BlockPos>, player: EntityPlayer?) {
        val chance = if (harvest) 16 else 32

        for (currentPos in pos) {
            val state = world.getBlockState(currentPos)
            val crop = state.block

            // Vines, leaves, tallgrass, deadbush, doubleplants
            if (crop is IShearable) {
                if (harvest) {
                    world.destroyBlock(currentPos, true)
                }
            } else if (crop is IGrowable) {
                val growable = crop as IGrowable
                if (!growable.canGrow(world, currentPos, state, false)) {
                    if (harvest
                            && crop !== Blocks.MELON_STEM && crop !== Blocks.PUMPKIN_STEM
                            && (player == null || PlayerHelper.hasBreakPermission(player as EntityPlayerMP?, currentPos))) {
                        world.destroyBlock(currentPos, true)
                    }
                } else if (world.rand.nextInt(chance) == 0) {
                    if (ProjectEConfig.items.harvBandGrass || !crop.translationKey.toLowerCase(Locale.ROOT).contains("grass")) {
                        growable.grow(world, world.rand, currentPos, state)
                    }
                }
            } else if (crop is IPlantable) {
                if (world.rand.nextInt(chance / 4) == 0) {
                    for (i in 0 until if (harvest) 8 else 4) {
                        crop.updateTick(world, currentPos, state, world.rand)
                    }
                }

                if (harvest) {
                    if (crop is BlockFlower) {
                        if (player == null || PlayerHelper.hasBreakPermission(player as EntityPlayerMP?, currentPos)) {
                            world.destroyBlock(currentPos, true)
                        }
                    }
                    if (crop === Blocks.REEDS || crop === Blocks.CACTUS) {
                        var shouldHarvest = true

                        for (i in 1..2) {
                            if (world.getBlockState(currentPos.up(i)).block !== crop) {
                                shouldHarvest = false
                                break
                            }
                        }

                        if (shouldHarvest) {
                            for (i in (if (crop === Blocks.REEDS) 1 else 0)..2) {
                                if (player != null && PlayerHelper.hasBreakPermission(player as EntityPlayerMP?, currentPos.up(i))) {
                                    world.destroyBlock(currentPos.up(i), true)
                                } else if (player == null) {
                                    world.destroyBlock(currentPos.up(i), true)
                                }
                            }
                        }
                    }
                    if (crop === Blocks.NETHER_WART) {
                        val age = state.getValue(BlockNetherWart.AGE)
                        if (age == 3) {
                            if (player == null || player != null && PlayerHelper.hasBreakPermission(player as EntityPlayerMP?, currentPos)) {
                                world.destroyBlock(currentPos, true)
                            }
                        }
                    }
                }

            }// All modded
            // Cactus, Reeds, Netherwart, Flower
            // Carrot, cocoa, wheat, grass (creates flowers and tall grass in vicinity),
            // Mushroom, potato, sapling, stems, tallgrass
        }
    }

    fun spawnEntityItem(world: World, stack: ItemStack, x: Double, y: Double, z: Double) {
        val f = world.rand.nextFloat() * 0.8f + 0.1f
        val f1 = world.rand.nextFloat() * 0.8f + 0.1f
        val f2 = world.rand.nextFloat() * 0.8f + 0.1f
        val entityitem = EntityItem(world, x + f, y + f1, z + f2, stack.copy())
        entityitem.motionX = world.rand.nextGaussian() * 0.05
        entityitem.motionY = world.rand.nextGaussian() * 0.05 + 0.2
        entityitem.motionZ = world.rand.nextGaussian() * 0.05
        world.spawnEntity(entityitem)
    }
}