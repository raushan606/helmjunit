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

package com.raushan.helmjunit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.raushan.helmjunit.extension.HelmChartTestExtension;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Annotation to mark a class as a Helm chart test.
 * <p>
 * This annotation can be used to indicate that the class is intended
 * to test Helm charts. It can be used in conjunction with the JUnit 5
 * test framework.
 * <p>
 * Example usage:
 * <pre>
 * &#64;HelmChartTest
 * public class MyHelmChartTest {
 *     // Test methods go here
 * }
 * </pre>
 * <p>
 * This annotation does not have any attributes and is used solely for
 * identification purposes.
 *
 * @see HelmResource
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith(HelmChartTestExtension.class)
public @interface HelmChartTest {
    boolean perTestLifecycle() default false;
}
