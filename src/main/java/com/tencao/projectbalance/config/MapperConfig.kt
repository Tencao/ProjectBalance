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

import com.tencao.projectbalance.mapper.Defaults
import com.tencao.projectbalance.mapper.ItemComponent
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import java.lang.IllegalArgumentException

object MapperConfig {
    private lateinit var config: Configuration
    private const val CATEGORY_VALUES = "zzValues"
    private const val CATEGORY_COMPLEXITIES = "zzComplexities"

    fun preInit(e: FMLPreInitializationEvent){
        config = Configuration(e.suggestedConfigurationFile, "1")
        config.load()

        if (config.hasCategory(CATEGORY_VALUES)) Defaults.values.putAll(config.getCategory(CATEGORY_VALUES).mapNotNull {
            try {
                Pair(ItemComponent(it.key).makeOutput(), it.value.int)
            } catch (e: IllegalArgumentException) {
                null
            }
        })
        if (config.hasCategory(CATEGORY_COMPLEXITIES)) Defaults.complexities.putAll(config.getCategory(CATEGORY_COMPLEXITIES).mapNotNull {
            try {
                Pair(ItemComponent(it.key).makeOutput(), it.value.int)
            } catch (e: IllegalArgumentException) {
                null
            }
        })

        config.save()
    }

    fun saveGraph() {
        Defaults.values.forEach {
            config[CATEGORY_VALUES, it.key.configName, it.value]
        }
        Defaults.complexities.forEach {
            config[CATEGORY_COMPLEXITIES, it.key.configName, it.value]
        }
        config.save()
    }
}