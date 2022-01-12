package problems.problem12

/**
 * Find the total number of paths from the [CavernNode.CavernType.START] node to
 * the [CavernNode.CavernType.END] node.
 * Uses a Single-Visit algorithm, where only 1 visit is permitted to caverns
 * of type SMALL. Caverns of type BIG are allowed repeat visits.
 * [startNode] must have CavernType START. Otherwise, 0 is returned.
 * If there exists an infinite loop between BIG caverns, the behavior is undefined.
 */
fun findTotalPathsSingleVisit(startNode: CavernNode): Int {
    if (startNode.type != CavernNode.CavernType.START) {
        return 0
    }

    // Local helper function to help us recursively explore every potential path. Anytime we
    // find a path that goes to the END node, we increment our counter by 1.
    var numPaths = 0
    fun walkPath(currentNode: CavernNode, visitedNodes: List<CavernNode>) {
        if (currentNode.type == CavernNode.CavernType.END) {
            // we're at the end! increment our counter and return, since we're now done with
            // this path.
            numPaths++
            return
        }

        // Recurse down every node which could still yield us a valid path
        for (node in currentNode.connectedNodes) {
            // skip over non-BIG caverns which we've already visited
            if (node.type != CavernNode.CavernType.BIG && visitedNodes.contains(node)) continue
            walkPath(node, visitedNodes + node)
        }
    }

    // walk all possible paths, starting at the START node (and noting that we've already visited the START node).
    // return the number of paths found after this traversal.
    walkPath(startNode, listOf(startNode))
    return numPaths
}

/**
 * Find the total number of paths from the [CavernNode.CavernType.START] node to
 * the [CavernNode.CavernType.END] node.
 * Uses a Multi-Visit algorithm, where only 1 visit is permitted to caverns
 * of type SMALL EXCEPT for 1 cavern, which is permitted 2 visits. Caverns of
 * type BIG are allowed an infinite number of repeat visits.
 * [startNode] must have CavernType START. Otherwise, 0 is returned.
 * If there exists an infinite loop between BIG caverns, the behavior is undefined.
 */
fun findTotalPathsMultiVisit(startNode: CavernNode): Int {
    if (startNode.type != CavernNode.CavernType.START) {
        return 0
    }

    // Local helper function to help us recursively explore every potential path. Anytime we
    // find a path that goes to the END node, we add that path to the set (in order to de-dupe paths).
    // Store the string representation of nodes to avoid infinite-loops in CavernNode equality checking.
    var paths: MutableSet<List<String>> = mutableSetOf()
    fun walkPath(currentNode: CavernNode,
                 visitedNodes: List<CavernNode>,
                 repeatVisitHasOccurred: Boolean,
                 pathSoFar: List<String>) {
        if (currentNode.type == CavernNode.CavernType.END) {
            // we're at the end! increment our counter and return, since we're now done with
            // this path.
            paths.add(pathSoFar)
            return
        }

        // Recurse down every node which could still yield us a valid path
        for (node in currentNode.connectedNodes) {
            val isBigNode = node.type == CavernNode.CavernType.BIG
            val doubleRepeatAllowed = node.type == CavernNode.CavernType.SMALL && !repeatVisitHasOccurred
            val hasNotAlreadyVisited = node.type != CavernNode.CavernType.BIG && !visitedNodes.contains(node)

            // recurse if one of the following is true:
            // - the node is a BIG node (visits are always permitted, even repeat ones)
            // - the node is not a BIG node and we have not already visited it
            if (isBigNode || hasNotAlreadyVisited) {
                walkPath(
                    node,
                    visitedNodes + node,
                    repeatVisitHasOccurred = repeatVisitHasOccurred,
                    pathSoFar = pathSoFar + node.cavernName
                )
            }

            // additionally, if the node is a SMALL node and we haven't yet used our 1 repeat visit, recurse again
            // to find all the paths where this is the point where we use the repeat visit
            if (doubleRepeatAllowed) {
                walkPath(
                    node,
                    visitedNodes + node,
                    repeatVisitHasOccurred = true,
                    pathSoFar = pathSoFar + node.cavernName
                )
            }
        }
    }

    // walk all possible paths, starting at the START node (and noting that we've already visited the START node).
    // return the number of paths found after this traversal.
    walkPath(startNode, listOf(startNode), repeatVisitHasOccurred = false, pathSoFar = listOf(startNode.cavernName))
    return paths.size
}

