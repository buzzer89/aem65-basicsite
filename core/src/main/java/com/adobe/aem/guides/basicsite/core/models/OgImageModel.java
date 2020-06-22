package com.adobe.aem.guides.basicsite.core.models;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
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

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class OgImageModel {

    private final Logger log = LoggerFactory.getLogger(OgImageModel.class);

    @Inject
    Resource resource;

    @SlingObject
    private ResourceResolver resourceResolver;

    public String getImagePath() {
        return imagePath;
    }

    public String getImageUrl() {
        Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
        String url = externalizer.publishLink(resourceResolver, imagePath);
        return url;
    }

    public Boolean getHasArticleImage() {
        return imagePath != null && imagePath.length() > 0;
    }

    private String imagePath;

    @PostConstruct
    protected void init() {
        Resource myResource = resource;
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        String path = myResource.getPath();
        Page page = pageManager.getContainingPage(path);
        String pagePath = page.getPath();
        Boolean isArticle = page.getTemplate().getPath().contains("article-page");
        if (isArticle) {
            Resource header = page.adaptTo(Resource.class).getChild("jcr:content/root/responsivegrid/responsivegrid/responsivegrid/articleheader");
            try {
                imagePath = header.adaptTo(Node.class).getProperty("fileReference").getString();
            } catch (RepositoryException e) {
                log.error(e.getMessage());
            }
        }
    }

}
