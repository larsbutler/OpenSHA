package org.opensha.sha.imr.attenRelImpl;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.opensha.commons.data.NamedObjectAPI;
import org.opensha.commons.data.Site;
import org.opensha.commons.exceptions.InvalidRangeException;
import org.opensha.commons.exceptions.ParameterException;
import org.opensha.commons.param.DoubleConstraint;
import org.opensha.commons.param.DoubleDiscreteConstraint;
import org.opensha.commons.param.StringConstraint;
import org.opensha.commons.param.StringParameter;
import org.opensha.commons.param.event.ParameterChangeEvent;
import org.opensha.commons.param.event.ParameterChangeListener;
import org.opensha.commons.param.event.ParameterChangeWarningListener;
import org.opensha.sha.earthquake.EqkRupture;
import org.opensha.sha.faultSurface.EvenlyGriddedSurfaceAPI;
import org.opensha.sha.imr.AttenuationRelationship;
import org.opensha.sha.imr.PropagationEffect;
import org.opensha.sha.imr.ScalarIntensityMeasureRelationshipAPI;
import org.opensha.sha.imr.param.EqkRuptureParams.MagParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.DampingParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.PGA_Param;
import org.opensha.sha.imr.param.IntensityMeasureParams.PeriodParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.SA_Param;
import org.opensha.sha.imr.param.OtherParams.ComponentParam;
import org.opensha.sha.imr.param.OtherParams.StdDevTypeParam;
import org.opensha.sha.imr.param.OtherParams.TectonicRegionTypeParam;
import org.opensha.sha.imr.param.PropagationEffectParams.DistanceRupParameter;
import org.opensha.sha.util.TectonicRegionType;

/**
 * <b>Title:</b> AB_2003_AttenRel<p>
 * <b>Description:</b> This implements the  Attenuation Relationship 
 * developed by Atkinson and Boore ("Empirical Ground Motion Relations for Subduction-Zone Earthquakes and their application to Cascadia and other regions" 
 * Bulletin of the Seismological Society of America, Vol. 93, No. 4, pp 1703-1729, 2003)<p>
 * Supported Intensity-Measure Parameters:<p>
 * <UL>
 * <LI>pgaParam - Peak Ground Acceleration
 * <LI>saParam - Response Spectral Acceleration
 * </UL><p>
 * Other Independent Parameters:<p>
 * <UL>
 * <LI>magParam - moment Magnitude
 * <LI>distanceRupParam - closest distance to fault surface
 * <LI>siteTypeParam - "NEHRP B", "NEHRP C", "NEHRP D", "NEHRP E"
 * <LI>fltTypeParam - Style of faulting "Interface" and "InSlab"
 * <LI>componentParam - random horizontal components
 * <LI>stdDevTypeParam - The type of standard deviation
 * </UL><p>
 * 
 *<p>
 *
 * Verification - This model MUST be validated against: 
 * 1) a verification file generated independently by L. Danciu,
 * implemented in the JUnit test class AB_2003test; 
 * 2) temp validation versus a matlab implementation!
 * 
 * 
 *</p>
 *
 **
 * @author     L. Danciu
 * @created    december, 2010
 * @version    1.0
 */
