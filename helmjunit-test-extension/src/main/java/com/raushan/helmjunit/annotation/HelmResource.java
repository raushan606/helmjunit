package com.raushan.helmjunit.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a field as a Helm resource.
 * <p>
 * This annotation can be used to specify the Helm chart details
 * for a field in a test class. It allows the test framework to
 * manage Helm resources during the test lifecycle.
 * <p>
 * Example usage:
 * <pre>
 * &#64;HelmResource(chart = "my-chart", releaseName = "my-release", namespace = "my-namespace")
 * private HelmResource myHelmResource;
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HelmResource {

    /**
     * The name of the Helm chart to be used.
     * This is a mandatory field.
     *
     * @return the name of the Helm chart
     */
    String chart();

    /**
     * The version of the Helm chart to be used.
     * If not specified, the default version will be used.
     *
     * @return the version of the Helm chart
     */
    String releaseName() default "";

    /**
     * The namespace in which the Helm chart will be deployed.
     * If not specified, the default namespace will be used.
     *
     * @return the namespace for the Helm chart deployment
     */
    String namespace() default "default";

    /**
     * The values to be passed to the Helm chart during deployment.
     * This can be used to override default values in the chart.
     * e.g.: values = {"key=value"}
     *
     * @return an array of values to be passed to the Helm chart
     */
    String[] values() default {};

    /**
     * Absolute or relative path to a values file to be used with the Helm chart.
     * This can be used to specify a file containing values to override
     * the defaults in the chart.
     * e.g.: valuesFile = "path/to/values.yaml"
     *
     * @return the path to the values file
     */
    String valuesFile() default "";

    /**
     * Whether to load values from the classpath resources.
     * e.g.: valuesFromClassPath = true
     * This will be auto-resolve via ClassLoader.
     *
     * @return true if values should be loaded from classpath, false otherwise
     */
    boolean valuesFromClassPath() default false;
}
