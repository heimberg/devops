package app;
import io.prometheus.client.Collector;
import org.eclipse.jetty.server.handler.StatisticsHandler;

import java.util.ArrayList;
import java.util.List;

public class StatisticsHandlerCollector extends Collector {
    private final StatisticsHandler statisticsHandler;
    private static final List<String> EMPTY_LIST = new ArrayList<>();

    private StatisticsHandlerCollector(StatisticsHandler statisticsHandler) {
        this.statisticsHandler = statisticsHandler;
    }

    public static void initialize(StatisticsHandler statisticsHandler) {
        new StatisticsHandlerCollector(statisticsHandler).register();
    }

    @Override
    public List

}

