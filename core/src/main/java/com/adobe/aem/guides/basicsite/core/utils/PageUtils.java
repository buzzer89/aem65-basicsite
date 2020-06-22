package com.adobe.aem.guides.basicsite.core.utils;

import com.day.cq.wcm.api.Page;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;

import java.util.*;

/**
 * Holds reusable methods for pages' related functions
 *
 * @author ConexioGroup (isosa)
 */

@Component(service = PageUtils.class)
public class PageUtils {

    public static final String PATH_KEY = "path";
    public static final String TITLE_KEY = "title";
    public static final String TARGET_KEY = "target";
    public static final String IMAGE_KEY = "image";
    public static final String TEXT_KEY = "text";
    public static final String MULTIFIELD_GENERIC_LINK_KEY = "link";

    /**
     * Provides needed data to compose the Mega Menu
     * @param rootPages an array[2] root pages to respect the designs
     * @param resolver of the context
     * @return megaMenuItems with parent and first level of children
     */
    public Map<Map<String, String>, Object> composeMegaMenuItems(String[] rootPages, ResourceResolver resolver) {
        Map<Map<String, String>, Object> megaMenuItems = new LinkedHashMap<>();

        for (String root : rootPages) {
            if (StringUtils.isNotEmpty(root)) {
                Iterator<Page> iterator = getFirstChildren(root, resolver);
                List<NavigableMap<String, String>> listOfPageProperties = new ArrayList<>();
                Map<String, String> parentMap = new HashMap<>();
                Page rootPage = resolver.getResource(root).adaptTo(Page.class);
                parentMap.put(PATH_KEY, rootPage.getPath());
                parentMap.put(TITLE_KEY, rootPage.getTitle());

                String landingCustomTitle;
                landingCustomTitle = StringUtils.containsIgnoreCase(rootPage.getTitle(), "knee") ? "All Knee Articles" : "All Hip Articles";

                while (iterator.hasNext()) {
                    NavigableMap<String, String> pagePropertiesMap = new TreeMap<>();
                    Page thisPage = iterator.next();
                    String name = thisPage.getName();
                    String title = thisPage.getTitle();
                    String path = thisPage.getPath();
                    String isHidden = String.valueOf(thisPage.isHideInNav());
                    pagePropertiesMap.put("name", name);
                    pagePropertiesMap.put(TITLE_KEY, title);
                    pagePropertiesMap.put(PATH_KEY, path);
                    pagePropertiesMap.put("parentPath", rootPage.getPath());
                    pagePropertiesMap.put("customParentTitle", landingCustomTitle);
                    pagePropertiesMap.put("isHidden", isHidden);
                    listOfPageProperties.add(pagePropertiesMap);
                }
                megaMenuItems.put(parentMap, listOfPageProperties);
            }
        }
        return megaMenuItems;
    }

    /**
     * A utility method
     * @param rootPage
     * @param resolver
     * @return an iterator<pages>
     */
    public Iterator<Page> getFirstChildren(String rootPage, ResourceResolver resolver) {
        return resolver.getResource(rootPage).adaptTo(Page.class).listChildren();
    }

    /**
     * Serves a list of specified page properties from a multifield of links
     * @param resource the component from which the model is called
     * @param multifieldNode the name of the multifield node
     * @return list of page properties
     */
    public List<LinkedHashMap> getPageInfoFromMultifield(Resource resource, String multifieldNode) {
        LinkedHashMap<String, String> links;
        List<LinkedHashMap> linkList = new LinkedList<>();

        if (null != resource && StringUtils.isNotEmpty(multifieldNode) && resource.getChild(multifieldNode) != null) {
            Iterator<Page> pageIterator = getPagesFromMultifieldPaths(resource.getChild(multifieldNode).listChildren(), resource);
            Iterator<String> pageTargetIterator = getPagesTargetsFromMultifieldPaths(resource.getChild(multifieldNode).listChildren(), resource);

            while (pageIterator.hasNext()) {
                links = new LinkedHashMap<>();
                Page page = pageIterator.next();
                String pageTarget = pageTargetIterator.next();
                links.put(TITLE_KEY, page.getTitle());
                links.put(PATH_KEY, page.getPath());
                links.put(TARGET_KEY, pageTarget);
                linkList.add(links);
            }
        }
        return linkList;
    }

