package org.opensha.sha.earthquake.rupForecastImpl;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import org.opensha.commons.calc.magScalingRelations.MagScalingRelationship;
import org.opensha.commons.calc.magScalingRelations.magScalingRelImpl.
	WC1994_MagAreaRelationship;
import org.opensha.commons.geo.GriddedRegion;
import org.opensha.commons.geo.Location;
import org.opensha.commons.geo.Region;
import org.opensha.sha.earthquake.FocalMechanism;
import org.opensha.sha.earthquake.ProbEqkRupture;
import org.opensha.sha.earthquake.rupForecastImpl.MDAreaSource;
import org.opensha.sha.magdist.IncrementalMagFreqDist;

public class MDAreaSourceTest {
	
	Region reg; 
	double gridResolution = 0.1;
	IncrementalMagFreqDist mfd;
	FocalMechanism[] focalMechanismArr;
	double[] focalMechanismWeight;
	double[] depthVector;
	double[] magVector;
	double[][] depthMagMatrix;
    double duration = 50.0; 
    double minMag = 4.0;
    double aspectRatio;
    MagScalingRelationship magScalRel;
    int numStrikes;
    double firstStrike;
    
    /*
     * 
     */
    @Test(expected = IllegalArgumentException.class)
	public void Test_Size_FocMec_and_FocMecWeights() throws Exception {
    	System.out.println("TEST : Test_Size_FocMec_and_FocMecWeights");
		double[] magVec03;
		double[] focMechWei;
		double[] depVec03;
		double[][] dMMtx;
		FocalMechanism[] focMecArr;
		//
		IncrementalMagFreqDist mfd03;
		// Create a region
		reg = new Region(new Location(45.0,10.0), new Location(45.805,10.005));
        // Define some of the MDAreaSource Parameters
		double aGR = 5.0;
        double bGR = 1.0;
        double minM = 5.05;
        double maxM = 6.05;
        double mWidth = 0.1;
        double mmid;
        // Compute the number of intervals in the FMD
        int nin = (int) Math.round((maxM-minM)/mWidth)+1;        
        // Define the Frequency-Magnitude distribution
        mfd03 = new IncrementalMagFreqDist(minM,maxM,nin);
        for (int i = 0; i < mfd03.getNum(); i++){
        	mmid = mfd03.getX(i);
        	mfd03.set(i,Math.pow(10.0, aGR-bGR*(mmid-mfd03.getDelta()/2)) - 
        			Math.pow(10.0,aGR-bGR*(mmid+mfd03.getDelta()/2)));
        }
        // Define the focal mechanism array
        focMecArr = new FocalMechanism[2];
        focMecArr[0] = new FocalMechanism(0.0,90.0,-90.0);
        focMecArr[1] = new FocalMechanism(90.0,90.0,0.0);
        // Define the focal mechanism weights vector
        focMechWei = new double[3];
        focMechWei[0] = 0.3;
        focMechWei[1] = 0.7;
        focMechWei[2] = 0.7;
        // Define the depth vector
        depVec03 = new double[2];
        depVec03[0] =  5.0;
        depVec03[1] = 10.0;
        // Define the magnitude vector
        magVec03 = new double[4];
        magVec03[0] = 5.15;
        magVec03[1] = 5.30;
        magVec03[2] = 5.70;
        magVec03[3] = 6.50; 
        // Define the magDep matrix
        dMMtx = new double[4][2];
        dMMtx[0][0] = 0.6; // mag 0 - dep 0
        dMMtx[0][1] = 0.4; // mag 0 - dep 1
        dMMtx[1][0] = 0.7; // mag 1 - dep 0 
        dMMtx[1][1] = 0.3; // mag 1 - dep 1
        dMMtx[2][0] = 0.8; // mag 2 - dep 0
        dMMtx[2][1] = 0.2; // mag 2 - dep 1
        dMMtx[3][0] = 0.9; // mag 3 - dep 0
        dMMtx[3][1] = 0.1; // mag 3 - dep 1
        // 
        aspectRatio = 2.0;
        magScalRel = new WC1994_MagAreaRelationship();
        numStrikes = 2;
        firstStrike = Double.NaN;
        // Instantiate a MDAreaSource source 
		new MDAreaSource (
        	reg, 
        	gridResolution,
        	mfd03,
        	focMecArr,
        	focMechWei,
        	depVec03,
        	magVec03,
        	dMMtx,
        	duration,
        	minMag,
            aspectRatio,
            magScalRel,
            numStrikes,
            firstStrike
    	);	
    }
    
