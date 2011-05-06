package org.opensha.sha.imr.attenRelImpl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import org.opensha.sha.imr.attenRelImpl.ZhaoEtAl_2006_AttenRel;
import org.opensha.sha.imr.param.IntensityMeasureParams.PGA_Param;
import org.opensha.sha.imr.param.OtherParams.StdDevTypeParam;
import org.opensha.sha.imr.param.SiteParams.Vs30_Param;
import org.opensha.sha.util.TectonicRegionType;

public class ZhaoEtAl_2006_test implements ParameterChangeWarningListener {

	private ZhaoEtAl_2006_AttenRel zhaoEtAlAttenRel = null;

	private static final String M65Dist20Depth10ShallowCrustNormalRockTableFile = "zhao_r20.0_m6.5_dep10.0_shallow_normal_site1.dat";

	private static final String M65Dist20Depth10ShallowCrustReverseRockTableFile = "zhao_r20.0_m6.5_dep10.0_shallow_reverse_site1.dat";

	private static final String M65Dist223Depth20InterfaceTableRockTableFile = "zhao_r22.3_m6.5_dep20.0_interf_site1.dat";
	
	private static final String M65Dist223Depth20InterfaceHardSoilTableFile = "zhao_r22.3_m6.5_dep20.0_interf_site2.dat";
	
	private static final String M65Dist223Depth20InterfaceMediumSoilTableFile = "zhao_r22.3_m6.5_dep20.0_interf_site3.dat";
	
	private static final String M65Dist223Depth20InterfaceSoftSoilTableFile = "zhao_r22.3_m6.5_dep20.0_interf_site4.dat";
	
	private static final String M65Dist223Depth20IntraSlabRockTableFile = "zhao_r22.3_m6.5_dep20.0_slab_site1.dat";
	
	private static final String M5Dist30Depth30InterfaceRockTableFile = "zhao_r30.0_m5.0_dep30_interf_site1.dat";

	/** Number of columns in test tables. */
	private static final int TABLE_NUM_COL = 4;

	/** Number of rows in test table. */
	private static final int TABLE_NUM_ROWS = 21;

	/**
	 * Table for mag=6.5, rupture distance 20 km, depth 10 km, shallow crust
	 * normal event. 1st column: period (s) 2nd column: median ground motion 3rd
	 * column: UNKNOWN (ask Marco P. for that) 4th column: total standard
	 * deviation.
	 */
	private static double[][] M65Dist20Depth10ShallowCrustNormalRockTable = null;

	private static double[][] M65Dist20Depth10ShallowCrustReverseRockTable = null;

	private static double[][] M65Dist223Depth20InterfaceRockTable = null;
	
	private static double[][] M65Dist223Depth20InterfaceHardSoilTable = null;
	
	private static double[][] M65Dist223Depth20InterfaceMediumSoilTable = null;
	
	private static double[][] M65Dist223Depth20InterfaceSoftSoilTable = null;
	
	private static double[][] M65Dist223Depth20IntraSlabRockTable = null;
	
	private static double[][] M5Dist30Depth30InterfaceRockTable = null;

	/**
	 * Tolerance level.
	 */
	private static final double TOLERANCE = 1e-3;

