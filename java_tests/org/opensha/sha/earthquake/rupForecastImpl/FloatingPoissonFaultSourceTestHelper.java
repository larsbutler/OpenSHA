package org.opensha.sha.earthquake.rupForecastImpl;

import java.util.ArrayList;
import java.util.ListIterator;

import org.opensha.commons.calc.magScalingRelations.MagScalingRelationship;
import org.opensha.commons.calc.magScalingRelations.magScalingRelImpl.PEER_testsMagAreaRelationship;
import org.opensha.commons.data.TimeSpan;
import org.opensha.commons.geo.Location;
import org.opensha.commons.geo.Region;
import org.opensha.commons.param.ParameterAPI;
import org.opensha.commons.param.ParameterList;
import org.opensha.sha.earthquake.EqkRupForecastAPI;
import org.opensha.sha.earthquake.EqkRupture;
import org.opensha.sha.earthquake.ProbEqkRupture;
import org.opensha.sha.earthquake.ProbEqkSource;
import org.opensha.sha.faultSurface.FaultTrace;
import org.opensha.sha.faultSurface.StirlingGriddedSurface;
import org.opensha.sha.magdist.GutenbergRichterMagFreqDist;
import org.opensha.sha.magdist.IncrementalMagFreqDist;
import org.opensha.sha.util.TectonicRegionType;

/**
 * Class providing helper methods for
 * {@link FloatingPoissonFaultSourceTest} class.
 */
public class FloatingPoissonFaultSourceTestHelper {
	
	public static FloatingPoissonFaultSource getPeerTestSet1Case5FaultSource(){
		FloatingPoissonFaultSource src = null;
		double aValue = 3.1292;
		double bValue = 0.9;
		double min = 5.0;
		double max = 6.5;
		double delta = 0.1;
		int num = (int) Math.round((max - min)/delta + 1);
		double totCumRate = Math.pow(10.0, aValue - bValue * min) -
							Math.pow(10.0, aValue - bValue * max);
		IncrementalMagFreqDist magDist =
			new GutenbergRichterMagFreqDist(bValue, totCumRate,
					min+delta/2, max-delta/2, num);
		FaultTrace faultTrace = new FaultTrace("");
		faultTrace.add(new Location(38.00000,-122.00000,0.0));
		faultTrace.add(new Location(38.22480,-122.00000,0.0));
		double aveDip = 90.0;
		double upperSeismogenicDepth = 0.0;
		double lowerSeismogenicDepth = 12.0;
		double gridSpacing = 1.0;
		StirlingGriddedSurface faultSurface =
			new StirlingGriddedSurface(faultTrace, aveDip,
					upperSeismogenicDepth, lowerSeismogenicDepth, gridSpacing);
		MagScalingRelationship magScalingRel =
			new PEER_testsMagAreaRelationship();
		double magScalingSigma = 0.0;
		double rupAspectRatio = 2.0;
		double rupOffset = 1.0;
		double rake = 0.0;
		double duration = 1.0;
		double minMag = 0.0;
		int floatTypeFlag = 1;
		double fullFaultRupMagThresh = 12.0;
		src = new FloatingPoissonFaultSource(magDist, faultSurface,
				magScalingRel, magScalingSigma, rupAspectRatio,
				rupOffset, rake, duration, minMag, floatTypeFlag,
				fullFaultRupMagThresh);
		return src;
	}
	
	public static EqkRupForecastAPI getPeerTestSet1Case5FaultSourceErf() {
		return new EqkRupForecastAPI() {

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void updateForecast() {
				// TODO Auto-generated method stub

			}

			@Override
			public String updateAndSaveForecast() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setTimeSpan(TimeSpan time) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean setParameter(String name, Object value) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isLocWithinApplicableRegion(Location loc) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public TimeSpan getTimeSpan() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ArrayList<TectonicRegionType> getIncludedTectonicRegionTypes() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Region getApplicableRegion() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ListIterator<ParameterAPI> getAdjustableParamsIterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ParameterList getAdjustableParameterList() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ArrayList getSourceList() {
				ArrayList sourceList = new ArrayList<ProbEqkSource>();
				sourceList.add(getPeerTestSet1Case5FaultSource());
				return null;
			}

			@Override
			public ProbEqkSource getSource(int iSource) {
				return getPeerTestSet1Case5FaultSource();
			}

			@Override
			public ProbEqkRupture getRupture(int iSource, int nRupture) {
				return getPeerTestSet1Case5FaultSource().getRupture(nRupture);
			}

			@Override
			public int getNumSources() {
				return 1;
			}

			@Override
			public int getNumRuptures(int iSource) {
				return getPeerTestSet1Case5FaultSource().getNumRuptures();
			}

			@Override
			public ArrayList<EqkRupture> drawRandomEventSet() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

}
