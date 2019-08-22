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

import com.tencao.projectbalance.mapper.Graph
import moze_intel.projecte.api.ProjectEAPI
import moze_intel.projecte.api.capabilities.IKnowledgeProvider
import moze_intel.projecte.api.item.IItemEmc
import moze_intel.projecte.emc.FuelMapper
import moze_intel.projecte.utils.Constants.TILE_MAX_EMC
import moze_intel.projecte.utils.EMCHelper
import moze_intel.projecte.utils.ItemHelper
import moze_intel.projecte.utils.NBTWhitelist
import moze_intel.projecte.utils.PlayerHelper.SCOREBOARD_EMC
import moze_intel.projecte.utils.PlayerHelper.updateScore
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemStackHandler
import net.minecraftforge.items.wrapper.CombinedInvWrapper
import java.util.*
import kotlin.math.max
import kotlin.streams.toList


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
            var canAdd = true
            if (Graph[stack].complexity > 1) {
                val recipes = Graph[stack]
                        .recipes
                        .parallelStream()
                        .filter { it ->
                            it.input
                                    .stream()
                                    .filter {
                                        it.toStacks()
                                                .stream()
                                                .anyMatch(EMCHelper::doesItemHaveEmc)
                                    }
                                    .count() > 0
                        }
                        .toList()
                if (recipes.isNotEmpty()) {
                    canAdd = recipes.stream().filter { it.input.stream().allMatch { input -> input.toStacks().any {stack -> EMCHelper.doesItemHaveEmc(stack) && provider.hasKnowledge(stack) } } }.findFirst().isPresent
                }
            }
            if (canAdd) {
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

        if (max(matterEmc, fuelEmc) > getAvailableEMC()) {
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
        if (!inputLocks.getStackInSlot(LOCK_INDEX).isEmpty) {
            lockCopy = ItemHelper.getNormalizedStack(inputLocks.getStackInSlot(LOCK_INDEX))

            if (ItemHelper.isDamageable(lockCopy)) {
                lockCopy.itemDamage = 0
            }

            val reqEmc = EMCHelper.getEmcValue(inputLocks.getStackInSlot(LOCK_INDEX))

            if (getAvailableEMC() < reqEmc) {
                return
            }

            if (lockCopy.hasTagCompound() && !NBTWhitelist.shouldDupeWithNBT(lockCopy)) {
                lockCopy.tagCompound = null
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

                if (!doesItemMatchFilter(stack)) {
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

                if (getAvailableEMC() < EMCHelper.getEmcValue(stack)) {
                    iter.remove()
                    continue
                }

                if (!doesItemMatchFilter(stack)) {
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

    private fun doesItemMatchFilter(stack: ItemStack): Boolean {
        val displayName: String?

        try {
            displayName = stack.displayName.toLowerCase(Locale.ROOT)
        } catch (e: Exception) {
            e.printStackTrace()
            //From old code... Not sure if intended to not remove items that crash on getDisplayName
            return true
        }

        if (displayName.isNullOrBlank()) {
            return false
        } else if (filter.isNotEmpty() && !displayName.contains(filter)) {
            return false
        }
        return true
    }

    fun writeIntoOutputSlot(slot: Int, item: ItemStack) {

        if (EMCHelper.doesItemHaveEmc(item)
                && EMCHelper.getEmcValue(item) <= getAvailableEMC()
                && provider.hasKnowledge(item)) {
            outputs.setStackInSlot(slot, item)
        } else {
            outputs.setStackInSlot(slot, ItemStack.EMPTY)
        }
    }

    fun addEmc(value: Long) {
        var value = value
        if (value == 0L) {
            //Optimization to not look at the items if nothing will happen anyways
            return
        }
        if (value < 0) {
            //Make sure it is using the correct method so that it handles the klein stars properly
            removeEmc(-value)
        }
        //Start by trying to add it to the EMC items on the left
        for (i in 0 until inputLocks.slots) {
            if (i == LOCK_INDEX) {
                continue
            }
            val stack = inputLocks.getStackInSlot(i)
            if (!stack.isEmpty && stack.item is IItemEmc) {
                val itemEmc = stack.item as IItemEmc
                val neededEmc = itemEmc.getMaximumEmc(stack) - itemEmc.getStoredEmc(stack)
                if (value <= neededEmc) {
                    //This item can store all of the amount being added
                    itemEmc.addEmc(stack, value)
                    return
                }
                //else more than this item can fit, so fill the item and then continue going
                itemEmc.addEmc(stack, neededEmc)
                value -= neededEmc
            }
        }
        val emcToMax = TILE_MAX_EMC - provider.emc
        if (value > emcToMax) {
            val excessEMC = value - emcToMax
            value = emcToMax
            //Will finish filling provider
            //Now with excess EMC we can check against the lock slot as that is the last spot that has its EMC used.
            val stack = inputLocks.getStackInSlot(LOCK_INDEX)
            if (!stack.isEmpty && stack.item is IItemEmc) {
                val itemEmc = stack.item as IItemEmc
                val neededEmc = itemEmc.getMaximumEmc(stack) - itemEmc.getStoredEmc(stack)
                if (excessEMC > neededEmc) {
                    itemEmc.addEmc(stack, neededEmc)
                } else {
                    itemEmc.addEmc(stack, excessEMC)
                }
            }
        }

        provider.emc = provider.emc + value

        if (provider.emc >= TILE_MAX_EMC || provider.emc < 0) {
            provider.emc = TILE_MAX_EMC
        }

        if (!player.entityWorld.isRemote) {
            updateScore(player as EntityPlayerMP, SCOREBOARD_EMC, provider.emc.toInt())
        }
    }

    fun removeEmc(value: Long) {
        var value = value
        if (value == 0L) {
            //Optimization to not look at the items if nothing will happen anyways
            return
        }
        if (value < 0) {
            //Make sure it is using the correct method so that it handles the klein stars properly
            addEmc(-value)
        }
        if (hasMaxedEmc()) {
            //If the EMC is maxed, check and try to remove from the lock slot if it is IItemEMC
            //This is the only case if the provider is full when the IItemEMC was put in the lock slot
            val stack = inputLocks.getStackInSlot(LOCK_INDEX)
            if (!stack.isEmpty && stack.item is IItemEmc) {
                val itemEmc = stack.item as IItemEmc
                val storedEmc = itemEmc.getStoredEmc(stack)
                if (storedEmc >= value) {
                    //All of it can be removed from the lock item
                    itemEmc.extractEmc(stack, value)
                    return
                }
                itemEmc.extractEmc(stack, storedEmc)
                value -= storedEmc
            }
        }
        if (value > provider.emc) {
            //Remove from provider first
            //This code runs first to simplify the logic
            //But it simulates removal first by extracting the amount from value and then removing that excess from items
            var toRemove = value - provider.emc
            value = provider.emc
            for (i in 0 until inputLocks.slots) {
                if (i == LOCK_INDEX) {
                    continue
                }
                val stack = inputLocks.getStackInSlot(i)
                if (!stack.isEmpty && stack.item is IItemEmc) {
                    val itemEmc = stack.item as IItemEmc
                    val storedEmc = itemEmc.getStoredEmc(stack)
                    if (toRemove <= storedEmc) {
                        //The EMC that is being removed that the provider does not contain is satisfied by this IItemEMC
                        //Remove it and then
                        itemEmc.extractEmc(stack, toRemove)
                        break
                    }
                    //Removes all the emc from this item
                    itemEmc.extractEmc(stack, storedEmc)
                    toRemove -= storedEmc
                }
            }
        }
        provider.emc = provider.emc - value

        if (provider.emc < 0) {
            provider.emc = 0
        }

        if (!player.entityWorld.isRemote) {
            updateScore(player as EntityPlayerMP, SCOREBOARD_EMC, provider.emc.toInt())
        }
    }

    fun hasMaxedEmc(): Boolean {
        return provider.emc >= TILE_MAX_EMC
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

    /**
     * @return EMC available from the Provider + any klein stars in the input slots.
     */
    fun getAvailableEMC(): Long {
        //TODO: Cache this value somehow, or at least cache which slots have IItemEMC in them?
        if (hasMaxedEmc()) {
            return TILE_MAX_EMC
        }

        var emc = provider.emc
        var emcToMax = TILE_MAX_EMC - emc
        for (i in 0 until inputLocks.slots) {
            if (i == LOCK_INDEX) {
                //Skip it even though this technically could add to available EMC.
                //This is because this case can only happen if the provider is already at max EMC
                continue
            }
            val stack = inputLocks.getStackInSlot(i)
            if (!stack.isEmpty && stack.item is IItemEmc) {
                val itemEmc = stack.item as IItemEmc
                val storedEmc = itemEmc.getStoredEmc(stack)
                if (storedEmc >= emcToMax) {
                    return TILE_MAX_EMC
                }
                emc += storedEmc
                emcToMax -= storedEmc
            }
        }
        return emc
    }

}