    /*
     * 
     */
    @Test(expected = IllegalArgumentException.class)
	public void Test_FocMecWeights_Sum_To_Unity() throws Exception {
    	System.out.println("TEST : Test_FocMecWeights_Sum_To_Unity");
		double[] magVec03;
		double[] focMechWei;
		double[] depVec03;
		double[][] dMMtx;
		FocalMechanism[] focMecArr;
		//
		IncrementalMagFreqDist mfd03;
		// Create a region
		reg = new Region(new Location(45.0,10.0), new Location(45.805,10.005));
        // Define some of the MDAreaSource Parameters
		double aGR = 5.0;
        double bGR = 1.0;
        double minM = 5.05;
        double maxM = 6.05;
        double mWidth = 0.1;
        double mmid;
        // Compute the number of intervals in the FMD
        int nin = (int) Math.round((maxM-minM)/mWidth)+1;        
        // Define the Frequency-Magnitude distribution
        mfd03 = new IncrementalMagFreqDist(minM,maxM,nin);
        for (int i = 0; i < mfd03.getNum(); i++){
        	mmid = mfd03.getX(i);
        	mfd03.set(i,Math.pow(10.0, aGR-bGR*(mmid-mfd03.getDelta()/2)) - 
        			Math.pow(10.0,aGR-bGR*(mmid+mfd03.getDelta()/2)));
        }
        // Define the focal mechanism array
        focMecArr = new FocalMechanism[2];
        focMecArr[0] = new FocalMechanism(0.0,90.0,-90.0);
        focMecArr[1] = new FocalMechanism(90.0,90.0,0.0);
        // Define the focal mechanism weights vector
        focMechWei = new double[2];
        focMechWei[0] = 0.3;
        focMechWei[1] = 0.5;
        // Define the depth vector
        depVec03 = new double[2];
        depVec03[0] =  5.0;
        depVec03[1] = 10.0;
        // Define the magnitude vector
        magVec03 = new double[4];
        magVec03[0] = 5.15;
        magVec03[1] = 5.30;
        magVec03[2] = 5.70;
        magVec03[3] = 6.50; 
        // Define the magDep matrix
        dMMtx = new double[4][2];
        dMMtx[0][0] = 0.6; // mag 0 - dep 0
        dMMtx[0][1] = 0.4; // mag 0 - dep 1
        dMMtx[1][0] = 0.7; // mag 1 - dep 0 
        dMMtx[1][1] = 0.3; // mag 1 - dep 1
        dMMtx[2][0] = 0.8; // mag 2 - dep 0
        dMMtx[2][1] = 0.2; // mag 2 - dep 1
        dMMtx[3][0] = 0.9; // mag 3 - dep 0
        dMMtx[3][1] = 0.1; // mag 3 - dep 1
        // 
        aspectRatio = 2.0;
        magScalRel = new WC1994_MagAreaRelationship();
        numStrikes = 2;
        firstStrike = Double.NaN;
        // Instantiate a MDAreaSource source 
		new MDAreaSource (
        	reg, 
        	gridResolution,
        	mfd03,
        	focMecArr,
        	focMechWei,
        	depVec03,
        	magVec03,
        	dMMtx,
        	duration,
        	minMag,
            aspectRatio,
            magScalRel,
            numStrikes,
            firstStrike
    	);
    }
    
    /**
     * This is to test the FMD created with a MDAreaSource
     */
	@Test(expected = IllegalArgumentException.class)
	public void Test_MagWeights_Sum_To_Unity(){
		System.out.println("TEST : Test_MagWeights_Sum_To_Unity");
        // Initalize default parameters
        initParameters();
        // Change the depth magnitude matrix so as to generate the exception
        depthMagMatrix[0][0] = 0.9;
        // Instantiate a MDAreaSource 
        new MDAreaSource(
        	reg, 
        	gridResolution,
        	mfd,
        	focalMechanismArr,
        	focalMechanismWeight,
        	depthVector,
        	magVector,
        	depthMagMatrix,
        	duration,
        	minMag
    	); 
	}
    
