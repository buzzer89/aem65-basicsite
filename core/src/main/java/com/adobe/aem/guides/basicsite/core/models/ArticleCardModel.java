package com.adobe.aem.guides.basicsite.core.models;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.aem.guides.basicsite.core.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.*;

/**
 * Provides information from Article Header component
 *
 * @author ConexioGroup (nmuscarelli)
 */

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ArticleCardModel {

    private static final String ARTICLE_COMPONENT_PATH =  "jcr:content/root/responsivegrid/responsivegrid/responsivegrid/articleheader";
    private static final String SLASH =  "/";
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleCardModel.class);

    @Inject
    String pathArticle;

    private String overlaycolor;

    private String imageQ;

    private String description;

    private String quote;

    private String storytitle;

    private String themetag = StringUtils.EMPTY;

    @Inject
    Map<Map<String, String>, Object> propsArticle;

    @SlingObject
    private ResourceResolver resourceResolver;

    private String[] zbThemeTags;
    private String tagColorName;


    @PostConstruct
    protected void init() throws RepositoryException {
        if(pathArticle!= null && !pathArticle.isEmpty()) {
            this.articleProperties(pathArticle, resourceResolver);
        }
    }

    public void articleProperties(String pathArticle, ResourceResolver resourceResolver) {
        if (StringUtils.isNotEmpty(pathArticle)) {
            try {
                boolean pathArticleExists = resourceResolver.getResource(pathArticle) != null;
                if (pathArticleExists) {
                    Page pathArticleProps = resourceResolver.getResource(pathArticle).adaptTo(Page.class);
                    Resource pageContainingArticleHeader = resourceResolver.getResource(pathArticle + SLASH + ARTICLE_COMPONENT_PATH);
                    if (pageContainingArticleHeader != null) {
                        description = pathArticleProps.getDescription();
                        Node nodehArticleProps = resourceResolver.getResource(pageContainingArticleHeader.getPath()).adaptTo(Node.class);
                        overlaycolor = nodehArticleProps.hasProperty("overlayColor") ? nodehArticleProps.getProperty("overlayColor").getValue().toString() : "";
                        imageQ = nodehArticleProps.hasProperty("fileReference") ? nodehArticleProps.getProperty("fileReference").getValue().toString() : "";
                        quote = nodehArticleProps.hasProperty("quote") ? nodehArticleProps.getProperty("quote").getValue().toString() : "";
                        storytitle = nodehArticleProps.hasProperty("storytitle") ? nodehArticleProps.getProperty("storytitle").getValue().toString() : "";

                        Tag[] tags = pathArticleProps.getTags();
                        zbThemeTags = new String[tags.length];
                        if (tags.length > 0) {
                            themetag = Utils.getJourneyTag(tags);
                            tagColorName = getTagColorName();
                            for (int i = 0; i < tags.length; i++) {
                                zbThemeTags[i] = tags[i].getTagID();
                            }
                        }
                    }
                }
            } catch (RepositoryException e) {
                LOGGER.warn("Unable to get article",e);
            }
        }
    }

    public String getDescription() {
        return description;
    }

    public String getOverlaycolor() {
        return overlaycolor;
    }

    public void setOverlaycolor(String overlaycolor) {
        this.overlaycolor = overlaycolor;
    }

    public String getImageQ() {
        return imageQ;
    }

    public void setImageQ(String imageQ) {
        this.imageQ = imageQ;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getStorytitle() {
        return storytitle;
    }

    public void setStorytitle(String storytitle) {
        this.storytitle = storytitle;
    }

    public String getThemetag() {
        return themetag;
    }

    public void setThemetag(String themetag) {
        this.themetag = themetag;
    }

    public String getPathArticle() {
        return pathArticle;
    }

    public void setPathArticle(String pathArticle) {
        this.pathArticle = pathArticle;
    }

    public String[] getZbThemeTags() {
        return zbThemeTags;
    }


    public String getTagColorName(){
        return this.themetag != null ? Utils.getColorTag(this.themetag) : StringUtils.EMPTY;
    }
}

