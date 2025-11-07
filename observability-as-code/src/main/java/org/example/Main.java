package org.example;

import com.grafana.foundation.dashboard.Dashboard;
import com.grafana.foundation.dashboard.DashboardBuilder;
import com.grafana.foundation.dashboard.PanelBuilder;
import com.grafana.foundation.prometheus.DataqueryBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        Dashboard dashboard = new DashboardBuilder("CPU Usage")
                .uid("cpu-usage-simple")
                .withPanel(
                        new PanelBuilder<>()
                                .title("CPU Usage")
                                .type("timeseries")
                                .withTarget(
                                        new DataqueryBuilder()
                                                .expr("cpu_usage")
                                                .legendFormat("{{instance}}")
                                )
                )
                .build();

        Path out = Path.of("cpu_usage_dashboard.json");
        Files.writeString(out, dashboard.toJSON());

        System.out.println("Dashboard created: " + out.toAbsolutePath());
    }
}
