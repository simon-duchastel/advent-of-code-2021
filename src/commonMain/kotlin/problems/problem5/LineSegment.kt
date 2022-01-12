package problems.problem5

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Takes a list [input] of line segments and returns the number the number of points where two or more
 * of these line segments intersect.
 * Only horizontal or vertical lines are considered (ie. no diagonals).
 * Uses an inefficient brute-force algorithm. This may be overly computationally-intensive for large lists of
 * line segments or for lines with large values. Consider using [findNumOverlappingPoints] instead.
 */
fun findNumNonDiagonallyOverlappingPointsBruteForce(input: List<LineSegment>): Int {
    // Find the coordinate representing the largest x value in the list of segments and the largest y value in the list of segments.
    val maxCoordinate = Coordinate(
        input.fold(Int.MIN_VALUE) { x, line -> max(x, max(line.start.getX(), line.end.getX())) } to
                input.fold(Int.MIN_VALUE) { y, line -> max(y, max(line.start.getY(), line.end.getY())) }
    )

    // Fold over all the (x,y) points and accumulate all points that intersect with 2 or more lines
    return (0..maxCoordinate.getX()).fold(0) { numPoints, x ->
        (0..maxCoordinate.getY()).fold(numPoints) { numPointsNested, y ->
            val numIntersections = input.fold(0) { intersections, segment ->
                if (segment.doesIntersectNonDiagonallyWith(Coordinate(x to y))) intersections + 1 else intersections
            }
            if (numIntersections >= 2) (numPointsNested + 1) else numPointsNested
        }
    }
}

/**
 * Takes a list [input] of line segments and returns the number the number of points where two or more
 * of these line segments intersect.
 * Only horizontal or vertical lines are considered (ie. no diagonals).
 */
fun findNumNonDiagonallyOverlappingPoints(input: List<LineSegment>): Int {
    // Find the coordinate representing the largest x value in the list of segments and the largest y value in the list of segments.
    val maxCoordinate = Coordinate(
        input.fold(Int.MIN_VALUE) { x, line -> max(x, max(line.start.getX(), line.end.getX())) } to
                input.fold(Int.MIN_VALUE) { y, line -> max(y, max(line.start.getY(), line.end.getY())) }
    )

    // when creating the frequency map, add 1 to each max coordinate since the coordinates are zero-indexed
    val frequencyMap: Array<Array<Int>> = Array(maxCoordinate.getY() + 1) { Array(maxCoordinate.getX() + 1) { 0 } }
    input.filter { it.isHorizontal() || it.isVertical() } // only consider horizontal or vertical lines
        .forEach { segment ->
            // Mark every point on this segment on the frequency graph
            if (segment.isHorizontal()) {
                val y = segment.start.getY()
                for (x in min(segment.start.getX(), segment.end.getX())..max(segment.start.getX(), segment.end.getX())) {
                    frequencyMap[y][x]++
                }
            } else if (segment.isVertical()) {
                val x = segment.start.getX()
                for (y in min(segment.start.getY(), segment.end.getY())..max(segment.start.getY(), segment.end.getY())) {
                    frequencyMap[y][x]++
                }
            }
        }
    return frequencyMap.sumBy { row -> row.count { it >= 2 } } // count the number of values > 2, ie. 2 or more segments intersected
}

/**
 * Takes a list [input] of line segments and returns the number the number of points where two or more
 * of these line segments intersect.
 * Takes into account horizontal, vertical, and 45-degree diagonal lines. Any diagonal lines with angles other
 * than 45 degrees are not considered.
 */
fun findNumOverlappingPoints(input: List<LineSegment>): Int {
    // Find the coordinate representing the largest x value in the list of segments and the largest y value in the list of segments.
    val maxCoordinate = Coordinate(
        input.fold(Int.MIN_VALUE) { x, line -> max(x, max(line.start.getX(), line.end.getX())) } to
                input.fold(Int.MIN_VALUE) { y, line -> max(y, max(line.start.getY(), line.end.getY())) }
    )

    // when creating the frequency map, add 1 to each max coordinate since the coordinates are zero-indexed
    val frequencyMap: Array<Array<Int>> = Array(maxCoordinate.getY() + 1) { Array(maxCoordinate.getX() + 1) { 0 } }
    input.filter { it.isHorizontal() || it.isVertical() || it.is45DegreesDiagonal() } // only consider horizontal, vertical, and 45-degree diagonal lines
        .forEach { segment ->
            // Mark every point on this segment on the frequency graph
            when {
                segment.isHorizontal() -> {
                    val y = segment.start.getY()
                    for (x in min(segment.start.getX(), segment.end.getX())..max(
                        segment.start.getX(),
                        segment.end.getX()
                    )) {
                        frequencyMap[y][x]++
                    }
                }
                segment.isVertical() -> {
                    val x = segment.start.getX()
                    for (y in min(segment.start.getY(), segment.end.getY())..max(
                        segment.start.getY(),
                        segment.end.getY()
                    )) {
                        frequencyMap[y][x]++
                    }
                }
                segment.is45DegreesDiagonal() -> {
                    for (offset in 0..abs(segment.end.getX() - segment.start.getX())) {
                        val x = min(segment.start.getX(), segment.end.getX()) + offset
                        val y = if ((segment.start.getX() < segment.end.getX() && segment.start.getY() < segment.end.getY()) ||
                            (segment.end.getX() < segment.start.getX() && segment.end.getY() < segment.start.getY())
                        ) {
                            // case where x and y increase together
                            min(segment.start.getY(), segment.end.getY()) + offset
                        } else {
                            // case where x increases while y decreases (and vice versa)
                            max(segment.start.getY(), segment.end.getY()) - offset
                        }
                        frequencyMap[y][x]++
                    }
                }
            }
        }

    return frequencyMap.sumBy { row -> row.count { it >= 2 } } // count the number of values > 2, ie. 2 or more segments intersected
}

