package codes.spectrum.konveyor

//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.promise

actual fun <T> runMultiplatformBlocking(block: suspend () -> T): T {
    //: dynamic = GlobalScope.promise { block(this) }
//    return GlobalScope.promise { block(this) }
}