    /**
     * Serves a list of pages out of a multifield of links
     * @param resource the component from which the model is called
     * @param resourceIterator an iterator of nodes
     * @return a list of pages
     */
    public Iterator<Page> getPagesFromMultifieldPaths(Iterator<Resource> resourceIterator, Resource resource) {
        List<Page> pages = new ArrayList<>();

        while (resourceIterator.hasNext()) {
            String pagePath = null;
            String itemPath = resourceIterator.next().getPath();
            ResourceResolver resolver = resource.getResourceResolver();
            Resource thisResource = resolver.getResource(itemPath);

            if (null != thisResource.getChild(MULTIFIELD_GENERIC_LINK_KEY)) {
                pagePath = thisResource.getValueMap().get(MULTIFIELD_GENERIC_LINK_KEY, String.class);
                if (null != resolver.getResource(pagePath)) {
                    pages.add(resolver.getResource(pagePath).adaptTo(Page.class));
                }
            }
        }
        return pages.iterator();
    }

    /**
     * Serves a list of pages out of a multifield of links
     * @param resource the component from which the model is called
     * @param resourceIterator an iterator of nodes
     * @return a list of pages
     */
    public Iterator<String> getPagesTargetsFromMultifieldPaths(Iterator<Resource> resourceIterator, Resource resource) {
        List<String> targets = new ArrayList<>();

        while (resourceIterator.hasNext()) {
            String pagePath = null;
            String pageTarget = null;
            String itemPath = resourceIterator.next().getPath();
            ResourceResolver resolver = resource.getResourceResolver();
            Resource thisResource = resolver.getResource(itemPath);

            if (null != thisResource.getChild(MULTIFIELD_GENERIC_LINK_KEY)) {
                pagePath = thisResource.getValueMap().get(MULTIFIELD_GENERIC_LINK_KEY, String.class);
                pageTarget = thisResource.getValueMap().get(TARGET_KEY, String.class);
                if (null != resolver.getResource(pagePath)) {
                    targets.add(pageTarget);
                }
            }
        }
        return targets.iterator();
    }

    /**
     *  Serves a list of links from a list of multifield nodes
     * @param resourceIterator the authored multifield nodes
     * @param resource the component from which the model is called
     * @return a list of path:title
     */
    public List<LinkedHashMap> getExternalLinksFromMultifield(Iterator<Resource> resourceIterator, Resource resource) {
        LinkedHashMap<String, String> links;
        List<LinkedHashMap> linkList = new LinkedList<>();

        if (null != resourceIterator) {
            while (resourceIterator.hasNext()) {
                links = new LinkedHashMap<>();
                String itemPath = resourceIterator.next().getPath();
                ResourceResolver resolver = resource.getResourceResolver();
                Resource thisResource = resolver.getResource(itemPath);

                ValueMap valueMap = thisResource.getValueMap();
                String properties[] = getPropertiesKey();

                for (String property : properties) {
                    if (valueMap.containsKey(property)) {
                        if(property.equals("link")){
                            links.put(property, getValidPath(valueMap.get(property, String.class)));
                        } else {
                            links.put(property, valueMap.get(property, String.class));
                        }
                    }
                }

                linkList.add(links);
            }
        }
        return linkList;
    }

    private String getValidPath(String path){
        if (path.startsWith("/content/")){
            return path.concat(".html");
        }
        return path;
    }

    /**
     * The array of properties to fetch from a component node
     * @return
     */
    private String[] getPropertiesKey() {
        return new String[]{PATH_KEY, TARGET_KEY, TITLE_KEY, IMAGE_KEY, TEXT_KEY, MULTIFIELD_GENERIC_LINK_KEY};
    }
}

