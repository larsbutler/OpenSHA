package org.opensha.sha.earthquake.rupForecastImpl;

import java.util.ArrayList;
import java.util.ListIterator;

import org.opensha.commons.data.TimeSpan;
import org.opensha.commons.data.function.ArbitrarilyDiscretizedFunc;
import org.opensha.commons.geo.BorderType;
import org.opensha.commons.geo.Location;
import org.opensha.commons.geo.LocationList;
import org.opensha.commons.geo.Region;
import org.opensha.commons.param.ParameterAPI;
import org.opensha.commons.param.ParameterList;
import org.opensha.commons.param.event.ParameterChangeWarningEvent;
import org.opensha.commons.param.event.ParameterChangeWarningListener;
import org.opensha.sha.earthquake.EqkRupForecastAPI;
import org.opensha.sha.earthquake.EqkRupture;
import org.opensha.sha.earthquake.FocalMechanism;
import org.opensha.sha.earthquake.ProbEqkRupture;
import org.opensha.sha.earthquake.ProbEqkSource;
import org.opensha.sha.earthquake.griddedForecast.MagFreqDistsForFocalMechs;
import org.opensha.sha.imr.ScalarIntensityMeasureRelationshipAPI;
import org.opensha.sha.imr.attenRelImpl.SadighEtAl_1997_AttenRel;
import org.opensha.sha.imr.param.IntensityMeasureParams.PGA_Param;
import org.opensha.sha.imr.param.OtherParams.StdDevTypeParam;
import org.opensha.sha.magdist.GutenbergRichterMagFreqDist;
import org.opensha.sha.util.TectonicRegionType;

/**
 * Class providing helper methods for PoissonianAreaSourceTest class
 */
public class PoissonianAreaSourceTestHelper {

	private final static ParameterChangeWarningListener testParamChangeListener = new ParameterChangeWarningListener() {
		@Override
		public void parameterChangeWarning(ParameterChangeWarningEvent event) {
		}
	};

	/**
	 * Returns {@link PoissonAreaSource} based on Peer Test Set 1 Case 10 area
	 * source data. [Area source with fixed depth. Frequency magnitude
	 * distribution expressed as truncated Gutenberg Richter with Mmax = 6.5 and
	 * Mmin=5.0. Lambda(M>=Mmin) = 0.0395, b value = 0.9. Source is modeled as
	 * uniformly distributed point sources (or approximations to point source)
	 * across the area (1 km grid spacing) at a fixed depth of 5 km. More
	 * detailed description in
	 * "Verification of Probabilistic Seismic Hazard Analysis Computer Programs"
	 * , Patricia Thomas, Ivan Wong and Norman Abrahamson, PACIFIC EARTHQUAKE
	 * ENGINEERING RESEARCH CENTER, May 2010.]
	 */
	public static PoissonAreaSource getPeerTestSet1Case10AreaSource() {
		Region reg = getPeerTestSet1Case10AreaSourceRegion();
		double gridResolution = 0.1;
		MagFreqDistsForFocalMechs magFreqDistsForFocalMechs = new MagFreqDistsForFocalMechs(
				getPeerTestSet1Case10AreaSourceMagFreqDist(),
				new FocalMechanism(Double.NaN, Double.NaN, Double.NaN));
		ArbitrarilyDiscretizedFunc aveRupTopVersusMag = new ArbitrarilyDiscretizedFunc();
		aveRupTopVersusMag.set(6.5, 0.0);
		double defaultHypoDepth = 5.0;
		double duration = 1.0;
		double Mmin = 0.0;
		return new PoissonAreaSource(reg, gridResolution,
				magFreqDistsForFocalMechs, aveRupTopVersusMag,
				defaultHypoDepth, duration, Mmin);
	}