    /**
     * This is to test the FMD created with a MDAreaSource
     */
	@Test(expected = IllegalArgumentException.class)
	public void Test_DepMtx_MagDepMtx_Consistency(){
		System.out.println("TEST : Test_DepMtx_MagDepMtx_Consistency");
        // Initalize default parameters
        initParameters();
		// Change the depth matrix
        depthVector    = new double[3];
        depthVector[0] =  5.0;
        depthVector[1] = 10.0;
        depthVector[2] = 15.0;
        // Instantiate a MDAreaSource 
        new MDAreaSource(
        	reg, 
        	gridResolution,
        	mfd,
        	focalMechanismArr,
        	focalMechanismWeight,
        	depthVector,
        	magVector,
        	depthMagMatrix,
        	duration,
        	minMag
    	); 
	}	
	
    /**
     * This is to test the FMD created with a MDAreaSource
     */
	@Test(expected = IllegalArgumentException.class)
	public void Test_MagMtx_MagDepMtx_Consistency(){
		System.out.println("TEST : Test_MagMtx_MagDepMtx_Consistency");
        // Initalize default parameters
        initParameters();
        // Define the magnitude vector
        magVector = new double[3];
        magVector[0] = 4.25;
        magVector[1] = 5.25;
        magVector[2] = 5.60;
        // Instantiate a MDAreaSource 
        new MDAreaSource(
        	reg, 
        	gridResolution,
        	mfd,
        	focalMechanismArr,
        	focalMechanismWeight,
        	depthVector,
        	magVector,
        	depthMagMatrix,
        	duration,
        	minMag
    	); 
	}
	
    /**
     * This is to test the number of ruptures created with a 
     * MDAreaSource
     */
	@Test
	public void Test_pointRupture_NumRup(){
		System.out.println("TEST : Test_pointRupture_NumRup");
		// Create a region - It contains a single point
		reg = new Region(new Location(45.0,10.0), new Location(45.005,10.005));
        // Define some of the MDAreaSource Parameters
		double aGR = 6.5;
        double bGR = 1.0;
        double minM = 5.05;
        double maxM = 6.05;
        double mmid;
        double tolerance = 1.0e-1;
        // Define the Frequency-Magnitude distribution
        mfd = new IncrementalMagFreqDist(minM,maxM,11);
        for(int i = 0; i < mfd.getNum(); i++){
        	mmid = mfd.getX(i);
        	mfd.set(i,Math.pow(10.0, aGR-bGR*(mmid-mfd.getDelta()/2)) - 
        			Math.pow(10.0,aGR-bGR*(mmid+mfd.getDelta()/2)));
        }
        // Instantiate a MDAreaSource
		MDAreaSource mdas = 
			instantiateMDAreaSource(reg,mfd);
		// Compute number of ruptures
		double nrup = 0.0;
		nrup = mfd.getNum() * mdas.depthVector.length * 
			mdas.focalMechanismWeight.length;		
		// Check
		assertEquals(nrup,mdas.getNumRuptures(),tolerance);
	}
	
