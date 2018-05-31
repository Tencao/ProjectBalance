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

import com.tencao.projectbalance.config.ProjectBConfig
import moze_intel.projecte.config.ProjectEConfig
import moze_intel.projecte.gameObjs.items.IFireProtector
import moze_intel.projecte.handlers.InternalTimers
import moze_intel.projecte.utils.WorldHelper
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class GemChest : GemArmorBase(EntityEquipmentSlot.CHEST), IFireProtector {

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, world: World?, tooltips: MutableList<String>?, flags: ITooltipFlag?) {
        tooltips?.add(I18n.format("pe.gem.chest.lorename"))
    }

    override fun onArmorTick(world: World, player: EntityPlayer, chest: ItemStack) {
        if (world.isRemote) {
            val x = Math.floor(player.posX).toInt()
            val y = (player.posY - player.yOffset).toInt()
            val z = Math.floor(player.posZ).toInt()
            val pos = BlockPos(x, y, z)

            val b = world.getBlockState(pos.down()).block

            if ((b === Blocks.LAVA || b === Blocks.FLOWING_LAVA) && world.isAirBlock(pos)) {
                if (!player.isSneaking) {
                    player.motionY = 0.0
                    player.fallDistance = 0.0f
                    player.onGround = true
                }
            }
        } else {
            player.getCapability(InternalTimers.CAPABILITY, null)!!.activateFeed()

            if (player.foodStats.needFood() && player.getCapability(InternalTimers.CAPABILITY, null)!!.canFeed() && getStoredEmc(chest) >= ProjectBConfig.tweaks.BMFoodAbility) {
                removeEmc(chest, ProjectBConfig.tweaks.BMFoodAbility.toDouble())
                player.foodStats.addStats(2, 10f)
            }
        }
    }

    fun doExplode(player: EntityPlayer, chest: ItemStack) {
        if (ProjectEConfig.difficulty.offensiveAbilities && getStoredEmc(chest) >= ProjectBConfig.tweaks.BMExplosionAbility) {
            removeEmc(chest, ProjectBConfig.tweaks.BMExplosionAbility.toDouble())
            WorldHelper.createNovaExplosion(player.entityWorld, player, player.posX, player.posY, player.posZ, 9.0f)
        }
    }

    override fun canProtectAgainstFire(stack: ItemStack, player: EntityPlayerMP): Boolean {
        return player.getItemStackFromSlot(EntityEquipmentSlot.CHEST) == stack
    }
}