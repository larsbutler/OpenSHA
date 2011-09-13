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
import org.opensha.sha.imr.param.EqkRuptureParams.MagParam;
import org.opensha.sha.imr.param.EqkRuptureParams.RakeParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.DampingParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.PGA_Param;
import org.opensha.sha.imr.param.IntensityMeasureParams.PGV_Param;
import org.opensha.sha.imr.param.IntensityMeasureParams.PeriodParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.SA_Param;
import org.opensha.sha.imr.param.OtherParams.ComponentParam;
import org.opensha.sha.imr.param.OtherParams.SigmaTruncLevelParam;
import org.opensha.sha.imr.param.OtherParams.SigmaTruncTypeParam;
import org.opensha.sha.imr.param.OtherParams.StdDevTypeParam;
import org.opensha.sha.imr.param.PropagationEffectParams.DistanceJBParameter;
import org.opensha.sha.imr.param.SiteParams.Vs30_Param;

/**
 * <b>Title:</b> BindiEtAl_2010_AttenRel
 * <p>
 * 
 * <b>Description:</b> Class implementing attenuation relationship described in:
 * "Horizontal and Vertical Ground Motion Prediction Equations derived from the Italian Accelerometric Archive (ITACA)",
 * Dino Bindi,  L. Luizi, M. Massa, F. Pacor, Bulletin of Earthquake Engineering 
 * Vol. 8, pp 1209-1230, 2010.
 * Note:
 * This class implements only the JB distance
 * <p>
 * 
 * Supported Intensity-Measure Parameters:
 * <p>
 * <UL>
 * <LI>pgaParam - Peak Ground Acceleration (g)
 * <LI>pgvParam - Peak Ground Velocity (cm/s)
 * <LI>saParam - Response Spectral Acceleration (g)
 * </UL>
 * <p>
 * Other Independent Parameters:
 * <p>
 * <UL>
 * <LI>magParam - moment magnitude
 * <LI>rakeParam - rake angle. Used to establish if event is normal (-150 < rake
 * < -30) or reverse ((30 < rake < 150).
 * <LI>distanceJBParam - Joyner-Boore distance
 * 
 * <LI>vs30Param - shear wave velocity (m/s) averaged over the top 30 m of the
 * soil profile; 
 * The model assumes the following classification: 
 * vs30 < 360 m/s -- soft soil, 
 * 360 <= vs30 <= 750 -> stiff soil, 
 * vs30 > 750 -> rock.
 * <LI>componentParam - average horizontal
 * <LI>stdDevTypeParam - total, inter-event, intra-event, none
 * </UL>
 * <p>
 * 
 * <p>
 * 
 * Verification -
 * 
 * 
 * 
 * </p>
 * 
 ** 
 * @author L. Danciu
 * @created August 20, 2010
 * @version 1.0
 */

