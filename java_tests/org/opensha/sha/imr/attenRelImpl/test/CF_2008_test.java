package org.opensha.sha.imr.attenRelImpl.test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opensha.commons.param.event.ParameterChangeWarningEvent;
import org.opensha.commons.param.event.ParameterChangeWarningListener;
import org.opensha.sha.imr.attenRelImpl.CF_2008_AttenRel;
import org.opensha.sha.imr.attenRelImpl.constants.CF2008Constants;
import org.opensha.sha.imr.param.OtherParams.StdDevTypeParam;

/**
 * Class providing methods for testing {@link CF_2008_AttenRel}. Tables
 * provided by the original authors.
 */
public class CF_2008_test implements ParameterChangeWarningListener {

	/** Cauzzi and Faciolli 2008 GMPE (attenuation relationship) */
	private CF_2008_AttenRel cf2008AttenRel = null;

	/**
	 * Table for total standard deviation validation.
	 */
	private static final String SIGMA_TOTAL_NN_ROCK_TABLE = "CF08_SIGMAT_NN_ROCK.OUT";

	/**
	 * Table for median ground motion validation. Normal event on rock.
	 */
	private static final String MEDIAN_NM_ROCK_TABLE = "CF08_MEDIAN_NN_ROCK.OUT";
	
	/**
	 * Table for median ground motion validation. Normal event on soft soil.
	 */
	private static final String MEDIAN_NM_SOFT_TABLE = "CF08_MEDIAN_NN_SOFT.OUT";
	
	/**
	 * Table for median ground motion validation. Normal event on stiff soil.
	 */
	private static final String MEDIAN_NM_STIFF_TABLE = "CF08_MEDIAN_NN_STIFF.OUT";
	/**
	 * Table for median ground motion validation. Normal event on soil class D.
	 */
	private static final String MEDIAN_NM_SITED_TABLE = "CF08_MEDIAN_NN_SITED.OUT";
	
	/**
	 * Table for median ground motion validation. Reverse event on rock.
	 */
	private static final String MEDIAN_REVERSE_ROCK_TABLE = "CF08_MEDIAN_RR_ROCK.OUT";
	
	/**
	 * Table for median ground motion validation. Reverse event on soft soil.
	 */
	private static final String MEDIAN_REVERSE_SOFT_TABLE = "CF08_MEDIAN_RR_SOFT.OUT";
	
	/**
	 * Table for median ground motion validation. Reverse event on stiff soil.
	 */
	private static final String MEDIAN_REVERSE_STIFF_TABLE = "CF08_MEDIAN_RR_STIFF.OUT";
	/**
	 * Table for median ground motion validation. Reverse event on stiff soil.
	 */
	private static final String MEDIAN_REVERSE_SITED_TABLE = "CF08_MEDIAN_RR_SITED.OUT";

	/**
	 * Table for median ground motion validation. Strike-slip event on rock.
	 */
	private static final String MEDIAN_STRIKESLIP_ROCK_TABLE = "CF08_MEDIAN_SS_ROCK.OUT";
	
	/**
	 * Table for median ground motion validation. Strike-slip event on soft soil.
	 */
	private static final String MEDIAN_STRIKESLIP_SOFT_TABLE = "CF08_MEDIAN_SS_SOFT.OUT";
	
	/**
	 * Table for median ground motion validation. Strike-slip event on stiff soil.
	 */
	private static final String MEDIAN_STRIKESLIP_STIFF_TABLE = "CF08_MEDIAN_SS_STIFF.OUT";
	/**
	 * Table for median ground motion validation. Strike-slip event on stiff soil.
	 */
	private static final String MEDIAN_STRIKESLIP_SITED_TABLE = "CF08_MEDIAN_SS_SITED.OUT";

	/** Header for meadian tables. */
	private static String[] TABLE_HEADER_MEDIAN = new String[1];
	
	/** Header for standard deviation tables. */
	private static String[] TABLE_HEADER_STD = new String[1];

	/** Number of columns in test tables for standard deviation. */
	private static final int TABLE_NUM_COL_STD = 405;

	/** Number of columns in test tables for median ground motion value. */
	private static final int TABLE_NUM_COL_MEDIAN = 405;

	/** Number of rows in interface test table. */
	private static final int TABLE_NUM_ROWS = 35;

	/** Inter event standard deviation verification table. */
	private static double[][] stdTotalTable = null;

	/** Median ground motion verification table. Normal event on rock. */
	private static double[][] medianNormalRockTable = null;
	/** Median ground motion verification table. Normal event on soft soil. */
	private static double[][] medianNormalSoftTable = null;
	/** Median ground motion verification table. Normal event on stiff soil. */
	private static double[][] medianNormalStiffTable = null;
	/** Median ground motion verification table. Normal event on stiff soil. */
	private static double[][] medianNormalSitedTable = null;
	
