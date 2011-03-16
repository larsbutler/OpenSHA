package org.opensha.sha.earthquake.rupForecastImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.opensha.commons.data.Site;
import org.opensha.commons.data.TimeSpan;
import org.opensha.commons.data.function.ArbitrarilyDiscretizedFunc;
import org.opensha.commons.geo.BorderType;
import org.opensha.commons.geo.Location;
import org.opensha.commons.geo.LocationList;
import org.opensha.commons.geo.Region;
import org.opensha.commons.param.ParameterAPI;
import org.opensha.commons.param.ParameterList;
import org.opensha.commons.param.StringParameter;
import org.opensha.commons.param.event.ParameterChangeWarningEvent;
import org.opensha.commons.param.event.ParameterChangeWarningListener;
import org.opensha.sha.calc.HazardCurveCalculator;
import org.opensha.sha.earthquake.EqkRupForecastAPI;
import org.opensha.sha.earthquake.EqkRupture;
import org.opensha.sha.earthquake.FocalMechanism;
import org.opensha.sha.earthquake.ProbEqkRupture;
import org.opensha.sha.earthquake.ProbEqkSource;
import org.opensha.sha.earthquake.griddedForecast.MagFreqDistsForFocalMechs;
import org.opensha.sha.imr.ScalarIntensityMeasureRelationshipAPI;
import org.opensha.sha.imr.attenRelImpl.SadighEtAl_1997_AttenRel;
import org.opensha.sha.imr.param.IntensityMeasureParams.PGA_Param;
import org.opensha.sha.imr.param.OtherParams.StdDevTypeParam;
import org.opensha.sha.magdist.GutenbergRichterMagFreqDist;
import org.opensha.sha.magdist.IncrementalMagFreqDist;
import org.opensha.sha.util.TectonicRegionType;


/**
 * Class implementing tests for PoissonianAreaSource
 */
public class PoissonAreaSourceTest {
	
	private static double integrationDistance = 200.0;
	private static double tolerance = 1e-2;
	
    /**
     * Implements Peer Test Set 1 Case 10. (i.e. check hazard curves
     * generated from PoissonAreaSource with reference hazard curves)
     * Area source with fixed depth. Use the truncated exponential (GR) model with 
     * Mmax = 6.5 and Mmin=5.0. Lambda(M>=Mmin) = 0.0395, b value = 0.9. 
     * Source should be uniformly distributed point sources 
     * (or approximations to point source) across the area (1 km grid spacing) 
     * at a fixed depth of 5 km. 
     * The attenuation relationship is Sadigh et al. (1997), rock, sigma = 0.
     * (test taken from "Verification of Probabilistic Seismic Hazard Analysis
     *  Computer Programs", Patricia Thomas, Ivan Wong and Norman Abrahamson, 
     * PACIFIC EARTHQUAKE ENGINEERING RESEARCH CENTER, May 2010)
     * @throws RemoteException
     */
	@Test
	public void peerTestSet1Case10() throws RemoteException{
		
		HazardCurveCalculator calc = new HazardCurveCalculator();
		calc.setMaxSourceDistance(integrationDistance);
		
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
		
		Map<Site,ArbitrarilyDiscretizedFunc> expectedResults = 
			getExpectedResultsPeerTestSet1Case10();
		
		for(Site site : expectedResults.keySet()){
			calc.getHazardCurve(hazCurve, site, 
					PoissonianAreaSourceTestHelper.getPeerTestSet1Case10GMPE(), 
					PoissonianAreaSourceTestHelper.getPeerTestSet1Case10AreaSourceErf());
			for(int i=0;i<expectedResults.get(site).getNum();i++){
				assertEquals(expectedResults.get(site).getY(i),hazCurve.getY(i),tolerance);
			}
		}
	}
	
	private static Map<Site,ArbitrarilyDiscretizedFunc> 
	                        getExpectedResultsPeerTestSet1Case10(){
		
		StringParameter sadighSiteType = new StringParameter("Sadigh Site Type");
		sadighSiteType.setValue("Rock");
		
		Map<Site,ArbitrarilyDiscretizedFunc> expectedResults = 
			new HashMap<Site,ArbitrarilyDiscretizedFunc>();
		
		ArbitrarilyDiscretizedFunc hazCurveSite1 = new ArbitrarilyDiscretizedFunc();
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
		Site site1 = new Site(new Location(38.000,-122.000));
		site1.addParameter(sadighSiteType);
		expectedResults.put(site1, hazCurveSite1);

		
		ArbitrarilyDiscretizedFunc hazCurveSite2 = new ArbitrarilyDiscretizedFunc();
		hazCurveSite2.set(Math.log(0.001), 3.87E-02);
		hazCurveSite2.set(Math.log(0.01), 1.82E-02);
		hazCurveSite2.set(Math.log(0.05), 2.96E-03);
		hazCurveSite2.set(Math.log(0.1), 9.21E-04);
		hazCurveSite2.set(Math.log(0.15), 3.59E-04);
		hazCurveSite2.set(Math.log(0.2), 1.31E-04);
		hazCurveSite2.set(Math.log(0.25), 4.76E-05);
		hazCurveSite2.set(Math.log(0.3), 1.72E-05);
		hazCurveSite2.set(Math.log(0.35), 5.37E-06);
		hazCurveSite2.set(Math.log(0.4), 1.18E-06);
		Site site2 = new Site(new Location(37.550,-122.000));
		site2.addParameter(sadighSiteType);
		expectedResults.put(site2, hazCurveSite2);

		ArbitrarilyDiscretizedFunc hazCurveSite3 = new ArbitrarilyDiscretizedFunc();
		hazCurveSite3.set(Math.log(0.001), 3.87E-02);
		hazCurveSite3.set(Math.log(0.01), 9.32E-03);
		hazCurveSite3.set(Math.log(0.05), 1.39E-03);
		hazCurveSite3.set(Math.log(0.1), 4.41E-04);
		hazCurveSite3.set(Math.log(0.15), 1.76E-04);
		hazCurveSite3.set(Math.log(0.2), 6.47E-05);
		hazCurveSite3.set(Math.log(0.25), 2.27E-05);
		hazCurveSite3.set(Math.log(0.3), 8.45E-06);
		hazCurveSite3.set(Math.log(0.35), 2.66E-06);
		hazCurveSite3.set(Math.log(0.4), 5.84E-07);
		Site site3 = new Site(new Location(37.099,-122.000));
		site3.addParameter(sadighSiteType);
		expectedResults.put(site3, hazCurveSite3);

		ArbitrarilyDiscretizedFunc hazCurveSite4 = new ArbitrarilyDiscretizedFunc();
		hazCurveSite4.set(Math.log(0.001), 3.83E-02);
		hazCurveSite4.set(Math.log(0.01), 5.33E-03);
		hazCurveSite4.set(Math.log(0.05), 1.25E-04);
		hazCurveSite4.set(Math.log(0.1), 1.63E-06);
		Site site4 = new Site(new Location(36.874,-122.000));
		site4.addParameter(sadighSiteType);
		expectedResults.put(site4, hazCurveSite4);
		
		return expectedResults;
	}

}
