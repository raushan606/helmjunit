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

import java.io.InputStream;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses rendered manifests from Helm to discover service name and port.
 */
public class HelmManifestServiceResolver implements ServiceResolver {

    private static final Logger logger = LoggerFactory.getLogger(HelmManifestServiceResolver.class.getName());

    @Override
    public Optional<String> resolveServiceName(String releaseName, String namespace) throws Exception {
        ProcessBuilder builder = new ProcessBuilder("helm", "get", "manifest", releaseName, "-n", namespace);
        Process process = builder.start();
        InputStream input = process.getInputStream();
        String manifest = new String(input.readAllBytes());
        process.waitFor();

        Pattern svcPattern = Pattern.compile("(?m)^kind: Service\n.*?metadata:\n  name: (.*?)\n.*?spec:\n.*?type: (ClusterIP|LoadBalancer|NodePort)", Pattern.DOTALL);
        Matcher matcher = svcPattern.matcher(manifest);

        while (matcher.find()) {
            String name = matcher.group(1);
            if (name.contains("master")) {
                return Optional.of(name);
            }
        }

        matcher.reset();
        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        }

        return Optional.empty();
    }

    @Override
    public int resolveServicePort(String serviceName, String namespace) throws Exception {
        // fallback to kubectl if not parsing YAML
        return new KubectlServiceResolver().resolveServicePort(serviceName, namespace);
    }
}
