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

/**
 * Class providing methods for testing {@link AB_2003_AttenRel}. 
 * Tables (for the global model, without corrections for Japan/Cascadia)
 * were provided by Celine Beauval (<celine.beauval@obs.ujf-grenoble.fr>)
 *  using matSHA.
 */
public class AB_2003_test implements ParameterChangeWarningListener {

	/** Atkinson and Boore 2003 attenuation relationship. */
	private AB_2003_AttenRel ab2003AttenRel = null;

	/** Table for peak ground acceleration interface test. */
	private static final String PGA_INTERFACE_TABLE_FILE = 
		"AtkinsonBoore2003Global-PGA-g.dat";

	/** Table for spectral acceleration interface test. */
	private static final String SA_INTERFACE_TABLE_FILE = 
		"AtkinsonBoore2003Global-1Hz-g.dat";

	/** Table for peak ground acceleration intraslab test. */
	private static final String PGA_INTRASLAB_TABLE_FILE = 
		"AtkinsonBoore2003Global-PGA-INTRASLAB-g.dat";

	/** Table for spectral acceleration intraslab test. */
	private static final String SA_INTRASLAB_TABLE_FILE = 
		"AtkinsonBoore2003Global-1Hz-INTRASLAB-g.dat";

	/** Number of columns in test tables. */
	private static final int TABLE_NUM_COL = 4;

	/** Number of rows in interface test table. */
	private static final int INTERFACE_TABLE_NUM_ROWS = 24;

	/** Number of rows in intraslab test table. */
	private static final int INTRA_SLAB_TABLE_NUM_ROWS = 21;

	/** Site types for interface/intraSlab tests. */
	private static final double[] VS_30 = new double[] { 800.0, 500.0, 200.0 };
	/**
	 * Magnitude values for interface/intraSlab tests.
	 */
	private static final double[] MAGNITUDE_VALUES = new double[] { 8.8, 8.0,
			7.0 };
	/** Peak ground acceleration table for interface test. */
	private static double[][] pgaInterfaceTable = null;
	
	/** Spectral acceleration table for interface test. */
	private static double[][] saInterfaceTable = null;
	
	/** Peak ground acceleration table for intraSlab test. */
	private static double[][] pgaIntraSlabTable = null;
	
	/** Spectral acceleration table for intraSlab test. */
	private static double[][] saIntraSlabTable = null;

	/** Hypocentral depth for interface test. */
	private static final double INTERFACE_HYPO_DEPTH = 25.0;
	
	/**
	 * Hypocentral depth for intraSlab test.
	 */
	private static final double INTRA_SLAB_HYPO_DEPTH = 60.0;
	
	/** Tolerance level (expressed in percentage). */
	private static final double TOLERANCE = 0.1;

	/**
	 * Set up attenuation relationship object, and tables for tests.
	 * 
	 * @throws Exception
	 */
	@Before
	public final void setUp() throws Exception {
		ab2003AttenRel = new AB_2003_AttenRel(this);
		ab2003AttenRel.setParamDefaults();
		pgaInterfaceTable = 
			new double[AB_2003_test.INTERFACE_TABLE_NUM_ROWS]
			           [AB_2003_test.TABLE_NUM_COL];
		readTable(
				new File(ClassLoader.getSystemResource(
						AB_2003_test.PGA_INTERFACE_TABLE_FILE).toURI()),
				pgaInterfaceTable);
		saInterfaceTable = 
			new double[AB_2003_test.INTERFACE_TABLE_NUM_ROWS]
			           [AB_2003_test.TABLE_NUM_COL];
		readTable(
				new File(ClassLoader.getSystemResource(
						AB_2003_test.SA_INTERFACE_TABLE_FILE).toURI()),
				saInterfaceTable);
		pgaIntraSlabTable =
			new double[AB_2003_test.INTRA_SLAB_TABLE_NUM_ROWS]
			           [AB_2003_test.TABLE_NUM_COL];
		readTable(
				new File(ClassLoader.getSystemResource(
						AB_2003_test.PGA_INTRASLAB_TABLE_FILE).toURI()),
				pgaIntraSlabTable);
		saIntraSlabTable =
			new double[AB_2003_test.INTRA_SLAB_TABLE_NUM_ROWS]
			           [AB_2003_test.TABLE_NUM_COL];
		readTable(
				new File(ClassLoader.getSystemResource(
						AB_2003_test.SA_INTRASLAB_TABLE_FILE).toURI()),
				saIntraSlabTable);
	}

