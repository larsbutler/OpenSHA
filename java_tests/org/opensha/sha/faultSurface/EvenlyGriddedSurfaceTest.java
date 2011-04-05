package org.opensha.sha.faultSurface;

import org.junit.Test;
import org.opensha.commons.geo.Location;
import org.opensha.commons.geo.LocationUtils;


public class EvenlyGriddedSurfaceTest {
	
	@Test
	public void checkGetSurfaceCentre(){
		FaultTrace faultTrace = new FaultTrace("");
		faultTrace.add( new Location(0.0, +1.0, 5.0));
		faultTrace.add( new Location(0.0, -1.0, 5.0));
		double aveDip = 90.0;
		double upperSeismogenicDepth = 5.0;
		double lowerSeismogenicDepth = 20.0;
		double gridSpacing = 1.0;
		StirlingGriddedSurface surf = new StirlingGriddedSurface(faultTrace,
				aveDip, upperSeismogenicDepth, lowerSeismogenicDepth,
				gridSpacing);
		Location upperLeftCorner = surf.get(0, 0);
		Location upperRightCorner = surf.get(0, surf.getNumCols() - 1);
		Location bottomLeftCorner = surf.get(surf.getNumRows() - 1, 0);
		Location bottomRightCorner = surf.get(surf.getNumRows()-1, surf.getNumCols()-1);
		Location surfaceCentre = surf.getSurfaceCentre();
		System.out.println("Number of rows: "+surf.getNumRows());
		System.out.println("Number of columns: "+surf.getNumCols());
		System.out.println("distance: "+LocationUtils.linearDistance(surfaceCentre, upperLeftCorner));
		System.out.println("distance: "+LocationUtils.linearDistance(surfaceCentre, upperRightCorner));
		System.out.println("distance: "+LocationUtils.linearDistance(surfaceCentre, bottomLeftCorner));
		System.out.println("distance: "+LocationUtils.linearDistance(surfaceCentre, bottomRightCorner));
	}

}
