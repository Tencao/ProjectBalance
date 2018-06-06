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

import be.bluexin.saomclib.packets.AbstractServerPacketHandler
import com.tencao.projectbalance.handlers.getInternalCooldowns
import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.IThreadListener
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class TransmutePacket(): IMessage {

    private var stack: ItemStack = ItemStack.EMPTY
    private var requiredTime: Int = 0
    private var timePassed: Int = 0

    constructor(stack: ItemStack) : this() {
        this.stack = stack
    }

    constructor(stack: ItemStack, requiredTime: Int, timePassed: Int) : this() {
        this.stack = stack
        this.requiredTime = requiredTime
        this.timePassed = timePassed
    }

    override fun fromBytes(buf: ByteBuf) {
        stack = ByteBufUtils.readItemStack(buf)
        requiredTime = buf.readInt()
        timePassed = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf) {
        ByteBufUtils.writeItemStack(buf, stack)
        buf.writeInt(requiredTime)
        buf.writeInt(timePassed)
    }

    companion object {
        class Handler : AbstractServerPacketHandler<TransmutePacket>() {
            override fun handleServerPacket(player: EntityPlayer, message: TransmutePacket, ctx: MessageContext, mainThread: IThreadListener): IMessage? {
                mainThread.addScheduledTask {
                    player.getInternalCooldowns().setStack(message.stack)
                }
                return null
            }
        }
    }
}