	/**
	 * Clean up.
	 */
	@After
	public final void tearDown() {
		ab2003AttenRel = null;
		pgaInterfaceTable = null;
		saInterfaceTable = null;
		pgaIntraSlabTable = null;
		saIntraSlabTable = null;
	}

	/**
	 * Check median spectral acceleration (1Hz) for Mw=8.8, intraslab event,
	 * site type NEHRP B, hypocentral depth 60 km.
	 */
	@Test
	public final void sa1HzMw88IntraSlabNerphBHypodepth60() {

		ab2003AttenRel.setIntensityMeasure(SA_Param.NAME);
		String tectonicRegionType = TectonicRegionType.SUBDUCTION_SLAB
				.toString();
		double hypocentralDepth = INTRA_SLAB_HYPO_DEPTH;
		int periodIndex = 5;
		int magnitudeIndex = 0;
		int siteTypeIndex = 0;
		int expectedResultIndex = 1;

		validateAgainstTable(tectonicRegionType, hypocentralDepth, periodIndex,
				magnitudeIndex, siteTypeIndex, expectedResultIndex,
				saIntraSlabTable);
	}

	/**
	 * Check median spectral acceleration (1Hz) for Mw8, intraslab event, site
	 * type Nerph C, hypocentral depth 60 km.
	 */
	@Test
	public final void sa1HzMw8IntraSlabNerphCHypodepth60() {

		ab2003AttenRel.setIntensityMeasure(SA_Param.NAME);
		String tectonicRegionType = TectonicRegionType.SUBDUCTION_SLAB
				.toString();
		double hypocentralDepth = INTRA_SLAB_HYPO_DEPTH;
		int periodIndex = 5;
		int magnitudeIndex = 1;
		int siteTypeIndex = 1;
		int expectedResultIndex = 2;

		validateAgainstTable(tectonicRegionType, hypocentralDepth, periodIndex,
				magnitudeIndex, siteTypeIndex, expectedResultIndex,
				saIntraSlabTable);
	}

	/**
	 * Check median spectral acceleration (1Hz) for Mw = 7.0, intraslab event,
	 * site type NEHRP D, hypocentral depth 60 km.
	 */
	@Test
	public final void sa1HzMw7IntraSlabNerphDHypodepth60() {

		ab2003AttenRel.setIntensityMeasure(SA_Param.NAME);
		String tectonicRegionType = TectonicRegionType.SUBDUCTION_SLAB
				.toString();
		double hypocentralDepth = INTRA_SLAB_HYPO_DEPTH;
		int periodIndex = 5;
		int magnitudeIndex = 2;
		int siteTypeIndex = 2;
		int expectedResultIndex = 3;

		validateAgainstTable(tectonicRegionType, hypocentralDepth, periodIndex,
				magnitudeIndex, siteTypeIndex, expectedResultIndex,
				saIntraSlabTable);
	}

	/**
	 * Check median pga, for Mw = 8.8, intraslab event, site type NEHRP B,
	 * hypocentral depth 60 km.
	 */
	@Test
	public final void pgaMw88IntraSlabNerphBHypodepth60() {
		//ab2003AttenRel.setIntensityMeasure(PGA_Param.NAME);
		String tectonicRegionType = TectonicRegionType.SUBDUCTION_SLAB
				.toString();
		double hypocentralDepth = INTRA_SLAB_HYPO_DEPTH;
		int periodIndex = 0;
		int magnitudeIndex = 0;
		int siteTypeIndex = 0;
		int expectedResultIndex = 1;

		validateAgainstTable(tectonicRegionType, hypocentralDepth, periodIndex,
				magnitudeIndex, siteTypeIndex, expectedResultIndex,
				pgaIntraSlabTable);
	}

