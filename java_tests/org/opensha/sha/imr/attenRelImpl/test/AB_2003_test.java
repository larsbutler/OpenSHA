package org.opensha.sha.imr.attenRelImpl.test;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opensha.commons.param.event.ParameterChangeWarningEvent;
import org.opensha.commons.param.event.ParameterChangeWarningListener;
import org.opensha.sha.imr.attenRelImpl.AB_2003_AttenRel;
import org.opensha.sha.imr.param.IntensityMeasureParams.PGA_Param;
import org.opensha.sha.imr.param.IntensityMeasureParams.SA_Param;
import org.opensha.sha.util.TectonicRegionType;

public class AB_2003_test implements ParameterChangeWarningListener {

	private AB_2003_AttenRel ab2003AttenRel = null;

	private static String pgaTableFile = "AtkinsonBoore2003Global-PGA-g.dat";
	private static String saTableFile = "AtkinsonBoore2003Global-1Hz-g.dat";
	private static int tableNumCol = 4;
	private static int tableNumRows = 24;

	private static int numSiteTypes = 3;
	private static int numMagnitude = 3;
	private static String[] siteType = null;
	private static double[] magnitude = null;
	private static double[][] pgaTable = null;
	private static double[][] saTable = null;

	private static double interfaceHypocentralDepth = 25.0;

	// for percentage difference
	private static double tolerance = 10;

	@Before
	public void setUp() throws Exception {
		ab2003AttenRel = new AB_2003_AttenRel(this);
		ab2003AttenRel.setParamDefaults();
		siteType = new String[numSiteTypes];
		siteType[0] = AB_2003_AttenRel.SITE_TYPE_ROCK;
		siteType[1] = AB_2003_AttenRel.SITE_TYPE_HARD_SOIL;
		siteType[2] = AB_2003_AttenRel.SITE_TYPE_MEDIUM_SOIL;
		magnitude = new double[numMagnitude];
		magnitude[0] = 8.8;
		magnitude[1] = 8.0;
		magnitude[2] = 7.0;
		pgaTable = new double[AB_2003_test.tableNumRows][AB_2003_test.tableNumCol];
		readTable(
				new File(ClassLoader.getSystemResource(
						AB_2003_test.pgaTableFile).toURI()), pgaTable);
		saTable = new double[AB_2003_test.tableNumRows][AB_2003_test.tableNumCol];
		readTable(
				new File(ClassLoader
						.getSystemResource(AB_2003_test.saTableFile).toURI()),
				saTable);
	}

	@After
	public void tearDown() {
		ab2003AttenRel = null;
		siteType = null;
		magnitude = null;
		pgaTable = null;
		saTable = null;
	}

	/**
	 * Check median spectral acceleration (1Hz) for Mw=8.8, interface event,
	 * site type NEHRP B, hypocentral depth 25 km
	 */
	@Test
	public void sa1HzMw88InterfaceNerphBHypodepth25() {

		ab2003AttenRel.setIntensityMeasure(SA_Param.NAME);
		String tectonicRegionType = TectonicRegionType.SUBDUCTION_INTERFACE
				.toString();
		double hypocentralDepth = interfaceHypocentralDepth;
		int periodIndex = 5;
		int magnitudeIndex = 0;
		int siteTypeIndex = 0;
		int expectedResultIndex = 1;

		validateAgainstTable(tectonicRegionType, hypocentralDepth, periodIndex,
				magnitudeIndex, siteTypeIndex, expectedResultIndex, saTable);
	}

	/**
	 * Check median spectral acceleration (1Hz) for Mw8, interface event, site
	 * type Nerph C, hypocentral depth 25 km
	 */
	@Test
	public void sa1HzMw8InterfaceNerphCHypodepth25() {

		ab2003AttenRel.setIntensityMeasure(SA_Param.NAME);
		String tectonicRegionType = TectonicRegionType.SUBDUCTION_INTERFACE
				.toString();
		double hypocentralDepth = interfaceHypocentralDepth;
		int periodIndex = 5;
		int magnitudeIndex = 1;
		int siteTypeIndex = 1;
		int expectedResultIndex = 2;

		validateAgainstTable(tectonicRegionType, hypocentralDepth, periodIndex,
				magnitudeIndex, siteTypeIndex, expectedResultIndex, saTable);
	}