	/**
	 * Set up attenuation relationship object, and tables for tests.
	 */
	@Before
	public final void setUp() throws Exception {
		zhaoEtAlAttenRel = new ZhaoEtAl_2006_AttenRel(this);
		zhaoEtAlAttenRel.setParamDefaults();
		M65Dist20Depth10ShallowCrustNormalRockTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL];
		AttenRelTestHelper.readNumericTable(
				new File(ClassLoader.getSystemResource(
						M65Dist20Depth10ShallowCrustNormalRockTableFile).toURI()),
				M65Dist20Depth10ShallowCrustNormalRockTable);
		M65Dist20Depth10ShallowCrustReverseRockTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL];
		AttenRelTestHelper.readNumericTable(
				new File(ClassLoader.getSystemResource(
						M65Dist20Depth10ShallowCrustReverseRockTableFile).toURI()),
				M65Dist20Depth10ShallowCrustReverseRockTable);
		M65Dist223Depth20InterfaceRockTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL];
		AttenRelTestHelper.readNumericTable(
				new File(ClassLoader.getSystemResource(
						M65Dist223Depth20InterfaceTableRockTableFile).toURI()),
				M65Dist223Depth20InterfaceRockTable);
		M65Dist223Depth20InterfaceHardSoilTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL];
		AttenRelTestHelper.readNumericTable(
				new File(ClassLoader.getSystemResource(
						M65Dist223Depth20InterfaceHardSoilTableFile).toURI()),
				M65Dist223Depth20InterfaceHardSoilTable);
		M65Dist223Depth20InterfaceMediumSoilTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL];
		AttenRelTestHelper.readNumericTable(
				new File(ClassLoader.getSystemResource(
						M65Dist223Depth20InterfaceMediumSoilTableFile).toURI()),
						M65Dist223Depth20InterfaceMediumSoilTable);
		M65Dist223Depth20InterfaceSoftSoilTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL];
		AttenRelTestHelper.readNumericTable(
				new File(ClassLoader.getSystemResource(
						M65Dist223Depth20InterfaceSoftSoilTableFile).toURI()),
						M65Dist223Depth20InterfaceSoftSoilTable);
		M65Dist223Depth20IntraSlabRockTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL];
		AttenRelTestHelper.readNumericTable(
				new File(ClassLoader.getSystemResource(
						M65Dist223Depth20IntraSlabRockTableFile).toURI()),
						M65Dist223Depth20IntraSlabRockTable);
		M5Dist30Depth30InterfaceRockTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL];
		AttenRelTestHelper.readNumericTable(
				new File(ClassLoader.getSystemResource(
						M5Dist30Depth30InterfaceRockTableFile).toURI()),
						M5Dist30Depth30InterfaceRockTable);
	}

	/**
	 * Clean up.
	 */
	@After
	public final void tearDown() {
		zhaoEtAlAttenRel = null;
		M65Dist20Depth10ShallowCrustNormalRockTable = null;
		M65Dist20Depth10ShallowCrustReverseRockTable = null;
		M65Dist223Depth20InterfaceRockTable = null;
		M65Dist223Depth20InterfaceHardSoilTable = null;
		M65Dist223Depth20InterfaceMediumSoilTable = null;
		M65Dist223Depth20InterfaceSoftSoilTable = null;
		M65Dist223Depth20IntraSlabRockTable = null;
		M5Dist30Depth30InterfaceRockTable = null;
	}

	@Test
	public void m65Dist20Depth10ShallowCrustNormalRock() {
		double mag = 6.5;
		double rRup = 20.0;
		double hypodepth = 10.0;
		double rake = -90.0;
		double vs30 = 760.0;
		String tectRegType = TectonicRegionType.ACTIVE_SHALLOW.toString();
		double[][] verificationTable = M65Dist20Depth10ShallowCrustNormalRockTable;
		compareMeanAndStd(mag, rRup, hypodepth, rake, vs30, tectRegType,
				verificationTable);
	}

	@Test
	public void m65Dist20Depth10ShallowCrustReverseRock() {
		double mag = 6.5;
		double rRup = 20.0;
		double hypodepth = 10.0;
		double rake = +90.0;
		double vs30 = 760.0;
		String tectRegType = TectonicRegionType.ACTIVE_SHALLOW.toString();
		double[][] verificationTable = M65Dist20Depth10ShallowCrustReverseRockTable;
		compareMeanAndStd(mag, rRup, hypodepth, rake, vs30, tectRegType,
				verificationTable);
	}

	@Test
	public void m65Dist223Depth20InterfaceRock() {
		double mag = 6.5;
		double rRup = 22.3;
		double hypodepth = 20.0;
		double rake = +90.0;
		double vs30 = 760.0;
		String tectRegType = TectonicRegionType.SUBDUCTION_INTERFACE.toString();
		double[][] verificationTable = M65Dist223Depth20InterfaceRockTable;
		compareMeanAndStd(mag, rRup, hypodepth, rake, vs30, tectRegType,
				verificationTable);
	}
	
	@Test
	public void m65Dist223Depth20InterfaceHardSoil() {
		double mag = 6.5;
		double rRup = 22.3;
		double hypodepth = 20.0;
		double rake = +90.0;
		double vs30 = 500.0;
		String tectRegType = TectonicRegionType.SUBDUCTION_INTERFACE.toString();
		double[][] verificationTable = M65Dist223Depth20InterfaceHardSoilTable;
		compareMeanAndStd(mag, rRup, hypodepth, rake, vs30, tectRegType,
				verificationTable);
	}

	@Test
	public void m65Dist223Depth20InterfaceMediumSoil() {
		double mag = 6.5;
		double rRup = 22.3;
		double hypodepth = 20.0;
		double rake = +90.0;
		double vs30 = 250.0;
		String tectRegType = TectonicRegionType.SUBDUCTION_INTERFACE.toString();
		double[][] verificationTable = M65Dist223Depth20InterfaceMediumSoilTable;
		compareMeanAndStd(mag, rRup, hypodepth, rake, vs30, tectRegType,
				verificationTable);
	}
	
	@Test
	public void m65Dist223Depth20InterfaceSoftSoil() {
		double mag = 6.5;
		double rRup = 22.3;
		double hypodepth = 20.0;
		double rake = +90.0;
		double vs30 = 100.0;
		String tectRegType = TectonicRegionType.SUBDUCTION_INTERFACE.toString();
		double[][] verificationTable = M65Dist223Depth20InterfaceSoftSoilTable;
		compareMeanAndStd(mag, rRup, hypodepth, rake, vs30, tectRegType,
				verificationTable);
	}
	
	@Test
	public void m65Dist223Depth20IntraSlabRock() {
		double mag = 6.5;
		double rRup = 22.3;
		double hypodepth = 20.0;
		double rake = +90.0;
		double vs30 = 760.0;
		String tectRegType = TectonicRegionType.SUBDUCTION_SLAB.toString();
		double[][] verificationTable = M65Dist223Depth20IntraSlabRockTable;
		compareMeanAndStd(mag, rRup, hypodepth, rake, vs30, tectRegType,
				verificationTable);
	}

	@Test
	public void m5Dist30Depth30InterfaceRock() {
		double mag = 5.0;
		double rRup = 30.0;
		double hypodepth = 30.0;
		double rake = +90.0;
		double vs30 = 760.0;
		String tectRegType = TectonicRegionType.SUBDUCTION_INTERFACE.toString();
		double[][] verificationTable = M5Dist30Depth30InterfaceRockTable;
		compareMeanAndStd(mag, rRup, hypodepth, rake, vs30, tectRegType,
				verificationTable);
	}

	/**
	 * Check ZhaoEtAl_2006_AttenRel usage for computing hazard curves using
	 * GEM1ERF constructed from area source data for intraslab events. Ruptures
	 * are treated as points.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void zhaoEtAl2006WithGEM1ERFPointRuptures() throws Exception{
		zhaoEtAlAttenRel.setIntensityMeasure(PGA_Param.NAME);
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
		hazCurveCalculator.getHazardCurve(hazCurve, site, zhaoEtAlAttenRel,
				erf);
	}
	
	/**
	 * Check ZhaoEtAl_2006_AttenRel usage for computing hazard curves using
	 * GEM1ERF constructed from area source data for intraslab events. Ruptures
	 * are treated as lines.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void zhaoEtAl2006WithGEM1ERFLineRuptures() throws Exception{
		zhaoEtAlAttenRel.setIntensityMeasure(PGA_Param.NAME);
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
		hazCurveCalculator.getHazardCurve(hazCurve, site, zhaoEtAlAttenRel,
				erf);
	}
	
	/**
	 * Check ZhaoEtAl_2006_AttenRel usage for computing hazard curves using GEM1ERF constructed
	 * from simple fault source data for interface events.
	 * @throws Exception 
	 */
	@Test
	public final void zhaoEtAl2006WithGEM1ERFInterfaceSimpleFault() throws Exception{
		zhaoEtAlAttenRel.setIntensityMeasure(PGA_Param.NAME);
		ArrayList<GEMSourceData> srcDataList = new ArrayList<GEMSourceData>();
		srcDataList.
		add(AttenRelTestHelper.getSubductionInterfaceSimpleFaultData());
		double timeSpan = 50.0;
		GEM1ERF erf = GEM1ERF.getGEM1ERF(srcDataList, timeSpan);
		HazardCurveCalculator hazCurveCalculator = new HazardCurveCalculator();
		ArbitrarilyDiscretizedFunc hazCurve = AttenRelTestHelper.setUpHazardCurve();
		Site site = new Site(new Location(-1.515,-81.456));
		site.addParameter(new DoubleParameter(Vs30_Param.NAME, 800.0));
		hazCurveCalculator.getHazardCurve(hazCurve, site, zhaoEtAlAttenRel,
				erf);
	}
	
	/**
	 * Check ZhaoEtAl_2006_AttenRel usage for computing hazard curves using GEM1ERF constructed
	 * from area source data for shallow crust events.
	 * @throws Exception 
	 */
	@Test
	public final void zhaoEtAl2006WithGEM1ERFShallowCrustAreaSource() throws Exception{
		zhaoEtAlAttenRel.setIntensityMeasure(PGA_Param.NAME);
		ArrayList<GEMSourceData> srcDataList = new ArrayList<GEMSourceData>();
		srcDataList.
		add(AttenRelTestHelper.getActiveCrustAreaSourceData());
		double timeSpan = 50.0;
		GEM1ERF erf = GEM1ERF.getGEM1ERF(srcDataList, timeSpan);
		HazardCurveCalculator hazCurveCalculator = new HazardCurveCalculator();
		ArbitrarilyDiscretizedFunc hazCurve = AttenRelTestHelper.setUpHazardCurve();
		Site site = new Site(new Location(-3.78,-81.18));
		site.addParameter(new DoubleParameter(Vs30_Param.NAME, 800.0));
		hazCurveCalculator.getHazardCurve(hazCurve, site, zhaoEtAlAttenRel,
				erf);
	}
	

	private void compareMeanAndStd(double mag, double rRup, double hypodepth,
			double rake, double vs30, String tectRegType,
			double[][] verificationTable) {
		for (int i = 0; i < verificationTable.length; i++) {
			int periodIndex = i;
			double computedMedian = Math
					.exp(zhaoEtAlAttenRel.getMean(periodIndex, mag, rRup,
							hypodepth, rake, vs30, tectRegType));
			double expectedMedian = verificationTable[i][1];
			double computedTotalStdDev = zhaoEtAlAttenRel.getStdDev(
					periodIndex, StdDevTypeParam.STD_DEV_TYPE_TOTAL.toString(),
					tectRegType);
			double expectedTotalStdDev = verificationTable[i][3];
			assertEquals(computedTotalStdDev, expectedTotalStdDev, TOLERANCE);
			assertEquals(computedMedian, expectedMedian, TOLERANCE);
		}
	}

	@Override
	public void parameterChangeWarning(ParameterChangeWarningEvent event) {
	}

}
