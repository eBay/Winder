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
package org.ebayopensource.winder.metadata;

import org.ebayopensource.winder.examples.SimpleJob;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Winder Step Registry
 *
 * @author Sheldon Shao xshao@ebay.com on 11/7/16.
 * @version 1.0
 */
public class WinderStepRegistryTest {


    @Test
    public void lookup() throws Exception {
        WinderStepRegistry registry = new WinderStepRegistry();
        registry.register(SimpleJob.class);

        assertEquals(registry.lookup(SimpleJob.class, 10).code(), 10);
        assertEquals(registry.lookup(SimpleJob.class, 20).code(), 20);
    }

    @Test
    public void register() throws Exception {

    }

    @Test
    public void getJobType() throws Exception {

    }

    @Test
    public void getFirstStep() throws Exception {

    }

    @Test
    public void getErrorStep() throws Exception {

    }

    @Test
    public void getDoneSteps() throws Exception {

    }
}