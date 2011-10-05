package org.opensha.sha.faultSurface;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.opensha.commons.geo.Location;
import org.opensha.commons.geo.LocationUtils;
import org.opensha.commons.geo.LocationVector;


public class EvenlyGriddedSurfaceTest {
	
	// in km
	private static final double TOLERANCE = 1e-5;
	
	/** check that surface centre is equidistant 
	 * with respect to rupture corners (for a plain rupture).
	 */
	@Test
	public void checkGetSurfaceCentre(){
		
		// with gridSpacing = 4 km, numRows and numCols are odd.
		double gridSpacing = 4.0;
		checkSurfaceCentreIsInTheMiddleOfRupture(gridSpacing);

		// with gridSpacing = 3 km, numRows is even and numCols is odd.
		gridSpacing = 3.0;
		checkSurfaceCentreIsInTheMiddleOfRupture(gridSpacing);
		
		// with gridSpacing = 2 km, numRows is odd and numCols is even.
		gridSpacing = 2.0;
		checkSurfaceCentreIsInTheMiddleOfRupture(gridSpacing);
		
		// with gridSpacing = 0.6 km, numRows and numCols are even.
		gridSpacing = 0.6;
		checkSurfaceCentreIsInTheMiddleOfRupture(gridSpacing);
	}
	
	private void checkSurfaceCentreIsInTheMiddleOfRupture(double gridSpacing){
		
		FaultTrace faultTrace = new FaultTrace("");
		faultTrace.add( new Location(0.0, +	1.0, 5.0));
		faultTrace.add( new Location(0.0, -1.0, 5.0));
		double aveDip = 90.0;
		double upperSeismogenicDepth = 5.0;
		double lowerSeismogenicDepth = 20.0;
		
		StirlingGriddedSurface surf = new StirlingGriddedSurface(faultTrace,
				aveDip, upperSeismogenicDepth, lowerSeismogenicDepth,
				gridSpacing);
		Location upperLeftCorner = surf.get(0, 0);
		Location upperRightCorner = surf.get(0, surf.getNumCols() - 1);
		Location bottomLeftCorner = surf.get(surf.getNumRows() - 1, 0);
		Location bottomRightCorner = surf.get(surf.getNumRows()-1, surf.getNumCols()-1);
		Location surfaceCentre = surf.getSurfaceCentre();
		double d1 = Math.sqrt(Math.pow(LocationUtils.
			horzDistance(surfaceCentre, upperLeftCorner),2) +
			Math.pow(LocationUtils.
			vertDistance(surfaceCentre, upperLeftCorner),2));
		double d2 = Math.sqrt(Math.pow(LocationUtils.
			horzDistance(surfaceCentre, upperRightCorner),2) +
			Math.pow(LocationUtils.
			vertDistance(surfaceCentre, upperRightCorner),2));
		double d3 = Math.sqrt(Math.pow(LocationUtils.
			horzDistance(surfaceCentre, bottomLeftCorner),2) +
			Math.pow(LocationUtils.
			vertDistance(surfaceCentre, bottomLeftCorner),2));
		double d4 = Math.sqrt(Math.pow(LocationUtils.
			horzDistance(surfaceCentre, bottomRightCorner),2) +
			Math.pow(LocationUtils.
			vertDistance(surfaceCentre, bottomRightCorner),2));
		assertEquals(d1,d2,TOLERANCE);
		assertEquals(d1,d3,TOLERANCE);
		assertEquals(d1,d4,TOLERANCE);
		assertEquals(d2,d3,TOLERANCE);
		assertEquals(d2,d4,TOLERANCE);
		assertEquals(d3,d4,TOLERANCE);
	}
}
