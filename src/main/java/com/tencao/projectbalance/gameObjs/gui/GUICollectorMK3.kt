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

import com.tencao.projectbalance.gameObjs.container.CollectorMK3Container
import com.tencao.projectbalance.gameObjs.tile.CollectorMK3Tile
import com.tencao.projectbalance.utils.Constants
import moze_intel.projecte.PECore
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation

class GUICollectorMK3(invPlayer: InventoryPlayer, private val tile: CollectorMK3Tile) : GuiContainer(CollectorMK3Container(invPlayer, tile)) {
    private val container: CollectorMK3Container = inventorySlots as CollectorMK3Container

    init {
        this.xSize = 218
        this.ySize = 165
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    override fun drawGuiContainerForegroundLayer(var1: Int, var2: Int) {
        this.fontRenderer.drawString(container.emc.toString(), 91, 32, 4210752)

        val kleinCharge = container.kleinEmc.toDouble()
        if (kleinCharge > 0)
            this.fontRenderer.drawString(Constants.EMC_FORMATTER.format(kleinCharge), 91, 44, 4210752)
    }

    override fun drawGuiContainerBackgroundLayer(var1: Float, var2: Int, var3: Int) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        Minecraft.getMinecraft().renderEngine.bindTexture(texture)

        val x = (width - xSize) / 2
        val y = (height - ySize) / 2

        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize)

        //Light Level. Max is 12
        var progress = (container.sunLevel * 12.0 / 16).toInt()

        this.drawTexturedModalRect(x + 160, y + 49 - progress, 220, 13 - progress, 12, progress)

        //EMC storage. Max is 48
        this.drawTexturedModalRect(x + 98, y + 18, 0, 166, (container.emc / tile.maximumEmc * 48).toInt(), 10)

        //Klein Star Charge Progress. Max is 48
        progress = (container.kleinChargeProgress * 48).toInt()
        this.drawTexturedModalRect(x + 98, y + 58, 0, 166, progress, 10)

        //Fuel Progress. Max is 24.
        progress = (container.fuelProgress * 24).toInt()
        this.drawTexturedModalRect(x + 172, y + 55 - progress, 219, 38 - progress, 10, progress + 1)
    }

    companion object {
        private val texture = ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/collector3.png")
    }
}
