package org.opensha.sha.imr.attenRelImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.opensha.commons.geo.Location;
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
import org.opensha.sha.imr.param.EqkRuptureParams.FaultTypeParam;
import org.opensha.sha.imr.param.EqkRuptureParams.MagParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.DampingParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.PGA_Param;
import org.opensha.sha.imr.param.IntensityMeasureParams.PeriodParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.SA_Param;
//import org.opensha.sha.imr.param.EqkRuptureParams.HypoDepthParam;
import org.opensha.sha.imr.param.OtherParams.ComponentParam;
import org.opensha.sha.imr.param.OtherParams.StdDevTypeParam;
import org.opensha.sha.imr.param.OtherParams.TectonicRegionTypeParam;
import org.opensha.sha.imr.param.PropagationEffectParams.DistanceRupParameter;
import org.opensha.sha.util.TectonicRegionType;

public class YoungsEtAl_1997_AttenRel extends AttenuationRelationship implements ScalarIntensityMeasureRelationshipAPI,
NamedObjectAPI, ParameterChangeListener {

	// Debugging stuff
	private final static String C = "Youngs_et_al_AttenRel";
	private final static boolean D = false;
	public final static String SHORT_NAME = "Youngs1997";
	private static final long serialVersionUID = 1234567890987654353L;	

	// Name of IMR
	public final static String NAME = "Youngs_et_al (1997)";

	// Coefficients : these are for soil type = rock
	double[] periods = {
			 0.00, 0.075, 0.10, 0.20, 0.30, 
			 0.40, 0.500, 0.75, 1.00, 1.50, 
			 2.00, 3.00};
	double[] c1s = {
			 0.000,  2.400,  2.516,  1.549,  0.793,  
			 0.144, -0.438, -1.704, -2.870, -5.101, 
			-6.433, -6.672, -7.618};
	double[] c2s = {
			 0.0000, -0.0019, -0.0019, -0.0019, -0.0020, 
			-0.0020, -0.0035, -0.0048, -0.0066, -0.0114, 
			-0.0164, -0.0221, -0.0235};
	double[] c3s = {
			-2.329, -2.697, -2.697, -2.464, -2.327, 
			-2.230, -2.140, -1.952, -1.785, -1.470, 
			-1.290, -1.347, -1.272};
	double[] c4s = {
			 1.45, 1.45, 1.45, 1.45, 1.45,
			 1.45, 1.45, 1.45, 1.45, 1.50, 
			 1.55, 1.65, 1.65};
	double[] c5s = {
			-0.10, -0.10, -0.10, -0.10, -0.10, 
			-0.10, -0.10, -0.10, -0.10, -0.10, 
			-0.10, -0.10, -0.10};
	
	// coefficients for rock
	double[] periodr = {
			 0.00, 0.075, 0.10, 0.20, 0.30, 
		     0.40, 0.500, 0.75, 1.00, 1.50, 
			 2.00, 3.00};
	double[] c1r = {
			 0.000,  1.275,  1.188,  0.722,  0.246, 
			-0.115, -0.400, -1.149, -1.736, -2.634, 
			-3.328, -4.511};
	double[] c2r = {
			 0.0000,  0.0000, -0.0011, -0.0027, -0.0036, 
			-0.0043, -0.0048, -0.0057, -0.0064, -0.0073, 
			-0.0080, -0.0089};
	double[] c3r = {
			-2.552, -2.707, -2.655, -2.528, -2.454, 
			-2.401, -2.360, -2.286, -2.234, -2.160, 
			-2.107, -2.033};
	double[] c4r = {
			1.45, 1.45, 1.45, 1.45, 1.45,
			1.45, 1.45, 1.45, 1.45, 1.50,
			1.55, 1.65};
	double[] c5r = {
			-0.10, -0.10, -0.10, -0.10, -0.10, 
			-0.10, -0.10, -0.10, -0.10, -0.10, 
			-0.10, -0.10};
	
	// independent coefficients for rock 	
	private static final double a1r = 0.2418;
	private static final double a2r = 1.4140;
	private static final double a3r = 10.000;
	private static final double a4r = 1.7818;
	private static final double a5r = 0.554;
	private static final double a6r = 0.00607;
	private static final double a7r = 0.3846;

	// independent coefficients for soil
	private static final double a1s = -0.6687;
	private static final double a2s = 1.438;
	private static final double a3s = 10.000;
	private static final double a4s = 1.097;
	private static final double a5s = 0.617;
	private static final double a6s = 0.00648;
	private static final double a7s = 0.3643;

	// Hashmap
	private HashMap<Double,Integer> indexFromPerHashMapRock;
	private HashMap<Double,Integer> indexFromPerHashMapSoil;

	private int iper;
	private double mag, rRup;
	double hypodepth =35;
	//	private double stdDevType;
	private String fltType, siteType, stdDevType, tecRegType; 
	private PropagationEffect propagationEffect;
	//	private double lnYref;
	//	private boolean lnYref_is_not_fresh;

	// Style of faulting options
//	public final static String FLT_TYPE_INTRAPLATE = "Intraplate";
//	public final static String FLT_TYPE_INTERFACE = "Interface";
//	public final static String FLT_TYPE_UNKNOWN = "Unknown";
	
	public final static String FLT_TEC_ENV_INTERFACE = TectonicRegionType.SUBDUCTION_INTERFACE.toString();
	public final static String FLT_TEC_ENV_SLAB = TectonicRegionType.SUBDUCTION_SLAB.toString();

	protected final static Double MAG_WARN_MIN = new Double(5);
	protected final static Double MAG_WARN_MAX = new Double(8);

	protected final static Double DISTANCE_RUP_WARN_MIN = new Double(0.0);
	protected final static Double DISTANCE_RUP_WARN_MAX = new Double(500.0);
	
	// depth hypocentre
	protected final static Double DEPTH_HYPO_WARN_MIN = new Double(10.0);
	protected final static Double DEPTH_HYPO_WARN_MAX = new Double(229.0);
	
	// Soil Types Parameters
	private StringParameter siteTypeParam = null;
	public final static String SITE_TYPE_INFO = "Geological conditions at the site";
	public final static String SITE_TYPE_NAME = "Youngs et al. 1997 site type";
	public final static String SITE_TYPE_ROCK = "Rock";
	public final static String SITE_TYPE_SOIL = "Soil";
	public final static String SITE_TYPE_DEFAULT = SITE_TYPE_ROCK;

	// for issuing warnings:
	private transient ParameterChangeWarningListener warningListener = null;


	/**
	 * Constructor 
	 * @param This initializes several ParameterList objects.  
	 */
	public YoungsEtAl_1997_AttenRel(ParameterChangeWarningListener warningListener) {
		super();

		this.warningListener = warningListener;

		initSupportedIntensityMeasureParams();
		
		// Init the hashmap for rock
		indexFromPerHashMapRock = new HashMap<Double, Integer>();
		for (int i = 1; i < periodr.length ; i++) {
			indexFromPerHashMapRock.put(new Double(periodr[i]), new Integer(i));
		}
		// Init the hashmap for soil
		indexFromPerHashMapSoil = new HashMap<Double, Integer>();
		for (int i = 1; i < periods.length ; i++) {
			indexFromPerHashMapSoil.put(new Double(periods[i]), new Integer(i));
		}
		
		//
		initEqkRuptureParams();
		initPropagationEffectParams();
		initSiteParams();		
		initOtherParams();
		initIndependentParamLists(); // This must be called after the above
		initParameterEventListeners(); // add the change listeners to the parameters
	}
	/**
	 * This sets the eqkRupture related parameters (magParam and fltTypeParam)
	 * based on the eqkRupture passed in. The internally held eqkRupture object
	 * is also set as that passed in. Warning constrains are ingored.
	 * 
	 * @param eqkRupture
	 *            The new eqkRupture value
	 * @throws InvalidRangeException
	 *             If not valid rake angle
	 */
	public void setEqkRupture(EqkRupture eqkRupture) throws InvalidRangeException {
		magParam.setValueIgnoreWarning(new Double(eqkRupture.getMag()));
		//	hypoDepthParam.setValueIgnoreWarning(new Double(eqkRupture.getHypocenterLocation().getDepth()));
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
			iper = 1;
		}
		else {
			if (siteType.equals(SITE_TYPE_ROCK)){
				iper = ( (Integer) indexFromPerHashMapRock.get(saPeriodParam.getValue())).intValue();
			} else {
				iper = ( (Integer) indexFromPerHashMapSoil.get(saPeriodParam.getValue())).intValue();
			}
		}
		intensityMeasureChanged = false;

	}
	/**
	 * Allows the user to set the default parameter values for the selected Attenuation
	 * Relationship.
	 */
	public void setParamDefaults() {

		magParam.setValueAsDefault();
		tectonicRegionTypeParam.setValue(FLT_TEC_ENV_INTERFACE);
		distanceRupParam.setValueAsDefault();
		saParam.setValueAsDefault();
		saPeriodParam.setValueAsDefault();
		saDampingParam.setValueAsDefault();
		pgaParam.setValueAsDefault();
		stdDevTypeParam.setValueAsDefault();
		siteTypeParam.setValue(SITE_TYPE_ROCK);
	    componentParam.setValueAsDefault();

		mag = ((Double) magParam.getValue()).doubleValue();
		rRup = ((Double) distanceRupParam.getValue()).doubleValue();
		// Site type
		siteType = siteTypeParam.getValue().toString();
		tecRegType = tectonicRegionTypeParam.getValue().toString();

	}
	/**
	 * Calculates the mean of the exceedence probability distribution. <p>
	 * @param tecRegType 
	 * @param depth 
	 * @param mag 
	 * @param rRup 
	 * @param siteTypeParam 
	 * @param iper
	 * @return the mean value
	 */
	public double getMean() {

		// check if distance is beyond the user specified max
		if (rRup > USER_MAX_DISTANCE) {
			return VERY_SMALL_MEAN;
		}

		if (intensityMeasureChanged) {
			setCoeffIndex(); // intensityMeasureChanged is set to false in this method
			//			lnYref_is_not_fresh = true;
		}

		return getMean(iper, mag, rRup, tecRegType, siteTypeParam.getValue());
	}

	/**
	 * @return    The stdDev value
	 */
	public double getStdDev() {
		if (intensityMeasureChanged) {
			setCoeffIndex();// intensityMeasureChanged is set to false in this method
			//		fl	lnYref_is_not_fresh = true;
		}
		return getStdDev(iper, stdDevType);
	}	  

	/**
	 * This creates the lists of independent parameters that the various dependent
	 * parameters (mean, standard deviation, exceedance probability, and IML at
	 * exceedance probability) depend upon. NOTE: these lists do not include anything
	 * about the intensity-measure parameters or any of their internal
	 * independentParamaters.
	 */
	protected void initEqkRuptureParams() {
		magParam = new MagParam(MAG_WARN_MIN, MAG_WARN_MAX);
		eqkRuptureParams.clear();
		eqkRuptureParams.addParameter(magParam);
	}
	
	@Override
	protected void initSiteParams() {
		StringConstraint siteConstraint = new StringConstraint();
		siteConstraint.addString(SITE_TYPE_ROCK);
		siteConstraint.addString(SITE_TYPE_SOIL);
		siteConstraint.setNonEditable();
		siteTypeParam = new StringParameter(SITE_TYPE_NAME, siteConstraint, null);
		siteTypeParam.setInfo(SITE_TYPE_INFO);
		siteTypeParam.setNonEditable();
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
		DoubleConstraint warndistance = new DoubleConstraint(DISTANCE_RUP_WARN_MIN,DISTANCE_RUP_WARN_MAX);
		warndistance.setNonEditable();
		distanceRupParam.setWarningConstraint(warndistance);
		distanceRupParam.setNonEditable();
		propagationEffectParams.addParameter(distanceRupParam);
	}
	/**
	 *  Creates other Parameters that the mean or stdDev depends upon,
	 *  such as the Component or StdDevType parameters.
	 */
	protected void initOtherParams() {

		// init other params defined in parent class
		super.initOtherParams();

		// the Component Parameter
	    StringConstraint constraint = new StringConstraint();
	    constraint.addString(ComponentParam.COMPONENT_AVE_HORZ);
	    constraint.setNonEditable();
	    componentParam = new ComponentParam(constraint,ComponentParam.COMPONENT_AVE_HORZ);
	    
		// the stdDevType Parameter
		StringConstraint stdDevTypeConstraint = new StringConstraint();
		stdDevTypeConstraint.addString(StdDevTypeParam.STD_DEV_TYPE_TOTAL);
		stdDevTypeConstraint.addString(StdDevTypeParam.STD_DEV_TYPE_NONE);
		stdDevTypeConstraint.setNonEditable();
		stdDevTypeParam = new StdDevTypeParam(stdDevTypeConstraint);

		// Seismotectonic region
		constraint = new StringConstraint();
		constraint.addString(FLT_TEC_ENV_SLAB);
		constraint.addString(FLT_TEC_ENV_INTERFACE);
		constraint.setNonEditable();
		tectonicRegionTypeParam = new TectonicRegionTypeParam(constraint,FLT_TEC_ENV_INTERFACE); // Constraint and default value
		
		// add these to the list
//		otherParams.clear();
		otherParams.addParameter(stdDevTypeParam);
	    otherParams.addParameter(componentParam);
		otherParams.replaceParameter(tectonicRegionTypeParam.NAME, tectonicRegionTypeParam);
	}
	
	/**
	 * 
	 */
	protected void initIndependentParamLists() {
		// params that the mean depends upon
		meanIndependentParams.clear();
		meanIndependentParams.addParameter(distanceRupParam);
		meanIndependentParams.addParameter(magParam);
		meanIndependentParams.addParameter(tectonicRegionTypeParam);
//		meanIndependentParams.addParameter(hypoDepthParam);
//		meanIndependentParams.addParameter(fltTypeParam);
		meanIndependentParams.addParameter(siteTypeParam);
	    meanIndependentParams.addParameter(componentParam);

		
		// params that the stdDev depends upon
		stdDevIndependentParams.clear();
		stdDevIndependentParams.addParameter(stdDevTypeParam);
	    stdDevIndependentParams.addParameter(componentParam);

		
		// params that the exceed. prob. depends upon
		exceedProbIndependentParams.clear();
		exceedProbIndependentParams.addParameterList(meanIndependentParams);
		exceedProbIndependentParams.addParameter(stdDevTypeParam);
		exceedProbIndependentParams.addParameter(sigmaTruncTypeParam);
		exceedProbIndependentParams.addParameter(sigmaTruncLevelParam);
		
		// params that the IML at exceed. prob. depends upon
		imlAtExceedProbIndependentParams.addParameterList(exceedProbIndependentParams);
		imlAtExceedProbIndependentParams.addParameter(exceedProbParam);
	}

	/**
	 *  Creates the two supported IM parameters (PGA and SA), as well as the
	 *  independenParameters of SA (periodParam and dampingParam) and adds
	 *  them to the supportedIMParams list. Makes the parameters noneditable.
	 */
	protected void initSupportedIntensityMeasureParams() {
		
		// Create saParam:
		DoubleDiscreteConstraint periodConstraint = new DoubleDiscreteConstraint();
//		if (siteType.equals(SITE_TYPE_ROCK)){
			for (int i = 2; i < periodr.length; i++) {
				periodConstraint.addDouble(new Double(periods[i]));
			}
//		} else {
//			for (int i = 2; i < periods.length; i++) {
//				periodConstraint.addDouble(new Double(periods[i]));
//			}
//		}
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

	private double getMean(int i, double mag, double rRup, String tecRegType, String siteType){

//		// Set the hypocentral depth
//		Location loc = this.eqkRupture.getHypocenterLocation();
//		hypodepth = loc.getDepth();
		
		
		// Computing the hypocentral depth
//		System.out.println("Zhao et al -->"+this.eqkRupture.getInfo());
	
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
		hypodepth = hypoDep;
//		if (INFO) System.out.println("computed hypocentral depth:"+hypodepth);
		
		// This is used just for verification - DO NOT use it for regular calculations
//		hypodepth = this.eqkRupture.getHypocenterLocation().getDepth();
//		System.out.println("real hypocentral depth:"+hypodepth);
		// ---------------------------------------------------------------------- MARCO 2010.03.15
		
		
		double Zt, mean;
		if (tecRegType.equals(FLT_TEC_ENV_INTERFACE)) {
			Zt = 0;
		} else {
			Zt = 1;
		}

		// 
		if (siteType.equals(SITE_TYPE_ROCK)){
			// computation for rock iper=1 is for PGA
			mean = a1r + a2r * mag + c1r[iper] + c2r[iper] * (Math.pow(a3r-mag, 3)) +
				c3r[iper] * Math.log(rRup + a4r * Math.exp(a5r * mag)) + a6r * hypodepth + a7r*Zt;
		}
		else {
			// soil 
			mean = a1s+ a2s * mag+ c1s[iper] + c2s[iper] * (Math.pow(a3s-mag, 3)) +
			c3s[iper] * Math.log(rRup + a4s * Math.exp(a5s * mag)) + a6s * hypodepth + a7s*Zt;
		}
		return mean;
	}	  


	/**
	 * @return The stdDev value
	 */
	private double getStdDev(int iper, String stdDevType) {
		if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_NONE)) {
			return 0;
		}	
		else {
			double sigmaTotal = c4s[iper] + c5s[iper] * mag;
			return (sigmaTotal);
		}
	}

	/**
	 * This listens for parameter changes and updates the primitive parameters accordingly
	 * @param e ParameterChangeEvent
	 */
	public void parameterChange(ParameterChangeEvent e) {

		String pName = e.getParameterName();
		Object val = e.getNewValue();
		if (pName.equals(DistanceRupParameter.NAME)) {
			rRup = ( (Double) val).doubleValue();
		}
		else if (pName.equals(MagParam.NAME)) {
			mag = ( (Double) val).doubleValue();
		}
		else if (pName.equals(TectonicRegionTypeParam.NAME)) {
			tecRegType = tectonicRegionTypeParam.getValue().toString();
			if (D) System.out.println("tecRegType new value:"+tecRegType);
		}		  
		else if (pName.equals(StdDevTypeParam.NAME)) {
			stdDevType = (String) val;
		}
		else if (pName.equals(FaultTypeParam.NAME)) {
			siteType = (String)siteTypeParam.getValue();
		}
//		else if (pName.equals(FaultTypeParam.NAME)) {
//			fltType = (String)fltTypeParam.getValue();
//		}
		else if (pName.equals(PeriodParam.NAME)) {
			intensityMeasureChanged = true;
		}
	}
	/**
	 * Allows to reset the change listeners on the parameters
	 */
	public void resetParameterEventListeners(){
		magParam.removeParameterChangeListener(this);
		distanceRupParam.removeParameterChangeListener(this);
//		hypoDepthParam.removeParameterChangeListener(this);
//		fltTypeParam.removeParameterChangeListener(this);
		tectonicRegionTypeParam.removeParameterChangeListener(this);
		siteTypeParam.removeParameterChangeListener(this);
		stdDevTypeParam.removeParameterChangeListener(this);
		saPeriodParam.removeParameterChangeListener(this);
		this.initParameterEventListeners();
	}
	/**
	 * Adds the parameter change listeners. This allows to listen to when-ever the
	 * parameter is changed.
	 */
	protected void initParameterEventListeners() {

		magParam.addParameterChangeListener(this);
		distanceRupParam.addParameterChangeListener(this);
//		hypoDepthParam.addParameterChangeListener(this);
//		fltTypeParam.addParameterChangeListener(this);
		tectonicRegionTypeParam.addParameterChangeListener(this);
		siteTypeParam.addParameterChangeListener(this);
		stdDevTypeParam.addParameterChangeListener(this);
		saPeriodParam.addParameterChangeListener(this);
	}

	/**
	 * This provides a URL where more info on this model can be obtained
	 * @throws MalformedURLException if returned URL is not a valid URL.
	 * @returns the URL to the AttenuationRelationship document on the Web.
	 */
	// URL Info String
	public URL getInfoURL() throws MalformedURLException{
		return new URL("http://www.opensha.org/documentation/modelsImplemented/attenRel/YoungsEtAl_1997AttenRel.html");
	}
}
