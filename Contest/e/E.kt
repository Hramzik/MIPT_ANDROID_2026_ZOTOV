import java.util.Scanner

// Submission id 157667656
fun Int.isBetween(a: Int, b: Int): Boolean = this in minOf(a, b)..maxOf(a, b)

data class Point(val x: Int, val y: Int) {

    operator fun minus(other: Point): Vector = Vector(x - other.x, y - other.y)

    fun getOrientationFrom(segment: Segment): Long {
        val v1 = segment.point2 - segment.point1
        val v2 = this - segment.point1
        return v1.cross(v2)
    }

    fun isOnSegment(segment: Segment): Boolean {
        val v1 = segment.point1 - segment.point2
        val v2 = this - segment.point1
        if (v1.cross(v2) != 0L) return false

        val xInRange = x.isBetween(segment.point1.x, segment.point2.x)
        val yInRange = y.isBetween(segment.point1.y, segment.point2.y)
        return xInRange && yInRange
    }
}

data class Vector(val dx: Int, val dy: Int) {
    fun cross(other: Vector): Long = dx.toLong() * other.dy - dy.toLong() * other.dx
}

class Segment(val point1: Point, val point2: Point) {
    fun doesIntersectWith(otherSegment: Segment): Boolean {
        val hisP1Orientation = otherSegment.point1.getOrientationFrom(this)
        val hisP2Orientation = otherSegment.point2.getOrientationFrom(this)
        val myP1Orientation = point1.getOrientationFrom(otherSegment)
        val myP2Orientation = point2.getOrientationFrom(otherSegment)

        if (hisP1Orientation * hisP2Orientation < 0 && myP1Orientation * myP2Orientation < 0)
                return true

        if (listOf(point1, point2).any { it.isOnSegment(otherSegment) }) return true
        if (listOf(otherSegment.point1, otherSegment.point2).any { it.isOnSegment(this) })
                return true

        return false
    }
}

fun main() {
    val scanner = Scanner(System.`in`)

    val Ax = scanner.nextInt()
    val Ay = scanner.nextInt()
    val Bx = scanner.nextInt()
    val By = scanner.nextInt()
    val road = Segment(Point(Ax, Ay), Point(Bx, By))

    val n = scanner.nextInt()
    var intersectionCount = 0

    repeat(n) {
        val riverX1 = scanner.nextInt()
        val riverY1 = scanner.nextInt()
        val riverX2 = scanner.nextInt()
        val riverY2 = scanner.nextInt()
        val river = Segment(Point(riverX1, riverY1), Point(riverX2, riverY2))
        if (road.doesIntersectWith(river)) {
            intersectionCount++
        }
    }

    println(intersectionCount)
}
