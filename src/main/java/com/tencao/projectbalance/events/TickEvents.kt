package com.tencao.projectbalance.events

import com.tencao.projectbalance.handlers.InternalCooldowns
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

object TickEvents {

    @SubscribeEvent
    fun playerTick(event: TickEvent.PlayerTickEvent) {
        if (event.phase == TickEvent.Phase.END) {
            event.player.getCapability<InternalCooldowns>(InternalCooldowns.CAPABILITY, null)!!.tick()
        }
    }
}