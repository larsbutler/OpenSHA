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
import org.opensha.sha.imr.param.IntensityMeasureParams.PGV_Param;
import org.opensha.sha.imr.param.IntensityMeasureParams.PeriodParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.SA_Param;
import org.opensha.sha.imr.param.OtherParams.ComponentParam;
import org.opensha.sha.imr.param.OtherParams.SigmaTruncLevelParam;
import org.opensha.sha.imr.param.OtherParams.SigmaTruncTypeParam;
import org.opensha.sha.imr.param.OtherParams.StdDevTypeParam;
import org.opensha.sha.imr.param.PropagationEffectParams.DistanceRupParameter;
import org.opensha.sha.imr.param.SiteParams.Vs30_Param;


/**
 * <b>Title:</b> GhasemEtAl_2009_AttenRel<p>
 *
 * <b>Description:</b> This implements the  Ground Motion Prediction Equation 
 * proposed by Ghasemi et al ("An empirical spectral ground motion model for Iran" 
 * Journal of Seismology  Vol. 13, pp 499-515)<p>
 *
 * Supported Intensity-Measure Parameters:<p>
 * <UL>
 * <LI>saParam - Response Spectral Acceleration
 * </UL><p>
 * Other Independent Parameters:<p>
 * <UL>
 * <LI>magParam - moment Magnitude
 * <LI>distanceRupParam - Rupture distance
 * <LI>siteTypeParam - "Rock", "Soft Soil"
 * <LI>componentParam - GMRotI component of shaking
 * <LI>stdDevTypeParam - The type of standard deviation
 * </UL><p>
 * 
 *<p>
 *
 * Verification - This model MUST be validated against: 
 * 1) a verification file generated independently by L. Danciu,
 * implemented in the JUnit test class GhasemiEtAl2009_test; 
 * 
 * 
 * 
 *</p>
 *
 **
 * @author     L. Danciu
 * @created    July 20, 2011
 * @version    1.0
 */


