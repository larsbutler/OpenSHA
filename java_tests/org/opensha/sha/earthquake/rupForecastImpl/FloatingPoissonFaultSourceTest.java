package org.opensha.sha.earthquake.rupForecastImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.opensha.commons.data.Site;
import org.opensha.commons.data.function.ArbitrarilyDiscretizedFunc;
import org.opensha.commons.geo.Location;
import org.opensha.commons.param.StringParameter;
import org.opensha.sha.calc.HazardCurveCalculator;
import org.opensha.sha.earthquake.ProbEqkRupture;
import org.opensha.sha.util.TectonicRegionType;

/**
 * Class providing methods for testing {@link FloatingPoissonFaultSource}.
 */
public class FloatingPoissonFaultSourceTest {

	private static final double TOLERANCE = 1e-3;
	
	@Test
	public void checkSetTectonicRegionTypeWithGetRupture(){
		FloatingPoissonFaultSource src =
			FloatingPoissonFaultSourceTestHelper
			.getPeerTestSet1Case5FaultSource();
		src.setTectonicRegionType(TectonicRegionType.VOLCANIC);
		for(int i=0;i<src.getRuptureList().size();i++){
			assertTrue(src.getRupture(i).getTectRegType().equals(TectonicRegionType.VOLCANIC));
		}
	}
	
	@Test
	public void checkSetTectonicRegionTypeWithGetRuptureList(){
		FloatingPoissonFaultSource src =
			FloatingPoissonFaultSourceTestHelper
			.getPeerTestSet1Case5FaultSource();
		src.setTectonicRegionType(TectonicRegionType.VOLCANIC);
		for(ProbEqkRupture rup: (ArrayList<ProbEqkRupture>)src.getRuptureList()){
			assertTrue(rup.getTectRegType().equals(TectonicRegionType.VOLCANIC));
		}
	}

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
		hazCurve.set(Math.log(0.8), 1.0);
		
		Map<Site, ArbitrarilyDiscretizedFunc> expectedResults = 
			getExpectedResultsPeerTestSet1Case5();

