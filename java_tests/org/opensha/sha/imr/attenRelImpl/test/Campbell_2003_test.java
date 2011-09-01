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
import org.opensha.sha.imr.attenRelImpl.CF_2008Constants;
import org.opensha.sha.imr.attenRelImpl.CF_2008_AttenRel;
import org.opensha.sha.imr.attenRelImpl.Campbell2003_AttenRel;
import org.opensha.sha.imr.param.IntensityMeasureParams.PGA_Param;
import org.opensha.sha.imr.param.OtherParams.StdDevTypeParam;
import org.opensha.sha.imr.param.SiteParams.Vs30_Param;

/**
 * Class providing methods for testing {@link Campbell_2003_AttenRel}. Tables
 * provided by the original authors.
 */
public class Campbell_2003_test implements ParameterChangeWarningListener {

	/** Campbell_2003_AttenRel GMPE (attenuation relationship) */
	private Campbell2003_AttenRel ca03AttenRel = null;

	/**
	 * Table for total standard deviation validation.
	 */
	private static final String SIGMA_TOTAL_HARD_ROCK_TABLE = "Ca03_SIGMcorr.txt";

	/**
	 * Table for median ground motion validation. Hard rock median.
	 */
	private static final String MEDIAN_HARD_ROCK_TABLE = "Ca03_MEDIAN.OUT";
	

	/** Header for meadian tables. */
	private static String[] TABLE_HEADER_MEDIAN = new String[1];
	
	/** Header for standard deviation tables. */
	private static String[] TABLE_HEADER_STD = new String[1];

	/** Number of columns in test tables for standard deviation. */
	private static final int TABLE_NUM_COL_STD = 20;

	/** Number of columns in test tables for median ground motion value. */
	private static final int TABLE_NUM_COL_MEDIAN = 20;

	/** Number of rows in interface test table. */
	private static final int TABLE_NUM_ROWS = 63;

	/** Inter event standard deviation verification table. */
	private static double[][] stdTotalTable = null;

	/** Median ground motion verification table. Normal event on rock. */
	private static double[][] medianHardRockTable = null;

	private static final double TOLERANCE = 1e-3;

