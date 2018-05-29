package com.tencao.projectbalance.gameObjs.tile

interface IEmcGen {

    /**
     * This is used to make a tile trigger it's EMC Generation,
     * useful for increasing generation through items
     * @param extra If true, will only generate at half the rate.
     */
    fun updateEmc(extra: Boolean)
}