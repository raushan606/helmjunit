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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

/**
 * Utility class to execute system processes and log their output.
 * This class provides a method to run a command with a context description,
 * capturing both standard output and error streams.
 */
public class ProcessExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ProcessExecutor.class);

    /**
     * Runs a command in a new process and logs the output.
     *
     * @param command            the command to run, as a list of strings
     * @param contextDescription a description of the context in which the command is run
     * @throws Exception if the process fails or if there is an error reading the output
     */
    public static void run(List<String> command, String contextDescription) throws Exception {
        ProcessBuilder builder = new ProcessBuilder(command);
        logger.debug("[{}] Running: {}", contextDescription, String.join(" ", command));

        Process process = builder.start();
        InputStream stdout = process.getInputStream();
        InputStream stderr = process.getErrorStream();

        String output = new String(stdout.readAllBytes());
        String error = new String(stderr.readAllBytes());

        int exitCode = process.waitFor();

        if (!output.isBlank()) {
            logger.info("[{}] STDOUT:\n{}", contextDescription, output);
        }
        if (!error.isBlank()) {
            logger.error("[{}] STDERR:\n{}", contextDescription, error);
        }

        if (exitCode != 0) {
            throw new RuntimeException("[" + contextDescription + "] failed with exit code " + exitCode);
        }
    }
}