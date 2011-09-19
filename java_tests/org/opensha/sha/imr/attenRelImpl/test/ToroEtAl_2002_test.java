package org.opensha.sha.imr.attenRelImpl.test;

import static org.junit.Assert.assertEquals;

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
import org.opensha.sha.imr.attenRelImpl.ToroEtAl2002_AttenRel;
import org.opensha.sha.imr.param.IntensityMeasureParams.PGA_Param;
import org.opensha.sha.imr.param.OtherParams.StdDevTypeParam;
import org.opensha.sha.imr.param.SiteParams.Vs30_Param;

/**
 * Class providing methods for testing {@link ToroEtAl_2002_AttenRel}. Tables
 * provided by the original authors.
 */
public class ToroEtAl_2002_test implements ParameterChangeWarningListener {

	/** ToroEtAl_2002_AttenRel GMPE (attenuation relationship) */
	private ToroEtAl2002_AttenRel toro2002AtenRel = null;

	/**
	 * Table for total standard deviation validation.
	 */
	private static final String SIGMA_TOTAL_HARD_ROCK_TABLE = "Toro02_SIGMAT.txt";

	/**
	 * Table for median ground motion validation. Hard rock median.
	 */
	private static final String MEDIAN_HARD_ROCK_TABLE = "Toro02_MEDIAN.OUT";
	

	/** Header for meadian tables. */
	private static String[] TABLE_HEADER_MEDIAN = new String[1];
	
	/** Header for standard deviation tables. */
	private static String[] TABLE_HEADER_STD = new String[1];

	/** Number of columns in test tables for standard deviation. */
	private static final int TABLE_NUM_COL_STD = 12;

	/** Number of columns in test tables for median ground motion value. */
	private static final int TABLE_NUM_COL_MEDIAN = 12;

	/** Number of rows in interface test table. */
	private static final int TABLE_NUM_ROWS = 70;

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
		toro2002AtenRel = new ToroEtAl2002_AttenRel(this);
		toro2002AtenRel.setParamDefaults();

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
		toro2002AtenRel = null;
		medianHardRockTable = null;
		stdTotalTable = null;
	}

	@Test
	public void checkMedianEventOnHardRock() {
		validateMedian(medianHardRockTable);
	}

	@Test
	public void checkStdTotal() {
		validateStdDev(StdDevTypeParam.STD_DEV_TYPE_TOTAL, stdTotalTable);
	}
	

	/**
	 * Check AkB_2010 usage for computing hazard curves using GEM1ERF constructed
	 * from area source data. Ruptures are treated as
	 * points.
	 * @throws Exception 
	 */
	@Test
	public final void GEM1ERFPointRuptures() throws Exception{
		toro2002AtenRel.setIntensityMeasure(PGA_Param.NAME);
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
		hazCurveCalculator.getHazardCurve(hazCurve, site, toro2002AtenRel,
				erf);
	}
	
	/**
	 * Check ToroEtAl_2002 usage for computing hazard curves using GEM1ERF constructed
	 * from area source data. Ruptures are treated as
	 * extended.
	 * @throws Exception 
	 */
	@Test
	public final void GEM1ERFLineRuptures() throws Exception{
		toro2002AtenRel.setIntensityMeasure(PGA_Param.NAME);
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
		hazCurveCalculator.getHazardCurve(hazCurve, site, toro2002AtenRel,
				erf);
	}
	
	/**
	 * Check AB2003 usage for computing hazard curves using GEM1ERF constructed
	 * from simple fault source data.
	 * @throws Exception 
	 */
	@Test
	public final void GEM1ERFSimpleFault() throws Exception{
		toro2002AtenRel.setIntensityMeasure(PGA_Param.NAME);
		ArrayList<GEMSourceData> srcDataList = new ArrayList<GEMSourceData>();
		srcDataList.
		add(AttenRelTestHelper.getActiveCrustSimpleFaultSourceData());
		double timeSpan = 50.0;
		GEM1ERF erf = GEM1ERF.getGEM1ERF(srcDataList, timeSpan);
		HazardCurveCalculator hazCurveCalculator = new HazardCurveCalculator();
		ArbitrarilyDiscretizedFunc hazCurve = AttenRelTestHelper.setUpHazardCurve();
		Site site = new Site(new Location(40.2317, 15.8577));
		site.addParameter(new DoubleParameter(Vs30_Param.NAME, 800.0));
		hazCurveCalculator.getHazardCurve(hazCurve, site, toro2002AtenRel,
				erf);
	}

	private void validateMedian(double[][] table) {
		String[] columnDescr = TABLE_HEADER_MEDIAN[0].trim().split("\\s+");
		// check for SA
		for (int i = 3; i < columnDescr.length - 1; i++) {
			for (int j = 0; j < table.length; j++) {
				int iper = i-2;
				double mag = table[j][0];
				double rJB = table[j][1];
				double expectedMedian = table[j][i];
				double computedMedian = Math.exp(toro2002AtenRel.getMean(iper, mag, rJB));
//				System.out.println(i);
//				System.out.println(expectedMedian);
//				System.out.println(computedMedian);
				assertEquals(expectedMedian, computedMedian, TOLERANCE);
			}
		}
		// check for PGA
		for (int j = 0; j < table.length; j++) {
			double mag = table[j][0];
			double rJB = table[j][1];
			double expectedMedian = table[j][columnDescr.length - 1];
			double computedMedian = Math.exp(toro2002AtenRel.getMean(0, mag, rJB));
			assertEquals(expectedMedian, computedMedian, TOLERANCE);
		}
	}

	private void validateStdDev(String stdDevType, double[][] table) {
		String[] columnDescr = TABLE_HEADER_STD[0].trim().split("\\s+");
		// check for SA
		for (int i = 3; i < columnDescr.length - 1; i++) {
			for (int j = 0; j < table.length; j++) {
				double mag = table[j][0];
				double rJB = table[j][1];
				double expectedStd = table[j][i];
				double computedStd = toro2002AtenRel.getStdDev(i-2, mag, 
						rJB, stdDevType);
				System.out.println(i + " = "+ j);
				System.out.println(expectedStd);
				System.out.println(mag + " + " + rJB);
				System.out.println(computedStd);
				assertEquals(expectedStd, computedStd, TOLERANCE);
			}
		}
		// check for PGA
		for (int j = 0; j < table.length; j++) {
			double mag = table[j][0];
			double rJB = table[j][1];
			double expectedStd = table[j][columnDescr.length - 1];
			double computedStd = toro2002AtenRel.getStdDev(0, mag, 
					rJB, stdDevType);
			assertEquals(expectedStd, computedStd, TOLERANCE);
		}
	}

	@Override
	public void parameterChangeWarning(ParameterChangeWarningEvent event) {
	}
}
