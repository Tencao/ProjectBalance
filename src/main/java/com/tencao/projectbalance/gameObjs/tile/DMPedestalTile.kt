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

package com.tencao.projectbalance.gameObjs.tile

import com.google.common.collect.Lists
import com.tencao.projectbalance.config.ProjectBConfig
import com.tencao.projectbalance.events.PedestalEvent
import com.tencao.projectbalance.gameObjs.items.armor.ArmorBase
import moze_intel.projecte.api.PESounds
import moze_intel.projecte.api.item.IItemEmc
import moze_intel.projecte.api.item.IPedestalItem
import moze_intel.projecte.api.tile.IEmcAcceptor
import moze_intel.projecte.api.tile.IEmcProvider
import moze_intel.projecte.gameObjs.items.TimeWatch
import moze_intel.projecte.gameObjs.items.Tome
import moze_intel.projecte.gameObjs.items.rings.HarvestGoddess
import moze_intel.projecte.utils.Constants
import moze_intel.projecte.utils.WorldHelper
import net.minecraft.block.IGrowable
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldServer
import net.minecraftforge.common.IPlantable
import net.minecraftforge.common.IShearable
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import java.util.*
import kotlin.collections.LinkedHashSet
import kotlin.math.min

open class DMPedestalTile: TileEmc, IEmcAcceptor {

    private val RANGE: Int
    private var isActive = false
    private var isConnected = false
    private var inventory = this.StackHandler(1)
    var nearbyPedestals: LinkedHashSet<DMPedestalTile> = LinkedHashSet()
    var nearbyBlocks: LinkedHashSet<BlockPos> = LinkedHashSet()
    private var particleCooldown = 10
    private var activityCooldown = 0
    var previousRedstoneState = false
    var centeredX: Double = 0.toDouble()
    var centeredY: Double = 0.toDouble()
    var centeredZ: Double = 0.toDouble()

    constructor() : super(ProjectBConfig.tweaks.DMPedestalMax){
        this.RANGE = 4
    }

    constructor(range: Int, maxEMC: Long): super(maxEMC){
        this.RANGE = range
    }

    override fun update() {
        centeredX = pos.x + 0.5
        centeredY = pos.y + 0.5
        centeredZ = pos.z + 0.5

        checkConnected()


        if (!inventory.getStackInSlot(0).isEmpty) {
            val item = inventory.getStackInSlot(0).item
            if (item is IItemEmc && isConnected() && !getActive())
                setActive(true)
            if (getActive() && activityCooldown <= 0 && item is IPedestalItem) {
                (item as IPedestalItem).updateInPedestal(world, getPos())
            } else {
                activityCooldown--
                setActive(false)
            }
        }
        if (getActive()) {

            if (particleCooldown <= 0) {
                spawnParticles()
                particleCooldown = 10
            } else {
                particleCooldown--
            }
        }

    }

    fun isConnected(): Boolean {
        return isConnected
    }

    private fun checkConnected() {
        val tiles = WorldHelper.getAdjacentTileEntitiesMapped(world, this)

        for (entry in tiles.entries) {
            if (entry.value is IEmcProvider) {
                isConnected = true
                return
            }
        }
        isConnected = false
    }

    fun getItem(): Item {
        return inventory.getStackInSlot(0).item
    }

    fun requiredEMC(): Long {
        return if (getItem() is IItemEmc) {
            (getItem() as IItemEmc).getMaximumEmc(inventory.getStackInSlot(0)) - (getItem() as IItemEmc).getStoredEmc(inventory.getStackInSlot(0))
        } else maximumEMC - currentEMC
    }

    fun hasRequiredEMC(emcCost: Long, simulate: Boolean): Boolean {
        if (getItem() is IItemEmc) {
            if (emcCost <= (getItem() as IItemEmc).getStoredEmc(inventory.getStackInSlot(0))) {
                if (!simulate)
                    (getItem() as IItemEmc).extractEmc(inventory.getStackInSlot(0), emcCost)
                return true
            }
        } else if (emcCost <= currentEMC) {
            if (!simulate)
                currentEMC -= emcCost
            return true
        }
        return false
    }

