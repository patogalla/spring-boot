package com.patogalla.api.utils.template;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TemplateService {

    private final ConcurrentHashMap<String, Template> templates = new ConcurrentHashMap<>();

    public String template(final String path, Map<String, Object> params) throws IOException {
        return getCompiledTemplate(path).execute(params);
    }

    private Template getCompiledTemplate(String path) throws IOException {
        if (!templates.containsKey(path)) {
            templates.put(path, Mustache.compiler().escapeHTML(false).compile(loadTemplate(path)));
        }
        return templates.get(path);
    }

    private String loadTemplate(final String path) throws IOException {
        URL url = Resources.getResource(path);
        return Resources.toString(url, Charsets.UTF_8);
    }
}
