package org.opensha.sha.imr.attenRelImpl.test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opensha.commons.param.event.ParameterChangeWarningEvent;
import org.opensha.commons.param.event.ParameterChangeWarningListener;
import org.opensha.sha.imr.attenRelImpl.AkB2010Constants;
import org.opensha.sha.imr.attenRelImpl.AkB_2010_AttenRel;
import org.opensha.sha.imr.param.OtherParams.StdDevTypeParam;

/**
 * Class providing methods for testing {@link AkB_2010_AttenRel}. Tables
 * provided by the original authors.
 */
public class AkB_2010_test implements ParameterChangeWarningListener {

	/** Akkar and Bommer 2010 attenuation relationship. */
	private AkB_2010_AttenRel akb2010AttenRel = null;

	/**
	 * Table for inter-event standard deviation validation.
	 */
	private static final String SIGMA_INTER_NM_ROCK_TABLE = "AB10_SIGMAINTER_NM_ROCK.OUT";
	
	/**
	 * Table for intra-event standard deviation validation.
	 */
	private static final String SIGMA_INTRA_NM_ROCK_TABLE = "AB10_SIGMAINTRA_NM_ROCK.OUT";
	
	/**
	 * Table for total standard deviation validation.
	 */
	private static final String SIGMA_TOTAL_NM_ROCK_TABLE = "AB10_SIGMAT_NM_ROCK.OUT";

	/** Table header. Same for all tables. */
	private static String[] TABLE_HEADER = new String[1];

	/** Number of columns in test tables. */
	private static final int TABLE_NUM_COL = 69;

	/** Number of rows in interface test table. */
	private static final int TABLE_NUM_ROWS = 42;

	/** Inter event standard deviation verification table. */
	private static double[][] stdInterTable = null;
	
	/** Inter event standard deviation verification table. */
	private static double[][] stdIntraTable = null;
	
	/** Inter event standard deviation verification table. */
	private static double[][] stdTotalTable = null;
	
	private static final double TOLERANCE = 1e-4;

	/**
	 * Set up attenuation relationship object, and tables for tests.
	 * 
	 * @throws Exception
	 */
	@Before
	public final void setUp() throws Exception {
		akb2010AttenRel = new AkB_2010_AttenRel(this);
		akb2010AttenRel.setParamDefaults();
		stdInterTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(SIGMA_INTER_NM_ROCK_TABLE).toURI()),
				stdInterTable, TABLE_HEADER);
		stdIntraTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(SIGMA_INTRA_NM_ROCK_TABLE).toURI()),
				stdIntraTable, TABLE_HEADER);
		stdTotalTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(SIGMA_TOTAL_NM_ROCK_TABLE).toURI()),
				stdTotalTable, TABLE_HEADER);
	}

	/**
	 * Clean up.
	 */
	@After
	public final void tearDown() {
		akb2010AttenRel = null;
		stdInterTable = null;
		stdIntraTable = null;
		stdTotalTable = null;
	}

	@Test
	public void checkStdInter() {
		validateStdDev(StdDevTypeParam.STD_DEV_TYPE_INTER, stdInterTable);
	}
	
	@Test
	public void checkStdIntra() {
		validateStdDev(StdDevTypeParam.STD_DEV_TYPE_INTRA, stdIntraTable);
	}
	
	@Test
	public void checkStdTotal() {
		validateStdDev(StdDevTypeParam.STD_DEV_TYPE_TOTAL, stdTotalTable);
	}

	private void validateStdDev(String stdDevType, double[][] table) {
		String[] columnDescr = TABLE_HEADER[0].split(" ");
		// check for SA
		for (int i = 3; i < columnDescr.length - 2; i++) {
			for (int j = 0; j < table.length; j++) {
				double expectedStd = table[j][i];
				double computedStd = akb2010AttenRel.getStdDev(i - 1,
						stdDevType) / AkB2010Constants.LOG10_2_LN;
				assertEquals(expectedStd,computedStd,TOLERANCE);
			}
		}
		// check for PGA
		for (int j = 0; j < table.length; j++) {
			double expectedStd = table[j][columnDescr.length - 2];
			double computedStd = akb2010AttenRel.getStdDev(1,
					stdDevType) / AkB2010Constants.LOG10_2_LN;
			assertEquals(expectedStd,computedStd,TOLERANCE);
		}
		// check for PGV
		for (int j = 0; j < table.length; j++) {
			double expectedStd = table[j][columnDescr.length - 1];
			double computedStd = akb2010AttenRel.getStdDev(0,
					stdDevType) / AkB2010Constants.LOG10_2_LN;
			assertEquals(expectedStd,computedStd,TOLERANCE);
		}
	}

	@Override
	public void parameterChangeWarning(ParameterChangeWarningEvent event) {
	}
}