	/**
	 * Returns {@link EqkRupForecastAPI} for Peer Test Set 1 Case 10 area source
	 * using a time span of 1 year. [Area source with fixed depth. Frequency
	 * magnitude distribution expressed as truncated Gutenberg Richter with Mmax
	 * = 6.5 and Mmin=5.0. Lambda(M>=Mmin) = 0.0395, b value = 0.9. Source is
	 * modeled as uniformly distributed point sources (or approximations to
	 * point source) across the area (1 km grid spacing) at a fixed depth of 5
	 * km. More detailed description in
	 * "Verification of Probabilistic Seismic Hazard Analysis Computer Programs"
	 * , Patricia Thomas, Ivan Wong and Norman Abrahamson, PACIFIC EARTHQUAKE
	 * ENGINEERING RESEARCH CENTER, May 2010.]
	 */
	public static EqkRupForecastAPI getPeerTestSet1Case10AreaSourceErf() {
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
				sourceList.add(getPeerTestSet1Case10AreaSource());
				return null;
			}

			@Override
			public ProbEqkSource getSource(int iSource) {
				return getPeerTestSet1Case10AreaSource();
			}

			@Override
			public ProbEqkRupture getRupture(int iSource, int nRupture) {
				return getPeerTestSet1Case10AreaSource().getRupture(nRupture);
			}

			@Override
			public int getNumSources() {
				return 1;
			}

			@Override
			public int getNumRuptures(int iSource) {
				return getPeerTestSet1Case10AreaSource().getNumRuptures();
			}

			@Override
			public ArrayList<EqkRupture> drawRandomEventSet() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	/**
	 * Returns ground motion prediction equation (GMPE) for Peer Test Set1 Case
	 * 10. (Sadigh et al 1997, PGA, standard deviation = 0.)
	 */
	public static ScalarIntensityMeasureRelationshipAPI getPeerTestSet1Case10GMPE() {
		ScalarIntensityMeasureRelationshipAPI gmpe = new SadighEtAl_1997_AttenRel(
				testParamChangeListener);
		gmpe.setParamDefaults();
		gmpe.setIntensityMeasure(PGA_Param.NAME);
		gmpe.getParameter(StdDevTypeParam.NAME).setValue(
				StdDevTypeParam.STD_DEV_TYPE_NONE);
		return gmpe;
	}

	public static GutenbergRichterMagFreqDist getPeerTestSet1Case10AreaSourceMagFreqDist() {
		double Mmin = 5.0;
		double Mmax = 6.5;
		double bValue = 0.9;
		double occRateAboveMmin = 0.0395;
		double aValue = bValue * Mmin + Math.log10(occRateAboveMmin);
		double totCumRate = Math.pow(10, aValue - bValue * Mmin)
				- Math.pow(10, aValue - bValue * Mmax);
		double delta = 0.1;

		Mmin = Mmin + delta / 2;
		Mmax = Mmax - delta / 2;
		int numVal = (int) Math.round(((Mmax - Mmin) / delta + 1));
		return new GutenbergRichterMagFreqDist(bValue, totCumRate, Mmin, Mmax,
				numVal);
	}

