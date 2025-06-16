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

package com.raushan.helmjunit.util;

import com.raushan.helmjunit.model.HelmChartDescriptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * HelmCommandBuilder is a utility class that constructs Helm command-line arguments
 * for installing and uninstalling Helm charts based on the provided HelmChartDescriptor.
 * It handles the creation of commands with necessary flags and options.
 */
public class HelmCommandBuilder {

    public static List<String> buildInstallCommand(HelmChartDescriptor descriptor) throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add("helm");
        cmd.add("install");
        cmd.add(descriptor.releaseName());
        cmd.add(descriptor.chart());
        cmd.add("--namespace");
        cmd.add(descriptor.namespace());
        cmd.add("--create-namespace");
        cmd.add("--wait");
        cmd.add("--timeout");
        cmd.add("120s");

        for (String val : descriptor.values()) {
            cmd.add("--set");
            cmd.add(val);
        }

        if (descriptor.valuesFile() != null && !descriptor.valuesFile().isBlank()) {
            String path = descriptor.valuesFile();
            if (descriptor.valuesFromClasspath()) {
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
                if (is == null) throw new IllegalArgumentException("Values file not found in classpath: " + path);
                File temp = File.createTempFile("helm-values", ".yaml");
                try (FileOutputStream out = new FileOutputStream(temp)) {
                    out.write(is.readAllBytes());
                }
                path = temp.getAbsolutePath();
            }
            cmd.add("-f");
            cmd.add(path);
        }

        return cmd;
    }

    public static List<String> buildUninstallCommand(HelmChartDescriptor descriptor) {
        return List.of(
                "helm", "uninstall",
                descriptor.releaseName(),
                "--namespace", descriptor.namespace()
        );
    }
}