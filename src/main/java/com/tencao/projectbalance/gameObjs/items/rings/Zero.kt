package com.tencao.projectbalance.gameObjs.items.rings

import com.tencao.projectbalance.gameObjs.tile.DMPedestalTile
import moze_intel.projecte.config.ProjectEConfig
import moze_intel.projecte.gameObjs.items.rings.Zero
import moze_intel.projecte.utils.WorldHelper
import net.minecraft.entity.Entity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class Zero: Zero() {

    override fun updateInPedestal(world: World, pos: BlockPos) {
        if (!world.isRemote && ProjectEConfig.pedestalCooldown.zeroPedCooldown != -1) {
            val te = world.getTileEntity(pos) as? DMPedestalTile ?: return
            val aabb = te.getEffectBounds()
            WorldHelper.freezeInBoundingBox(world, aabb, null, false)
            val list = world.getEntitiesWithinAABB(Entity::class.java, aabb)
            for (ent in list) {
                if (ent.isBurning) {
                    ent.extinguish()
                }
            }
            te.setActivityCooldown(ProjectEConfig.pedestalCooldown.zeroPedCooldown)
        }
    }
}