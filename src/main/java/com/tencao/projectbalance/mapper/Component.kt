package com.tencao.projectbalance.mapper

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary
import java.util.*

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

    constructor(configName: String) : this(ItemStack(Item.getByNameOrId(configName.substringBefore('@')), 0, Integer.parseInt(configName.substringAfter('@')))) {
        if (this.itemStack.item == null) throw IllegalArgumentException("$configName unknown!")
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

    override fun makeOutput(): ItemComponent { // TODO: fix usage of wildcard
        val new = ItemComponent(itemStack.copy())
        new.itemStack.count = 0
        if (new.itemStack.itemDamage == OreDictionary.WILDCARD_VALUE) new.itemStack.itemDamage = 0
        new.amount = 0
        return new
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
