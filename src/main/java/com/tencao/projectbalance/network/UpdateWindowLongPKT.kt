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
import com.tencao.projectbalance.gameObjs.container.IWindowLongProp
import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.IThreadListener
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext


class UpdateWindowLongPKT(): IMessage {

    private var windowId: Short = 0
    private var propId: Short = 0
    private var propVal: Long = 0

    constructor(windowId: Short, propId: Short, propVal: Long): this() {
        this.windowId = windowId
        this.propId = propId
        this.propVal = propVal
    }

    override fun fromBytes(buf: ByteBuf) {
        windowId = buf.readUnsignedByte()
        propId = buf.readShort()
        propVal = buf.readLong()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeByte(windowId.toInt())
        buf.writeShort(propId.toInt())
        buf.writeLong(propVal)
    }

    companion object {
        class Handler : AbstractClientPacketHandler<UpdateWindowLongPKT>(){
            override fun handleClientPacket(player: EntityPlayer, message: UpdateWindowLongPKT, ctx: MessageContext, mainThread: IThreadListener): IMessage? {
                mainThread.addScheduledTask {
                    if (player.openContainer is IWindowLongProp && player.openContainer.windowId == message.windowId.toInt()) {
                        (player.openContainer as IWindowLongProp).updateProgressBar(message.propId.toInt(), message.propVal)
                    }
                }
                return null
            }
        }
    }
}