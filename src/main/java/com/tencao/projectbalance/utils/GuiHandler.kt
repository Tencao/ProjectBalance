package com.tencao.projectbalance.utils

import com.tencao.projectbalance.gameObjs.container.*
import com.tencao.projectbalance.gameObjs.gui.*
import com.tencao.projectbalance.gameObjs.tile.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

class GuiHandler: IGuiHandler {

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val tile = world.getTileEntity(BlockPos(x, y, z))
        when (ID) {
            Constants.CONDENSER_GUI -> if (tile != null && tile is CondenserTile)
                return CondenserContainer(player.inventory, tile)
            Constants.CONDENSER_MK2_GUI -> if (tile != null && tile is CondenserMK2Tile)
                return CondenserMK2Container(player.inventory, tile)
            Constants.COLLECTOR1_GUI -> if (tile != null && tile is CollectorMK1Tile)
                return CollectorMK1Container(player.inventory, tile)
            Constants.COLLECTOR2_GUI -> if (tile != null && tile is CollectorMK2Tile)
                return CollectorMK2Container(player.inventory, tile)
            Constants.COLLECTOR3_GUI -> if (tile != null && tile is CollectorMK3Tile)
                return CollectorMK3Container(player.inventory, tile)
            Constants.COLLECTOR4_GUI -> if (tile != null && tile is CollectorMK4Tile)
                return CollectorMK4Container(player.inventory, tile)
            Constants.RELAY1_GUI -> if (tile != null && tile is RelayMK1Tile)
                return RelayMK1Container(player.inventory, tile)
            Constants.RELAY2_GUI -> if (tile != null && tile is RelayMK2Tile)
                return RelayMK2Container(player.inventory, tile)
            Constants.RELAY3_GUI -> if (tile != null && tile is RelayMK3Tile)
                return RelayMK3Container(player.inventory, tile)
            Constants.RELAY4_GUI -> if (tile != null && tile is RelayMK4Tile)
                return RelayMK4Container(player.inventory, tile)
            Constants.CONDENSED_RELAY1_GUI -> if (tile != null && tile is CondensedRelayMK1Tile)
                return CondensedRelayMK1Container(player.inventory, tile)
            Constants.CONDENSED_RELAY2_GUI -> if (tile != null && tile is CondensedRelayMK2Tile)
                return CondensedRelayMK2Container(player.inventory, tile)
            Constants.CONDENSED_RELAY3_GUI -> if (tile != null && tile is CondensedRelayMK3Tile)
                return CondensedRelayMK3Container(player.inventory, tile)
            Constants.CONDENSED_RELAY4_GUI -> if (tile != null && tile is CondensedRelayMK4Tile)
                return CondensedRelayMK4Container(player.inventory, tile)
            Constants.POWERFLOWER1_GUI -> if (tile != null && tile is PowerFlowerMK1Tile)
                return PowerFlowerMK1Container(player.inventory, tile)
            Constants.POWERFLOWER2_GUI -> if (tile != null && tile is PowerFlowerMK2Tile)
                return PowerFlowerMK1Container(player.inventory, tile)
            Constants.POWERFLOWER3_GUI -> if (tile != null && tile is PowerFlowerMK3Tile)
                return PowerFlowerMK2Container(player.inventory, tile)
            Constants.POWERFLOWER4_GUI -> if (tile != null && tile is PowerFlowerMK4Tile)
                return PowerFlowerMK2Container(player.inventory, tile)
        }
        return null
    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val tile = world.getTileEntity(BlockPos(x, y, z))

        when (ID) {
            Constants.CONDENSER_GUI -> if (tile != null && tile is CondenserTile)
                return GUICondenser(player.inventory, tile)
            Constants.CONDENSER_MK2_GUI -> if (tile != null && tile is CondenserMK2Tile)
                return GUICondenserMK2(player.inventory, tile)
            Constants.COLLECTOR1_GUI -> if (tile != null && tile is CollectorMK1Tile)
                return GUICollectorMK1(player.inventory, tile)
            Constants.COLLECTOR2_GUI -> if (tile != null && tile is CollectorMK2Tile)
                return GUICollectorMK2(player.inventory, tile)
            Constants.COLLECTOR3_GUI -> if (tile != null && tile is CollectorMK3Tile)
                return GUICollectorMK3(player.inventory, tile)
            Constants.COLLECTOR4_GUI -> if (tile != null && tile is CollectorMK4Tile)
                return GUICollectorMK4(player.inventory, tile)
            Constants.RELAY1_GUI -> if (tile != null && tile is RelayMK1Tile)
                return GUIRelayMK1(player.inventory, tile)
            Constants.RELAY2_GUI -> if (tile != null && tile is RelayMK2Tile)
                return GUIRelayMK2(player.inventory, tile)
            Constants.RELAY3_GUI -> if (tile != null && tile is RelayMK3Tile)
                return GUIRelayMK3(player.inventory, tile)
            Constants.RELAY4_GUI -> if (tile != null && tile is RelayMK4Tile)
                return GUIRelayMK4(player.inventory, tile)
            Constants.CONDENSED_RELAY1_GUI -> if (tile != null && tile is CondensedRelayMK1Tile)
                return GUICondensedRelayMK1(player.inventory, tile)
            Constants.CONDENSED_RELAY2_GUI -> if (tile != null && tile is CondensedRelayMK2Tile)
                return GUICondensedRelayMK2(player.inventory, tile)
            Constants.CONDENSED_RELAY3_GUI -> if (tile != null && tile is CondensedRelayMK3Tile)
                return GUICondensedRelayMK3(player.inventory, tile)
            Constants.CONDENSED_RELAY4_GUI -> if (tile != null && tile is CondensedRelayMK4Tile)
                return GUICondensedRelayMK4(player.inventory, tile)
            Constants.POWERFLOWER1_GUI -> if (tile != null && tile is PowerFlowerMK1Tile)
                return GUIPowerFlowerMK1(player.inventory, tile)
            Constants.POWERFLOWER2_GUI -> if (tile != null && tile is PowerFlowerMK2Tile)
                return GUIPowerFlowerMK2(player.inventory, tile)
            Constants.POWERFLOWER3_GUI -> if (tile != null && tile is PowerFlowerMK3Tile)
                return GUIPowerFlowerMK3(player.inventory, tile)
            Constants.POWERFLOWER4_GUI -> if (tile != null && tile is PowerFlowerMK4Tile)
                return GUIPowerFlowerMK4(player.inventory, tile)
        }

        return null
    }
}