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

package com.raushan.helmjunit.core.service;

import com.raushan.helmjunit.core.ServiceResolver;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ChainedServiceResolverTest {

    static class AlwaysFailsResolver implements ServiceResolver {
        public Optional<String> resolveServiceName(String releaseName, String namespace) {
            return Optional.empty();
        }

        public int resolveServicePort(String serviceName, String namespace) {
            throw new RuntimeException("fail");
        }
    }

    static class StubResolver implements ServiceResolver {
        public Optional<String> resolveServiceName(String releaseName, String namespace) {
            return Optional.of("test-service");
        }

        public int resolveServicePort(String serviceName, String namespace) {
            return 1234;
        }
    }

    @Test
    void shouldFallbackToSecondResolverForName() throws Exception {
        ChainedServiceResolver resolver = new ChainedServiceResolver(
                new AlwaysFailsResolver(), new StubResolver()
        );

        Optional<String> name = resolver.resolveServiceName("dummy", "default");
        assertTrue(name.isPresent());
        assertEquals("test-service", name.get());
    }

    @Test
    void shouldFallbackToSecondResolverForPort() throws Exception {
        ChainedServiceResolver resolver = new ChainedServiceResolver(
                new AlwaysFailsResolver(), new StubResolver()
        );

        int port = resolver.resolveServicePort("dummy", "default");
        assertEquals(1234, port);
    }

    @Test
    void shouldThrowIfAllResolversFail() throws Exception {
        ChainedServiceResolver resolver = new ChainedServiceResolver(
                new AlwaysFailsResolver(), new AlwaysFailsResolver()
        );

        assertThrows(RuntimeException.class, () -> resolver.resolveServicePort("svc", "ns"));
        assertTrue(resolver.resolveServiceName("svc", "ns").isEmpty());
    }
}