	/**
	 * Check median pga for Mw8, intra slab event, site type Nerph C,
	 * hypocentral depth 60 km.
	 */
	@Test
	public final void pgaMw8IntraSlabNerphCHypodepth60() {

		ab2003AttenRel.setIntensityMeasure(PGA_Param.NAME);
		String tectonicRegionType = TectonicRegionType.SUBDUCTION_SLAB
				.toString();
		double hypocentralDepth = INTRA_SLAB_HYPO_DEPTH;
		int periodIndex = 0;
		int magnitudeIndex = 1;
		int siteTypeIndex = 1;
		int expectedResultIndex = 2;

		validateAgainstTable(tectonicRegionType, hypocentralDepth, periodIndex,
				magnitudeIndex, siteTypeIndex, expectedResultIndex,
				pgaIntraSlabTable);
	}

	/**
	 * Check median pga for Mw = 7.0, intraslab event, site type NEHRP D,
	 * hypocentral depth 60 km.
	 */
	@Test
	public final void pgaMw7IntraSlabNerphDHypodepth60() {

		ab2003AttenRel.setIntensityMeasure(PGA_Param.NAME);
		String tectonicRegionType = TectonicRegionType.SUBDUCTION_SLAB
				.toString();
		double hypocentralDepth = INTRA_SLAB_HYPO_DEPTH;
		int periodIndex = 0;
		int magnitudeIndex = 2;
		int siteTypeIndex = 2;
		int expectedResultIndex = 3;

		validateAgainstTable(tectonicRegionType, hypocentralDepth, periodIndex,
				magnitudeIndex, siteTypeIndex, expectedResultIndex,
				pgaIntraSlabTable);
	}

	/**
	 * Check median spectral acceleration (1Hz) for Mw=8.8, interface event,
	 * site type NEHRP B, hypocentral depth 25 km.
	 */
	@Test
	public final void sa1HzMw88InterfaceNerphBHypodepth25() {

		ab2003AttenRel.setIntensityMeasure(SA_Param.NAME);
		String tectonicRegionType = TectonicRegionType.SUBDUCTION_INTERFACE
				.toString();
		double hypocentralDepth = INTERFACE_HYPO_DEPTH;
		int periodIndex = 5;
		int magnitudeIndex = 0;
		int siteTypeIndex = 0;
		int expectedResultIndex = 1;

		validateAgainstTable(tectonicRegionType, hypocentralDepth, periodIndex,
				magnitudeIndex, siteTypeIndex, expectedResultIndex,
				saInterfaceTable);
	}

	/**
	 * Check median spectral acceleration (1Hz) for Mw8, interface event, site
	 * type Nerph C, hypocentral depth 25 km.
	 */
	@Test
	public final void sa1HzMw8InterfaceNerphCHypodepth25() {

		ab2003AttenRel.setIntensityMeasure(SA_Param.NAME);
		String tectonicRegionType = TectonicRegionType.SUBDUCTION_INTERFACE
				.toString();
		double hypocentralDepth = INTERFACE_HYPO_DEPTH;
		int periodIndex = 5;
		int magnitudeIndex = 1;
		int siteTypeIndex = 1;
		int expectedResultIndex = 2;

		validateAgainstTable(tectonicRegionType, hypocentralDepth, periodIndex,
				magnitudeIndex, siteTypeIndex, expectedResultIndex,
				saInterfaceTable);
	}

	/**
	 * Check median spectral acceleration (1Hz) for Mw = 7.0, interface event,
	 * site type NEHRP D, hypocentral depth 25 km.
	 */
	@Test
	public final void sa1HzMw7InterfaceNerphDHypodepth25() {

		ab2003AttenRel.setIntensityMeasure(SA_Param.NAME);
		String tectonicRegionType = TectonicRegionType.SUBDUCTION_INTERFACE
				.toString();
		double hypocentralDepth = INTERFACE_HYPO_DEPTH;
		int periodIndex = 5;
		int magnitudeIndex = 2;
		int siteTypeIndex = 2;
		int expectedResultIndex = 3;

		validateAgainstTable(tectonicRegionType, hypocentralDepth, periodIndex,
				magnitudeIndex, siteTypeIndex, expectedResultIndex,
				saInterfaceTable);
	}

