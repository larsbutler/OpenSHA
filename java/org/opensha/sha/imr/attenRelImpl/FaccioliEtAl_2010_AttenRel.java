/*******************************************************************************
 * Copyright 2009 OpenSHA.org in partnership with
 * the Southern California Earthquake Center (SCEC, http://www.scec.org)
 * at the University of Southern California and the UnitedStates Geological
 * Survey (USGS; http://www.usgs.gov)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

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
import org.opensha.sha.imr.param.SiteParams.Vs30_Param;

/**
 * <b>Title:</b> CF_2008_AttenRel<p>
 *
 * <b>Description:</b> 
 * This implements the GMPE published by E., Faccioli, A., Bianchini, and M., Villani (2010),
 * "New ground motion prediction equations for T> 1s and their influence on seismic hazard assesment", 
 * Procceedings of the University of Tokyo Symposium on Long_Period Ground Motion 
 * and Urban Disastser Mitigation, March 17-18, 2010)
 * This implements only horizontal components and the equation (2) page 462.
 * 
 * Supported Intensity-Measure Parameters:<p>
 * <UL>
 * <LI>pgaParam - Peak Ground Acceleration
 * <LI>pgvParam - Peak Ground Velocity
 * <LI>sdParam - Default values are for Displacement Response Spectra
 * <LI>saParam = ((2*pi/T)^2)*sdParam Convert to Acceleration Response Spectra
 * Other Independent Parameters:<p>
 * <UL>
 * <LI>magParam - moment Magnitude
 * <LI>distanceRup - Rupture distance;
 * <LI>vs30Param [>= 800m/sec]  default 30-meter shear wave velocity;
 *     The model assumes the following classification (based on EC 8 scheme):
 *     vs30 >= 800 -> Class A (rock-like); 360 <= vs30 <800 -> Class B (Stiff Soil); 
 *     180 <= vs30 < 360 -> Class C (Soft Soil); vs30 < 180 -> Class D (Very Soft Soil);
 * <LI>soilTypeParam - Local soil conditions; default values are for rock-type 
 * <LI>fltTypeParam - Style of faulting
 * <LI>componentParam - [geometric mean] default component of shaking 
 * <LI>stdDevTypeParam - The type of standard deviation
 * </UL></p>
 * 
 *<p>
 *
 * Verification - This model has been tested against: 
 * 1) 
 * 2) 
 * 
 *</p>
 *
 *
 * @author     L. Danciu  
 * @created    July, 2011
 * @version    1.0
 */


