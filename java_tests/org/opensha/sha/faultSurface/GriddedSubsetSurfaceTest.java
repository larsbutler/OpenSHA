package org.opensha.sha.faultSurface;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.opensha.commons.geo.Location;
import org.opensha.commons.geo.LocationUtils;


public class GriddedSubsetSurfaceTest {
	
	private static final double TOLERANCE = 1e-5;
	
	/**
	 * Check the getSurfaceCentre method for 
	 * sub surface. Creates first evenly discretized
	 * surface and then extract GriddedSubsetSurface.
	 */
	@Test
	public void CheckGetSurfaceCentre(){
		FaultTrace faultTrace = new FaultTrace("");
		faultTrace.add( new Location(0.0, +	1.0, 5.0));
		faultTrace.add( new Location(0.0, -1.0, 5.0));
		double aveDip = 90.0;
		double upperSeismogenicDepth = 5.0;
		double lowerSeismogenicDepth = 20.0;
		double gridSpacing = 1.0;
		StirlingGriddedSurface surf = new StirlingGriddedSurface(faultTrace,
				aveDip, upperSeismogenicDepth, lowerSeismogenicDepth,
				gridSpacing);
		double subSurfaceLength = 10.0;
		double subSurfaceWidth = 5.0;
		double subSurfaceOffset = 1.0;
		int n = 0;
		GriddedSubsetSurface subSurf =
			 surf.getNthSubsetSurface(subSurfaceLength, subSurfaceWidth, subSurfaceOffset, n);
		Location upperLeftCorner = subSurf.get(0, 0);
		Location upperRightCorner = subSurf.get(0, subSurf.getNumCols() - 1);
		Location bottomLeftCorner = subSurf.get(subSurf.getNumRows() - 1, 0);
		Location bottomRightCorner = subSurf.get(subSurf.getNumRows()-1, subSurf.getNumCols()-1);
		Location surfaceCentre = subSurf.getSurfaceCentre();
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
