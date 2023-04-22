import kengine.util.Resource
import kotlin.test.Test
import kotlin.test.assertEquals

class ResourceTest {
    @Test
    fun testText() {
        suspend {
            val res = Resource("test.txt")
            res.waitForLoad()
            assertEquals("actual text", res.text)
        }
    }
}