	/**
	 * Check median pga for Mw=8.8, interface event, site type NEHRP B,
	 * hypocentral depth 25 km.
	 */
	@Test
	public final void pgaMw88InterfaceNerphBHypodepth25() {

		ab2003AttenRel.setIntensityMeasure(PGA_Param.NAME);
		String tectonicRegionType = TectonicRegionType.SUBDUCTION_INTERFACE
				.toString();
		double hypocentralDepth = INTERFACE_HYPO_DEPTH;
		int periodIndex = 0;
		int magnitudeIndex = 0;
		int siteTypeIndex = 0;
		int expectedResultIndex = 1;

		validateAgainstTable(tectonicRegionType, hypocentralDepth, periodIndex,
				magnitudeIndex, siteTypeIndex, expectedResultIndex,
				pgaInterfaceTable);
	}

	/**
	 * Check median pga for Mw8, interface event, site type Nerph C, hypocentral
	 * depth 25 km.
	 */
	@Test
	public final void pgaMw8InterfaceNerphCHypodepth25() {

		ab2003AttenRel.setIntensityMeasure(PGA_Param.NAME);
		String tectonicRegionType = TectonicRegionType.SUBDUCTION_INTERFACE
				.toString();
		double hypocentralDepth = INTERFACE_HYPO_DEPTH;
		int periodIndex = 0;
		int magnitudeIndex = 1;
		int siteTypeIndex = 1;
		int expectedResultIndex = 2;

		validateAgainstTable(tectonicRegionType, hypocentralDepth, periodIndex,
				magnitudeIndex, siteTypeIndex, expectedResultIndex,
				pgaInterfaceTable);
	}

	/**
	 * Check median pga for Mw = 7.0, interface event, site type NEHRP D,
	 * hypocentral depth 25 km.
	 */
	@Test
	public final void pgaMw7InterfaceNerphDHypodepth25() {

		ab2003AttenRel.setIntensityMeasure(PGA_Param.NAME);
		String tectonicRegionType = TectonicRegionType.SUBDUCTION_INTERFACE
				.toString();
		double hypocentralDepth = INTERFACE_HYPO_DEPTH;
		int periodIndex = 0;
		int magnitudeIndex = 2;
		int siteTypeIndex = 2;
		int expectedResultIndex = 3;

		validateAgainstTable(tectonicRegionType, hypocentralDepth, periodIndex,
				magnitudeIndex, siteTypeIndex, expectedResultIndex,
				pgaInterfaceTable);
	}

	/**
	 * Read table.
	 * 
	 * @param file
	 * @param table
	 * @throws Exception
	 */
	private void readTable(final File file, final double[][] table)
			throws Exception {
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

	/**
	 * Compare median ground motion againts values in table.
	 * 
	 * @param tectonicRegionType
	 * @param hypocentralDepth
	 * @param periodIndex
	 * @param magnitudeIndex
	 * @param siteTypeIndex
	 * @param expectedResultIndex
	 * @param table
	 */
	private void validateAgainstTable(final String tectonicRegionType,
			final double hypocentralDepth, final int periodIndex,
			final int magnitudeIndex, final int siteTypeIndex,
			final int expectedResultIndex, final double[][] table) {
		for (int i = 0; i < table.length; i++) {

			double distance = table[i][0];
			double mag = MAGNITUDE_VALUES[magnitudeIndex];
			double vs30 = VS_30[siteTypeIndex];

			double predicted = Math.exp(ab2003AttenRel.getMean(periodIndex,
					mag, distance, vs30, tectonicRegionType, hypocentralDepth));
			double expected = table[i][expectedResultIndex];
			double percentageDifference = Math.abs((expected - predicted)
					/ expected) * 100;

			String msg = "distance: " + distance + ", magnitude: " + mag
					+ ", vs30: " + vs30 + ", tectonic region type: "
					+ tectonicRegionType + ", hypocentral depth: "
					+ hypocentralDepth + ", expected: " + expected
					+ ", predicted: " + predicted + ",percentage difference: "
					+ percentageDifference;
//			System.out.println("expected: " + expected + ", predicted: "
//					+ predicted + "percentage diff: " + percentageDifference);
			assertTrue(msg, percentageDifference < TOLERANCE);
		}
	}

	@Override
	public void parameterChangeWarning(final ParameterChangeWarningEvent event) {
	}

}
