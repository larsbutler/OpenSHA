package org.opensha.sha.earthquake.rupForecastImpl.GEM1;

import org.opensha.commons.data.function.ArbitrarilyDiscretizedFunc;
import org.opensha.commons.geo.BorderType;
import org.opensha.commons.geo.Location;
import org.opensha.commons.geo.LocationList;
import org.opensha.commons.geo.Region;
import org.opensha.sha.earthquake.FocalMechanism;
import org.opensha.sha.earthquake.griddedForecast.HypoMagFreqDistAtLoc;
import org.opensha.sha.earthquake.griddedForecast.MagFreqDistsForFocalMechs;
import org.opensha.sha.earthquake.rupForecastImpl.GEM1.SourceData.GEMAreaSourceData;
import org.opensha.sha.earthquake.rupForecastImpl.GEM1.SourceData.GEMFaultSourceData;
import org.opensha.sha.earthquake.rupForecastImpl.GEM1.SourceData.GEMPointSourceData;
import org.opensha.sha.earthquake.rupForecastImpl.GEM1.SourceData.GEMSubductionFaultSourceData;
import org.opensha.sha.faultSurface.FaultTrace;
import org.opensha.sha.magdist.GutenbergRichterMagFreqDist;
import org.opensha.sha.magdist.IncrementalMagFreqDist;
import org.opensha.sha.util.TectonicRegionType;

public class GEM1ERFTestHelper {

	/**
	 * Example area source data.
	 */
	public static GEMAreaSourceData getAreaSourceData() {
		String id = "Src01";
		String name = "Quito";
		TectonicRegionType tectReg = TectonicRegionType.ACTIVE_SHALLOW;
		LocationList regBorder = new LocationList();
		regBorder.add(new Location(-0.437, -78.686));
		regBorder.add(new Location(-0.076, -78.622));
		regBorder.add(new Location(0.175, -78.4408));
		regBorder.add(new Location(0.1354, -78.2786));
		regBorder.add(new Location(-0.5138, -78.4275));
		Region reg = new Region(regBorder, BorderType.GREAT_CIRCLE);
		double min = 4.5;
		double max = 7.0;
		double delta = 0.1;
		double aValueCum = 3.08;
		double bValue = 0.96;
		GutenbergRichterMagFreqDist mfd = getGutenbergRichterMagFreqDist(min,
				max, delta, aValueCum, bValue);
		double strike = 20.0;
		double dip = 90.0;
		double rake = 90.0;
		FocalMechanism focMech = new FocalMechanism(strike, dip, rake);
		MagFreqDistsForFocalMechs magfreqDistFocMech = new MagFreqDistsForFocalMechs(
				mfd, focMech);
		ArbitrarilyDiscretizedFunc aveRupTopVsMag = new ArbitrarilyDiscretizedFunc();
		double thresholdMag = 6.5;
		double topOfRupDepth = 2.5;
		aveRupTopVsMag.set(thresholdMag, topOfRupDepth);
		Double aveHypoDepth = 5.0;
		GEMAreaSourceData areaSrc = new GEMAreaSourceData(id, name, tectReg,
				reg, magfreqDistFocMech, aveRupTopVsMag, aveHypoDepth);
		return areaSrc;
	}

	/**
	 * Example fault source data.
	 */
	public static GEMFaultSourceData getFaultSourceData() {
		String id = "Src01";
		String name = "Mount Diablo Thrust";
		TectonicRegionType tectReg = TectonicRegionType.ACTIVE_SHALLOW;
		double min = 6.55;
		int num = 5;
		double delta = 0.1;
		IncrementalMagFreqDist mfd = new IncrementalMagFreqDist(min, num, delta);
		mfd.add(0, 0.0010614989);
		mfd.add(1, 8.8291627E-4);
		mfd.add(2, 7.3437777E-4);
		mfd.add(3, 6.108288E-4);
		mfd.add(4, 5.080653E-4);
		FaultTrace trc = new FaultTrace("");
		trc.add(new Location(37.73010, -121.82290, 8.0));
		trc.add(new Location(37.87710, -122.03880, 8.0));
		double dip = 38.0;
		double rake = 90.0;
		double seismDepthLow = 8.0;
		double seismDepthUpp = 13.0;
		boolean floatRuptureFlag = true;
		GEMFaultSourceData faultSrc = new GEMFaultSourceData(id, name, tectReg,
				mfd, trc, dip, rake, seismDepthLow, seismDepthUpp,
				floatRuptureFlag);
		return faultSrc;
	}

