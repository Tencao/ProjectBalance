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

import com.tencao.projectbalance.gameObjs.container.RelayMK4Container
import com.tencao.projectbalance.gameObjs.tile.RelayMK4Tile
import moze_intel.projecte.PECore
import moze_intel.projecte.utils.Constants
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation

class GUIRelayMK4(invPlayer: InventoryPlayer, private val tile: RelayMK4Tile) : GuiContainer(RelayMK4Container(invPlayer, tile)) {
    private val container: RelayMK4Container

    init {
        this.xSize = 212
        this.ySize = 194
        this.container = inventorySlots as RelayMK4Container
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    override fun drawGuiContainerForegroundLayer(var1: Int, var2: Int) {
        this.fontRenderer.drawString(I18n.format("pe.relay.mk4"), 38, 6, 4210752)
        this.fontRenderer.drawString(Constants.EMC_FORMATTER.format(container.emc), 125, 39, 4210752)
    }

    override fun drawGuiContainerBackgroundLayer(var1: Float, var2: Int, var3: Int) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        Minecraft.getMinecraft().renderEngine.bindTexture(texture)

        val x = (width - xSize) / 2
        val y = (height - ySize) / 2

        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize)

        //Emc bar progress
        var progress = (container.emc / tile.maximumEmc * 102).toInt()
        this.drawTexturedModalRect(x + 105, y + 6, 30, 195, progress, 10)

        //Klein start bar progress. Max is 30.
        progress = (container.kleinChargeProgress * 30).toInt()
        this.drawTexturedModalRect(x + 153, y + 82, 0, 195, progress, 10)

        //Burn Slot bar progress. Max is 30.
        progress = (container.inputBurnProgress * 30).toInt()
        drawTexturedModalRect(x + 101, y + 82, 0, 195, progress, 10)
    }

    companion object {
        private val texture = ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/relay3.png")
    }
}
