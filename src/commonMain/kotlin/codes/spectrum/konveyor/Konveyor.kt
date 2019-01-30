package codes.spectrum.konveyor

class Konveyor<T>(
    private val handlers: List<IKonveyorHandler<T>> = listOf()
) {
    suspend fun process(context: T) {
        handlers.forEach {
            if (it.match(context)) it.exec(context)
        }
    }
//    expect fun proces(context: T)
}

