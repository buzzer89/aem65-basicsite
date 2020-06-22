package com.adobe.aem.guides.basicsite.core.models.utils;


import com.day.cq.commons.PathInfo;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import javax.annotation.PostConstruct;
import org.apache.sling.models.annotations.injectorspecific.Self;

@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SelectorCheck {

    @Self
    SlingHttpServletRequest request;

    private boolean isEmbedded = false;

    @PostConstruct
    protected void init() {
        isEmbedded = hasSelector(request, "embed");
    }

    /** True if our request has the given selector */
    private boolean hasSelector(SlingHttpServletRequest req, String selectorToCheck) {
        for (String selector : getPathInfo(req).getSelectors()) {
            if (selectorToCheck.equals(selector)) {
                return true;
            }
        }
        return false;
    }

    private PathInfo getPathInfo(SlingHttpServletRequest req) {
        return new PathInfo(request.getPathInfo());
    }

    public boolean isEmbedded() {
        return isEmbedded;
    }
}