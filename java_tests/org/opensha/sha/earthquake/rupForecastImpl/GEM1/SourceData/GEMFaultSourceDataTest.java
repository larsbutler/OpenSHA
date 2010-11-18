package org.opensha.sha.earthquake.rupForecastImpl.GEM1.SourceData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.opensha.commons.geo.Location;
import org.opensha.sha.faultSurface.FaultTrace;
import org.opensha.sha.magdist.IncrementalMagFreqDist;
import org.opensha.sha.util.TectonicRegionType;

public class GEMFaultSourceDataTest
{

    @Test
    public void equals()
    {
        IncrementalMagFreqDist dist1 = new IncrementalMagFreqDist(0.1, 1.0, 5);
        IncrementalMagFreqDist dist2 = new IncrementalMagFreqDist(0.1, 1.0, 7);

        // same data but different objects to make sure equals()
        // is overrided
        FaultTrace trace1 = new FaultTrace("trace1");
        trace1.add(new Location(1.0, 1.0));

        FaultTrace trace2 = new FaultTrace("trace2");
        trace2.add(new Location(1.0, 1.0));

        FaultTrace trace3 = new FaultTrace("trace3");
        trace3.add(new Location(2.0, 2.0));

        GEMFaultSourceData data1 = new GEMFaultSourceData("id", "name",
                TectonicRegionType.ACTIVE_SHALLOW, dist1, trace1, 1.0, 1.0,
                1.0, 1.0, false);

        GEMFaultSourceData data2 = new GEMFaultSourceData("id", "name",
                TectonicRegionType.ACTIVE_SHALLOW, dist1, trace2, 1.0, 1.0,
                1.0, 1.0, false);

        // different trace
        GEMFaultSourceData data3 = new GEMFaultSourceData("id", "name",
                TectonicRegionType.ACTIVE_SHALLOW, dist1, trace3, 1.0, 1.0,
                1.0, 1.0, false);

        // different dist
        GEMFaultSourceData data4 = new GEMFaultSourceData("id", "name",
                TectonicRegionType.ACTIVE_SHALLOW, dist2, trace1, 1.0, 1.0,
                1.0, 1.0, false);

        // different id
        GEMFaultSourceData data5 = new GEMFaultSourceData("another_id", "name",
                TectonicRegionType.ACTIVE_SHALLOW, dist2, trace1, 1.0, 1.0,
                1.0, 1.0, false);

        // different name
        GEMFaultSourceData data6 = new GEMFaultSourceData("another_id",
                "another_name", TectonicRegionType.ACTIVE_SHALLOW, dist2,
                trace1, 1.0, 1.0, 1.0, 1.0, false);

        // different tectonic region
        GEMFaultSourceData data7 = new GEMFaultSourceData("id", "name",
                TectonicRegionType.STABLE_SHALLOW, dist2, trace1, 1.0, 1.0,
                1.0, 1.0, false);

        // different dip
        GEMFaultSourceData data8 = new GEMFaultSourceData("id", "name",
                TectonicRegionType.STABLE_SHALLOW, dist2, trace1, 2.0, 1.0,
                1.0, 1.0, false);

        // different rake
        GEMFaultSourceData data9 = new GEMFaultSourceData("id", "name",
                TectonicRegionType.STABLE_SHALLOW, dist2, trace1, 2.0, 2.0,
                1.0, 1.0, false);

        // different seism depth low
        GEMFaultSourceData data10 = new GEMFaultSourceData("id", "name",
                TectonicRegionType.STABLE_SHALLOW, dist2, trace1, 2.0, 2.0,
                2.0, 1.0, false);

        // different seism depth up
        GEMFaultSourceData data11 = new GEMFaultSourceData("id", "name",
                TectonicRegionType.STABLE_SHALLOW, dist2, trace1, 2.0, 2.0,
                2.0, 2.0, false);

        assertEquals(data1, data2);

        assertFalse(data1.equals(data3));
        assertFalse(data1.equals(data4));
        assertFalse(data4.equals(data5));
        assertFalse(data4.equals(data5));
        assertFalse(data5.equals(data6));
        assertFalse(data6.equals(data7));
        assertFalse(data7.equals(data8));
        assertFalse(data8.equals(data9));
        assertFalse(data10.equals(data11));

        assertFalse(data1.equals(new Object()));
    }

}
