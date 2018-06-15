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

import com.tencao.projectbalance.config.ProjectBConfig
import com.tencao.projectbalance.gameObjs.ObjRegistry
import com.tencao.projectbalance.utils.ComplexHelper
import com.tencao.projectbalance.utils.Constants
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates
import moze_intel.projecte.utils.EMCHelper
import moze_intel.projecte.utils.ItemHelper
import moze_intel.projecte.utils.NBTWhitelist
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.SoundCategory
import net.minecraft.world.EnumSkyBlock
import net.minecraftforge.common.BiomeDictionary
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.ItemStackHandler
import java.util.*

open class PowerFlowerMK1Tile internal constructor(maxEmc: Int, private val emcGen: Float) : TileEmc(maxEmc), IEmcGen, ICraftingGen {

    val input = this.createInput()
    val output = this.createOutput()
    private val automationInventory = this.createAutomationInventory()
    val lock: ItemStackHandler = this.StackHandler(1)
    private var ticksSinceSync: Int = 0
    var displayEmc: Int = 0
    var lidAngle: Float = 0.toFloat()
    var prevLidAngle: Float = 0.toFloat()
    var numPlayersUsing: Int = 0
    var requiredEmc: Int = 0
    private var extraEMCAdded: Boolean = false
    var requiredTime: Long = 0
    var timePassed: Long = 0
    var tomeProviders = LinkedHashSet<DMPedestalTile>()

    val sunLevel: Float
        get() {
            if (world.provider.isNether) {
                return 0.0f
            }
            var sunBrightness = limit(Math.cos(world.getCelestialAngleRadians(1.0f).toDouble()).toFloat() * 2.0f + 0.2f, 0.0f, 1.0f)
            if (!BiomeDictionary.hasType(world.getBiome(pos), BiomeDictionary.Type.SANDY)) {
                sunBrightness *= 1.0f - world.getRainStrength(1.0f) * 5.0f / 16.0f
                sunBrightness *= 1.0f - world.getThunderStrength(1.0f) * 5.0f / 16.0f

                sunBrightness = limit(sunBrightness, 0.0f, 1.0f)
            }
            return world.getLightFor(EnumSkyBlock.SKY, pos.up()) / 15.0f * sunBrightness
        }

    constructor() : this(Constants.POWER_FLOWER_MK1_MAX, Constants.POWER_FLOWER_MK1_GEN)

    protected open fun createInput(): ItemStackHandler {
        return this.StackHandler(91)
    }

    protected open fun createOutput(): ItemStackHandler {
        return input
    }

    protected open fun createAutomationInventory(): IItemHandler {
        return object : WrappedItemHandler(input, WrappedItemHandler.WriteMode.IN_OUT) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                return if (SlotPredicates.HAS_EMC.test(stack) && !isStackEqualToLock(stack))
                    super.insertItem(slot, stack, simulate)
                else
                    stack
            }

