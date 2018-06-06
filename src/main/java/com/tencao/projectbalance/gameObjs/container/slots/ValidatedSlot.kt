package com.tencao.projectbalance.gameObjs.container.slots

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler
import java.util.function.Predicate

// Partial copy of SlotItemhandler with a validator
class ValidatedSlot(itemHandler: IItemHandler, index: Int, xPosition: Int, yPosition: Int, private val validator: Predicate<ItemStack>) : SlotItemHandler(itemHandler, index, xPosition, yPosition) {

    override fun isItemValid(stack: ItemStack): Boolean {
        return super.isItemValid(stack) && validator.test(stack)
    }

}
