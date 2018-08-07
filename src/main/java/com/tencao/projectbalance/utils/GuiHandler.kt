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

package com.tencao.projectbalance.utils

import com.tencao.projectbalance.gameObjs.container.*
import com.tencao.projectbalance.gameObjs.container.inventory.TransmutationInventory
import com.tencao.projectbalance.gameObjs.gui.*
import com.tencao.projectbalance.gameObjs.tile.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

class GuiHandler: IGuiHandler {

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val tile = world.getTileEntity(BlockPos(x, y, z))
        val hand = if (ID == Constants.TRANSMUTATION_GUI) if (x == 1) EnumHand.OFF_HAND else EnumHand.MAIN_HAND else null

        when (ID) {
            Constants.CONDENSER_GUI -> if (tile is CondenserTile)
                return CondenserContainer(player.inventory, tile)
            Constants.CONDENSER_MK2_GUI -> if (tile is CondenserMK2Tile)
                return CondenserMK2Container(player.inventory, tile)
            Constants.COLLECTOR1_GUI -> if (tile is CollectorMK1Tile)
                return CollectorMK1Container(player.inventory, tile)
            Constants.COLLECTOR2_GUI -> if (tile is CollectorMK2Tile)
                return CollectorMK2Container(player.inventory, tile)
            Constants.COLLECTOR3_GUI -> if (tile is CollectorMK3Tile)
                return CollectorMK3Container(player.inventory, tile)
            Constants.COLLECTOR4_GUI -> if (tile is CollectorMK4Tile)
                return CollectorMK4Container(player.inventory, tile)
            Constants.RELAY1_GUI -> if (tile is RelayMK1Tile)
                return RelayMK1Container(player.inventory, tile)
            Constants.RELAY2_GUI -> if (tile is RelayMK2Tile)
                return RelayMK2Container(player.inventory, tile)
            Constants.RELAY3_GUI -> if (tile is RelayMK3Tile)
                return RelayMK3Container(player.inventory, tile)
            Constants.RELAY4_GUI -> if (tile is RelayMK4Tile)
                return RelayMK4Container(player.inventory, tile)
            Constants.CONDENSED_RELAY1_GUI -> if (tile is CondensedRelayMK1Tile)
                return CondensedRelayMK1Container(player.inventory, tile)
            Constants.CONDENSED_RELAY2_GUI -> if (tile is CondensedRelayMK2Tile)
                return CondensedRelayMK2Container(player.inventory, tile)
            Constants.CONDENSED_RELAY3_GUI -> if (tile is CondensedRelayMK3Tile)
                return CondensedRelayMK3Container(player.inventory, tile)
            Constants.CONDENSED_RELAY4_GUI -> if (tile is CondensedRelayMK4Tile)
                return CondensedRelayMK4Container(player.inventory, tile)
            Constants.POWERFLOWER1_GUI -> if (tile is PowerFlowerMK1Tile)
                return PowerFlowerMK1Container(player.inventory, tile)
            Constants.POWERFLOWER2_GUI -> if (tile is PowerFlowerMK2Tile)
                return PowerFlowerMK1Container(player.inventory, tile)
            Constants.POWERFLOWER3_GUI -> if (tile is PowerFlowerMK3Tile)
                return PowerFlowerMK2Container(player.inventory, tile)
            Constants.POWERFLOWER4_GUI -> if (tile is PowerFlowerMK4Tile)
                return PowerFlowerMK2Container(player.inventory, tile)
            Constants.DM_FURNACE_GUI -> if (tile is DMFurnaceTile)
                return DMFurnaceContainer(player.inventory, tile)
            Constants.RM_FURNACE_GUI -> if (tile is RMFurnaceTile)
                return RMFurnaceContainer(player.inventory, tile)
            Constants.BM_FURNACE_GUI -> if (tile is BMFurnaceTile)
                return BMFurnaceContainer(player.inventory, tile)
            Constants.TRANSMUTE_STONE_GUI ->
                return TransmutationContainer(player.inventory, TransmutationInventory(player), null)
            Constants.TRANSMUTATION_GUI ->
                return TransmutationContainer(player.inventory, TransmutationInventory(player), hand)
        }
        return null
    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val tile = world.getTileEntity(BlockPos(x, y, z))
        val hand = if (ID == Constants.TRANSMUTATION_GUI) if (x == 1) EnumHand.OFF_HAND else EnumHand.MAIN_HAND else null

