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

package com.tencao.projectbalance.network

import be.bluexin.saomclib.packets.AbstractClientPacketHandler
import com.tencao.projectbalance.mapper.Defaults
import com.tencao.projectbalance.mapper.Graph
import com.tencao.projectbalance.mapper.ItemComponent
import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.IThreadListener
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class SyncComplexityPacket: IMessage {

    val values = mutableMapOf<ItemComponent, Int>()
    val complexities = mutableMapOf<ItemComponent, Int>()

    override fun fromBytes(buf: ByteBuf) {
        for (i in 0 until buf.readInt()){
            values[ItemComponent(ByteBufUtils.readItemStack(buf)).makeOutput()] = buf.readInt()
        }
        for (i in 0 until buf.readInt()){
            complexities[ItemComponent(ByteBufUtils.readItemStack(buf)).makeOutput()] = buf.readInt()
        }
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(Defaults.values.size)
        Defaults.values.forEach {
            ByteBufUtils.writeItemStack(buf, it.key.itemStack)
            buf.writeInt(it.value)
        }
        buf.writeInt(Defaults.complexities.size)
        Defaults.complexities.forEach {
            ByteBufUtils.writeItemStack(buf, it.key.itemStack)
            buf.writeInt(it.value)
        }
    }

    companion object {
        class Handler : AbstractClientPacketHandler<SyncComplexityPacket>(){
            override fun handleClientPacket(player: EntityPlayer, message: SyncComplexityPacket, ctx: MessageContext, mainThread: IThreadListener): IMessage? {
                mainThread.addScheduledTask {
                    Defaults.values.clear()
                    Defaults.values.putAll(message.values)
                    Defaults.complexities.clear()
                    Defaults.complexities.putAll(message.complexities)
                    Graph.clean()
                }
                return null
            }
        }
    }



}