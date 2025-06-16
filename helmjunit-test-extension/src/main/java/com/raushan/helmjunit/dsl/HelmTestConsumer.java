/*
 * Copyright 2025 Raushan Kumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.raushan.helmjunit.dsl;

/**
 * Interface for a consumer that accepts a HelmTestEnvironment.
 * This functional interface is used to define a method that takes a HelmTestEnvironment
 * and performs some operations on it, typically in the context of a Helm test.
 *
 * @see HelmTestEnvironment
 */
@FunctionalInterface
public interface HelmTestConsumer {
    void accept(HelmTestEnvironment builder) throws Exception;
}
