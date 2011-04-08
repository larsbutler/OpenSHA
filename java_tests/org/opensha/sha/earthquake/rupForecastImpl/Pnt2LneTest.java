package org.opensha.sha.earthquake.rupForecastImpl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;
import org.opensha.commons.calc.magScalingRelations.magScalingRelImpl.
	WC1994_MagAreaRelationship;
import org.opensha.commons.geo.Location;
import org.opensha.commons.geo.LocationUtils;
import org.opensha.sha.earthquake.FocalMechanism;
import org.opensha.sha.earthquake.EqkRupture;

public class Pnt2LneTest {

	static boolean D = true;
	
	/**
	 * This is to test that the Pnt2Lne class given a focal mechanism and a 
	 * magnitude creates the correct rupture.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateRupturesWithSpecifiedStrike() throws Exception {
		
		if (D) System.out.println("--- RUNNING " +
				"testCreateRupturesWithSpecifiedStrike");
		
		double strike = 170.0;
		double dip = 90.0; 
		double rake = -90.0;
		double magnitude = 6.0;
		double topRupDepth = 5.0;
		double aspectRatio = 2.0;
		Location location = new Location(45.00,10.00);
		WC1994_MagAreaRelationship magScalingRelationship = 
			new WC1994_MagAreaRelationship();
		ArrayList<EqkRupture> rupLis = new ArrayList<EqkRupture>();
		FocalMechanism focalMech = new FocalMechanism(strike,dip,rake);
		// Create the point to line object
		Pnt2Lne pnt2Lne = new Pnt2Lne(
				location,
				magnitude,
				focalMech,
				topRupDepth,
				aspectRatio,
				magScalingRelationship);
		// Create all the ruptures
		rupLis = pnt2Lne.makeRuptures();
		// Checking the number of ruptures
		assertEquals(rupLis.size(),1,1e-5);
		// Checking that the rupture magnitude is the correct one
		assertEquals(rupLis.get(0).getMag(),magnitude,1e-5);
		// Checking 
		double area = rupLis.get(0).getRuptureSurface().getSurfaceArea();
		double areaMsr = magScalingRelationship.getMedianScale(magnitude);
		assertEquals(areaMsr,area,5);
	}
	
	/**
	 * This is to test the creation of two perpendicular ruptures. The strike of
	 * the first rupture in this case is randomly defined.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreate2PerpendicularRuptures() throws Exception {
		
		if (D) System.out.println("--- RUNNING " +
				"testCreate2PerpendicularRuptures");
		
		double strike = Double.NaN; double dip = 45.0; double rake = -90.0;
		FocalMechanism focalMech = new FocalMechanism(strike,dip,rake);
		double magnitude   = 6.0;
		double topRupDepth = 5.0;
		double aspectRatio = 2.0;
		int numStrikes     = 2;
		double firstStrike = 359.0;
		Location location  = new Location(45.00,10.00);
		WC1994_MagAreaRelationship magScalingRelationship = 
			new WC1994_MagAreaRelationship();
		ArrayList<EqkRupture> rupLis = new ArrayList<EqkRupture>();
		
		// Create the point to line object
		Pnt2Lne pnt2Lne = new Pnt2Lne(
				location,
				magnitude,
				focalMech,
				topRupDepth,
				aspectRatio,
				magScalingRelationship,
				numStrikes);
		// Create all the ruptures
		rupLis = pnt2Lne.makeRuptures();
		// Checking the number of ruptures
		assertEquals(rupLis.size(),2,1e-5);
		// Checking that the two ruptures are perpendicular
		int nCol = rupLis.get(0).getRuptureSurface().getNumCols();
		Location loc1 = rupLis.get(0).getRuptureSurface().getLocation(0,0);
		Location loc2 = rupLis.get(0).getRuptureSurface().getLocation(0,nCol-1);
		double strike1 = LocationUtils.azimuth(loc2,loc1);
		loc1 = rupLis.get(1).getRuptureSurface().getLocation(0,0);
		loc2 = rupLis.get(1).getRuptureSurface().getLocation(0,nCol-1);
		double strike2 = LocationUtils.azimuth(loc2,loc1);
		double diff;
		if (Math.abs(strike1-strike2) < 180) {
			diff = Math.abs(strike1-strike2);
		} else {
			if (strike1 > strike2){
				diff = (360-strike1) + strike2;
			} else {
				diff = (360-strike2) + strike1;
			}
		}
		assertEquals(90.0,diff,1);
		
		// Create the point to line object - In this case we specify the value 
		// of the first strike and we create tests accordingly
		pnt2Lne = new Pnt2Lne(
				location,
				magnitude,
				focalMech,
				topRupDepth,
				aspectRatio,
				magScalingRelationship,
				numStrikes,
				firstStrike);
		// Create all the ruptures
		rupLis = pnt2Lne.makeRuptures();
		// Checking the number of ruptures
		assertEquals(rupLis.size(),2,1e-5);
		// Checking the strike of the first rupture
		nCol = rupLis.get(0).getRuptureSurface().getNumCols();
		loc1 = rupLis.get(0).getRuptureSurface().getLocation(0,0);
		loc2 = rupLis.get(0).getRuptureSurface().getLocation(0,nCol-1);
		// TODO 
		// I don't understand why the location at (0,0) is at the farthest 
		// extreme of the rupture along the strike direction
		strike1 = LocationUtils.azimuth(loc2,loc1);
		assertEquals(firstStrike,
				strike1,
				1.0);
		// Checking the strike of the second rupture
		loc1 = rupLis.get(1).getRuptureSurface().getLocation(0,0);
		loc2 = rupLis.get(1).getRuptureSurface().getLocation(0,nCol-1);
		// TODO 
		// I don't understand why the location at (0,0) is at the farthest 
		// extreme of the rupture along the strike direction
		strike2 = LocationUtils.azimuth(loc2,loc1);
		double secondStrike = firstStrike+90.0;
		if (secondStrike > 360.0) secondStrike -= 360.0;
		assertEquals(secondStrike,
				strike2,
				1.0);
	}
		
	@Test(expected = IllegalArgumentException.class)
	public void testCreate2PerpendicularRupturesError() throws Exception {
		
		if (D) System.out.println("--- RUNNING " +
				"testCreate2PerpendicularRupturesError");
		
		double strike = 0.0; double dip = 90.0; double rake = -90.0;
		FocalMechanism focalMech = new FocalMechanism(strike,dip,rake);
		double magnitude = 6.0;
		double topRupDepth = 5.0;
		double aspectRatio = 2.0;
		int numStrikes = 2;
		Location location = new Location(45.00,10.00);
		WC1994_MagAreaRelationship magScalingRelationship = 
			new WC1994_MagAreaRelationship();
		ArrayList<EqkRupture> rupLis = new ArrayList<EqkRupture>();
		// Create the point to line object
		Pnt2Lne pnt2Lne = new Pnt2Lne(
				location,
				magnitude,
				focalMech,
				topRupDepth,
				aspectRatio,
				magScalingRelationship,
				numStrikes);
		// Create all the ruptures
		rupLis = pnt2Lne.makeRuptures();   
	}
	
	@Test
	public void testCreateNRadialRuptures() throws Exception {
		if (D) System.out.println("--- RUNNING " +
		"testCreateNRadialRuptures");
		// 
		double strike = Double.NaN; double dip = 45.0; double rake = -90.0;
		FocalMechanism focalMech = new FocalMechanism(strike,dip,rake);
		double magnitude   = 6.0;
		double topRupDepth = 5.0;
		double aspectRatio = 2.0;
		int numStrikes     = 4;
		Location location  = new Location(45.00,10.00);	
		WC1994_MagAreaRelationship magScalingRelationship = 
			new WC1994_MagAreaRelationship();
		ArrayList<EqkRupture> rupLis = new ArrayList<EqkRupture>();
		// Create the point to line object
		Pnt2Lne pnt2Lne = new Pnt2Lne(
				location,
				magnitude,
				focalMech,
				topRupDepth,
				aspectRatio,
				magScalingRelationship,
				numStrikes);
		// Create all the ruptures
		rupLis = pnt2Lne.makeRuptures();  
		// Checking the number of ruptures
		assertEquals(4,rupLis.size(),1e-5);
	}
	
	
}
