package com.tencao.projectbalance.proxies

import com.tencao.projectbalance.events.PedestalEvent
import com.tencao.projectbalance.events.TickEvents
import com.tencao.projectbalance.events.ToolTipEvent
import com.tencao.projectbalance.gameObjs.ObjRegistry
import net.minecraftforge.common.MinecraftForge

class ServerProxy: IProxy {

    override fun registerRenderers() {}

    override fun registerEvents() {
        MinecraftForge.EVENT_BUS.register(ObjRegistry)
        MinecraftForge.EVENT_BUS.register(PedestalEvent)
        MinecraftForge.EVENT_BUS.register(ToolTipEvent)
        MinecraftForge.EVENT_BUS.register(TickEvents)
    }
}