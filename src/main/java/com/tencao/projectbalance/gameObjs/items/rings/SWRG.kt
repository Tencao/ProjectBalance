package com.tencao.projectbalance.gameObjs.items.rings

import com.tencao.projectbalance.config.ProjectBConfig
import com.tencao.projectbalance.gameObjs.tile.DMPedestalTile
import moze_intel.projecte.config.ProjectEConfig
import moze_intel.projecte.gameObjs.items.rings.SWRG
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.entity.passive.EntityTameable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class SWRG: SWRG() {

    override fun updateInPedestal(world: World, pos: BlockPos) {
        if (!world.isRemote && ProjectEConfig.pedestalCooldown.swrgPedCooldown != -1) {
            val te = world.getTileEntity(pos) as? DMPedestalTile ?: return
            val list = world.getEntitiesWithinAABB(EntityLiving::class.java, te.getEffectBounds())
            for (living in list) {
                if (living is EntityTameable && living.isTamed) {
                    continue
                }
                if (te.hasRequiredEMC(ProjectBConfig.tweaks.SWRGPedestalCost.toDouble(), false))
                    world.addWeatherEffect(EntityLightningBolt(world, living.posX, living.posY, living.posZ, false))
            }
            te.setActivityCooldown(ProjectEConfig.pedestalCooldown.swrgPedCooldown)
        }
    }
}