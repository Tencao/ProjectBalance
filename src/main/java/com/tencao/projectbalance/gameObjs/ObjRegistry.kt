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

package com.tencao.projectbalance.gameObjs

import com.tencao.projectbalance.ProjectBCore
import com.tencao.projectbalance.gameObjs.blocks.*
import com.tencao.projectbalance.gameObjs.items.*
import com.tencao.projectbalance.gameObjs.items.armor.*
import com.tencao.projectbalance.gameObjs.items.itemBlocks.ItemMatterBlock
import com.tencao.projectbalance.gameObjs.items.itemBlocks.ItemPedestalBlock
import com.tencao.projectbalance.gameObjs.items.rings.*
import com.tencao.projectbalance.gameObjs.tile.*
import moze_intel.projecte.PECore
import moze_intel.projecte.gameObjs.ObjHandler
import moze_intel.projecte.gameObjs.items.itemBlocks.ItemCollectorBlock
import moze_intel.projecte.gameObjs.items.itemBlocks.ItemCondenserBlock
import moze_intel.projecte.gameObjs.items.itemBlocks.ItemRelayBlock
import net.minecraft.block.Block
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.ReflectionHelper
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import java.lang.reflect.Field
import java.lang.reflect.Modifier

@Mod.EventBusSubscriber(modid = ProjectBCore.MODID)
object ObjRegistry
{
    val condenser: Block = Condenser()
    val condenserMk2: Block = CondenserMK2()
    val pedestal: Block = Pedestal()
    val matterBlock: Block = MatterBlock()
    val collectorMK1: Block = Collector(1)
    val collectorMK2: Block = Collector(2)
    val collectorMK3: Block = Collector(3)
    val collectorMK4: Block = Collector(4)
    val relay: Block = Relay(1)
    val relayMK2: Block = Relay(2)
    val relayMK3: Block = Relay(3)
    val relayMK4: Block = Relay(4)
    val condensedRelayMK1: Block = CondensedRelay(1)
    val condensedRelayMK2: Block = CondensedRelay(2)
    val condensedRelayMK3: Block = CondensedRelay(3)
    val condensedRelayMK4: Block = CondensedRelay(4)
    val powerFlower: Block = PowerFlower(1)
    val powerFlowerMK2: Block = PowerFlower(2)
    val powerFlowerMK3: Block = PowerFlower(3)
    val powerFlowerMK4: Block = PowerFlower(4)

    val repairTalisman: Item = RepairTalisman()
    val kleinStars: Item = KleinStar()
    val matter: Item = Matter()

    val dmHelmet: Item = DMArmor(EntityEquipmentSlot.HEAD)
    val dmChest: Item = DMArmor(EntityEquipmentSlot.CHEST)
    val dmLegs: Item = DMArmor(EntityEquipmentSlot.LEGS)
    val dmFeet: Item = DMArmor(EntityEquipmentSlot.FEET)

    val rmHelmet: Item = RMArmor(EntityEquipmentSlot.HEAD)
    val rmChest: Item = RMArmor(EntityEquipmentSlot.CHEST)
    val rmLegs: Item = RMArmor(EntityEquipmentSlot.LEGS)
    val rmFeet: Item = RMArmor(EntityEquipmentSlot.FEET)

    val gemHelmet: Item = GemHelmet()
    val gemChest: Item = GemChest()
    val gemLegs: Item = GemLegs()
    val gemFeet: Item = GemFeet()

    val angelSmite: Item = ArchangelSmite()
    val harvestGod: Item = HarvestGoddess()
    val ignition: Item = Ignition()
    val zero: Item = Zero()
    val swrg: Item = SWRG()
    val timeWatch: Item = TimeWatch()
    val everTide: Item = EvertideAmulet()
    val volcanite: Item = VolcaniteAmulet()
    val arcana: Item = Arcana()

