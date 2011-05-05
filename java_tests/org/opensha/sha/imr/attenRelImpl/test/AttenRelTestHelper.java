package org.opensha.sha.imr.attenRelImpl.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;

import org.opensha.commons.data.Site;
import org.opensha.commons.data.function.ArbitrarilyDiscretizedFunc;
import org.opensha.commons.geo.BorderType;
import org.opensha.commons.geo.GriddedRegion;
import org.opensha.commons.geo.Location;
import org.opensha.commons.geo.LocationList;
import org.opensha.commons.geo.LocationUtils;
import org.opensha.sha.earthquake.EqkRupture;
import org.opensha.sha.earthquake.FocalMechanism;
import org.opensha.sha.earthquake.griddedForecast.MagFreqDistsForFocalMechs;
import org.opensha.sha.earthquake.rupForecastImpl.GEM1.GEM1ERF;
import org.opensha.sha.earthquake.rupForecastImpl.GEM1.SourceData.GEMAreaSourceData;
import org.opensha.sha.earthquake.rupForecastImpl.GEM1.SourceData.GEMFaultSourceData;
import org.opensha.sha.earthquake.rupForecastImpl.GEM1.SourceData.GEMSubductionFaultSourceData;
import org.opensha.sha.faultSurface.EvenlyGriddedSurfaceAPI;
import org.opensha.sha.faultSurface.FaultTrace;
import org.opensha.sha.faultSurface.PointSurface;
import org.opensha.sha.faultSurface.StirlingGriddedSurface;
import org.opensha.sha.imr.param.PropagationEffectParams.DistanceEpicentralParameter;
import org.opensha.sha.magdist.GutenbergRichterMagFreqDist;
import org.opensha.sha.util.TectonicRegionType;

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
	
	/**
	 * Active crust area source data.
	 */
	public static GEMAreaSourceData getActiveCrustAreaSourceData(){
		GEMAreaSourceData srcData = null;
		String id = "Src1";
		String name = "Pun";
		TectonicRegionType tectReg = TectonicRegionType.ACTIVE_SHALLOW;
		LocationList regBoundary = new LocationList();
		regBoundary.add(new Location(-3.78,-81.18));
		regBoundary.add(new Location(-3.023,-81.09));
		regBoundary.add(new Location(-2.132,-79.194));
		regBoundary.add(new Location(-2.252,-79.032));
		regBoundary.add(new Location(-2.384,-78.842));
		double spacing = 0.1;
		Location anchor = null;
		GriddedRegion reg = new GriddedRegion(regBoundary,
				BorderType.GREAT_CIRCLE, spacing, anchor);
		double aValue = 2.7;
		double bValue = 0.96;
		double min = 4.5;
		double max = 7.5;
		double delta = 0.1;
		int num = (int) Math.round((max - min)/delta + 1);
		double totCumRate =
			Math.pow(10.0, aValue - bValue * min) -
			Math.pow(10.0,aValue - bValue * max);
		GutenbergRichterMagFreqDist mfd = 
			new GutenbergRichterMagFreqDist(bValue,
					totCumRate, min+delta/2, max-delta/2, num);
		double strike = Double.NaN;
		double dip = Double.NaN;
		double rake = 0.0;
		FocalMechanism focMech = new FocalMechanism(strike, dip, rake);
		MagFreqDistsForFocalMechs magfreqDistFocMech =
			new MagFreqDistsForFocalMechs(mfd, focMech);
		ArbitrarilyDiscretizedFunc aveRupTopVsMag =
			new ArbitrarilyDiscretizedFunc();
		double thresholdMag = 6.5;
		double aveHypoDepth = 5.0;
		aveRupTopVsMag.set(thresholdMag, aveHypoDepth);
		srcData = new GEMAreaSourceData(id, name, tectReg,
				reg, magfreqDistFocMech, aveRupTopVsMag, aveHypoDepth);
		return srcData;
	}
	
	/**
	 * Subduction intraslab area source data.
	 */
	public static GEMAreaSourceData getSubductionIntraSlabAreaSourceData(){
		GEMAreaSourceData srcData = null;
		String id = "Src1";
		String name = "Ibarra";
		TectonicRegionType tectReg = TectonicRegionType.SUBDUCTION_SLAB;
		LocationList regBoundary = new LocationList();
		regBoundary.add(new Location(-0.171,-75.555));
		regBoundary.add(new Location(-2.082,-80.979));
		regBoundary.add(new Location(-1.6289,-80.9326));
		regBoundary.add(new Location(-0.621,-80.632));
		regBoundary.add(new Location(-0.797,-80.138));
		regBoundary.add(new Location(0.614,-79.427));
		regBoundary.add(new Location(2.122,-78.442));
		regBoundary.add(new Location(3.349,-77.6753));
		regBoundary.add(new Location(3.349,-73.7073));
		double spacing = 0.1;
		Location anchor = null;
		GriddedRegion reg = new GriddedRegion(regBoundary,
				BorderType.GREAT_CIRCLE, spacing, anchor);
		double aValue = 4.83;
		double bValue = 1.21;
		double min = 4.5;
		double max = 7.5;
		double delta = 0.1;
		int num = (int) Math.round((max - min)/delta + 1);
		double totCumRate =
			Math.pow(10.0, aValue - bValue * min) -
			Math.pow(10.0,aValue - bValue * max);
		GutenbergRichterMagFreqDist mfd = 
			new GutenbergRichterMagFreqDist(bValue,
					totCumRate, min+delta/2, max-delta/2, num);
		double strike = Double.NaN;
		double dip = Double.NaN;
		double rake = Double.NaN;
		FocalMechanism focMech = new FocalMechanism(strike, dip, rake);
		MagFreqDistsForFocalMechs magfreqDistFocMech =
			new MagFreqDistsForFocalMechs(mfd, focMech);
		ArbitrarilyDiscretizedFunc aveRupTopVsMag =
			new ArbitrarilyDiscretizedFunc();
		double thresholdMag = 6.5;
		double aveHypoDepth = 50.0;
		aveRupTopVsMag.set(thresholdMag, aveHypoDepth);
		srcData = new GEMAreaSourceData(id, name, tectReg,
				reg, magfreqDistFocMech, aveRupTopVsMag, aveHypoDepth);
		return srcData;
	}
	
	/**
	 * Subduction interface simple fault data.
	 */
	public static GEMFaultSourceData getSubductionInterfaceSimpleFaultData(){
		GEMFaultSourceData srcData = null;
		String id = "Src1";
		String name = "Manta";
		TectonicRegionType tectReg = TectonicRegionType.SUBDUCTION_INTERFACE;
		double aValue = 4.52;
		double bValue = 1.02;
		double min = 4.5;
		double max = 7.0;
		double delta = 0.1;
		int num = (int) Math.round((max - min)/delta + 1);
		double totCumRate =
			Math.pow(10.0, aValue - bValue * min) -
			Math.pow(10.0,aValue - bValue * max);
		GutenbergRichterMagFreqDist mfd = 
			new GutenbergRichterMagFreqDist(bValue,
					totCumRate, min+delta/2, max-delta/2, num);
		FaultTrace trc = new FaultTrace("");
		trc.add(new Location(-1.515,-81.456,0.0));
		trc.add(new Location(-0.439,-81.156,0.0));
		double dip = 20.0;
		double rake = 90.0;
		double seismDepthLow = 48.0;
		double seismDepthUpp = 4.0;
		boolean floatRuptureFlag = true;
		srcData = new GEMFaultSourceData(id, name, tectReg, mfd, trc, dip, rake, seismDepthLow, seismDepthUpp, floatRuptureFlag);		
		return srcData;
	}
	
	public static ArbitrarilyDiscretizedFunc setUpHazardCurve() {
		ArbitrarilyDiscretizedFunc hazCurve = new ArbitrarilyDiscretizedFunc();
		hazCurve.set(0.005, 0.0);
		hazCurve.set(0.007, 0.0);
		hazCurve.set(0.0098, 0.0);
		hazCurve.set(0.0137, 0.0);
		hazCurve.set(0.0192, 0.0);
		hazCurve.set(0.0269, 0.0);
		hazCurve.set(0.0376, 0.0);
		return hazCurve;
	}
}
