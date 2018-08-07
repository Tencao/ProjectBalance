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

package com.tencao.projectbalance

import be.bluexin.saomclib.capabilities.CapabilitiesHandler
import be.bluexin.saomclib.packets.PacketPipeline
import com.tencao.projectbalance.commands.CommandBase
import com.tencao.projectbalance.commands.RegisterCMD
import com.tencao.projectbalance.commands.RemoveCMD
import com.tencao.projectbalance.config.MapperConfig
import com.tencao.projectbalance.gameObjs.ObjRegistry
import com.tencao.projectbalance.handlers.InternalCooldowns
import com.tencao.projectbalance.mapper.Graph
import com.tencao.projectbalance.network.SearchUpdatePacket
import com.tencao.projectbalance.network.SyncComplexityPacket
import com.tencao.projectbalance.network.TransmutePacket
import com.tencao.projectbalance.proxies.IProxy
import com.tencao.projectbalance.utils.GuiHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(modid = ProjectBCore.MODID, name = ProjectBCore.NAME, version = ProjectBCore.VERSION, dependencies = ProjectBCore.DEPS)
object ProjectBCore {
    const val MODID = "projectbalance"
    const val VERSION = "1.12.2-0.2.1"
    const val NAME = "ProjectBalance"
    const val DEPS = "required-after:forge@[14.23.1.2594,);" +
            "required-after:forgelin@[1.6.0,);" +
            "required-after:saomclib@[1.2.0.7,);" +
            "required-after:projecte@[1.12-PE1.3.0,);"

    @SidedProxy(clientSide = "com.tencao.projectbalance.proxies.ClientProxy", serverSide = "com.tencao.projectbalance.proxies.ServerProxy")
    lateinit var proxy: IProxy

    val LOGGER: Logger = LogManager.getLogger(MODID)

    @JvmStatic
    @Mod.InstanceFactory
    fun shenanigan() = this

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        proxy.registerEvents()
        CapabilitiesHandler.registerEntityCapability(InternalCooldowns::class.java, InternalCooldowns.DummyStorage) { it is EntityPlayer }

        NetworkRegistry.INSTANCE.registerGuiHandler(this, GuiHandler())
        PacketPipeline.registerMessage(TransmutePacket::class.java, TransmutePacket.Companion.Handler::class.java)
        PacketPipeline.registerMessage(SearchUpdatePacket::class.java, SearchUpdatePacket.Companion.Handler::class.java)
        PacketPipeline.registerMessage(SyncComplexityPacket::class.java, SyncComplexityPacket.Companion.Handler::class.java)

        ObjRegistry.register()

        proxy.registerRenderers()
        MapperConfig.preInit(event)
    }

    @Mod.EventHandler
    fun loadComplete(event: FMLLoadCompleteEvent) {
        Graph.make()
    }

    @Mod.EventHandler
    fun serverStart(event: FMLServerStartingEvent) {
        event.registerServerCommand(CommandBase)
        event.registerServerCommand(RegisterCMD)
        event.registerServerCommand(RemoveCMD)
    }
}