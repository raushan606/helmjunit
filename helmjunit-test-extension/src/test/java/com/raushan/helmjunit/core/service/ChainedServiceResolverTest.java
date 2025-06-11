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