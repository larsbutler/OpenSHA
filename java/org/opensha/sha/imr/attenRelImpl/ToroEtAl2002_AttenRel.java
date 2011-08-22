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
import org.opensha.sha.imr.param.PropagationEffectParams.DistanceJBParameter;
import org.opensha.sha.imr.param.PropagationEffectParams.DistanceRupParameter;
import org.opensha.sha.imr.param.SiteParams.Vs30_Param;


/**
 * <b>Title:</b> Toro_2002_AttenRel<p>
 *
 * <b>Description:</b> This implements the  updated GMPE developed by Toro et al - 2002, 
 *  (www.riskeng.com/PDF/atten_toro_extended.pdf)
 * The GMPE is adjusted to account the style -of faulting and a default rock soil (Vs30 >=800m/sec)
 * The adjustment coefficients were proposed by S. Drouet [2010];  
 * Supported Intensity-Measure Parameters:<p>
 * <UL>
 * <LI>PGA - Peak Ground Velocity
 * <LI>saParam - Response Spectral Acceleration
 * <LI>
 * </UL><p>
 * Other Independent Parameters:<p>
 * <UL>
 * <LI>magParam - moment Magnitude
 * <LI>distanceJBParam - JB distance
 * <LI>componentParam - Component of shaking
 * <LI>siteType - adjusted rock
 * <LI>fltType - adjuste to account for style-of-faulting 
 * <LI> stdDevTypeParam - The type of standard deviation (magnitude dependent)
 * </UL><p>
 *
 * @author     l.danciu
 * @created    July, 2011
 * @version    1.0
 */


