package com.tencao.projectbalance.gameObjs.gui

import com.tencao.projectbalance.gameObjs.container.CondenserMK2Container
import com.tencao.projectbalance.gameObjs.tile.CondenserMK2Tile
import com.tencao.projectbalance.utils.ComplexHelper
import com.tencao.projectbalance.utils.Constants
import moze_intel.projecte.PECore
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation

class GUICondenserMK2(invPlayer: InventoryPlayer, tile: CondenserMK2Tile) : GuiContainer(CondenserMK2Container(invPlayer, tile)) {
    private val container: CondenserMK2Container = inventorySlots as CondenserMK2Container

    init {
        this.xSize = 255
        this.ySize = 233
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    override fun drawGuiContainerBackgroundLayer(var1: Float, var2: Int, var3: Int) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        Minecraft.getMinecraft().renderEngine.bindTexture(texture)

        val x = (width - xSize) / 2
        val y = (height - ySize) / 2

        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize)

        val progress = container.progressScaled
        this.drawTexturedModalRect(x + 33, y + 10, 0, 235, progress, 10)
    }

    override fun drawGuiContainerForegroundLayer(var1: Int, var2: Int) {
        if (container.requiredEmc == 0) {
            this.fontRenderer.drawString("0", 140, 10, 4210752)
        } else if ((container.displayEmc >= container.requiredEmc || container.timePassed > 0) && container.requiredTime > 100) {
            if (container.tomes > 0) {
                val factor = Math.min(
                        (container.requiredTime * (5.0f / 100.0f) / ComplexHelper.getComplexity(container.inventory[0])).toInt(),
                        (container.tomes / (container.tomes + 2f) * 10f).toInt()
                ) + 1
                val toDisplay = (container.requiredTime - container.timePassed) / (20 * factor)
                this.fontRenderer.drawString(String.format("%02dm %02ds", toDisplay / 60, toDisplay % 60), 140, 10, 4210752)
            } else {
                val toDisplay = (container.requiredTime - container.timePassed) / 20
                this.fontRenderer.drawString(String.format("%02dm %02ds", toDisplay / 60, toDisplay % 60), 140, 10, 4210752)
            }
        } else {
            val toDisplay = if (container.displayEmc > container.requiredEmc) container.requiredEmc else container.displayEmc
            this.fontRenderer.drawString(Constants.EMC_FORMATTER.format(toDisplay.toLong()), 140, 10, 4210752)
        }
    }

    companion object {
        private val texture = ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/condenser_mk2.png")
    }
}
