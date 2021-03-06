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

import com.tencao.projectbalance.gameObjs.blocks.PowerFlower
import com.tencao.projectbalance.gameObjs.tile.PowerFlowerMK1Tile
import moze_intel.projecte.PECore
import moze_intel.projecte.api.state.PEStateProps
import net.minecraft.client.model.ModelChest
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation

class PowerFlowerRenderer(tier: Int) : TileEntitySpecialRenderer<PowerFlowerMK1Tile>() {
    private val model by lazy { ModelChest() }
    private val texture: ResourceLocation = when (tier) {
        2 -> {
            ResourceLocation(PECore.MODID.toLowerCase(), "textures/blocks/powerflower/powerflower_mk2.png")
        }
        3 -> {
            ResourceLocation(PECore.MODID.toLowerCase(), "textures/blocks/powerflower/powerflower_mk3.png")
        }
        4 -> {
            ResourceLocation(PECore.MODID.toLowerCase(), "textures/blocks/powerflower/powerflower_mk4.png")
        }
        else -> ResourceLocation(PECore.MODID.toLowerCase(), "textures/blocks/powerflower/powerflower_mk1.png")
    }


    override fun render(condenser: PowerFlowerMK1Tile, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, unknown: Float) {
        var direction: EnumFacing? = null
        if (condenser.world != null && !condenser.isInvalid) {
            val state = condenser.world.getBlockState(condenser.pos)
            direction = if (state.block is PowerFlower) state.getValue(PEStateProps.FACING) else null
        }


        this.bindTexture(texture)
        GlStateManager.pushMatrix()
        GlStateManager.enableRescaleNormal()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.translate(x, y + 1.0f, z + 1.0f)
        GlStateManager.scale(1.0f, -1.0f, -1.0f)
        GlStateManager.translate(0.5f, 0.5f, 0.5f)

        var angle: Short = 0

        if (direction != null) {
            when (direction) {
                EnumFacing.NORTH -> angle = 180
                EnumFacing.SOUTH -> angle = 0
                EnumFacing.WEST -> angle = 90
                EnumFacing.EAST -> angle = -90
                else -> angle = 0
            }
        }

        GlStateManager.rotate(angle.toFloat(), 0.0f, 1.0f, 0.0f)
        GlStateManager.translate(-0.5f, -0.5f, -0.5f)
        var adjustedLidAngle = condenser.prevLidAngle + (condenser.lidAngle - condenser.prevLidAngle) * partialTicks
        adjustedLidAngle = 1.0f - adjustedLidAngle
        adjustedLidAngle = 1.0f - adjustedLidAngle * adjustedLidAngle * adjustedLidAngle
        model.chestLid.rotateAngleX = -(adjustedLidAngle * Math.PI.toFloat() / 2.0f)
        model.renderAll()
        GlStateManager.disableRescaleNormal()
        GlStateManager.popMatrix()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    }
}
