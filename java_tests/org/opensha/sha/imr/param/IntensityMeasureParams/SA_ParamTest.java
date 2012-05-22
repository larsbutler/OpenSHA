package org.opensha.sha.imr.param.IntensityMeasureParams;

import org.junit.Before;
import org.junit.Test;
import org.opensha.commons.param.DoubleDiscreteConstraint;
import org.opensha.commons.exceptions.WarningException;
import org.opensha.commons.exceptions.ConstraintException;

public class SA_ParamTest
{

    private SA_Param saParam;

    @Before
    public void setUp()
    {
        double[] period = { 0.01, 0.02, 0.03, 0.04, 0.05,
                0.075, 0.1, 0.15, 0.2, 0.25, 0.3, 0.4, 0.5, 0.75, 1, 1.5, 2, 3, 4,
                5, 7.5, 10};
        DoubleDiscreteConstraint ddc = new DoubleDiscreteConstraint();
        for (double d : period)
        {
            ddc.addDouble(d);
        }

        PeriodParam pp = new PeriodParam(ddc);
        DampingParam dp = new DampingParam();

        saParam = new SA_Param(pp, dp);
    }

    @Test
    public void testDefaultWarnMax()
    {
        // Should succeed with no error.
        // This is maximum allowed value.
        saParam.setValue(Math.log(Double.MAX_VALUE));
    }

    @Test(expected=WarningException.class)
    public void testDefaultWarnMax2()
    {
        saParam.setValue(Double.MAX_VALUE);
    }

    @Test(expected=WarningException.class)
    public void testDefaultWarnMax3()
    {
        // The test value just slightly exceeds the max allowed value.
        saParam.setValue(Math.log(Double.MAX_VALUE) + 0.0000000001);
    }

    @Test
    public void testMinValue()
    {
        saParam.setValue(Math.log(Double.MIN_VALUE));
    }

    @Test(expected=ConstraintException.class)
    public void testMinValue2()
    {
        saParam.setValue(Math.log(Double.MIN_VALUE) - 0.0000000001);
    }
}