/**
 * Given a list of raw cavern connections (where the caverns are simply represented as
 * Strings and are unconnected), build a typed Cavern graph from [CavernNode] objects.
 * Returns the node with type [CavernNode.CavernType.START], or null if the resulting
 * graph is invalid (for example, if no START cavern exists).
 */
fun constructCavern(cavernConnections: List<RawCavernConnection>): CavernNode? {
    // helper function for determining the type of a cavern node
    fun extractType(name: String): CavernNode.CavernType {
        return when {
            name.toLowerCase() == "start" -> CavernNode.CavernType.START
            name.toLowerCase() == "end" -> CavernNode.CavernType.END
            name.firstOrNull()?.isUpperCase() == true -> CavernNode.CavernType.BIG
            else -> CavernNode.CavernType.SMALL
        }
    }

    // going through every connection, accumulate
    val cavernMap: MutableMap<String, CavernNode> = mutableMapOf()
    var startNode: CavernNode? = null
    for (connection in cavernConnections) {
        // create the node for the caverns, if they don't already exist
        if (!cavernMap.containsKey(connection.firstCavern())) {
            val node = CavernNode(extractType(connection.firstCavern()), mutableListOf(), connection.firstCavern())
            cavernMap.put(connection.firstCavern(), node)
            if (node.type == CavernNode.CavernType.START) startNode = node // keep track of the START node
        }
        if (!cavernMap.containsKey(connection.secondCavern())) {
            val node = CavernNode(extractType(connection.secondCavern()), mutableListOf(), connection.secondCavern())
            cavernMap.put(connection.secondCavern(), node)
            if (node.type == CavernNode.CavernType.START) startNode = node // keep track of the START node
        }

        // connect each cavern to each other, if they aren't already connected
        val firstNode = cavernMap[connection.firstCavern()]!! // we can use !! since we just added the node above
        val secondNode = cavernMap[connection.secondCavern()]!!

        if (!firstNode.connectedNodes.contains(secondNode)) firstNode.connectedNodes.add(secondNode)
        if (!secondNode.connectedNodes.contains(firstNode)) secondNode.connectedNodes.add(firstNode)
    }

    // return the START node (could be null if it was never created)
    return startNode
}

/**
 * Represents a connection between 2 raw cavern nodes in an unbuilt Cavern graph. Each String in
 * the connection is the name of a cavern in the unbuilt Cavern graph.
 * This class is used internally to build up a valid Cavern graph composed of [CavernNode] objects.
 */
inline class RawCavernConnection(private val cavernConnection: Pair<String, String>) {
    fun firstCavern(): String = cavernConnection.first
    fun secondCavern(): String = cavernConnection.second
}

/**
 * Represents a node in the Cavern graph.
 * WARNING: BE VERY CAREFUL WITH EQUALITY CHECKS WITH THIS TYPE.
 * Due to the cyclical references of this type (since it references
 * other CavernNodes which may reference this node, a property of the
 * graph this is representing) the equality check Kotlin generates
 * as part of the data class can get into an infinite loop. This sometimes
 * occurs for things like equality checking of lists or sets, for example.
 */
data class CavernNode(val type: CavernType, val connectedNodes: MutableList<CavernNode>, val cavernName: String) {
    /**
     * Represents the type of cavern this node represents.
     */
    enum class CavernType {
        START,
        END,
        BIG,
        SMALL
    }

    /**
     * Returns a String representation of this Node.
     * Overriding this method is important as otherwise, Kotlin will get into an infinite loop trying
     * to print the graph (since each node has its neighbor nested, which may have this node nested within).
     */
    override fun toString(): String {
        return "$cavernName ($type): [${connectedNodes.joinToString(" ") { it.cavernName }}]"
    }
}