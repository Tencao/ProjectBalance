package com.tencao.projectbalance.gameObjs.tile

interface ICraftingGen {

    fun addTomeToCounter(tile: DMPedestalTile)

    fun removeTomeFromCounter(tile: DMPedestalTile)
}