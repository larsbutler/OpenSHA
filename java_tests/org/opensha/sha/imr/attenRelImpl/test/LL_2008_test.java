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
import org.opensha.sha.imr.attenRelImpl.LL_2008_AttenRel;
import org.opensha.sha.imr.param.IntensityMeasureParams.PGA_Param;
import org.opensha.sha.imr.param.OtherParams.StdDevTypeParam;
import org.opensha.sha.imr.param.SiteParams.Vs30_Param;
import org.opensha.sha.util.TectonicRegionType;

public class LL_2008_test implements ParameterChangeWarningListener {

	/** LL_2008_AttenRel GMPE (attenuation relationship) */
	private LL_2008_AttenRel ll08AttenRel = null;
	/**
	 * Table for total standard deviation validation.
	 */
	private static final String SIGMA_TOTAL_ROCK_TABLE = "LL08_SIGMA_ROCK.OUT";
	private static final String SIGMA_TOTAL_SOFT_TABLE = "LL08_SIGMA_SOIL.OUT";

	/**
	 * Table for median ground motion validation. Inslab events - median values for rock and soft soil
	 */
	private static final String MEDIAN_INSLAB_ROCK_TABLE = "LL08_MEDIAN_INSLAB_ROCK.OUT";
	private static final String MEDIAN_INSLAB_SOFT_TABLE = "LL08_MEDIAN_INSLAB_SOIL.DAT";

	/**
	 * Table for median ground motion validation. Interface events - median values for rock and soft soil
	 */
	private static final String MEDIAN_INTERFACE_ROCK_TABLE = "LL08_MEDIAN_INTERFACE_ROCK.OUT";
	private static final String MEDIAN_INTERFACE_SOFT_TABLE = "LL08_MEDIAN_INTERFACE_SOIL.TXT";
	
	/** Header for standard deviation tables. */
	private static String[] TABLE_HEADER_STD = new String[1];
	
	/** Header for meadian tables. */
	private static String[] TABLE_HEADER_MEDIAN = new String[1];
	
	/** Number of columns in test tables for standard deviation. */
	private static final int TABLE_NUM_COL_STD = 31;

	/** Number of columns in test tables. */
	private static final int TABLE_NUM_COL = 31;

	/** Number of rows in test table. */
	private static final int TABLE_NUM_ROWS = 81;

	/** Total standard deviation verification table. */
	private static double[][] stdRockTable = null;
	private static double[][] stdSoftTable = null;

	/** Median ground motion verification table. Normal event on rock. */
	private static double[][] medianInslabRockTable = null;
	private static double[][] medianInslabSoftTable = null;
	private static double[][] medianInterfaceRockTable = null;
	private static double[][] medianInterfaceSoftTable = null;

	private static final double TOLERANCE = 1e-2;
	private static final double TOLERANCE_pgv = 1e-2;


