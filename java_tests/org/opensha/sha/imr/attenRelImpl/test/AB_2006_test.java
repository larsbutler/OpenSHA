package org.opensha.sha.imr.attenRelImpl.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opensha.commons.param.event.ParameterChangeWarningEvent;
import org.opensha.commons.param.event.ParameterChangeWarningListener;
import org.opensha.sha.imr.attenRelImpl.AB_2006_AttenRel;
import org.opensha.sha.imr.param.EqkRuptureParams.MagParam;
import org.opensha.sha.imr.param.EqkRuptureParams.StressDropParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.PeriodParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.SA_Param;
import org.opensha.sha.imr.param.PropagationEffectParams.DistanceRupParameter;
import org.opensha.sha.imr.param.SiteParams.Vs30_Param;

/**
 * Class providing tests for {@link AB_2006_AttenRel}.
 * 
 */

public class AB_2006_test implements ParameterChangeWarningListener {

	// Atkinson and Boore 2006 Attenuation Relationship
	private AB_2006_AttenRel ab2006_AttenRel = null;

	// verification table for ground motion calculation on hard rock
	private static String HARD_ROCK_TABLE_FILE = "AB06_HARD_ROCK.OUT";

	// verification table for ground motion calculation on soft soil
	private static String SOIL_TABLE_FILE = "AB06_SOIL.OUT";

	private static int TABLE_NUM_COLS = 16;

	private static int TABLE_NUM_ROWS = 36;

	private static double[][] hardRockTable = null;

	private static double[][] soilTable = null;

	// in percent
	private static double TOLERANCE = 6.0;

	/**
	 * Set up attenuation relationship object and tables for verification.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws URISyntaxException, Exception {
		ab2006_AttenRel = new AB_2006_AttenRel(this);
		ab2006_AttenRel.setParamDefaults();
		hardRockTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COLS];
		soilTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COLS];
		String[] header = new String[1];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(HARD_ROCK_TABLE_FILE).toURI()),
				hardRockTable, header);
		AttenRelTestHelper
				.readNumericTableWithHeader(new File(ClassLoader
						.getSystemResource(SOIL_TABLE_FILE).toURI()),
						soilTable, header);
	}

	@After
	public void tearDown() {
		ab2006_AttenRel = null;
		hardRockTable = null;
		soilTable = null;
	}

	@Test
	public void hardRock() {
		for (int i = 0; i < TABLE_NUM_ROWS; i++) {
			
			double period = hardRockTable[i][1];
			double vs30 = hardRockTable[i][5];
			double rrup = hardRockTable[i][4];
			double mag = hardRockTable[i][2];
			double stressDrop = hardRockTable[i][6];
			
			ab2006_AttenRel.setIntensityMeasure(SA_Param.NAME);
			ab2006_AttenRel.getParameter(PeriodParam.NAME).setValue(period);
			ab2006_AttenRel.getParameter(Vs30_Param.NAME).setValue(vs30);
			ab2006_AttenRel.getParameter(DistanceRupParameter.NAME).setValue(rrup);
			ab2006_AttenRel.getParameter(MagParam.NAME).setValue(mag);
			ab2006_AttenRel.getParameter(StressDropParam.NAME).setValue(stressDrop);

			double computedMedian = Math.exp(ab2006_AttenRel.getMean());
			double expectedMedian = hardRockTable[i][10];
			double percentageDifference = 100
					* Math.abs(computedMedian - expectedMedian)
					/ expectedMedian;
			String msg = "computed: " + computedMedian + ", expected: "
					+ expectedMedian + ", percentage difference: "
					+ percentageDifference;
			assertTrue(msg, percentageDifference < TOLERANCE);
		}
	}

	@Test
	public void soil() {
		for (int i = 0; i < TABLE_NUM_ROWS; i++) {
			
			double period = soilTable[i][1];
			double vs30 = soilTable[i][5];
			double rrup = soilTable[i][4];
			double mag = soilTable[i][2];
			double stressDrop = soilTable[i][6];
			
			ab2006_AttenRel.setIntensityMeasure(SA_Param.NAME);
			ab2006_AttenRel.getParameter(PeriodParam.NAME).setValue(period);
			ab2006_AttenRel.getParameter(Vs30_Param.NAME).setValue(vs30);
			ab2006_AttenRel.getParameter(DistanceRupParameter.NAME).setValue(rrup);
			ab2006_AttenRel.getParameter(MagParam.NAME).setValue(mag);
			ab2006_AttenRel.getParameter(StressDropParam.NAME).setValue(stressDrop);
			double computedMedian = Math.exp(ab2006_AttenRel.getMean());
			double expectedMedian = soilTable[i][10];
			double percentageDifference = 100
					* Math.abs(computedMedian - expectedMedian)
					/ expectedMedian;
			String msg = "computed: " + computedMedian + ", expected: "
					+ expectedMedian + ", percentage difference: "
					+ percentageDifference;
			assertTrue(msg, percentageDifference < TOLERANCE);
		}
	}

	@Override
	public void parameterChangeWarning(ParameterChangeWarningEvent event) {
	}

}
