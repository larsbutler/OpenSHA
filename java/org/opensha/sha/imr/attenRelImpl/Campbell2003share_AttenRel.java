package org.opensha.sha.imr.attenRelImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.opensha.commons.data.NamedObjectAPI;
import org.opensha.commons.data.Site;
import org.opensha.commons.param.DoubleConstraint;
import org.opensha.commons.param.DoubleDiscreteConstraint;
import org.opensha.commons.param.StringConstraint;
import org.opensha.commons.param.event.ParameterChangeEvent;
import org.opensha.commons.param.event.ParameterChangeListener;
import org.opensha.commons.param.event.ParameterChangeWarningListener;
import org.opensha.sha.earthquake.EqkRupture;
import org.opensha.sha.imr.AttenuationRelationship;
import org.opensha.sha.imr.ScalarIntensityMeasureRelationshipAPI;
import org.opensha.sha.imr.param.EqkRuptureParams.FaultTypeParam;
import org.opensha.sha.imr.param.EqkRuptureParams.MagParam;
import org.opensha.sha.imr.param.EqkRuptureParams.RakeParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.DampingParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.PGA_Param;
import org.opensha.sha.imr.param.IntensityMeasureParams.PeriodParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.SA_Param;
import org.opensha.sha.imr.param.OtherParams.ComponentParam;
import org.opensha.sha.imr.param.OtherParams.SigmaTruncLevelParam;
import org.opensha.sha.imr.param.OtherParams.SigmaTruncTypeParam;
import org.opensha.sha.imr.param.OtherParams.StdDevTypeParam;
import org.opensha.sha.imr.param.PropagationEffectParams.DistanceRupParameter;


/**
 * <b>Title:</b> Campbell_2003_AttenRel<p>
 *
 * <b>Description:</b> This implements the  GMPE developed by K. W. Campbell (2003, BSSA, vol
 * 93, no 3, pp 1012-1033) <p>
 * The GMPE is adjusted to account the style -of faulting and a default rock soil (Vs30 >=800m/sec)
 * The adjustment coefficients were proposed by S. Drouet [2010] - internal SHARE WP4 report;  
 * Supported Intensity-Measure Parameters:<p>
 * <UL>
 * <LI>PGA - Peak Ground Acceleration
 * <LI>saParam - Response Spectral Acceleration
 * <LI>
 * </UL><p>
 * Other Independent Parameters:<p>
 * <UL>
 * <LI>magParam - moment Magnitude
 * <LI>distanceRupParam - closest distance to surface projection of fault
 * <LI>componentParam - Component of shaking
 * <LI>siteType - adjusted rock
 * <LI>fltType - adjuste to account for style-of-faulting 
 * <LI> stdDevTypeParam - The type of standard deviation (distance dependent)
 * </UL><p>
 *
 * @author     l.danciu
 * @created    October, 2010 - updated July 2011
 * @version    1.01
 */