    /**
     * This is to test the FMD created with a MDAreaSource
     */
	@Test
	public void Test_pointRupture_FMD(){
		System.out.println("TEST : Test_pointRupture_FMD");
		// 
		IncrementalMagFreqDist fmdCheck;
		IncrementalMagFreqDist depthCheck;
		int idx, idxR, idxC;
		// Create a region (2 points)
		reg = new Region(new Location(45.0,10.0), new Location(45.105,10.005));
        // Define some of the MDAreaSource Parameters
        double minM = 5.05;
        double maxM = 6.05;
        double tolerance = 1.0e-2;
        // Initalize default parameters
        initParameters();
        // Calculate number of rows and columns in the depthMagMatrix
        int nrow = depthMagMatrix.length;
        int ncol = depthMagMatrix[0].length;
        // Instantiate a MDAreaSource 
        MDAreaSource mdas = new MDAreaSource(
        	reg, 
        	gridResolution,
        	mfd,
        	focalMechanismArr,
        	focalMechanismWeight,
        	depthVector,
        	magVector,
        	depthMagMatrix,
        	duration,
        	minMag
    	);        
        // Create a discretized region covering the area polygon
		GriddedRegion gridReg = new GriddedRegion(reg, gridResolution, null);
		// Computing the number of ruptures - note that this methods to compute 
		// the number of rupture holds only 
		// if the magDepth distribution doesn't contain null weights. 
		int nrup = gridReg.getNodeCount() * 
			mfd.getNum() * 
			mdas.depthVector.length * 
			mdas.focalMechanismWeight.length;
		// Get the list of ruptures created 
		ArrayList<ProbEqkRupture> rupLis = mdas.getRuptureList();
		// Check the number of ruptures
		assertEquals(nrup,mdas.getNumRuptures(),tolerance);
		// Create a fmd to store the annual rates of occurrence
		fmdCheck = new IncrementalMagFreqDist(minM,maxM,11);
		// Create an evenly discretized function to check seismicity 
		// distribution with depth
		depthCheck = new IncrementalMagFreqDist(
				depthVector[0],
				depthVector[depthVector.length-1],
				depthVector.length);		
		// Counter
		
		Double[][] deCk = new Double[nrow][ncol];
		// Initialise deCk matrix
		for (int i=0; i<deCk.length; i++){
			for (int j=0; j<deCk[i].length; j++) deCk[i][j] = 0.0;
		}
		// Create the FMD
		for (ProbEqkRupture rup : rupLis){
			idx = fmdCheck.getXIndex(rup.getMag());
			fmdCheck.add(idx,rup.getMeanAnnualRate(duration));
			// Find the magnitude index
			idxR = 0; 
			while (magVector[idxR]<rup.getMag()){
				idxR += 1;
			}
			idxC = depthCheck.getXIndex(
					rup.getRuptureSurface().get(0,0).getDepth());
			deCk[idxR][idxC] += rup.getMeanAnnualRate(duration);
		}
		// Check the fmd consistency
		for (int i=0; i<fmdCheck.getNum(); i++){
			assertEquals(mfd.getY(i),fmdCheck.getY(i),tolerance);
		}
	}	
    /**
     * This is to test the FMD created with a MDAreaSource
     */
	@Test
	public void Test_finiteRupture_FMD_depthDist(){
		//
		System.out.println("TEST : Test_finiteRupture_FMD_depthDist");
		// 
		double[] magVec03;
		double[] focMechWei;
		double[] depVec03;
		double[][] dMMtx;
		FocalMechanism[] focMecArr;
		//
		IncrementalMagFreqDist mfd03;
		IncrementalMagFreqDist fmdCheck;
		IncrementalMagFreqDist depthCheck;
		int idx, idxR, idxC;
		// Create a region
		reg = new Region(new Location(45.0,10.0), new Location(45.805,10.005));
        // Define some of the MDAreaSource Parameters
		double aGR = 5.0;
        double bGR = 1.0;
        double minM = 5.05;
        double maxM = 6.05;
        double mWidth = 0.1;
        double mmid;
        double tolerance = 1.0e-2;
        // Compute the number of intervals in the FMD
        int nin = (int) Math.round((maxM-minM)/mWidth)+1;        
        // Define the Frequency-Magnitude distribution
        mfd03 = new IncrementalMagFreqDist(minM,maxM,nin);
        for (int i = 0; i < mfd03.getNum(); i++){
        	mmid = mfd03.getX(i);
        	mfd03.set(i,Math.pow(10.0, aGR-bGR*(mmid-mfd03.getDelta()/2)) - 
        			Math.pow(10.0,aGR-bGR*(mmid+mfd03.getDelta()/2)));
        }
        // Define the focal mechanism array
        focMecArr = new FocalMechanism[2];
        focMecArr[0] = new FocalMechanism(0.0,90.0,-90.0);
        focMecArr[1] = new FocalMechanism(90.0,90.0,0.0);
        // Define the focal mechanism weights vector
        focMechWei = new double[2];
        focMechWei[0] = 0.3;
        focMechWei[1] = 0.7;
        // Define the depth vector
        depVec03 = new double[2];
        depVec03[0] =  5.0;
        depVec03[1] = 10.0;
        // Define the magnitude vector
        magVec03 = new double[4];
        magVec03[0] = 5.15;
        magVec03[1] = 5.30;
        magVec03[2] = 5.70;
        magVec03[3] = 6.50; 
        // Define the magDep matrix
        int ncol = 2; int nrow = 4;
        dMMtx = new double[4][2];
        dMMtx[0][0] = 0.6; // mag 0 - dep 0
        dMMtx[0][1] = 0.4; // mag 0 - dep 1
        dMMtx[1][0] = 0.7; // mag 1 - dep 0 
        dMMtx[1][1] = 0.3; // mag 1 - dep 1
        dMMtx[2][0] = 0.8; // mag 2 - dep 0
        dMMtx[2][1] = 0.2; // mag 2 - dep 1
        dMMtx[3][0] = 0.9; // mag 3 - dep 0
        dMMtx[3][1] = 0.1; // mag 3 - dep 1
        // 
        aspectRatio = 2.0;
        magScalRel = new WC1994_MagAreaRelationship();
        numStrikes = 2;
        firstStrike = Double.NaN;
        // Instantiate a MDAreaSource source 
		MDAreaSource mdas = new MDAreaSource (
        	reg, 
        	gridResolution,
        	mfd03,
        	focMecArr,
        	focMechWei,
        	depVec03,
        	magVec03,
        	dMMtx,
        	duration,
        	minMag,
            aspectRatio,
            magScalRel,
            numStrikes,
            firstStrike
    	);
        // Create a discretized region covering the area polygon
		GriddedRegion gridReg = new GriddedRegion(reg, gridResolution, null);
		// Computing the number of ruptures - note that this methods to compute 
		// the number of rupture holds only 
		// if the magDepth distribution doesn't contain null weights. 
		int nrup = gridReg.getNodeCount() * 
			mfd03.getNum() * 
			mdas.depthVector.length * 
			mdas.focalMechanismWeight.length;
		// Get the list of ruptures created 
		ArrayList<ProbEqkRupture> rupLis = mdas.getRuptureList();
		// Check the number of ruptures
		assertEquals(nrup,mdas.getNumRuptures(),tolerance);
		// Create a fmd to store the annual rates of occurrence
		fmdCheck = new IncrementalMagFreqDist(minM,maxM,11);
		// Create an evenly discretized function to check seismicity 
		// distribution with depth
		depthCheck = new IncrementalMagFreqDist(
				depVec03[0],
				depVec03[depVec03.length-1],
				depVec03.length);		
		// Counter
		Double[][] deCk = new Double[nrow][ncol];
		// Initialise the depth-magnitude matrix
		for (int i=0; i<deCk.length; i++){
			for (int j=0; j<deCk[i].length; j++){
				deCk[i][j] = 0.0;
			}
		}
		// Create the FMD
		for (ProbEqkRupture rup : rupLis){
			idx = fmdCheck.getXIndex(rup.getMag());
			fmdCheck.add(idx,rup.getMeanAnnualRate(duration));
			// Find the magnitude index
			idxR = 0;
			while (magVec03[idxR]<rup.getMag()){
				idxR += 1;
			}
			idxC = depthCheck.getXIndex(
					rup.getRuptureSurface().get(0,0).getDepth());
			deCk[idxR][idxC] += rup.getMeanAnnualRate(duration);
		}
		// Check the fmd consistency
		for (int i=0; i<fmdCheck.getNum(); i++){
			assertEquals(fmdCheck.getY(i),mfd03.getY(i),tolerance);
		}
		// Check the computed magDepthDistribution array vs the one initially 
		// defined
		double sum;
		for (int i=0; i<deCk.length; i++){
			sum = 0.0;
			for (int j=0; j<deCk[i].length; j++) sum += deCk[i][j];
			for (int j=0; j<deCk[i].length; j++){
				assertEquals(dMMtx[i][j],deCk[i][j]/sum,tolerance);
			}
		}
	}
	/**
	 * This creates a MDAreaSource to be used for testing purposes
	 * @return
	 */
    private MDAreaSource instantiateMDAreaSource(
    		Region reg,
    		IncrementalMagFreqDist mfd)
    {
        // Define the focal mechanism array
        focalMechanismArr = new FocalMechanism[2];
        focalMechanismArr[0] = new FocalMechanism(0.0,90.0,-90.0);
        focalMechanismArr[1] = new FocalMechanism(90.0,90.0,0.0);
        // Define the focal mechanism weights vector
        focalMechanismWeight = new double[2];
        focalMechanismWeight[0] = 0.3;
        focalMechanismWeight[1] = 0.7;
        // Define the depth vector
        depthVector = new double[3];
        depthVector[0] =  5.0;
        depthVector[1] = 10.0;
        depthVector[2] = 15.0;
        // Define the magnitude vector
        magVector = new double[5];
        magVector[0] = 4.25;
        magVector[1] = 5.25;
        magVector[2] = 5.60;
        magVector[3] = 5.95;
        magVector[4] = 6.50;
        // Define the magDep matrix
        depthMagMatrix = new double[5][3];
        depthMagMatrix[0][0] = 0.50; // mag 0 - dep 0
        depthMagMatrix[0][1] = 0.30; // mag 0 - dep 1
        depthMagMatrix[0][2] = 0.20; // mag 0 - dep 2
        depthMagMatrix[1][0] = 0.60; // mag 1 - dep 0 
        depthMagMatrix[1][1] = 0.30; // mag 1 - dep 1
        depthMagMatrix[1][2] = 0.10; // mag 1 - dep 2
        depthMagMatrix[2][0] = 0.79; // mag 2 - dep 0
        depthMagMatrix[2][1] = 0.20; // mag 2 - dep 1
        depthMagMatrix[2][2] = 0.01; // mag 2 - dep 2
        depthMagMatrix[3][0] = 0.70; // mag 3 - dep 0
        depthMagMatrix[3][1] = 0.25; // mag 3 - dep 1
        depthMagMatrix[3][2] = 0.05; // mag 3 - dep 2
        depthMagMatrix[4][0] = 0.33; // mag 4 - dep 0
        depthMagMatrix[4][1] = 0.33; // mag 4 - dep 1
        depthMagMatrix[4][2] = 0.34; // mag 4 - dep 2
        // Instantiate a MDAreaSource 
		MDAreaSource mdas = new MDAreaSource(
        	reg, 
        	gridResolution,
        	mfd,
        	focalMechanismArr,
        	focalMechanismWeight,
        	depthVector,
        	magVector,
        	depthMagMatrix,
        	duration,
        	minMag
    	);
		// Get the list of ruptures created 
		return mdas;
    }
    