public class BindiEtAl_2010jb_AttenRel extends AttenuationRelationship implements
       ScalarIntensityMeasureRelationshipAPI, NamedObjectAPI,
       ParameterChangeListener {

	/** Short name. */
	public final static String SHORT_NAME = "BindiEtAl2010";

	/** Full name. */
	public final static String NAME = "Bindi Et Al 2010";

	/** Version number. */
	private static final long serialVersionUID = 1234567890987654353L;

	/** Period index. */
	private int iper;

	/** Moment magnitude. */
	private double mag;

	/** rake angle. */
	private double rake;

	/** Joyner and Boore distance. */
	private double rJB;

	/** Vs 30. */
	private double vs30;

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
	public BindiEtAl_2010jb_AttenRel(
			final ParameterChangeWarningListener warningListener) {

		// creates exceedProbParam
		super();

		this.warningListener = warningListener;

		initSupportedIntensityMeasureParams();

		// Create an Hash map that links the period with its index
		indexFromPerHashMap = new HashMap<Double, Integer>();
		for (int i = 1; i < BindiEtAl2010Constants.PERIOD.length; i++) {
			indexFromPerHashMap.put(new Double(BindiEtAl2010Constants.PERIOD[i]),
					new Integer(i));
		}

		initEqkRuptureParams();
		initPropagationEffectParams();
		initSiteParams();
		initOtherParams();
		initIndependentParamLists();
		initParameterEventListeners();
	}

	/**
	 * Creates the three supported IM parameters (PGA, SA, and PGV), as well as
	 * the independenParameters of SA (periodParam and dampingParam) and adds
	 * them to the supportedIMParams list. Makes the parameters non-editable.
	 */
	protected final void initSupportedIntensityMeasureParams() {

		// set supported periods for spectral acceleration
		DoubleDiscreteConstraint periodConstraint = new DoubleDiscreteConstraint();
		for (int i = 1; i < BindiEtAl2010Constants.PERIOD.length; i++) {
			periodConstraint.addDouble(new Double(BindiEtAl2010Constants.PERIOD[i]));
		}
		periodConstraint.setNonEditable();
		// set period param (default is 1s, which is provided by BindiEtAl2010 GMPE)
		saPeriodParam = new PeriodParam(periodConstraint);

		// set damping parameter. Empty constructor set damping
		// factor to 5 % (which is the one provided by BindiEtAl2010 GMPE)
		saDampingParam = new DampingParam();

		// initialize spectral acceleration parameter (units: g)
		saParam = new SA_Param(saPeriodParam, saDampingParam);
		saParam.setNonEditable();

		// initialize peak ground acceleration parameter (units: g)
		pgaParam = new PGA_Param();
		pgaParam.setNonEditable();

		// initialize peak ground velocity parameter (units: cm/sec)
		pgvParam = new PGV_Param();
		pgvParam.setNonEditable();

		// add the warning listeners
		saParam.addParameterChangeWarningListener(warningListener);
		pgaParam.addParameterChangeWarningListener(warningListener);
		pgvParam.addParameterChangeWarningListener(warningListener);

		// put parameters in the supportedIMParams list
		supportedIMParams.clear();
		supportedIMParams.addParameter(saParam);
		supportedIMParams.addParameter(pgaParam);
		supportedIMParams.addParameter(pgvParam);

	}

	/**
	 * Initialize earthquake rupture parameter (moment magnitude, rake) and add
	 * to eqkRuptureParams list. Makes the parameters non-editable.
	 */
	protected final void initEqkRuptureParams() {

		// moment magnitude (default 5.5)
		magParam = new MagParam(BindiEtAl2010Constants.MAG_WARN_MIN,
				BindiEtAl2010Constants.MAG_WARN_MAX);
		// rake angle (default 0.0 -> strike-slip)
		rakeParam = new RakeParam();

		eqkRuptureParams.clear();
		eqkRuptureParams.addParameter(magParam);
		eqkRuptureParams.addParameter(rakeParam);
	}

	/**
	 * Initialize site parameters (vs30) and adds it to the siteParams list.
	 * Makes the parameters non-editable.
	 */
	protected final void initSiteParams() {

		// vs30 parameters (constrains are not set, default value to 760 m/s)
		vs30Param = new Vs30_Param();

		siteParams.clear();
		siteParams.addParameter(vs30Param);
	}

	/**
	 * Initialize Propagation Effect parameters (Joyner-Boore) and adds
	 * them to the propagationEffectParams list. Makes the parameters
	 * non-editable.
	 */
	protected final void initPropagationEffectParams() {

		distanceJBParam = new DistanceJBParameter(
		        BindiEtAl2010Constants.DISTANCE_JB_WARN_MIN);
		distanceJBParam.addParameterChangeWarningListener(warningListener);
		DoubleConstraint warn = new DoubleConstraint(
				BindiEtAl2010Constants.DISTANCE_JB_WARN_MIN,
				BindiEtAl2010Constants.DISTANCE_JB_WARN_MAX);
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
        componentParam = new ComponentParam(constraint, ComponentParam.COMPONENT_GMRotI50);
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
		meanIndependentParams.addParameter(rakeParam);
		meanIndependentParams.addParameter(distanceJBParam);
		meanIndependentParams.addParameter(vs30Param);

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
	 * Adds the parameter change listeners. This allows to listen to when-ever
	 * the parameter is changed.
	 */
	protected void initParameterEventListeners() {

		magParam.addParameterChangeListener(this);
		rakeParam.addParameterChangeListener(this);
		distanceJBParam.addParameterChangeListener(this);
		vs30Param.addParameterChangeListener(this);
		componentParam.addParameterChangeListener(this);
		stdDevTypeParam.addParameterChangeListener(this);

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
		} else if (pName.equals(RakeParam.NAME)) {
			rake = ((Double) val).doubleValue();
		} else if (pName.equals(DistanceJBParameter.NAME)) {
			rJB = ((Double) val).doubleValue();
		} else if (pName.equals(Vs30_Param.NAME)) {
			vs30 = ((Double) val).doubleValue();
		} else if (pName.equals(StdDevTypeParam.NAME)) {
			stdDevType = (String) val;
		}
	}

	/**
	 * Allows to reset the change listeners on the parameters
	 */
	public void resetParameterEventListeners() {

		magParam.removeParameterChangeListener(this);
		rakeParam.removeParameterChangeListener(this);
		distanceJBParam.removeParameterChangeListener(this);
		vs30Param.removeParameterChangeListener(this);
		stdDevTypeParam.removeParameterChangeListener(this);
		this.initParameterEventListeners();
	}

	/**
	 * This sets the eqkRupture related parameters (moment magnitude, rake
	 * angle) based on the eqkRupture passed in. The internally held eqkRupture
	 * object is also set as that passed in.
	 */
	public final void setEqkRupture(final EqkRupture eqkRupture) {

		magParam.setValueIgnoreWarning(new Double(eqkRupture.getMag()));
		if (!Double.isNaN(eqkRupture.getAveRake())) {
			rakeParam.setValue(eqkRupture.getAveRake());
		}
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
	 * This calculates the JB Distance propagation effect parameter based on the
	 * current site and eqkRupture.
	 */
	protected void setPropagationEffectParams() {

		if ((this.site != null) && (this.eqkRupture != null)) {
			distanceJBParam.setValue(eqkRupture, site);
		}
	}

	/**
	 * Set period index.
	 */
	protected final void setPeriodIndex() {
		if (im.getName().equalsIgnoreCase(PGV_Param.NAME)) {
			iper = 0;
		} else if (im.getName().equalsIgnoreCase(PGA_Param.NAME)) {
			iper = 1;
		} else {
			iper = ((Integer) indexFromPerHashMap.get(saPeriodParam.getValue()))
			.intValue();
		}
	}

	/**
	 * Compute mean.
	 */
	public double getMean() {

		if (rJB > USER_MAX_DISTANCE) {
			return VERY_SMALL_MEAN;
		} else {
			setPeriodIndex();
			return getMean(iper, mag, rJB, vs30);
		}
	}

	/**
	 * Compute standard deviation.
	 */
	public final double getStdDev() {

		setPeriodIndex();
		return getStdDev(iper, stdDevType);
	}

	/**
	 * Allows the user to set the default parameter values for the selected
	 * Attenuation Relationship.
	 */
	public final void setParamDefaults() {

		magParam.setValueAsDefault();
		rakeParam.setValueAsDefault();
		distanceJBParam.setValueAsDefault();
		vs30Param.setValueAsDefault();
		saPeriodParam.setValueAsDefault();
		saDampingParam.setValueAsDefault();
		saParam.setValueAsDefault();
		pgaParam.setValueAsDefault();
		pgvParam.setValueAsDefault();
		stdDevTypeParam.setValueAsDefault();
		sigmaTruncTypeParam.setValueAsDefault();
		sigmaTruncLevelParam.setValueAsDefault();
		componentParam.setValueAsDefault();

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
	 * Compute mean (natural logarithm of median ground motion) for 
	 * b) JB Distance - getMeanJB.
	 */
	public double getMean(int iper, double mag, final double rJB, final double vs30) {

		double logY = Double.NaN;

		double soilTerms = setSoilTermsJB(iper, vs30);
		double mag_fact = (mag-BindiEtAl2010Constants.MAG_Ref);

		double term1 = BindiEtAl2010Constants.b1_jb[iper] * mag_fact; 
		
		double term2 = BindiEtAl2010Constants.b2_jb[iper] * Math.pow(mag_fact,2);
		
		double r = Math.sqrt(rJB*rJB + Math.pow(BindiEtAl2010Constants.h_jb[iper], 2));
		
		double term3 = (BindiEtAl2010Constants.c1_jb[iper] + 
		               BindiEtAl2010Constants.c2_jb[iper] * mag_fact) * Math.log10(r);

		logY = BindiEtAl2010Constants.a_jb[iper] + term1 + term2 + term3 + soilTerms;
		
		logY = logY * BindiEtAl2010Constants.LOG10_2_LN;

		if  (iper == 0) {
			logY = Math.exp(logY);

		}
		else {
			logY = ((Math.exp(logY)
					* BindiEtAl2010Constants.CMS2_TO_G_CONVERSION_FACTOR));
		}

		return  Math.log(logY);

	}

	private double setSoilTermsJB(final int iper, final double vs30) {
		double soilTerms = Double.NaN;

		if (vs30 > BindiEtAl2010Constants.ROCK_SOIL_LOWER_BOUND) {
			soilTerms = BindiEtAl2010Constants.C0_jb[iper];
		}
		else if (vs30 > BindiEtAl2010Constants.STIFF_SOIL_UPPER_BOUND
				&& vs30 <= BindiEtAl2010Constants.ROCK_SOIL_LOWER_BOUND) {
			soilTerms = BindiEtAl2010Constants.C1_jb[iper];
		}
		else if (vs30 > BindiEtAl2010Constants.SOFT_SOIL_UPPER_BOUND
				&& vs30 <= BindiEtAl2010Constants.STIFF_SOIL_UPPER_BOUND) {
			soilTerms = BindiEtAl2010Constants.C2_jb[iper];
		} else {
			soilTerms = 0;
		}
		return soilTerms;
	}


	/**
	 * Get Standard Deviation
	 */	
	public double getStdDev(int iper, String stdDevType) {
		if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_NONE))
			return 0;
		else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_INTER))
			return BindiEtAl2010Constants.LOG10_2_LN
			* BindiEtAl2010Constants.INTER_EVENT_STD[iper];
		else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_INTRA))
			return BindiEtAl2010Constants.LOG10_2_LN
			* BindiEtAl2010Constants.INTRA_EVENT_STD[iper];
		else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_TOTAL))
			return BindiEtAl2010Constants.LOG10_2_LN
			* BindiEtAl2010Constants.TOTAL_STD[iper];
		else
			return Double.NaN;
	}

	/**
	 * This provides a URL where more info on this model can be obtained.
	 * 
	 */
	public URL getInfoURL() throws MalformedURLException {
		return null;
	}
	/**
	 * For testing
	 * 
	 */
	public static void main(String[] args) {
		
		BindiEtAl_2010jb_AttenRel ar = new BindiEtAl_2010jb_AttenRel(null);

		for (int i= 1; i < 2; i++){
			System.out.println("Rock ="  + BindiEtAl2010Constants.PERIOD[i]);
//			System.out.println("meanRjb =" + Math.exp(ar.getMean(i, 6.0, 3, 800)));
			System.out.println(5.00 + " " + 5.00 + " " + Math.exp(ar.getMean(i, 5.00, 5.00, 800)));
			System.out.println(6.00 + " " + 5.00 + " " + Math.exp(ar.getMean(i, 6.00, 5.00, 800)));
			System.out.println(6.50 + " " + 5.00 + " " + Math.exp(ar.getMean(i, 6.50, 5.00, 800)));
			System.out.println(7.00 + " " + 5.00 + " " + Math.exp(ar.getMean(i, 7.00, 5.00, 800)));
			System.out.println(7.50 + " " + 5.00 + " " + Math.exp(ar.getMean(i, 7.50, 5.00, 800)));
			System.out.println(5.00 + " " + 10.0 + " " + Math.exp(ar.getMean(i, 5.00, 10.0, 800)));
			System.out.println(6.00 + " " + 10.0 + " " + Math.exp(ar.getMean(i, 6.00, 10.0, 800)));
			System.out.println(6.50 + " " + 10.0 + " " + Math.exp(ar.getMean(i, 6.50, 10.0, 800)));
			System.out.println(7.00 + " " + 10.0 + " " + Math.exp(ar.getMean(i, 7.00, 10.0, 800)));
			System.out.println(7.50 + " " + 10.0 + " " + Math.exp(ar.getMean(i, 7.50, 10.0, 800)));
			System.out.println(5.00 + " " + 15.0 + " " + Math.exp(ar.getMean(i, 5.00, 15.0, 800)));
			System.out.println(6.00 + " " + 15.0 + " " + Math.exp(ar.getMean(i, 6.00, 15.0, 800)));
			System.out.println(6.50 + " " + 15.0 + " " + Math.exp(ar.getMean(i, 6.50, 15.0, 800)));
			System.out.println(7.00 + " " + 15.0 + " " + Math.exp(ar.getMean(i, 7.00, 15.0, 800)));
			System.out.println(7.50 + " " + 15.0 + " " + Math.exp(ar.getMean(i, 7.50, 15.0, 800)));
			System.out.println(5.00 + " " + 25.0 + " " + Math.exp(ar.getMean(i, 5.00, 25.0, 800)));
			System.out.println(6.00 + " " + 25.0 + " " + Math.exp(ar.getMean(i, 6.00, 25.0, 800)));
			System.out.println(6.50 + " " + 25.0 + " " + Math.exp(ar.getMean(i, 6.50, 25.0, 800)));
			System.out.println(7.00 + " " + 25.0 + " " + Math.exp(ar.getMean(i, 7.00, 25.0, 800)));
			System.out.println(7.50 + " " + 25.0 + " " + Math.exp(ar.getMean(i, 7.50, 25.0, 800)));
			System.out.println(5.00 + " " + 50.0 + " " + Math.exp(ar.getMean(i, 5.00, 50.0, 800)));
			System.out.println(6.00 + " " + 50.0 + " " + Math.exp(ar.getMean(i, 6.00, 50.0, 800)));
			System.out.println(6.50 + " " + 50.0 + " " + Math.exp(ar.getMean(i, 6.50, 50.0, 800)));
			System.out.println(7.00 + " " + 50.0 + " " + Math.exp(ar.getMean(i, 7.00, 50.0, 800)));
			System.out.println(7.50 + " " + 50.0 + " " + Math.exp(ar.getMean(i, 7.50, 50.0, 800)));
			System.out.println(5.00 + " " + 75.0 + " " + Math.exp(ar.getMean(i, 5.00, 75.0, 800)));
			System.out.println(6.00 + " " + 75.0 + " " + Math.exp(ar.getMean(i, 6.00, 75.0, 800)));
			System.out.println(6.50 + " " + 75.0 + " " + Math.exp(ar.getMean(i, 6.50, 75.0, 800)));
			System.out.println(7.00 + " " + 75.0 + " " + Math.exp(ar.getMean(i, 7.00, 75.0, 800)));
			System.out.println(7.50 + " " + 75.0 + " " + Math.exp(ar.getMean(i, 7.50, 75.0, 800)));
			System.out.println(5.00 + " " + 100. + " " + Math.exp(ar.getMean(i, 5.00, 100., 800)));
			System.out.println(6.00 + " " + 100. + " " + Math.exp(ar.getMean(i, 6.00, 100., 800)));
			System.out.println(6.50 + " " + 100. + " " + Math.exp(ar.getMean(i, 6.50, 100., 800)));
			System.out.println(7.00 + " " + 100. + " " + Math.exp(ar.getMean(i, 7.00, 100., 800))); 
			System.out.println(7.50 + " " + 100. + " " + Math.exp(ar.getMean(i, 7.50, 100., 800))); 
			

		}
	}	


}