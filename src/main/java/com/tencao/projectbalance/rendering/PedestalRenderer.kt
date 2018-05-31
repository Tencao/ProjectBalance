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

package com.tencao.projectbalance.rendering

import com.tencao.projectbalance.gameObjs.tile.DMPedestalTile
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.math.MathHelper

class PedestalRenderer : TileEntitySpecialRenderer<DMPedestalTile>() {

    override fun render(te: DMPedestalTile, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, unused: Float) {
        if (!te.isInvalid) {
            if (Minecraft.getMinecraft().renderManager.isDebugBoundingBox) {
                GlStateManager.pushMatrix()
                GlStateManager.translate(x, y, z)
                GlStateManager.depthMask(false)
                GlStateManager.disableTexture2D()
                GlStateManager.disableLighting()
                GlStateManager.disableCull()
                GlStateManager.disableBlend()
                val aabb = te.getEffectBounds().offset((-te.pos.x).toDouble(), (-te.pos.y).toDouble(), (-te.pos.z).toDouble())
                RenderGlobal.drawBoundingBox(
                        aabb.minX, aabb.minY, aabb.minZ,
                        aabb.maxX + 1, aabb.maxY + 1, aabb.maxZ + 1,
                        1f, 0f, 1f, 1f)
                GlStateManager.enableBlend()
                GlStateManager.enableCull()
                GlStateManager.enableLighting()
                GlStateManager.enableTexture2D()
                GlStateManager.depthMask(true)
                GlStateManager.popMatrix()
            }

            if (!te.getInventory().getStackInSlot(0).isEmpty) {
                GlStateManager.pushMatrix()
                GlStateManager.translate(x + 0.5, y + 0.7, z + 0.5)
                GlStateManager.translate(0f, MathHelper.sin((te.world.totalWorldTime + partialTicks) / 10.0f) * 0.1f + 0.1f, 0f)
                GlStateManager.scale(0.75, 0.75, 0.75)
                val angle = (te.world.totalWorldTime + partialTicks) / 20.0f * (180f / Math.PI.toFloat())
                GlStateManager.rotate(angle, 0.0f, 1.0f, 0.0f)
                Minecraft.getMinecraft().renderItem.renderItem(te.getInventory().getStackInSlot(0), ItemCameraTransforms.TransformType.GROUND)
                GlStateManager.popMatrix()
            }
        }
    }

}
