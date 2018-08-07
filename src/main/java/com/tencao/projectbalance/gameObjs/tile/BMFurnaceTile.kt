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

import moze_intel.projecte.utils.ItemHelper
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraftforge.items.ItemHandlerHelper

class BMFurnaceTile: DMFurnaceTile(2, 6) {

    override fun getInvSize(): Int{
        return 13
    }

    override fun smeltItem() {
        val toSmelt = inputInventory.getStackInSlot(0)
        val smeltResult = FurnaceRecipes.instance().getSmeltingResult(toSmelt).copy()

        if (world.rand.nextFloat() < getOreDoubleChance() && ItemHelper.getOreDictionaryName(toSmelt).startsWith("ore")) {
            smeltResult.grow(smeltResult.count)
        }
        smeltResult.grow(1)

        ItemHandlerHelper.insertItemStacked(outputInventory, smeltResult, false)

        toSmelt.shrink(1)
    }


}