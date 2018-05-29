package com.tencao.projectbalance

import com.tencao.projectbalance.gameObjs.ObjRegistry
import com.tencao.projectbalance.handlers.InternalCooldowns
import com.tencao.projectbalance.proxies.IProxy
import com.tencao.projectbalance.utils.GuiHandler
import moze_intel.projecte.utils.DummyIStorage
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
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
}