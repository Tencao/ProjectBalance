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

package com.tencao.projectbalance.config

import com.tencao.projectbalance.ProjectBCore
import com.tencao.projectbalance.mapper.Defaults
import moze_intel.projecte.emc.SimpleStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import java.io.File
import java.lang.IllegalArgumentException

object MapperConfig {
    private lateinit var config: Configuration
    private const val CATEGORY_VALUES = "zzValues"
    private const val CATEGORY_COMPLEXITIES = "zzComplexities"

    fun preInit(e: FMLPreInitializationEvent){
        config = Configuration(File(File(e.modConfigurationDirectory, ProjectBCore.NAME), "mapper.cfg"), "2")
        config.load()

        if (config.hasCategory(CATEGORY_VALUES)) Defaults.values.putAll(config.getCategory(CATEGORY_VALUES).mapNotNull {
            val strings = it.key.split(":")
            if (strings.isNotEmpty()) {
                try {
                    Pair(SimpleStack(ResourceLocation(strings[0], strings[1]), strings[2].toInt()), it.value.int)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
            else null
        })
        if (config.hasCategory(CATEGORY_COMPLEXITIES)) Defaults.complexities.putAll(config.getCategory(CATEGORY_COMPLEXITIES).mapNotNull {
            val strings = it.key.split(":")
            if (strings.isNotEmpty()) {
                try {
                    Pair(SimpleStack(ResourceLocation(strings[0], strings[1]), strings[2].toInt()), it.value.int)
                } catch (e: IllegalArgumentException) {
                    null
                }
            } else null
        })

        saveGraph()
    }

    fun saveGraph() {
        Defaults.values.forEach {
            config[CATEGORY_VALUES, "${it.key.id}:${it.key.damage}", it.value]
        }
        Defaults.complexities.forEach {
            config[CATEGORY_COMPLEXITIES, "${it.key.id}:${it.key.damage}", it.value]
        }
        config.save()
    }
}