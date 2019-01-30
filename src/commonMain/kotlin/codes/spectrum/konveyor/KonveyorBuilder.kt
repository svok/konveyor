package codes.spectrum.konveyor

@KonveyorTagMarker
class KonveyorBuilder<T> {

    val handlers: MutableList<IKonveyorHandler<T>> = mutableListOf()

    fun build(): Konveyor<T> = Konveyor(handlers)

    fun exec(block: suspend T.() -> Unit) {
        handlers.add(KonveyorWrapperHandler(executor = block))
    }

    fun handler(block: HandlerBuilder<T>.() -> Unit) {
        val builder = HandlerBuilder<T>()
        builder.block()
        val handler = builder.build()
        handlers.add(handler)
    }

}
