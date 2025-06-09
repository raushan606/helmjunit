# HelmJUnit Showcase

This module demonstrates how to use the [HelmJUnit](https://github.com/raushan606/helmjunit) extension to run 
integration tests on real services deployed via Helm inside Kubernetes.

## 🚀 What It Shows

- ✅ Automatic Helm chart deployment during test lifecycle
- ⏳ Waits for Kubernetes resources to be ready (pods, services)
- 🔌 Access services via port forwarding (no Ingress/NodePort needed)
- 💉 Injects HelmRelease metadata into test classes
- 🧼 Full cleanup after test run (uninstall Helm + delete namespace)

## 📁 Structure

```
helmjunit-showcase/
├── charts/
│   └── echo-service/               # Minimal working Helm chart
├── src/test/java/com/example/
│   ├── EchoServiceIT.java          # Tests helm chart and HTTP service response
│   ├── ConnectionIT.java           # Tests real Redis interaction, PostgreSQL access
│   └── HttpClientHelper.java       # Simple HTTP wrapper
├── build.gradle.kts
└── settings.gradle.kts
```

## 📦 Prerequisites

- Java 17+
- Helm installed
- kubectl installed
- Docker + Minikube

## 🧪 Run Tests

```bash
./gradlew test
```

This will:
1. Deploy charts defined in test annotations
2. Wait for pods
3. Run the tests
4. Port forward services for test access
5. Clean up everything

## 🧪 Tests Included

### ✅ EchoServiceIT
Deploys a tiny HTTP echo server and verifies it returns `hello`.

### ✅ ConnectionIT
- Deploys Redis via Helm and tests `SET/GET` with Jedis client.
- Deploys PostgreSQL and connects via JDBC to run SQL assertions.

## 🧠 How It Works

The test class is annotated like:

```java
@HelmChartTest
public class ConnectionIT {

    @HelmResource(chart = "bitnami/redis", releaseName = "redis", namespace = "connection-ns")
    HelmRelease redis;

    @Test
    void shouldConnectToRedis() throws Exception {
        try (PortForwardManager pf = new PortForwardManager(
                "svc/" + redis.getServiceName(),
                redis.getServicePort(),
                redis.getNamespace()
        )) {
            // Jedis or HTTP client logic
        }
    }
}
```

## ✅ Benefits

- 🛠 Zero manual Helm or Kubernetes config
- ⚙️ Works in CI or local
- 🔐 No public service exposure needed
- 🚫 No YAML duplication

[//]: # (## 💡 Tip)

[//]: # (To run against a real cloud cluster, disable local provisioning:)

[//]: # ()
[//]: # (```java)

[//]: # (@HelmChartTest&#40;localDevelopment = false&#41;)

[//]: # (```)

---

## 📣 Contribute
Feel free to fork this showcase, adapt it to your charts, and open issues or PRs to HelmJUnit!
