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
import org.opensha.commons.param.event.ParameterChangeEvent;
import org.opensha.commons.param.event.ParameterChangeListener;
import org.opensha.commons.param.event.ParameterChangeWarningListener;
import org.opensha.sha.earthquake.EqkRupture;
import org.opensha.sha.imr.AttenuationRelationship;
import org.opensha.sha.imr.PropagationEffect;
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
import org.opensha.sha.imr.param.PropagationEffectParams.DistanceJBParameter;
import org.opensha.sha.imr.param.SiteParams.Vs30_Param;


/**
 * <b>Title:</b> HakanGulkan_1997_AttenRel<p>
 *
 * <b>Description:</b> This implements the Attenuation Relationship
 * developed by Kalkan & Gulkan (2004, Earthquake Spectra, vol 20, no 4, pp 1111-1138) <p>
 *
 * Supported Intensity-Measure Parameters:<p>
 * <UL>
 * <LI>pgaParam - Peak Ground Acceleration
 * <LI>saParam - Response Spectral Acceleration
 * </UL><p>
 * Other Independent Parameters:<p>
 * <UL>
 * <LI>magParam - moment Magnitude
 * <LI>distanceJBParam - closest distance to surface projection of fault
 * <LI>vs30Param - Average 30-meter shear-wave velocity at the site
 * <LI>componentParam - Component of shaking (only one) the largest of the two horizontal component
 * <LI>stdDevTypeParam - The type of standard deviation
 * <LI>The relationship has not a specified style-of-faulting parameter 
 * </UL><p>
 *
 * @author     l.danciu
 * @created    jan, 2011
 * @version    1.0
 */


