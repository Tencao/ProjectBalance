package com.tencao.projectbalance.mapper

import java.util.*

object GraphMapper {

    fun calculateShortestPathFromSource(graph: Graph, source: Node): Graph {

        source.setDistance(0)

        val settledNodes = HashSet<Node>()
        val unsettledNodes = HashSet<Node>()
        unsettledNodes.add(source)

        while (unsettledNodes.size != 0) {
            val currentNode = getLowestDistanceNode(unsettledNodes)
            unsettledNodes.remove(currentNode)
            for (adjacencyPair in currentNode!!.getAdjacentNodes()) {
                val adjacentNode = adjacencyPair.key
                val edgeWeigh = adjacencyPair.value

                if (!settledNodes.contains(adjacentNode)) {
                    CalculateMinimumDistance(adjacentNode, edgeWeigh, currentNode)
                    unsettledNodes.add(adjacentNode)
                }
            }
            settledNodes.add(currentNode)
        }
        return graph
    }

    private fun CalculateMinimumDistance(evaluationNode: Node, edgeWeigh: Int, sourceNode: Node) {
        val sourceDistance = sourceNode.getDistance()
        if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeigh)
            val shortestPath = LinkedList(sourceNode.getShortestPath())
            shortestPath.add(sourceNode)
            evaluationNode.setShortestPath(shortestPath)
        }
    }

    private fun getLowestDistanceNode(unsettledNodes: Set<Node>): Node? {
        var lowestDistanceNode: Node? = null
        var lowestDistance = Integer.MAX_VALUE
        for (node in unsettledNodes) {
            val nodeDistance = node.getDistance()
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance
                lowestDistanceNode = node
            }
        }
        return lowestDistanceNode
    }
}