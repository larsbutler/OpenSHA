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
import org.opensha.sha.imr.attenRelImpl.FS_2011_AttenRel;
import org.opensha.sha.imr.param.IntensityMeasureParams.IA_Param;
import org.opensha.sha.imr.param.SiteParams.Vs30_Param;

/**
 * Class providing methods for testing {@link FS_2011_AttenRel}. Tables
 * created from StaffordGMPEs.jar provided by Peter J. Stafford.
 */
public class FS_2011_test implements ParameterChangeWarningListener {

	/** Foulser-Piggott & Stafford (2011) GMPE (attenuation relationship) */
	private FS_2011_AttenRel FS2011AttenRel = null;

	/**
	 * Table for median ground motion validation. M4.5 to M8 at R=10km. 
	 */
	private static final String MEDIAN_M45_M8_R10_TABLE = "foulserpiggottstafford2011_M45-M8_R10.out";

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
		FS2011AttenRel = new FS_2011_AttenRel(this);
		FS2011AttenRel.setParamDefaults();

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
		FS2011AttenRel = null;
		medianTable = null;
	}

	@Test
	public void medianTable() {
		double vs30 = 250.0;
		double rake = 0;
		validateMedian(vs30, rake, medianTable);
		validateStdDev(vs30, rake, medianTable);
	}

	/**
	 * Check FS_2011 usage for computing hazard curves using GEM1ERF constructed
	 * from area source data. Ruptures are treated as
	 * points.
	 * @throws Exception
	 */
	@Test
	public final void GEM1ERFPointRuptures() throws Exception{
		FS2011AttenRel.setIntensityMeasure(IA_Param.NAME);
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
		hazCurveCalculator.getHazardCurve(hazCurve, site, FS2011AttenRel,
				erf);
	}

	/**
	 * Check FS_2011 usage for computing hazard curves using GEM1ERF constructed
	 * from area source data. Ruptures are treated as
	 * extended.
	 * @throws Exception
	 */
	@Test
	public final void GEM1ERFLineRuptures() throws Exception{
		FS2011AttenRel.setIntensityMeasure(IA_Param.NAME);
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
		hazCurveCalculator.getHazardCurve(hazCurve, site, FS2011AttenRel,
				erf);
	}

	/**
	 * Check FS_2011 usage for computing hazard curves using GEM1ERF constructed
	 * from simple fault source data.
	 * @throws Exception
	 */
	@Test
	public final void GEM1ERFSimpleFault() throws Exception{
		FS2011AttenRel.setIntensityMeasure(IA_Param.NAME);
		ArrayList<GEMSourceData> srcDataList = new ArrayList<GEMSourceData>();
		srcDataList.
		add(AttenRelTestHelper.getActiveCrustSimpleFaultSourceData());
		double timeSpan = 50.0;
		GEM1ERF erf = GEM1ERF.getGEM1ERF(srcDataList, timeSpan);
		HazardCurveCalculator hazCurveCalculator = new HazardCurveCalculator();
		ArbitrarilyDiscretizedFunc hazCurve = AttenRelTestHelper.setUpHazardCurve();
		Site site = new Site(new Location(40.2317, 15.8577));
		site.addParameter(new DoubleParameter(Vs30_Param.NAME, 800.0));
		hazCurveCalculator.getHazardCurve(hazCurve, site, FS2011AttenRel,
				erf);
	}

	private void validateMedian(double vs30, double rake, double[][] table) {
		// check for relative significant duration
		for (int j = 0; j < table.length; j++) {
			double mag=4.5+j*0.5;
			double rrup = 10;
			double expectedMedian = table[j][1];
			double computedMedian = Math.exp(FS2011AttenRel.getMean(mag,rrup, vs30,rake));
			System.out.println("IA");
			assertEquals(expectedMedian, computedMedian, TOLERANCE);
		}
	}

	private void validateStdDev(double vs30, double rake,double[][] table) {
		// check for standard deviations
		for (int j = 0; j < table.length; j++) {
			double mag=4.5+j*0.5;
			double rrup = 10;
			double expectedStd_Inter = table[j][3];
			double computedStd_Inter = FS2011AttenRel.getStdDev(mag,rrup,vs30,rake,"Inter-Event");
			double expectedStd_Intra = table[j][4];
			double computedStd_Intra = FS2011AttenRel.getStdDev(mag,rrup,vs30,rake,"Intra-Event");
			double expectedStd_Total = table[j][5];
			double computedStd_Total = FS2011AttenRel.getStdDev(mag,rrup,vs30,rake,"Total");
			System.out.println("IA");	
			assertEquals(expectedStd_Inter, computedStd_Inter, TOLERANCE);
			assertEquals(expectedStd_Intra, computedStd_Intra, TOLERANCE);
			assertEquals(expectedStd_Total, computedStd_Total, TOLERANCE);
		}
	}

	@Override
	public void parameterChangeWarning(ParameterChangeWarningEvent event) {
	}
}

