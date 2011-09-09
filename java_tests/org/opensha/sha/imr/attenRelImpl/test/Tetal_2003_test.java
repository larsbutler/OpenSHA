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
import org.opensha.sha.imr.attenRelImpl.Tetal_2003_AttenRel;
import org.opensha.sha.imr.param.IntensityMeasureParams.IA_Param;
import org.opensha.sha.imr.param.SiteParams.Vs30_Param;

/**
* Class providing methods for testing {@link Tetal_2003_AttenRel}. Tables
* created from Excel spreadsheet provided by Peter J. Stafford.
*/
public class Tetal_2003_test implements ParameterChangeWarningListener {

/** Travasarou et al. (2003) GMPE (attenuation relationship) */
private Tetal_2003_AttenRel tetal2003AttenRel = null;

/**
 * Strike-slip
 */

/**
* Table for median ground motion validation. M5 to M8 at R=1,30 and 100km. SS and D.
*/
private static final String MEDIAN_M5_M8_R1_30_100_SS_D_TABLE = "travasarouetal2003_M5-M8_R1-30-100_SS_D.out";
																 
/**
* Table for median ground motion validation. M5.5, M6.5 and M7.5 at R=1 to 250km. SS and D
*/
private static final String MEDIAN_M55_M65_M75_R01_250_SS_D_TABLE = "travasarouetal2003_M55-M65-M75_R01-250_SS_D.out";

/**
* Table for median ground motion validation. M5 to M8 at R=1,30 and 100km. SS and C.
*/
private static final String MEDIAN_M5_M8_R1_30_100_SS_C_TABLE = "travasarouetal2003_M5-M8_R1-30-100_SS_C.out";

/**
* Table for median ground motion validation. M5.5, M6.5 and M7.5 at R=1 to 250km. SS and C
*/
private static final String MEDIAN_M55_M65_M75_R01_250_SS_C_TABLE = "travasarouetal2003_M55-M65-M75_R01-250_SS_C.out";

/**
* Table for median ground motion validation. M5 to M8 at R=1,30 and 100km. SS and B.
*/
private static final String MEDIAN_M5_M8_R1_30_100_SS_B_TABLE = "travasarouetal2003_M5-M8_R1-30-100_SS_B.out";

/**
* Table for median ground motion validation. M5.5, M6.5 and M7.5 at R=1 to 250km. SS and B.
*/
private static final String MEDIAN_M55_M65_M75_R01_250_SS_B_TABLE = "travasarouetal2003_M55-M65-M75_R01-250_SS_B.out";

/**
 * Reverse
 */

/**
* Table for median ground motion validation. M5 to M8 at R=1,30 and 100km. R and D.
*/
private static final String MEDIAN_M5_M8_R1_30_100_R_D_TABLE = "travasarouetal2003_M5-M8_R1-30-100_R_D.out";

/**
* Table for median ground motion validation. M5.5, M6.5 and M7.5 at R=1 to 250km. R and D
*/
private static final String MEDIAN_M55_M65_M75_R01_250_R_D_TABLE = "travasarouetal2003_M55-M65-M75_R01-250_R_D.out";

/**
* Table for median ground motion validation. M5 to M8 at R=1,30 and 100km. R and C.
*/
private static final String MEDIAN_M5_M8_R1_30_100_R_C_TABLE = "travasarouetal2003_M5-M8_R1-30-100_R_C.out";

/**
* Table for median ground motion validation. M5.5, M6.5 and M7.5 at R=1 to 250km. R and C
*/
private static final String MEDIAN_M55_M65_M75_R01_250_R_C_TABLE = "travasarouetal2003_M55-M65-M75_R01-250_R_C.out";

/**
* Table for median ground motion validation. M5 to M8 at R=1,30 and 100km. R and B.
*/
private static final String MEDIAN_M5_M8_R1_30_100_R_B_TABLE = "travasarouetal2003_M5-M8_R1-30-100_R_B.out";

/**
* Table for median ground motion validation. M5.5, M6.5 and M7.5 at R=1 to 250km. R and B.
*/
private static final String MEDIAN_M55_M65_M75_R01_250_R_B_TABLE = "travasarouetal2003_M55-M65-M75_R01-250_R_B.out";

/**
 * Normal
 */

/**
* Table for median ground motion validation. M5 to M8 at R=1,30 and 100km. N and D.
*/
private static final String MEDIAN_M5_M8_R1_30_100_N_D_TABLE = "travasarouetal2003_M5-M8_R1-30-100_N_D.out";

/**
* Table for median ground motion validation. M5.5, M6.5 and M7.5 at R=1 to 250km. N and D
*/
private static final String MEDIAN_M55_M65_M75_R01_250_N_D_TABLE = "travasarouetal2003_M55-M65-M75_R01-250_N_D.out";

/**
* Table for median ground motion validation. M5 to M8 at R=1,30 and 100km. N and C.
*/
private static final String MEDIAN_M5_M8_R1_30_100_N_C_TABLE = "travasarouetal2003_M5-M8_R1-30-100_N_C.out";

/**
* Table for median ground motion validation. M5.5, M6.5 and M7.5 at R=1 to 250km. N and C
*/
private static final String MEDIAN_M55_M65_M75_R01_250_N_C_TABLE = "travasarouetal2003_M55-M65-M75_R01-250_N_C.out";

/**
* Table for median ground motion validation. M5 to M8 at R=1,30 and 100km. N and B.
*/
private static final String MEDIAN_M5_M8_R1_30_100_N_B_TABLE = "travasarouetal2003_M5-M8_R1-30-100_N_B.out";

/**
* Table for median ground motion validation. M5.5, M6.5 and M7.5 at R=1 to 250km. N and B.
*/
private static final String MEDIAN_M55_M65_M75_R01_250_N_B_TABLE = "travasarouetal2003_M55-M65-M75_R01-250_N_B.out";

/** Header for median tables. */
private static String[] TABLE_HEADER_MEDIAN = new String[1];

/** Number of columns in test tables for median ground motion value. */
private static final int TABLE_NUM_COL_MEDIAN = 3;

/** Number of rows in first test table. */
private static final int TABLE_NUM_ROWS_1 = 301;

/** Number of rows in second test table. */
private static final int TABLE_NUM_ROWS_2 = 2500;

/** 
 * Strike-slip
 */

/** Median ground motion verification table. M5 to M8 at R=1,30 and 100km. SS and D*/
private static double[][] medianTable_SS_D_1 = null;
/** Median ground motion verification table. M5.5, M6.5 and M7.5 at R=1 to 250km. SS and D*/
private static double[][] medianTable_SS_D_2 = null;
/** Median ground motion verification table. M5 to M8 at R=1,30 and 100km. SS and C*/
private static double[][] medianTable_SS_C_1 = null;
/** Median ground motion verification table. M5.5, M6.5 and M7.5 at R=1 to 250km. SS and C*/
private static double[][] medianTable_SS_C_2 = null;
/** Median ground motion verification table. M5 to M8 at R=1,30 and 100km. SS and B*/
private static double[][] medianTable_SS_B_1 = null;
/** Median ground motion verification table. M5.5, M6.5 and M7.5 at R=1 to 250km. SS and B*/
private static double[][] medianTable_SS_B_2 = null;

/**
 * Reverse
 */

/** Median ground motion verification table. M5 to M8 at R=1,30 and 100km. R and D*/
private static double[][] medianTable_R_D_1 = null;
/** Median ground motion verification table. M5.5, M6.5 and M7.5 at R=1 to 250km. R and D*/
private static double[][] medianTable_R_D_2 = null;
/** Median ground motion verification table. M5 to M8 at R=1,30 and 100km. R and C*/
private static double[][] medianTable_R_C_1 = null;
/** Median ground motion verification table. M5.5, M6.5 and M7.5 at R=1 to 250km. R and C*/
private static double[][] medianTable_R_C_2 = null;
/** Median ground motion verification table. M5 to M8 at R=1,30 and 100km. R and B*/
private static double[][] medianTable_R_B_1 = null;
/** Median ground motion verification table. M5.5, M6.5 and M7.5 at R=1 to 250km. R and B*/
private static double[][] medianTable_R_B_2 = null;

/**
 * Normal
 */

/** Median ground motion verification table. M5 to M8 at R=1,30 and 100km. N and D*/
private static double[][] medianTable_N_D_1 = null;
/** Median ground motion verification table. M5.5, M6.5 and M7.5 at R=1 to 250km. N and D*/
private static double[][] medianTable_N_D_2 = null;
/** Median ground motion verification table. M5 to M8 at R=1,30 and 100km. N and C*/
private static double[][] medianTable_N_C_1 = null;
/** Median ground motion verification table. M5.5, M6.5 and M7.5 at R=1 to 250km. N and C*/
private static double[][] medianTable_N_C_2 = null;
/** Median ground motion verification table. M5 to M8 at R=1,30 and 100km. N and B*/
private static double[][] medianTable_N_B_1 = null;
/** Median ground motion verification table. M5.5, M6.5 and M7.5 at R=1 to 250km. N and B*/
private static double[][] medianTable_N_B_2 = null;

private static final double TOLERANCE = 1e-3;

/**
* Set up attenuation relationship object, and tables for tests.
*
* @throws Exception
*/
@Before
public final void setUp() throws Exception {
tetal2003AttenRel = new Tetal_2003_AttenRel(this);
tetal2003AttenRel.setParamDefaults();

/**
 * Strike-slip
 */
medianTable_SS_D_1 = new double[TABLE_NUM_ROWS_1][TABLE_NUM_COL_MEDIAN];
AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
.getSystemResource(MEDIAN_M5_M8_R1_30_100_SS_D_TABLE).toURI()),
medianTable_SS_D_1, TABLE_HEADER_MEDIAN);
medianTable_SS_D_2 = new double[TABLE_NUM_ROWS_2][TABLE_NUM_COL_MEDIAN];
AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
.getSystemResource(MEDIAN_M55_M65_M75_R01_250_SS_D_TABLE).toURI()),
medianTable_SS_D_2, TABLE_HEADER_MEDIAN);
medianTable_SS_C_1 = new double[TABLE_NUM_ROWS_1][TABLE_NUM_COL_MEDIAN];
AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
.getSystemResource(MEDIAN_M5_M8_R1_30_100_SS_C_TABLE).toURI()),
medianTable_SS_C_1, TABLE_HEADER_MEDIAN);
medianTable_SS_C_2 = new double[TABLE_NUM_ROWS_2][TABLE_NUM_COL_MEDIAN];
AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
.getSystemResource(MEDIAN_M55_M65_M75_R01_250_SS_C_TABLE).toURI()),
medianTable_SS_C_2, TABLE_HEADER_MEDIAN);
medianTable_SS_B_1 = new double[TABLE_NUM_ROWS_1][TABLE_NUM_COL_MEDIAN];
AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
.getSystemResource(MEDIAN_M5_M8_R1_30_100_SS_B_TABLE).toURI()),
medianTable_SS_B_1, TABLE_HEADER_MEDIAN);
medianTable_SS_B_2 = new double[TABLE_NUM_ROWS_2][TABLE_NUM_COL_MEDIAN];
AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
.getSystemResource(MEDIAN_M55_M65_M75_R01_250_SS_B_TABLE).toURI()),
medianTable_SS_B_2, TABLE_HEADER_MEDIAN);

