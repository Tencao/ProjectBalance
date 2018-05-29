package com.tencao.projectbalance.gameObjs.tile

import com.tencao.projectbalance.utils.Constants

class CollectorMK3Tile : CollectorMK1Tile(Constants.COLLECTOR_MK3_MAX, Constants.COLLECTOR_MK3_GEN) {

    override val invSize: Int
        get() = 16
}