	/**
	 * Example subduction fault source data.
	 */
	public static GEMSubductionFaultSourceData getSubductionFaultSourceData() {
		String id = "Src01";
		String name = "Cascadia Megathrust";
		TectonicRegionType tectReg = TectonicRegionType.SUBDUCTION_INTERFACE;
		FaultTrace topTrace = new FaultTrace("");
		topTrace.add(new Location(40.363, -124.704, 0.5493260E+01));
		topTrace.add(new Location(41.214, -124.977, 0.4988560E+01));
		topTrace.add(new Location(42.096, -125.140, 0.4897340E+01));
		FaultTrace bottomTrace = new FaultTrace("");
		bottomTrace.add(new Location(40.347, -123.829, 0.2038490E+02));
		bottomTrace.add(new Location(41.218, -124.137, 0.1741390E+02));
		bottomTrace.add(new Location(42.115, -124.252, 0.1752740E+02));
		double rake = 90.0;
		double min = 6.5;
		double max = 9.0;
		double delta = 0.1;
		double aValueCum = 1.0;
		double bValue = 0.8;
		GutenbergRichterMagFreqDist mfd = getGutenbergRichterMagFreqDist(min,
				max, delta, aValueCum, bValue);
		boolean floatRuptureFlag = true;
		GEMSubductionFaultSourceData subFaultSrc = new GEMSubductionFaultSourceData(
				id, name, tectReg, topTrace, bottomTrace, rake, mfd,
				floatRuptureFlag);
		return subFaultSrc;
	}

	/**
	 * Example point source data.
	 */
	public static GEMPointSourceData getPointSourceData() {
		String id = "Src01";
		String name = "point";
		TectonicRegionType tectReg = TectonicRegionType.ACTIVE_SHALLOW;
		double min = 5.0;
		double max = 7.0;
		double delta = 0.1;
		double aValueCum = 0.1;
		double bValue = 0.8;
		GutenbergRichterMagFreqDist mfd = getGutenbergRichterMagFreqDist(min,
				max, delta, aValueCum, bValue);
		Location loc = new Location(38.0,-122.0);
		double strike = 0.0;
		double dip = 90.0;
		double rake = 0.0;
		FocalMechanism focalMech = new FocalMechanism(strike,dip,rake);
		HypoMagFreqDistAtLoc hypoMagFreqDistAtLoc =
			new HypoMagFreqDistAtLoc(mfd, loc, focalMech);
		ArbitrarilyDiscretizedFunc aveRupTopVsMag = new ArbitrarilyDiscretizedFunc();
		double magThreshold = 6.5;
		double topOfRupDepth = 2.5;
		aveRupTopVsMag.set(magThreshold, topOfRupDepth);
		double aveHypoDepth = 5.0;
		GEMPointSourceData pointSrc = new GEMPointSourceData(id, name, tectReg,
				hypoMagFreqDistAtLoc, aveRupTopVsMag, aveHypoDepth);
		return pointSrc;
	}

	/**
	 * Compute Gutenberg-Richter magnitude frequency distribution.
	 */
	private static GutenbergRichterMagFreqDist getGutenbergRichterMagFreqDist(
			double min, double max, double delta, double aValueCum,
			double bValue) {
		double totCumRate = Math.pow(10, aValueCum - bValue * min)
				- Math.pow(10, aValueCum - bValue * max);
		int num = (int) (((max - delta / 2) - (min + delta / 2)) / delta + 1);
		GutenbergRichterMagFreqDist mfd = new GutenbergRichterMagFreqDist(min
				+ delta / 2, max - delta / 2, num);
		mfd.setAllButTotMoRate(min + delta / 2, max - delta / 2, totCumRate,
				bValue);
		return mfd;
	}
}
