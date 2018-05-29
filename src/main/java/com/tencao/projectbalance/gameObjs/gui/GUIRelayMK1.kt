package com.tencao.projectbalance.gameObjs.gui

import com.tencao.projectbalance.gameObjs.container.RelayMK1Container
import com.tencao.projectbalance.gameObjs.tile.RelayMK1Tile
import com.tencao.projectbalance.utils.Constants
import moze_intel.projecte.PECore
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation

class GUIRelayMK1(invPlayer: InventoryPlayer, private val tile: RelayMK1Tile) : GuiContainer(RelayMK1Container(invPlayer, tile)) {
    private val container: RelayMK1Container

    init {
        this.xSize = 175
        this.ySize = 176
        this.container = inventorySlots as RelayMK1Container
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    override fun drawGuiContainerForegroundLayer(var1: Int, var2: Int) {
        this.fontRenderer.drawString(I18n.format("pe.relay.mk1"), 10, 6, 4210752)
        this.fontRenderer.drawString(Constants.EMC_FORMATTER.format(container.emc.toLong()), 88, 24, 4210752)
    }

    override fun drawGuiContainerBackgroundLayer(var1: Float, var2: Int, var3: Int) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        Minecraft.getMinecraft().renderEngine.bindTexture(texture)

        val x = (width - xSize) / 2
        val y = (height - ySize) / 2

        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize)

        //Emc bar progress. Max is 102.
        var progress = (container.emc / tile.maximumEmc * 102).toInt()
        this.drawTexturedModalRect(x + 64, y + 6, 30, 177, progress, 10)

        //Klein start bar progress. Max is 30.
        progress = (container.kleinChargeProgress * 30).toInt()
        this.drawTexturedModalRect(x + 116, y + 67, 0, 177, progress, 10)

        //Burn Slot bar progress. Max is 30.
        progress = (container.inputBurnProgress * 30).toInt()
        drawTexturedModalRect(x + 64, y + 67, 0, 177, progress, 10)
    }

    companion object {
        private val texture = ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/relay1.png")
    }
}
