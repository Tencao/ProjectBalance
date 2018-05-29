package com.tencao.projectbalance.gameObjs

import com.tencao.projectbalance.config.ProjectBConfig
import com.tencao.projectbalance.gameObjs.tile.DMPedestalTile
import moze_intel.projecte.config.ProjectEConfig
import moze_intel.projecte.gameObjs.items.EvertideAmulet
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.MobEffects
import net.minecraft.potion.PotionEffect
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class EvertideAmulet: EvertideAmulet() {

    override fun updateInPedestal(world: World, pos: BlockPos) {
        if (!world.isRemote && ProjectEConfig.pedestalCooldown.evertidePedCooldown != -1) {
            val te = world.getTileEntity(pos) as? DMPedestalTile ?: return

            if (te.hasRequiredEMC(ProjectBConfig.tweaks.EvertideAmuletPedestalCost.toDouble(), true)) {
                for (player in world.getEntitiesWithinAABB(EntityPlayerMP::class.java, te.getEffectBounds())) {
                    if (te.hasRequiredEMC(ProjectBConfig.tweaks.EvertideAmuletPedestalCost.toDouble(), false)) {
                        player.addPotionEffect(PotionEffect(MobEffects.WATER_BREATHING, 600))
                        player.addPotionEffect(PotionEffect(MobEffects.NIGHT_VISION, 600))
                        player.addPotionEffect(PotionEffect(MobEffects.HASTE, 600))
                    }
                }
                te.setActivityCooldown(ProjectEConfig.pedestalCooldown.evertidePedCooldown)
            }

        }
    }
}