	/**
	 * Check median spectral acceleration (1Hz) for Mw = 7.0, interface event,
	 * site type NEHRP D, hypocentral depth 25 km
	 */
	@Test
	public void sa1HzMw7InterfaceNerphDHypodepth25() {

		ab2003AttenRel.setIntensityMeasure(SA_Param.NAME);
		String tectonicRegionType = TectonicRegionType.SUBDUCTION_INTERFACE
				.toString();
		double hypocentralDepth = interfaceHypocentralDepth;
		int periodIndex = 5;
		int magnitudeIndex = 2;
		int siteTypeIndex = 2;
		int expectedResultIndex = 3;

		validateAgainstTable(tectonicRegionType, hypocentralDepth, periodIndex,
				magnitudeIndex, siteTypeIndex, expectedResultIndex, saTable);
	}

	/**
	 * Check median pga for Mw=8.8, interface event, site type NEHRP B,
	 * hypocentral depth 25 km
	 */
	@Test
	public void pgaMw88InterfaceNerphBHypodepth25() {

		ab2003AttenRel.setIntensityMeasure(PGA_Param.NAME);
		String tectonicRegionType = TectonicRegionType.SUBDUCTION_INTERFACE
				.toString();
		double hypocentralDepth = interfaceHypocentralDepth;
		int periodIndex = 0;
		int magnitudeIndex = 0;
		int siteTypeIndex = 0;
		int expectedResultIndex = 1;

		validateAgainstTable(tectonicRegionType, hypocentralDepth, periodIndex,
				magnitudeIndex, siteTypeIndex, expectedResultIndex, pgaTable);
	}

	/**
	 * Check median pga for Mw8, interface event, site type Nerph C, hypocentral
	 * depth 25 km
	 */
	@Test
	public void pgaMw8InterfaceNerphCHypodepth25() {

		ab2003AttenRel.setIntensityMeasure(PGA_Param.NAME);
		String tectonicRegionType = TectonicRegionType.SUBDUCTION_INTERFACE
				.toString();
		double hypocentralDepth = interfaceHypocentralDepth;
		int periodIndex = 0;
		int magnitudeIndex = 1;
		int siteTypeIndex = 1;
		int expectedResultIndex = 2;

		validateAgainstTable(tectonicRegionType, hypocentralDepth, periodIndex,
				magnitudeIndex, siteTypeIndex, expectedResultIndex, pgaTable);
	}

	/**
	 * Check median pga for Mw = 7.0, interface event, site type NEHRP D,
	 * hypocentral depth 25 km
	 */
	@Test
	public void pgaMw7InterfaceNerphDHypodepth25() {

		ab2003AttenRel.setIntensityMeasure(PGA_Param.NAME);
		String tectonicRegionType = TectonicRegionType.SUBDUCTION_INTERFACE
				.toString();
		double hypocentralDepth = interfaceHypocentralDepth;
		int periodIndex = 0;
		int magnitudeIndex = 2;
		int siteTypeIndex = 2;
		int expectedResultIndex = 3;

		validateAgainstTable(tectonicRegionType, hypocentralDepth, periodIndex,
				magnitudeIndex, siteTypeIndex, expectedResultIndex, pgaTable);
	}

	private void readTable(File file, double[][] table) throws Exception {
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

	private void validateAgainstTable(String tectonicRegionType,
			double hypocentralDepth, int periodIndex, int magnitudeIndex,
			int siteTypeIndex, int expectedResultIndex, double[][] table) {
		for (int i = 0; i < table.length; i++) {

			double distance = table[i][0];
			double mag = magnitude[magnitudeIndex];
			String sType = siteType[siteTypeIndex];

			double predicted = Math
					.exp(ab2003AttenRel.getMean(periodIndex, mag, distance,
							sType, tectonicRegionType, hypocentralDepth));
			double expected = table[i][expectedResultIndex];
			double percentageDifference = Math.abs((expected - predicted)
					/ expected) * 100;

			String msg = "distance: " + distance + ", magnitude: " + mag
					+ ", site type: " + sType + ", tectonic region type: "
					+ tectonicRegionType + ", hypocentral depth: "
					+ hypocentralDepth + ", expected: " + expected
					+ ", predicted: " + predicted + ",percentage difference: "
					+ percentageDifference;
			System.out.println("expected: " + expected + ", predicted: "
					+ predicted + "percentage diff: " + percentageDifference);
			assertTrue(msg, percentageDifference < tolerance);
		}
	}

	@Override
	public void parameterChangeWarning(ParameterChangeWarningEvent event) {
	}

}
