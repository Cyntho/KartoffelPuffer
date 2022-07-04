package org.cyntho.fh.kotlin.kartoffelpuffer.net

@kotlinx.serialization.Serializable
data class LayoutWrapper(
    val id: Int,
    val sizeX: Int,
    val sizeY: Int,
    val name: String,
    val created: Long,
    val validFrom: Long,
    val active: Boolean,
    var data: MutableList<Int>?
) {
    fun fillFromArray2D(arr: Array2D){
        data = mutableListOf()
        var counter = 0
        for (x in 0 until arr.height){
            for (y in 0 until arr.width){
                data!!.add(counter++, arr.arrayContents[y][x])
            }
        }
    }

    fun asArray2D(): Array2D? {
        if (data == null) return null
        val arr: Array2D = Array2D(sizeX, sizeY)

        var counter = 0
        for (y in 0 until sizeY){
            for (x in 0 until sizeX ){
                arr.arrayContents[x][y] = data!![counter++]
            }
        }

        return arr
    }
}

