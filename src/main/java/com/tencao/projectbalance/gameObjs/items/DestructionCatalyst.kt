package com.tencao.projectbalance.gameObjs.items

import com.tencao.projectbalance.config.ProjectBConfig
import moze_intel.projecte.api.PESounds
import moze_intel.projecte.gameObjs.items.DestructionCatalyst
import moze_intel.projecte.utils.PlayerHelper
import moze_intel.projecte.utils.WorldHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import java.util.*

class DestructionCatalyst: DestructionCatalyst() {

    override fun onItemUse(player: EntityPlayer, world: World, coords: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        if (world.isRemote) return EnumActionResult.SUCCESS

        val stack = player.getHeldItem(hand)
        var numRows = calculateDepthFromCharge(stack)
        var hasAction = false

        val box = WorldHelper.getDeepBox(coords, facing, --numRows)

        val drops = ArrayList<ItemStack>()


        if (!consumeFuel(player, stack, ProjectBConfig.tweaks.DestroEmc.toDouble(), false)) {
            player.sendMessage(TextComponentTranslation("pe.noemc"))
        }

        for (pos in WorldHelper.getPositionsFromBox(box)) {
            val state = world.getBlockState(pos)
            val hardness = state.getBlockHardness(world, pos)

            if (world.isAirBlock(pos) || hardness >= 50.0f || hardness == -1.0f) {
                continue
            }

            if (!consumeFuel(player, stack, ProjectBConfig.tweaks.DestroEmc.toDouble(), true)) {
                player.sendMessage(TextComponentTranslation("pe.noemc"))
                break
            }

            hasAction = true

            if (PlayerHelper.hasBreakPermission(player as EntityPlayerMP, pos)) {
                val list = WorldHelper.getBlockDrops(world, player, world.getBlockState(pos), stack, pos)
                if (list != null && list.size > 0) {
                    drops.addAll(list)
                }

                world.setBlockToAir(pos)

                if (world.rand.nextInt(8) == 0) {
                    (world as WorldServer).spawnParticle(if (world.rand.nextBoolean()) EnumParticleTypes.EXPLOSION_NORMAL else EnumParticleTypes.SMOKE_LARGE, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), 2, 0.0, 0.0, 0.0, 0.05)
                }
            }
        }

        PlayerHelper.swingItem(player, hand)
        if (hasAction) {
            WorldHelper.createLootDrop(drops, world, coords)
            world.playSound(null, player.posX, player.posY, player.posZ, PESounds.DESTRUCT, SoundCategory.PLAYERS, 1.0f, 1.0f)
        }

        return EnumActionResult.SUCCESS
    }

    private fun calculateDepthFromCharge(stack: ItemStack): Int {
        val charge = getCharge(stack)
        if (charge <= 0) {
            return 1
        }
        return Math.pow(2.0, (1 + charge).toDouble()).toInt()
    }
}