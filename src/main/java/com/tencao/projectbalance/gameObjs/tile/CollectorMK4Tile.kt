package com.tencao.projectbalance.gameObjs.tile

import com.tencao.projectbalance.utils.Constants

class CollectorMK4Tile : CollectorMK1Tile(Constants.COLLECTOR_MK4_MAX, Constants.COLLECTOR_MK4_GEN) {

    override val invSize: Int
        get() = 16
}
