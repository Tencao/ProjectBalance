package com.tencao.projectbalance.gameObjs.items.armor

import com.tencao.projectbalance.config.ProjectBConfig
import moze_intel.projecte.config.ProjectEConfig
import moze_intel.projecte.handlers.InternalTimers
import moze_intel.projecte.utils.ClientKeyHelper
import moze_intel.projecte.utils.ItemHelper
import moze_intel.projecte.utils.PEKeybind
import moze_intel.projecte.utils.PlayerHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.MobEffects
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.potion.PotionEffect
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.fml.common.Optional
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import thaumcraft.api.items.IGoggles
import thaumcraft.api.items.IRevealer

@Optional.InterfaceList(value = [(Optional.Interface(iface = "thaumcraft.api.items.IRevealer", modid = "Thaumcraft")), (Optional.Interface(iface = "thaumcraft.api.items.IGoggles", modid = "Thaumcraft"))])
class GemHelmet : GemArmorBase(EntityEquipmentSlot.HEAD), IGoggles, IRevealer {

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack?, world: World?, tooltips: MutableList<String>?, flags: ITooltipFlag?) {
        tooltips!!.add(I18n.format("pe.gem.helm.lorename"))

        tooltips.add(
                I18n.format("pe.gem.nightvision.prompt", ClientKeyHelper.getKeyName(Minecraft.getMinecraft().gameSettings.keyBindSneak), ClientKeyHelper.getKeyName(PEKeybind.ARMOR_TOGGLE)
                ))

        val e = if (isNightVisionEnabled(stack!!)) TextFormatting.GREEN else TextFormatting.RED
        val s = if (isNightVisionEnabled(stack)) "pe.gem.enabled" else "pe.gem.disabled"
        tooltips.add(I18n.format("pe.gem.nightvision_tooltip") + " "
                + e + I18n.format(s))
    }

    override fun onArmorTick(world: World, player: EntityPlayer, stack: ItemStack) {
        if (world.isRemote) {
            val x = Math.floor(player.posX).toInt()
            val y = (player.posY - player.yOffset).toInt()
            val z = Math.floor(player.posZ).toInt()
            val pos = BlockPos(x, y, z)
            val b = world.getBlockState(pos.down()).block

            if ((b === Blocks.WATER || b === Blocks.FLOWING_WATER) && world.isAirBlock(pos)) {
                if (!player.isSneaking) {
                    player.motionY = 0.0
                    player.fallDistance = 0.0f
                    player.onGround = true
                }
            }
        } else {
            player.getCapability(InternalTimers.CAPABILITY, null)!!.activateHeal()

            if (player.health < player.maxHealth && player.getCapability(InternalTimers.CAPABILITY, null)!!.canHeal() && getStoredEmc(stack) > ProjectBConfig.tweaks.BMHealAbility) {
                removeEmc(stack, ProjectBConfig.tweaks.BMHealAbility.toDouble())
                player.heal(2.0f)
            }

            if (isNightVisionEnabled(stack)) {
                player.addPotionEffect(PotionEffect(MobEffects.NIGHT_VISION, 220, 0, true, false))
            } else {
                player.removePotionEffect(MobEffects.NIGHT_VISION)
            }

            if (player.isInWater) {
                player.air = 300
            }
        }
    }

    @Optional.Method(modid = "Thaumcraft")
    override fun showIngamePopups(stack: ItemStack, player: EntityLivingBase): Boolean {
        return true
    }

    @Optional.Method(modid = "Thaumcraft")
    override fun showNodes(stack: ItemStack, player: EntityLivingBase): Boolean {
        return true
    }

    fun doZap(player: EntityPlayer, stack: ItemStack) {
        if (ProjectEConfig.difficulty.offensiveAbilities && getStoredEmc(stack) >= ProjectBConfig.tweaks.BMLightningAbility) {
            val strikePos = PlayerHelper.getBlockLookingAt(player, 120.0)
            if (strikePos != null) {
                removeEmc(stack, ProjectBConfig.tweaks.BMLightningAbility.toDouble())
                player.entityWorld.addWeatherEffect(EntityLightningBolt(player.entityWorld, strikePos.x.toDouble(), strikePos.y.toDouble(), strikePos.z.toDouble(), false))
            }
        }
    }

    companion object {

        fun isNightVisionEnabled(helm: ItemStack): Boolean {
            return helm.hasTagCompound() && helm.tagCompound!!.hasKey("NightVision") && helm.tagCompound!!.getBoolean("NightVision")
        }

        fun toggleNightVision(helm: ItemStack, player: EntityPlayer) {
            val value: Boolean = if (ItemHelper.getOrCreateCompound(helm).hasKey("NightVision")) {
                helm.tagCompound!!.setBoolean("NightVision", !helm.tagCompound!!.getBoolean("NightVision"))
                helm.tagCompound!!.getBoolean("NightVision")
            } else {
                helm.tagCompound!!.setBoolean("NightVision", false)
                false
            }

            val e = if (value) TextFormatting.GREEN else TextFormatting.RED
            val s = if (value) "pe.gem.enabled" else "pe.gem.disabled"
            player.sendMessage(TextComponentTranslation("pe.gem.nightvision_tooltip").appendText(" ")
                    .appendSibling(TextComponentTranslation(s).setStyle(Style().setColor(e))))
        }
    }
}
