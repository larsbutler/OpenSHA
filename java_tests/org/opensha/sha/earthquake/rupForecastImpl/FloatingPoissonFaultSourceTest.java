package org.opensha.sha.earthquake.rupForecastImpl;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.opensha.commons.data.Site;
import org.opensha.commons.data.function.ArbitrarilyDiscretizedFunc;
import org.opensha.commons.geo.Location;
import org.opensha.commons.param.StringParameter;
import org.opensha.sha.calc.HazardCurveCalculator;

/**
 * Class providing methods for testing {@link FloatingPoissonFaultSource}.
 */
public class FloatingPoissonFaultSourceTest {

	private static final double TOLERANCE = 1e-2;

	/**
	 * Implements Peer Test Set 1 Case 5. (i.e. compares hazard curves
	 * generated from {@link FloatingPoissonFaultSource}
	 * with reference hazard curves) (test taken from "Verification of
	 * Probabilistic Seismic Hazard Analysis Computer Programs",
	 * Patricia Thomas, Ivan Wong and Norman Abrahamson,
	 * PACIFIC EARTHQUAKE ENGINEERING RESEARCH CENTER, May 2010)
	 * 
	 * @throws Exception
	 */
	@Test
	public void peerTestSet1Case5() throws Exception {
		
		HazardCurveCalculator calc = new HazardCurveCalculator();
		
		ArbitrarilyDiscretizedFunc hazCurve = new ArbitrarilyDiscretizedFunc();
		hazCurve.set(Math.log(0.001), 1.0);
		hazCurve.set(Math.log(0.01), 1.0);
		hazCurve.set(Math.log(0.05), 1.0);
		hazCurve.set(Math.log(0.1), 1.0);
		hazCurve.set(Math.log(0.15), 1.0);
		hazCurve.set(Math.log(0.2), 1.0);
		hazCurve.set(Math.log(0.25), 1.0);
		hazCurve.set(Math.log(0.3), 1.0);
		hazCurve.set(Math.log(0.35), 1.0);
		hazCurve.set(Math.log(0.4), 1.0);
		hazCurve.set(Math.log(0.45), 1.0);
		hazCurve.set(Math.log(0.5), 1.0);
		hazCurve.set(Math.log(0.55), 1.0);
		hazCurve.set(Math.log(0.6), 1.0);
		hazCurve.set(Math.log(0.7), 1.0);
		
		Map<Site, ArbitrarilyDiscretizedFunc> expectedResults = 
			getExpectedResultsPeerTestSet1Case5();

		for (Site site : expectedResults.keySet()) {
			calc.getHazardCurve(hazCurve, site, PoissonianAreaSourceTestHelper
					.getPeerTestSet1Case10GMPE(),
					PoissonianAreaSourceTestHelper
							.getPeerTestSet1Case10AreaSourceErf());
			for (int i = 0; i < expectedResults.get(site).getNum(); i++) {
				assertEquals(expectedResults.get(site).getY(i),
						hazCurve.getY(i), TOLERANCE);
			}
		}
		
	}
	
	private static Map<Site, ArbitrarilyDiscretizedFunc> 
	getExpectedResultsPeerTestSet1Case5() {
		
//		Site Latitude Longitude Comment
//		1 38.113 -122.000 On Fault Midpoint along Strike
//		2 38.113 -122.114 10km West of fault, at midpoint
//		3 38.111 -122.570 50km West of fault, at midpoint
//		4 38.000 -122.000 South end of fault
//		5 37.910 -122.000 10km south of fault along strike
//		6 38.225 -122.000 North end of fault
//		7 38.113 -121.886 10km East of fault, at midpoint
		
//		Acceleration (g)
//		Site 1 Site 2 Site 3 Site 7
//		0.001 4.00E-02 4.00E-02 4.00E-02 4.00E-02
//		0.01 4.00E-02 4.00E-02 4.00E-02 4.00E-02
//		0.05 4.00E-02 4.00E-02 4.00E-02
//		0.1 3.99E-02 3.31E-02 3.31E-02
//		0.15 3.46E-02 1.22E-02 1.22E-02
//		0.2 2.57E-02 4.85E-03 4.85E-03
//		0.25 1.89E-02 1.76E-03 1.76E-03
//		0.3 1.37E-02 2.40E-04 2.40E-04
//		0.35 9.88E-03
//		0.4 6.93E-03
//		0.45 4.84E-03
//		0.5 3.36E-03
//		0.55 2.34E-03
//		0.6 1.52E-03
//		0.7 5.12E-04

		
//		Hand Solutions for Set 1, Test Case 5
//		Peak Ground
//		Acceleration (g)
//		Site 4 Site 5 Site 6
//		0.001 3.99E-02 3.99E-02 3.99E-02
//		0.01 3.99E-02 3.99E-02 3.99E-02
//		0.05 3.98E-02 3.14E-02 3.98E-02
//		0.1 2.99E-02 1.21E-02 2.99E-02
//		0.15 2.00E-02 4.41E-03 2.00E-02
//		0.2 1.30E-02 1.89E-03 1.30E-02
//		0.25 8.58E-03 7.53E-04 8.58E-03
//		0.3 5.72E-03 1.25E-04 5.72E-03
//		0.35 3.88E-03 0.00E+00 3.88E-03
//		0.4 2.69E-03 0.00E+00 2.69E-03
//		0.45 1.91E-03 0.00E+00 1.91E-03
//		0.5 1.37E-03 0.00E+00 1.37E-03
//		0.55 9.74E-04 0.00E+00 9.74E-04
//		0.6 6.75E-04 0.00E+00 6.75E-04
//		0.7 2.52E-04 0.00E+00 2.52E-04
//		0.8 0.00E+00 0.00E+00 0.00E+00

		StringParameter sadighSiteType = new StringParameter("Sadigh Site Type");
		sadighSiteType.setValue("Rock");

		Map<Site, ArbitrarilyDiscretizedFunc> expectedResults = 
			new HashMap<Site, ArbitrarilyDiscretizedFunc>();

		ArbitrarilyDiscretizedFunc hazCurveSite1 = 
			new ArbitrarilyDiscretizedFunc();
		hazCurveSite1.set(Math.log(0.001), 3.87E-02);
		hazCurveSite1.set(Math.log(0.01), 2.19E-02);
		hazCurveSite1.set(Math.log(0.05), 2.97E-03);
		hazCurveSite1.set(Math.log(0.1), 9.22E-04);
		hazCurveSite1.set(Math.log(0.15), 3.59E-04);
		hazCurveSite1.set(Math.log(0.2), 1.31E-04);
		hazCurveSite1.set(Math.log(0.25), 4.76E-05);
		hazCurveSite1.set(Math.log(0.3), 1.72E-05);
		hazCurveSite1.set(Math.log(0.35), 5.38E-06);
		hazCurveSite1.set(Math.log(0.4), 1.18E-06);
		Site site1 = new Site(new Location(38.000, -122.000));
		site1.addParameter(sadighSiteType);
		expectedResults.put(site1, hazCurveSite1);

		return expectedResults;
	}
}
