package org.cyntho.fh.kotlin.kartoffelpuffer.data

class TableManager {

    private var gridSizeX: Int = 5
    private var gridSizeY: Int = 10

    private var validFrom: Long = 0
    private lateinit var name: String
    private lateinit var tableGrid: Array<Array<Int>>


    private fun TableManager(){

    }

    fun setGrid(x: Int, y: Int, value: Int){

    }

    fun save() {

    }

    fun load(): Boolean {

        return false;
    }
}