	/**
	 * Set up attenuation relationship object, and tables for tests.
	 * 
	 * @throws Exception
	 */
	@Before
	public final void setUp() throws Exception {
		ca03AttenRel = new Campbell2003_AttenRel(this);
		ca03AttenRel.setParamDefaults();

		stdTotalTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL_STD];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(SIGMA_TOTAL_HARD_ROCK_TABLE).toURI()),
				stdTotalTable, TABLE_HEADER_STD);
		medianHardRockTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL_MEDIAN];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(MEDIAN_HARD_ROCK_TABLE).toURI()),
				medianHardRockTable, TABLE_HEADER_MEDIAN);

	}

	/**
	 * Clean up.
	 */
	@After
	public final void tearDown() {
		ca03AttenRel = null;
		stdTotalTable = null;
		medianHardRockTable = null;
	}

	@Test
	public void checkStdTotal() {
		validateStdDev(StdDevTypeParam.STD_DEV_TYPE_TOTAL_MAG_DEP, stdTotalTable);
	}

	@Test
	public void checkMedianEventOnHardRock() {
		double vs30 = 800.0;
		double rake = -90.0;
		validateMedian(vs30, rake, medianHardRockTable);
	}
	

	/**
	 * Check AkB_2010 usage for computing hazard curves using GEM1ERF constructed
	 * from area source data. Ruptures are treated as
	 * points.
	 * @throws Exception 
	 */
	@Test
	public final void GEM1ERFPointRuptures() throws Exception{
		ca03AttenRel.setIntensityMeasure(PGA_Param.NAME);
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
		hazCurveCalculator.getHazardCurve(hazCurve, site, ca03AttenRel,
				erf);
	}
	
	/**
	 * Check Campbell_2003 usage for computing hazard curves using GEM1ERF constructed
	 * from area source data. Ruptures are treated as
	 * extended.
	 * @throws Exception 
	 */
	@Test
	public final void GEM1ERFLineRuptures() throws Exception{
		ca03AttenRel.setIntensityMeasure(PGA_Param.NAME);
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
		hazCurveCalculator.getHazardCurve(hazCurve, site, ca03AttenRel,
				erf);
	}
	
	/**
	 * Check AB2003 usage for computing hazard curves using GEM1ERF constructed
	 * from simple fault source data.
	 * @throws Exception 
	 */
	@Test
	public final void GEM1ERFSimpleFault() throws Exception{
		ca03AttenRel.setIntensityMeasure(PGA_Param.NAME);
		ArrayList<GEMSourceData> srcDataList = new ArrayList<GEMSourceData>();
		srcDataList.
		add(AttenRelTestHelper.getActiveCrustSimpleFaultSourceData());
		double timeSpan = 50.0;
		GEM1ERF erf = GEM1ERF.getGEM1ERF(srcDataList, timeSpan);
		HazardCurveCalculator hazCurveCalculator = new HazardCurveCalculator();
		ArbitrarilyDiscretizedFunc hazCurve = AttenRelTestHelper.setUpHazardCurve();
		Site site = new Site(new Location(40.2317, 15.8577));
		site.addParameter(new DoubleParameter(Vs30_Param.NAME, 800.0));
		hazCurveCalculator.getHazardCurve(hazCurve, site, ca03AttenRel,
				erf);
	}

	private void validateMedian(double vs30, double rake, double[][] table) {
		String[] columnDescr = TABLE_HEADER_MEDIAN[0].trim().split("\\s+");
		// check for SA
		for (int i = 3; i < columnDescr.length - 2; i++) {
			for (int j = 0; j < table.length; j++) {
				int iper = i-1;
				double mag = table[j][0];
				double rJB = table[j][1];
				double expectedMedian = table[j][i];
				System.out.println(expectedMedian);
				double computedMedian = Math.exp(ca03AttenRel.getMean(iper,mag, rJB, vs30, rake));
				System.out.println(computedMedian);
				System.out.println(mag);
				System.out.println(rJB);
				assertEquals(expectedMedian, computedMedian, TOLERANCE);
			}
		}
		// check for PGA
		for (int j = 0; j < table.length; j++) {
			double mag = table[j][0];
			double rJB = table[j][1];
			double expectedMedian = table[j][columnDescr.length - 2];
			double computedMedian = Math.exp(ca03AttenRel.getMean(1, mag,
					rJB, vs30, rake));
			System.out.println("PGA");
			assertEquals(expectedMedian, computedMedian, TOLERANCE);
		}
		// check for PGV
		for (int j = 0; j < table.length; j++) {
			double mag = table[j][0];
			double rJB = table[j][1];
			double expectedMedian = table[j][columnDescr.length - 1];
			double computedMedian = Math.exp(ca03AttenRel.getMean(0, mag,
					rJB, vs30, rake));
			System.out.println("PGV");
			assertEquals(expectedMedian, computedMedian, TOLERANCE);
		}
	}

	private void validateStdDev(String stdDevType, double[][] table) {
		String[] columnDescr = TABLE_HEADER_STD[0].trim().split("\\s+");
		// check for SA
		for (int i = 3; i < columnDescr.length - 2; i++) {
			for (int j = 0; j < table.length; j++) {
				double mag = table[j][0];
				double expectedStd = table[j][i];
				double computedStd = ca03AttenRel.getStdDev(i-1, mag, stdDevType);
				assertEquals(expectedStd, computedStd, TOLERANCE);
			}
		}
		// check for PGA
		for (int j = 0; j < table.length; j++) {
			double mag = table[j][0];
			double expectedStd = table[j][columnDescr.length - 2];
			double computedStd = ca03AttenRel.getStdDev(1, mag, stdDevType);
			assertEquals(expectedStd, computedStd, TOLERANCE);
		}
		// check for PGV
		for (int j = 0; j < table.length; j++) {
			double mag = table[j][0];
			double expectedStd = table[j][columnDescr.length - 1];
			double computedStd = ca03AttenRel.getStdDev(0, mag, stdDevType);

			assertEquals(expectedStd, computedStd, TOLERANCE);
		}
	}

	@Override
	public void parameterChangeWarning(ParameterChangeWarningEvent event) {
	}
}
