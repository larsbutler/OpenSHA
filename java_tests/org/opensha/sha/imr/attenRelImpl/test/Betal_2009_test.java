package org.opensha.sha.imr.attenRelImpl.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opensha.commons.data.Site;
import org.opensha.commons.data.function.ArbitrarilyDiscretizedFunc;
import org.opensha.commons.geo.Location;
import org.opensha.commons.param.DoubleParameter;
import org.opensha.commons.param.event.ParameterChangeWarningEvent;
import org.opensha.commons.param.event.ParameterChangeWarningListener;
import org.opensha.sha.calc.HazardCurveCalculator;
import org.opensha.sha.earthquake.rupForecastImpl.GEM1.GEM1ERF;
import org.opensha.sha.earthquake.rupForecastImpl.GEM1.SourceData.GEMSourceData;
import org.opensha.sha.imr.attenRelImpl.Betal_2009_AttenRel;
import org.opensha.sha.imr.param.IntensityMeasureParams.RelativeSignificantDuration_Param;
import org.opensha.sha.imr.param.SiteParams.Vs30_Param;

/**
 * Class providing methods for testing {@link Betal_2009_AttenRel}. Tables
 * created from StaffordGMPEs.jar provided by Peter J. Stafford.
 */
public class Betal_2009_test implements ParameterChangeWarningListener {

	/** Bommer et al. (2009) GMPE (attenuation relationship) */
	private Betal_2009_AttenRel Betal2009AttenRel = null;

	/**
	 * Table for median ground motion validation. M4.5 to M8 at R=10km. 
	 */
	private static final String MEDIAN_M45_M8_R10_TABLE = "bommeretal2009_M45-M8_R10.out";

	/** Header for median tables. */
	private static String[] TABLE_HEADER_MEDIAN = new String[1];

	/** Number of columns in test tables for median ground motion value. */
	private static final int TABLE_NUM_COL_MEDIAN = 6;

	/** Number of rows in first test table. */
	private static final int TABLE_NUM_ROWS = 8;

	/** Median ground motion validation table. M4.5 to M8 at R=10km. */
	private static double[][] medianTable = null;

	private static final double TOLERANCE = 1e-3;

