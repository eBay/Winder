package org.ebayopensource.winder;

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