/**
 * Reverse
 */
medianTable_R_D_1 = new double[TABLE_NUM_ROWS_1][TABLE_NUM_COL_MEDIAN];
AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
.getSystemResource(MEDIAN_M5_M8_R1_30_100_R_D_TABLE).toURI()),
medianTable_R_D_1, TABLE_HEADER_MEDIAN);
medianTable_R_D_2 = new double[TABLE_NUM_ROWS_2][TABLE_NUM_COL_MEDIAN];
AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
.getSystemResource(MEDIAN_M55_M65_M75_R01_250_R_D_TABLE).toURI()),
medianTable_R_D_2, TABLE_HEADER_MEDIAN);
medianTable_R_C_1 = new double[TABLE_NUM_ROWS_1][TABLE_NUM_COL_MEDIAN];
AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
.getSystemResource(MEDIAN_M5_M8_R1_30_100_R_C_TABLE).toURI()),
medianTable_R_C_1, TABLE_HEADER_MEDIAN);
medianTable_R_C_2 = new double[TABLE_NUM_ROWS_2][TABLE_NUM_COL_MEDIAN];
AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
.getSystemResource(MEDIAN_M55_M65_M75_R01_250_R_C_TABLE).toURI()),
medianTable_R_C_2, TABLE_HEADER_MEDIAN);
medianTable_R_B_1 = new double[TABLE_NUM_ROWS_1][TABLE_NUM_COL_MEDIAN];
AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
.getSystemResource(MEDIAN_M5_M8_R1_30_100_R_B_TABLE).toURI()),
medianTable_R_B_1, TABLE_HEADER_MEDIAN);
medianTable_R_B_2 = new double[TABLE_NUM_ROWS_2][TABLE_NUM_COL_MEDIAN];
AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
.getSystemResource(MEDIAN_M55_M65_M75_R01_250_R_B_TABLE).toURI()),
medianTable_R_B_2, TABLE_HEADER_MEDIAN);


