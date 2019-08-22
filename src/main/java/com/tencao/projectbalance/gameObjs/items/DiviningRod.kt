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

package com.tencao.projectbalance.gameObjs.items

import com.tencao.projectbalance.utils.ItemHelper
import moze_intel.projecte.gameObjs.ObjHandler
import moze_intel.projecte.gameObjs.items.DiviningRod
import moze_intel.projecte.utils.EMCHelper
import moze_intel.projecte.utils.PlayerHelper
import moze_intel.projecte.utils.WorldHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import java.util.*

class DiviningRod(val modes: Array<String>): DiviningRod(modes) {

    override fun onItemUse(player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        if (world.isRemote) {
            return EnumActionResult.SUCCESS
        }

        PlayerHelper.swingItem(player, hand)
        val emcValues = ArrayList<Long>()
        var totalEmc: Long = 0
        var numBlocks = 0

        val mode = getMode(player.getHeldItem(hand))
        val depth = getDepthFromMode(mode)
        val box = WorldHelper.getDeepBox(pos, facing, depth)

        when(mode.toInt()){
            0 -> {
                if (!ItemHelper.decreaseStack(ItemStack(ObjHandler.covalence, 1, 0), player.inventory, 1) && !consumeFuel(player, player.getHeldItem(hand), EMCHelper.getEmcValue(ItemStack(ObjHandler.covalence, 1, 0)), true)) {
                    player.sendMessage(TextComponentTranslation("pe.divining.noemc"))
                    return EnumActionResult.SUCCESS
                }
            }
            1 -> {
                if (!ItemHelper.decreaseStack(ItemStack(ObjHandler.covalence, 1, 1), player.inventory, 1) && !consumeFuel(player, player.getHeldItem(hand), EMCHelper.getEmcValue(ItemStack(ObjHandler.covalence, 1, 1)), true)) {
                    player.sendMessage(TextComponentTranslation("pe.divining.noemc"))
                    return EnumActionResult.SUCCESS
                }
            }
            2 -> {
                if (!ItemHelper.decreaseStack(ItemStack(ObjHandler.covalence, 1, 2), player.inventory, 1) && !consumeFuel(player, player.getHeldItem(hand), EMCHelper.getEmcValue(ItemStack(ObjHandler.covalence, 1, 2)), true)) {
                    player.sendMessage(TextComponentTranslation("pe.divining.noemc"))
                    return EnumActionResult.SUCCESS
                }
            }
            else -> return EnumActionResult.SUCCESS
        }

        for (digPos in WorldHelper.getPositionsFromBox(box)) {
            val state = world.getBlockState(digPos)
            val block = state.block

            if (world.isAirBlock(digPos)) {
                continue
            }

            val drops = block.getDrops(world, digPos, state, 0)

            if (drops.size == 0) {
                continue
            }

            val blockStack = drops[0]
            val blockEmc = EMCHelper.getEmcValue(blockStack)

            if (blockEmc == 0L) {
                val map = FurnaceRecipes.instance().smeltingList

                for (entry in map.entries) {
                    if (entry.key.isEmpty) {
                        continue
                    }

                    if (ItemStack.areItemsEqual(entry.key, blockStack)) {
                        val currentValue = EMCHelper.getEmcValue(entry.value)

                        if (currentValue != 0L) {
                            if (!emcValues.contains(currentValue)) {
                                emcValues.add(currentValue)
                            }

                            totalEmc += currentValue
                        }
                    }
                }
            } else {
                if (!emcValues.contains(blockEmc)) {
                    emcValues.add(blockEmc)
                }

                totalEmc += blockEmc
            }

            numBlocks++
        }

        if (numBlocks == 0) {
            return EnumActionResult.FAIL
        }

        player.sendMessage(TextComponentTranslation("pe.divining.results", modes[mode.toInt()]))
        player.sendMessage(TextComponentTranslation("pe.divining.avgemc",  totalEmc / numBlocks))
        player.sendMessage(TextComponentTranslation("pe.divining.totalemc",  totalEmc))

        return EnumActionResult.SUCCESS
    }

    private fun getDepthFromMode(mode: Byte): Int {
        val modeDesc = this.modes[mode.toInt()]
        return Integer.parseInt(modeDesc.substring(0, modeDesc.indexOf(120.toChar(), 0))) - 1
    }
}