public class ToroEtAl2002_AttenRel extends AttenuationRelationship implements
ScalarIntensityMeasureRelationshipAPI,NamedObjectAPI, ParameterChangeListener {

	// Debugging stuff
	private final static String C = "ToroEtAl_2002_AttenRel";
	private final static boolean D = false;
	public final static String SHORT_NAME = "ToroEtAl2002";
	private static final long serialVersionUID = 1234567890987654353L;


	// Name of IMR
	public final static String NAME = "Toro et al. (2002)";

	/** Period index. */
	private int iper;

	/** Moment magnitude. */
	private double mag;

	/** Vs 30. */
	private double vs30;

	/** rake angle. */
	private double rake;

	/** Rupture distance. */
	private double rJB;

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
	public ToroEtAl2002_AttenRel(ParameterChangeWarningListener
			warningListener) {

		super();

		this.warningListener = warningListener;

		initSupportedIntensityMeasureParams();
		indexFromPerHashMap = new HashMap<Double, Integer>();
		for (int i = 0; i < ToroEtAl2002Constants.PERIOD.length ; i++) {
			indexFromPerHashMap.put(new Double(ToroEtAl2002Constants.PERIOD[i]), 
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
	 * Creates the two supported IM parameters (PGA and SA), as well as the
	 * independenParameters of SA (periodParam and dampingParam) and adds them
	 * to the supportedIMParams list. Makes the parameters non-editable.
	 */
	protected final void initSupportedIntensityMeasureParams() {

		// set supported periods for spectral acceleration
		DoubleDiscreteConstraint periodConstraint = new DoubleDiscreteConstraint();
		for (int i = 0; i < ToroEtAl2002Constants.PERIOD.length; i++) {
			periodConstraint.addDouble(new Double(ToroEtAl2002Constants.PERIOD[i]));
		}
		periodConstraint.setNonEditable();
		// set period param (default is 1s, which is provided by Toro et al 2002 GMPE)
		saPeriodParam = new PeriodParam(periodConstraint);

		// set damping parameter. Empty constructor set damping
		// factor to 5 % (which is the one provided by Toro et 2002 GMPE)
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
		magParam = new MagParam(ToroEtAl2002Constants.MAG_WARN_MIN,
				ToroEtAl2002Constants.MAG_WARN_MAX);
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

		// vs30 parameters (constrains are not set, default value to 800 m/s)
		vs30Param = new Vs30_Param();

		siteParams.clear();
		siteParams.addParameter(vs30Param);
	}

	/**
	 * Initialize Propagation Effect parameters (JB distance)
	 * and adds them to the propagationEffectParams list. Makes the parameters
	 * non-editable.
	 */
	protected final void initPropagationEffectParams() {

		distanceJBParam = new DistanceJBParameter(
				ToroEtAl2002Constants.DISTANCE_JB_WARN_MIN);
		distanceJBParam.addParameterChangeWarningListener(warningListener);
		DoubleConstraint warn = new DoubleConstraint(
				ToroEtAl2002Constants.DISTANCE_JB_WARN_MIN,
				ToroEtAl2002Constants.DISTANCE_JB_WARN_MAX);
		warn.setNonEditable();
		distanceJBParam.setWarningConstraint(warn);
		distanceJBParam.setNonEditable();

		propagationEffectParams.addParameter(distanceJBParam);
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
		stdDevTypeConstraint.setNonEditable();
		stdDevTypeParam = new StdDevTypeParam(stdDevTypeConstraint);

		// component Parameter
		StringConstraint constraint = new StringConstraint();
		constraint.addString(ComponentParam.COMPONENT_AVE_HORZ);
		constraint.setNonEditable();
		componentParam = new ComponentParam(constraint,
				ComponentParam.COMPONENT_AVE_HORZ);

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
		meanIndependentParams.addParameter(vs30Param);
		meanIndependentParams.addParameter(distanceJBParam);

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
		vs30Param.setValueIgnoreWarning((Double) site.getParameter(
				Vs30_Param.NAME).getValue());
		this.site = site;
		setPropagationEffectParams();
	}

	/**
	 * This calculates the Rupture Distance  propagation effect parameter based
	 * on the current site and eqkRupture. <P>
	 */
	protected void setPropagationEffectParams() {

		if ( (this.site != null) && (this.eqkRupture != null)) {
			distanceJBParam.setValue(eqkRupture, site);
		}
	}

	/**
	 * Set period index. PGV is missing
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
		if (rJB > USER_MAX_DISTANCE) {
			return VERY_SMALL_MEAN;
		}
		else{
			setPeriodIndex();
			return getMean (iper, mag, rJB, vs30, rake);
		}
	}

	public double getStdDev() {
		if (intensityMeasureChanged){
			setPeriodIndex();
		}
		return getStdDev(iper, mag, rJB, stdDevType, vs30);
	}

	/**
	 * This computes the mean ln(Y) 
	 * @param iper
	 * @param rJB
	 * @param mag
	 * @param vs30
	 * @param rake
	 */
	public double getMean(final int iper, double mag, double rJB, double vs30, double rake){
		/**
		 * This is to avoid very small values for rJB 
		 * 
		 * */
		if (rJB < 1e-3) {
			rJB = 1;
		}
		
		double lnY_adj = Double.NaN;;
		double[] s = computeSiteTerm(iper, vs30);
		double[] f = computeStyleOfFaultingTerm(iper, rake);
		double lnY_Hrock = computeHardRockResponse(iper, mag, rJB);
//		System.out.println(lnY_Hrock);

		lnY_adj = Math.exp(lnY_Hrock) * f[2] * s[0];
//		lnY_adj = lnY_Hrock;
//		System.out.println (s[0]);
//		System.out.println (f[2]);
//		System.out.println (lnY_adj);

		return Math.log(lnY_adj);

	};

	private double computeHardRockResponse (int iper, double mag, double rJB){

		double magDiff = mag - 6.0;

		double rM = Math.sqrt(rJB * rJB + ToroEtAl2002Constants.c7[iper] * ToroEtAl2002Constants.c7[iper] *
				    Math.pow(Math.exp(-1.25 + 0.227 * mag), 2));

		double f1 = ToroEtAl2002Constants.c1[iper] + ToroEtAl2002Constants.c2[iper]*magDiff +
		             ToroEtAl2002Constants.c3[iper] * magDiff * magDiff;

		double f2 = ToroEtAl2002Constants.c4[iper] * Math.log (rM); 

		double f3 = (ToroEtAl2002Constants.c5[iper]-ToroEtAl2002Constants.c4[iper]) * 
		             Math.max(Math.log(rM/100), 0);

		double f4 = ToroEtAl2002Constants.c6[iper]*rM;

		double lnY = f1 - f2 - f3 - f4;

		return lnY;
	}
	/**
	 * Compute style-of-faulting adjustment
	**/		
			private double[] computeStyleOfFaultingTerm(final int iper, final double rake) {
				double[] f = new double[3];
				if (rake > ToroEtAl2002Constants.FLT_TYPE_NORMAL_RAKE_LOWER
					&& rake <= ToroEtAl2002Constants.FLT_TYPE_NORMAL_RAKE_UPPER){
					f[0] = 1.0;
					f[1] = 0.0;
					f[2] = f[0]*Math.pow(ToroEtAl2002Constants.Frss[iper], (1-ToroEtAl2002Constants.pR)) * 
					            Math.pow(ToroEtAl2002Constants.Fnss, - ToroEtAl2002Constants.pN);
				} else if (rake > ToroEtAl2002Constants.FLT_TYPE_REVERSE_RAKE_LOWER
						&& rake <= ToroEtAl2002Constants.FLT_TYPE_REVERSE_RAKE_UPPER) {
					f[0] = 0.0;
					f[1] = 1.0;
					f[2] = f[1] * Math.pow(ToroEtAl2002Constants.Frss[iper], -ToroEtAl2002Constants.pR) * 
					              Math.pow(ToroEtAl2002Constants.Fnss, (1-ToroEtAl2002Constants.pN));;
				} else {
					f[0] = 0.0;
					f[1] = 0.0;
					f[2] = Math.pow(ToroEtAl2002Constants.Frss[iper], -ToroEtAl2002Constants.pR) * 
					       Math.pow(ToroEtAl2002Constants.Fnss, -ToroEtAl2002Constants.pN);
				}
				return f;
			}
			/**
			 * Compute adjustment factor for rock (vs30 = 800m/s)
			**/		
			private double[] computeSiteTerm(final int iper, final double vs30) {
				double[] s = new double[2];
				if (vs30 == ToroEtAl2002Constants.SITE_TYPE_ROCK_UPPER_BOUND) {
					s[0] = ToroEtAl2002Constants.AFrock[iper];
					s[1] = 0.0; 
				} else {
					s[0] = 0.0;
					s[1] = 0.0;
				}
				return s;
			}


	public double getStdDev(int iper, double mag, double rJB, String stdDevType, double vs30 ) {
		double sigmaaM  = Double.NaN;
		double sigmaaR  = Double.NaN;
		double sigmae   = Double.NaN;
		double sigmatot = Double.NaN;

		if(stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_NONE))
			return 0;
		else {
			if (mag <= 5.0) {
				sigmaaM = ToroEtAl2002Constants.m50[iper]; 
			}
			else if (mag <= 5.5) {
				sigmaaM = ToroEtAl2002Constants.m50[iper] + 
				         (ToroEtAl2002Constants.m55[iper] - ToroEtAl2002Constants.m50[iper]) / 
				          (5.5-5.0) * (mag - 5.0);
			}
			else if (mag <= 8.0) {
				sigmaaM = ToroEtAl2002Constants.m55[iper] + 
				         (ToroEtAl2002Constants.m80[iper] - ToroEtAl2002Constants.m55[iper]) /
				         (8.0-5.5) * (mag - 5.5);
			}
			else {
				sigmaaM = ToroEtAl2002Constants.m80[iper];
			}

			if (rJB <= 5.0) {
				sigmaaR = ToroEtAl2002Constants.r05[iper]; 
			}
			else if (rJB <= 20.0) {
				sigmaaR = ToroEtAl2002Constants.r05[iper] + 
				(ToroEtAl2002Constants.r20[iper] - ToroEtAl2002Constants.r05[iper]) / 
				(20.0-5.0) * (rJB-5.0);
			}
			else {
				sigmaaR = ToroEtAl2002Constants.r20[iper];
			}	  

			if (ToroEtAl2002Constants.PERIOD[iper] == 2.0) {
				sigmae = 0.34 + 0.06 * (mag - 6.0);
			}
			else {
				sigmae = 0.36 + 0.07 * (mag - 6.0);
			}

			sigmatot = (Math.sqrt(sigmaaM*sigmaaM+sigmaaR*sigmaaR+sigmae*sigmae));

			if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_TOTAL) && 
					vs30 == ToroEtAl2002Constants.SITE_TYPE_ROCK_UPPER_BOUND) {
				return sigmatot * ToroEtAl2002Constants.sig_AFrock[iper];
			}
			else {
				return sigmatot ;
			}	  
		}
	}

	/**
	 * Allows the user to set the default parameter values for the selected
	 * Attenuation Relationship.
	 */
	public final void setParamDefaults() {

		magParam.setValueAsDefault();
		rakeParam.setValueAsDefault();
		vs30Param.setValueAsDefault();
		distanceJBParam.setValueAsDefault();
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
		} else if (pName.equals(Vs30_Param.NAME)) {
			vs30 = ((Double) val).doubleValue();
		} else if (pName.equals(DistanceRupParameter.NAME)) {
			rJB = ((Double) val).doubleValue();
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
		vs30Param.removeParameterChangeListener(this);
		distanceJBParam.removeParameterChangeListener(this);
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
		vs30Param.addParameterChangeListener(this);
		distanceJBParam.addParameterChangeListener(this);
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
	 * 
	 * @throws MalformedURLException if returned URL is not a valid URL.
	 * @returns the URL to the AttenuationRelationship document on the Web.
	 */
	public URL getAttenuationRelationshipURL() throws MalformedURLException{
		return new URL("http://www.opensha.org/documentation/modelsImplemented/attenRel/ToroEtAl_2002.html");
	}

	/**
	 * For testing
	 * 
	 */
	
	public static void main(String[] args) {
		
		ToroEtAl2002_AttenRel ar = new ToroEtAl2002_AttenRel(null);

		System.out.println("A first test on ToroEtAl2002_AttenRel GMPE!");
		for (int i=0; i < 8; i++){
			System.out.println("mean = " + Math.exp(ar.getMean(i, 7.50, 10, 800, 45)));
		}
	}	

}
