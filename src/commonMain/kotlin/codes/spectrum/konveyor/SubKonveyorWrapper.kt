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

/**
 * Main Konveyor class that includes all workflow of the konveyor
 */
class SubKonveyorWrapper<T, S>(
    private val matcher: KonveyorMatcherType<T> = { true },
    private val subKonveyor: Konveyor<S> = Konveyor(),
    private val splitter: SubKonveyorSplitterType<T, S> = { sequence { } },
    private val joiner: SubKonveyorJoinerType<T, S> = { _: S, _: IKonveyorEnvironment -> }
): IKonveyorHandler<T> {

    override fun match(context: T, env: IKonveyorEnvironment): Boolean = context.matcher(env)

    override suspend fun exec(context: T, env: IKonveyorEnvironment) {
        fun T.getKonveyorEnv() = env
        context
            .splitter(env)
            .forEach {
                subKonveyor.exec(it, env)
                context.joiner(it, env)
            }
    }
}

