package com.tencao.projectbalance.gameObjs.items

import com.tencao.projectbalance.config.ProjectBConfig
import com.tencao.projectbalance.events.PedestalEvent
import com.tencao.projectbalance.gameObjs.tile.DMPedestalTile
import com.tencao.projectbalance.gameObjs.tile.IEmcGen
import moze_intel.projecte.config.ProjectEConfig
import moze_intel.projecte.gameObjs.items.TimeWatch
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class TimeWatch: TimeWatch() {

    override fun updateInPedestal(world: World, pos: BlockPos) {
        // Change from old EE2 behaviour (universally increased tickrate) for safety and impl reasons.

        if (!world.isRemote && ProjectEConfig.items.enableTimeWatch) {
            val te = world.getTileEntity(pos)
            if (te is DMPedestalTile) {
                PedestalEvent.getTileEntities(world, te).forEach { it ->
                    if (it is IEmcGen)
                        if (te.hasRequiredEMC(ProjectBConfig.tweaks.TimeWatchBoostPedestalCost.toDouble(), false))
                            (it as IEmcGen).updateEmc(true)
                }

            }
        }
    }
}