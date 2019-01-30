package codes.spectrum.konveyor

class KonveyorWrapperHandler<T>(
    val matcher: T.() -> Boolean = { true },
    val executor: suspend T.() -> Unit = {}
): IKonveyorHandler<T> {

    override fun match(context: T): Boolean = context.matcher()
    override suspend fun exec(context: T) = context.executor()
}
