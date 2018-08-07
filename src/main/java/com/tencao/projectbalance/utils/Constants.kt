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

package com.tencao.projectbalance.utils

import com.tencao.projectbalance.config.ProjectBConfig
import java.text.DecimalFormat

object Constants {

    val EMC_FORMATTER = DecimalFormat("#,###.##")

    val COLLECTOR_LIGHT_VALS = floatArrayOf(0.4375f, 0.6875f, 1.0f, 1.0f)

    val COLLECTOR_MK1_MAX = ProjectBConfig.tweaks.CollectorMaxMK1
    val COLLECTOR_MK2_MAX = ProjectBConfig.tweaks.CollectorMaxMK2
    val COLLECTOR_MK3_MAX = ProjectBConfig.tweaks.CollectorMaxMK3
    val COLLECTOR_MK4_MAX = ProjectBConfig.tweaks.CollectorMaxMK4
    val COLLECTOR_MK1_GEN = ProjectBConfig.tweaks.CollectorRateMK1.toFloat()
    val COLLECTOR_MK2_GEN = ProjectBConfig.tweaks.CollectorRateMK2.toFloat()
    val COLLECTOR_MK3_GEN = ProjectBConfig.tweaks.CollectorRateMK3.toFloat()
    val COLLECTOR_MK4_GEN = ProjectBConfig.tweaks.CollectorRateMK4.toFloat()

    val RELAY_MK1_OUTPUT = ProjectBConfig.tweaks.TransferRateMK1
    val RELAY_MK2_OUTPUT = ProjectBConfig.tweaks.TransferRateMK2
    val RELAY_MK3_OUTPUT = ProjectBConfig.tweaks.TransferRateMK3
    val RELAY_MK4_OUTPUT = ProjectBConfig.tweaks.TransferRateMK4

    val RELAY_MK1_MAX = ProjectBConfig.tweaks.RelayMaxMK1
    val RELAY_MK2_MAX = ProjectBConfig.tweaks.RelayMaxMK2
    val RELAY_MK3_MAX = ProjectBConfig.tweaks.RelayMaxMK3
    val RELAY_MK4_MAX = ProjectBConfig.tweaks.RelayMaxMK4

    val RELAY_MK1_BONUS = ProjectBConfig.tweaks.RelayBonusMK1.toFloat() / 20
    val RELAY_MK2_BONUS = ProjectBConfig.tweaks.RelayBonusMK2.toFloat() / 20
    val RELAY_MK3_BONUS = ProjectBConfig.tweaks.RelayBonusMK3.toFloat() / 20
    val RELAY_MK4_BONUS = ProjectBConfig.tweaks.RelayBonusMK4.toFloat() / 20

    val CONDENSED_RELAY_MK1_OUTPUT = ProjectBConfig.tweaks.TransferRateMK1 * 6
    val CONDENSED_RELAY_MK2_OUTPUT = ProjectBConfig.tweaks.TransferRateMK2 * 6
    val CONDENSED_RELAY_MK3_OUTPUT = ProjectBConfig.tweaks.TransferRateMK3 * 6
    val CONDENSED_RELAY_MK4_OUTPUT = ProjectBConfig.tweaks.TransferRateMK4 * 6

    val CONDENSED_RELAY_MK1_MAX = ProjectBConfig.tweaks.RelayMaxMK1 + ProjectBConfig.tweaks.CollectorMaxMK1 * 5
    val CONDENSED_RELAY_MK2_MAX = ProjectBConfig.tweaks.RelayMaxMK2 + ProjectBConfig.tweaks.CollectorMaxMK2 * 5
    val CONDENSED_RELAY_MK3_MAX = ProjectBConfig.tweaks.RelayMaxMK3 + ProjectBConfig.tweaks.CollectorMaxMK3 * 5
    val CONDENSED_RELAY_MK4_MAX = ProjectBConfig.tweaks.RelayMaxMK4 + ProjectBConfig.tweaks.CollectorMaxMK4 * 5

    val CONDENSED_RELAY_MK1_GEN = COLLECTOR_MK1_GEN * 5 + RELAY_MK1_BONUS * 5
    val CONDENSED_RELAY_MK2_GEN = COLLECTOR_MK2_GEN * 5 + RELAY_MK2_BONUS * 5
    val CONDENSED_RELAY_MK3_GEN = COLLECTOR_MK3_GEN * 5 + RELAY_MK3_BONUS * 5
    val CONDENSED_RELAY_MK4_GEN = COLLECTOR_MK4_GEN * 5 + RELAY_MK4_BONUS * 5

    val POWER_FLOWER_MK1_MAX = CONDENSED_RELAY_MK1_MAX * 6
    val POWER_FLOWER_MK2_MAX = CONDENSED_RELAY_MK2_MAX * 6
    val POWER_FLOWER_MK3_MAX = CONDENSED_RELAY_MK3_MAX * 6
    val POWER_FLOWER_MK4_MAX = CONDENSED_RELAY_MK4_MAX * 6

    val POWER_FLOWER_MK1_GEN = CONDENSED_RELAY_MK1_GEN * 6
    val POWER_FLOWER_MK2_GEN = CONDENSED_RELAY_MK2_GEN * 6
    val POWER_FLOWER_MK3_GEN = CONDENSED_RELAY_MK3_GEN * 6
    val POWER_FLOWER_MK4_GEN = CONDENSED_RELAY_MK4_GEN * 6


    const val COLLECTOR1_GUI = 0
    const val COLLECTOR2_GUI = 1
    const val COLLECTOR3_GUI = 2
    const val COLLECTOR4_GUI = 3
    const val RELAY1_GUI = 4
    const val RELAY2_GUI = 5
    const val RELAY3_GUI = 6
    const val RELAY4_GUI = 7
    const val CONDENSED_RELAY1_GUI = 8
    const val CONDENSED_RELAY2_GUI = 9
    const val CONDENSED_RELAY3_GUI = 10
    const val CONDENSED_RELAY4_GUI = 11
    const val POWERFLOWER1_GUI = 12
    const val POWERFLOWER2_GUI = 13
    const val POWERFLOWER3_GUI = 14
    const val POWERFLOWER4_GUI = 15
    const val CONDENSER_GUI = 16
    const val CONDENSER_MK2_GUI = 17
    const val TRANSMUTE_STONE_GUI = 18
    const val TRANSMUTATION_GUI = 19
    const val DM_FURNACE_GUI = 20
    const val RM_FURNACE_GUI = 21
    const val BM_FURNACE_GUI = 22
}