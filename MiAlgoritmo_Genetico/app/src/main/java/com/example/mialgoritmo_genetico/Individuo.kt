package com.example.mialgoritmo_genetico

class Individuo {
    var num_ciudades =0
    var distancia:Int = 0 // aptitud
    var camino:String = ""
    var cromosoma:IntArray = IntArray(num_ciudades) { it }

    constructor(num_ciudades: Int) {
        this.num_ciudades = num_ciudades
        this.cromosoma = IntArray(num_ciudades) { it }
        this.cromosoma.shuffle()
        for (i in (0 until this.cromosoma.size)) {
            this.camino = this.camino + " - " +this.cromosoma[i].toString()
        }
    }
    fun pitagoras(p1:Punto,p2:Punto):Int{
        var x1: Float =p1.x
        var y1: Float =p1.y
        var x2: Float =p2.x
        var y2: Float =p2.y
        return Math.sqrt(((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)).toDouble()).toInt()
    }
    fun get_distancia(C: MutableList<Punto>):Int{
        this.distancia = 0
        for(i in (0 until C.size-1)){
            this.distancia += pitagoras(C[cromosoma[i]],C[cromosoma[i+1]])
        }
        return this.distancia
    }
    fun get_camino():String{
        return this.camino
    }
    fun get_camino_indices(): List<Int> {
        return this.camino.split(" - ").filter { it.isNotEmpty() }.map { it.toInt() }
    }


}