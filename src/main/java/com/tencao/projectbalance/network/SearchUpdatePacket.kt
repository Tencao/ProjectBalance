package com.tencao.projectbalance.network

import be.bluexin.saomclib.packets.AbstractServerPacketHandler
import com.tencao.projectbalance.gameObjs.container.TransmutationContainer
import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.IThreadListener
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

/**
 * Duplicate from ProjectE to be compatible with the new Container
 */
class SearchUpdatePacket(): IMessage {

    var slot: Int = 0
    var itemStack: ItemStack = ItemStack.EMPTY

    constructor(slot: Int, itemStack: ItemStack): this() {
        this.slot = slot
        this.itemStack = itemStack.copy()
    }

    override fun fromBytes(buf: ByteBuf) {
        this.slot = buf.readInt()
        this.itemStack = ByteBufUtils.readItemStack(buf)
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(this.slot)
        ByteBufUtils.writeItemStack(buf, this.itemStack)
    }


    companion object {
        class Handler : AbstractServerPacketHandler<SearchUpdatePacket>() {
            override fun handleServerPacket(player: EntityPlayer, message: SearchUpdatePacket, ctx: MessageContext, mainThread: IThreadListener): IMessage? {
                mainThread.addScheduledTask {
                    if (ctx.serverHandler.player.openContainer is TransmutationContainer) {
                        val container = ctx.serverHandler.player.openContainer as TransmutationContainer
                        container.transmutationInventory.writeIntoOutputSlot(message.slot, message.itemStack)
                    }
                }
                return null
            }

        }
    }
}