    val bodyStone: Item = BodyStone()
    val soulStone: Item = SoulStone()
    val lifeStone: Item = LifeStone()

    val tome: Item = Tome()

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun registerBlocks(evt: RegistryEvent.Register<Block>) {
        val r = evt.registry
        registerObj<Block>(r, collectorMK1, "collector_mk1")
        setNewValue("collectorMK1", collectorMK1)
        registerObj<Block>(r, collectorMK2, "collector_mk2")
        setNewValue("collectorMK2", collectorMK2)
        registerObj<Block>(r, collectorMK3, "collector_mk3")
        setNewValue("collectorMK3", collectorMK3)
        registerObj<Block>(r, collectorMK4, "collector_mk4")
        registerObj<Block>(r, condenser, "condenser_mk1")
        setNewValue("condenser", condenser)
        registerObj<Block>(r, condenserMk2, "condenser_mk2")
        setNewValue("condenserMk2", condenserMk2)
        registerObj<Block>(r, pedestal, "pedestal")
        setNewValue("dmPedestal", pedestal)
        registerObj<Block>(r, matterBlock, "matter_block")
        setNewValue("matterBlock", matterBlock)
        registerObj<Block>(r, relay, "relay_mk1")
        setNewValue("relay", relay)
        registerObj<Block>(r, relayMK2, "relay_mk2")
        setNewValue("relayMK2", relayMK2)
        registerObj<Block>(r, relayMK3, "relay_mk3")
        setNewValue("relayMK3", relayMK3)
        registerObj<Block>(r, relayMK4, "relay_mk4")
        registerObj<Block>(r, condensedRelayMK1, "condensed_relay_mk1")
        registerObj<Block>(r, condensedRelayMK2, "condensed_relay_mk2")
        registerObj<Block>(r, condensedRelayMK3, "condensed_relay_mk3")
        registerObj<Block>(r, condensedRelayMK4, "condensed_relay_mk4")
        registerObj<Block>(r, powerFlower, "power_flower_mk1")
        registerObj<Block>(r, powerFlowerMK2, "power_flower_mk2")
        registerObj<Block>(r, powerFlowerMK3, "power_flower_mk3")
        registerObj<Block>(r, powerFlowerMK4, "power_flower_mk4")
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun registerItems(evt: RegistryEvent.Register<Item>) {
        val r = evt.registry
        registerObj<Item>(r, ItemCollectorBlock(collectorMK1), collectorMK1.registryName!!)
        registerObj<Item>(r, ItemCollectorBlock(collectorMK2), collectorMK2.registryName!!)
        registerObj<Item>(r, ItemCollectorBlock(collectorMK3), collectorMK3.registryName!!)
        registerObj<Item>(r, ItemCollectorBlock(collectorMK4), collectorMK4.registryName!!)
        registerObj<Item>(r, ItemCondenserBlock(condenser), condenser.registryName!!)
        registerObj<Item>(r, ItemBlock(condenserMk2), condenserMk2.registryName!!)
        registerObj<Item>(r, ItemPedestalBlock(pedestal), pedestal.registryName!!)
        registerObj<Item>(r, ItemMatterBlock(matterBlock), matterBlock.registryName!!)
        registerObj<Item>(r, ItemRelayBlock(relay), relay.registryName!!)
        registerObj<Item>(r, ItemRelayBlock(relayMK2), relayMK2.registryName!!)
        registerObj<Item>(r, ItemRelayBlock(relayMK3), relayMK3.registryName!!)
        registerObj<Item>(r, ItemRelayBlock(relayMK4), relayMK4.registryName!!)
        registerObj<Item>(r, ItemBlock(condensedRelayMK1), condensedRelayMK1.registryName!!)
        registerObj<Item>(r, ItemBlock(condensedRelayMK2), condensedRelayMK2.registryName!!)
        registerObj<Item>(r, ItemBlock(condensedRelayMK3), condensedRelayMK3.registryName!!)
        registerObj<Item>(r, ItemBlock(condensedRelayMK4), condensedRelayMK4.registryName!!)
        registerObj<Item>(r, ItemBlock(powerFlower), powerFlower.registryName!!)
        registerObj<Item>(r, ItemBlock(powerFlowerMK2), powerFlowerMK2.registryName!!)
        registerObj<Item>(r, ItemBlock(powerFlowerMK3), powerFlowerMK3.registryName!!)
        registerObj<Item>(r, ItemBlock(powerFlowerMK4), powerFlowerMK4.registryName!!)

        registerObj<Item>(r, repairTalisman, repairTalisman.unlocalizedName)
        setNewValue("repairTalisman", repairTalisman)
        registerObj<Item>(r, kleinStars, kleinStars.unlocalizedName)
        setNewValue("kleinStars", kleinStars)
        registerObj<Item>(r, matter, matter.unlocalizedName)
        setNewValue("matter", matter)

        registerObj<Item>(r, dmHelmet, dmHelmet.unlocalizedName)
        setNewValue("dmHelmet", dmHelmet)
        registerObj<Item>(r, dmChest, dmChest.unlocalizedName)
        setNewValue("dmChest", dmChest)
        registerObj<Item>(r, dmLegs, dmLegs.unlocalizedName)
        setNewValue("dmLegs", dmLegs)
        registerObj<Item>(r, dmFeet, dmFeet.unlocalizedName)
        setNewValue("dmFeet", dmFeet)

        registerObj<Item>(r, rmHelmet, rmHelmet.unlocalizedName)
        setNewValue("rmHelmet", rmHelmet)
        registerObj<Item>(r, rmChest, rmChest.unlocalizedName)
        setNewValue("rmChest", rmChest)
        registerObj<Item>(r, rmLegs, rmLegs.unlocalizedName)
        setNewValue("rmLegs", rmLegs)
        registerObj<Item>(r, rmFeet, rmFeet.unlocalizedName)
        setNewValue("rmFeet", rmFeet)

        registerObj<Item>(r, gemHelmet, gemHelmet.unlocalizedName)
        setNewValue("gemHelmet", gemHelmet)
        registerObj<Item>(r, gemChest, gemChest.unlocalizedName)
        setNewValue("gemChest", gemChest)
        registerObj<Item>(r, gemLegs, gemLegs.unlocalizedName)
        setNewValue("gemLegs", gemLegs)
        registerObj<Item>(r, gemFeet, gemFeet.unlocalizedName)
        setNewValue("gemFeet", gemFeet)

        registerObj<Item>(r, angelSmite, angelSmite.unlocalizedName)
        setNewValue("angelSmite", angelSmite)
        registerObj<Item>(r, harvestGod, harvestGod.unlocalizedName)
        setNewValue("harvestGod", harvestGod)
        registerObj<Item>(r, ignition, ignition.unlocalizedName)
        setNewValue("ignition", ignition)
        registerObj<Item>(r, zero, zero.unlocalizedName)
        setNewValue("zero", zero)
        registerObj<Item>(r, swrg, swrg.unlocalizedName)
        setNewValue("swrg", swrg)
        registerObj<Item>(r, timeWatch, timeWatch.unlocalizedName)
        setNewValue("timeWatch", timeWatch)
        registerObj<Item>(r, arcana, arcana.unlocalizedName)
        setNewValue("arcana", arcana)

        registerObj<Item>(r, bodyStone, bodyStone.unlocalizedName)
        setNewValue("bodyStone", bodyStone)
        registerObj<Item>(r, soulStone, soulStone.unlocalizedName)
        setNewValue("soulStone", soulStone)
        registerObj<Item>(r, lifeStone, lifeStone.unlocalizedName)
        setNewValue("lifeStone", lifeStone)

        registerObj<Item>(r, everTide, everTide.unlocalizedName)
        setNewValue("everTide", everTide)
        registerObj<Item>(r, volcanite, volcanite.unlocalizedName)
        setNewValue("volcanite", volcanite)

        registerObj<Item>(r, tome, tome.unlocalizedName)
        setNewValue("tome", tome)

    }

    fun setNewValue(name: String, obj: Block){
        val field = ReflectionHelper.findField(ObjHandler::class.java, name).also { it.isAccessible = true }
        Field::class.java.getDeclaredField("modifiers").also { it.isAccessible = true }.setInt(field, field.modifiers.and(Modifier.FINAL).inv())
        field.set(null, obj)
    }

    fun setNewValue(name: String, obj: Item){
        val field = ReflectionHelper.findField(ObjHandler::class.java, name).also { it.isAccessible = true }
        Field::class.java.getDeclaredField("modifiers").also { it.isAccessible = true }.setInt(field, field.modifiers.and(Modifier.FINAL).inv())
        field.set(null, obj)
    }

    fun register() {
        //Tile Entities
        GameRegistry.registerTileEntity(CondenserTile::class.java, "CondenserTile")
        GameRegistry.registerTileEntity(CondenserMK2Tile::class.java, "CondenserMK2Tile")
        GameRegistry.registerTileEntity(CollectorMK1Tile::class.java, "CollectorMK1Tile")
        GameRegistry.registerTileEntity(CollectorMK2Tile::class.java, "CollectorMK2Tile")
        GameRegistry.registerTileEntity(CollectorMK3Tile::class.java, "CollectorMK3Tile")
        GameRegistry.registerTileEntity(CollectorMK4Tile::class.java, "CollectorMK4Tile")
        GameRegistry.registerTileEntity(RelayMK1Tile::class.java, "RelayMK1Tile")
        GameRegistry.registerTileEntity(RelayMK2Tile::class.java, "RelayMK2Tile")
        GameRegistry.registerTileEntity(RelayMK3Tile::class.java, "RelayMK3Tile")
        GameRegistry.registerTileEntity(RelayMK4Tile::class.java, "RelayMK4Tile")
        GameRegistry.registerTileEntity(CondensedRelayMK1Tile::class.java, "CondensedRelayMK1Tile")
        GameRegistry.registerTileEntity(CondensedRelayMK2Tile::class.java, "CondensedRelayMK2Tile")
        GameRegistry.registerTileEntity(CondensedRelayMK3Tile::class.java, "CondensedRelayMK3Tile")
        GameRegistry.registerTileEntity(CondensedRelayMK4Tile::class.java, "CondensedRelayMK4Tile")
        GameRegistry.registerTileEntity(PowerFlowerMK1Tile::class.java, "PowerFlowerMK1Tile")
        GameRegistry.registerTileEntity(PowerFlowerMK2Tile::class.java, "PowerFlowerMK2Tile")
        GameRegistry.registerTileEntity(PowerFlowerMK3Tile::class.java, "PowerFlowerMK3Tile")
        GameRegistry.registerTileEntity(PowerFlowerMK4Tile::class.java, "PowerFlowerMK4Tile")
        GameRegistry.registerTileEntity(DMPedestalTile::class.java, "DMPedestalTile")
        GameRegistry.registerTileEntity(RMPedestalTile::class.java, "RMPedestalTile")
        GameRegistry.registerTileEntity(BMPedestalTile::class.java, "BMPedestalTile")
    }

    private fun <V : IForgeRegistryEntry<V>> registerObj(registry: IForgeRegistry<V>, o: IForgeRegistryEntry<V>, name: String) {
        registerObj(registry, o, ResourceLocation(PECore.MODID, name))
    }

    private fun <V : IForgeRegistryEntry<V>> registerObj(registry: IForgeRegistry<V>, o: IForgeRegistryEntry<V>, name: ResourceLocation) {
         registry.register(o.setRegistryName(name))
    }
}