            override fun extractItem(slot: Int, max: Int, simulate: Boolean): ItemStack {
                return if (!getStackInSlot(slot).isEmpty && isStackEqualToLock(getStackInSlot(slot))) {
                    super.extractItem(slot, max, simulate)
                } else {
                    ItemStack.EMPTY
                }
            }
        }
    }

    override fun hasCapability(cap: Capability<*>, side: EnumFacing?): Boolean {
        return cap === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side)
    }

    override fun <T> getCapability(cap: Capability<T>, side: EnumFacing?): T? {
        return if (cap === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast<T>(automationInventory)
        } else super.getCapability(cap, side)
    }

    override fun update() {
        updateChest()

        if (this.getWorld().isRemote) {
            return
        }

        checkLockAndUpdate()

        displayEmc = this.storedEmc.toInt()

        if (!lock.getStackInSlot(0).isEmpty && requiredEmc != 0) {
            condense()
        }

        updateEmc(false)
    }

    override fun updateEmc(isExtra: Boolean) {
        if (isExtra) {
            if (!extraEMCAdded) {
                val emc = getSunRelativeEmc(emcGen) / 20.0f
                if (emc > 0f)
                    this.addEMC((emc / 2).toDouble())
                extraEMCAdded = true
            }
        } else {
            extraEMCAdded = false
            val emc = getSunRelativeEmc(emcGen) / 20.0f
            if (emc > 0f)
                this.addEMC(emc.toDouble())
        }
    }

    private fun getSunRelativeEmc(emc: Float): Float {
        return emc * sunLevel
    }

    private fun checkLockAndUpdate() {
        if (lock.getStackInSlot(0).isEmpty) {
            requiredEmc = 0
            timePassed = 0
            requiredTime = 0
            return
        }

        if (EMCHelper.doesItemHaveEmc(lock.getStackInSlot(0))) {
            val lockEmc = EMCHelper.getEmcValue(lock.getStackInSlot(0))

            if (requiredEmc != lockEmc) {
                requiredTime = ComplexHelper.getCraftTime(lock.getStackInSlot(0))
                requiredEmc = lockEmc
            }
        } else {
            lock.setStackInSlot(0, ItemStack.EMPTY)

            requiredEmc = 0
            timePassed = 0
            requiredTime = 0
        }
    }

    protected open fun condense() {
        if (this.storedEmc == 0.0 || this.storedEmc / 2 < requiredEmc) {
            for (i in 0 until input.slots) {
                val stack = input.getStackInSlot(i)

                if (stack.isEmpty || isStackEqualToLock(stack)) {
                    continue
                }

                input.extractItem(i, 1, false)
                this.addEMC(EMCHelper.getEmcSellValue(stack).toDouble())
                break
            }
        }
        craft()
    }

    protected open fun craft(){
        if (this.storedEmc >= requiredEmc && this.hasSpace()) {
            if (requiredTime <= 0) {
                val time = ComplexHelper.getCraftTime(lock.getStackInSlot(0))
                if (time <= 100) {
                    this.removeEMC(requiredEmc.toDouble())
                    pushStack()
                } else {
                    requiredTime = time
                    timePassed = 0
                }
            } else {
                timePassed += if (tomeProviders.isEmpty())
                    1
                else {
                    var counter = 0
                    for (tile in tomeProviders)
                        if (tile.hasRequiredEMC(ProjectBConfig.tweaks.TomeCost.toDouble(), false))
                            counter++
                    if (counter > 0)
                        Math.min(
                                (requiredTime * (5.0f / 100.0f) / 20).toInt(),
                                ((counter * 2)) + 1)
                    else
                        1
                }
                if (timePassed >= requiredTime) {
                    this.removeEMC(requiredEmc.toDouble())
                    pushStack()
                    timePassed = 0


                }
            }
        }
    }

    override fun addTomeToCounter(tile: DMPedestalTile) {
        tomeProviders.add(tile)
    }

    override fun removeTomeFromCounter(tile: DMPedestalTile) {
        tomeProviders.remove(tile)
    }

    protected fun pushStack() {
        val lockCopy = lock.getStackInSlot(0).copy()

        if (lockCopy.hasTagCompound() && !NBTWhitelist.shouldDupeWithNBT(lockCopy)) {
            lockCopy.tagCompound = NBTTagCompound()
        }

        ItemHandlerHelper.insertItemStacked(output, lockCopy, false)
    }

    protected fun hasSpace(): Boolean {
        for (i in 0 until output.slots) {
            val stack = output.getStackInSlot(i)

            if (stack.isEmpty) {
                return true
            }

            if (isStackEqualToLock(stack) && stack.count < stack.maxStackSize) {
                return true
            }
        }

        return false
    }

    fun isStackEqualToLock(stack: ItemStack): Boolean {
        if (lock.getStackInSlot(0).isEmpty) {
            return false
        }

        return if (NBTWhitelist.shouldDupeWithNBT(lock.getStackInSlot(0))) {
            ItemHelper.areItemStacksEqual(lock.getStackInSlot(0), stack)
        } else ItemHelper.areItemStacksEqualIgnoreNBT(lock.getStackInSlot(0), stack)

    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        input.deserializeNBT(nbt.getCompoundTag("Input"))
        lock.deserializeNBT(nbt.getCompoundTag("LockSlot"))
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        var nbt = nbt
        nbt = super.writeToNBT(nbt)
        nbt.setTag("Input", input.serializeNBT())
        nbt.setTag("LockSlot", lock.serializeNBT())
        return nbt
    }

    private fun updateChest() {
        if (++ticksSinceSync % 20 * 4 == 0) {
            world.addBlockEvent(pos, ObjRegistry.powerFlower, 1, numPlayersUsing)
        }

        prevLidAngle = lidAngle
        val angleIncrement = 0.1f

        if (numPlayersUsing > 0 && lidAngle == 0.0f) {
            world.playSound(null, pos, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5f, world.rand.nextFloat() * 0.1f + 0.9f)
        }

        if (numPlayersUsing == 0 && lidAngle > 0.0f || numPlayersUsing > 0 && lidAngle < 1.0f) {
            val var8 = lidAngle

            if (numPlayersUsing > 0) {
                lidAngle += angleIncrement
            } else {
                lidAngle -= angleIncrement
            }

            if (lidAngle > 1.0f) {
                lidAngle = 1.0f
            }

            if (lidAngle < 0.5f && var8 >= 0.5f) {
                world.playSound(null, pos, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5f, world.rand.nextFloat() * 0.1f + 0.9f)
            }

            if (lidAngle < 0.0f) {
                lidAngle = 0.0f
            }
        }
    }

    override fun receiveClientEvent(number: Int, arg: Int): Boolean {
        return if (number == 1) {
            numPlayersUsing = arg
            true
        } else
            super.receiveClientEvent(number, arg)
    }

    companion object {

        fun limit(value: Float, min: Float, max: Float): Float {
            if (java.lang.Float.isNaN(value) || value <= min) {
                return min
            }
            return if (value >= max) {
                max
            } else value
        }
    }
}