    private void initParameters() {
		// Create a region (2 points)
		reg = new Region(new Location(45.0,10.0), new Location(45.105,10.005));
        // Define some of the MDAreaSource Parameters
		double aGR  = 5.0;
        double bGR  = 1.0;
        double minM = 5.05;
        double maxM = 6.05;
        double mmid;
        // Define the Frequency-Magnitude distribution
        mfd = new IncrementalMagFreqDist(minM,maxM,11);
        for(int i = 0; i < mfd.getNum(); i++){
        	mmid = mfd.getX(i);
        	mfd.set(i,Math.pow(10.0,aGR-bGR*(mmid-mfd.getDelta()/2)) - 
        			  Math.pow(10.0,aGR-bGR*(mmid+mfd.getDelta()/2)));
        }
        // Define the focal mechanism array
        focalMechanismArr    = new FocalMechanism[2];
        focalMechanismArr[0] = new FocalMechanism(0.0,90.0,-90.0);
        focalMechanismArr[1] = new FocalMechanism(90.0,90.0,0.0);
        // Define the focal mechanism weights vector
        focalMechanismWeight    = new double[2];
        focalMechanismWeight[0] = 0.3;
        focalMechanismWeight[1] = 0.7;
        // Define the depth vector
        depthVector    = new double[2];
        depthVector[0] =  5.0;
        depthVector[1] = 10.0;
        // Define the magnitude vector
        magVector = new double[4];
        magVector[0] = 4.25;
        magVector[1] = 5.25;
        magVector[2] = 5.70;
        magVector[3] = 6.50; 
        // Define the magDep matrix
        int ncol = 2; int nrow = 4;
        depthMagMatrix = new double[nrow][ncol];
        depthMagMatrix[0][0] = 0.6; // mag 0 - dep 0
        depthMagMatrix[0][1] = 0.4; // mag 0 - dep 1
        depthMagMatrix[1][0] = 0.7; // mag 1 - dep 0 
        depthMagMatrix[1][1] = 0.3; // mag 1 - dep 1
        depthMagMatrix[2][0] = 0.8; // mag 2 - dep 0
        depthMagMatrix[2][1] = 0.2; // mag 2 - dep 1
        depthMagMatrix[3][0] = 0.9; // mag 3 - dep 0
        depthMagMatrix[3][1] = 0.1; // mag 3 - dep 1
    }
    
    

}
