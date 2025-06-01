package com.raushan.helmjunit;

import com.raushan.helmjunit.annotation.HelmChartTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@HelmChartTest
public class HelmJunitSmokeTest {

    @Test
    void testHelmJunitAnnotation() {
        // This test is just to ensure that the HelmJunit annotation is correctly applied
        // and the extension is loaded without any issues.
        assertTrue(true, "HelmJunit annotation should be processed without errors.");
    }
}