	public static Region getPeerTestSet1Case10AreaSourceRegion() {
		LocationList regionBorder = new LocationList();
		regionBorder.add(new Location(38.901, -122.000));
		regionBorder.add(new Location(38.899, -121.920));
		regionBorder.add(new Location(38.892, -121.840));
		regionBorder.add(new Location(38.881, -121.760));
		regionBorder.add(new Location(38.866, -121.682));
		regionBorder.add(new Location(38.846, -121.606));
		regionBorder.add(new Location(38.822, -121.532));
		regionBorder.add(new Location(38.794, -121.460));
		regionBorder.add(new Location(38.762, -121.390));
		regionBorder.add(new Location(38.727, -121.324));
		regionBorder.add(new Location(38.688, -121.261));
		regionBorder.add(new Location(38.645, -121.202));
		regionBorder.add(new Location(38.600, -121.147));
		regionBorder.add(new Location(38.551, -121.096));
		regionBorder.add(new Location(38.500, -121.050));
		regionBorder.add(new Location(38.446, -121.008));
		regionBorder.add(new Location(38.390, -120.971));
		regionBorder.add(new Location(38.333, -120.940));
		regionBorder.add(new Location(38.273, -120.913));
		regionBorder.add(new Location(38.213, -120.892));
		regionBorder.add(new Location(38.151, -120.876));
		regionBorder.add(new Location(38.089, -120.866));
		regionBorder.add(new Location(38.026, -120.862));
		regionBorder.add(new Location(37.963, -120.863));
		regionBorder.add(new Location(37.900, -120.869));
		regionBorder.add(new Location(37.838, -120.881));
		regionBorder.add(new Location(37.777, -120.899));
		regionBorder.add(new Location(37.717, -120.921));
		regionBorder.add(new Location(37.658, -120.949));
		regionBorder.add(new Location(37.601, -120.982));
		regionBorder.add(new Location(37.545, -121.020));
		regionBorder.add(new Location(37.492, -121.063));
		regionBorder.add(new Location(37.442, -121.110));
		regionBorder.add(new Location(37.394, -121.161));
		regionBorder.add(new Location(37.349, -121.216));
		regionBorder.add(new Location(37.308, -121.275));
		regionBorder.add(new Location(37.269, -121.337));
		regionBorder.add(new Location(37.234, -121.403));
		regionBorder.add(new Location(37.203, -121.471));
		regionBorder.add(new Location(37.176, -121.542));
		regionBorder.add(new Location(37.153, -121.615));
		regionBorder.add(new Location(37.133, -121.690));
		regionBorder.add(new Location(37.118, -121.766));
		regionBorder.add(new Location(37.108, -121.843));
		regionBorder.add(new Location(37.101, -121.922));
		regionBorder.add(new Location(37.099, -122.000));
		regionBorder.add(new Location(37.101, -122.078));
		regionBorder.add(new Location(37.108, -122.157));
		regionBorder.add(new Location(37.118, -122.234));
		regionBorder.add(new Location(37.133, -122.310));
		regionBorder.add(new Location(37.153, -122.385));
		regionBorder.add(new Location(37.176, -122.458));
		regionBorder.add(new Location(37.203, -122.529));
		regionBorder.add(new Location(37.234, -122.597));
		regionBorder.add(new Location(37.269, -122.663));
		regionBorder.add(new Location(37.308, -122.725));
		regionBorder.add(new Location(37.349, -122.784));
		regionBorder.add(new Location(37.394, -122.839));
		regionBorder.add(new Location(37.442, -122.890));
		regionBorder.add(new Location(37.492, -122.937));
		regionBorder.add(new Location(37.545, -122.980));
		regionBorder.add(new Location(37.601, -123.018));
		regionBorder.add(new Location(37.658, -123.051));
		regionBorder.add(new Location(37.717, -123.079));
		regionBorder.add(new Location(37.777, -123.101));
		regionBorder.add(new Location(37.838, -123.119));
		regionBorder.add(new Location(37.900, -123.131));
		regionBorder.add(new Location(37.963, -123.137));
		regionBorder.add(new Location(38.026, -123.138));
		regionBorder.add(new Location(38.089, -123.134));
		regionBorder.add(new Location(38.151, -123.124));
		regionBorder.add(new Location(38.213, -123.108));
		regionBorder.add(new Location(38.273, -123.087));
		regionBorder.add(new Location(38.333, -123.060));
		regionBorder.add(new Location(38.390, -123.029));
		regionBorder.add(new Location(38.446, -122.992));
		regionBorder.add(new Location(38.500, -122.950));
		regionBorder.add(new Location(38.551, -122.904));
		regionBorder.add(new Location(38.600, -122.853));
		regionBorder.add(new Location(38.645, -122.798));
		regionBorder.add(new Location(38.688, -122.739));
		regionBorder.add(new Location(38.727, -122.676));
		regionBorder.add(new Location(38.762, -122.610));
		regionBorder.add(new Location(38.794, -122.540));
		regionBorder.add(new Location(38.822, -122.468));
		regionBorder.add(new Location(38.846, -122.394));
		regionBorder.add(new Location(38.866, -122.318));
		regionBorder.add(new Location(38.881, -122.240));
		regionBorder.add(new Location(38.892, -122.160));
		regionBorder.add(new Location(38.899, -122.080));
		Region reg = new Region(regionBorder, BorderType.GREAT_CIRCLE);
		return reg;
	}

}
