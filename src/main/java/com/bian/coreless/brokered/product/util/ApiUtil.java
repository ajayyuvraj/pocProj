package com.bian.coreless.brokered.product.util;

import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;

public class ApiUtil {

    public HttpPost intializeHttpPost(String url) {
        HttpPost post = new HttpPost(url);
        return post;
    }

    public HttpGet intializeHttpGet(String url, List<NameValuePair> getParams) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(url);
        uriBuilder.addParameters(getParams);
        HttpGet request = new HttpGet(uriBuilder.build());
        return request;
    }
}
