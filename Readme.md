# CPU Usage Dashboard (Observability-as-Code)

Grafana dashboard for the `cpu_usage` metric with code-based generation using Grafana Foundation SDK.

## Dashboards

- `dashboards/CPU Usage handcrafted.json` - handcrafted from Grafana UI (full-featured, reference)
- `observability-as-code/dashboards/cpu_usage_dashboard_generated.json` - generated from code (SDK demo)

([ProcessDocumentation.md](ProcessDocumentation.md) contains more details about my thought process and decisions)

## Prerequisites

- Docker & Docker Compose
- Java 21
- Maven 3.8+
- k6 (https://grafana.com/docs/k6/latest/set-up/install-k6/)

## Quick Start

### Option 1: Automatic Setup (Recommended)

Use the Makefile to automate the entire pipeline:

```bash
make setup

make start-demo

make generate-metrics
```

Then access Grafana the dashboards at:

- generated dashboard for CPU usage metrics: http://localhost:3000/d/cpu-usage-generated/cpu-usage-generated
- handcrafted full feature dashboard for CPU usage metrics: http://localhost:3000/d/adh68m7/cpu-usage-handcrafted

or find them in the /TestsFolder under Dashboards in Grafana UI.

### Option 2: Manual Setup

#### 1. Start demo environment

```bash
cd demo
cp environments/smtp.env.example environments/smtp.env
docker compose up -d
```

Wait for services to start, then verify:

- Grafana: http://localhost:3000
- Prometheus: http://localhost:9090

#### 2. Generate test metrics

```bash
k6 run demo/testdata/1.cpu-usage.js
```

This generates `cpu_usage` metric data with instances `server1` and `server2`.

#### 3. Generate dashboard from code

```bash
cd observability-as-code
mvn clean install
mvn exec:java -Dexec.mainClass=org.example.Main
```

Output: `observability-as-code/dashboards/cpu_usage_dashboard_generated.json`

#### 4. Import to Grafana

**Option A: Auto-load (copy to demo folder)**

```bash
cp observability-as-code/dashboards/cpu_usage_dashboard_generated.json demo/grafana/dashboards/definitions/
cp dashboards/cpu_usage_handcrafted.json demo/grafana/dashboards/definitions/
```

**Option B: Manual import**
- Open http://localhost:3000 (admin/admin)
- **Dashboards** → **Import** → **Upload JSON file**
- Select `observability-as-code/dashboards/cpu_usage_dashboard_generated.json`
- Click **Import**
- Do the same for `dashboards/cpu_usage_handcrafted.json`

Then access Grafana the dashboards at:

- generated dashboard for CPU usage metrics: http://localhost:3000/d/cpu-usage-generated/cpu-usage-generated
- handcrafted full feature dashboard for CPU usage metrics: http://localhost:3000/d/adh68m7/cpu-usage-handcrafted

or find them in the /TestsFolder under Dashboards in Grafana UI.

### Project structure

```
jb-task/
├── dashboards/                              # Dashboard JSONs
│   ├── cpu_usage_handcrafted.json           # Handcrafted
│   └── cpu_usage_dashboard_generated.json   # Generated
├── observability-as-code/                   # Java SDK project
│   ├── src/
│   │   ├── main/java/org/example/Main.java  # Generator
│   │   └── test/java/org/example/           # Tests
│   └── pom.xml
├── demo/                                    # Git submodule - local grafana and prometheus demo
├── Makefile                                 # local automation
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
- Demonstrates SDK usage
- Query: `cpu_usage`
- Query: `avg_over_time(cpu_usage[5m])`

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