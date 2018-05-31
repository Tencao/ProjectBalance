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

import com.tencao.projectbalance.gameObjs.ObjRegistry
import com.tencao.projectbalance.handlers.InternalCooldowns
import com.tencao.projectbalance.mapper.Graph
import com.tencao.projectbalance.proxies.IProxy
import com.tencao.projectbalance.utils.GuiHandler
import moze_intel.projecte.utils.DummyIStorage
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(modid = ProjectBCore.MODID, name = ProjectBCore.NAME, version = ProjectBCore.VERSION, dependencies = ProjectBCore.DEPS)
object ProjectBCore {
    const val MODID = "projectbalance"
    const val VERSION = "1.12.2-0.0.1"
    const val NAME = "ProjectBalance"
    const val DEPS = "required-after:forge@[14.23.1.2594,);" +
            "required-after:forgelin@[1.6.0,);" +
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
        CapabilityManager.INSTANCE.register<InternalCooldowns>(InternalCooldowns::class.java, DummyIStorage<InternalCooldowns>(), { InternalCooldowns(null) })

        NetworkRegistry.INSTANCE.registerGuiHandler(this, GuiHandler())

        ObjRegistry.register()

        proxy.registerRenderers()
    }

    @Mod.EventHandler
    fun loadComplete(event: FMLLoadCompleteEvent) {
        Graph.make()
    }
}