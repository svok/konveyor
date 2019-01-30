package codes.spectrum.konveyor

import kotlin.test.Test
import kotlin.test.assertEquals

class UsageDemonstrationTest {

    @Test
    fun createExecTest() {
        val myContext = MyContext(id = "1", value = 1)
        val conveyor = konveyor<MyContext> {
            exec {
                value ++
            }
        }

        runMultiplatformBlocking { conveyor.process(myContext) }

        assertEquals(2, myContext.value)

    }

    @Test
    fun createHandlerTest() {
        val myContext1 = MyContext(id = "1", value = 1)
        val myContext2 = MyContext(id = "2", value = 1)
        val conveyor = konveyor<MyContext> {
            handler {
                on {
                    id == "1"
                }
                exec {
                    value ++
                }
            }
        }

        runMultiplatformBlocking { conveyor.process(myContext1) }
        runMultiplatformBlocking { conveyor.process(myContext2) }

        assertEquals(2, myContext1.value)
        assertEquals(1, myContext2.value)

    }

    internal data class MyContext(
        var id: String = "",
        var value: Int = 0
    )

}