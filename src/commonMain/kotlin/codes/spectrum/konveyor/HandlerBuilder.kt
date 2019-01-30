package codes.spectrum.konveyor

@KonveyorTagMarker
class HandlerBuilder<T> {

    private var matcher: T.() -> Boolean = { true }
    private var executor: suspend T.() -> Unit = { }

    fun on(block: T.() -> Boolean) {
        matcher = block
    }

    fun exec(block: suspend T.() -> Unit) {
        executor = block
    }

    fun build() = KonveyorWrapperHandler<T>(
        matcher = matcher,
        executor = executor
    )
}
