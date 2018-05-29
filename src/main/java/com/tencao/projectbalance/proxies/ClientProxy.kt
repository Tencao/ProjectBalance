package com.tencao.projectbalance.proxies

import com.tencao.projectbalance.ProjectBCore
import com.tencao.projectbalance.events.PedestalEvent
import com.tencao.projectbalance.events.ToolTipEvent
import com.tencao.projectbalance.gameObjs.ObjRegistry
import com.tencao.projectbalance.gameObjs.state.EnumMatterType
import com.tencao.projectbalance.gameObjs.tile.*
import com.tencao.projectbalance.rendering.CondenserMK2Renderer
import com.tencao.projectbalance.rendering.CondenserRenderer
import com.tencao.projectbalance.rendering.PedestalRenderer
import com.tencao.projectbalance.rendering.PowerFlowerRenderer
import moze_intel.projecte.PECore
import moze_intel.projecte.api.state.PEStateProps
import net.minecraft.block.Block
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.StateMap
import net.minecraft.item.Item
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.fml.relauncher.Side

@Mod.EventBusSubscriber(modid = ProjectBCore.MODID)
class ClientProxy: IProxy {

    override fun registerRenderers() {
        // Tile Entity
        ClientRegistry.bindTileEntitySpecialRenderer<CondenserTile>(CondenserTile::class.java, CondenserRenderer())
        ClientRegistry.bindTileEntitySpecialRenderer<CondenserMK2Tile>(CondenserMK2Tile::class.java, CondenserMK2Renderer())
        ClientRegistry.bindTileEntitySpecialRenderer(PowerFlowerMK1Tile::class.java, PowerFlowerRenderer(1))
        ClientRegistry.bindTileEntitySpecialRenderer(PowerFlowerMK2Tile::class.java, PowerFlowerRenderer(2))
        ClientRegistry.bindTileEntitySpecialRenderer(PowerFlowerMK3Tile::class.java, PowerFlowerRenderer(3))
        ClientRegistry.bindTileEntitySpecialRenderer(PowerFlowerMK4Tile::class.java, PowerFlowerRenderer(4))
        ClientRegistry.bindTileEntitySpecialRenderer<DMPedestalTile>(DMPedestalTile::class.java, PedestalRenderer())
    }

    override fun registerEvents() {
        MinecraftForge.EVENT_BUS.register(ClientProxy)
        MinecraftForge.EVENT_BUS.register(ObjRegistry)
        MinecraftForge.EVENT_BUS.register(PedestalEvent)
        MinecraftForge.EVENT_BUS.register(ToolTipEvent)
    }