/**
 * Represents a line segment.
 */
data class LineSegment(val start: Coordinate, val end: Coordinate) {

    /**
     * Returns true if [coordinate] intersects with this line segment, false otherwise.
     * Only vertical or horizontal line segments are considered - diagonal intersections will
     * return false.
     */
    fun doesIntersectNonDiagonallyWith(coordinate: Coordinate): Boolean {
        // if this segment is not a horizontal or vertical line, return false
        if (!isHorizontal() && !isVertical()) return false

        // If this segment is a horizontal line and the coordinate lies in-between it's y coordinates, return true
        if (start.getX() == end.getX() && start.getX() == coordinate.getX()) {
            // Case if start.y <= end.y
            if (start.getY() <= end.getY() && start.getY() <= coordinate.getY() && coordinate.getY() <= end.getY()) return true
            // Case if start.y > end.y
            else if (end.getY() <= coordinate.getY() && coordinate.getY() <= start.getY()) return true
        }

        // If this segment is a vertical line and the coordinate lies in-between it's x coordinates, return true
        if (start.getY() == end.getY() && start.getY() == coordinate.getY()) {
            // Case if start.x <= end.x
            if (start.getX() <= end.getX() && start.getX() <= coordinate.getX() && coordinate.getX() <= end.getX()) return true
            // Case if start.x > end.x
            else if (end.getX() <= coordinate.getX() && coordinate.getX() <= start.getX()) return true
        }

        // If we weren't able to find any intersections, return false
        return false
    }

    /**
     * Returns true if this line segment is horizontal, false otherwise.
     */
    fun isHorizontal(): Boolean = start.getY() == end.getY()

    /**
     * Returns true if this line segment is vertical, false otherwise.
     */
    fun isVertical(): Boolean = start.getX() == end.getX()

    /**
     * Returns true if this line segments is diagonal at exactly 45 degrees
     */
    fun is45DegreesDiagonal(): Boolean = abs(start.getX() - end.getX()) == abs(start.getY() - end.getY())

    companion object {
        /**
         * Takes an [input] as a String in 'x2,y2 -> x2,y2' form and returns the corresponding [LineSegment]
         * representation, or null if [input] is invalid. 'x1', 'y1', 'x2', and 'y2' must be valid integers, with
         * 'x1,y1' representing the start of the line segment and 'x2,y2' representing the end of the segment.
         */
        fun fromString(input: String): LineSegment? {
            val splitSegment = input.split(" -> ", limit = 2)
            if (splitSegment.size != 2) return null
            return LineSegment(
                Coordinate.fromString(splitSegment[0]) ?: return null,
                Coordinate.fromString(splitSegment[1]) ?: return null
            )
        }
    }
}

/**
 * Represents a coordinate in (x,y) form, where the first element is the x
 * value and the second element is the y value.
 */
inline class Coordinate(private val coordinate: Pair<Int, Int>) {
    /**
     * Returns the 'x' component of this Coordinate.
     */
    fun getX() = coordinate.first

    /**
     * Returns the 'y' component of this Coordinate.
     */
    fun getY() = coordinate.second

    companion object {
        /**
         * Takes an [input] as a String in 'x,y' form and returns the corresponding [Coordinate]
         * representation, or null if [input] is invalid. 'x' and 'y' must be valid integers.
         */
        fun fromString(input: String): Coordinate? {
            val splitCoordinate = input.split(',', limit = 2)
            if (splitCoordinate.size != 2) return null
            return Coordinate(
                (splitCoordinate[0].toIntOrNull() ?: return null)
                    to (splitCoordinate[1].toIntOrNull() ?: return null)
            )
        }
    }
}