/**
 * Normal
 */
medianTable_N_D_1 = new double[TABLE_NUM_ROWS_1][TABLE_NUM_COL_MEDIAN];
AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
.getSystemResource(MEDIAN_M5_M8_R1_30_100_N_D_TABLE).toURI()),
medianTable_N_D_1, TABLE_HEADER_MEDIAN);
medianTable_N_D_2 = new double[TABLE_NUM_ROWS_2][TABLE_NUM_COL_MEDIAN];
AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
.getSystemResource(MEDIAN_M55_M65_M75_R01_250_N_D_TABLE).toURI()),
medianTable_N_D_2, TABLE_HEADER_MEDIAN);
medianTable_N_C_1 = new double[TABLE_NUM_ROWS_1][TABLE_NUM_COL_MEDIAN];
AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
.getSystemResource(MEDIAN_M5_M8_R1_30_100_N_C_TABLE).toURI()),
medianTable_N_C_1, TABLE_HEADER_MEDIAN);
medianTable_N_C_2 = new double[TABLE_NUM_ROWS_2][TABLE_NUM_COL_MEDIAN];
AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
.getSystemResource(MEDIAN_M55_M65_M75_R01_250_N_C_TABLE).toURI()),
medianTable_N_C_2, TABLE_HEADER_MEDIAN);
medianTable_N_B_1 = new double[TABLE_NUM_ROWS_1][TABLE_NUM_COL_MEDIAN];
AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
.getSystemResource(MEDIAN_M5_M8_R1_30_100_N_B_TABLE).toURI()),
medianTable_N_B_1, TABLE_HEADER_MEDIAN);
medianTable_N_B_2 = new double[TABLE_NUM_ROWS_2][TABLE_NUM_COL_MEDIAN];
AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
.getSystemResource(MEDIAN_M55_M65_M75_R01_250_N_B_TABLE).toURI()),
medianTable_N_B_2, TABLE_HEADER_MEDIAN);

}

