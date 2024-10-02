package com.example.mygeneticalgorithm

class Person {
    var nCities:Int = 0
    var dist:Int = 0
    var path:String = ""
    var chromosome:IntArray = IntArray(nCities){it}

    constructor(nCities: Int){
        this.nCities = nCities
        this.chromosome = chromosome

    }
}