public class FaccioliEtAl_2010_AttenRel extends AttenuationRelationship implements
ScalarIntensityMeasureRelationshipAPI,
NamedObjectAPI, ParameterChangeListener {

	/** Short name. */
	public static final String SHORT_NAME = "Faccioli2010";

	/** Full name. */
	public static final String NAME = "FaccioliEtAl (2010)";

	/** Version number. */
	private static final long serialVersionUID = 1234567890987654353L;

	/** Moment magnitude. */
	private double mag;

	/** Tectonic region type. */
	private String tecRegType;

	/** Focal depth. */
	private double focalDepth;

	/** rake angle. */
	private double rake;

	/** Vs 30. */
	private double vs30;

	/** hypocentral distance */
	private double rRup;

	/** Standard deviation type. */
	private String stdDevType;

	/** Map period-value/period-index. */
	private HashMap<Double, Integer> indexFromPerHashMap;

	/** Period index. */
	private int iper;

	/** For issuing warnings. */
	private transient ParameterChangeWarningListener warningListener = null;
	/**
	 * Construct attenuation relationship. Initialize parameters and parameter
	 * lists.
	 */
	public FaccioliEtAl_2010_AttenRel(ParameterChangeWarningListener warningListener) {

		// creates exceedProbParam
		super();

		this.warningListener = warningListener;

		initSupportedIntensityMeasureParams();

		indexFromPerHashMap = new HashMap<Double, Integer>();
		for (int i = 0; i < FaccioliEtAl_2010Constants.PERIOD.length; i++) {
			indexFromPerHashMap.put(new Double(FaccioliEtAl_2010Constants.PERIOD[i]),
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
		for (int i = 0; i < FaccioliEtAl_2010Constants.PERIOD.length; i++) {
			periodConstraint.addDouble(new Double(FaccioliEtAl_2010Constants.PERIOD[i]));
		}
		periodConstraint.setNonEditable();
		// set period param (default is 1s, which is provided by Faccioli2010 GMPE)
		saPeriodParam = new PeriodParam(periodConstraint);

		// set damping parameter. Empty constructor set damping
		// factor to 5 % (which is the one provided by Faccioli2010 GMPE)
		saDampingParam = new DampingParam();

		// initialize spectral acceleration parameter (units: g)
		saParam = new SA_Param(saPeriodParam, saDampingParam);
		saParam.setNonEditable();

		// initialize peak ground acceleration parameter (units: g)
		pgaParam = new PGA_Param();
		pgaParam.setNonEditable();

		// initialize peak ground velocity parameter (units: cm/sec)
//		pgvParam = new PGV_Param();
//		pgvParam.setNonEditable();

		// add the warning listeners
		saParam.addParameterChangeWarningListener(warningListener);
		pgaParam.addParameterChangeWarningListener(warningListener);
//		pgvParam.addParameterChangeWarningListener(warningListener);

		// put parameters in the supportedIMParams list
		supportedIMParams.clear();
		supportedIMParams.addParameter(saParam);
		supportedIMParams.addParameter(pgaParam);
//		supportedIMParams.addParameter(pgvParam);

	}
	/**
	 * Initialize earthquake rupture parameter (moment magnitude, rake) 
	 * and add to eqkRuptureParams list. Makes the parameters non-editable.
	 */
	protected final void initEqkRuptureParams() {

		// moment magnitude (default 5.0)
		magParam = new MagParam(FaccioliEtAl_2010Constants.MAG_WARN_MIN,
				FaccioliEtAl_2010Constants.MAG_WARN_MAX);
		// Focal mechanism
		rakeParam = new RakeParam(); 
		eqkRuptureParams.clear();
		eqkRuptureParams.addParameter(magParam);
		eqkRuptureParams.addParameter(rakeParam);

	}
	/**
	 * Initialize the site type geology from the Vs30 value.
	 * @param vs30   default 30-meter shear wave velocity                  
	 */
	protected final void initSiteParams() {

		vs30Param = new Vs30_Param();

		siteParams.clear();
		siteParams.addParameter(vs30Param);
	}
	/**
	 * Initialize Propagation Effect parameters (closest distance to rupture)
	 * and adds them to the propagationEffectParams list. Makes the parameters
	 * non-editable.
	 */
	protected final void initPropagationEffectParams() {

		distanceRupParam = new DistanceRupParameter(
				FaccioliEtAl_2010Constants.DISTANCE_RUP_WARN_MIN);
		distanceRupParam.addParameterChangeWarningListener(warningListener);
		DoubleConstraint warn = new DoubleConstraint(
				FaccioliEtAl_2010Constants.DISTANCE_RUP_WARN_MIN,
				FaccioliEtAl_2010Constants.DISTANCE_RUP_WARN_MAX);
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
		meanIndependentParams.addParameter(vs30Param);
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
	 * Adds the parameter change listeners. This allows to listen to when-ever the
	 * parameter is changed.
	 */
	protected void initParameterEventListeners() {

		magParam.addParameterChangeListener(this);
		rakeParam.addParameterChangeListener(this);
		vs30Param.addParameterChangeListener(this);
		distanceRupParam.addParameterChangeListener(this);
		stdDevTypeParam.addParameterChangeListener(this);
		saPeriodParam.addParameterChangeListener(this);
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
			rRup = ( (Double) val).doubleValue();
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
		distanceRupParam.removeParameterChangeListener(this);
		stdDevTypeParam.removeParameterChangeListener(this);
		saPeriodParam.removeParameterChangeListener(this);
		this.initParameterEventListeners();
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
	 * This calculates the Hypocentral Distance  propagation effect parameter based
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
	 * Compute mean. 
	 */
	public double getMean(){
		if (rRup > USER_MAX_DISTANCE) {
			return VERY_SMALL_MEAN;
		}
		else{
			setPeriodIndex();
			return getMean (iper, mag, rRup, vs30, rake);
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
		vs30Param.setValueAsDefault();
		distanceRupParam.setValueAsDefault();
		saPeriodParam.setValueAsDefault();
		saDampingParam.setValueAsDefault();
		saParam.setValueAsDefault();
		pgaParam.setValueAsDefault();
//		pgvParam.setValueAsDefault();
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
	 * This computes the mean log10(Y) 
	 * @param iper
	 * @param rRup
	 * @param mag
	 */
	public double getMean(int iper, double mag, double rRup, final double vs30, final double rake){

		double logY;

		// This is to avoid rRup == 0 distances
		if (rRup < 1e-3) {
			rRup = 1;
		}


		double soilTerms = computeSiteTerm(iper, vs30);
		double faultTerm = computeStyleOfFaultingTerm(iper, rake);
		double[] a = setConstants(iper);
		double drs2psa = convert2psa(iper);

		logY = a[0] + a[1] * mag + a[2] * Math.log10(rRup + a[3] * Math.pow(10, a[4] * mag)) 
		     + soilTerms + faultTerm;
		
		logY = logY*FaccioliEtAl_2010Constants.LOG10_2_LN;

		if (iper == 0) {

			logY = (Math.exp(logY) * FaccioliEtAl_2010Constants.MS2_TO_G_CONVERSION_FACTOR);

		} else {
			
			logY = Math.exp(logY) * drs2psa;

		}
		
		return Math.log(logY);
	}

	// set fault mechanism 
	private double computeStyleOfFaultingTerm(final int iper, final double rake) {
		double faultTerm = Double.NaN;
		if (rake > FaccioliEtAl_2010Constants.FLT_TYPE_NORMAL_RAKE_LOWER
				&& rake <= FaccioliEtAl_2010Constants.FLT_TYPE_NORMAL_RAKE_UPPER){
			faultTerm = FaccioliEtAl_2010Constants.aN[iper];
		} else if (rake > FaccioliEtAl_2010Constants.FLT_TYPE_REVERSE_RAKE_LOWER
				&& rake <= FaccioliEtAl_2010Constants.FLT_TYPE_REVERSE_RAKE_UPPER) {
			faultTerm = FaccioliEtAl_2010Constants.aR[iper];
		} else {
			faultTerm = FaccioliEtAl_2010Constants.aS[iper];
		}
		return faultTerm;
	}
	
	private double computeSiteTerm(final int iper, final double vs30) {
		double soilTerms = Double.NaN;

		if (vs30 >= FaccioliEtAl_2010Constants.SITE_TYPE_STIFF_SOIL_UPPER_BOUND
				&& vs30 < FaccioliEtAl_2010Constants.SOIL_TYPE_ROCK_UPPER_BOUND) {
			soilTerms = FaccioliEtAl_2010Constants.aB[iper];
		} else if (vs30 >= FaccioliEtAl_2010Constants.SITE_TYPE_SOFT_UPPER_BOUND
				&& vs30 < FaccioliEtAl_2010Constants.SITE_TYPE_STIFF_SOIL_UPPER_BOUND) {
			soilTerms = FaccioliEtAl_2010Constants.aC[iper];
		} else if (vs30 < FaccioliEtAl_2010Constants.SITE_TYPE_SOFT_UPPER_BOUND) {
			soilTerms = FaccioliEtAl_2010Constants.aC[iper];
		} else {
			soilTerms = 0;
		}
		return soilTerms;
	}
	
	private double[] setConstants(int iper) {
		double[] a = new double[5];
		a[0]  = FaccioliEtAl_2010Constants.a1[iper];
		a[1]  = FaccioliEtAl_2010Constants.a2[iper];
		a[2]  = FaccioliEtAl_2010Constants.a3[iper];
		a[3]  = FaccioliEtAl_2010Constants.a4[iper];
		a[4]  = FaccioliEtAl_2010Constants.a5[iper];
		return a;
	}

	//tmp variables to convert to DRS to mean PSA(g);
	private double convert2psa (final int iper){
		double drs2psa = Double.NaN;
		return drs2psa = Math.pow((2*Math.PI)/FaccioliEtAl_2010Constants.PERIOD[iper], 2) 
        * FaccioliEtAl_2010Constants.CMS2_TO_G_CONVERSION_FACTOR;
	}
	
	public double getStdDev(int iper, String stdDevType) {
		if(stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_NONE))
			return 0;
		else if(stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_TOTAL))
			return FaccioliEtAl_2010Constants.LOG10_2_LN * 
			       FaccioliEtAl_2010Constants.TOTAL_STD[iper];
		else 
			return Double.NaN;
	}

	/**
	 * This provides a URL where more info on this model can be obtained.
	 * It currently returns null because no URL has been set up.
	 */
	public final URL getInfoURL() throws MalformedURLException {
		return null;
	}
	/**
	 * For testing
	 * 
	 */
	public static void main(String[] args) {
		
		FaccioliEtAl_2010_AttenRel ar = new FaccioliEtAl_2010_AttenRel(null);

		for (int i= 0; i < 23; i++){
			System.out.println("reverse - rock "  + FaccioliEtAl_2010Constants.PERIOD[i]);
			System.out.println("mean = " + Math.exp(ar.getMean(i, 7.00, 10, 800, -60.0)));
		}
	}	

}
