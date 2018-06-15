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

import com.tencao.projectbalance.gameObjs.container.TransmutationContainer
import com.tencao.projectbalance.gameObjs.container.inventory.TransmutationInventory
import com.tencao.projectbalance.handlers.getInternalCooldowns
import com.tencao.projectbalance.utils.Constants
import moze_intel.projecte.PECore
import moze_intel.projecte.api.ProjectEAPI
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.io.IOException
import java.util.*

class GUITransmutation(val invPlayer: InventoryPlayer, private val inv: TransmutationInventory, hand: EnumHand?) : GuiContainer(TransmutationContainer(invPlayer, inv, hand)), ICraftingGUI {
    private var textBoxFilter: GuiTextField? = null

    init {
        this.xSize = 228
        this.ySize = 196
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    override fun initGui() {
        super.initGui()

        val xLocation = (this.width - this.xSize) / 2
        val yLocation = (this.height - this.ySize) / 2

        this.textBoxFilter = GuiTextField(0, this.fontRenderer, xLocation + 88, yLocation + 8, 45, 10)
        this.textBoxFilter!!.text = inv.filter

        this.buttonList.add(GuiButton(1, xLocation + 125, yLocation + 100, 14, 14, "<"))
        this.buttonList.add(GuiButton(2, xLocation + 193, yLocation + 100, 14, 14, ">"))
    }

    override fun drawGuiContainerBackgroundLayer(var1: Float, var2: Int, var3: Int) {
        GlStateManager.color(1f, 1f, 1f, 1f)
        Minecraft.getMinecraft().renderEngine.bindTexture(texture)
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize)
        this.textBoxFilter!!.drawTextBox()
    }

    override fun drawGuiContainerForegroundLayer(var1: Int, var2: Int) {
        this.fontRenderer.drawString(I18n.format("pe.transmutation.transmute"), 6, 5, 4210752)
        val emcAmount = inv.player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null)!!.emc
        val emc = I18n.format("pe.emc.emc_tooltip_prefix") + " " + Constants.EMC_FORMATTER.format(emcAmount)
        this.fontRenderer.drawString(emc, 6, this.ySize - 94, 4210752)

        if (inv.learnFlag > 0) {
            this.fontRenderer.drawString(I18n.format("pe.transmutation.learned0"), 98, 30, 4210752)
            this.fontRenderer.drawString(I18n.format("pe.transmutation.learned1"), 99, 38, 4210752)
            this.fontRenderer.drawString(I18n.format("pe.transmutation.learned2"), 100, 46, 4210752)
            this.fontRenderer.drawString(I18n.format("pe.transmutation.learned3"), 101, 54, 4210752)
            this.fontRenderer.drawString(I18n.format("pe.transmutation.learned4"), 102, 62, 4210752)
            this.fontRenderer.drawString(I18n.format("pe.transmutation.learned5"), 103, 70, 4210752)
            this.fontRenderer.drawString(I18n.format("pe.transmutation.learned6"), 104, 78, 4210752)
            this.fontRenderer.drawString(I18n.format("pe.transmutation.learned7"), 107, 86, 4210752)

            inv.learnFlag--
        }

        if (inv.unlearnFlag > 0) {
            this.fontRenderer.drawString(I18n.format("pe.transmutation.unlearned0"), 97, 22, 4210752)
            this.fontRenderer.drawString(I18n.format("pe.transmutation.unlearned1"), 98, 30, 4210752)
            this.fontRenderer.drawString(I18n.format("pe.transmutation.unlearned2"), 99, 38, 4210752)
            this.fontRenderer.drawString(I18n.format("pe.transmutation.unlearned3"), 100, 46, 4210752)
            this.fontRenderer.drawString(I18n.format("pe.transmutation.unlearned4"), 101, 54, 4210752)
            this.fontRenderer.drawString(I18n.format("pe.transmutation.unlearned5"), 102, 62, 4210752)
            this.fontRenderer.drawString(I18n.format("pe.transmutation.unlearned6"), 103, 70, 4210752)
            this.fontRenderer.drawString(I18n.format("pe.transmutation.unlearned7"), 104, 78, 4210752)
            this.fontRenderer.drawString(I18n.format("pe.transmutation.unlearned8"), 107, 86, 4210752)

            inv.unlearnFlag--
        }
        if (invPlayer.player.getInternalCooldowns().getRequiredTime() > 100){
            val totalSeconds = (invPlayer.player.getInternalCooldowns().getRequiredTime() - invPlayer.player.getInternalCooldowns().getTimePassed()) / 20
            val totalMinutes = (totalSeconds % 3600) / 60
            val totalHours = totalSeconds / 3600
            when {
                totalHours > 0 -> this.fontRenderer.drawString("x${invPlayer.player.getInternalCooldowns().getStack().count} ${String.format("%02dh %02dm %02ds", totalHours , totalMinutes, totalSeconds % 60)}", 23, 16, 4210752)
                totalMinutes > 0 -> this.fontRenderer.drawString("x${invPlayer.player.getInternalCooldowns().getStack().count} ${String.format("%02dm %02ds", totalMinutes, totalSeconds % 60)}", 23, 16, 4210752)
                else -> this.fontRenderer.drawString("x${invPlayer.player.getInternalCooldowns().getStack().count} ${String.format("%02ds", totalSeconds % 60)}", 23, 16, 4210752)
            }
            drawItemStack(invPlayer.player.getInternalCooldowns().getStack(), 5, 13, "")
        }
    }

    override fun updateScreen() {
        super.updateScreen()
        this.textBoxFilter!!.updateCursorCounter()
    }

    override fun keyTyped(par1: Char, par2: Int) {
        if (this.textBoxFilter!!.isFocused) {
            this.textBoxFilter!!.textboxKeyTyped(par1, par2)

            val srch = this.textBoxFilter!!.text.toLowerCase()

            if (inv.filter != srch) {
                inv.filter = srch
                inv.searchpage = 0
                inv.updateClientTargets()
            }
        }

        if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.keyCode && !this.textBoxFilter!!.isFocused) {
            this.mc.player.closeScreen()
        }
    }

    @Throws(IOException::class)
    override fun mouseClicked(x: Int, y: Int, mouseButton: Int) {
        super.mouseClicked(x, y, mouseButton)

        val minX = textBoxFilter!!.x
        val minY = textBoxFilter!!.y
        val maxX = minX + textBoxFilter!!.width
        val maxY = minY + textBoxFilter!!.height

        if (mouseButton == 1 && x >= minX && x <= maxX && y <= maxY) {
            inv.filter = ""
            inv.searchpage = 0
            inv.updateClientTargets()
            this.textBoxFilter!!.text = ""
        }

        this.textBoxFilter!!.mouseClicked(x, y, mouseButton)
    }

    override fun onGuiClosed() {
        super.onGuiClosed()
        inv.learnFlag = 0
        inv.unlearnFlag = 0
    }

    override fun actionPerformed(button: GuiButton?) {
        val srch = this.textBoxFilter!!.text.toLowerCase(Locale.ROOT)

        if (button!!.id == 1) {
            if (inv.searchpage != 0) {
                inv.searchpage--
            }
        } else if (button.id == 2) {
            if (inv.knowledge.size > 12) {
                inv.searchpage++
            }
        }
        inv.filter = srch
        inv.updateClientTargets()
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
        private val texture = ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/transmute.png")
    }
}