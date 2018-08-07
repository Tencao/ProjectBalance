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

import com.tencao.projectbalance.gameObjs.container.RMFurnaceContainer
import com.tencao.projectbalance.gameObjs.tile.RMFurnaceTile
import moze_intel.projecte.PECore
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation


class GUIRMFurnace(invPlayer: InventoryPlayer, val tile: RMFurnaceTile) : GuiContainer(RMFurnaceContainer(invPlayer, tile)) {
    private val texture = ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/rmfurnace.png")

    init {
        this.xSize = 209
        this.ySize = 165
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


        var progress: Int

        if (tile.isBurning()) {
            progress = tile.getBurnTimeRemainingScaled(12)
            this.drawTexturedModalRect(x + 66, y + 38 + 10 - progress, 210, 10 - progress, 21, progress + 2)
        }

        progress = tile.getCookProgressScaled(24)
        this.drawTexturedModalRect(x + 88, y + 35, 210, 14, progress, 17)
    }

    override fun drawGuiContainerForegroundLayer(var1: Int, var2: Int) {
        this.fontRenderer.drawString(I18n.format("pe.rmfurnace.shortname"), 76, 5, 4210752)
        this.fontRenderer.drawString(I18n.format("container.inventory"), 76, ySize - 96 + 2, 4210752)
    }

}