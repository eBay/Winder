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
package org.ebayopensource.winder.quartz;

import org.apache.commons.lang3.StringUtils;
import org.ebayopensource.winder.*;
import org.quartz.JobDataMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ebayopensource.winder.quartz.QuartzWinderConstants.*;

/**
 *
 * @author Sheldon Shao xshao@ebay.com on 10/16/16.
 * @version 1.0
 */
class QuartzJobUtil {

    public static String formatString(final String s, int maxlen, boolean dotdotdot) {
        if (StringUtils.isBlank(s)) {
            return "";
        }
        if (s.length() < maxlen) {
            return s;
        }
        return s.substring(0, maxlen) + (dotdotdot ? "..." : "");
    }

    static final String ID_FORMAT = "%s/%s";

    static String generateKeyName(String id, String key) {
        return String.format(ID_FORMAT, id, key);
    }

    /**
     * Add the new status update or return last if the status is same as the former one.
     *
     * @param engine
     * @param namespace
     * @param map
     * @param statusEnum
     * @param message
     * @return
     */
    public static StatusUpdate addOrGetUpdate(WinderEngine engine, String namespace, JobDataMap map,
                                              StatusEnum statusEnum, String message) {
        final String countKey = generateKeyName(namespace, KEY_COUNT);
        int size = 0;
        if (map.containsKey(countKey)) {
            size = map.getInt(countKey);
        }

        String id =  generateKeyName(namespace, String.valueOf(size - 1));


        if (size > 0) {
            String keyStatus = generateKeyName(id, KEY_STATUS_UPDATE_EXECUTION_STATUS);
            String keyMessage = generateKeyName(id, KEY_STATUS_UPDATE_MESSAGE);
            String lastStatus = map.getString(keyStatus);
            String lastMessage = map.getString(keyMessage);
            if (statusEnum.name().equals(lastStatus) && StringUtils.equals(message, lastMessage)) {
                return new QuartzStatusUpdate(engine, map, id);
            }
        }

        size ++;
        id = generateKeyName(namespace, String.valueOf(size-1));
        map.put(countKey, size);
        return new QuartzStatusUpdate(engine, map, id, statusEnum, message);
    }

    /**
     * Add the new status update or return last if the status is same as the former one.
     *
     * @param engine
     * @param map
     * @param actionType
     * @param message
     * @param owner
     * @return
     */
    public static UserAction addUserAction(WinderEngine engine, JobDataMap map,
                                           UserActionType actionType, String message,
                                           String owner) {
        String namespace = JOB_ALERT_STATUS_PREFIX;
        final String countKey = generateKeyName(namespace, KEY_COUNT);
        int size = 0;
        if (map.containsKey(countKey)) {
            size = map.getInt(countKey);
        }
        size ++;
        String id = generateKeyName(namespace, String.valueOf(size-1));
        map.put(countKey, size);
        return new QuartzUserAction(engine, map, id, actionType, message, owner);
    }

    public static UserAction addUserAction(WinderEngine engine, JobDataMap map, UserAction userAction) {
        return addUserAction(engine, map, userAction.getType(), userAction.getMessage(), userAction.getUser());
    }

    private static Map<Class, Constructor> constructorMap = new HashMap<>();

    public static <S> List<S> getAllStatus(Class<?> clazz,
                                                                    WinderEngine engine,
                                                                    String namespace,
                                                                    JobDataMap map) {
        List<S> updates = new ArrayList<>();

        final String countKey = generateKeyName(namespace, KEY_COUNT);
        int size = 0;
        if (map.containsKey(countKey)) {
            size = map.getInt(countKey);
        }

        Constructor<S> constructor = constructorMap.get(clazz);
        if (constructor == null) {
            try {
                constructor = (Constructor<S>)clazz.getConstructor(WinderEngine.class, JobDataMap.class, String.class);
                constructorMap.put(clazz, constructor);
            } catch (NoSuchMethodException e) {
            }
        }

        String id;
        for (int i = 0; i < size; i++) {
            id = generateKeyName(namespace, String.valueOf(i));
            try {
                updates.add(constructor.newInstance(engine, map, id));
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
        }
        return updates;
    }

}