public class AB_2003_AttenRel extends AttenuationRelationship implements ScalarIntensityMeasureRelationshipAPI,
NamedObjectAPI, ParameterChangeListener {

//		public static void main(String[] args) {
//			AB_2003_AttenRel  ar = new AB_2003_AttenRel(null);
//			ar.setParamDefaults();
//			ar.setIntensityMeasure(PGA_Param.NAME);
//			ar.setIntensityMeasure(SA_Param.NAME);
//
//			System.out.println("Results for AB_2003_AttenRel: ");
////	      int iper, double hypodepth, double mag, double rRup, String siteType, String tecRegType
////			period = { 0.00000,	0.04000,  0.10000,	0.20000,  0.40000,  1.00000,  2.00000, 3.03030};
//			System.out.println("mag = 5.50 " + "Hypodepth = 30 " + " rrup=10 " + "SiteType: B " + "Interface");
//			System.out.println("PGA_int = "      + Math.exp(ar.getMean(0, 30.00, 5.50, 10, SITE_TYPE_ROCK, FLT_TEC_ENV_INTERFACE)) + "g");
//			System.out.println("SA(0.04)_int = " + Math.exp(ar.getMean(1, 30.00, 5.50, 10, SITE_TYPE_ROCK, FLT_TEC_ENV_INTERFACE)) + "g");
//			System.out.println("SA(0.10)_int = " + Math.exp(ar.getMean(2, 30.00, 5.50, 10, SITE_TYPE_ROCK, FLT_TEC_ENV_INTERFACE)) + "g");
//			System.out.println("SA(0.20)_int = " + Math.exp(ar.getMean(3, 30.00, 5.50, 10, SITE_TYPE_ROCK, FLT_TEC_ENV_INTERFACE)) + "g");
//			System.out.println("SA(0.40)_int = " + Math.exp(ar.getMean(4, 30.00, 5.50, 10, SITE_TYPE_ROCK, FLT_TEC_ENV_INTERFACE)) + "g");
//			System.out.println("SA(1.00)_int = " + Math.exp(ar.getMean(5, 30.00, 5.50, 10, SITE_TYPE_ROCK, FLT_TEC_ENV_INTERFACE)) + "g");
//			System.out.println("SA(2.00)_int = " + Math.exp(ar.getMean(6, 30.00, 5.50, 10, SITE_TYPE_ROCK, FLT_TEC_ENV_INTERFACE)) + "g");
//			System.out.println("SA(3.00)_int = " + Math.exp(ar.getMean(7, 30.00, 5.50, 10, SITE_TYPE_ROCK, FLT_TEC_ENV_INTERFACE)) + "g");
//            System.out.println();
//			System.out.println("mag = 5.50" + "Hypodepth = 50" + " rrup=40" + "SiteType: B " + "IntraSlab");
//			System.out.println("PGA_inSlab = " + Math.exp(ar.getMean(0, 50.00, 5.50, 40, SITE_TYPE_ROCK, FLT_TEC_ENV_INSLAB)) + "g");
//			System.out.println("SA(0.04)_inSlab = " + Math.exp(ar.getMean(1, 50.00, 5.50, 40, SITE_TYPE_ROCK, FLT_TEC_ENV_INSLAB)) + "g");
//			System.out.println("SA(0.10)_inSlab = " + Math.exp(ar.getMean(2, 50.00, 5.50, 40, SITE_TYPE_ROCK, FLT_TEC_ENV_INSLAB)) + "g");
//			System.out.println("SA(0.20)_inSlab = " + Math.exp(ar.getMean(3, 50.00, 5.50, 40, SITE_TYPE_ROCK, FLT_TEC_ENV_INSLAB)) + "g");
//			System.out.println("SA(0.40)_inSlab = " + Math.exp(ar.getMean(4, 50.00, 5.50, 40, SITE_TYPE_ROCK, FLT_TEC_ENV_INSLAB)) + "g");
//			System.out.println("SA(1.00)_inSlab = " + Math.exp(ar.getMean(5, 50.00, 5.50, 40, SITE_TYPE_ROCK, FLT_TEC_ENV_INSLAB)) + "g");
//			System.out.println("SA(2.00)_inSlab = " + Math.exp(ar.getMean(6, 50.00, 5.50, 40, SITE_TYPE_ROCK, FLT_TEC_ENV_INSLAB)) + "g");
//			System.out.println("SA(3.00)_inSlab = " + Math.exp(ar.getMean(7, 50.00, 5.50, 40, SITE_TYPE_ROCK, FLT_TEC_ENV_INSLAB)) + "g");
//		}	

	// Debugging stuff
	private final static String C = "AB_2003_AttenRel";
	private final static boolean D = false;
	public final static String SHORT_NAME = "AB2003";
	private static final long serialVersionUID = 1234567890987654353L;	
	private static boolean INFO = false;

	// Name of IMR
	public final static String NAME = "AB (2003)";
	// Coefficients
	// index 1 is for PGA - coefficients Table 1 (page 1715)
	//interface coefficients

	private final double[] freq   = { 0.00000, 	25.0000,  10.0000,	5.00000,  2.50000,  1.00000,  0.50000, 0.33000};
	private final double[] period = { 0.00000,	0.04000,  0.10000,	0.20000,  0.40000,  1.00000,  2.00000, 3.00000};
	private final double[] c1     = { 2.99100,	2.87530,  2.77890,	2.66380,  2.52490,	2.14420,  2.19070, 2.30100};
	private final double[] c2     = { 0.03525,	0.07052,  0.09841,	0.12386,  0.14770,	0.13450,  0.07148, 0.02237};
	private final double[] c3     = { 0.00759,	0.01004,  0.00974,	0.00884,  0.00728,	0.00521,  0.00224, 0.00012};
	private final double[] c4     = {-0.00206, -0.00278, -0.00287, -0.00280, -0.00235, -0.00110,  0.00000, 0.00000};
	private final double[] c5     = { 0.19000,	0.15000,  0.15000,	0.15000,  0.13000,	0.10000,  0.10000, 0.10000};
	private final double[] c6     = { 0.24000,	0.20000,  0.23000,	0.27000,  0.37000,	0.30000,  0.25000, 0.25000};
	private final double[] c7     = { 0.29000,	0.20000,  0.20000,	0.25000,  0.38000,	0.55000,  0.40000, 0.36000};
	private final double[] sig_in = { 0.23000,	0.26000,  0.27000,	0.28000,  0.29000,	0.34000,  0.34000, 0.36000};
	private final double[] s1     = { 0.20000,	0.22000,  0.25000,	0.25000,  0.25000,	0.28000,  0.29000, 0.31000};
	private final double[] s2     = { 0.11000,	0.14000,  0.10000,	0.13000,  0.15000,	0.19000,  0.18000, 0.18000};
	// inslab coefficents	
	private final double[] cc1    = {-0.04713,	0.50697,  0.43928,	0.51589,  0.00545, -1.02133, -2.39234, -3.70012};
	private final double[] cc2    = { 0.69090,	0.63273,  0.66675,	0.69186,  0.77270,	0.87890,  0.99640,	1.11690};
	private final double[] cc3    = { 0.01130,	0.01275,  0.01080,	0.00572,  0.00173,	0.00130,  0.00364,	0.00615};
	private final double[] cc4    = {-0.00202, -0.00234, -0.00219, -0.00192, -0.00178, -0.00173, -0.00118, -0.00045};
	private final double[] cc5    = { 0.19000,	0.15000,  0.15000,	0.15000,  0.13000,	0.10000,  0.10000,	0.10000};
	private final double[] cc6    = { 0.24000,	0.20000,  0.23000,	0.27000,  0.37000,	0.30000,  0.25000,	0.25000};
	private final double[] cc7    = { 0.29000,	0.20000,  0.20000,	0.25000,  0.38000,	0.55000,  0.40000,	0.36000};
	private final double[] sig_sl = { 0.27000,	0.25000,  0.28000,	0.28000,  0.28000,	0.29000,  0.30000,	0.30000};
	private final double[] ss1    = { 0.23000,	0.24000,  0.27000,	0.26000,  0.26000,	0.27000,  0.28000,	0.29000};
	private final double[] ss2    = { 0.14000,	0.07000,  0.07000,	0.10000,  0.10000,	0.11000,  0.11000,	0.08000};	

	// Hashmap
	private HashMap<Double, Integer> indexFromPerHashMap;
	private int iper;
	private double mag, rRup;
	private double delta;
	private double sl;
	private String siteType; 
	private String stdDevType;
	private String tecRegType;
	private double log2ln = 2.302585;

	//	private boolean parameterChange;
	private PropagationEffect propagationEffect;
	// Site class Definitions - 
	private StringParameter siteTypeParam = null;
	public final static String SITE_TYPE_INFO = "Geological conditions at the site";
	public final static String SITE_TYPE_NAME = "AB et al 2003 Site Type";

	// Rock description: Vs30 > 760m/s NEHRP Class B
	public final static String SITE_TYPE_ROCK = "NEHRP B";
	// Hard rock description: 360< Vs30 = 760m/s NEHRP Class C
	public final static String SITE_TYPE_HARD_SOIL = "NEHRP C";
	// Hard rock description: 180< Vs30 = 360m/s NEHRP Class D
	public final static String SITE_TYPE_MEDIUM_SOIL = "NEHRP D";
	// Hard rock description: Vs30 <180m/s NEHRP Class E
	public final static String SITE_TYPE_SOFT_SOIL = "NEHRP E";
	public final static String SITE_TYPE_DEFAULT = SITE_TYPE_ROCK;

	// Style of faulting options
	// Only crustal events with reverse fault mechanism 
	public final static String FLT_TEC_ENV_INTERFACE = TectonicRegionType.SUBDUCTION_INTERFACE.toString();
	public final static String FLT_TEC_ENV_INSLAB = TectonicRegionType.SUBDUCTION_SLAB.toString();

	protected final static Double MAG_WARN_MIN = new Double(5.5);
	protected final static Double MAG_WARN_MAX = new Double(8.5);

	protected final static Double DISTANCE_RUP_WARN_MIN = new Double(0.0);
	protected final static Double DISTANCE_RUP_WARN_MAX = new Double(500.0);

	// depth hypocentre
	protected final static Double DEPTH_HYPO_WARN_MIN = new Double(0.0);
	protected final static Double DEPTH_HYPO_WARN_MAX = new Double(125.0);

	// for issuing warnings:
	private transient ParameterChangeWarningListener warningListener = null;

	/**
	 *  This initializes several ParameterList objects.
	 */
	public AB_2003_AttenRel(ParameterChangeWarningListener warningListener) {

		super();

		this.warningListener = warningListener;

		initSupportedIntensityMeasureParams();

		indexFromPerHashMap = new HashMap<Double, Integer>();
		for (int i = 0; i < period.length; i++) { 
			//			System.out.println(period[i]+" "+i);
			indexFromPerHashMap.put(new Double(period[i]), new Integer(i));
		}

		initEqkRuptureParams();
		initSiteParams();
		initPropagationEffectParams();
		initOtherParams();
		initIndependentParamLists(); // This must be called after the above
		initParameterEventListeners(); //add the change listeners to the parameters

		if (D) System.out.println("--- AB_2003_AttenRel end");

	}
	/**
	 * This sets the eqkRupture related parameters (magParam and fltTypeParam)
	 * based on the eqkRupture passed in. The internally held eqkRupture object
	 * is also set as that passed in. Warning constrains are ignored.
	 * 
	 * @param eqkRupture
	 *            The new eqkRupture value
	 * @throws InvalidRangeException
	 *             If not valid rake angle
	 */
	public void setEqkRupture(EqkRupture eqkRupture) throws InvalidRangeException {
		magParam.setValueIgnoreWarning(new Double(eqkRupture.getMag()));		
		this.eqkRupture = eqkRupture;
		setPropagationEffectParams();
	}

	/**
	 * This sets the site-related parameter (siteTypeParam) based on what is in
	 * the Site object passed in (the Site object must have a parameter with the
	 * same name as that in siteTypeParam). This also sets the internally held
	 * Site object as that passed in.
	 * 
	 * @param site
	 *            The new site object
	 * @throws ParameterException
	 *             Thrown if the Site object doesn't contain a Vs30 parameter
	 */
	public void setSite(Site site) throws ParameterException {	 	

//		System.out.println("AB et al --->"+site.getParameter(SITE_TYPE_NAME).getValue());

		siteTypeParam.setValue((String) site.getParameter(SITE_TYPE_NAME).getValue());
		this.site = site;
		setPropagationEffectParams();
	}

	/**
	 * This sets the site and eqkRupture, and the related parameters, from the
	 * propEffect object passed in. Warning constrains are ignored.
	 * 
	 * @param propEffect
	 * @throws ParameterException
	 *             
	 * @throws InvalidRangeException
	 *             If not valid distance, depth??? to check!!!
	 */

	public void setPropagationEffectParams() {
		// Set the distance to rupture
		if ( (this.site != null) && (this.eqkRupture != null)) {
			distanceRupParam.setValue(eqkRupture,site);
		}
	}

	/**
	 * This sets the site and eqkRupture, and the related parameters,
	 *  from the propEffect object passed in. Warning constrains are ingored.
	 * @param propEffect
	 * @throws ParameterException Thrown if the Site object doesn't contain a
	 * Vs30 parameter
	 * @throws InvalidRangeException    If not valid rake angle
	 */
	public void setPropagationEffect(PropagationEffect propEffect) throws
	ParameterException, InvalidRangeException {

		this.site = propEffect.getSite();
		this.eqkRupture = propEffect.getEqkRupture();
		siteTypeParam.setValue((String)site.getParameter(SITE_TYPE_NAME).getValue());
		magParam.setValueIgnoreWarning(new Double(eqkRupture.getMag()));

		// set the distance param
		propEffect.setParamValue(distanceRupParam);
	}

	/**
	 * 
	 * @throws ParameterException
	 */
	protected void setCoeffIndex() throws ParameterException {

		// Check that parameter exists
		if (im == null) {
			throw new ParameterException(C +
					": updateCoefficients(): " +
					"The Intensity Measusre Parameter has not been set yet, unable to process."
			);
		}

		if (im.getName().equalsIgnoreCase(PGA_Param.NAME)) {
			iper = 0;
		}
		else {
			iper = ((Integer) indexFromPerHashMap.get(saPeriodParam.getValue())).intValue();
		}
		intensityMeasureChanged = false;
	}

	/**
	 * 
	 */
	public double getMean() { 

		// Check if distance is beyond the user specified max
		if (rRup > USER_MAX_DISTANCE) {
			return VERY_SMALL_MEAN;
		}

		if (intensityMeasureChanged) {
			setCoeffIndex();// intensityMeasureChanged is set to false in this method
		}

		// Return the computed mean 
		return getMean(iper, mag,rRup, siteType, tecRegType );

	}

	/**
	 * @return    The stdDev value
	 */
	public double getStdDev() {

		if (intensityMeasureChanged) {
			setCoeffIndex();// intensityMeasureChanged is set to false in this method
		}
		return getStdDev(iper, stdDevType, tecRegType);
	}

	/**
	 * Allows the user to set the default parameter values for the selected Attenuation
	 * Relationship.
	 */
	public void setParamDefaults() {

		magParam.setValueAsDefault();
		tectonicRegionTypeParam.setValueAsDefault();
		distanceRupParam.setValueAsDefault();
		saParam.setValueAsDefault();
		saPeriodParam.setValueAsDefault();
		saDampingParam.setValueAsDefault();
		pgaParam.setValueAsDefault();
		stdDevTypeParam.setValueAsDefault();
		siteTypeParam.setValue(SITE_TYPE_DEFAULT);

		mag = ((Double) magParam.getValue()).doubleValue();
		rRup = ((Double) distanceRupParam.getValue()).doubleValue();
		tecRegType = tectonicRegionTypeParam.getValue().toString();
		siteType = siteTypeParam.getValue().toString();
	}
    /**
     * This is a simple aproach to set the hypodepth value.
     * This has to be further investigated, to be consistent with the approach 
     * applied in Zhao et al 2006. 
     * We are not allowed to use the common OpenSHA ParameterAPI approach; 
     */
//	public void setHypodepth(double value){
//		hypodepth = value;
//	}

	/**
	 *  Creates the two Potential Earthquake parameters (magParam and
	 *  fltTypeParam) and adds them to the eqkRuptureParams
	 *  list. Makes the parameters non-editable.
	 */
	protected void initEqkRuptureParams() {

		if (D) System.out.println("--- initEqkRuptureParams");

		// Magnitude parameter
		magParam = new MagParam(MAG_WARN_MIN, MAG_WARN_MAX);
		// Add parameters 
		eqkRuptureParams.clear();
		eqkRuptureParams.addParameter(magParam);
//		eqkRuptureParams.addParameter(tectonicRegionTypeParam);
		

		if (D) System.out.println("--- initEqkRuptureParams end");
	}

	/**
	 *  Creates the Site-Type parameter and adds it to the siteParams list.
	 *  Makes the parameters non-edit-able.
	 */
	protected void initSiteParams() {
		// 
		StringConstraint siteConstraint = new StringConstraint();

		siteConstraint.addString(SITE_TYPE_ROCK);
		siteConstraint.addString(SITE_TYPE_HARD_SOIL);
		siteConstraint.addString(SITE_TYPE_MEDIUM_SOIL);
		siteConstraint.addString(SITE_TYPE_SOFT_SOIL);
		siteConstraint.setNonEditable();
		//
		siteTypeParam = new StringParameter(SITE_TYPE_NAME, siteConstraint, null);
		siteTypeParam.setInfo(SITE_TYPE_INFO);
		siteTypeParam.setNonEditable();
		// Add siteTypeParam to the set of parameters describing the site
		siteParams.clear();
		siteParams.addParameter(siteTypeParam);  
	}

	/**
	 *  Creates the Propagation Effect parameters and adds them to the
	 *  propagationEffectParams list. Makes the parameters non-editable.
	 */
	protected void initPropagationEffectParams() {
		distanceRupParam = new DistanceRupParameter(0.0);
		distanceRupParam.addParameterChangeWarningListener(warningListener);
		DoubleConstraint warn = new DoubleConstraint(DISTANCE_RUP_WARN_MIN, DISTANCE_RUP_WARN_MAX);
		warn.setNonEditable();
		distanceRupParam.setWarningConstraint(warn);

		distanceRupParam.setNonEditable();
		propagationEffectParams.addParameter(distanceRupParam);
	}

	/**
	 *  Creates other Parameters that the mean or stdDev depends upon,
	 *  such as the Component or StdDevType parameters.
	 */
	protected void initOtherParams() {

		if (D) System.out.println("--- initOtherParams");

		// init other params defined in parent class
		super.initOtherParams();

		// The stdDevType Parameter
		StringConstraint stdDevTypeConstraint = new StringConstraint();
		stdDevTypeConstraint.addString(StdDevTypeParam.STD_DEV_TYPE_TOTAL);
		stdDevTypeConstraint.addString(StdDevTypeParam.STD_DEV_TYPE_NONE);
		stdDevTypeConstraint.addString(StdDevTypeParam.STD_DEV_TYPE_INTER);
		stdDevTypeConstraint.addString(StdDevTypeParam.STD_DEV_TYPE_INTRA);
		stdDevTypeConstraint.setNonEditable();
		stdDevTypeParam = new StdDevTypeParam(stdDevTypeConstraint);

		// The Component Parameter
		StringConstraint constraint = new StringConstraint();
		constraint.addString(ComponentParam.COMPONENT_RANDOM_HORZ);
		constraint.addString(ComponentParam.COMPONENT_AVE_HORZ);
		constraint.setNonEditable();
		componentParam = new ComponentParam(constraint,ComponentParam.COMPONENT_RANDOM_HORZ);
		componentParam = new ComponentParam(constraint,ComponentParam.COMPONENT_AVE_HORZ);
		// tecRegType
		StringConstraint tecRegConstr = new StringConstraint(); 
		tecRegConstr.addString(FLT_TEC_ENV_INTERFACE);
		tecRegConstr.addString(FLT_TEC_ENV_INSLAB);
		tectonicRegionTypeParam = new TectonicRegionTypeParam(tecRegConstr,FLT_TEC_ENV_INTERFACE);
//		tectonicRegionTypeParam = new TectonicRegionTypeParam(tecRegConstr,FLT_TEC_ENV_INSLAB);

		
		// add these to the list
		otherParams.addParameter(stdDevTypeParam);
		otherParams.addParameter(componentParam);
//		otherParams.addParameter(tectonicRegionTypeParam);

		otherParams.replaceParameter(tectonicRegionTypeParam.NAME, tectonicRegionTypeParam);
		

	}	

	/**
	 * This creates the lists of independent parameters that the various dependent
	 * parameters (mean, standard deviation, exceedance probability, and IML at
	 * exceedance probability) depend upon. NOTE: these lists do not include anything
	 * about the intensity-measure parameters or any of their internal
	 * independentParamaters.
	 */
	protected void initIndependentParamLists() {

		// params that the mean depends upon
		meanIndependentParams.clear();
		meanIndependentParams.addParameter(magParam);
		meanIndependentParams.addParameter(tectonicRegionTypeParam);
		meanIndependentParams.addParameter(distanceRupParam);
		meanIndependentParams.addParameter(siteTypeParam);

		// params that the stdDev depends upon
		stdDevIndependentParams.clear();
		stdDevIndependentParams.addParameter(saPeriodParam);
		stdDevIndependentParams.addParameter(tectonicRegionTypeParam);
		stdDevIndependentParams.addParameter(stdDevTypeParam);

		// params that the exceed. prob. depends upon
		exceedProbIndependentParams.clear();
		exceedProbIndependentParams.addParameterList(meanIndependentParams);
		exceedProbIndependentParams.addParameter(stdDevTypeParam);
		exceedProbIndependentParams.addParameter(sigmaTruncTypeParam);
		exceedProbIndependentParams.addParameter(sigmaTruncLevelParam);

		// params that the IML at exceed. prob. depends upon
		imlAtExceedProbIndependentParams.addParameterList(
				exceedProbIndependentParams);
		imlAtExceedProbIndependentParams.addParameter(exceedProbParam);
	}

	/**
	 *  Creates the two supported IM parameters (PGA and SA), as well as the
	 *  independenParameters of SA (periodParam and dampingParam) and adds
	 *  them to the supportedIMParams list. Makes the parameters non-editable.
	 */
	protected void initSupportedIntensityMeasureParams() {
		// Create saParam:
		DoubleDiscreteConstraint periodConstraint = new DoubleDiscreteConstraint();
		for (int i = 1; i < period.length; i++) {
			periodConstraint.addDouble(new Double(period[i]));
		}
		periodConstraint.setNonEditable();
		saPeriodParam = new PeriodParam(periodConstraint);
		saDampingParam = new DampingParam();
		saParam = new SA_Param(saPeriodParam, saDampingParam);
		saParam.setNonEditable();

		//  Create PGA Parameter (pgaParam):
		pgaParam = new PGA_Param();
		pgaParam.setNonEditable();

		// Add the warning listeners:
		saParam.addParameterChangeWarningListener(warningListener);
		pgaParam.addParameterChangeWarningListener(warningListener);

		// Put parameters in the supportedIMParams list:
		supportedIMParams.clear();
		supportedIMParams.addParameter(saParam);
		supportedIMParams.addParameter(pgaParam);	
	}

	/**
	 * get the name of this IMR
	 *
	 * @returns the name of this IMR
	 */
	public String getName() {
		return NAME;
	}

	/**
	 * Returns the Short Name of each AttenuationRelationship
	 * @return String
	 */
	public String getShortName() {
		return SHORT_NAME;
	}

	/**
	 * 
	 * @param iper
	 * @param mag
	 * @param rRup
	 * @return
	 */
	public double getMean(int iper, double mag, double rRup, String siteType, String tecRegType) {
		double hypodepth;
		double g = 0.0; // this is unity for 
		double flag_Sc  = 0.0; // This is unity for soil NEHRP C - Otherwise 0
		double flag_Sd  = 0.0; // This is unity for soil NEHRP D - Otherwise 0
		double flag_Se  = 0.0; // This is unity for soil NEHRP E - Otherwise 0
		// NEHRP B all soil coefficients equal zero
		double mean =0.00;
		// Computing the hypocentral depth
//		System.out.println("AB2003 et al -->"+this.eqkRupture.getInfo());
		EvenlyGriddedSurfaceAPI surf = this.eqkRupture.getRuptureSurface();
		
		// ---------------------------------------------------------------------- MARCO 2010.03.15
		// Compute the hypocenter as the middle point of the rupture
		double hypoLon = 0.0;
		double hypoLat = 0.0;
		double hypoDep = 0.0;
		double cnt = 0.0;
		for (int j=0; j < surf.getNumCols(); j++){
			for (int k=0; k < surf.getNumRows(); k++){
				hypoLon += surf.getLocation(k,j).getLongitude();
				hypoLat += surf.getLocation(k,j).getLatitude();
				hypoDep = hypoDep + surf.getLocation(k,j).getDepth();
				cnt += 1;
			}
		}
		double chk = surf.getNumCols() * surf.getNumRows();
		
		hypoLon = hypoLon / cnt;
		hypoLat = hypoLat / cnt;
		hypoDep = hypoDep / cnt;
		// set the depth for events with hdepth > 100km
		if (hypoDep > 100){
			hypodepth = 100;
		} else {
			hypodepth = hypoDep ;
		}
//		System.out.println("computed hypocentral depth:"+hypodepth);
		if (INFO) System.out.println("computed hypocentral depth:"+hypodepth);
		
		// This is used just for verification - DO NOT use it for regular calculations
//		hypodepth = this.eqkRupture.getHypocenterLocation().getDepth();
//		System.out.println("real hypocentral depth:"+hypodepth);
		// ---------------------------------------------------------------------- MARCO 2010.03.15

		delta = 0.00724*Math.pow(10, 0.507*mag);
		double R = Math.sqrt(rRup*rRup + delta*delta);
		double PGArx = 0.00;

		if (tecRegType.equals(FLT_TEC_ENV_INTERFACE)){
			if (mag >= 8.5) {
				mag = 8.5;
			} else {
				this.mag = mag;
			} 
			g = Math.pow(10, 1.2-0.18*mag);
			PGArx = Math.pow(10, c1[0] + c2[0] * mag + c3[0] * hypodepth + c4[0]*R - g*Math.log10(R));
		} else if (tecRegType.equals(FLT_TEC_ENV_INSLAB)){
			if (mag >= 8.0) {
				mag = 8.0;
			} else {
				this.mag = mag;
			}
			g = Math.pow(10, 0.301-0.01*mag);
			PGArx = Math.pow(10,cc1[0] + cc2[0] * mag + cc3[0] * hypodepth + cc4[0]*R - g*Math.log10(R));
		} else {
			System.out.println("+++"+tecRegType.toString()+"--");
			throw new RuntimeException("\n  Cannot handle this combination: \n  tectonic region");
		}

		if (im.getName().equals(PGA_Param.NAME) || freq[iper] >=2){
			if ((100 < PGArx) && (PGArx< 500)) 
				sl = 1.00-(PGArx-100)/400;
			else if (PGArx >= 500)
				sl = 0.00;
//						System.out.println("case0");
		} else if ( freq[iper] <= 1) {
			if (100 < PGArx)
				sl =1.00;
//						System.out.println("case1");
		} else if ((1 < freq[iper]) && (freq[iper]< 2)) {	
			if ((100 < PGArx) && (PGArx< 500))
				sl = 1.00-(freq[iper]-1)*(PGArx-100)/400;
			else if (PGArx >= 500)
				sl = 1.00-(freq[iper]-1);
//						System.out.println("case2");
		} else {
			throw new RuntimeException("\n  Unrecognized nonlinear soil effect \n");
		}

		// Site term correction
		if (D) System.out.println("Site conditions: "+siteType);
		if (siteType.equals(SITE_TYPE_ROCK)){
			// Vs30 > 760
			if (D) System.out.println("NEHRP B");
			flag_Sc  = 0.0; 
			flag_Sd  = 0.0; 
			flag_Se  = 0.0; 
		} else if (siteType.equals(SITE_TYPE_HARD_SOIL)) {	
			// 360 < Vs30 < 760
			if (D) System.out.println("NEHRP C");
			flag_Sc  = 1.0; 
			flag_Sd  = 0.0; 
			flag_Se  = 0.0; 
		} else if (siteType.equals(SITE_TYPE_MEDIUM_SOIL)) {
			// 180 < Vs30 < 360
			if (D) System.out.println("NEHRP D");
			flag_Sc  = 0.0; 
			flag_Sd  = 1.0; 
			flag_Se  = 0.0;		
		} else if (siteType.equals(SITE_TYPE_SOFT_SOIL)) {
			// Vs30 < 180
			if (D) System.out.println("NEHRP E");
			flag_Sc  = 0.0; 
			flag_Sd  = 0.0; 
			flag_Se  = 1.0;
		} else {
			throw new RuntimeException("\n  Unrecognized site type \n");
		}

		double logY = 0.00;
		// compute the mean
		if (tecRegType.equals(FLT_TEC_ENV_INTERFACE)){
			// add soil nonlinearity effect

			logY =  c1[iper] + c2[iper] * mag + c3[iper] * hypodepth + c4[iper]*R - g*Math.log10(R) + 
			flag_Sc * c5[iper]*sl+ flag_Sd * c6[iper]*sl+ flag_Se * c7[iper]*sl;
			// correction from erratum AB2008
			double logY02 =  c1[3] + c2[3] * mag + c3[3] * hypodepth + c4[3]*R - g*Math.log10(R) + 
			flag_Sc * c5[3]*sl+ flag_Sd * c6[3]*sl+ flag_Se * c7[3]*sl;
			double logY04 =  c1[4] + c2[4] * mag + c3[4] * hypodepth + c4[4]*R - g*Math.log10(R) + 
			flag_Sc * c5[4]*sl+ flag_Sd * c6[4]*sl+ flag_Se * c7[4]*sl;
			if (period[iper] == 0.2){
				// add correction
			logY = 0.333*logY02 + 0.667*logY04;
			} else if (period[iper] == 0.4){
				// add correction
			logY = 0.333*logY04 + 0.667*logY02;
			}
			logY *= log2ln;
		} else if (tecRegType.equals(FLT_TEC_ENV_INSLAB)) {
			logY = cc1[iper] + cc2[iper] * mag + cc3[iper] * hypodepth + cc4[iper]*R - g*Math.log10(R) + 
			flag_Sc * cc5[iper]*sl + flag_Sd * cc6[iper]*sl + flag_Se * cc7[iper]*sl;
			logY *= log2ln;
		}
		return Math.log(Math.exp(logY)/981);
	}
	/**
	 * This gets the standard deviation for specific parameter settings.  We might want another 
	 * version that takes the actual SA period rather than the period index.
	 * @param iper
	 * @param rRup
	 * @param mag
	 * @param stdDevType
	 * @return
	 */
	public double getStdDev(int iper, String stdDevType, String tecRegType) {

		if (tecRegType.equals(FLT_TEC_ENV_INTERFACE)){
			if(stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_TOTAL))
				return log2ln*sig_in[iper];
			else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_NONE))
				return 0;
			else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_INTER))
				return log2ln*s2[iper];
			else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_INTRA))
				return log2ln*s1[iper];
			else 
				return Double.NaN;
		} 
		else {
			if(stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_TOTAL))
				return log2ln*sig_sl[iper];
			else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_NONE))
				return 0;
			else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_INTER))
				return log2ln*ss1[iper];
			else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_INTRA))
				return log2ln*ss2[iper];
			else 
				return Double.NaN; 
		}
	}

	/**
	 * This listens for parameter changes and updates the primitive parameters accordingly
	 * @param e ParameterChangeEvent
	 */
	public void parameterChange(ParameterChangeEvent e) {

		String pName = e.getParameterName();
		Object val = e.getNewValue();

		if (D) System.out.println("Changed param: "+pName);

		if (pName.equals(DistanceRupParameter.NAME)) {
			rRup = ( (Double) val).doubleValue();
		} 
		else if (pName.equals(MagParam.NAME)) {
			mag = ( (Double) val).doubleValue();
		} 
		else if (pName.equals(StdDevTypeParam.NAME)) {
			stdDevType = (String) val;
		} 
		else if (pName.equals(TectonicRegionTypeParam.NAME)) {
			tecRegType = tectonicRegionTypeParam.getValue().toString();
			if (D) System.out.println("tecRegType new value:"+tecRegType);
		} 
		else if (pName.equals(SITE_TYPE_NAME)) {
			siteType = this.getParameter(this.SITE_TYPE_NAME).getValue().toString();
		}
		else if (pName.equals(PeriodParam.NAME) ) {
			intensityMeasureChanged = true;
		}
	}
	/**
	 * Allows to reset the change listeners on the parameters
	 */
	public void resetParameterEventListeners(){
		magParam.removeParameterChangeListener(this);
		tectonicRegionTypeParam.removeParameterChangeListener(this);
		siteTypeParam.removeParameterChangeListener(this);
		distanceRupParam.removeParameterChangeListener(this);
		stdDevTypeParam.removeParameterChangeListener(this);
		saPeriodParam.removeParameterChangeListener(this);
		this.initParameterEventListeners();
	}
	/**
	 * Adds the parameter change listeners. This allows to listen to when-ever the
	 * parameter is changed.
	 */
	protected void initParameterEventListeners() {
		if (D) System.out.println("--- initParameterEventListeners begin");

		magParam.addParameterChangeListener(this);
		tectonicRegionTypeParam.addParameterChangeListener(this);
		siteTypeParam.addParameterChangeListener(this);
		distanceRupParam.addParameterChangeListener(this);
		stdDevTypeParam.addParameterChangeListener(this);
		saPeriodParam.addParameterChangeListener(this);

		if (D) System.out.println("--- initParameterEventListeners end");
	}


	/**
	 * This provides a URL where more info on this model can be obtained
	 * @throws MalformedURLException if returned URL is not a valid URL.
	 * @returns the URL to the AttenuationRelationship document on the Web.
	 */
	public URL getInfoURL() throws MalformedURLException{
		return new URL("http://www.opensha.org/documentation/modelsImplemented/attenRel/AB_2003.html");
	}

}
