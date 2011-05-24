package org.opensha.sha.imr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.opensha.commons.param.event.ParameterChangeWarningEvent;
import org.opensha.commons.param.event.ParameterChangeWarningListener;
import org.opensha.sha.imr.attenRelImpl.BA_2008_AttenRel;
import org.opensha.sha.imr.param.OtherParams.ComponentParam;

public class AttenuationRelationshipTest
{

    private AttenuationRelationship ar;

    @Before
    public void setUp()
    {
        ar = new BA_2008_AttenRel(new ParameterChangeWarningListener()
        {

            @Override
            public void parameterChangeWarning(ParameterChangeWarningEvent event)
            {
                // TODO Auto-generated method stub
            }
        });
    }

    @Test
    public void whenMeasureTypeIsMMINoComponentIsSet()
    {
        ar.setComponentParameter("COMPONENT_NAME", "MMI");
        assertNull(null, ar.getParameter(ComponentParam.NAME).getValue());
    }

    @Test
    public void whenMeasureTypeIsNotMMITheComponentIsSet()
    {
        // Average Horizontal (GMRotI50) is a supported parameter name
        ar.setComponentParameter("Average Horizontal (GMRotI50)", "AAA");
        assertEquals("Average Horizontal (GMRotI50)", ar.getParameter(ComponentParam.NAME).getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenTheComponentIsNotSupportedAnExceptionIsRaised()
    {
        ar.setComponentParameter("NOT_SUPPORTED", "AAA");
        assertEquals("NOT_SUPPORTED", ar.getParameter(ComponentParam.NAME).getValue());
    }

}