/**
* Clean up.
*/
@After
public final void tearDown() {
tetal2003AttenRel = null;
medianTable_SS_D_1 = null;
medianTable_SS_D_2 = null;
medianTable_SS_C_1 = null;
medianTable_SS_C_2 = null;
medianTable_SS_B_1 = null;
medianTable_SS_B_2 = null;

medianTable_R_D_1 = null;
medianTable_R_D_2 = null;
medianTable_R_C_1 = null;
medianTable_R_C_2 = null;
medianTable_R_B_1 = null;
medianTable_R_B_2 = null;

medianTable_N_D_1 = null;
medianTable_N_D_2 = null;
medianTable_N_C_1 = null;
medianTable_N_C_2 = null;
medianTable_N_B_1 = null;
medianTable_N_B_2 = null;

}

@Test
public void medianTable_SS_D() {
double vs30 = 300.0;
double rake = 0.0;
validateMedian_1(vs30, rake, medianTable_SS_D_1);
validateMedian_2(vs30, rake, medianTable_SS_D_2);

}

@Test
public void medianTable_SS_C() {
double vs30 = 500.0;
double rake = 0.0;
validateMedian_1(vs30, rake, medianTable_SS_C_1);
validateMedian_2(vs30, rake, medianTable_SS_C_2);
}