        when (ID) {
            Constants.CONDENSER_GUI -> if (tile is CondenserTile)
                return GUICondenser(player.inventory, tile)
            Constants.CONDENSER_MK2_GUI -> if (tile is CondenserMK2Tile)
                return GUICondenserMK2(player.inventory, tile)
            Constants.COLLECTOR1_GUI -> if (tile is CollectorMK1Tile)
                return GUICollectorMK1(player.inventory, tile)
            Constants.COLLECTOR2_GUI -> if (tile is CollectorMK2Tile)
                return GUICollectorMK2(player.inventory, tile)
            Constants.COLLECTOR3_GUI -> if (tile is CollectorMK3Tile)
                return GUICollectorMK3(player.inventory, tile)
            Constants.COLLECTOR4_GUI -> if (tile is CollectorMK4Tile)
                return GUICollectorMK4(player.inventory, tile)
            Constants.RELAY1_GUI -> if (tile is RelayMK1Tile)
                return GUIRelayMK1(player.inventory, tile)
            Constants.RELAY2_GUI -> if (tile is RelayMK2Tile)
                return GUIRelayMK2(player.inventory, tile)
            Constants.RELAY3_GUI -> if (tile is RelayMK3Tile)
                return GUIRelayMK3(player.inventory, tile)
            Constants.RELAY4_GUI -> if (tile is RelayMK4Tile)
                return GUIRelayMK4(player.inventory, tile)
            Constants.CONDENSED_RELAY1_GUI -> if (tile is CondensedRelayMK1Tile)
                return GUICondensedRelayMK1(player.inventory, tile)
            Constants.CONDENSED_RELAY2_GUI -> if (tile is CondensedRelayMK2Tile)
                return GUICondensedRelayMK2(player.inventory, tile)
            Constants.CONDENSED_RELAY3_GUI -> if (tile is CondensedRelayMK3Tile)
                return GUICondensedRelayMK3(player.inventory, tile)
            Constants.CONDENSED_RELAY4_GUI -> if (tile is CondensedRelayMK4Tile)
                return GUICondensedRelayMK4(player.inventory, tile)
            Constants.POWERFLOWER1_GUI -> if (tile is PowerFlowerMK1Tile)
                return GUIPowerFlowerMK1(player.inventory, tile)
            Constants.POWERFLOWER2_GUI -> if (tile is PowerFlowerMK2Tile)
                return GUIPowerFlowerMK2(player.inventory, tile)
            Constants.POWERFLOWER3_GUI -> if (tile is PowerFlowerMK3Tile)
                return GUIPowerFlowerMK3(player.inventory, tile)
            Constants.POWERFLOWER4_GUI -> if (tile is PowerFlowerMK4Tile)
                return GUIPowerFlowerMK4(player.inventory, tile)
            Constants.DM_FURNACE_GUI -> if (tile is DMFurnaceTile)
                return GUIDMFurnace(player.inventory, tile)
            Constants.RM_FURNACE_GUI -> if (tile is RMFurnaceTile)
                return GUIRMFurnace(player.inventory, tile)
            Constants.BM_FURNACE_GUI -> if (tile is BMFurnaceTile)
                return GUIBMFurnace(player.inventory, tile)
            Constants.TRANSMUTE_STONE_GUI ->
                return GUITransmutation(player.inventory, TransmutationInventory(player), null)
            Constants.TRANSMUTATION_GUI ->
                return GUITransmutation(player.inventory, TransmutationInventory(player), hand)
        }

        return null
    }
}