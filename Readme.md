# CPU Usage Dashboard (Observability-as-Code)

Grafana dashboard for the `cpu_usage` metric with code-based generation using Grafana Foundation SDK.

## Dashboards

- `dashboards/CPU Usage handcrafted.json` - handcrafted from Grafana UI (full-featured)
- `dashboards/cpu_usage_dashboard_generated.json` - generated from code (minimal)

## Prerequisites

- Docker & Docker Compose
- Java 21
- Maven 3.8+
- k6 (https://grafana.com/docs/k6/latest/set-up/install-k6/)

## Quick Start

### 1. Start demo environment

```bash
cd demo
cp smtp.env.example smtp.env #needs to be configured for docker compose to work
docker compose up -d
```

Wait for services to start, then verify:

- Grafana: http://localhost:3000 (admin/admin)
- Prometheus: http://localhost:9090

### 2. Generate test metrics

```bash
k6 run demo/k6/cpu_usage.js
```

This generates `cpu_usage` metric data with instances `server1` and `server2`.

### 3. Generate dashboard from code

```bash
cd observability-as-code
mvn clean install
mvn exec:java -Dexec.mainClass=org.example.Main
```

Output: `dashboards/cpu_usage_dashboard_generated.json`

### 4. Import to Grafana

- Open http://localhost:3000 (admin/admin)
- **Dashboards** → **Import** → **Upload JSON file**
- Select `dashboards/cpu_usage_dashboard_generated.json`
- Click **Import**

The dashboard will display CPU usage timeseries for all instances.

## Development

### Run tests

```bash
cd observability-as-code
mvn test
```

### Generate dashboard

```bash
cd observability-as-code
mvn exec:java -Dexec.mainClass=org.example.Main
```

### Project structure

```
jb-task/
├── dashboards/                              # Dashboard JSONs
│   ├── CPU Usage handcrafted.json           # Handcrafted
│   └── cpu_usage_dashboard_generated.json   # Generated
├── observability-as-code/                   # Java SDK project
│   ├── src/
│   │   ├── main/java/org/example/Main.java  # Generator
│   │   └── test/java/org/example/           # Tests
│   └── pom.xml
├── demo/                                    # Git submodule
└── .github/workflows/ci.yml                 # CI pipeline
```

## Dashboard Features

### Handcrafted dashboard

- Timeseries visualization with smooth interpolation
- Instance variable dropdown for filtering
- Panel repeat (one panel per instance)
- Current + average CPU usage queries
- Color thresholds: green (0-70%), yellow (70-90%), red (90-100%)
- Auto-refresh every 30 seconds

### Generated dashboard

- Minimal timeseries panel
- Query: `cpu_usage`
- Legend shows all instances
- Demonstrates SDK usage

## CI/CD pipeline

GitHub Actions workflow runs on push to `main`:

1. Builds Java project
2. Runs unit tests
3. Generates dashboard JSON
4. Get grafanactl
5. Deploy dashboard to Grafana using grafanactl

Step 4. and 5. are commented out (would push to production Grafana).

### Demo environment

Uses the official Grafana demo stack:

- https://github.com/grafana/demo-prometheus-and-grafana-alerts
- Added as Git submodule in `demo/`
- Provides Grafana, Prometheus, and test data generator