package com.tencao.projectbalance.gameObjs.items.rings

import com.tencao.projectbalance.gameObjs.tile.DMPedestalTile
import moze_intel.projecte.config.ProjectEConfig
import moze_intel.projecte.gameObjs.items.rings.Ignition
import net.minecraft.entity.EntityLiving
import net.minecraft.util.DamageSource
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class Ignition: Ignition() {

    override fun updateInPedestal(world: World, pos: BlockPos) {
        if (!world.isRemote && ProjectEConfig.pedestalCooldown.ignitePedCooldown != -1) {
            val te = world.getTileEntity(pos) as? DMPedestalTile ?: return
            val list = world.getEntitiesWithinAABB(EntityLiving::class.java, te.getEffectBounds())
            for (living in list) {
                living.attackEntityFrom(DamageSource.IN_FIRE, 3.0f)
                living.setFire(8)
            }

            te.setActivityCooldown(ProjectEConfig.pedestalCooldown.ignitePedCooldown)
        }

    }
}