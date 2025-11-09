.PHONY: help setup build test generate deploy clean start-demo stop-demo generate-metrics all

setup: build generate deploy

build:
	cd observability-as-code && mvn clean compile

test:
	cd observability-as-code && mvn test

generate:
	cd observability-as-code && mvn -q exec:java -Dexec.mainClass=org.example.Main

deploy:
	@mkdir -p demo/grafana/dashboards/definitions
	@cp observability-as-code/dashboards/cpu_usage_dashboard_generated.json demo/grafana/dashboards/definitions/
	@cp dashboards/cpu_usage_handcrafted.json demo/grafana/dashboards/definitions/

start-demo:
	@if [ ! -f demo/environments/smtp.env ]; then \
		echo "Creating smtp.env from example..."; \
		cp demo/environments/smtp.env.example demo/environments/smtp.env; \
	fi
	cd demo && docker compose up -d


stop-demo:
	cd demo && docker compose down

generate-metrics:
	@if ! command -v k6 >/dev/null 2>&1; then \
		echo "k6 not found. Install from: https://grafana.com/docs/k6/latest/set-up/install-k6/"; \
		exit 1; \
	fi
	k6 run demo/testdata/1.cpu-usage.js
	echo "View generated dashboard for CPU usage metrics in Grafana at: http://localhost:3000/d/your-dashboard-id/cpu-usage-dashboard"
	echo "View handcrafted full feature dashboard for CPU usage metrics in Grafana at: http://localhost:3000/d/your-dashboard-id/cpu-usage-dashboard"

clean:
	cd observability-as-code && mvn clean
	@rm -f observability-as-code/dashboards/cpu_usage_dashboard_generated.json
	@rm -f demo/grafana/dashboards/definitions/cpu_usage_dashboard_generated.json


all: clean build test generate deploy