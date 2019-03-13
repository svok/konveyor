/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package codes.spectrum.konveyor

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.map
import kotlinx.coroutines.channels.produce
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Main Konveyor class that includes all workflow of the konveyor
 */
class SubKonveyorWrapper<T, S>(
    private val matcher: KonveyorMatcherType<T> = { true },
    private val subKonveyor: Konveyor<S> = Konveyor(),
    private val splitter: SubKonveyorSplitterType<T, S> = { sequence { } },
    private val joiner: SubKonveyorJoinerType<T, S> = { _: S, _: IKonveyorEnvironment -> },
    private val bufferSizer: SubKonveyorCoroutineBufferSize<T> = { 1 },
    private val contexter: SubKonveyorCoroutineContextType<T> = { EmptyCoroutineContext }
): IKonveyorHandler<T> {

    override fun match(context: T, env: IKonveyorEnvironment): Boolean = context.matcher(env)

    override suspend fun exec(context: T, env: IKonveyorEnvironment) {
        GlobalScope
            .produce(context = context.contexter(env), capacity = context.bufferSizer(env)) { context.splitter(env).forEach { send(it) } }
            .map { subKonveyor.exec(it, env); it }
            .consumeEach {
                context.joiner(it, env)
            }
    }
}

