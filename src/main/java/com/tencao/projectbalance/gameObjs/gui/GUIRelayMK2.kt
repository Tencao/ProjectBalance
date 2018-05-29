package com.tencao.projectbalance.gameObjs.gui

import com.tencao.projectbalance.gameObjs.container.RelayMK2Container
import com.tencao.projectbalance.gameObjs.tile.RelayMK2Tile
import com.tencao.projectbalance.utils.Constants
import moze_intel.projecte.PECore
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation

class GUIRelayMK2(invPlayer: InventoryPlayer, private val tile: RelayMK2Tile) : GuiContainer(RelayMK2Container(invPlayer, tile)) {
    private val container: RelayMK2Container

    init {
        this.xSize = 193
        this.ySize = 182
        this.container = inventorySlots as RelayMK2Container
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    override fun drawGuiContainerForegroundLayer(var1: Int, var2: Int) {
        this.fontRenderer.drawString(I18n.format("pe.relay.mk2"), 28, 6, 4210752)
        this.fontRenderer.drawString(Constants.EMC_FORMATTER.format(container.emc.toLong()), 107, 25, 4210752)
    }

    override fun drawGuiContainerBackgroundLayer(var1: Float, var2: Int, var3: Int) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        Minecraft.getMinecraft().renderEngine.bindTexture(texture)

        val x = (width - xSize) / 2
        val y = (height - ySize) / 2

        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize)

        //Emc bar progress
        var progress = (container.emc / tile.maximumEmc * 102).toInt()
        this.drawTexturedModalRect(x + 86, y + 6, 30, 183, progress, 10)

        //Klein start bar progress. Max is 30.
        progress = (container.kleinChargeProgress * 30).toInt()
        this.drawTexturedModalRect(x + 133, y + 68, 0, 183, progress, 10)

        //Burn Slot bar progress. Max is 30.
        progress = (container.inputBurnProgress * 30).toInt()
        drawTexturedModalRect(x + 81, y + 68, 0, 183, progress, 10)
    }

    companion object {
        private val texture = ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/relay2.png")
    }
}