public class KG_2004_AttenRel extends AttenuationRelationship implements
ScalarIntensityMeasureRelationshipAPI,
NamedObjectAPI, ParameterChangeListener {


	/** Short name. */
	public final static String SHORT_NAME = "AkB2010";

	/** Full name. */
	public final static String NAME = "Akkar & Bommer 2010";

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
	 *  This initializes several ParameterList objects.
	 */
	public KG_2004_AttenRel(ParameterChangeWarningListener warningListener){
		// creates exceedProbParam
		super();

		this.warningListener = warningListener;

		initSupportedIntensityMeasureParams();

		// Create an Hash map that links the period with its index
		indexFromPerHashMap = new HashMap<Double, Integer>();
		for (int i = 0; i < KG_2004Constants.PERIOD.length; i++) {
			indexFromPerHashMap.put(new Double(KG_2004Constants.PERIOD[i]),
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
		for (int i = 1; i < AkB2010Constants.PERIOD.length; i++) {
			periodConstraint.addDouble(new Double(AkB2010Constants.PERIOD[i]));
		}
		periodConstraint.setNonEditable();
		// set period param (default is 1s, which is provided by AkB2010 GMPE)
		saPeriodParam = new PeriodParam(periodConstraint);

		// set damping parameter. Empty constructor set damping
		// factor to 5 % (which is the one provided by AkB2010 GMPE)
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
		magParam = new MagParam(KG_2004Constants.MAG_WARN_MIN,
				KG_2004Constants.MAG_WARN_MAX);
		// rake angle (default 0.0 -> strike-slip)
		rakeParam = new RakeParam();

		eqkRuptureParams.clear();
		eqkRuptureParams.addParameter(magParam);
		eqkRuptureParams.addParameter(rakeParam);
	}
	/**
	 * Initialize Propagation Effect parameters (Joyner-Boore distance) and adds
	 * them to the propagationEffectParams list. Makes the parameters
	 * non-editable.
	 */
	protected final void initPropagationEffectParams() {

		distanceJBParam = new DistanceJBParameter(
				KG_2004Constants.DISTANCE_JB_WARN_MIN);
		distanceJBParam.addParameterChangeWarningListener(warningListener);
		DoubleConstraint warn = new DoubleConstraint(
				KG_2004Constants.DISTANCE_JB_WARN_MIN,
				KG_2004Constants.DISTANCE_JB_WARN_MAX);
		warn.setNonEditable();
		distanceJBParam.setWarningConstraint(warn);
		distanceJBParam.setNonEditable();

		propagationEffectParams.addParameter(distanceJBParam);
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
	 *  Creates other Parameters that the mean or stdDev depends upon,
	 *  such as the Component or StdDevType parameters.
	 */
	protected void initOtherParams() {

		// init other params defined in parent class
		super.initOtherParams();

		StringConstraint componentConstraint = new StringConstraint();
		componentConstraint.addString(ComponentParam.COMPONENT_AVE_HORZ);
		componentConstraint.addString(ComponentParam.COMPONENT_GREATER_OF_TWO_HORZ);
		componentConstraint.setNonEditable();
		componentParam = new ComponentParam(componentConstraint,ComponentParam.COMPONENT_AVE_HORZ);


		// the stdDevType Parameter
		StringConstraint stdDevTypeConstraint = new StringConstraint();
		stdDevTypeConstraint.addString(StdDevTypeParam.STD_DEV_TYPE_TOTAL);
		stdDevTypeConstraint.addString(StdDevTypeParam.STD_DEV_TYPE_NONE);
		stdDevTypeConstraint.setNonEditable();
		stdDevTypeParam = new StdDevTypeParam(stdDevTypeConstraint);

		// add these to the list
		otherParams.addParameter(componentParam);
		otherParams.addParameter(stdDevTypeParam);

	}
	/**
	 * This creates the lists of independent parameters that the various dependent
	 * parameters (mean, standard deviation, exceedance probability, and IML at
	 * exceedance probability) depend upon. NOTE: these lists do not include anything
	 * about the intensity-measure parameters or any of thier internal
	 * independentParamaters.
	 */
	protected void initIndependentParamLists() {

		// params that the mean depends upon
		meanIndependentParams.clear();
		meanIndependentParams.addParameter(distanceJBParam);
		meanIndependentParams.addParameter(vs30Param);
		meanIndependentParams.addParameter(magParam);
		meanIndependentParams.addParameter(componentParam);


		// params that the stdDev depends upon
		stdDevIndependentParams.clear();
		stdDevIndependentParams.addParameter(saPeriodParam);
		stdDevIndependentParams.addParameter(stdDevTypeParam);
		stdDevIndependentParams.addParameter(componentParam);

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
	 * Adds the parameter change listeners. This allows to listen to when-ever
	 * the parameter is changed.
	 */
	protected void initParameterEventListeners() {

		magParam.addParameterChangeListener(this);
		rakeParam.addParameterChangeListener(this);
		distanceJBParam.addParameterChangeListener(this);
		vs30Param.addParameterChangeListener(this);
		componentParam.addParameterChangeListener(this);
		saPeriodParam.addParameterChangeListener(this);
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
	public double getMean() {

		if (rJB > USER_MAX_DISTANCE) {
			return VERY_SMALL_MEAN;
		} else {
			setPeriodIndex();
			return getMean(iper, mag, rJB, vs30, rake);
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

	public double getMean(int iper, double mag,  double rjb, double vs30, double rake) {
		double mean;
		double lnY = 0.00;
		// focal mechanism adjustment
		double AF = 0.00;
		double temp1 =  KG_2004Constants.b2[iper] * (mag-6);
		double temp2 =  KG_2004Constants.b3[iper] * Math.pow((mag-6), 2);
		double r = Math.sqrt(rjb * rjb + KG_2004Constants.h[iper] * KG_2004Constants.h[iper]);
		double temp3 =  KG_2004Constants.b5[iper] * Math.log(r);
		double temp4 = KG_2004Constants.bv[iper] * Math.log(vs30/KG_2004Constants.Va[iper]);
		lnY = KG_2004Constants.b1[iper] + temp1 + temp2 + temp3 + temp4 ;
		
		return mean = lnY;
	}

	public double getStdDev(int iper, String stdDevType) {

		if(stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_TOTAL))
			return KG_2004Constants.sig[iper];
		else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_NONE))
			return 0;
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

		KG_2004_AttenRel ar = new KG_2004_AttenRel(null);
		ar.setParamDefaults();
		ar.setIntensityMeasure(SA_Param.NAME);

				 for (int i=0; i < 44; i++){
					 System.out.println("iper ="  + KG_2004Constants.PERIOD[i]);
					 System.out.println("iper" + i + " mean = " + Math.exp(ar.getMean(i, 7.00, 10, 800, 45)));
				 }

	}	





}