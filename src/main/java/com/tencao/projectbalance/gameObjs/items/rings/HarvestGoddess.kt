package com.tencao.projectbalance.gameObjs.items.rings

import com.tencao.projectbalance.config.ProjectBConfig
import com.tencao.projectbalance.events.PedestalEvent
import com.tencao.projectbalance.gameObjs.tile.DMPedestalTile
import com.tencao.projectbalance.utils.WorldHelper
import moze_intel.projecte.config.ProjectEConfig
import moze_intel.projecte.gameObjs.items.ItemPE
import moze_intel.projecte.gameObjs.items.rings.HarvestGoddess
import moze_intel.projecte.utils.ItemHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class HarvestGoddess: HarvestGoddess() {

    override fun onUpdate(stack: ItemStack, world: World, entity: Entity, par4: Int, par5: Boolean) {
        if (world.isRemote || par4 > 8 || entity !is EntityPlayer) {
            return
        }

        val cost = ProjectBConfig.tweaks.HarvestGoddessPedestalCost * WorldHelper.getNearbyGrowth(true, world, entity.position, null)

        if (ItemHelper.getOrCreateCompound(stack).getBoolean(ItemPE.TAG_ACTIVE)) {
            val storedEmc = ItemPE.getEmc(stack)

            if (storedEmc == 0.0 && !ItemPE.consumeFuel(entity, stack, cost.toDouble(), true)) {
                stack.tagCompound!!.setBoolean(ItemPE.TAG_ACTIVE, false)
            } else {
                WorldHelper.growNearbyRandomly(true, world, BlockPos(entity), entity)
                ItemPE.removeEmc(stack, 0.32)
            }
        } else if (ItemPE.consumeFuel(entity, stack, cost.toDouble(), true)) {
            WorldHelper.growNearbyRandomly(false, world, BlockPos(entity), entity)
        }
    }

    override fun updateInPedestal(world: World, pos: BlockPos) {
        if (!world.isRemote && ProjectEConfig.pedestalCooldown.harvestPedCooldown != -1) {
            val te = world.getTileEntity(pos) as? DMPedestalTile ?: return

            val cost = ProjectBConfig.tweaks.HarvestGoddessPedestalCost * WorldHelper.getNearbyGrowth(true, world, PedestalEvent.getBlockPos(te), null)

            if (te.hasRequiredEMC(cost.toDouble(), false))
                WorldHelper.growNearbyRandomly(true, world, PedestalEvent.getBlockPos(te), null)

            te.setActivityCooldown(ProjectEConfig.pedestalCooldown.harvestPedCooldown)
        }
    }
}