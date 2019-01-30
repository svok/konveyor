package codes.spectrum.konveyor

fun <T> konveyor(body: KonveyorBuilder<T>.() -> Unit ): Konveyor<T> {
    val builder = KonveyorBuilder<T>()
    builder.body()
    return builder.build()
}

expect fun <T> runMultiplatformBlocking(block: suspend () -> T): T
