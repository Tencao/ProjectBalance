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

package com.tencao.projectbalance.gameObjs.container.inventory

import moze_intel.projecte.api.ProjectEAPI
import moze_intel.projecte.api.capabilities.IKnowledgeProvider
import moze_intel.projecte.emc.FuelMapper
import moze_intel.projecte.utils.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.MathHelper
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemStackHandler
import net.minecraftforge.items.wrapper.CombinedInvWrapper
import java.util.*

class TransmutationInventory(val player: EntityPlayer): CombinedInvWrapper(player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null)!!.inputAndLocks as IItemHandlerModifiable,
        ItemStackHandler(2), ItemStackHandler(16)) {


    val provider: IKnowledgeProvider = player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null)!!
    private val inputLocks: IItemHandlerModifiable = itemHandler[0]
    private val learning: IItemHandlerModifiable = itemHandler[1]
    val outputs: IItemHandlerModifiable = itemHandler[2]

    private val LOCK_INDEX = 8
    private val FUEL_START = 12
    var learnFlag = 0
    var unlearnFlag = 0
    var filter = ""
    var searchpage = 0
    val knowledge: MutableList<ItemStack> = ArrayList()

    init {
        if (player.entityWorld.isRemote) {
            updateClientTargets()
        }
    }

    fun handleKnowledge(stack: ItemStack) {
        if (stack.count > 1) {
            stack.count = 1
        }

        if (ItemHelper.isDamageable(stack)) {
            stack.itemDamage = 0
        }

        if (!provider.hasKnowledge(stack)) {
            learnFlag = 300
            unlearnFlag = 0

            if (stack.hasTagCompound() && !NBTWhitelist.shouldDupeWithNBT(stack)) {
                stack.tagCompound = null
            }

            provider.addKnowledge(stack)

            if (!player.entityWorld.isRemote) {
                provider.sync(player as EntityPlayerMP)
            }
        }

        updateClientTargets()
    }

    fun handleUnlearn(stack: ItemStack) {
        if (stack.count > 1) {
            stack.count = 1
        }

        if (ItemHelper.isDamageable(stack)) {
            stack.itemDamage = 0
        }

        if (provider.hasKnowledge(stack)) {
            unlearnFlag = 300
            learnFlag = 0

            if (stack.hasTagCompound() && !NBTWhitelist.shouldDupeWithNBT(stack)) {
                stack.tagCompound = null
            }

            provider.removeKnowledge(stack)

            if (!player.entityWorld.isRemote) {
                provider.sync(player as EntityPlayerMP)
            }
        }

        updateClientTargets()
    }

    fun checkForUpdates() {
        val matterEmc = EMCHelper.getEmcValue(outputs.getStackInSlot(0))
        val fuelEmc = EMCHelper.getEmcValue(outputs.getStackInSlot(FUEL_START))

        if (Math.max(matterEmc, fuelEmc) > provider.emc) {
            updateClientTargets()
        }
    }

    fun updateClientTargets() {
        if (!this.player.entityWorld.isRemote) {
            return
        }

        knowledge.clear()
        knowledge.addAll(provider.knowledge)

        for (i in 0 until outputs.slots) {
            outputs.setStackInSlot(i, ItemStack.EMPTY)
        }

        var lockCopy = ItemStack.EMPTY

        knowledge.sortBy ( EMCHelper::getEmcValue )
        val searchHelper = ItemSearchHelper.create(filter)
        if (!inputLocks.getStackInSlot(LOCK_INDEX).isEmpty) {
            lockCopy = ItemHelper.getNormalizedStack(inputLocks.getStackInSlot(LOCK_INDEX))

            if (ItemHelper.isDamageable(lockCopy)) {
                lockCopy.itemDamage = 0
            }

            val reqEmc = EMCHelper.getEmcValue(inputLocks.getStackInSlot(LOCK_INDEX))

            if (provider.emc < reqEmc) {
                return
            }

            if (lockCopy.hasTagCompound() && !NBTWhitelist.shouldDupeWithNBT(lockCopy)) {
                lockCopy.tagCompound = NBTTagCompound()
            }

            val iter = knowledge.iterator()
            var pagecounter = 0

            while (iter.hasNext()) {
                val stack = iter.next()

                if (EMCHelper.getEmcValue(stack) > reqEmc) {
                    iter.remove()
                    continue
                }

                if (ItemHelper.basicAreStacksEqual(lockCopy, stack)) {
                    iter.remove()
                    continue
                }

                if (!searchHelper.doesItemMatchFilter(stack)) {
                    iter.remove()
                    continue
                }

                if (pagecounter < searchpage * 12) {
                    pagecounter++
                    iter.remove()
                }
            }
        } else {
            val iter = knowledge.iterator()
            var pagecounter = 0

            while (iter.hasNext()) {
                val stack = iter.next()

                if (provider.emc < EMCHelper.getEmcValue(stack)) {
                    iter.remove()
                    continue
                }

                if (!searchHelper.doesItemMatchFilter(stack)) {
                    iter.remove()
                    continue
                }

                if (pagecounter < searchpage * 12) {
                    pagecounter++
                    iter.remove()
                }
            }
        }

        var matterCounter = 0
        var fuelCounter = 0

        if (!lockCopy.isEmpty) {
            if (FuelMapper.isStackFuel(lockCopy)) {
                outputs.setStackInSlot(FUEL_START, lockCopy)
                fuelCounter++
            } else {
                outputs.setStackInSlot(0, lockCopy)
                matterCounter++
            }
        }

        for (stack in knowledge) {
            if (FuelMapper.isStackFuel(stack)) {
                if (fuelCounter < 4) {
                    outputs.setStackInSlot(FUEL_START + fuelCounter, stack)

                    fuelCounter++
                }
            } else {
                if (matterCounter < 12) {
                    outputs.setStackInSlot(matterCounter, stack)

                    matterCounter++
                }
            }
        }
    }

    fun writeIntoOutputSlot(slot: Int, item: ItemStack) {

        if (EMCHelper.doesItemHaveEmc(item)
                && EMCHelper.getEmcValue(item) <= provider.emc
                && provider.hasKnowledge(item)) {
            outputs.setStackInSlot(slot, item)
        } else {
            outputs.setStackInSlot(slot, ItemStack.EMPTY)
        }
    }

    fun addEmc(value: Double) {
        provider.emc = provider.emc + value

        if (provider.emc >= Constants.TILE_MAX_EMC || provider.emc < 0) {
            provider.emc = Constants.TILE_MAX_EMC.toDouble()
        }

        if (!player.entityWorld.isRemote) {
            PlayerHelper.updateScore(player as EntityPlayerMP, PlayerHelper.SCOREBOARD_EMC, MathHelper.floor(provider.emc))
        }
    }

    fun removeEmc(value: Double) {
        provider.emc = provider.emc - value

        if (provider.emc < 0) {
            provider.emc = 0.0
        }

        if (!player.entityWorld.isRemote) {
            PlayerHelper.updateScore(player as EntityPlayerMP, PlayerHelper.SCOREBOARD_EMC, MathHelper.floor(provider.emc))
        }
    }

    fun hasMaxedEmc(): Boolean {
        return provider.emc >= Constants.TILE_MAX_EMC
    }

    fun getHandlerForSlot(slot: Int): IItemHandlerModifiable {
        return super.getHandlerFromIndex(super.getIndexForSlot(slot))
    }

    fun getIndexFromSlot(slot: Int): Int {
        var slot = slot
        for (h in itemHandler) {
            if (slot >= h.slots) {
                slot -= h.slots
            }
        }

        return slot
    }

}