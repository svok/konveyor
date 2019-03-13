package codes.spectrum.konveyor

import kotlin.test.Test
import kotlin.test.assertEquals

class HandlerTest {

    @Test
    fun createTest() {
        val h1 = handler<MyContext> {
            on { true }
            exec { value+=100 }
        }
        val h2 = handler<MyContext> {
            on { false }
            exec { value+=120 }
        }

        val konv = konveyor<MyContext> {
            +h1
            +h2
        }
        val context = MyContext()
        runMultiplatformBlocking { konv.exec(context) }
        assertEquals(100, context.value)
    }

    internal data class MyContext(
        var id: String = "",
        var value: Int = 0
    )

}
