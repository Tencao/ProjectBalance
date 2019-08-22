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

import com.tencao.projectbalance.config.ProjectBConfig
import com.tencao.projectbalance.gameObjs.tile.DMPedestalTile
import moze_intel.projecte.api.PESounds
import moze_intel.projecte.config.ProjectEConfig
import moze_intel.projecte.gameObjs.items.ItemPE
import moze_intel.projecte.gameObjs.items.VolcaniteAmulet
import moze_intel.projecte.utils.FluidHelper
import moze_intel.projecte.utils.PlayerHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.init.MobEffects
import net.minecraft.item.ItemStack
import net.minecraft.potion.PotionEffect
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.capability.CapabilityFluidHandler

class VolcaniteAmulet: VolcaniteAmulet() {

    override fun onItemUse(player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, sideHit: EnumFacing, f1: Float, f2: Float, f3: Float): EnumActionResult {
        if (!world.isRemote
                && PlayerHelper.hasEditPermission(player as EntityPlayerMP, pos)
                && ItemPE.consumeFuel(player, player.getHeldItem(hand), ProjectBConfig.tweaks.VolcaniteEmc.toLong(), true)) {
            val tile = world.getTileEntity(pos)

            if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, sideHit)) {
                FluidHelper.tryFillTank(tile, FluidRegistry.LAVA, sideHit, Fluid.BUCKET_VOLUME)
            } else {
                placeLava(player, pos.offset(sideHit), hand)
                world.playSound(null, player.posX, player.posY, player.posZ, PESounds.TRANSMUTE, SoundCategory.PLAYERS, 1.0f, 1.0f)
            }
        }

        return EnumActionResult.SUCCESS
    }

    private fun placeLava(player: EntityPlayer, pos: BlockPos, hand: EnumHand) {
        PlayerHelper.checkedPlaceBlock(player as EntityPlayerMP, pos, Blocks.FLOWING_LAVA.defaultState, hand)
    }

    override fun shootProjectile(player: EntityPlayer, stack: ItemStack, hand: EnumHand?): Boolean {
        if (ItemPE.consumeFuel(player, stack, ProjectBConfig.tweaks.VolcaniteEmc.toLong(), true)) {
            return super.shootProjectile(player, stack, hand)
        }
        return false
    }

    override fun updateInPedestal(world: World, pos: BlockPos) {
        if (!world.isRemote && ProjectEConfig.pedestalCooldown.volcanitePedCooldown != -1) {
            val te = world.getTileEntity(pos) as? DMPedestalTile ?: return

            if (te.hasRequiredEMC(ProjectBConfig.tweaks.VolcaniteAmuletPedestalCost.toLong(), true)) {
                for (player in world.getEntitiesWithinAABB(EntityPlayerMP::class.java, te.getEffectBounds())) {
                    if (te.hasRequiredEMC(ProjectBConfig.tweaks.VolcaniteAmuletPedestalCost.toLong(), false)) {
                        player.addPotionEffect(PotionEffect(MobEffects.WATER_BREATHING, 600))
                        player.addPotionEffect(PotionEffect(MobEffects.FIRE_RESISTANCE, 600))
                        player.addPotionEffect(PotionEffect(MobEffects.HASTE, 600))
                    }
                }
                te.setActivityCooldown(ProjectEConfig.pedestalCooldown.volcanitePedCooldown)
            }

        }
    }
}