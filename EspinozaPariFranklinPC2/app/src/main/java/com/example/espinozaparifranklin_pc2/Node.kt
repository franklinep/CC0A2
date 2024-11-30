package com.example.espinozaparifranklin_pc2

data class Node(
    var x: Float,
    var y: Float,
    var name: String
) {
    private val nodeID: Int get() = hashCode()
}
