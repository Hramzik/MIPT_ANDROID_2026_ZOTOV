import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import tasks.retryWithBackoff

class Task1Test {

    @Test
    fun `succeeds on first attempt without delay`() = runTest {
        val result = retryWithBackoff(maxAttempts = 3, baseDelayMs = 100) { "ok" }
        assertEquals("ok", result)
        assertEquals(0L, currentTime)
    }

    @Test
    fun `succeeds on second attempt after one backoff delay`() = runTest {
        var calls = 0
        val result = retryWithBackoff(maxAttempts = 3, baseDelayMs = 100) { attempt ->
            calls++
            if (attempt == 0) error("fail") else "ok"
        }
        assertEquals("ok", result)
        assertEquals(2, calls)
        assertEquals(100L, currentTime)
    }

    @Test
    fun `succeeds on third attempt with correct cumulative delay`() = runTest {
        var calls = 0
        val result = retryWithBackoff(maxAttempts = 5, baseDelayMs = 50) { attempt ->
            calls++
            if (attempt < 2) error("fail") else "done"
        }
        assertEquals("done", result)
        assertEquals(3, calls)
        assertEquals(150L, currentTime)
    }

    @Test
    fun `throws last exception after all attempts exhausted`() = runTest {
        val ex = assertThrows(RuntimeException::class.java) {
            kotlinx.coroutines.runBlocking {
                retryWithBackoff(maxAttempts = 3, baseDelayMs = 10) { attempt ->
                    throw RuntimeException("attempt-$attempt")
                }
            }
        }
        assertEquals("attempt-2", ex.message)
    }

    @Test
    fun `correct delays for maxAttempts=4 baseDelay=200`() = runTest {
        runCatching {
            retryWithBackoff(maxAttempts = 4, baseDelayMs = 200) { error("x") }
        }
        assertEquals(1400L, currentTime)
    }

    @Test
    fun `maxAttempts=1 throws immediately without delay`() = runTest {
        var calls = 0
        runCatching {
            retryWithBackoff(maxAttempts = 1, baseDelayMs = 1000) {
                calls++
                error("fail")
            }
        }
        assertEquals(1, calls)
        assertEquals(0L, currentTime)
    }
}
