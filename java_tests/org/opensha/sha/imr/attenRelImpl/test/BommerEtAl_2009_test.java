package org.opensha.sha.imr.attenRelImpl.test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opensha.commons.param.event.ParameterChangeWarningEvent;
import org.opensha.commons.param.event.ParameterChangeWarningListener;
import org.opensha.sha.imr.attenRelImpl.BommerEtAl_2009_AttenRel;
import org.opensha.sha.imr.param.OtherParams.StdDevTypeParam;

/**
 * Class providing methods for testing {@link BommerEtAl_2009_AttenRel}. Tables
 * created from StaffordGMPEs.jar provided by Peter J. Stafford.
 */
public class BommerEtAl_2009_test implements ParameterChangeWarningListener {

	/** Bommer et al. (2009) GMPE (attenuation relationship) */
	private BommerEtAl_2009_AttenRel Betal2009AttenRel = null;

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
		Betal2009AttenRel = new BommerEtAl_2009_AttenRel(this);
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

	private void validateMedian(double vs30, double ztor, double[][] table) {
		// check for relative significant duration
		for (int j = 0; j < table.length; j++) {
			double mag=4.5+j*0.5;
			double rrup = 10;
			double expectedMedian = table[j][1];
			double computedMedian = Math.exp(Betal2009AttenRel.getMean(mag,rrup, vs30,ztor));
			assertEquals(expectedMedian, computedMedian, TOLERANCE);
		}
	}

	private void validateStdDev(double[][] table) {
		// check for standard deviations
		for (int j = 0; j < table.length; j++) {
			double expectedStd_Inter = table[j][3];
			double computedStd_Inter = Betal2009AttenRel.getStdDev(StdDevTypeParam.STD_DEV_TYPE_INTER);
			double expectedStd_Intra = table[j][4];
			double computedStd_Intra = Betal2009AttenRel.getStdDev(StdDevTypeParam.STD_DEV_TYPE_INTRA);
			double expectedStd_Total = table[j][5];
			double computedStd_Total = Betal2009AttenRel.getStdDev(StdDevTypeParam.STD_DEV_TYPE_TOTAL);
			assertEquals(expectedStd_Inter, computedStd_Inter, TOLERANCE);
			assertEquals(expectedStd_Intra, computedStd_Intra, TOLERANCE);
			assertEquals(expectedStd_Total, computedStd_Total, TOLERANCE);
		}
	}

	@Override
	public void parameterChangeWarning(ParameterChangeWarningEvent event) {
	}
}

