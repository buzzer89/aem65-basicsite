package com.adobe.aem.guides.basicsite.core.models;

import com.adobe.cq.export.json.ExporterConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TaskListModel {
    static final String TASK_CARD_NODE = "task-card-";

    @Inject
    String maxCardItems;

    public List<String> getTaskNodes() {
        return taskNodes;
    }

    private List<String> taskNodes;

    @PostConstruct
    protected void init() {
        taskNodes =  getCardsNodes(maxCardItems);
    }

    private List<String> getCardsNodes(String maxItems) {
        if (maxItems != null) {
            int items = Integer.valueOf(maxItems);

            if (items > 0) {
                taskNodes = new ArrayList<>();
                for (int i = 0; i < items; i += 1) {
                    taskNodes.add(TASK_CARD_NODE + i);
                }
            }
        }
        return taskNodes;
    }
}

