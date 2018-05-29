package com.tencao.projectbalance.rendering

import com.tencao.projectbalance.gameObjs.ObjRegistry
import com.tencao.projectbalance.gameObjs.tile.CondenserMK2Tile
import moze_intel.projecte.PECore
import moze_intel.projecte.api.state.PEStateProps
import net.minecraft.client.model.ModelChest
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
class CondenserMK2Renderer : TileEntitySpecialRenderer<CondenserMK2Tile>() {
    private val texture = ResourceLocation(PECore.MODID.toLowerCase(), "textures/blocks/condenser_mk2.png")
    private val model = ModelChest()

    override fun render(condenser: CondenserMK2Tile, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, unknown: Float) {
        var direction: EnumFacing? = null
        if (condenser.world != null && !condenser.isInvalid) {
            val state = condenser.world.getBlockState(condenser.pos)
            direction = if (state.block === ObjRegistry.condenserMk2) state.getValue(PEStateProps.FACING) else null
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