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

package com.tencao.projectbalance.mapper

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import java.lang.IllegalArgumentException
import java.lang.UnsupportedOperationException
import java.util.*

/**
 * Part of FoodBuffs by Bluexin.
 *
 * @author Bluexin
 */
abstract class Component(var amount: Int) {
    abstract fun corresponds(other: Component?): Boolean

    val value by lazy { Graph[makeOutput()].value }
    val complexity by lazy { Graph[makeOutput()].complexity }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Component) return false
        if (amount != other.amount) return false
        return true
    }

    override fun hashCode() = amount

    open fun makeOutput(): ItemComponent {
        amount = 0
        return this as ItemComponent
    }

    abstract val configName: String
}

class ItemComponent(val itemStack: ItemStack) : Component(itemStack.count) {
    override val configName by lazy { "${itemStack.item.registryName}@${itemStack.itemDamage}" }

    constructor(configName: String) : this(ItemStack(Item.getByNameOrId(configName.substringBefore('@')), 1, Integer.parseInt(configName.substringAfter('@')))) {
        if (this.itemStack == ItemStack.EMPTY) throw IllegalArgumentException("$configName unknown!")
    }

    override fun corresponds(other: Component?) = this === other || (other is ItemComponent && ItemStack.areItemsEqual(this.itemStack, other.itemStack)) || (other is ODComponent && other.corresponds(this))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (!super.equals(other)) return false
        if (other !is ItemComponent) return false

        return (other is ItemComponent && ItemStack.areItemStacksEqual(this.itemStack, other.itemStack)) || (other is ODComponent && other == this)
    }

    override fun hashCode() = 31 * super.hashCode() + itemStack.hashCodeImpl()

    override fun toString() = "ItemComponent(itemStack=$itemStack)"

    override fun makeOutput(): ItemComponent {
        return ItemComponent(itemStack.copy())
    }
}

class ODComponent(val itemStacks: List<ItemStack>) : Component(itemStacks[0].count) {
    override val configName: String
        get() = throw UnsupportedOperationException()

    override fun corresponds(other: Component?) = this === other || (other is ODComponent && other.itemStacks.map { Pair(it.item, it.itemDamage) } == this.itemStacks.map { Pair(it.item, it.itemDamage) }) || (other is ItemComponent && this.itemStacks.any { ItemStack.areItemsEqual(other.itemStack, it) })

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (!super.equals(other)) return false
        if (other !is ODComponent) return false

        return (other is ODComponent && other.itemStacks.map { Triple(it.item, it.itemDamage, it.count) } == this.itemStacks.map { Triple(it.item, it.itemDamage, it.count) }) || (other is ItemComponent && this.itemStacks.any { ItemStack.areItemStacksEqual(other.itemStack, it) })
    }

    override fun hashCode() = 31 * super.hashCode() + itemStacks.map { it.hashCodeImpl() }.hashCode()

    override fun toString() = "ODComponent(itemStacks=$itemStacks)"

    override fun makeOutput() = Graph.getODEntry(this)
}

private fun ItemStack.hashCodeImpl() = Objects.hash(item.registryName.toString(), count, metadata)