public class Campbell2003share_AttenRel extends AttenuationRelationship implements
ScalarIntensityMeasureRelationshipAPI,
NamedObjectAPI, ParameterChangeListener {

	/** Short name. */	
	public final static String SHORT_NAME = "Campbell_2003";

	/** Full name. */
	public final static String NAME = "Campbell (2003)";

	/** Version number. */
	private static final long serialVersionUID = 007L;

	/** Period index. */
	private int iper;

	/** Moment magnitude. */
	private double mag;

	/** rake angle. */
	private double rake;

	/** Rupture distance. */
	private double rRup;

	/** Tectonic region type. */
	private String tecRegType;

	/** Standard deviation type. */
	private String stdDevType;

	/** Map period-value/period-index. */
	private HashMap<Double, Integer> indexFromPerHashMap;

	/** For issuing warnings. */
	private transient ParameterChangeWarningListener warningListener = null;

	/**
	 * Construct attenuation relationship. Initialize parameters and parameter
	 * lists.
	 */
	public Campbell2003share_AttenRel(ParameterChangeWarningListener
			warningListener) {

		// creates exceedProbParam
		super();

		this.warningListener = warningListener;

		initSupportedIntensityMeasureParams();

		// Create an Hash map that links the period with its index
		indexFromPerHashMap = new HashMap<Double, Integer>();
		for (int i = 1; i < Campbell2003Constants.PERIOD.length; i++) { 
			indexFromPerHashMap.put(new Double(Campbell2003Constants.PERIOD[i]), 
					new Integer(i));
		}
		// Initialize earthquake Rupture parameters (e.g. magnitude)
		initEqkRuptureParams();
		// Initialize Propagation Effect Parameters (e.g. source-site distance)
		initPropagationEffectParams();
		// Initialize site parameters (e.g. vs30)
		initSiteParams();	
		// Initialize other parameters (e.g. stress drop)
		initOtherParams();
		// Initialize the independent parameters list 
		initIndependentParamLists();
		// Initialize the parameter change listeners
		initParameterEventListeners();
	}

	/**
	 * Creates the three supported IM parameters (PGA, PGV and SA), as well as the
	 * independenParameters of SA (periodParam and dampingParam) and adds them
	 * to the supportedIMParams list. Makes the parameters non-editable.
	 */
	protected final void initSupportedIntensityMeasureParams() {

		// set supported periods for spectral acceleration
		DoubleDiscreteConstraint periodConstraint = new DoubleDiscreteConstraint();
		for (int i = 1; i < Campbell2003Constants.PERIOD.length; i++) {
			periodConstraint.addDouble(new Double(Campbell2003Constants.PERIOD[i]));
		}
		periodConstraint.setNonEditable();
		// set period param (default is 1s, which is provided by Campbell2003 GMPE)
		saPeriodParam = new PeriodParam(periodConstraint);

		// set damping parameter. Empty constructor set damping
		// factor to 5 % (which is the one provided by Campbell2003 GMPE)
		saDampingParam = new DampingParam();

		// initialize spectral acceleration parameter (units: g)
		saParam = new SA_Param(saPeriodParam, saDampingParam);
		saParam.setNonEditable();

		// initialize peak ground acceleration parameter (units: g)
		pgaParam = new PGA_Param();
		pgaParam.setNonEditable();


		// add the warning listeners
		saParam.addParameterChangeWarningListener(warningListener);
		pgaParam.addParameterChangeWarningListener(warningListener);

		// put parameters in the supportedIMParams list
		supportedIMParams.clear();
		supportedIMParams.addParameter(saParam);
		supportedIMParams.addParameter(pgaParam);

	}

	/**
	 * Initialize earthquake rupture parameter (moment magnitude, rake) 
	 * and add to eqkRuptureParams list. Makes the parameters non-editable.
	 */
	protected final void initEqkRuptureParams() {

		// moment magnitude (default 5.0)
		magParam = new MagParam(Campbell2003Constants.MAG_WARN_MIN,
				Campbell2003Constants.MAG_WARN_MAX);
		// Focal mechanism
		rakeParam = new RakeParam(); 
		eqkRuptureParams.clear();
		eqkRuptureParams.addParameter(magParam);
		eqkRuptureParams.addParameter(rakeParam);

	}

	/**
	 * Initialize the style of faulting parameter from the rake angle.
	 * @param rake                      ave. rake of rupture (degrees)
	 */
	protected final void initSiteParams() {

	}
	/**
	 * Initialize Propagation Effect parameters (closest distance to rupture)
	 * and adds them to the propagationEffectParams list. Makes the parameters
	 * non-editable.
	 */
	protected final void initPropagationEffectParams() {

		distanceRupParam = new DistanceRupParameter(
				Campbell2003Constants.DISTANCE_RUP_WARN_MIN);
		distanceRupParam.addParameterChangeWarningListener(warningListener);
		DoubleConstraint warn = new DoubleConstraint(
				Campbell2003Constants.DISTANCE_RUP_WARN_MIN,
				Campbell2003Constants.DISTANCE_RUP_WARN_MAX);
		warn.setNonEditable();
		distanceRupParam.setWarningConstraint(warn);
		distanceRupParam.setNonEditable();

		propagationEffectParams.addParameter(distanceRupParam);
	}

	/**
	 * Initialize other Parameters (standard deviation type, component, sigma
	 * truncation type, sigma truncation level).
	 */
	protected final void initOtherParams() {

		sigmaTruncTypeParam = new SigmaTruncTypeParam();
		sigmaTruncLevelParam = new SigmaTruncLevelParam();

		// stdDevType Parameter
		StringConstraint stdDevTypeConstraint = new StringConstraint();
		stdDevTypeConstraint.addString(StdDevTypeParam.STD_DEV_TYPE_TOTAL);
		stdDevTypeConstraint.addString(StdDevTypeParam.STD_DEV_TYPE_NONE);
		stdDevTypeConstraint.addString(StdDevTypeParam.STD_DEV_TYPE_INTER);
		stdDevTypeConstraint.addString(StdDevTypeParam.STD_DEV_TYPE_INTRA);
		stdDevTypeConstraint.setNonEditable();
		stdDevTypeParam = new StdDevTypeParam(stdDevTypeConstraint);

        // the Component Parameter
		// Geometrical Mean (COMPONENT_AVE_HORZ) = Geometrical MeanI50 (COMPONENT_GMRotI50)
        StringConstraint constraint = new StringConstraint();
        constraint.addString(ComponentParam.COMPONENT_AVE_HORZ);
        constraint.addString(ComponentParam.COMPONENT_GMRotI50);
        constraint.setNonEditable();
//        componentParam = new ComponentParam(constraint, ComponentParam.COMPONENT_GMRotI50);
        componentParam = new ComponentParam(constraint, ComponentParam.COMPONENT_AVE_HORZ);

		// add these to the list
		otherParams.clear();
		otherParams.addParameter(sigmaTruncTypeParam);
		otherParams.addParameter(sigmaTruncLevelParam);
		otherParams.addParameter(stdDevTypeParam);
		otherParams.addParameter(componentParam);
	}

	/**
	 * This creates the lists of independent parameters that the various
	 * dependent parameters (mean, standard deviation, exceedance probability,
	 * and IML at exceedance probability) depend upon. NOTE: these lists do not
	 * include anything about the intensity-measure parameters or any of their
	 * internal independentParamaters.
	 */
	protected final void initIndependentParamLists() {

		// params that the mean depends upon
		meanIndependentParams.clear();
		meanIndependentParams.addParameter(magParam);
		meanIndependentParams.addParameter(distanceRupParam);

		// params that the stdDev depends upon
		stdDevIndependentParams.clear();
		stdDevIndependentParams.addParameter(stdDevTypeParam);

		// params that the exceed. prob. depends upon
		exceedProbIndependentParams.clear();
		exceedProbIndependentParams.addParameterList(meanIndependentParams);
		exceedProbIndependentParams.addParameter(stdDevTypeParam);
		exceedProbIndependentParams.addParameter(sigmaTruncTypeParam);
		exceedProbIndependentParams.addParameter(sigmaTruncLevelParam);

		// params that the IML at exceed. prob. depends upon
		imlAtExceedProbIndependentParams
		.addParameterList(exceedProbIndependentParams);
		imlAtExceedProbIndependentParams.addParameter(exceedProbParam);
	}

	/**
	 * This sets the eqkRupture related parameters (moment magnitude, tectonic
	 * region type, focal depth) based on the eqkRupture passed in. The
	 * internally held eqkRupture object is also set as that passed in. Warning
	 * constrains on magnitude and focal depth are ignored.
	 */
	public final void setEqkRupture(final EqkRupture eqkRupture) {

		magParam.setValueIgnoreWarning(new Double(eqkRupture.getMag()));
		this.eqkRupture = eqkRupture;
		setPropagationEffectParams();
	}

	/**
	 * This sets the site-related parameter (vs30) based on what is in the Site
	 * object passed in. This also sets the internally held Site object as that
	 * passed in.
	 */
	public final void setSite(final Site site) {
	}

	/**
	 * This calculates the Rupture Distance  propagation effect parameter based
	 * on the current site and eqkRupture. <P>
	 */
	protected void setPropagationEffectParams() {

		if ( (this.site != null) && (this.eqkRupture != null)) {
			distanceRupParam.setValue(eqkRupture, site);
		}
	}

	/**
	 * Set period index.
	 */
	protected final void setPeriodIndex() {
		if (im.getName().equalsIgnoreCase(PGA_Param.NAME)) {
			iper = 0;
		} else {
			iper = ((Integer) indexFromPerHashMap.get(saPeriodParam.getValue()))
			.intValue();
		}
	}


	/**
	 * Compute mean. Applies correction for style of faulting and 
	 * generic rock - Vs30 >= 800m/s .
	 */
	public double getMean(){
		if (rRup > USER_MAX_DISTANCE) {
			return VERY_SMALL_MEAN;
		}
		else{
			setPeriodIndex();
			return getMean (iper, mag, rRup, rake);
		}
	}

	public double getStdDev() {
		if (intensityMeasureChanged){
			setPeriodIndex();
		}
		return getStdDev(iper, mag, stdDevType);
	}

	/**
	 * This computes the mean ln(Y) 
	 * @param iper
	 * @param rRup
	 * @param mag
	 * @param rake
	 */
	public double getMean(int iper, double mag, double rRup, double rake){

		double f1 = Double.NaN;
		double f2 = Double.NaN;
		double f3 = Double.NaN;
		double tmp = Double.NaN;
		double R = Double.NaN;
		double lnY = Double.NaN;
		/**
		 * This is to avoid very small values for Rup 
		 * 
		 * */
		if (rRup < 1e-3) {
			rRup = 1;
		}

		tmp = Campbell2003Constants.c7[iper] * Math.exp(Campbell2003Constants.c8[iper] * mag);
		R   = Math.sqrt(rRup*rRup + tmp * tmp);
		f1  = Campbell2003Constants.c2[iper] * mag + Campbell2003Constants.c3[iper] * Math.pow((8.5-mag),2);
		f2  = Campbell2003Constants.c4[iper] * Math.log (R) + 
		(Campbell2003Constants.c5[iper] + Campbell2003Constants.c6[iper] * mag) * rRup;
		f3  = computedf3(iper, rRup);
		double[] f = computeStyleOfFaultingTerm(iper, rake);

		lnY = Campbell2003Constants.c1[iper] + f1 + f2 + f3;
        lnY = Math.exp(lnY) * f[2];
		
        return Math.log(lnY);
	}

	private double computedf3(int iper, double rRup) {
		double f3factor = Double.NaN;
		if (rRup <= Campbell2003Constants.R1) {
			f3factor = 0.00;
		} else if (rRup > Campbell2003Constants.R1 && rRup <= Campbell2003Constants.R2) {
			f3factor = Campbell2003Constants.c9[iper] * (Math.log(rRup) - 
					Math.log(Campbell2003Constants.R1));
		} else if (rRup > Campbell2003Constants.R2) {
			f3factor = Campbell2003Constants.c9[iper] * (Math.log(rRup) - 
					Math.log(Campbell2003Constants.R1)) + 
					Campbell2003Constants.c10[iper] * (Math.log(rRup) - 
							Math.log(Campbell2003Constants.R2));
		}
		return f3factor;
	};

	/**
	 * Compute style-of-faulting adjustment
	 **/		
	public double[] computeStyleOfFaultingTerm(final int iper, final double rake) {
		double[] f = new double[3];
		if (rake > Campbell2003Constants.FLT_TYPE_NORMAL_RAKE_LOWER
				&& rake <= Campbell2003Constants.FLT_TYPE_NORMAL_RAKE_UPPER){
			f[0] = 1.0;
			f[1] = 0.0;
			f[2] = f[0]*Math.pow(Campbell2003Constants.Frss[iper], (1-Campbell2003Constants.pR)) * 
			Math.pow(Campbell2003Constants.Fnss, - Campbell2003Constants.pN);
		} else if (rake > Campbell2003Constants.FLT_TYPE_REVERSE_RAKE_LOWER
				&& rake <= Campbell2003Constants.FLT_TYPE_REVERSE_RAKE_UPPER) {
			f[0] = 0.0;
			f[1] = 1.0;
			f[2] = f[1] * Math.pow(Campbell2003Constants.Frss[iper], -Campbell2003Constants.pR) * 
			Math.pow(Campbell2003Constants.Fnss, (1-Campbell2003Constants.pN));;
		} else {
			f[0] = 0.0;
			f[1] = 0.0;
			f[2] = Math.pow(Campbell2003Constants.Frss[iper], -Campbell2003Constants.pR) * 
			Math.pow(Campbell2003Constants.Fnss, -Campbell2003Constants.pN);
		}
		return f;
	}

	public double getStdDev(int iper, double mag, String stdDevType ) {
		double sigma = Double.NaN;;

		if(stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_NONE))
			return 0;
		else {
			final double M1 = 7.16; 
			if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_TOTAL_MAG_DEP))
				if (mag < M1) {
					sigma = (Campbell2003Constants.c11[iper] + Campbell2003Constants.c12[iper] * mag);
				} 
				else {
					sigma = Campbell2003Constants.c13[iper];
				}
			return (sigma);
		}
	}

	/**
	 * Allows the user to set the default parameter values for the selected
	 * Attenuation Relationship.
	 */
	public final void setParamDefaults() {

		magParam.setValueAsDefault();
		rakeParam.setValueAsDefault();
		distanceRupParam.setValueAsDefault();
		saPeriodParam.setValueAsDefault();
		saDampingParam.setValueAsDefault();
		saParam.setValueAsDefault();
		pgaParam.setValueAsDefault();
		stdDevTypeParam.setValueAsDefault();
		sigmaTruncTypeParam.setValueAsDefault();
		sigmaTruncLevelParam.setValueAsDefault();
		componentParam.setValueAsDefault();
	}
	/**
	 * This listens for parameter changes and updates the primitive parameters
	 * accordingly
	 */
	public final void parameterChange(final ParameterChangeEvent e) {

		String pName = e.getParameterName();
		Object val = e.getNewValue();

		if (pName.equals(MagParam.NAME)) {
			mag = ((Double) val).doubleValue();
		} else if (pName.equals(DistanceRupParameter.NAME)) {
			rRup = ((Double) val).doubleValue();
		} else if (pName.equals(StdDevTypeParam.NAME)) {
			stdDevType = (String) val;
		} else if (pName.equals(FaultTypeParam.NAME)) {
			rake = ((Double) val).doubleValue();
		}
	}
	/**
	 * Allows to reset the change listeners on the parameters
	 */
	public void resetParameterEventListeners(){
		magParam.removeParameterChangeListener(this);
		rakeParam.removeParameterChangeListener(this);
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

		magParam.addParameterChangeListener(this);
		rakeParam.addParameterChangeListener(this);
		distanceRupParam.addParameterChangeListener(this);
		stdDevTypeParam.addParameterChangeListener(this);
		saPeriodParam.addParameterChangeListener(this);
	}
	/**
	 * Get the name of this IMR.
	 */
	public final String getName() {
		return NAME;
	}

	/**
	 * Returns the Short Name of each AttenuationRelationship
	 * 
	 */
	public final String getShortName() {
		return SHORT_NAME;
	}
	/**
	 * This provides a URL where more info on this model can be obtained
	 * @throws MalformedURLException if returned URL is not a valid URL.
	 * @returns the URL to the AttenuationRelationship document on the Web.
	 */
	public URL getInfoURL() throws MalformedURLException{
		return new URL("http://www.opensha.org/documentation/modelsImplemented/attenRel/Campbell2003.html");
	}
	/**
	 * For testing
	 * 
	 */

	public static void main(String[] args) {

		Campbell2003share_AttenRel ar = new Campbell2003share_AttenRel(null);
		ar.setParamDefaults();
		ar.setIntensityMeasure(SA_Param.NAME);
     		for (int i=0; i < 1; i++){
			 System.out.println(i + ". T(sec) ="  + Campbell2003Constants.PERIOD[i]);
			 System.out.println(" mag = 5.00 " + "r =    1.00 " + " SA = " + Math.exp(ar.getMean(i, 5.00, 1.00, -90)));
//			 System.out.println(" mag = 5.00 " + "r =   20.00 " + " SA = " + Math.exp(ar.getMean(i, 5.00,   20.00, 800, -90)));
//			 System.out.println(" mag = 5.00 " + "r =   30.00 " + " SA = " + Math.exp(ar.getMean(i, 5.00,   30.00, 800, -90)));
//			 System.out.println(" mag = 5.00 " + "r =   50.00 " + " SA = " + Math.exp(ar.getMean(i, 5.00,   50.00, 800, -90)));
//			 System.out.println(" mag = 5.00 " + "r =   75.00 " + " SA = " + Math.exp(ar.getMean(i, 5.00,   75.00, 800, -90)));
//			 System.out.println(" mag = 5.00 " + "r =  100.00 " + " SA = " + Math.exp(ar.getMean(i, 5.00,  100.00, 800, -90)));
//			 System.out.println(" mag = 5.00 " + "r =  200.00 " + " SA = " + Math.exp(ar.getMean(i, 5.00,  200.00, 800, -90)));
//			 System.out.println(" mag = 5.00 " + "r =  500.00 " + " SA = " + Math.exp(ar.getMean(i, 5.00,  500.00, 800, -90)));
//			 System.out.println(" mag = 5.00 " + "r = 1000.00 " + " SA = " + Math.exp(ar.getMean(i, 5.00, 1000.00, 800, -90)));
			 System.out.println(ar.getStdDev(i, 5.00, StdDevTypeParam.STD_DEV_TYPE_TOTAL_MAG_DEP.toString()));
			 System.out.println (ar.getStdDev(i, 7.00, StdDevTypeParam.STD_DEV_TYPE_TOTAL_MAG_DEP.toString()));
			 
		 }
	}	

}
