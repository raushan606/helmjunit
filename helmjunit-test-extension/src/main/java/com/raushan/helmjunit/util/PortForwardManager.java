package com.raushan.helmjunit.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;

public class PortForwardManager implements AutoCloseable {
    private final Process portForwardProcess;
    private final int localPort;

    public PortForwardManager(String target, int targetPort, String namespace) throws Exception {
        this.localPort = findAvailablePort();

        ProcessBuilder pb = new ProcessBuilder(
                "kubectl", "port-forward", target,
                localPort + ":" + targetPort,
                "-n", namespace
        );
        pb.redirectErrorStream(true);

        this.portForwardProcess = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(portForwardProcess.getInputStream()));
        long timeout = System.currentTimeMillis() + 10_000;
        boolean started = false;

        while (System.currentTimeMillis() < timeout) {
            if (reader.ready()) {
                String line = reader.readLine();
                if (line != null && line.contains("Forwarding from")) {
                    started = true;
                    break;
                }
            }
            Thread.sleep(200);
        }

        if (!started) {
            portForwardProcess.destroy();
            throw new RuntimeException("Port-forward did not start within timeout");
        }
    }

    private int findAvailablePort() throws Exception {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    public String getLocalUrl(int targetPort) {
        return "http://localhost:" + localPort;
    }

    @Override
    public void close() {
        portForwardProcess.destroy();
    }
}
