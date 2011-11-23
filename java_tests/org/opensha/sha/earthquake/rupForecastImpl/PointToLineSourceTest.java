package org.opensha.sha.earthquake.rupForecastImpl;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opensha.commons.calc.magScalingRelations.MagScalingRelationship;
import org.opensha.commons.calc.magScalingRelations.magScalingRelImpl.WC1994_MagAreaRelationship;
import org.opensha.commons.data.function.ArbitrarilyDiscretizedFunc;
import org.opensha.commons.geo.Location;
import org.opensha.sha.earthquake.FocalMechanism;
import org.opensha.sha.earthquake.ProbEqkRupture;
import org.opensha.sha.faultSurface.EvenlyGriddedSurfaceAPI;
import org.opensha.sha.faultSurface.PointSurface;
import org.opensha.sha.imr.param.EqkRuptureParams.DipParam;
import org.opensha.sha.magdist.GutenbergRichterMagFreqDist;
import org.opensha.sha.magdist.IncrementalMagFreqDist;

public class PointToLineSourceTest
{

    /**
     * When area sources are treated as line source,
     * we must set the dip for the rupture surface.
     * (PointSurfaces must also have the strike set.)
     * 
     * Previously, the dip was not being set on PointSurfaces,
     * resulting in a ConstraintException in the CY_2008_AttenRel GMPE.
     * 
     * See:
     * https://bugs.launchpad.net/openquake/+bug/887989
     */
    @Test
    public void testMkAndAddRuptures_DipAndStrikeAreSet()
    {
        Location loc = new Location(0.0, 0.0);
        double aValue = 3.2;
        double bValue = 0.7;
        double min = 5.0;
        double max = 8.0;
        double delta = 0.1;
        int num = (int) Math.round((max - min)/delta + 1);
        double totCumRate = Math.pow(10.0, aValue - bValue * min) -
                            Math.pow(10.0, aValue - bValue * max);
        IncrementalMagFreqDist magDist =
                new GutenbergRichterMagFreqDist(bValue, totCumRate,
                        min+delta/2, max-delta/2, num);
        FocalMechanism fm = new FocalMechanism(0.0, 90.0, 0.0);
        ArbitrarilyDiscretizedFunc aveRupTopVersusMag = new ArbitrarilyDiscretizedFunc();
        aveRupTopVersusMag.set(6.0, 0.0);
        double defaultHypoDepth = 5.0;
        MagScalingRelationship magScalingRel = new WC1994_MagAreaRelationship();
        double lowerSeisDepth = 15.0;  // km
        double duration = 50.0;
        double minMag = 5.0;
        double weight = 1.0;


        PointToLineSource pls = new PointToLineSource();
        pls.probEqkRuptureList = new ArrayList<ProbEqkRupture>();
        pls.rates = new ArrayList<Double>();

        pls.mkAndAddRuptures(
                loc,
                magDist,
                fm,
                aveRupTopVersusMag,
                defaultHypoDepth,
                magScalingRel,
                lowerSeisDepth,
                duration,
                minMag,
                weight);

        assertEquals(31, pls.probEqkRuptureList.size());
        ArrayList<EvenlyGriddedSurfaceAPI> surfaces = new ArrayList<EvenlyGriddedSurfaceAPI>();
        for (ProbEqkRupture rupture : pls.probEqkRuptureList) {
            EvenlyGriddedSurfaceAPI surface = rupture.getRuptureSurface();
            surfaces.add(surface);
            assertFalse(Double.isNaN(surface.getAveDip()));
            // PointSurfaces need to have strike set as well:
            if (surface instanceof PointSurface) {
                assertFalse(Double.isNaN(surface.getAveStrike()));
            }
        }
    }

}
