package com.adobe.aem.guides.basicsite.core.models;

import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.aem.guides.basicsite.core.utils.PageUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class FooterModel {

    static final String MULTIFIELD_NODE_COLUMN_ONE = "firstColumnLinks";
    static final String MULTIFIELD_NODE_COLUMN_TWO = "secondColumnLinks";
    static final String MULTIFIELD_NODE_COLUMN_THREE = "thirdColumnLinks";
    static final String MULTIFIELD_NODE_COLUMN_FOUR = "fourthColumnLinks";
    static final String MULTIFIELD_NODE_COLUMN_SIX = "sixthColumnLinks";

    @Inject
    Resource resource;

    @SlingObject
    private ResourceResolver resourceResolver;

    @OSGiService
    private PageUtils utils;

    private List<LinkedHashMap> columnOneLinks;

    private List<LinkedHashMap> columnTwoLinks;

    private List<LinkedHashMap> columnThreeLinks;

    private List<LinkedHashMap> columnFourLinks;

    private List<LinkedHashMap> columnSixLinks;

    public List<LinkedHashMap> getColumnOneLinks() {
        return columnOneLinks;
    }

    public List<LinkedHashMap> getColumnTwoLinks() {
        return columnTwoLinks;
    }

    public List<LinkedHashMap> getColumnThreeLinks() {
        return columnThreeLinks;
    }

    public List<LinkedHashMap> getColumnFourLinks() {
        return columnFourLinks;
    }

    public List<LinkedHashMap> getColumnSixLinks() {
        return columnSixLinks;
    }

    @PostConstruct
    protected void init() {
        columnOneLinks = utils.getPageInfoFromMultifield(resource, MULTIFIELD_NODE_COLUMN_ONE);
        columnTwoLinks = utils.getPageInfoFromMultifield(resource, MULTIFIELD_NODE_COLUMN_TWO);
        columnThreeLinks = utils.getPageInfoFromMultifield(resource, MULTIFIELD_NODE_COLUMN_THREE);
        columnFourLinks =  getExternalLinks(MULTIFIELD_NODE_COLUMN_FOUR);
        columnSixLinks = getExternalLinks(MULTIFIELD_NODE_COLUMN_SIX);
    }

    private List<LinkedHashMap> getExternalLinks(String multifieldNode) {
        Iterator<Resource> resourceIterator = null;

        if (null != resource && resource.getChild(multifieldNode) != null) {
            resourceIterator = resource.getChild(multifieldNode).listChildren();
        }
        return utils.getExternalLinksFromMultifield(resourceIterator, resource);
    }
}

