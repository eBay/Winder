/**
 * Copyright (c) 2016 eBay Software Foundation. All rights reserved.
 *
 * Licensed under the MIT license.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.ebayopensource.winder.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ebayopensource.common.util.Parameters;
import org.ebayopensource.common.util.ParametersMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Json Util
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public class JsonUtil {


    //Object Mapper is thread safe
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static JsonNode readTree(String str) throws IOException {
        return objectMapper.readTree(str);
    }

    public static JsonNode readTree(File src) throws IOException {
        return objectMapper.readTree(src);
    }

    public static <T> T readValue(File src, Class<T> valueType) throws IOException {
        return objectMapper.readValue(src, valueType);
    }

    public static <T> T readValue(URL src, Class<T> valueType) throws IOException {
        return objectMapper.readValue(src, valueType);
    }

    public static <T> T readValue(Reader src, Class<T> valueType) throws IOException {
        return objectMapper.readValue(src, valueType);
    }

    public static <T> T readValue(JsonNode node, Class<T> valueType) throws IOException {
        return objectMapper.readValue(node.traverse(), valueType);
    }


    public static <T> T readValue(String src, Class<T> valueType) throws IOException {
        if (src == null) {
            return null;
        }
        return objectMapper.readValue(src, valueType);
    }

    public static <T> T readValue(InputStream src, Class<T> valueType) throws IOException {
        return objectMapper.readValue(src, valueType);
    }

    public static <T> T convertValue(Object fromValue, Class<T> valueType) throws IOException {
        return objectMapper.convertValue(fromValue, valueType);
    }

    public static String writeValueAsString(Object obj) throws IOException {
        return objectMapper.writeValueAsString(obj);
    }

    private static JsonFactory jsonFactory = new JsonFactory();

    private static ObjectMapper mapper;

    private static Logger log = LoggerFactory.getLogger(JsonUtil.class);

    static {
        try {
            mapper = new ObjectMapper();
        }
        catch(Throwable t) {
            log.error("Can't create jackson mapper:", t);
        }
    }

    public static Map jsonToMap(String str) throws IOException {
        if (str != null) {
            JsonParser jsonParser = jsonFactory.createParser(str);
            jsonParser.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
            jsonParser.enable(JsonParser.Feature.ALLOW_COMMENTS);
            return mapper.readValue(jsonParser, HashMap.class);
        }
        return null;
    }

    public static Parameters jsonToParameters(String str) throws IOException {
        if (str != null) {
            JsonParser jsonParser = jsonFactory.createParser(str);
            jsonParser.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
            jsonParser.enable(JsonParser.Feature.ALLOW_COMMENTS);
            return mapper.readValue(jsonParser, ParametersMap.class);
        }
        return null;
    }
}

