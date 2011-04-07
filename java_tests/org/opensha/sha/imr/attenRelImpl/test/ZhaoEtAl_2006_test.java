package org.opensha.sha.imr.attenRelImpl.test;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opensha.commons.param.event.ParameterChangeWarningEvent;
import org.opensha.commons.param.event.ParameterChangeWarningListener;
import org.opensha.sha.imr.attenRelImpl.ZhaoEtAl_2006_AttenRel;
import org.opensha.sha.util.TectonicRegionType;

public class ZhaoEtAl_2006_test implements ParameterChangeWarningListener {

	private ZhaoEtAl_2006_AttenRel zhaoEtAlAttenRel = null;

	private static final String M65Dist20Depth10ShallowCrustNormalTableFile = "zhao_r20.0_m6.5_dep10.0_shallow_normal_site1.dat";

	/** Number of columns in test tables. */
	private static final int TABLE_NUM_COL = 4;

	/** Number of rows in test table. */
	private static final int TABLE_NUM_ROWS = 21;

	/** Peak ground acceleration table for interface test. */
	private static double[][] M65Dist20Depth10ShallowCrustNormalTable = null;

	/**
	 * Set up attenuation relationship object, and tables for tests.
	 */
	@Before
	public final void setUp() throws Exception {
		zhaoEtAlAttenRel = new ZhaoEtAl_2006_AttenRel(this);
		zhaoEtAlAttenRel.setParamDefaults();
		M65Dist20Depth10ShallowCrustNormalTable = new double[TABLE_NUM_ROWS][TABLE_NUM_COL];
		AttenRelTestHelper.readTable(
				new File(ClassLoader.getSystemResource(
						M65Dist20Depth10ShallowCrustNormalTableFile).toURI()),
				M65Dist20Depth10ShallowCrustNormalTable);
	}

	/**
	 * Clean up.
	 */
	@After
	public final void tearDown() {
		zhaoEtAlAttenRel = null;
		M65Dist20Depth10ShallowCrustNormalTable = null;
	}

//	@Test
//	public void m65Dist20Depth10ShallowCrustNormal() {
//
//		String tectonicRegionType = TectonicRegionType.SUBDUCTION_SLAB
//				.toString();
//		double hypocentralDepth = INTRA_SLAB_HYPO_DEPTH;
//		int periodIndex = 5;
//		int magnitudeIndex = 0;
//		int siteTypeIndex = 0;
//		double magnitude = INTRASLAB_MAGNITUDE_VALUES[magnitudeIndex];
//		double vs30 = VS_30[siteTypeIndex];
//		int expectedResultIndex = 1;
//
//		validateAgainstTable(tectonicRegionType, hypocentralDepth, periodIndex,
//				magnitude, vs30, expectedResultIndex, saIntraSlabTable);
//	}
//	
//	/**
//	 * Compare median ground motion againt values in table.
//	 */
//	private void validateAgainstTable(final String tectonicRegionType,
//			final double hypocentralDepth, final int periodIndex,
//			final double mag, final double vs30,
//			final int expectedResultIndex, final double[][] table) {
//		for (int i = 0; i < table.length; i++) {
//
//			double distance = table[i][0];
//
//			double predicted = Math.exp(zhaoEtAlAttenRel.getMean(periodIndex,
//					mag, distance, vs30, tectonicRegionType, hypocentralDepth));
//			double expected = table[i][expectedResultIndex];
//			double percentageDifference = Math.abs((expected - predicted)
//					/ expected) * 100;
//
//			String msg = "distance: " + distance + ", magnitude: " + mag
//					+ ", vs30: " + vs30 + ", tectonic region type: "
//					+ tectonicRegionType + ", hypocentral depth: "
//					+ hypocentralDepth + ", expected: " + expected
//					+ ", predicted: " + predicted + ",percentage difference: "
//					+ percentageDifference;
//			assertTrue(msg, percentageDifference < TOLERANCE);
//		}
//	}

	@Override
	public void parameterChangeWarning(ParameterChangeWarningEvent event) {
	}

}
