package org.example;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CpuUsageDashboardTest {

    @Test
    void generatedDashboardHasExpectedFields() throws Exception {
        Main.main(new String[0]);

        Path jsonPath = Path.of("dashboards/cpu_usage_dashboard_generated.json");
        assertTrue(Files.exists(jsonPath), "Dashboard JSON not generated");

        String json = Files.readString(jsonPath);
        assertTrue(json.contains("cpu_usage"), "Metric query missing");
    }
}
