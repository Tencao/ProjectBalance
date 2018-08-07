/*
 * Copyright (C) 2018
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.tencao.projectbalance.gameObjs.gui

import com.tencao.projectbalance.gameObjs.ObjRegistry
import com.tencao.projectbalance.gameObjs.container.PowerFlowerMK2Container
import com.tencao.projectbalance.gameObjs.tile.PowerFlowerMK3Tile
import com.tencao.projectbalance.utils.Constants
import moze_intel.projecte.PECore
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

class GUIPowerFlowerMK3(invPlayer: InventoryPlayer, tile: PowerFlowerMK3Tile) : GuiContainer(PowerFlowerMK2Container(invPlayer, tile)), ICraftingGUI {
    private val container: PowerFlowerMK2Container = inventorySlots as PowerFlowerMK2Container

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

        var progress = container.progressScaled
        this.drawTexturedModalRect(x + 33, y + 10, 0, 235, progress, 10)

        Minecraft.getMinecraft().renderEngine.bindTexture(sunTexture)

        //Light Level. Max is 12
        progress = container.sunLevel * 12 / 16
        this.drawTexturedModalRect(x + 230, y + 20 - progress, 177, 13 - progress, 12, progress)
    }

    override fun drawGuiContainerForegroundLayer(var1: Int, var2: Int) {
        if (container.requiredEmc == 0L) {
            this.fontRenderer.drawString(Constants.EMC_FORMATTER.format(container.displayEmc), 140, 10, 4210752)
        } else if ((container.displayEmc >= container.requiredEmc || container.timePassed > 0) && container.requiredTime > 100) {
            val totalSeconds = if (container.tomes > 0){
                val factor = Math.min(
                        ((container.requiredTime * (5.0f / 100.0f)) / 20).toInt(),
                        container.tomes * 2) + 1
                (container.requiredTime - container.timePassed) / (factor * 20)
            } else (container.requiredTime - container.timePassed) / 20
            val totalMinutes = (totalSeconds % 3600) / 60
            val totalHours = totalSeconds / 3600
            when {
                totalHours > 0 -> this.fontRenderer.drawString(String.format("%02dh %02dm %02ds", totalHours , totalMinutes, totalSeconds % 60), 140, 10, 4210752)
                totalMinutes > 0 -> this.fontRenderer.drawString(String.format("%02dm %02ds", totalMinutes, totalSeconds % 60), 140, 10, 4210752)
                else -> this.fontRenderer.drawString(String.format("%02ds", totalSeconds % 60), 140, 10, 4210752)
            }
        } else {
            val toDisplay = if (container.displayEmc > container.requiredEmc) container.requiredEmc else container.displayEmc
            this.fontRenderer.drawString(Constants.EMC_FORMATTER.format(toDisplay), 140, 10, 4210752)
        }
        this.fontRenderer.drawString("x${container.tomes}", 235, 10, 4210752)
        drawItemStack(ItemStack(ObjRegistry.tome), 215, 5, "")
    }

    /**
     * Copied from GuiContainer.class
     * Draws an ItemStack.
     *
     * The z index is increased by 32 (and not decreased afterwards), and the item is then rendered at z=200.
     */
    private fun drawItemStack(stack: ItemStack, x: Int, y: Int, altText: String) {
        RenderHelper.disableStandardItemLighting()
        GL11.glDisable(GL11.GL_LIGHTING)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        RenderHelper.enableGUIStandardItemLighting()
        GlStateManager.translate(0.0f, 0.0f, 32.0f)
        this.zLevel = 200.0f
        this.itemRender.zLevel = 200.0f
        var font: net.minecraft.client.gui.FontRenderer? = stack.item.getFontRenderer(stack)
        if (font == null) font = fontRenderer
        this.itemRender.renderItemAndEffectIntoGUI(stack, x, y)
        this.itemRender.renderItemOverlayIntoGUI(font!!, stack, x, y - 0, altText)
        this.zLevel = 0.0f
        this.itemRender.zLevel = 0.0f
        GL11.glEnable(GL11.GL_LIGHTING)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        RenderHelper.enableStandardItemLighting()
    }

    companion object {
        private val texture = ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/condenser_mk2.png")
        private val sunTexture = ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/collector1.png")
    }
}
