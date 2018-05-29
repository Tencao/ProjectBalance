package com.tencao.projectbalance.gameObjs.state

import net.minecraft.util.IStringSerializable

enum class EnumMatterType (val matter: String) : IStringSerializable {
    DARK_MATTER("dark_matter"),
    RED_MATTER("red_matter"),
    BLUE_MATTER("blue_matter");

    override fun getName(): String {
        return matter
    }

    override fun toString(): String {
        return name
    }
}
