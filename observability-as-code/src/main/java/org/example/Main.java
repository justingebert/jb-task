package org.example;

import com.grafana.foundation.dashboard.Dashboard;
import com.grafana.foundation.dashboard.DashboardBuilder;
import com.grafana.foundation.dashboard.DashboardDashboardTimeBuilder;
import com.grafana.foundation.dashboard.PanelBuilder;
import com.grafana.foundation.prometheus.DataqueryBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {

        Dashboard dashboard = new DashboardBuilder("CPU Usage - generated")
                .uid("cpu-usage-generated")
                .refresh("30s")
                .time(new DashboardDashboardTimeBuilder().from("now-30m").to("now"))
                .withPanel(
                        new PanelBuilder<>()
                                .title("CPU Usage (%)")
                                .type("timeseries")
                                .withTarget(
                                        new DataqueryBuilder()
                                                .expr("cpu_usage")
                                                .legendFormat("current - {{instance}}")
                                                .refId("A")
                                )
                                .withTarget(
                                        new DataqueryBuilder()
                                                .expr("avg_over_time(cpu_usage[5m])")
                                                .legendFormat("5m avg - {{instance}}")
                                                .refId("B")
                                )
                )
                .build();

        Path out = Path.of("dashboards/cpu_usage_dashboard_generated.json");
        Files.createDirectories(out.getParent());
        Files.writeString(out, dashboard.toJSON());

        System.out.println("Dashboard created: " + out.toAbsolutePath());
    }
}
