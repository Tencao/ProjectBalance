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

package com.tencao.projectbalance.gameObjs.items

import baubles.api.BaublesApi
import com.tencao.projectbalance.gameObjs.tile.DMPedestalTile
import moze_intel.projecte.api.item.IModeChanger
import moze_intel.projecte.config.ProjectEConfig
import moze_intel.projecte.gameObjs.items.ItemPE
import moze_intel.projecte.gameObjs.items.RepairTalisman
import moze_intel.projecte.gameObjs.items.rings.RingToggle
import moze_intel.projecte.gameObjs.tiles.AlchChestTile
import moze_intel.projecte.handlers.InternalTimers
import moze_intel.projecte.utils.EMCHelper
import moze_intel.projecte.utils.ItemHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Optional
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler

class RepairTalisman: RepairTalisman() {

    override fun onUpdate(stack: ItemStack?, world: World?, entity: Entity?, par4: Int, par5: Boolean) {
        if (world!!.isRemote || entity !is EntityPlayer) {
            return
        }

        val player = entity as EntityPlayer?

        player!!.getCapability(InternalTimers.CAPABILITY, null)!!.activateRepair()

        if (player.getCapability(InternalTimers.CAPABILITY, null)!!.canRepair()) {
            repairAllItems(player)
        }
    }

    private fun repairAllItems(player: EntityPlayer) {
        val inv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)

        for (i in 0 until inv!!.slots) {
            val invStack = inv.getStackInSlot(i)

            if (invStack.isEmpty || invStack.item is IModeChanger || !invStack.item.isRepairable) {
                continue
            }

            if (invStack == player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND) && player.isSwingInProgress) {
                //Don't repair item that is currently used by the player.
                continue
            }

            if (ItemHelper.isDamageable(invStack) && invStack.itemDamage > 0) {
                if (ItemPE.consumeFuel(player, invStack, EMCHelper.getEMCPerDurability(invStack), true))
                    invStack.itemDamage = invStack.itemDamage - 1
            }
        }

        if (Loader.isModLoaded("baubles")) baubleRepair(player)
    }

    private fun getRepairCost(player: EntityPlayer): Long {
        return getRepairCost(player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)!!)
    }

    private fun getRepairCost(inv: IItemHandler): Long {
        var emcCost = 0L

        for (i in 0 until inv.slots) {
            val invStack = inv.getStackInSlot(i)

            if (invStack == ItemStack.EMPTY || invStack.item is IModeChanger || !invStack.item.isRepairable) {
                continue
            }

            if (ItemHelper.isItemRepairable(invStack)) {
                emcCost += EMCHelper.getEMCPerDurability(invStack)
            }
        }

        return emcCost
    }

    @Optional.Method(modid = "baubles")
    override fun baubleRepair(player: EntityPlayer) {
        val bInv = BaublesApi.getBaublesHandler(player)

        for (i in 0 until bInv.slots) {
            val bInvStack = bInv.getStackInSlot(i)
            if (bInvStack.isEmpty || bInvStack.item is IModeChanger || !bInvStack.item.isRepairable) {
                continue
            }

            if (ItemHelper.isDamageable(bInvStack) && bInvStack.itemDamage > 0) {
                if (ItemPE.consumeFuel(player, bInvStack, EMCHelper.getEMCPerDurability(bInvStack), true))
                    bInvStack.itemDamage = bInvStack.itemDamage - 1
            }
        }
    }

    override fun updateInPedestal(world: World, pos: BlockPos) {
        if (!world.isRemote && ProjectEConfig.pedestalCooldown.repairPedCooldown != -1) {
            val tile = world.getTileEntity(pos) as DMPedestalTile?
            world.getEntitiesWithinAABB(EntityPlayerMP::class.java, tile!!.getEffectBounds()).forEach { player ->
                if (tile.hasRequiredEMC(getRepairCost(player), false))
                    repairAllItems(player)
            }
            tile.setActivityCooldown(ProjectEConfig.pedestalCooldown.repairPedCooldown)
        }
    }

    override fun updateInAlchChest(world: World, pos: BlockPos, stack: ItemStack) {
        if (world.isRemote) {
            return
        }

        val te = world.getTileEntity(pos) as? AlchChestTile ?: return

        val coolDown = ItemHelper.getOrCreateCompound(stack).getByte("Cooldown")

        if (coolDown > 0) {
            stack.tagCompound!!.setByte("Cooldown", (coolDown - 1).toByte())
        } else {
            var hasAction = false

            val inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
            for (i in 0 until inv!!.slots) {
                val invStack = inv.getStackInSlot(i)

                if (invStack.isEmpty || invStack.item is RingToggle || !invStack.item.isRepairable) {
                    continue
                }

                if (ItemHelper.isDamageable(invStack) && invStack.itemDamage > 0) {
                    if (consumeFuel(inv, invStack, EMCHelper.getEMCPerDurability(invStack), true)) {
                        invStack.itemDamage = invStack.itemDamage - 1

                        if (!hasAction) {
                            hasAction = true
                        }
                    }
                }
            }

            if (hasAction) {
                stack.tagCompound!!.setByte("Cooldown", 19.toByte())
                te.markDirty()
            }
        }
    }

    override fun updateInAlchBag(inv: IItemHandler, player: EntityPlayer, stack: ItemStack): Boolean {
        if (player.entityWorld.isRemote) {
            return false
        }

        val coolDown = ItemHelper.getOrCreateCompound(stack).getByte("Cooldown")

        if (coolDown > 0) {
            stack.tagCompound!!.setByte("Cooldown", (coolDown - 1).toByte())
        } else {
            var hasAction = false

            for (i in 0 until inv.slots) {
                val invStack = inv.getStackInSlot(i)

                if (invStack.isEmpty || invStack.item is RingToggle || !invStack.item.isRepairable) {
                    continue
                }

                if (ItemHelper.isDamageable(invStack) && invStack.itemDamage > 0) {
                    if (consumeFuel(inv, invStack, EMCHelper.getEMCPerDurability(invStack), true) || ItemPE.consumeFuel(player, invStack, EMCHelper.getEMCPerDurability(invStack), true)) {
                        invStack.itemDamage = invStack.itemDamage - 1

                        if (!hasAction) {
                            hasAction = true
                        }
                    }
                }
            }

            if (hasAction) {
                stack.tagCompound!!.setByte("Cooldown", 19.toByte())
                return true
            }
        }
        return false
    }

    fun consumeFuel(inv: IItemHandler, stack: ItemStack, amount: Long, shouldRemove: Boolean): Boolean {
        if (amount <= 0) {
            return true
        }

        val current = ItemPE.getEmc(stack)

        if (current < amount) {
            val consume = com.tencao.projectbalance.utils.EMCHelper.consumeInvFuel(inv, amount - current)

            if (consume == -1L) {
                return false
            }

            ItemPE.addEmcToStack(stack, consume)
        }

        if (shouldRemove) {
            ItemPE.removeEmc(stack, amount)
        }

        return true
    }
}