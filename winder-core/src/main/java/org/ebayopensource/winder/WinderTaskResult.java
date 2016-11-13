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
package org.ebayopensource.winder;

import org.ebayopensource.common.util.Parameters;
import org.ebayopensource.common.util.ParametersMap;
import org.ebayopensource.winder.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Sheldon Shao xshao@ebay.com on 10/19/16.
 * @version 1.0
 */
public class WinderTaskResult extends ParametersMap<Object> implements TaskResult {

    private static Logger log = LoggerFactory.getLogger(WinderTaskResult.class);

    public WinderTaskResult() {

    }

    public WinderTaskResult(Parameters<Object> result) {
        super(result);
    }


    @Override
    public String toJson() {
        try {
            return JsonUtil.writeValueAsString(this);
        } catch (IOException e) {
            log.warn("Convert to json exception", e);
            throw new IllegalStateException("Illegal state");
        }
    }
}