	/**
	 * Set up attenuation relationship object, and tables for tests.
	 *
	 * @throws Exception
	 */
	@Before
	public final void setUp() throws Exception {
		Betal2009AttenRel = new Betal_2009_AttenRel(this);
		Betal2009AttenRel.setParamDefaults();

		medianTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL_MEDIAN];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(MEDIAN_M45_M8_R10_TABLE).toURI()),
				medianTable, TABLE_HEADER_MEDIAN);
	}

	/**
	 * Clean up.
	 */
	@After
	public final void tearDown() {
		Betal2009AttenRel = null;
		medianTable = null;
	}

	@Test
	public void medianTable() {
		double vs30 = 250.0;
		double ztor = 1;
		validateMedian(vs30, ztor, medianTable);
		validateStdDev(medianTable);
	}

	/**
	 * Check Betal_2009 usage for computing hazard curves using GEM1ERF constructed
	 * from area source data. Ruptures are treated as
	 * points.
	 * @throws Exception
	 */
	@Test
	public final void GEM1ERFPointRuptures() throws Exception{
		Betal2009AttenRel.setIntensityMeasure(RelativeSignificantDuration_Param.NAME);
		ArrayList<GEMSourceData> srcDataList = new ArrayList<GEMSourceData>();
		srcDataList.
		add(AttenRelTestHelper.getActiveCrustAreaSourceData());
		double timeSpan = 50.0;
		GEM1ERF erf = GEM1ERF.getGEM1ERF(srcDataList, timeSpan);
		erf.setParameter(GEM1ERF.AREA_SRC_RUP_TYPE_NAME,
				GEM1ERF.AREA_SRC_RUP_TYPE_POINT);
		erf.updateForecast();
		HazardCurveCalculator hazCurveCalculator = new HazardCurveCalculator();
		ArbitrarilyDiscretizedFunc hazCurve = AttenRelTestHelper.setUpHazardCurve();
		Site site = new Site(new Location(-0.171,-75.555));
		site.addParameter(new DoubleParameter(Vs30_Param.NAME, 800.0));
		hazCurveCalculator.getHazardCurve(hazCurve, site, Betal2009AttenRel,
				erf);
	}

	/**
	 * Check Betal_2009 usage for computing hazard curves using GEM1ERF constructed
	 * from area source data. Ruptures are treated as
	 * extended.
	 * @throws Exception
	 */
	@Test
	public final void GEM1ERFLineRuptures() throws Exception{
		Betal2009AttenRel.setIntensityMeasure(RelativeSignificantDuration_Param.NAME);
		ArrayList<GEMSourceData> srcDataList = new ArrayList<GEMSourceData>();
		srcDataList.
		add(AttenRelTestHelper.getActiveCrustAreaSourceData());
		double timeSpan = 50.0;
		GEM1ERF erf = GEM1ERF.getGEM1ERF(srcDataList, timeSpan);
		erf.setParameter(GEM1ERF.AREA_SRC_RUP_TYPE_NAME,
				GEM1ERF.AREA_SRC_RUP_TYPE_LINE);
		erf.updateForecast();
		HazardCurveCalculator hazCurveCalculator = new HazardCurveCalculator();
		ArbitrarilyDiscretizedFunc hazCurve = AttenRelTestHelper.setUpHazardCurve();
		Site site = new Site(new Location(-0.171,-75.555));
		site.addParameter(new DoubleParameter(Vs30_Param.NAME, 800.0));
		hazCurveCalculator.getHazardCurve(hazCurve, site, Betal2009AttenRel,
				erf);
	}

	/**
	 * Check Betal_2009 usage for computing hazard curves using GEM1ERF constructed
	 * from simple fault source data.
	 * @throws Exception
	 */
	@Test
	public final void GEM1ERFSimpleFault() throws Exception{
		Betal2009AttenRel.setIntensityMeasure(RelativeSignificantDuration_Param.NAME);
		ArrayList<GEMSourceData> srcDataList = new ArrayList<GEMSourceData>();
		srcDataList.
		add(AttenRelTestHelper.getActiveCrustSimpleFaultSourceData());
		double timeSpan = 50.0;
		GEM1ERF erf = GEM1ERF.getGEM1ERF(srcDataList, timeSpan);
		HazardCurveCalculator hazCurveCalculator = new HazardCurveCalculator();
		ArbitrarilyDiscretizedFunc hazCurve = AttenRelTestHelper.setUpHazardCurve();
		Site site = new Site(new Location(40.2317, 15.8577));
		site.addParameter(new DoubleParameter(Vs30_Param.NAME, 800.0));
		hazCurveCalculator.getHazardCurve(hazCurve, site, Betal2009AttenRel,
				erf);
	}

	private void validateMedian(double vs30, double ztor, double[][] table) {
		// check for relative significant duration
		for (int j = 0; j < table.length; j++) {
			double mag=4.5+j*0.5;
			double rrup = 10;
			double expectedMedian = table[j][1];
			double computedMedian = Math.exp(Betal2009AttenRel.getMean(mag,rrup, vs30,ztor));
			System.out.println("RSD");
			assertEquals(expectedMedian, computedMedian, TOLERANCE);
		}
	}

	private void validateStdDev(double[][] table) {
		// check for standard deviations
		for (int j = 0; j < table.length; j++) {
			double expectedStd_Inter = table[j][3];
			double computedStd_Inter = Betal2009AttenRel.getStdDev("Inter-Event");
			double expectedStd_Intra = table[j][4];
			double computedStd_Intra = Betal2009AttenRel.getStdDev("Intra-Event");
			double expectedStd_Total = table[j][5];
			double computedStd_Total = Betal2009AttenRel.getStdDev("Total");
			System.out.println("RSD");	
			assertEquals(expectedStd_Inter, computedStd_Inter, TOLERANCE);
			assertEquals(expectedStd_Intra, computedStd_Intra, TOLERANCE);
			assertEquals(expectedStd_Total, computedStd_Total, TOLERANCE);
		}
	}

	@Override
	public void parameterChangeWarning(ParameterChangeWarningEvent event) {
	}
}