	/**
	 * Set up attenuation relationship object, and tables for tests.
	 */
	@Before
	public final void setUp() throws Exception {
		
		ll08AttenRel = new LL_2008_AttenRel(this);
		ll08AttenRel.setParamDefaults();
		
		stdRockTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL_STD];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(SIGMA_TOTAL_ROCK_TABLE).toURI()),
				stdRockTable, TABLE_HEADER_STD);
		
		stdSoftTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL_STD];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(SIGMA_TOTAL_SOFT_TABLE).toURI()),
				stdSoftTable, TABLE_HEADER_STD);
		
		medianInslabRockTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL];
		AttenRelTestHelper.readNumericTableWithHeader(
				new File(ClassLoader.getSystemResource(
						MEDIAN_INSLAB_ROCK_TABLE).toURI()),
						medianInslabRockTable, TABLE_HEADER_MEDIAN);
		
		medianInslabSoftTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL];
		AttenRelTestHelper.readNumericTableWithHeader(
				new File(ClassLoader.getSystemResource(
						MEDIAN_INSLAB_SOFT_TABLE).toURI()),
						medianInslabSoftTable, TABLE_HEADER_MEDIAN);
		
		medianInterfaceRockTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL];
		AttenRelTestHelper.readNumericTableWithHeader(
				new File(ClassLoader.getSystemResource(
						MEDIAN_INTERFACE_ROCK_TABLE).toURI()),
						medianInterfaceRockTable, TABLE_HEADER_MEDIAN);
		
		medianInterfaceSoftTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL];
		AttenRelTestHelper.readNumericTableWithHeader(
				new File(ClassLoader.getSystemResource(
						MEDIAN_INTERFACE_SOFT_TABLE).toURI()),
						medianInterfaceSoftTable, TABLE_HEADER_MEDIAN);
	}

	/**
	 * Clean up.
	 */
	@After
	public final void tearDown() {
		ll08AttenRel = null;
		stdRockTable = null;
		stdSoftTable = null;
		medianInslabRockTable = null;
		medianInslabSoftTable = null;
		medianInterfaceRockTable = null;
		medianInterfaceSoftTable = null;

		
	}
	@Test
	public void stdRockTable() {
		double vs30 = 800.0;
		validateStdDev(vs30, StdDevTypeParam.STD_DEV_TYPE_TOTAL, stdRockTable);
	}
	@Test
	public void stdSoftTable() {
		double vs30 = 200.0;
		validateStdDev(vs30, StdDevTypeParam.STD_DEV_TYPE_TOTAL, stdSoftTable);
	}

	@Test
	public void medianInslabRockTable() {
		double hypoDepth = 80.0;
		double vs30 = 800.0;
		String tectRegType = TectonicRegionType.SUBDUCTION_SLAB.toString();
		validateMedian(hypoDepth, vs30,  tectRegType, medianInslabRockTable);
	}
	@Test
	public void medianInslabSoftTable() {
		double hypoDepth = 80.0;
		double vs30 = 300.0;
		String tectRegType = TectonicRegionType.SUBDUCTION_SLAB.toString();
		validateMedian(hypoDepth, vs30,  tectRegType, medianInslabSoftTable);
	}
	@Test
	public void medianInterfaceRockTable() {
		double hypoDepth = 30.0;
		double vs30 = 800.0;
		String tectRegType = TectonicRegionType.SUBDUCTION_INTERFACE.toString();
		validateMedian(hypoDepth, vs30,  tectRegType, medianInterfaceRockTable);
	}
	@Test
	public void medianInterfaceSoftTable() {
		double hypoDepth = 30.0;
		double vs30 = 300.0;
		String tectRegType = TectonicRegionType.SUBDUCTION_INTERFACE.toString();
		validateMedian(hypoDepth, vs30,  tectRegType, medianInterfaceSoftTable);
	}

	
	private void validateMedian(double hypoDepth, double vs30,  String tectRegType, double[][] table) {
		String[] columnDescr = TABLE_HEADER_MEDIAN[0].trim().split("\\s+");
		// check for SA
		for (int i = 3; i < columnDescr.length - 1; i++) {
			for (int j = 0; j < table.length; j++) {
				int iper = i-2;
				double mag = table[j][0];
				double rJB = table[j][1];
				double expectedMedian = table[j][i];
				double computedMedian = Math.exp(ll08AttenRel.getMean(iper, mag, rJB, 
						hypoDepth, vs30, tectRegType));
				System.out.println(iper + " mag = " + mag + "r= "+rJB 
						+ " expected: "+expectedMedian);
				System.out.println(iper + " mag = " + mag + "r= "+rJB
						+ " computed: "+computedMedian);
				assertEquals(expectedMedian, computedMedian, TOLERANCE);

			}
		}
		// check for PGA
		for (int j = 0; j < table.length; j++) {
			double mag = table[j][0];
			double rJB = table[j][1];
			double expectedMedian = table[j][columnDescr.length - 1];
			double computedMedian = Math.exp(ll08AttenRel.getMean(0, mag, rJB, 
					hypoDepth, vs30, tectRegType));
			assertEquals(expectedMedian, computedMedian, TOLERANCE);
		}
	}


	private void validateStdDev(double vs30, String stdDevType, double[][] table) {
		String[] columnDescr = TABLE_HEADER_STD[0].trim().split("\\s+");
		// check for SA
		for (int i = 3; i < columnDescr.length - 2; i++) {
			for (int j = 0; j < table.length; j++) {
				double expectedStd = table[j][i];
				double computedStd = ll08AttenRel.getStdDev(i-2, stdDevType, vs30);
				assertEquals(expectedStd, computedStd, TOLERANCE);
			}
		}
		// check for PGA
		for (int j = 0; j < table.length; j++) {
			double expectedStd = table[j][columnDescr.length - 1];
			double computedStd = ll08AttenRel.getStdDev(0, stdDevType, vs30);

			assertEquals(expectedStd, computedStd, TOLERANCE);
		}
	}
	/**
	 * Check LL_2008_AttenRel usage for computing hazard curves using
	 * GEM1ERF constructed from area source data for intraslab events. Ruptures
	 * are treated as lines.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void ll2008WithGEM1ERFLineRuptures() throws Exception{
		ll08AttenRel.setIntensityMeasure(PGA_Param.NAME);
		ArrayList<GEMSourceData> srcDataList = new ArrayList<GEMSourceData>();
		srcDataList.
		add(AttenRelTestHelper.getSubductionIntraSlabAreaSourceData());
		double timeSpan = 50.0;
		GEM1ERF erf = GEM1ERF.getGEM1ERF(srcDataList, timeSpan);
		erf.setParameter(GEM1ERF.AREA_SRC_RUP_TYPE_NAME,
				GEM1ERF.AREA_SRC_RUP_TYPE_LINE);
		erf.updateForecast();
		HazardCurveCalculator hazCurveCalculator = new HazardCurveCalculator();
		ArbitrarilyDiscretizedFunc hazCurve = AttenRelTestHelper.setUpHazardCurve();;
		Site site = new Site(new Location(-0.171,-75.555));
		site.addParameter(new DoubleParameter(Vs30_Param.NAME, 800.0));
		hazCurveCalculator.getHazardCurve(hazCurve, site, ll08AttenRel,
				erf);
	}
	
	/**
	 * Check LL_2008_AttenRel usage for computing hazard curves using GEM1ERF constructed
	 * from simple fault source data for interface events.
	 * @throws Exception 
	 */
	@Test
	public final void ll2008WithGEM1ERFInterfaceSimpleFault() throws Exception{
		ll08AttenRel.setIntensityMeasure(PGA_Param.NAME);
		ArrayList<GEMSourceData> srcDataList = new ArrayList<GEMSourceData>();
		srcDataList.
		add(AttenRelTestHelper.getSubductionInterfaceSimpleFaultData());
		double timeSpan = 50.0;
		GEM1ERF erf = GEM1ERF.getGEM1ERF(srcDataList, timeSpan);
		HazardCurveCalculator hazCurveCalculator = new HazardCurveCalculator();
		ArbitrarilyDiscretizedFunc hazCurve = AttenRelTestHelper.setUpHazardCurve();
		Site site = new Site(new Location(-1.515,-81.456));
		site.addParameter(new DoubleParameter(Vs30_Param.NAME, 800.0));
		hazCurveCalculator.getHazardCurve(hazCurve, site, ll08AttenRel,
				erf);
	}
	
	/**
	 * Check LL_2008_AttenRel usage for computing hazard curves using
	 * GEM1ERF constructed from area source data for intraslab events. Ruptures
	 * are treated as points.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void ll2008WithGEM1ERFPointRuptures() throws Exception{
		ll08AttenRel.setIntensityMeasure(PGA_Param.NAME);
		ArrayList<GEMSourceData> srcDataList = new ArrayList<GEMSourceData>();
		srcDataList.
		add(AttenRelTestHelper.getSubductionIntraSlabAreaSourceData());
		double timeSpan = 50.0;
		GEM1ERF erf = GEM1ERF.getGEM1ERF(srcDataList, timeSpan);
		erf.setParameter(GEM1ERF.AREA_SRC_RUP_TYPE_NAME,
				GEM1ERF.AREA_SRC_RUP_TYPE_POINT);
		erf.updateForecast();
		HazardCurveCalculator hazCurveCalculator = new HazardCurveCalculator();
		ArbitrarilyDiscretizedFunc hazCurve = AttenRelTestHelper.setUpHazardCurve();;
		Site site = new Site(new Location(-0.171,-75.555));
		site.addParameter(new DoubleParameter(Vs30_Param.NAME, 800.0));
		hazCurveCalculator.getHazardCurve(hazCurve, site, ll08AttenRel,
				erf);
	}

	@Override
	public void parameterChangeWarning(ParameterChangeWarningEvent event) {
	}

}
