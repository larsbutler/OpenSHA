package org.opensha.commons.param;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.opensha.commons.param.event.ParameterChangeWarningEvent;
import org.opensha.commons.param.event.ParameterChangeWarningListener;

public class WarningDoubleParameterTest
{

    private WarningDoubleParameter wdp;
    private ParameterChangeWarningListener listener;

    @Before
    public void setUp()
    {
        wdp = new WarningDoubleParameter("Test");
        listener = new ParameterChangeWarningListener()
        {
            public void parameterChangeWarning(ParameterChangeWarningEvent event){}

        };
    }
    /**
     * Test addParameterChangeWarningListener with null input.
     * 
     * If the input is null, it should simply be ignored.
     */
    @Test
    public void testAddNullPCWL()
    {
        assertNull(wdp.getWarningListeners());

        wdp.addParameterChangeWarningListener(null);
        // It should still be null.
        assertNull(wdp.getWarningListeners());

        // Add a real listener.
        wdp.addParameterChangeWarningListener(listener);
        assertEquals(1, wdp.getWarningListeners().size());
        assertEquals(listener, wdp.getWarningListeners().get(0));

        // Now try to add a null; the current list should be unaffected.
        wdp.addParameterChangeWarningListener(null);
        assertEquals(1, wdp.getWarningListeners().size());
        assertEquals(listener, wdp.getWarningListeners().get(0));
    }

    /**
     * Test addParameterChangeWarningListener with normal input.
     */
    @Test
    public void testAddPCWL()
    {
        assertNull(wdp.getWarningListeners());

        wdp.addParameterChangeWarningListener(listener);
        assertEquals(1, wdp.getWarningListeners().size());
        assertEquals(listener, wdp.getWarningListeners().get(0));
    }
}
