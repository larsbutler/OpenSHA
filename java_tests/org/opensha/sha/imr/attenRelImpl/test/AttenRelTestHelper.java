package org.opensha.sha.imr.attenRelImpl.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;

import org.opensha.commons.data.Site;
import org.opensha.commons.geo.Location;
import org.opensha.commons.geo.LocationUtils;
import org.opensha.sha.earthquake.EqkRupture;
import org.opensha.sha.faultSurface.EvenlyGriddedSurfaceAPI;
import org.opensha.sha.faultSurface.FaultTrace;
import org.opensha.sha.faultSurface.PointSurface;
import org.opensha.sha.faultSurface.StirlingGriddedSurface;
import org.opensha.sha.imr.param.PropagationEffectParams.DistanceEpicentralParameter;

public class AttenRelTestHelper {

    /**
     * Creates an EqkRupture object for a point source.
     */
    public static EqkRupture getPointEqkRupture(double mag, Location hypo,
            double aveRake) {
        EvenlyGriddedSurfaceAPI rupSurf = new PointSurface(hypo);
        EqkRupture rup = new EqkRupture(mag, aveRake, rupSurf, hypo);
        return rup;
    }

    /**
     * Creates an EqkRupture object for a finite source.
     */
    public static EqkRupture getFiniteEqkRupture(double aveDip,
            double lowerSeisDepth, double upperSeisDepth,
            FaultTrace faultTrace, double gridSpacing, double mag,
            Location hypo, double aveRake) {
        StirlingGriddedSurface rupSurf =
                new StirlingGriddedSurface(faultTrace, aveDip, upperSeisDepth,
                        lowerSeisDepth, gridSpacing);
        EqkRupture rup = new EqkRupture(mag, aveRake, rupSurf, hypo);
        return rup;
    }

    /**
     * Applies "Pytagoras" to the horizontal and vertical distances between hypo
     * and location.
     * 
     * @param location
     * @param hypo
     * @return
     */
    public static double calcHypoDist(Location location, Location hypo) {
        double hypoDist =
                Math.sqrt(Math.pow(LocationUtils.horzDistance(hypo, location),
                        2)
                        + Math.pow(LocationUtils.vertDistance(hypo, location),
                                2));
        return hypoDist;
    }
    
	/**
	 * Read table from file and store in a bidimensional array.
	 */
	public static void readTable(final File file, final double[][] table)
			throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		StringTokenizer st = null;
		int rowIndex = 0;
		while ((line = br.readLine()) != null) {
			st = new StringTokenizer(line);
			for (int i = 0; i < table[0].length; i++) {
				table[rowIndex][i] = Double.valueOf(st.nextToken());
			}
			rowIndex = rowIndex + 1;
		}
	}

}