@Test
public void medianTable_SS_B() {
double vs30 = 800.0;
double rake = 0.0;
validateMedian_1(vs30, rake, medianTable_SS_B_1);
validateMedian_2(vs30, rake, medianTable_SS_B_2);
}

@Test
public void medianTable_R_D() {
double vs30 = 300.0;
double rake = 90.0;
validateMedian_1(vs30, rake, medianTable_R_D_1);
validateMedian_2(vs30, rake, medianTable_R_D_2);
}

@Test
public void medianTable_R_C() {
double vs30 = 500.0;
double rake = 90.0;
validateMedian_1(vs30, rake, medianTable_R_C_1);
validateMedian_2(vs30, rake, medianTable_R_C_2);
}


@Test
public void medianTable_R_B() {
double vs30 = 800.0;
double rake = 90.0;
validateMedian_1(vs30, rake, medianTable_R_B_1);
validateMedian_2(vs30, rake, medianTable_R_B_2);
}


@Test
public void medianTable_N_D() {
double vs30 = 300.0;
double rake = -90.0;
validateMedian_1(vs30, rake, medianTable_N_D_1);
validateMedian_2(vs30, rake, medianTable_N_D_2);
}

@Test
public void medianTable_N_C() {
double vs30 = 500.0;
double rake = -90.0;
validateMedian_1(vs30, rake, medianTable_N_C_1);
validateMedian_2(vs30, rake, medianTable_N_C_2);
}

@Test
public void medianTable_N_B() {
double vs30 = 800.0;
double rake = -90.0;
validateMedian_1(vs30, rake, medianTable_N_B_1);
validateMedian_2(vs30, rake, medianTable_N_B_2);
}


/**
* Check AkB_2010 usage for computing hazard curves using GEM1ERF constructed
* from area source data. Ruptures are treated as
* points.
* @throws Exception
*/
@Test
public final void GEM1ERFPointRuptures() throws Exception{
tetal2003AttenRel.setIntensityMeasure(IA_Param.NAME);
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
hazCurveCalculator.getHazardCurve(hazCurve, site, tetal2003AttenRel,
erf);
}

/**
* Check Tetal_2003 usage for computing hazard curves using GEM1ERF constructed
* from area source data. Ruptures are treated as
* extended.
* @throws Exception
*/
@Test
public final void GEM1ERFLineRuptures() throws Exception{
tetal2003AttenRel.setIntensityMeasure(IA_Param.NAME);
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
hazCurveCalculator.getHazardCurve(hazCurve, site, tetal2003AttenRel,
erf);
}