    companion object {

        @SubscribeEvent(priority = EventPriority.LOWEST)
        fun registerModels(evt: ModelRegistryEvent) {
            // Blocks with special needs
            ModelLoader.setCustomStateMapper(
                    ObjRegistry.condenser,
                    StateMap.Builder().ignore(PEStateProps.FACING).build()
            )

            ModelLoader.setCustomStateMapper(
                    ObjRegistry.condenserMk2,
                    StateMap.Builder().ignore(PEStateProps.FACING).build()
            )

            ModelLoader.setCustomStateMapper(
                    ObjRegistry.powerFlower,
                    StateMap.Builder().ignore(PEStateProps.FACING).build()
            )

            ModelLoader.setCustomStateMapper(
                    ObjRegistry.powerFlowerMK2,
                    StateMap.Builder().ignore(PEStateProps.FACING).build()
            )

            ModelLoader.setCustomStateMapper(
                    ObjRegistry.powerFlowerMK3,
                    StateMap.Builder().ignore(PEStateProps.FACING).build()
            )

            ModelLoader.setCustomStateMapper(
                    ObjRegistry.powerFlowerMK4,
                    StateMap.Builder().ignore(PEStateProps.FACING).build()
            )

            // Items that have different properties or textures per meta value.
            registerMatter()
            registerKlein()

            // Normal items that have no variants / meta values. The json models are named "item.pe_<name>" because we register items with unlocal name.
            // Which was a dumb decision made by somebody way back when. Oh well.
            registerItem(ObjRegistry.bodyStone)
            registerItem(ObjRegistry.soulStone)
            registerItem(ObjRegistry.lifeStone)
            registerItem(ObjRegistry.harvestGod)
            registerItem(ObjRegistry.timeWatch)
            registerItem(ObjRegistry.ignition)
            registerItem(ObjRegistry.zero)

            registerItem(ObjRegistry.repairTalisman)
            registerItem(ObjRegistry.tome)
            registerItem(ObjRegistry.everTide)
            registerItem(ObjRegistry.volcanite)
            registerItem(ObjRegistry.angelSmite)
            ModelLoader.setCustomModelResourceLocation(ObjRegistry.angelSmite, 1, ModelResourceLocation(ObjRegistry.angelSmite.registryName!!, "inventory"))

            registerItem(ObjRegistry.dmHelmet)
            registerItem(ObjRegistry.dmChest)
            registerItem(ObjRegistry.dmLegs)
            registerItem(ObjRegistry.dmFeet)

            registerItem(ObjRegistry.rmHelmet)
            registerItem(ObjRegistry.rmChest)
            registerItem(ObjRegistry.rmLegs)
            registerItem(ObjRegistry.rmFeet)

            registerItem(ObjRegistry.gemHelmet)
            registerItem(ObjRegistry.gemChest)
            registerItem(ObjRegistry.gemLegs)
            registerItem(ObjRegistry.gemFeet)

            // Item models for blocks
            registerBlock(ObjRegistry.collectorMK1)
            registerBlock(ObjRegistry.collectorMK2)
            registerBlock(ObjRegistry.collectorMK3)
            registerBlock(ObjRegistry.collectorMK4)
            registerBlock(ObjRegistry.condenser)
            registerBlock(ObjRegistry.condenserMk2)
            registerPedestals()
            registerBlock(ObjRegistry.relay)
            registerBlock(ObjRegistry.relayMK2)
            registerBlock(ObjRegistry.relayMK3)
            registerBlock(ObjRegistry.relayMK4)
            registerBlock(ObjRegistry.condensedRelayMK1)
            registerBlock(ObjRegistry.condensedRelayMK2)
            registerBlock(ObjRegistry.condensedRelayMK3)
            registerBlock(ObjRegistry.condensedRelayMK4)
            registerBlock(ObjRegistry.powerFlower)
            registerBlock(ObjRegistry.powerFlowerMK2)
            registerBlock(ObjRegistry.powerFlowerMK3)
            registerBlock(ObjRegistry.powerFlowerMK4)
        }

        private fun registerBlock(b: Block) {
            val name = ForgeRegistries.BLOCKS.getKey(b)!!.toString()
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), 0, ModelResourceLocation(name, "inventory"))
        }

        private fun registerItem(i: Item) {
            registerItem(i, 0)
        }

        private fun registerItem(i: Item, meta: Int) {
            val name = ForgeRegistries.ITEMS.getKey(i)!!.toString()
            ModelLoader.setCustomModelResourceLocation(i, meta, ModelResourceLocation(name, "inventory"))
        }

        private fun registerMatter() {
            for (m in EnumMatterType.values()) {
                ModelLoader.setCustomModelResourceLocation(ObjRegistry.matter, m.ordinal, ModelResourceLocation(PECore.MODID + ":" + m.getName(), "inventory"))

                val name = ForgeRegistries.BLOCKS.getKey(ObjRegistry.matterBlock)!!.toString()
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ObjRegistry.matterBlock), m.ordinal, ModelResourceLocation(name, "tier=" + m.getName()))
            }
        }

        private fun registerPedestals() {
            for (m in EnumMatterType.values()) {
                val name = ForgeRegistries.BLOCKS.getKey(ObjRegistry.pedestal)!!.toString()
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ObjRegistry.pedestal), m.ordinal, ModelResourceLocation(name, "tier=" + m.getName()))
            }
        }

        private fun registerKlein() {
            for (e in moze_intel.projecte.gameObjs.items.KleinStar.EnumKleinTier.values()) {
                ModelLoader.setCustomModelResourceLocation(ObjRegistry.kleinStars, e.ordinal, ModelResourceLocation(PECore.MODID + ":" + "stars/klein_star_" + e.toString().toLowerCase(), "inventory"))
            }
        }
    }


}