		for (Site site : expectedResults.keySet()) {
			calc.getHazardCurve(hazCurve, site, PoissonianAreaSourceTestHelper
					.getPeerTestSet1Case10GMPE(),
					FloatingPoissonFaultSourceTestHelper
							.getPeerTestSet1Case5FaultSourceErf());
			System.out.println("Site: "+site);
			for (int i = 0; i < expectedResults.get(site).getNum(); i++) {
				System.out.println("Expected: "+expectedResults.get(site).getY(i)+
						", predicted: "+hazCurve.getY(i));
				assertEquals(expectedResults.get(site).getY(i),
						hazCurve.getY(i), TOLERANCE);
			}
		}
		
	}
	
	private static Map<Site, ArbitrarilyDiscretizedFunc> 
	getExpectedResultsPeerTestSet1Case5() {

		StringParameter sadighSiteType = new StringParameter("Sadigh Site Type");
		sadighSiteType.setValue("Rock");

		Map<Site, ArbitrarilyDiscretizedFunc> expectedResults = 
			new HashMap<Site, ArbitrarilyDiscretizedFunc>();

		// site 1
		ArbitrarilyDiscretizedFunc hazCurveSite1 = 
			new ArbitrarilyDiscretizedFunc();
		hazCurveSite1.set(Math.log(0.001), 4.00E-02);
		hazCurveSite1.set(Math.log(0.01), 4.00E-02);
		hazCurveSite1.set(Math.log(0.05), 4.00E-02);
		hazCurveSite1.set(Math.log(0.1), 3.99E-02);
		hazCurveSite1.set(Math.log(0.15), 3.46E-02);
		hazCurveSite1.set(Math.log(0.2), 2.57E-02);
		hazCurveSite1.set(Math.log(0.25), 1.89E-02);
		hazCurveSite1.set(Math.log(0.3), 1.37E-02);
		hazCurveSite1.set(Math.log(0.35), 9.88E-03);
		hazCurveSite1.set(Math.log(0.4), 6.93E-03);
		hazCurveSite1.set(Math.log(0.45), 4.84E-03);
		hazCurveSite1.set(Math.log(0.5), 3.36E-03);
		hazCurveSite1.set(Math.log(0.55), 2.34E-03);
		hazCurveSite1.set(Math.log(0.6), 1.52E-03);
		hazCurveSite1.set(Math.log(0.7), 5.12E-04);
		Site site1 = new Site(new Location(38.113,-122.000));
		site1.addParameter(sadighSiteType);
		expectedResults.put(site1, hazCurveSite1);

		// site 2
		ArbitrarilyDiscretizedFunc hazCurveSite2 = 
			new ArbitrarilyDiscretizedFunc();
		hazCurveSite2.set(Math.log(0.001), 4.00E-02);
		hazCurveSite2.set(Math.log(0.01), 4.00E-02);
		hazCurveSite2.set(Math.log(0.05), 4.00E-02);
		hazCurveSite2.set(Math.log(0.1), 3.31E-02);
		hazCurveSite2.set(Math.log(0.15), 1.22E-02);
		hazCurveSite2.set(Math.log(0.2), 4.85E-03);
		hazCurveSite2.set(Math.log(0.25), 1.76E-03);
		hazCurveSite2.set(Math.log(0.3), 2.40E-04);
		Site site2 = new Site(new Location(38.113,-122.114));
		site2.addParameter(sadighSiteType);
		expectedResults.put(site2, hazCurveSite2);

		// site 3
		ArbitrarilyDiscretizedFunc hazCurveSite3 = 
			new ArbitrarilyDiscretizedFunc();
		hazCurveSite3.set(Math.log(0.001), 4.00E-02);
		hazCurveSite3.set(Math.log(0.01), 4.00E-02);
		Site site3 = new Site(new Location(38.111,-122.570));
		site3.addParameter(sadighSiteType);
		expectedResults.put(site3, hazCurveSite3);
		
		// site 4
		ArbitrarilyDiscretizedFunc hazCurveSite4 = 
			new ArbitrarilyDiscretizedFunc();
		hazCurveSite4.set(Math.log(0.001), 3.99E-02);
		hazCurveSite4.set(Math.log(0.01), 3.99E-02);
		hazCurveSite4.set(Math.log(0.05), 3.98E-02);
		hazCurveSite4.set(Math.log(0.1), 2.99E-02);
		hazCurveSite4.set(Math.log(0.15), 2.00E-02);
		hazCurveSite4.set(Math.log(0.2), 1.30E-02);
		hazCurveSite4.set(Math.log(0.25), 8.58E-03);
		hazCurveSite4.set(Math.log(0.3), 5.72E-03);
		hazCurveSite4.set(Math.log(0.35), 3.88E-03);
		hazCurveSite4.set(Math.log(0.4), 2.69E-03);
		hazCurveSite4.set(Math.log(0.45), 1.91E-03);
		hazCurveSite4.set(Math.log(0.5), 1.37E-03);
		hazCurveSite4.set(Math.log(0.55), 9.74E-04);
		hazCurveSite4.set(Math.log(0.6), 6.75E-04);
		hazCurveSite4.set(Math.log(0.7), 2.52E-04);
		hazCurveSite4.set(Math.log(0.8), 0.00E+00);
		Site site4 = new Site(new Location(38.000,-122.000));
		site4.addParameter(sadighSiteType);
		expectedResults.put(site4, hazCurveSite4);

		// site 5
		ArbitrarilyDiscretizedFunc hazCurveSite5 = 
			new ArbitrarilyDiscretizedFunc();
		hazCurveSite5.set(Math.log(0.001), 3.99E-02);
		hazCurveSite5.set(Math.log(0.01), 3.99E-02);
		hazCurveSite5.set(Math.log(0.05), 3.14E-02);
		hazCurveSite5.set(Math.log(0.1), 1.21E-02);
		hazCurveSite5.set(Math.log(0.15), 4.41E-03);
		hazCurveSite5.set(Math.log(0.2), 1.89E-03);
		hazCurveSite5.set(Math.log(0.25), 7.53E-04);
		hazCurveSite5.set(Math.log(0.3), 1.25E-04);
		hazCurveSite5.set(Math.log(0.35), 0.00E+00);
		hazCurveSite5.set(Math.log(0.4), 0.00E+00);
		hazCurveSite5.set(Math.log(0.45), 0.00E+00);
		hazCurveSite5.set(Math.log(0.5), 0.00E+00);
		hazCurveSite5.set(Math.log(0.55), 0.00E+00);
		hazCurveSite5.set(Math.log(0.6), 0.00E+00);
		hazCurveSite5.set(Math.log(0.7), 0.00E+00);
		hazCurveSite5.set(Math.log(0.8), 0.00E+00);
		Site site5 = new Site(new Location(37.910,-122.000));
		site5.addParameter(sadighSiteType);
		expectedResults.put(site5, hazCurveSite5);

		// site 6
		ArbitrarilyDiscretizedFunc hazCurveSite6 = 
			new ArbitrarilyDiscretizedFunc();
		hazCurveSite6.set(Math.log(0.001), 3.99E-02);
		hazCurveSite6.set(Math.log(0.01), 3.99E-02);
		hazCurveSite6.set(Math.log(0.05), 3.98E-02);
		hazCurveSite6.set(Math.log(0.1), 2.99E-02);
		hazCurveSite6.set(Math.log(0.15), 2.00E-02);
		hazCurveSite6.set(Math.log(0.2), 1.30E-02);
		hazCurveSite6.set(Math.log(0.25), 8.58E-03);
		hazCurveSite6.set(Math.log(0.3), 5.72E-03);
		hazCurveSite6.set(Math.log(0.35), 3.88E-03);
		hazCurveSite6.set(Math.log(0.4), 2.69E-03);
		hazCurveSite6.set(Math.log(0.45), 1.91E-03);
		hazCurveSite6.set(Math.log(0.5), 1.37E-03);
		hazCurveSite6.set(Math.log(0.55), 9.74E-04);
		hazCurveSite6.set(Math.log(0.6), 6.75E-04);
		hazCurveSite6.set(Math.log(0.7), 2.52E-04);
		hazCurveSite6.set(Math.log(0.8), 0.00E+00);
		Site site6 = new Site(new Location(38.225,-122.000));
		site6.addParameter(sadighSiteType);
		expectedResults.put(site6, hazCurveSite6);

		// site 7
		ArbitrarilyDiscretizedFunc hazCurveSite7 = 
			new ArbitrarilyDiscretizedFunc();
		hazCurveSite7.set(Math.log(0.001), 4.00E-02);
		hazCurveSite7.set(Math.log(0.01), 4.00E-02);
		hazCurveSite7.set(Math.log(0.05), 4.00E-02);
		hazCurveSite7.set(Math.log(0.1), 3.31E-02);
		hazCurveSite7.set(Math.log(0.15), 1.22E-02);
		hazCurveSite7.set(Math.log(0.2), 4.85E-03);
		hazCurveSite7.set(Math.log(0.25), 1.76E-03);
		hazCurveSite7.set(Math.log(0.3), 2.40E-04);
		Site site7 = new Site(new Location(38.113,-121.886));
		site7.addParameter(sadighSiteType);
		expectedResults.put(site7, hazCurveSite7);

		return expectedResults;
	}

	/**
	 * This test was introduced as a results of this bug report:
	 * https://bugs.launchpad.net/openquake/+bug/901092
	 * 
	 * FloatingPoissonFaultSource overrides the setTectonicRegionType
	 * method from the base class (ProbEqkSource).
	 * 
	 * However, the override fails to do what the base implementation
	 * does: set the tectonic region type.
	 * 
	 * I wonder how long this bug has been here, and why we found
	 * it just now.
	 */
	@Test
	public void testSetTectonicRegionTypeActuallySetsTectonicRegionType() {
	    FloatingPoissonFaultSource src =
	            FloatingPoissonFaultSourceTestHelper.getPeerTestSet1Case5FaultSource();
	    // We assume the default is ACTIVE_SHALLOW
	    assertEquals(TectonicRegionType.ACTIVE_SHALLOW, src.getTectonicRegionType());

	    src.setTectonicRegionType(TectonicRegionType.SUBDUCTION_INTERFACE);
	    assertEquals(TectonicRegionType.SUBDUCTION_INTERFACE, src.getTectonicRegionType());
	}
}
