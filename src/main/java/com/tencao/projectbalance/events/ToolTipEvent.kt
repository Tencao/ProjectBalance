package com.tencao.projectbalance.events

import com.tencao.projectbalance.gameObjs.ObjRegistry
import com.tencao.projectbalance.gameObjs.gui.*
import com.tencao.projectbalance.utils.ComplexHelper
import com.tencao.projectbalance.utils.Constants
import moze_intel.projecte.config.ProjectEConfig
import moze_intel.projecte.gameObjs.gui.GUITransmutation
import moze_intel.projecte.utils.EMCHelper
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object ToolTipEvent {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun ttEvent(event: ItemTooltipEvent){

        val current = event.itemStack
        val currentItem = current.item
        val currentBlock = Block.getBlockFromItem(currentItem)

        if (ProjectEConfig.misc.emcToolTips){
            if (EMCHelper.doesItemHaveEmc(current)){
                if (event.flags.isAdvanced)
                    event.toolTip.add(TextFormatting.YELLOW.toString() + I18n.format("pe.emc.complexity_tooltip") + " " + TextFormatting.WHITE + ComplexHelper.getComplexity(current))
                val currentScreen = Minecraft.getMinecraft().currentScreen
                if (currentScreen != null && (currentScreen is GUIPowerFlowerMK1 || currentScreen is GUIPowerFlowerMK2 ||
                                currentScreen is GUIPowerFlowerMK3 || currentScreen is GUIPowerFlowerMK4 ||
                                currentScreen is GUICondenser || currentScreen is GUICondenserMK2 || currentScreen is GUITransmutation)) {
                    var totalSeconds = ComplexHelper.getCraftTime(current) / 20
                    if (totalSeconds <= 5)
                        totalSeconds = 0
                    event.toolTip.add(TextFormatting.YELLOW.toString() + I18n.format("pe.emc.craft_time") + " " +
                            TextFormatting.WHITE + String.format("%02dm %02ds", totalSeconds / 60, totalSeconds % 60))
                }
            }
        }

        if (ProjectEConfig.misc.statToolTips) {
            /*
			 * Collector ToolTips
			 */
            val unit = I18n.format("pe.emc.name")
            val rate = I18n.format("pe.emc.rate")
            val rate10 = I18n.format("pe.emc.rate10")

            if (currentBlock === ObjRegistry.collectorMK1) {
                event.toolTip.removeIf {
                    it.contains(String.format(I18n.format("pe.emc.maxgenrate_tooltip"))) ||
                            it.contains(String.format(I18n.format("pe.emc.maxstorage_tooltip"))) }
                if (Constants.COLLECTOR_MK1_GEN < 1) {
                    event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxgenrate_tooltip")
                            + TextFormatting.BLUE + " %d " + rate10, (Constants.COLLECTOR_MK1_GEN * 10.50).toInt()))
                } else {
                    event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxgenrate_tooltip")
                            + TextFormatting.BLUE + " %d " + rate, Constants.COLLECTOR_MK1_GEN.toInt()))
                }
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxstorage_tooltip")
                        + TextFormatting.BLUE + " %d " + unit, Constants.COLLECTOR_MK1_MAX))
            }
            if (currentBlock === ObjRegistry.collectorMK2) {
                event.toolTip.removeIf {
                    it.contains(String.format(I18n.format("pe.emc.maxgenrate_tooltip"))) ||
                            it.contains(String.format(I18n.format("pe.emc.maxstorage_tooltip"))) }
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxgenrate_tooltip")
                        + TextFormatting.BLUE + " %d " + rate, Constants.COLLECTOR_MK2_GEN.toInt()))
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxstorage_tooltip")
                        + TextFormatting.BLUE + " %d " + unit, Constants.COLLECTOR_MK2_MAX))
            }

            if (currentBlock === ObjRegistry.collectorMK3) {
                event.toolTip.removeIf {
                    it.contains(String.format(I18n.format("pe.emc.maxgenrate_tooltip"))) ||
                            it.contains(String.format(I18n.format("pe.emc.maxstorage_tooltip"))) }
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxgenrate_tooltip")
                        + TextFormatting.BLUE + " %d " + rate, Constants.COLLECTOR_MK3_GEN.toInt()))
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxstorage_tooltip")
                        + TextFormatting.BLUE + " %d " + unit, Constants.COLLECTOR_MK3_MAX))
            }
            if (currentBlock === ObjRegistry.collectorMK4) {
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxgenrate_tooltip")
                        + TextFormatting.BLUE + " %d " + rate, Constants.COLLECTOR_MK4_GEN.toInt()))
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxstorage_tooltip")
                        + TextFormatting.BLUE + " %d " + unit, Constants.COLLECTOR_MK4_MAX))
            }

            /*
			 * Relay ToolTips
			 */
            if (currentBlock === ObjRegistry.relay) {
                event.toolTip.removeIf {
                    it.contains(String.format(I18n.format("pe.emc.maxoutrate_tooltip"))) ||
                            it.contains(String.format(I18n.format("pe.emc.maxstorage_tooltip"))) }
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxoutrate_tooltip")
                        + TextFormatting.BLUE + " %d " + rate, Constants.RELAY_MK1_OUTPUT))
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxstorage_tooltip")
                        + TextFormatting.BLUE + " %d " + unit, Constants.RELAY_MK1_MAX))
            }

            if (currentBlock === ObjRegistry.relayMK2) {
                event.toolTip.removeIf {
                    it.contains(String.format(I18n.format("pe.emc.maxoutrate_tooltip"))) ||
                            it.contains(String.format(I18n.format("pe.emc.maxstorage_tooltip"))) }
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxoutrate_tooltip")
                        + TextFormatting.BLUE + " %d " + rate, Constants.RELAY_MK2_OUTPUT))
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxstorage_tooltip")
                        + TextFormatting.BLUE + " %d " + unit, Constants.RELAY_MK2_MAX))
            }

            if (currentBlock === ObjRegistry.relayMK3) {
                event.toolTip.removeIf {
                    it.contains(String.format(I18n.format("pe.emc.maxoutrate_tooltip"))) ||
                            it.contains(String.format(I18n.format("pe.emc.maxstorage_tooltip"))) }
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxoutrate_tooltip")
                        + TextFormatting.BLUE + " %d " + rate, Constants.RELAY_MK3_OUTPUT))
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxstorage_tooltip")
                        + TextFormatting.BLUE + " %d " + unit, Constants.RELAY_MK3_MAX))
            }
            if (currentBlock === ObjRegistry.relayMK4) {
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxoutrate_tooltip")
                        + TextFormatting.BLUE + " %d " + rate, Constants.RELAY_MK4_OUTPUT))
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxstorage_tooltip")
                        + TextFormatting.BLUE + " %d " + unit, Constants.RELAY_MK4_MAX))
            }
            if (currentBlock === ObjRegistry.condensedRelayMK1) {
                if (Constants.COLLECTOR_MK1_GEN < 1) {
                    event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxgenrate_tooltip")
                            + TextFormatting.BLUE + " %d " + rate10, (Constants.CONDENSED_RELAY_MK1_GEN * 10.50).toInt()))
                } else {
                    event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxgenrate_tooltip")
                            + TextFormatting.BLUE + " %d " + rate, Constants.CONDENSED_RELAY_MK1_GEN.toInt()))
                }
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxoutrate_tooltip")
                        + TextFormatting.BLUE + " %d " + rate, Constants.CONDENSED_RELAY_MK1_OUTPUT))
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxstorage_tooltip")
                        + TextFormatting.BLUE + " %d " + unit, Constants.CONDENSED_RELAY_MK1_MAX))
            }
            if (currentBlock === ObjRegistry.condensedRelayMK2) {
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxgenrate_tooltip")
                        + TextFormatting.BLUE + " %d " + rate, Constants.CONDENSED_RELAY_MK2_GEN.toInt()))
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxoutrate_tooltip")
                        + TextFormatting.BLUE + " %d " + rate, Constants.CONDENSED_RELAY_MK2_OUTPUT))
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxstorage_tooltip")
                        + TextFormatting.BLUE + " %d " + unit, Constants.CONDENSED_RELAY_MK2_MAX))
            }
            if (currentBlock === ObjRegistry.condensedRelayMK3) {
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxgenrate_tooltip")
                        + TextFormatting.BLUE + " %d " + rate, Constants.CONDENSED_RELAY_MK3_GEN.toInt()))
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxoutrate_tooltip")
                        + TextFormatting.BLUE + " %d " + rate, Constants.CONDENSED_RELAY_MK3_OUTPUT))
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxstorage_tooltip")
                        + TextFormatting.BLUE + " %d " + unit, Constants.CONDENSED_RELAY_MK3_MAX))
            }
            if (currentBlock === ObjRegistry.condensedRelayMK4) {
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxgenrate_tooltip")
                        + TextFormatting.BLUE + " %d " + rate, Constants.CONDENSED_RELAY_MK4_GEN.toInt()))
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxoutrate_tooltip")
                        + TextFormatting.BLUE + " %d " + rate, Constants.CONDENSED_RELAY_MK4_OUTPUT))
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxstorage_tooltip")
                        + TextFormatting.BLUE + " %d " + unit, Constants.CONDENSED_RELAY_MK4_MAX))
            }
            if (currentBlock === ObjRegistry.powerFlower) {
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxgenrate_tooltip")
                        + TextFormatting.BLUE + " %d " + rate, Constants.POWER_FLOWER_MK1_GEN.toInt()))
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxstorage_tooltip")
                        + TextFormatting.BLUE + " %d " + unit, Constants.POWER_FLOWER_MK1_MAX))
            }
            if (currentBlock === ObjRegistry.powerFlowerMK2) {
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxgenrate_tooltip")
                        + TextFormatting.BLUE + " %d " + rate, Constants.POWER_FLOWER_MK2_GEN.toInt()))
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxstorage_tooltip")
                        + TextFormatting.BLUE + " %d " + unit, Constants.POWER_FLOWER_MK2_MAX))
            }
            if (currentBlock === ObjRegistry.powerFlowerMK3) {
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxgenrate_tooltip")
                        + TextFormatting.BLUE + " %d " + rate, Constants.POWER_FLOWER_MK3_GEN.toInt()))
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxstorage_tooltip")
                        + TextFormatting.BLUE + " %d " + unit, Constants.POWER_FLOWER_MK3_MAX))
            }
            if (currentBlock === ObjRegistry.powerFlowerMK4) {
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxgenrate_tooltip")
                        + TextFormatting.BLUE + " %d " + rate, Constants.POWER_FLOWER_MK4_GEN.toInt()))
                event.toolTip.add(TextFormatting.DARK_PURPLE.toString() + String.format(I18n.format("pe.emc.maxstorage_tooltip")
                        + TextFormatting.BLUE + " %d " + unit, Constants.POWER_FLOWER_MK4_MAX))
            }
        }
    }
}