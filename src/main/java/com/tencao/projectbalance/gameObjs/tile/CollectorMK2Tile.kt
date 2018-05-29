package com.tencao.projectbalance.gameObjs.tile

import com.tencao.projectbalance.utils.Constants

class CollectorMK2Tile : CollectorMK1Tile(Constants.COLLECTOR_MK2_MAX, Constants.COLLECTOR_MK2_GEN) {

    override val invSize: Int
        get() = 12
}