public class GhasemiEtAl_2009_AttenRel extends AttenuationRelationship implements 
ScalarIntensityMeasureRelationshipAPI, NamedObjectAPI, 
ParameterChangeListener {

	/** Short name. */	
	public final static String SHORT_NAME = "GhasemiEtAl_2009";

	/** Full name. */
	public final static String NAME = "Ghasemi Et Al 2009";

	/** Version number. */
	private static final long serialVersionUID = 007L;


	/** Period index. */
	private int iper;

	/** Moment magnitude. */
	private double mag;

	/** Vs 30. */
	private double vs30;

	/** rake angle. */
	private double rake;

	/** Joybner and Boore distance. */
	private double rRup;

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
	public GhasemiEtAl_2009_AttenRel(final ParameterChangeWarningListener warningListener){

		// creates exceedProbParam
		super();

		this.warningListener = warningListener;

		initSupportedIntensityMeasureParams();

		// Create an Hash map that links the period with its index
		indexFromPerHashMap = new HashMap<Double, Integer>();
		for (int i = 0; i < GhasemiEtAl2009Constants.PERIOD.length; i++) { 
			indexFromPerHashMap.put(new Double(GhasemiEtAl2009Constants.PERIOD[i]), 
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
		for (int i = 1; i < GhasemiEtAl2009Constants.PERIOD.length; i++) {
			periodConstraint.addDouble(new Double(GhasemiEtAl2009Constants.PERIOD[i]));
		}
		periodConstraint.setNonEditable();
		// set period param (default is 1s, which is provided by AkB2010 GMPE)
		saPeriodParam = new PeriodParam(periodConstraint);

		// set damping parameter. Empty constructor set damping
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
	 * Initialize earthquake rupture parameter (moment magnitude, rake) 
	 * and add to eqkRuptureParams list. Makes the parameters non-editable.
	 */
	protected final void initEqkRuptureParams() {

		// moment magnitude (default 5.0)
		magParam = new MagParam(GhasemiEtAl2009Constants.MAG_WARN_MIN,
				GhasemiEtAl2009Constants.MAG_WARN_MAX);
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

		// vs30 parameters (constrains are not set, default value to 760 m/s)
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
				GhasemiEtAl2009Constants.DISTANCE_Rup_WARN_MIN);
		distanceRupParam.addParameterChangeWarningListener(warningListener);
		DoubleConstraint warn = new DoubleConstraint(
				GhasemiEtAl2009Constants.DISTANCE_Rup_WARN_MIN,
				GhasemiEtAl2009Constants.DISTANCE_Rup_WARN_MAX);
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
        componentParam = new ComponentParam(constraint, ComponentParam.COMPONENT_GMRotI50);
//        componentParam = new ComponentParam(constraint, ComponentParam.COMPONENT_AVE_HORZ);

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
		vs30Param.removeParameterChangeListener(this);
		distanceRupParam.removeParameterChangeListener(this);
		stdDevTypeParam.removeParameterChangeListener(this);
		saPeriodParam.removeParameterChangeListener(this);
		this.initParameterEventListeners();
	}

//	/**
//	 * This sets the eqkRupture related parameters (moment magnitude, tectonic
//	 * region type, focal depth) based on the eqkRupture passed in. The
//	 * internally held eqkRupture object is also set as that passed in. Warning
//	 * constrains on magnitude and focal depth are ignored.
//	 */
//	public final void setEqkRupture(final EqkRupture eqkRupture) {
//
//		magParam.setValueIgnoreWarning(new Double(eqkRupture.getMag()));
//		this.eqkRupture = eqkRupture;
//		setPropagationEffectParams();
//	}
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
	 * Compute mean. Applies correction for periods = 2.5 and 5 Hz 
	 * (for interface) as for Atkinson and Boore 2008 Erratum.
	 */
	public double getMean(){
		if (rRup > USER_MAX_DISTANCE) {
			return VERY_SMALL_MEAN;
		}
		else{
			setPeriodIndex();
			return getMean (iper, mag, rRup, vs30);
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
	 * Compute mean (natural logarithm of median ground motion).
	 */
	public double getMean(int iper, double mag, final double rRup, final double vs30){
		//
		double logY;
		
		double[] s = computeSiteTerm(iper, vs30);
		
		double term1 = GhasemiEtAl2009Constants.a1[iper] + GhasemiEtAl2009Constants.a2[iper] * mag; 
	                  
	    double term2 =  GhasemiEtAl2009Constants.a3[iper] * Math.log10(rRup + GhasemiEtAl2009Constants.a4[iper] * 
	    		       Math.pow(10, GhasemiEtAl2009Constants.a5[iper] * mag));
		
		logY = term1 + term2 + s[0];

		logY = logY * GhasemiEtAl2009Constants.LOG10_2_LN;
		
		return Math.log(Math.exp(logY)*GhasemiEtAl2009Constants.CMS2_TO_G_CONVERSION_FACTOR);
	}

	private double[] computeSiteTerm(final int iper, final double vs30) {
		double[] s = new double[1];
		if (vs30 >= GhasemiEtAl2009Constants.SOIL_TYPE_ROCK_UPPER_BOUND) {
			s[0] = GhasemiEtAl2009Constants.a6[iper];

		} else if (vs30 < GhasemiEtAl2009Constants.SOIL_TYPE_ROCK_UPPER_BOUND) {
			s[0] = GhasemiEtAl2009Constants.a7[iper];
		}
		return s;
	}


public double getStdDev(int iper, String stdDevType) {
	if(stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_NONE))
		return 0;
	else if(stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_TOTAL))
		return GhasemiEtAl2009Constants.LOG10_2_LN*GhasemiEtAl2009Constants.TOTAL_STD[iper];
	else 
		return Double.NaN;
}

/**
 * This provides a URL where more info on this model can be obtained
 * @throws MalformedURLException if returned URL is not a valid URL.
 * @returns the URL to the AttenuationRelationship document on the Web.
 */
public URL getInfoURL() throws MalformedURLException{
	return new URL("http://www.opensha.org/documentation/modelsImplemented/attenRel/Akkar_Bommer_2010.html");
}
/**
 * For testing
 * 
 */

public static void main(String[] args) {

	GhasemiEtAl_2009_AttenRel ar = new GhasemiEtAl_2009_AttenRel(null);
	 for (int i=0; i < 1; i++){
		 System.out.println("iper ="  + GhasemiEtAl2009Constants.PERIOD[i]);
//		 System.out.println(GhasemiEtAl2009Constants.PERIOD[i] + " mean = " + Math.exp(ar.getMean(i, 6.50, 10, 800)));
		 System.out.println(5.00 + " " + 8.81   + " " + Math.exp(ar.getMean(i, 5.00, 8.81  , 800)));
		 System.out.println(6.00 + " " + 7.49   + " " + Math.exp(ar.getMean(i, 6.00, 7.49  , 800)));
		 System.out.println(6.50 + " " + 6.57   + " " + Math.exp(ar.getMean(i, 6.50, 6.57  , 800)));
		 System.out.println(7.00 + " " + 5.57   + " " + Math.exp(ar.getMean(i, 7.00, 5.57  , 800)));
		 System.out.println(7.50 + " " + 5.00   + " " + Math.exp(ar.getMean(i, 7.50, 5.00  , 800)));
		 System.out.println(5.00 + " " + 12.35  + " " + Math.exp(ar.getMean(i, 5.00, 12.35 , 800)));
		 System.out.println(6.00 + " " + 11.45  + " " + Math.exp(ar.getMean(i, 6.00, 11.45 , 800)));
		 System.out.println(6.50 + " " + 10.87  + " " + Math.exp(ar.getMean(i, 6.50, 10.87 , 800)));
		 System.out.println(7.00 + " " + 10.30  + " " + Math.exp(ar.getMean(i, 7.00, 10.30 , 800))); 
		 System.out.println(7.50 + " " + 10.00  + " " + Math.exp(ar.getMean(i, 7.50, 10.00 , 800)));
		 System.out.println(5.00 + " " + 16.66  + " " + Math.exp(ar.getMean(i, 5.00, 16.66 , 800)));
		 System.out.println(6.00 + " " + 16.00  + " " + Math.exp(ar.getMean(i, 6.00, 16.00 , 800))); 
		 System.out.println(6.50 + " " + 15.59  + " " + Math.exp(ar.getMean(i, 6.50, 15.59 , 800)));
		 System.out.println(7.00 + " " + 15.20  + " " + Math.exp(ar.getMean(i, 7.00, 15.20 , 800)));
		 System.out.println(7.50 + " " + 15.00  + " " + Math.exp(ar.getMean(i, 7.50, 15.00 , 800)));
		 System.out.println(5.00 + " " + 26.03  + " " + Math.exp(ar.getMean(i, 5.00, 26.03 , 800)));
		 System.out.println(6.00 + " " + 25.61  + " " + Math.exp(ar.getMean(i, 6.00, 25.61 , 800)));
		 System.out.println(6.50 + " " + 25.36  + " " + Math.exp(ar.getMean(i, 6.50, 25.36 , 800)));
		 System.out.println(7.00 + " " + 25.12  + " " + Math.exp(ar.getMean(i, 7.00, 25.12 , 800)));
		 System.out.println(7.50 + " " + 25.00  + " " + Math.exp(ar.getMean(i, 7.50, 25.00 , 800)));
		 System.out.println(5.00 + " " + 50.52  + " " + Math.exp(ar.getMean(i, 5.00, 50.52 , 800)));
		 System.out.println(6.00 + " " + 50.31  + " " + Math.exp(ar.getMean(i, 6.00, 50.31 , 800)));
		 System.out.println(6.50 + " " + 50.18  + " " + Math.exp(ar.getMean(i, 6.50, 50.18 , 800)));
		 System.out.println(7.00 + " " + 50.06  + " " + Math.exp(ar.getMean(i, 7.00, 50.06 , 800)));
		 System.out.println(7.50 + " " + 50.00  + " " + Math.exp(ar.getMean(i, 7.50, 50.00 , 800)));
		 System.out.println(5.00 + " " + 75.35  + " " + Math.exp(ar.getMean(i, 5.00, 75.35 , 800)));
		 System.out.println(6.00 + " " + 75.21  + " " + Math.exp(ar.getMean(i, 6.00, 75.21 , 800)));
		 System.out.println(6.50 + " " + 75.12  + " " + Math.exp(ar.getMean(i, 6.50, 75.12 , 800)));
		 System.out.println(7.00 + " " + 75.04  + " " + Math.exp(ar.getMean(i, 7.00, 75.04 , 800)));
		 System.out.println(7.50 + " " + 75.00  + " " + Math.exp(ar.getMean(i, 7.50, 75.00 , 800)));
		 System.out.println(5.00 + " " + 100.26 + " " + Math.exp(ar.getMean(i, 5.00, 100.26, 800)));
		 System.out.println(6.00 + " " + 100.16 + " " + Math.exp(ar.getMean(i, 6.00, 100.16, 800)));
		 System.out.println(6.50 + " " + 100.09 + " " + Math.exp(ar.getMean(i, 6.50, 100.09, 800)));
		 System.out.println(7.00 + " " + 100.03 + " " + Math.exp(ar.getMean(i, 7.00, 100.03, 800))); 
		 System.out.println(7.50 + " " + 100.00 + " " + Math.exp(ar.getMean(i, 7.50, 100.00, 800))); 
	 }
}	

}
