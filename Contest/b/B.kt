// Submission id 157505941
class ModuledMatrix2x2(a00: Long, a01: Long, a10: Long, a11: Long, val module: Long)
{
    class ModuledLong(value: Long, var module: Long) {
        var value = value % module
        operator fun plus(other: ModuledLong) = ModuledLong((value + other.value) % module, module)
        operator fun times(other: ModuledLong) = ModuledLong((value * other.value) % module, module)
    }

    constructor(_a00: ModuledLong, _a01: ModuledLong, _a10: ModuledLong, _a11: ModuledLong):
            this(_a00.value, _a01.value, _a10.value, _a11.value, _a00.module)

    val _a00 = ModuledLong(a00, module)
    val _a01 = ModuledLong(a01, module)
    val _a10 = ModuledLong(a10, module)
    val _a11 = ModuledLong(a11, module)

    val a00: Long get() = _a00.value
    val a01: Long get() = _a01.value
    val a10: Long get() = _a10.value
    val a11: Long get() = _a11.value

    fun pow(n: Long): ModuledMatrix2x2
    {
        if (n == 0L) return ModuledMatrix2x2(1, 0, 0, 1, module)
        
        val halfPower = pow(n / 2)
        return if (n % 2 == 0L)
        {
            halfPower * halfPower
        }
        else
        {
            halfPower * halfPower * this
        }
    }

    operator fun times(other: ModuledMatrix2x2): ModuledMatrix2x2
    {
        val result00 = _a00 * other._a00 + _a01 * other._a10
        val result01 = _a00 * other._a01 + _a01 * other._a11
        val result10 = _a10 * other._a00 + _a11 * other._a10
        val result11 = _a10 * other._a01 + _a11 * other._a11
        
        return ModuledMatrix2x2(result00, result01, result10, result11)
    }
}

fun main()
{
    val (n, m) = readln().split(" ").map { it.toLong() }
    val matrix = ModuledMatrix2x2(0, 1, 1, 1, m).pow(n)
    println(matrix.a01)
}
