package org.opensha.sha.earthquake.rupForecastImpl;

import java.util.ArrayList;

import org.opensha.commons.calc.magScalingRelations.MagScalingRelationship;
import org.opensha.commons.calc.magScalingRelations.magScalingRelImpl.WC1994_MagAreaRelationship;
import org.opensha.commons.data.Site;
import org.opensha.commons.geo.GriddedRegion;
import org.opensha.commons.geo.Location;
import org.opensha.commons.geo.LocationList;
import org.opensha.commons.geo.Region;
import org.opensha.sha.earthquake.EqkRupture;
import org.opensha.sha.earthquake.FocalMechanism;
import org.opensha.sha.earthquake.ProbEqkRupture;
import org.opensha.sha.earthquake.ProbEqkSource;
import org.opensha.sha.faultSurface.EvenlyGriddedSurfaceAPI;
import org.opensha.sha.magdist.IncrementalMagFreqDist;

/**
 * <p>
 * Title: MultiDepthAreaSource
 * </p>
 * <p>
 * Description: 
 * </p>
 * 
 * @author Marco Pagani
 * @version 1.0
 */

public class MDAreaSource extends ProbEqkSource implements
        java.io.Serializable {
	// Private variables
    private static final long serialVersionUID = 1L;
    private static String C = new String("MultiDepthAreaSource");
    private static String NAME = "Multi-depth Area Source";
    // Define fields
    Region reg;
    double gridResolution;
    IncrementalMagFreqDist magFreqDist;
    FocalMechanism[] focalMechanisms;
    double[][] depthMagMatrix;
    double[] depthVector;
    double[] magVector;
    double[] focalMechanismWeight;
    double duration;
    double minMag;
    double aspectRatio;
    boolean pointSources;
    int numStrikes;
	double firstStrike;
    MagScalingRelationship magScalRel;
    // 
    double[] nodeWeights;
    GriddedRegion gridReg;
    int numRuptures;
    // 
    ArrayList<ProbEqkRupture> probEqkRuptureList;

    /**
     * MultiDepthAreaSource constructor. In this case all the ruptures are 
     * considered as point sources. 
     * 
     */
    public MDAreaSource (
    		Region reg,
    		double gridResolution,
    		IncrementalMagFreqDist IncrementalMagFreqDist,
    		FocalMechanism[] focalMechanism,
    		double[] focalMechanismWeight,
    		double[] depthVector,
    		double[] magVector,
    		double[][] depthMagMatrix,
            double duration,
            double minMag,
            double aspectRatio,
            MagScalingRelationship magScalRel,
            int numStrikes,
            double firstStrike) {
    	
    	CreateMDAreaSource(
    			reg,
    			gridResolution,
    			IncrementalMagFreqDist,
    			focalMechanism,
    			focalMechanismWeight,
    			depthVector,
    			magVector,
    			depthMagMatrix,
    			duration,
    			minMag,
    			aspectRatio,
    			magScalRel,
    			numStrikes,
    			firstStrike);
    }
    
    /**
     * MultiDepthAreaSource constructor. In this case all the ruptures are 
     * considered as point sources. 
     * 
     * @param reg
     * @param gridResolution 
     * @param IncrementalMagFreqDist 
     * @param focalMechanismArr - 
     * @param focalMechanismWeight - Weight of the different focal mechanisms 
     * 	defined for the current source (must sum to one).
     * 	Example:
     * 		focMec1 	wei1	0.2
     * 		focMec2		wei2	0.5
     * 		focMec3 	wei3 	0.3
     * 
     * @param depthVector
     * @param magVector
     * @param depthMagMatrix - The number of rows correspond to the number of 
     * 	magnitude intervals whereas the number of columns corresponds to the 
     * 	number of magnitude bins
     * 	Example: 
     * 				dep1	dep2	dep3
     * 		mag1	wei11	wei12	we133  = 1
     * 		mag2	wei21	wei22	wei23  = 1
     * 		mag3	wei31	wei32	wei33  = 1
     * 
     * @param duration - 
     * @param minMag - 
     */
    public MDAreaSource (
    		Region reg, 
    		double gridResolution,
    		IncrementalMagFreqDist IncrementalMagFreqDist,
    		FocalMechanism[] focalMechanism,
    		double[] focalMechanismWeight,
    		double[] depthVector,
    		double[] magVector,
    		double[][] depthMagMatrix,
            double duration,
            double minMag) {

    	// This is a dummy magscaling
    	WC1994_MagAreaRelationship dummy = 
			new WC1994_MagAreaRelationship();
    	
    	CreateMDAreaSource(
    			reg,
    			gridResolution,
    			IncrementalMagFreqDist,
    			focalMechanism,
    			focalMechanismWeight,
    			depthVector,
    			magVector,
    			depthMagMatrix,
    			duration,
    			minMag,
    			Double.NaN,
    			dummy,
    			1,
    			Double.NaN);

    }
    	
    private void CreateMDAreaSource (
    		Region reg,
    		double gridResolution,
    		IncrementalMagFreqDist IncrementalMagFreqDist,
    		FocalMechanism[] focalMechanism,
    		double[] focalMechanismWeight,
    		double[] depthVector,
    		double[] magVector,
    		double[][] depthMagMatrix,
            double duration,
            double minMag,
            double aspectRatio,
            MagScalingRelationship magScalRel,
            int numStrikes,
            double firstStrike){
    	// 
    	double[][] tmpdmm;
    	// Set MultiDepthAreaSource instance variables
    	// -- Frequency-Magnitude Distribution
        this.magFreqDist = IncrementalMagFreqDist;
        // -- Focal Mechanism 
        this.focalMechanisms = focalMechanism;
        this.focalMechanismWeight = focalMechanismWeight;     
        this.depthVector = depthVector;
        this.magVector = magVector;
        tmpdmm = depthMagMatrix;
        this.duration = duration;
        this.minMag = minMag;
        this.reg = reg;
        this.isPoissonian = true;
        this.aspectRatio = aspectRatio;
        this.magScalRel = magScalRel;
        this.numStrikes = numStrikes;
        
        // Turns on the point sources flag
        pointSources = true;
        // Create a discretized region covering the area polygon
        gridReg = new GriddedRegion(reg, gridResolution, null);
        
        // TODO - Check this - The purpose of this is not completely clear to me
        // Compute maxLength needed for the getMinDistance(Site) method, so this
        // can be computed before ruptures are generated
        // this.maxLength = 0.0;
        
        // Check input
        checkInput(
        		this.reg, 
        		this.gridResolution,
        		this.magFreqDist,
        		this.focalMechanisms,
        		this.focalMechanismWeight,
        		this.depthVector,
        		this.magVector,
        		tmpdmm);
		
		// Normalizing the weights of the depthMagMatrix - for each magnitude 
		// interval the weights must sum to unity
		IncrementalMagFreqDist tmp = IncrementalMagFreqDist.deepClone();
		double sumw = 0.0;
		for (int i = 0; i < tmpdmm.length; i++) {
			sumw = 0.0;
			for (int j = 0; j < tmpdmm[i].length; j++) {
				 sumw += tmpdmm[i][j];
			}
			tmp.set(i,sumw);
		}
		for (int i = 0; i < tmpdmm.length; i++) {
			for (int j = 0; j < tmpdmm[i].length; j++) {
				tmpdmm[i][j] = tmpdmm[i][j]/sumw;
			}
		} 
		
        // Transposing tmpdmm and create the depthMagMatrix
        int col = 0;
        int row = tmpdmm.length;
        for (int i=0; i < row; i++){
        	if (tmpdmm[i].length > col) col = tmpdmm[i].length;
        }
        this.depthMagMatrix = new double[col][row];
		for (int r = 0; r < tmpdmm.length; r++) {
			for (int c = 0; c < tmpdmm[r].length; c++) {
				this.depthMagMatrix[c][r] = tmpdmm[r][c];
			}
		} 	
    }

    /**
     * This method checks that the input provided is correct.
     */
    private static Boolean checkInput(
    		Region reg, 
    		double gridResolution,
    		IncrementalMagFreqDist magFreqDists,
    		FocalMechanism[] focalMechanisms,
    		double[] focalMechanismWeight,
    		double[] depthVector,
    		double[] magVector,
    		double[][] depthMagMatrix){
    	
    	// Check the consistency of focalMechanism and focalMechanismWeight
    	// vector sizes
        if (focalMechanisms.length != focalMechanismWeight.length){
        	throw new IllegalArgumentException(
            	"Focal mechanism vector and Focal mechanism weights vector " +
            	"sizes are not compatible"); 
        }
        // Check that the focalMechanismWeight sum to one
        double sum = 0.0;
        for (int i = 0; i < focalMechanismWeight.length; i++){  	
        	sum += focalMechanismWeight[i];
        }
        if (Math.abs(1.0-sum) > 1e-4){
        	throw new IllegalArgumentException(
                "The focal mechanism weights do not sum to unity");
        }
        // Check that the weights for each magnitude in the depthMagMatrix sum 
        // to unity 
        for (int i = 0; i < depthMagMatrix.length; i++){
        	sum = 0.0;
        	// Looping over depths
        	for (int j = 0; j < depthMagMatrix[i].length; j++){
        		sum += depthMagMatrix[i][j];
        	}
        	// Checking
        	if (Math.abs(sum-1.0) > 1.0e-4){
        		throw new IllegalArgumentException(
                    	"The weights for the magnitude interval " +
                    	magVector[i]+" do not sum to unity: "+sum); 
        	}
        }
        // Check that the number of depths in the depthVector is consistent with 
        // the size of the depthMagMatrix
        for (int i = 0; i < depthMagMatrix.length; i++){
        	if (depthMagMatrix[i].length != depthVector.length)
        		throw new IllegalArgumentException(
                    	"The size of the magnitude-depth matrix is not " +
                    	"consistent with the depth vector" +
                    	depthMagMatrix[i].length + " vs "+
                    	depthVector.length);
        }
        // Check that the number of magnitudes in the magVector is consistent
        // with the size of the depthMagMatrix
        if (depthMagMatrix.length != magVector.length)
        	throw new IllegalArgumentException(
                "The size of the magnitude-depth matrix is not " +
            	"consistent with the magnitude vector "+ depthMagMatrix.length +
            	" vs " + magVector.length); 
    	return true;
    }
    
    /**
     * This computes the weight for each node as one over the total number of
     * nodes multiplied by the area of the node (which changes with lat), and
     * then renormalized
     */
    private void computeNodeWeights() {
        int numPts = gridReg.getNodeCount();
        nodeWeights = new double[numPts];
        double tot = 0;
        for (int i = 0; i < numPts; i++) {
            double latitude = gridReg.locationForIndex(i).getLatitude();
            latitude = latitude - gridReg.getSpacing()/2;
            nodeWeights[i] = Math.cos(latitude * Math.PI / 180);
            tot += nodeWeights[i];
        }
        for (int i = 0; i < numPts; i++)
            nodeWeights[i] = nodeWeights[i]/tot;
    }
    
    /**
     * Given a depth value, this computes the weight for each magnitude bin 
     */
    private double[] computeFMDweights(int depthIndex) {
    	double[] w = new double[this.magFreqDist.getNum()];
    	int idxm = 0;
    	int idxw = 0;
    	// Iterate over the magnitude thresholds used to create the 
    	// depthMagMatrix
    	while (idxw < depthMagMatrix[depthIndex].length){
    		// Iterate over 
    		while (magFreqDist.getX(idxm) < magVector[idxw]){
    			w[idxm] = depthMagMatrix[depthIndex][idxw];
    			idxm += 1;
    		}
    		idxw += 1;
    	}
        return w;
    }
    /**
     * This method creates all the possible ruptures that can be generated by 
     * this area source - each rupture is associated with a probability of 
     * occurrence in the given time span (duration) 
     * @throws Exception 
     */
    private void mkAllRuptures() throws Exception {
    	double depth;
    	double[] mfdWeight;
    	ProbEqkRupture prbRup;
        probEqkRuptureList = new ArrayList<ProbEqkRupture>();
        ArrayList<Double> rates = new ArrayList<Double>();
        // Computes the node wts (including fact that area changes with
        // latitude) - Note that this weights refer to one node of the grid
        // used to discretise the area polygon; these weights thus do not 
        // take into account seismicity depth distribution. 
        computeNodeWeights();
        // If they are point sources
        int cnt = 0;
        IncrementalMagFreqDist mfd = magFreqDist;
    	// For each depth interval
        for (int d=0; d < depthVector.length; d++){
        	// Computing depth dependent weights;
            mfdWeight = computeFMDweights(d);       	                
            // For each node used to discretize the area polygon at the 
            // surface 
            for (int j = 0; j < gridReg.getNodeCount(); j++) {
            	Location loc = gridReg.getNodeList().get(j);
                // Now I scale the Frequency-Magnitude distribution; to
                // do this, for each bin in the Frequency-Magnitude 
            	// distribution I multiply the occurrence rate by a
            	// depth weight and a cell weight and a focal mechanism 
            	// weight
            	for (int w = 0; w < mfd.getNum(); w++) {
            		double mag = mfd.getX(w);       	
            		// For each focal mechanism
                    for (int k = 0; k < focalMechanisms.length; k++) {
                        // Scale the occurrences in the FMD
                        double rate = mfd.getY(w) * nodeWeights[j] *
                        	mfdWeight[w] * focalMechanismWeight[k];
                        // Compute the probability of at least one 
                        // occurrence
                        double prob = 1.0-Math.exp(-duration*rate);
                        // Add the rupture to the list of ruptures
                        if (mag >= minMag && prob > 0) {
                        	cnt += 1;
                            if (pointSources) {
                            	// Point sources
                            	prbRup = new ProbEqkRupture();
	                            // Setting rupture parameters
                            	prbRup.setMag(mag);
                            	prbRup.setProbability(prob);
                            	prbRup.setAveRake(focalMechanisms[k].getRake());
	                            // Set depth to the top of rupture
	                            depth = depthVector[d];
	                            // Set final location of the rupture
	                            Location finalLoc =
	                                    new Location(loc.getLatitude(),
	                                            loc.getLongitude(), depth);
	                            prbRup.setPointSurface(finalLoc,
	                                    focalMechanisms[k].getDip());
	                            // Adding the rupture
	                            probEqkRuptureList.add(prbRup);
	                            rates.add(rate); 
                            } else {
                        		// Create the point to line object
                    			Pnt2Lne pnt2Lne = new Pnt2Lne(
                    				loc,
                    				mag,
                    				focalMechanisms[k],
                                    depthVector[d],
                    				aspectRatio,
                    				magScalRel,
                    				numStrikes,
                    				firstStrike);
                        		// Create all the ruptures
                                ArrayList<EqkRupture> rupLis = 
                                	pnt2Lne.makeRuptures();
                                // Scale the occurrences in the MFD
                                rate /= numStrikes;
                                // Compute the probability of at least one 
                                // occurrence
                                prob = 1.0-Math.exp(-duration*rate);
                                // Iterate over the ruptures contained in the 
                                // ArrayList computed using the pnt2Lne class
                                for (EqkRupture rup : rupLis){
                                	// Finite rupture
    	                            prbRup = new ProbEqkRupture();
    	                            // Setting rupture parameters
    	                            prbRup.setMag(rup.getMag());
    	                            prbRup.setProbability(prob);
    	                            prbRup.setAveRake(focalMechanisms[k].
    	                            		getRake());
    	                            prbRup.setRuptureSurface(
    	                            		rup.getRuptureSurface());
    	                            // Set depth to the top of rupture
    	                            depth = depthVector[d];
    	                            // Set final location of the rupture
    	                            Location finalLoc =
    	                                    new Location(loc.getLatitude(),
    	                                            loc.getLongitude(), depth);
    	                            prbRup.setPointSurface(finalLoc,
    	                                    focalMechanisms[k].getDip());
    	                            // Adding the rupture
    	                            probEqkRuptureList.add(prbRup);
    	                            rates.add(rate);
                          		}
                            }
                        }    
                    } // End Focal Mechanism loop    
                } // End of FMD loop
            }        	
        }
    }
        

    /**
     * This makes and returns the nth probEqkRupture for this source.
     */
    public ProbEqkRupture getRupture(int nthRupture) {
        if (probEqkRuptureList == null)
			try {
				mkAllRuptures();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        return probEqkRuptureList.get(nthRupture);
    }
    
    /**
     * This makes and returns the nth probEqkRupture for this source.
     */
    @Override
    public ArrayList<ProbEqkRupture> getRuptureList() {
        if (probEqkRuptureList == null)
			try {
				mkAllRuptures();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        return probEqkRuptureList;
    }

	@Override
	public LocationList getAllSourceLocs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EvenlyGriddedSurfaceAPI getSourceSurface() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getMinDistance(Site site) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumRuptures() {
        if (probEqkRuptureList == null)
			try {
				mkAllRuptures();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        return probEqkRuptureList.size();
	}
    
}
