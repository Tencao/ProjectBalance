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