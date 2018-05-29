package com.tencao.projectbalance.gameObjs.state

import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum

object PEStateProps {

    val TIER_PROP: IProperty<EnumMatterType> = PropertyEnum.create("tier", EnumMatterType::class.java)
}