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

package com.tencao.projectbalance.events

import be.bluexin.saomclib.packets.PacketPipeline
import com.tencao.projectbalance.handlers.InternalCooldowns
import com.tencao.projectbalance.network.SyncComplexityPacket
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

object TickEvents {

    @SubscribeEvent
    fun playerTick(event: TickEvent.PlayerTickEvent) {
        if (event.phase == TickEvent.Phase.END) {
            event.player.getCapability<InternalCooldowns>(InternalCooldowns.CAPABILITY, null)!!.tick()
        }
    }

    @SubscribeEvent
    fun playerConnect(event: PlayerEvent.PlayerLoggedInEvent){
        val player = event.player as EntityPlayerMP
        PacketPipeline.sendTo(SyncComplexityPacket(), player)
    }
}