package org.opensha.sha.earthquake.rupForecastImpl;

import java.util.ArrayList;

import org.opensha.commons.calc.magScalingRelations.MagAreaRelationship;
import org.opensha.commons.calc.magScalingRelations.MagLengthRelationship;
import org.opensha.commons.calc.magScalingRelations.MagScalingRelationship;
import org.opensha.commons.geo.Location;
import org.opensha.commons.geo.LocationUtils;
import org.opensha.commons.geo.LocationVector;
import org.opensha.sha.earthquake.EqkRupture;
import org.opensha.sha.earthquake.FocalMechanism;
import org.opensha.sha.faultSurface.FaultTrace;
import org.opensha.sha.faultSurface.StirlingGriddedSurface;

/**
 * <p>s
 * Title: Pnt2Lne
 * </p>
 * <p>
 * Description: 
 * </p>
 * </UL>
 * <p>
 * 
 * @author
 * @version 1.0
 */

//public class Pnt2Lne extends EqkRupture implements java.io.Serializable {
public class Pnt2Lne {
	// 
	double mag;
	ArrayList<EqkRupture> eqkRup;
	FocalMechanism focalMechanism;
	double lowerSeisDepth;
	double topRupDepth; 
	double aspectRatio;
	double dip;
	Location location;
	int numStrikes;
	double firstStrike;
	MagScalingRelationship magSclRel;
	//private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param location
	 * @param magnitude
	 * @param focalMech
	 * @param topRupDepth
	 * @param aspectRatio
	 * @param magSclRel
	 * @throws Exception
	 */
	public Pnt2Lne(
			Location location,
			double magnitude,
			FocalMechanism focalMech,
			double topRupDepth,
			double aspectRatio,
			MagScalingRelationship magSclRel) throws Exception {
		this.eqkRup = new ArrayList<EqkRupture>();
		this.location = location;
		this.mag = magnitude;
		this.focalMechanism = focalMech;
		this.aspectRatio = aspectRatio;
		this.magSclRel = magSclRel;
		this.lowerSeisDepth = Double.NaN;
		this.aspectRatio = aspectRatio;
		this.numStrikes = 0;
	}
	
	/**
	 * 
	 * @param location
	 * @param magnitude
	 * @param focalMech
	 * @param topRupDepth
	 * @param aspectRatio
	 * @param magSclRel
	 * @param numStrikes
	 * @throws Exception
	 */
	public Pnt2Lne(
			Location location,
			double magnitude,
			FocalMechanism focalMech,
			double topRupDepth,
			double aspectRatio,
			MagScalingRelationship magSclRel,
			int numStrikes) throws Exception {
		this.eqkRup = new ArrayList<EqkRupture>();
		this.location = location;
		this.mag = magnitude;
		this.focalMechanism = focalMech;
		this.aspectRatio = aspectRatio;
		this.magSclRel = magSclRel;
		this.lowerSeisDepth = Double.NaN;
		this.aspectRatio = aspectRatio;
		this.numStrikes = numStrikes;
		this.firstStrike = -1;
	}
	
	/**
	 * 
	 * @param location
	 * @param magnitude
	 * @param focalMech
	 * @param topRupDepth
	 * @param aspectRatio
	 * @param magSclRel
	 * @param numStrikes
	 * @param firstStrike
	 * @throws Exception
	 */
	
	public Pnt2Lne(
			Location location,
			double magnitude,
			FocalMechanism focalMech,
			double topRupDepth,
			double aspectRatio,
			MagScalingRelationship magSclRel,
			int numStrikes,
			double firstStrike) throws Exception {
		this.eqkRup = new ArrayList<EqkRupture>();
		this.location = location;
		this.mag = magnitude;
		this.focalMechanism = focalMech;
		this.aspectRatio = aspectRatio;
		this.magSclRel = magSclRel;
		this.lowerSeisDepth = Double.NaN;
		this.aspectRatio = aspectRatio;
		this.numStrikes = numStrikes;
		this.firstStrike = firstStrike;
	}
	
