package com.adobe.aem.guides.basicsite.core.utils;

import com.day.cq.tagging.Tag;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;

public class Utils {

    private static final String JOURNEY_STAGE_TAG =  "basicsite:journey-stage/";

    /**
     * get color tag from tagName
     * @param tagName
     * @return
     */
    public static String getColorTag(String tagName){
        return tagName.toLowerCase().replaceAll("[^a-zA-Z]+","");
    }

    /**
     * get first journey tag alphabetically or first tag from tags
     * @param tags
     * @return
     */
    public static String getJourneyTag(Tag[] tags) {
        String journeyTag = StringUtils.EMPTY;
        if (tags.length > 0) {
            List<Tag> tagList = Arrays.asList(tags);
            // filter tag list by journey-stage only
            List<Tag> journeyTags = tagList.stream().filter(tag -> tag.getTagID().contains(JOURNEY_STAGE_TAG)).collect(Collectors.toList());
            if (!journeyTags.isEmpty()) {
                // get first journey tag alphabetically
                journeyTag = journeyTags.stream().sorted(Comparator.comparing(Tag::getTitle)).findFirst().get().getTitle();
            } else {
                // if no journey tag is found, return the first tag
                journeyTag = tagList.get(0).getTitle();
            }
        }

        return journeyTag;
    }

}
