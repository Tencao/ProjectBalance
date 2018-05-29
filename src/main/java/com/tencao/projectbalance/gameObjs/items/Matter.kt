package com.tencao.projectbalance.gameObjs.items

import moze_intel.projecte.gameObjs.items.Matter
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class Matter: Matter() {
    private val names = arrayOf("dark", "red", "blue")

    override fun getUnlocalizedName(stack: ItemStack?): String {
        return super.getUnlocalizedName() + "_" + names[stack!!.itemDamage]
    }

    @SideOnly(Side.CLIENT)
    override fun getSubItems(cTab: CreativeTabs?, list: NonNullList<ItemStack>) {
        if (isInCreativeTab(cTab)) {
            for (i in 0..2) {
                list.add(ItemStack(this, 1, i))
            }
        }
    }
}