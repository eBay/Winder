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
package org.ebayopensource.common.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Parameters JSON Deserializer
 *
 * @author Sheldon Shao xshao@ebay.com on 10/22/16.
 * @version 1.0
 */
public class ParametersDeserializer<P extends Parameters<Object>> extends StdDeserializer<P>
        implements ResolvableDeserializer {

    public ParametersDeserializer() {
        super(Parameters.class);
    }

    protected ParametersDeserializer(Class<? extends Parameters> clazz) {
        super(clazz);
    }

    protected JsonDeserializer<Map> _mapDeserializer;

    @SuppressWarnings("unchecked")
    protected JsonDeserializer<Map> _findCustomDeser(DeserializationContext ctxt, JavaType type)
            throws JsonMappingException {
        // NOTE: since we don't yet have the referring property, this should be fine:
        JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(type);
        return (JsonDeserializer<Map>) deser;
    }

    /**
     * We need to implement this method to properly find things to delegate
     * to: it can not be done earlier since delegated deserializers almost
     * certainly require access to this instance (at least "List" and "Map" ones)
     */
    @Override
    public void resolve(DeserializationContext ctxt) throws JsonMappingException {
        JavaType obType = ctxt.constructType(Object.class);
        JavaType stringType = ctxt.constructType(String.class);
        TypeFactory tf = ctxt.getTypeFactory();
        _mapDeserializer = _findCustomDeser(ctxt, tf.constructMapType(Map.class, stringType, obType));
    }

    @Override
    public P deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (_mapDeserializer == null) {
            resolve(ctxt);
        }
        Map map = _mapDeserializer.deserialize(jp, ctxt);
        return (P)AbstractParameters.toParameters(map);
    }
}
