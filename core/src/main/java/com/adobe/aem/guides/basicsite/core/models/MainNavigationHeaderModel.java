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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class MainNavigationHeaderModel {

    static final String MULTIFIELD_NODE = "ctaLinks";
    static final String MULTIFIELD_NODE_SOCIAL_ICONS = "socialIconLinks";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    Resource resource;

    @SlingObject
    private ResourceResolver resourceResolver;

    @OSGiService
    private PageUtils utils;

    @Inject
    private String hipRoot;

    @Inject
    private String kneeRoot;

    @Inject
    private Node bodyParts;

    @Inject
    private List<LinkedHashMap> ctaLinks;

    private Map<Map<String, String>, Object> megaMenu;

    private Map<String, List> journeyMegaMenu;

    @PostConstruct
    protected void init() {
        try {
            journeyNav();
            megaMenu = utils.composeMegaMenuItems(getRootPages(), resourceResolver);
        } catch (NullPointerException npe){
            log.error("MainNavigationHeaderModel, unable to get mega menu items",npe);
        }
        ctaLinks = utils.getPageInfoFromMultifield(resource, MULTIFIELD_NODE);
        socialIconLinks =  getExternalLinks(MULTIFIELD_NODE_SOCIAL_ICONS);
    }

    protected void journeyNav(){
        try {
            journeyMegaMenu = new LinkedHashMap<>();
            Iterator<Node> bodyPartIt = bodyParts.getNodes();
            log.info("MainNavigationHeaderModel : journeyNav : outsidebodyPartIt" + bodyParts.getName().toString());
            while (bodyPartIt.hasNext()) {

                Node landingPageNode = bodyPartIt.next();
                Node journeyLinks = landingPageNode.getNode("journeyLinks");

                log.info("MainNavigationHeaderModel : journeyNav : landingPageNode" + landingPageNode.getName().toString());

                Iterator<Node> journeyIt = journeyLinks.getNodes();
                List<HashMap> journeyFilter = new ArrayList<>();

                while (journeyIt.hasNext()) {
                    Node journeyNode = journeyIt.next();
                    HashMap<String, String> journey = new HashMap<>();

                    log.info("MainNavigationHeaderModel : journeyNav : landingPageNode" + journeyNode.getName().toString());


                    journey.put("Title", journeyNode.hasProperty("journeyTitle") ? journeyNode.getProperty("journeyTitle").getString() : "");
                    journey.put("Data", journeyNode.hasProperty("journeyData") ? journeyNode.getProperty("journeyData").getString() : "");

                    journeyFilter.add(journey);
                }

                journeyMegaMenu.put(landingPageNode.getProperty("bodyPartRoot").getString(), journeyFilter);

                log.info(journeyMegaMenu.toString());
            }
        } catch (Exception e) {
            log.error("Journey Navigation Exception", e);
        }

    }

    public Map<String, List> getJourneyMegaMenu() { return journeyMegaMenu; }

    public String getHipRoot() {
        return hipRoot;
    }

    public void setHipRoot(String hipRoot) {
        this.hipRoot = hipRoot;
    }

    public String getKneeRoot() {
        return kneeRoot;
    }

    public void setKneeRoot(String kneeRoot) {
        this.kneeRoot = kneeRoot;
    }

    public Map<Map<String, String>, Object> getMegaMenu() {
        return megaMenu;
    }

    private String[] getRootPages() {
        ArrayList<String> rootPages = new ArrayList<>();
        journeyMegaMenu.forEach((k,v) -> {
            rootPages.add(k);
        });
        String[] stringArray = rootPages.toArray(new String[0]);
        return stringArray;
    }

    public List<LinkedHashMap> getCtaLinks() {
        return ctaLinks;
    }

    private List<LinkedHashMap> socialIconLinks;

    public List<LinkedHashMap> getSocialIconLinks() {
        return socialIconLinks;
    }

    private List<LinkedHashMap> getExternalLinks(String multifieldNode) {
        Iterator<Resource> resourceIterator = null;

        if (null != resource && resource.getChild(multifieldNode) != null) {
            resourceIterator = resource.getChild(multifieldNode).listChildren();
        }
        return utils.getExternalLinksFromMultifield(resourceIterator, resource);
    }

}