    private fun spawnEMCParticles(distance: BlockPos, pos: BlockPos) {

        val i = distance.x
        val j = distance.y
        val k = distance.z

        val rand = world.rand
        if (rand.nextFloat() <= 0.60f) {
            (world as WorldServer).spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, pos.x.toDouble() + 0.5,
                    pos.y.toDouble() + 2.0, pos.z.toDouble() + 0.5,
                    0, (i.toFloat() + rand.nextFloat()).toDouble() - 0.5,
                    (j.toFloat() - rand.nextFloat() - 1.0f).toDouble(), (k.toFloat() + rand.nextFloat()).toDouble() - 0.5,
                    1.0, EnumParticleTypes.ENCHANTMENT_TABLE.argumentCount)
        }
    }

    private fun spawnParticles() {
        val x = pos.x
        val y = pos.y
        val z = pos.z

        world.spawnParticle(EnumParticleTypes.FLAME, x + 0.2, y + 0.3, z + 0.2, 0.0, 0.0, 0.0)
        world.spawnParticle(EnumParticleTypes.FLAME, x + 0.2, y + 0.3, z + 0.5, 0.0, 0.0, 0.0)
        world.spawnParticle(EnumParticleTypes.FLAME, x + 0.2, y + 0.3, z + 0.8, 0.0, 0.0, 0.0)
        world.spawnParticle(EnumParticleTypes.FLAME, x + 0.5, y + 0.3, z + 0.2, 0.0, 0.0, 0.0)
        world.spawnParticle(EnumParticleTypes.FLAME, x + 0.5, y + 0.3, z + 0.8, 0.0, 0.0, 0.0)
        world.spawnParticle(EnumParticleTypes.FLAME, x + 0.8, y + 0.3, z + 0.2, 0.0, 0.0, 0.0)
        world.spawnParticle(EnumParticleTypes.FLAME, x + 0.8, y + 0.3, z + 0.5, 0.0, 0.0, 0.0)
        world.spawnParticle(EnumParticleTypes.FLAME, x + 0.8, y + 0.3, z + 0.8, 0.0, 0.0, 0.0)

        val rand = world.rand
        for (i in 0..2) {
            val j = rand.nextInt(2) * 2 - 1
            val k = rand.nextInt(2) * 2 - 1
            val d0 = pos.x.toDouble() + 0.5 + 0.25 * j.toDouble()
            val d1 = (pos.y.toFloat() + rand.nextFloat()).toDouble()
            val d2 = pos.z.toDouble() + 0.5 + 0.25 * k.toDouble()
            val d3 = (rand.nextFloat() * j.toFloat()).toDouble()
            val d4 = (rand.nextFloat().toDouble() - 0.5) * 0.125
            val d5 = (rand.nextFloat() * k.toFloat()).toDouble()
            world.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5)
        }
    }

    fun scanNearbyPedestals() {
        val tiles = nearbyPedestals.asSequence().filter { it.requiresEMC() && it.getActive() &&
                (isConnected && it.getItem() is IItemEmc && !it.isConnected) ||
                (it.getItem() !is IItemEmc && it.getItem() is IPedestalItem) }

        val list = world.getEntitiesWithinAABB<EntityPlayer>(EntityPlayerMP::class.java, getEffectBounds())
        val players = LinkedHashMap<EntityPlayer, List<ItemStack>>()
        for (player in list) {
            val armors = Lists.newLinkedList<ItemStack>()
            for (stack in player.inventory.armorInventory)
                if (stack.item is ArmorBase)
                    if ((stack.item as ArmorBase).requireEMC(stack))
                        armors.add(stack)
            if (!armors.isEmpty())
                players[player] = armors
        }

        val toSend = (inventory.getStackInSlot(0).item as IItemEmc).getStoredEmc(inventory.getStackInSlot(0)) / (tiles.count() + players.size)

        if (toSend < 1) {
            return
        }

        tiles.forEach {
            sendEMC(it, toSend)
            val x = pos.x - it.getPos().x
            val y = pos.y - it.getPos().y
            val z = pos.z - it.getPos().z
            spawnEMCParticles(BlockPos(x, y, z), it.getPos())
        }

        players.forEach { (key, value) ->
            value.forEach { armor -> sendEMC(armor, toSend) }
            val x = pos.x - key.posX
            val y = pos.y - key.posY
            val z = pos.z - key.posZ
            spawnEMCParticles(BlockPos(x, y, z), key.position)
        }

    }

    fun requiresEMC(): Boolean {
        return requiredEMC() > 0
    }

    fun setActivityCooldown(i: Int) {
        activityCooldown = i
    }

    /**
     * @return Inclusive bounding box of all positions this pedestal should apply effects in
     */
    fun getEffectBounds(): AxisAlignedBB {
        return AxisAlignedBB(getPos().add(-RANGE, -4, -RANGE), getPos().add(RANGE, 4, RANGE))
    }

    override fun readFromNBT(tag: NBTTagCompound) {
        super.readFromNBT(tag)
        inventory = this.StackHandler(1)
        inventory.deserializeNBT(tag)
        setActive(tag.getBoolean("isActive"))
        activityCooldown = tag.getInteger("activityCooldown")
        previousRedstoneState = tag.getBoolean("powered")
    }

    override fun writeToNBT(tag: NBTTagCompound): NBTTagCompound {
        var tag = tag
        tag = super.writeToNBT(tag)
        tag.merge(inventory.serializeNBT())
        tag.setBoolean("isActive", getActive())
        tag.setInteger("activityCooldown", activityCooldown)
        tag.setBoolean("powered", previousRedstoneState)
        return tag
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity? {
        return SPacketUpdateTileEntity(pos, -1, updateTag)
    }

    override fun onDataPacket(manager: NetworkManager?, packet: SPacketUpdateTileEntity?) {
        readFromNBT(packet!!.nbtCompound)
    }

    fun getActive(): Boolean {
        return isActive
    }

    fun setActive(newState: Boolean) {
        //if (!newState && this.getItem() instanceof IItemEmc && isConnected) return;
        if (newState != this.getActive() && world != null) {
            if (newState) {
                world.playSound(null, pos, PESounds.CHARGE, SoundCategory.BLOCKS, 1.0f, 1.0f)
                for (i in 0 until world.rand.nextInt(35) + 10) {
                    this.getWorld().spawnParticle(EnumParticleTypes.SPELL_WITCH, centeredX + world.rand.nextGaussian() * 0.12999999523162842,
                            getPos().y.toDouble() + 1.0 + world.rand.nextGaussian() * 0.12999999523162842,
                            centeredZ + world.rand.nextGaussian() * 0.12999999523162842,
                            0.0, 0.0, 0.0)
                }
                PedestalEvent.registerTileEntity(this)
            } else {
                world.playSound(null, pos, PESounds.UNCHARGE, SoundCategory.BLOCKS, 1.0f, 1.0f)
                for (i in 0 until world.rand.nextInt(35) + 10) {
                    this.getWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, centeredX + world.rand.nextGaussian() * 0.12999999523162842,
                            getPos().y.toDouble() + 1.0 + world.rand.nextGaussian() * 0.12999999523162842,
                            centeredZ + world.rand.nextGaussian() * 0.12999999523162842,
                            0.0, 0.0, 0.0)
                }
                PedestalEvent.removeTileEntity(this)
            }
        }
        this.isActive = newState

        if (world != null) {
            val state = world.getBlockState(pos)
            world.notifyBlockUpdate(pos, state, state, 7)
        }
    }

    fun registerTileEntity(tile: TileEntity?) {
        val item = inventory.getStackInSlot(0).item
        if (item is TimeWatch && tile is IEmcGen)
            nearbyBlocks.add(tile.pos)
        if (item is Tome && tile is ICraftingGen)
            nearbyBlocks.add(tile.pos)
        if (item is IItemEmc && tile is DMPedestalTile)
            nearbyPedestals.add(tile)
    }

    fun registerBlock(pos: BlockPos) {
        val item = inventory.getStackInSlot(0).item
        val block = world.getBlockState(pos).block
        if (item is IPedestalItem) {
            if (item is HarvestGoddess)
                if (block is IShearable || block is IGrowable || block is IPlantable)
                    nearbyBlocks.add(pos)

        }
    }

    override fun hasCapability(cap: Capability<*>, side: EnumFacing?): Boolean {
        return cap === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side)
    }

    override fun <T> getCapability(cap: Capability<T>, side: EnumFacing?): T? {
        return if (cap === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast<T>(inventory)
        } else super.getCapability(cap, side)
    }

    override fun onLoad() {
        if (getItem() !== Items.AIR && getActive())
            PedestalEvent.registerTileEntity(this)
        super.onLoad()
    }

    override fun onChunkUnload() {
        PedestalEvent.removeTileEntity(this)
        super.onChunkUnload()
    }

    fun getInventory(): IItemHandlerModifiable {
        return inventory
    }

    private fun sendEMC(tile: DMPedestalTile, emc: Long) {
        val toAdd = min(emc, Constants.RELAY_MK2_OUTPUT)

        val emcSent = tile.acceptEMC(EnumFacing.DOWN, toAdd)

        (inventory.getStackInSlot(0).item as IItemEmc).extractEmc(inventory.getStackInSlot(0), emcSent)
    }

    private fun sendEMC(stack: ItemStack, emc: Long) {
        val toAdd = min(emc, Constants.RELAY_MK2_OUTPUT)

        val emcSent = (stack.item as ArmorBase).acceptEMC(stack, toAdd)

        (inventory.getStackInSlot(0).item as IItemEmc).extractEmc(inventory.getStackInSlot(0), emcSent)
    }

    override fun acceptEMC(side: EnumFacing, toAccept: Long): Long {
        if (inventory.getStackInSlot(0).item is IItemEmc) {
            val itemEmc = (inventory.getStackInSlot(0).item as IItemEmc)

            val storedEmc = itemEmc.getStoredEmc(inventory.getStackInSlot(0))
            val maxEmc = itemEmc.getMaximumEmc(inventory.getStackInSlot(0))
            var toAdd = min(maxEmc - storedEmc, toAccept)

            return if ((storedEmc + toAdd) <= maxEmc) {
                itemEmc.addEmc(inventory.getStackInSlot(0), toAdd)
                toAdd
            } else {
                toAdd = maxEmc - storedEmc
                itemEmc.addEmc(inventory.getStackInSlot(0), toAdd)
                toAdd
            }
        } else if (requiredEMC() > 0) {
            val toAdd = min(requiredEMC(), toAccept)
            currentEMC += toAdd
            return toAdd
        }
        return 0
    }

    companion object {

        fun DMPedestalTile(): DMPedestalTile {
            return DMPedestalTile(4, ProjectBConfig.tweaks.DMPedestalMax.toLong())
        }
    }
}