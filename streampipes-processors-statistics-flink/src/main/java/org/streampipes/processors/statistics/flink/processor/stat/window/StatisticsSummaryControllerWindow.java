/*
 * Copyright 2018 FZI Forschungszentrum Informatik
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
 *
 */

package org.streampipes.processors.statistics.flink.processor.stat.window;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.processors.statistics.flink.config.StatisticsFlinkConfig;
import org.streampipes.processors.statistics.flink.processor.stat.StatisticsSummaryController;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.vocabulary.Statistics;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

import java.util.concurrent.TimeUnit;

public class StatisticsSummaryControllerWindow extends
        FlinkDataProcessorDeclarer<StatisticsSummaryParametersWindow> {

  private static final String VALUE_TO_OBSERVE = "value-to-observe";
  private static final String PARTITION_BY = "partition-by";
  private static final String TIMESTAMP_MAPPING = "timestamp-mapping";
  private static final String TIME_WINDOW = "time-window";
  private static final String TIME_SCALE = "time-scale";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("statistics-summary-window", "Sliding Descriptive " +
                    "Statistics",
            "Calculate" +
                    " simple descriptive summary statistics based on a configurable time window")
            .iconUrl(StatisticsFlinkConfig.getIconUrl("statistics-icon"))
            .requiredPropertyStream1WithUnaryMapping(EpRequirements.numberReq(),
                    VALUE_TO_OBSERVE, "Value to " +
                            "observe", "Provide a value where statistics are calculated upon")
            .requiredPropertyStream1WithUnaryMapping(EpRequirements.timestampReq(),
                    TIMESTAMP_MAPPING, "Time", "Provide a time parameter")
            .requiredPropertyStream1WithUnaryMapping(EpRequirements.stringReq(),
                    PARTITION_BY, "Group by", "Partition the stream by a given id")
            .requiredIntegerParameter(TIME_WINDOW, "Time Window Size", "Size of the time window")
            .requiredSingleValueSelection(TIME_SCALE, "Time Window Scale", "",
                    Options.from("Hours", "Minutes", "Seconds"))
            .outputStrategy(OutputStrategies.fixed(
                    EpProperties.timestampProperty("timestamp"),
                    EpProperties.stringEp(Labels.empty(), "id", "http://schema.org/id"),
                    EpProperties.doubleEp(Labels.empty(), StatisticsSummaryController.MEAN, Statistics.MEAN),
                    EpProperties.doubleEp(Labels.empty(), StatisticsSummaryController.MIN, Statistics.MIN),
                    EpProperties.doubleEp(Labels.empty(), StatisticsSummaryController.MAX, Statistics.MAX),
                    EpProperties.doubleEp(Labels.empty(), StatisticsSummaryController.SUM, Statistics.SUM),
                    EpProperties.doubleEp(Labels.empty(), StatisticsSummaryController.STDDEV, Statistics.STDDEV),
                    EpProperties.doubleEp(Labels.empty(), StatisticsSummaryController.VARIANCE, Statistics.VARIANCE),
                    EpProperties.doubleEp(Labels.empty(), StatisticsSummaryController.N, Statistics.N)))
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka())
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<StatisticsSummaryParametersWindow> getRuntime(DataProcessorInvocation sepa, ProcessingElementParameterExtractor extractor) {

    String valueToObserve = extractor.mappingPropertyValue(VALUE_TO_OBSERVE);
    String timestampMapping = extractor.mappingPropertyValue(TIMESTAMP_MAPPING);

    String groupBy = extractor.mappingPropertyValue(PARTITION_BY);

    int timeWindowSize = extractor.singleValueParameter(TIME_WINDOW, Integer.class);
    String scale = SepaUtils.getOneOfProperty(sepa, TIME_SCALE);

    TimeUnit timeUnit;

    if (scale.equals("Hours")) {
      timeUnit = TimeUnit.HOURS;
    }
    else if (scale.equals("Minutes")) {
      timeUnit = TimeUnit.MINUTES;
    }
    else {
      timeUnit = TimeUnit.SECONDS;
    }

    StatisticsSummaryParametersWindow params = new StatisticsSummaryParametersWindow(sepa,
            valueToObserve, timestampMapping, groupBy, (long) timeWindowSize, timeUnit);

    StatisticsSummaryParamsSerializable serializableParams = new StatisticsSummaryParamsSerializable
            (valueToObserve, timestampMapping, groupBy, (long) timeWindowSize, timeUnit);

    return new StatisticsSummaryProgramWindow(params, serializableParams);

  }
}
