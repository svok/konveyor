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

import kotlin.contracts.ExperimentalContracts
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.experimental.ExperimentalTypeInference


@KonveyorTagMarker
class SubKonveyorBuilder<T: Any,S: Any>: KonveyorBuilder<S>() {

    protected var matcherT: KonveyorMatcherType<T> = { true }
    private var splitter: SubKonveyorSplitterType<T, S> = { sequence {  } }
    private var joiner: SubKonveyorJoinerType<T, S> = { _: S, _: IKonveyorEnvironment -> }
    private var contexter: SubKonveyorCoroutineContextType<T> = { EmptyCoroutineContext }
    private var bufferSizer: SubKonveyorCoroutineBufferSize<T> = { 1 }
    private var consumer: SubKonveyorCoroutineConsumer<T> = { 1 }

    fun buildNew(): SubKonveyorWrapper<T, S> = SubKonveyorWrapper(
        matcher = matcherT,
        subKonveyor = build(),
        splitter = splitter,
        joiner = joiner,
        bufferSizer = bufferSizer,
        contexter = contexter,
        consumer = consumer
    )

//    /**
//     * With this methos one can set the lambda for matcher [[IKonveyorHandler.match]] to the handler
//     */
//    fun on(block: KonveyorMatcherShortType<T>) {
//        onEnv(block = { block() } as KonveyorMatcherType<T>)
//    }

//    /**
//     * With this methos one can set the lambda for matcher [[IKonveyorHandler.match]] having access to
//     * [[IKonveyorEnvironment]] through lambda parameter to the handler
//     */
//    fun onEnv(block: KonveyorMatcherType<T>) {
//        matcherT = block
//    }

    fun bufferSize(block: SubKonveyorCoroutineBufferSize<T>) { bufferSizer = block}
    @ExperimentalContracts
    fun coroutineContext(block: SubKonveyorCoroutineContextType<T>) { contexter = block}
    fun joinersNumber(block: SubKonveyorCoroutineConsumer<T>) { consumer = block}

    fun split(block: SubKonveyorSplitterShortType<T, S>) {
        splitEnv { env ->
            block()
        }
    }

    fun splitEnv(block: SubKonveyorSplitterType<T, S>) {
        splitter = block
    }

    fun join(block: SubKonveyorJoinerShortType<T, S>) {
        joinEnv { joining, env ->
            block(joining)
        }
    }

    fun joinEnv(block: SubKonveyorJoinerType<T, S>) {
        joiner = block
    }

}