	/**
	 * This method creates the ruptures 
	 * @throws Exception 
	 */
    public ArrayList<EqkRupture> makeRuptures() throws Exception {
    	// Init some variables
    	ArrayList<Double> strikeArr;
    	double angle = 0;
    	double tmpStrike;
    	double oldStrike;
    	double rupBottom;
    	double rupLength;
    	// Set the dip of the rupture
    	if (!Double.isNaN(focalMechanism.getDip())){
    		dip = focalMechanism.getDip();
    	} else {
    		dip = 90.0;
    	}
    	// Get location
        Location loc = new Location(
        		location.getLatitude(),
        		location.getLongitude(),
        		topRupDepth);
        // Compute rupture length
        Double[] rupData = getRupLength(
        		mag, 
        		topRupDepth, 
        		aspectRatio,
        		magSclRel,
        		lowerSeisDepth);
        rupLength = rupData[0];
        rupBottom = rupData[1];
        // Check if the focalMech array contains only focal mechanisms with 
        // specified strike values
        strikeArr = new ArrayList<Double>(); 
        if (!Double.isNaN(focalMechanism.getStrike()))
        	strikeArr.add(focalMechanism.getStrike());
        // Check that the number of Focal Mechanisms defined in the array 
    	// If strike is null we use one out of three alternative options 
    	if (numStrikes == 0 && strikeArr.size() < 1){
    		// Single strike with random orientation
    		strikeArr.add((Math.random() - 0.5) * 180.0);
    	} else if (numStrikes == 0 && strikeArr.size() == 1){
    		// Nothing to do since the strike value is already in the strikeArr
    	} else if (numStrikes == 2 && strikeArr.size() < 1){
    		// Creates two perpendicular ruptures: in case the value of the 
    		// first strike is lower than 0, this value is taken by sampling 
    		// randomly the interval [0:360] 
    		if (firstStrike > 0){
    			strikeArr.add(firstStrike);
    		} else {
    			firstStrike = (Math.random() - 0.5) * 180.0;
    			strikeArr.add(firstStrike);
    		}
    		// Compute the second strike
    		double secondStrike = firstStrike+90.0;
    		if (secondStrike > 360) secondStrike = secondStrike - 360;
    		strikeArr.add(secondStrike);
    		 		
//    		System.out.printf(" 1st %5.2f  2nd %5.2f \n",
//    				strikeArr.get(0), strikeArr.get(1));
    				
    	} else if (numStrikes > 2 && strikeArr.size() < 1){
    		// Creates evenly separated ruptures: in case the value of the 
    		// first strike is lower than 0, this value is taken sampling 
    		// randomly the interval [0:360] 
    		if (firstStrike > 0){
    			strikeArr.add(firstStrike);
    		} else {
    			strikeArr.add((Math.random() - 0.5) * 180.0);
    		}
    		// Compute the angle separating the ruptures
    		angle = 360 / numStrikes;
    		// 
    		oldStrike = firstStrike;
    		for (int i = 0; i < numStrikes-1; i++){
        		tmpStrike = oldStrike+angle;
        		if (tmpStrike > 360) tmpStrike = tmpStrike - 360;
        		strikeArr.add(tmpStrike);
        		oldStrike = tmpStrike;
    		}
    	} else {
    		throw new IllegalArgumentException (
            	"This parameters combination is not supported " +
            	"num strikes:"+numStrikes+" strArr size:"+strikeArr.size());
    	}    				
    	// Creates all the ruptures 
    	for (Double stk : strikeArr){	
    		//
    		LocationVector dir =
    			new LocationVector(stk,rupLength/2, 0.0);
    		Location loc1 = LocationUtils.location(loc, dir);
    		dir.setAzimuth(stk-180);
    		Location loc2 = LocationUtils.location(loc, dir);
    		
//    		System.out.printf(" coo %5.2f %5.2f %5.2f %5.2f \n",
//    				loc1.getLongitude(),
//    				loc1.getLatitude(),
//    				loc2.getLongitude(),
//    				loc2.getLatitude());
    		
    		// Creating the fault trace
    		FaultTrace fltTrace = new FaultTrace(null);
    		fltTrace.add(loc1);
    		fltTrace.add(loc2);
    		
//    		System.out.printf(" dip %5.2f topR %5.2f botR %5.2f \n",
//    				dip,topRupDepth,rupBottom);
    		
    		// Create the rupture surface
            StirlingGriddedSurface surf =
            	new StirlingGriddedSurface(fltTrace,dip,topRupDepth,
            			rupBottom,0.5);       
            // Create the rupture
            EqkRupture rup = new EqkRupture();
            rup.setMag(mag);
            rup.setRuptureSurface(surf);
            eqkRup.add(rup);
    	}
    	return eqkRup;
    }
    
    /**
     * This method computes the length of the (supposed vertical) rupture 
     * provided a magnitude scaling relationship.
     * @throws Exception  
     */
    private Double[] getRupLength(
    		double mag, 
    		double topRupDepth,
    		double aspectRatio,
    		MagScalingRelationship magSclRel,
    		double lowerSeisDepth) throws Exception {
    	return getRupLength(mag,topRupDepth,aspectRatio,magSclRel,
    		lowerSeisDepth,90.0);
    }
    /**
     * This method computes the length of the rupture provided a magnitude 
     * scaling relationship. The aspect ratio is used to calculate the rupture 
     * width and the depth to the rupture bottom. If the lowerSeisDepth is not
     * NaN, this method checks that the ruptures to not extend beyond otherwise
     * no check is completed.  
     *   
     * @throws Exception  
     */
    private Double[] getRupLength(double mag, 
    		double topRupDepth,
    		double aspectRatio,
    		MagScalingRelationship magSclRel,
    		double lowerSeisDepth,
    		double dip) throws Exception {
    	
    	double rupLength;
    	double rupWidth;
    	double bottomDepth;
    	Double[] rupData = new Double[2];
	
    	// Calculating the rupture length
    	if (magSclRel instanceof MagAreaRelationship) { 
    		double tmp = magSclRel.getMedianScale(mag)*aspectRatio;
    		rupLength = Math.pow(tmp,0.5); 
    	} else if (magSclRel instanceof MagLengthRelationship) {
    		rupLength = magSclRel.getMedianScale(mag);
    	} else {
    		throw new IllegalArgumentException(
            "This MagScaling relationship is not supported");
    	}
    	// Computing rupture width
    	rupWidth = rupLength / aspectRatio;
		// Computing the rupture bottom depth  
		bottomDepth = topRupDepth + rupWidth * Math.sin(dip/180*Math.PI);
		// Checking the position of the rupture bottom wrt the lower seismogenic 
		// depth
		if (Double.isNaN(lowerSeisDepth)) {
			lowerSeisDepth = bottomDepth + 1.0;
		} else {
			if (bottomDepth > lowerSeisDepth)
				throw new Exception("The rupture extends below the " +
				"lower seismogenic depth");
		}
		// Return data describing the rupture: length and bottomDepth
		rupData[0] = rupLength;
		rupData[1] = bottomDepth;
    	return rupData;
    }
    
}

