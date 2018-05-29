package com.tencao.projectbalance.mapper

import java.util.*

class Node {

    private var name: String = ""

    private var shortestPath: LinkedList<Node> = LinkedList()

    private var distance: Int = Integer.MAX_VALUE

    private var adjacentNodes: MutableMap<Node, Int> = HashMap()

    fun Node(name: String) {
        this.name = name
    }

    fun addDestination(destination: Node, distance: Int) {
        adjacentNodes[destination] = distance
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getAdjacentNodes(): Map<Node, Int> {
        return adjacentNodes
    }

    fun setAdjacentNodes(adjacentNodes: MutableMap<Node, Int>) {
        this.adjacentNodes = adjacentNodes
    }

    fun getDistance(): Int {
        return distance
    }

    fun setDistance(distance: Int) {
        this.distance = distance
    }

    fun getShortestPath(): List<Node> {
        return shortestPath
    }

    fun setShortestPath(shortestPath: LinkedList<Node>) {
        this.shortestPath = shortestPath
    }

}