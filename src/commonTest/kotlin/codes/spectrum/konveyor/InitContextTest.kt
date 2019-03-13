package codes.spectrum.konveyor

//import kotlin.reflect.KClass
//import kotlin.test.Test
//
//class EnvironmentKonveyorTest {
//
//    data class MyConfig  (var x: Int=0, var y:String )
//
//    interface IXContext { var x : Int } // есть некая контекстная универсалия
//
//    object Handler:IKonveyorHandler<IXContext> {
//        override fun match(context: IXContext, env: IKonveyorEnvironment): Boolean {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        } // eсть универсальный хандлер для универсалия
//        override suspend fun exec(context:IXContext, host:IKonveyorEnvironment){
//            // не надо прямо работать поверх System.env или @Inject или любой другой "глобальной" настройки если ты всего лишь винтик в некоем конвеере, но можешь иметь и дефолт
//            context.x+=host.env.get<Int>("increment", { defaultIncrement } ) // при этом у него есть зависящее от среды поведение
//        }
//        const val defaultIncrement:Int = 1
//    }
//
//    fun envTest(){
//
//        class SpecContext(override var x : Int = 0, var y:String = "" ):IXContext // есть некий конкретный контекст
//
//        val env1 : IEnvironment = SimpleEnv( params = mapOf("increment" to 1)) // есть 2 разных среды
//        val env2 : IEnvironment = SimpleEnv( params = mapOf("increment" to 2))
//
//        // есть соответственно 2 разных конвеера для одного контекста, но с разными хандлерами и настройками
//        val konv1 = konvier<SpecContext>(env1) {
//            +Handler.instance     // при этом всюду используется один единственный на все приложение хэндлер без состояния
//            +Handler.instance
//        }
//        val konv2 = konvier<SpecContext>(env2){
//            +Handler.instance
//            +Handler.instance
//            +Handler.instance
//            +Handler.instance
//        }
//
//        // выозовы этих конвееров и контексты к ним на вход не регламентирются
//        val ctx1 = SpecContext()
//        val ctx2 = SpecContext()
//        konv1.exec(ctx1)
//        konv2.exec(ctx2)
//        konv1.exec(ctx2) // конвееры никакого понятия не имеют - работал с этим контекстом кто-то или нет уже,
//        то есть это еще и выход на включение одних конвееров в другие
//
//
//                // ну и проверяем что результаты сошлись с ожидаемыми
//                assertThat(ctx1.x).isEqualTo(2)
//        assertThat(ctx2.x).isEqualTo(10)
//    }
//
//}
