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