/**
* Check AB2003 usage for computing hazard curves using GEM1ERF constructed
* from simple fault source data.
* @throws Exception
*/
@Test
public final void GEM1ERFSimpleFault() throws Exception{
tetal2003AttenRel.setIntensityMeasure(IA_Param.NAME);
ArrayList<GEMSourceData> srcDataList = new ArrayList<GEMSourceData>();
srcDataList.
add(AttenRelTestHelper.getActiveCrustSimpleFaultSourceData());
double timeSpan = 50.0;
GEM1ERF erf = GEM1ERF.getGEM1ERF(srcDataList, timeSpan);
HazardCurveCalculator hazCurveCalculator = new HazardCurveCalculator();
ArbitrarilyDiscretizedFunc hazCurve = AttenRelTestHelper.setUpHazardCurve();
Site site = new Site(new Location(40.2317, 15.8577));
site.addParameter(new DoubleParameter(Vs30_Param.NAME, 800.0));
hazCurveCalculator.getHazardCurve(hazCurve, site, tetal2003AttenRel,
erf);
}

private void validateMedian_1(double vs30, double rake, double[][] table) {
// check for IA
for (int j = 0; j < table.length; j++) {
double mag = 5+j*0.01;
double expectedMedian_R1 = table[j][0];
double computedMedian_R1 = Math.exp(tetal2003AttenRel.getMean(mag,1.0, vs30, rake));
double expectedMedian_R30 = table[j][1];
double computedMedian_R30 = Math.exp(tetal2003AttenRel.getMean(mag,30.0, vs30, rake));
double expectedMedian_R100 = table[j][2];
double computedMedian_R100 = Math.exp(tetal2003AttenRel.getMean(mag,100.0, vs30, rake));
System.out.println("IA");
assertEquals(expectedMedian_R1, computedMedian_R1, TOLERANCE);
assertEquals(expectedMedian_R30, computedMedian_R30, TOLERANCE);
assertEquals(expectedMedian_R100, computedMedian_R100, TOLERANCE);
}
}

private void validateMedian_2(double vs30, double rake, double[][] table) {
	// check for IA
	for (int j = 0; j < table.length; j++) {
	double rrup = 0.1+j*0.1;
	double expectedMedian_M55 = table[j][0];
	double computedMedian_M55 = Math.exp(tetal2003AttenRel.getMean(5.5,rrup, vs30, rake));
	double expectedMedian_M65 = table[j][1];
	double computedMedian_M65 = Math.exp(tetal2003AttenRel.getMean(6.5,rrup, vs30, rake));
	double expectedMedian_M75 = table[j][2];
	double computedMedian_M75 = Math.exp(tetal2003AttenRel.getMean(7.5,rrup, vs30, rake));
	System.out.println("IA");
	assertEquals(expectedMedian_M55, computedMedian_M55, TOLERANCE);
	assertEquals(expectedMedian_M65, computedMedian_M65, TOLERANCE);
	assertEquals(expectedMedian_M75, computedMedian_M75, TOLERANCE);
	}
	}

/**
private void validateStdDev(String stdDevType, double[][] table) {
String[] columnDescr = TABLE_HEADER_STD[0].trim().split("\\s+");
// check for SA
for (int i = 3; i < columnDescr.length - 2; i++) {
for (int j = 0; j < table.length; j++) {
double expectedStd = table[j][i];
double computedStd = tetal2003AttenRel.getStdDev(i - 1,
stdDevType) / Tetal2003Constants.LOG10_2_LN;
assertEquals(expectedStd, computedStd, TOLERANCE);
}
}
// check for PGA
for (int j = 0; j < table.length; j++) {
double expectedStd = table[j][columnDescr.length - 2];
double computedStd = tetal2003AttenRel.getStdDev(1, stdDevType)
/ Tetal2003Constants.LOG10_2_LN;
assertEquals(expectedStd, computedStd, TOLERANCE);
}
// check for PGV
for (int j = 0; j < table.length; j++) {
double expectedStd = table[j][columnDescr.length - 1];
double computedStd = tetal2003AttenRel.getStdDev(0, stdDevType)
/ Tetal2003Constants.LOG10_2_LN;
assertEquals(expectedStd, computedStd, TOLERANCE);
}
} 
 */

@Override
public void parameterChangeWarning(ParameterChangeWarningEvent event) {
}
}

