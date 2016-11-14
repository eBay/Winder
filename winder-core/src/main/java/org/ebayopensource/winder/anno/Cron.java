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
package org.ebayopensource.winder.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Cron table style
 *
 * http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger.html
 *
 * Format
 *
 * A cron expression is a string comprised of 6 or 7 fields separated by white space.
 * Fields can contain any of the allowed values, along with various combinations of the allowed special characters for that field.
 *
 * The fields are as follows:
 *
 * Field Name	Mandatory	Allowed Values	Allowed Special Characters
 * Seconds	    NO	0-59	, - * /
 * Minutes	    NO	0-59	, - * /
 * Hours	    NO	0-23	, - * /
 * Day of month	NO	1-31	, - * ? / L W
 * Month	    YES	1-12 or JAN-DEC	, - * /
 * Day of week	YES	1-7 or SUN-SAT	, - * ? / L #
 * Year	NO	empty, 1970-2099	, - * /
 *
 * So cron expressions can be as simple as this: * * * * ? *
 * or more complex, like this: 0/5 14,18,3-39,52 * ? JAN,MAR,SEP MON-FRI 2002-2010
 *
 * @author Sheldon Shao xshao@ebay.com on 11/13/16.
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cron {

    /**
     * Seconds
     */
    String seconds() default "0";

    /**
     * Minutes
     */
    String minutes() default "*";

    /**
     * Hours
     */
    String hours() default "*";

    /**
     * Day of Month
     */
    String dayOfMonth() default "*";

    /**
     * Month
     */
    String month() default "*";

    /**
     * Day of Week
     */
    String dayOfWeek() default "*";

    /**
     * Year
     */
    String year() default "*";
}