	/** Median ground motion verification table. Reverse event on rock. */
	private static double[][] medianReverseRockTable = null;
	/** Median ground motion verification table. Reverse event on soft soil. */
	private static double[][] medianReverseSoftTable = null;
	/** Median ground motion verification table. Reverse event on stiff soil. */
	private static double[][] medianReverseStiffTable = null;
	/** Median ground motion verification table. Reverse event on stiff soil. */
	private static double[][] medianReverseSitedTable = null;
	
	/** Median ground motion verification table. Strike slip event on rock. */
	private static double[][] medianStrikeSlipRockTable = null;
	
	/** Median ground motion verification table. Strike slip event on soft soil. */
	private static double[][] medianStrikeSlipSoftTable = null;
	/** Median ground motion verification table. Strike slip event on stiff soil. */
	private static double[][] medianStrikeSlipStiffTable = null;
	/** Median ground motion verification table. Strike slip event on stiff soil. */
	private static double[][] medianStrikeSlipSitedTable = null;

	private static final double TOLERANCE = 1e-3;

	/**
	 * Set up attenuation relationship object, and tables for tests.
	 * 
	 * @throws Exception
	 */
	@Before
	public final void setUp() throws Exception {
		cf2008AttenRel = new CF_2008_AttenRel(this);
		cf2008AttenRel.setParamDefaults();

		stdTotalTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL_STD];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(SIGMA_TOTAL_NN_ROCK_TABLE).toURI()),
				stdTotalTable, TABLE_HEADER_STD);
		medianNormalRockTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL_MEDIAN];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(MEDIAN_NM_ROCK_TABLE).toURI()),
				medianNormalRockTable, TABLE_HEADER_MEDIAN);
		medianNormalSoftTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL_MEDIAN];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(MEDIAN_NM_SOFT_TABLE).toURI()),
				medianNormalSoftTable, TABLE_HEADER_MEDIAN);
		medianNormalStiffTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL_MEDIAN];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(MEDIAN_NM_STIFF_TABLE).toURI()),
				medianNormalStiffTable, TABLE_HEADER_MEDIAN);
		medianNormalSitedTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL_MEDIAN];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(MEDIAN_NM_SITED_TABLE).toURI()),
				medianNormalSitedTable, TABLE_HEADER_MEDIAN);
		
		medianReverseRockTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL_MEDIAN];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(MEDIAN_REVERSE_ROCK_TABLE).toURI()),
				medianReverseRockTable, TABLE_HEADER_MEDIAN);
		medianReverseSoftTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL_MEDIAN];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(MEDIAN_REVERSE_SOFT_TABLE).toURI()),
				medianReverseSoftTable, TABLE_HEADER_MEDIAN);
		
		medianReverseStiffTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL_MEDIAN];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(MEDIAN_REVERSE_STIFF_TABLE).toURI()),
				medianReverseStiffTable, TABLE_HEADER_MEDIAN);
		
		medianReverseSitedTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL_MEDIAN];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(MEDIAN_REVERSE_SITED_TABLE).toURI()),
				medianReverseSitedTable, TABLE_HEADER_MEDIAN);

		
		medianStrikeSlipRockTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL_MEDIAN];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(MEDIAN_STRIKESLIP_ROCK_TABLE).toURI()),
				medianStrikeSlipRockTable, TABLE_HEADER_MEDIAN);
		medianStrikeSlipSoftTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL_MEDIAN];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(MEDIAN_STRIKESLIP_SOFT_TABLE).toURI()),
				medianStrikeSlipSoftTable, TABLE_HEADER_MEDIAN);
		medianStrikeSlipStiffTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL_MEDIAN];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(MEDIAN_STRIKESLIP_STIFF_TABLE).toURI()),
				medianStrikeSlipStiffTable, TABLE_HEADER_MEDIAN);
		medianStrikeSlipSitedTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL_MEDIAN];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(MEDIAN_STRIKESLIP_SITED_TABLE).toURI()),
				medianStrikeSlipSitedTable, TABLE_HEADER_MEDIAN);

	}

	/**
	 * Clean up.
	 */
	@After
	public final void tearDown() {
		cf2008AttenRel = null;
		stdTotalTable = null;
		medianNormalRockTable = null;
		medianNormalSoftTable = null;
		medianNormalStiffTable = null;
		medianNormalSitedTable = null;
		
		medianReverseRockTable = null;
		medianReverseSoftTable = null;
		medianReverseStiffTable = null;
		medianReverseSitedTable = null;

		medianStrikeSlipRockTable = null;
		medianStrikeSlipSoftTable = null;
		medianStrikeSlipStiffTable = null;
		medianStrikeSlipSitedTable = null;
	}

	@Test
	public void checkStdTotal() {
		validateStdDev(StdDevTypeParam.STD_DEV_TYPE_TOTAL, stdTotalTable);
	}

	@Test
	public void checkMedianNormalEventOnRock() {
		double vs30 = 800.0;
		double rake = -90.0;
		validateMedian(vs30, rake, medianNormalRockTable);
	}
	
	@Test
	public void checkMedianNormalEventOnSoftSoil() {
		double vs30 = 200.0;
		double rake = -90.0;
		validateMedian(vs30, rake, medianNormalSoftTable);
	}
	
	@Test
	public void checkMedianNormalEventOnStiffSoil() {
		double vs30 = 400.0;
		double rake = -90.0;
		validateMedian(vs30, rake, medianNormalStiffTable);
	}

	@Test
	public void checkMedianNormalEventOnSitedSoil() {
		double vs30 = 100.0;
		double rake = -90.0;
		validateMedian(vs30, rake, medianNormalSitedTable);
	}

	@Test
	public void checkMedianReverseEventOnRock() {
		double vs30 = 800.0;
		double rake = 90.0;
		validateMedian(vs30, rake, medianReverseRockTable);
	}
	
	@Test
	public void checkMedianReverseEventOnSoftSoil() {
		double vs30 = 200.0;
		double rake = 90.0;
		validateMedian(vs30, rake, medianReverseSoftTable);
	}
	
	@Test
	public void checkMedianReverseEventOnStiffSoil() {
		double vs30 = 400.0;
		double rake = 90.0;
		validateMedian(vs30, rake, medianReverseStiffTable);
	}

	@Test
	public void checkMedianReverseEventOnSitedSoil() {
		double vs30 = 100.0;
		double rake = 90.0;
		validateMedian(vs30, rake, medianReverseSitedTable);
	}
	
	@Test
	public void checkMedianStrikeSlipEventOnRock() {
		double vs30 = 800.0;
		double rake = 0.0;
		validateMedian(vs30, rake, medianStrikeSlipRockTable);
	}
	
	@Test
	public void checkMedianStrikeSlipEventOnSoftSoil() {
		double vs30 = 200.0;
		double rake = 0.0;
		validateMedian(vs30, rake, medianStrikeSlipSoftTable);
	}
	
	@Test
	public void checkMedianStrikeSlipEventOnStiffSoil() {
		double vs30 = 400.0;
		double rake = 0.0;
		validateMedian(vs30, rake, medianStrikeSlipStiffTable);
	}

	@Test
	public void checkMedianStrikeSlipEventOnSitedSoil() {
		double vs30 = 100.0;
		double rake = 0.0;
		validateMedian(vs30, rake, medianStrikeSlipSitedTable);
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
				double computedMedian = Math.exp(cf2008AttenRel.getMean(iper,
						mag, rJB, vs30, rake));
				assertEquals(expectedMedian, computedMedian, TOLERANCE);
			}
		}
		// check for PGA
		for (int j = 0; j < table.length; j++) {
			double mag = table[j][0];
			double rJB = table[j][1];
			double expectedMedian = table[j][columnDescr.length - 2];
			double computedMedian = Math.exp(cf2008AttenRel.getMean(1, mag,
					rJB, vs30, rake));
			assertEquals(expectedMedian, computedMedian, TOLERANCE);
		}
		// check for PGV
		for (int j = 0; j < table.length; j++) {
			double mag = table[j][0];
			double rJB = table[j][1];
			double expectedMedian = table[j][columnDescr.length - 1];
			double computedMedian = Math.exp(cf2008AttenRel.getMean(0, mag,
					rJB, vs30, rake));
			assertEquals(expectedMedian, computedMedian, TOLERANCE);
		}
	}

	private void validateStdDev(String stdDevType, double[][] table) {
		String[] columnDescr = TABLE_HEADER_STD[0].trim().split("\\s+");
		// check for SA
		for (int i = 3; i < columnDescr.length - 2; i++) {
			for (int j = 0; j < table.length; j++) {
				double expectedStd = table[j][i];
				double computedStd = cf2008AttenRel.getStdDev(i - 1,
						stdDevType) / CF2008Constants.LOG10_2_LN;
				assertEquals(expectedStd, computedStd, TOLERANCE);
			}
		}
		// check for PGA
		for (int j = 0; j < table.length; j++) {
			double expectedStd = table[j][columnDescr.length - 2];
			double computedStd = cf2008AttenRel.getStdDev(1, stdDevType)
					/ CF2008Constants.LOG10_2_LN;
			assertEquals(expectedStd, computedStd, TOLERANCE);
		}
		// check for PGV
		for (int j = 0; j < table.length; j++) {
			double expectedStd = table[j][columnDescr.length - 1];
			double computedStd = cf2008AttenRel.getStdDev(0, stdDevType)
					/ CF2008Constants.LOG10_2_LN;
			assertEquals(expectedStd, computedStd, TOLERANCE);
		}
	}

	@Override
	public void parameterChangeWarning(ParameterChangeWarningEvent event) {
	}
}