"use strict";
use(function () {
    var analytics = {};
    var tags = resource.properties["cq:tags"];
    if(resource.properties["cq:template"] == "/conf/basicsite/settings/wcm/templates/article-page"){
        analytics["data-story-type"] = "article";
    }

    for(var tag in tags){
        var tagKeyValue = tags[tag].substring(tags[tag].indexOf(":")+1).split("/");
        analytics["data-"+tagKeyValue[0]] = tagKeyValue[1];
    }
    return analytics;
});