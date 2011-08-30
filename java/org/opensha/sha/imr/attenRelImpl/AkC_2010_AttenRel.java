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
import org.opensha.commons.util.FaultUtils;
import org.opensha.sha.earthquake.EqkRupture;
import org.opensha.sha.imr.AttenuationRelationship;
import org.opensha.sha.imr.PropagationEffect;
import org.opensha.sha.imr.ScalarIntensityMeasureRelationshipAPI;
import org.opensha.sha.imr.attenRelImpl.BA_2008_AttenRel;
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
import org.opensha.sha.imr.param.OtherParams.TectonicRegionTypeParam;
import org.opensha.sha.imr.param.PropagationEffectParams.DistanceJBParameter;
import org.opensha.sha.imr.param.SiteParams.Vs30_Param;
import org.opensha.sha.util.TectonicRegionType;

/**
 * <b>Title:</b> Akkar_Bommer_2010_AttenRel
 * <p>
 * 
 * <b>Description:</b> This implements the Attenuation Relationship developed by
 * Akkar and Cagnac ("A local Ground-Motion Predictive Model for Turkey and its
 * comparison with Other Regional and Global Ground-Motion Models" Bulletin of
 * the Seismological Society of America, Vol. 100, No 6, pp 2978-2995)
 * <p>
 * 
 * Supported Intensity-Measure Parameters:
 * <p>
 * <UL>
 * <LI>pgaParam - Peak Ground Acceleration
 * <LI>pgvParam - Peak Ground Velocity
 * <LI>saParam - Response Spectral Acceleration
 * </UL>
 * <p>
 * Other Independent Parameters:
 * <p>
 * <UL>
 * <LI>magParam - moment Magnitude
 * <LI>distanceJBParam - Joyner-Boore distance
 * <LI>siteTypeParam - "Rock", "Stiff Soil", "Soft Soil" as a function of
 * continuous Vs30 value
 * <LI>fltTypeParam - Style of faulting "Normal" and "Reverse"
 * <LI>componentParam - Component of shaking
 * <LI>stdDevTypeParam - The type of standard deviation
 * </UL>
 * <p>
 * 
 *<p>
 * 
 * Verification - This model MUST be validated against: 1) a verification file
 * generated independently by L. Danciu, implemented in the JUnit test class
 * AC_2010_test;
 * 
 * 
 * 
 *</p>
 * 
 ** 
 * @author L. Danciu
 * @created July 12, 2010
 * @version 1.0
 */

public class AkC_2010_AttenRel extends AttenuationRelationship implements
ScalarIntensityMeasureRelationshipAPI, NamedObjectAPI,
ParameterChangeListener {

	/** Short name. */
	public final static String SHORT_NAME = "AC_2010";

	/** Full name. */
	public final static String NAME = "Akkar & Cagnan 2010";

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
	private double rJB;

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
	public AkC_2010_AttenRel(
			final ParameterChangeWarningListener warningListener) {

		// creates exceedProbParam
		super();

		this.warningListener = warningListener;

		initSupportedIntensityMeasureParams();

		// Create an Hash map that links the period with its index
		indexFromPerHashMap = new HashMap<Double, Integer>();
		for (int i = 0; i < AkC2010Constants.PERIOD.length; i++) {
			indexFromPerHashMap.put(new Double(AkC2010Constants.PERIOD[i]),
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
		for (int i = 0; i < AkC2010Constants.PERIOD.length; i++) {
			periodConstraint.addDouble(new Double(AkC2010Constants.PERIOD[i]));
		}
		periodConstraint.setNonEditable();
		// set period param (default is 1s, which is provided by AkB2010 GMPE)
		saPeriodParam = new PeriodParam(periodConstraint);

		// set damping parameter. Empty constructor set damping
		// factor to 5 % (which is the one provided by AB2003 GMPE)
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

		// moment magnitude (default 5.0)
		magParam = new MagParam(AkC2010Constants.MAG_WARN_MIN,
				AkC2010Constants.MAG_WARN_MAX);
		// Focal mechanism
		rakeParam = new RakeParam();
		eqkRuptureParams.clear();
		eqkRuptureParams.addParameter(magParam);
		eqkRuptureParams.addParameter(rakeParam);

	}

	/**
	 * Initialize the style of faulting parameter from the rake angle.
	 * 
	 * @param rake
	 *            ave. rake of rupture (degrees)
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

		distanceJBParam = new DistanceJBParameter(
				AkC2010Constants.DISTANCE_JB_WARN_MIN);
		distanceJBParam.addParameterChangeWarningListener(warningListener);
		DoubleConstraint warn = new DoubleConstraint(
				AkC2010Constants.DISTANCE_JB_WARN_MIN,
				AkC2010Constants.DISTANCE_JB_WARN_MAX);
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
	 * Adds the parameter change listeners. This allows to listen to when-ever
	 * the parameter is changed.
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
		} else if (pName.equals(DistanceJBParameter.NAME)) {
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
	public void resetParameterEventListeners() {
		magParam.removeParameterChangeListener(this);
		rakeParam.removeParameterChangeListener(this);
		vs30Param.removeParameterChangeListener(this);
		distanceJBParam.removeParameterChangeListener(this);
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
	 * This calculates the JB Distance propagation effect parameter based on the
	 * current site and eqkRupture.
	 * <P>
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
	 * Compute mean. Applies correction for periods = 2.5 and 5 Hz (for
	 * interface) as for Atkinson and Boore 2008 Erratum.
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
		vs30Param.setValueAsDefault();
		distanceJBParam.setValueAsDefault();
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
	public double getMean(final int iper, double mag, final double rJB,
			final double vs30, double rake) {
		//
		double lnY;
		double Fs = computeSiteTerm(iper, mag, rJB, vs30, rake);

		// compute logY for AC2010
		int FN = 0, FR = 0;
		if (rake > 30 && rake < 150) {
			FN = 0;
			FR = 1;
		} else if (rake > -150 && rake < -30) {
			FN = 1;
			FR = 0;
		} else {
			FN = 0;
			FR = 0;
		}


		if (mag <= AkC2010Constants.MAG_REF) {
			double termH = Math.sqrt(Math.pow(rJB, 2)+ Math.pow(AkC2010Constants.a7[iper], 2));
			double diffMag = (mag - AkC2010Constants.MAG_REF);
			double termR = (AkC2010Constants.a5[iper] + AkC2010Constants.a6[iper] * (diffMag)) * Math.log(termH);

			lnY = AkC2010Constants.a1[iper] + AkC2010Constants.a2[iper] * (diffMag) + AkC2010Constants.a4[iper]
			      * Math.pow((8.5 - mag), 2) + termR + AkC2010Constants.a8[iper] * FN + AkC2010Constants.a9[iper] * FR;

		} else {

			double termH = Math.sqrt(Math.pow(rJB, 2)+ Math.pow(AkC2010Constants.a7[iper], 2));
			double diffMag = (mag - AkC2010Constants.MAG_REF);
			double termR = (AkC2010Constants.a5[iper] + AkC2010Constants.a6[iper] * (diffMag)) * Math.log(termH);


			lnY = AkC2010Constants.a1[iper] + AkC2010Constants.a3[iper] * (diffMag) + AkC2010Constants.a4[iper]
			      * Math.pow((8.5 - mag), 2) + termR + AkC2010Constants.a8[iper] * FN + AkC2010Constants.a9[iper] * FR;
		}

		lnY = lnY + Fs;

		// convert PGA and SA from cm/s2 to g
		if (iper != 0) {
			lnY = Math.log(Math.exp(lnY)
					* AkC2010Constants.CMS_TO_G_CONVERSION_FACTOR);
		}

		return (lnY);
	}

	// compute png4nl
	private double computePGA4NL(double mag, double rJB, double rake) {
		double pga4nl = Double.NaN;

		int UU =0, SS=0, NS=0, RS=0;
		if (rake<=30 && rake>=-30){
			SS=1;
			NS=0;
			RS=0;
			UU=0;
		} else if (rake<=-150 || rake>=150) {
			SS=1;
			NS=0;
			RS=0;
			UU=0;
		} else if (rake > 30 && rake < 150) {
			SS=0;
			NS=0;
			RS=1;
			UU=0;
		} else if (rake > -150 && rake < -30) {
			SS=0;
			NS=1;
			RS=0;
			UU=0;
		} else {  
		    SS=0;
		    NS=0;
		    RS=0;
		    UU=1;
		}

		if (mag <= AkC2010Constants.pga4nl_mag_hinge) {			
			double termR = Math.sqrt(Math.pow(rJB, 2) + Math.pow(AkC2010Constants.h, 2));
			double diffMag = mag-AkC2010Constants.pga4nl_mag_hinge;

			pga4nl = Math.exp(AkC2010Constants.e1 * UU + AkC2010Constants.e2 * SS + 
					          AkC2010Constants.e3 * NS + AkC2010Constants.e4 * RS + 
					          AkC2010Constants.e5 * (diffMag) + 
					          AkC2010Constants.e6 * Math.pow(diffMag, 2) + 
					          (AkC2010Constants.c1 + AkC2010Constants.c2 * 
					          (mag - AkC2010Constants.pga4nl_mag_ref)) * 
					          Math.log(termR/AkC2010Constants.r_ref) + 
					          AkC2010Constants.c3 * (termR-AkC2010Constants.r_ref));

		} else {

			double termR = Math.sqrt(Math.pow(rJB, 2) + Math.pow(AkC2010Constants.h, 2));
			double diffMag = mag-AkC2010Constants.pga4nl_mag_hinge;

			pga4nl = Math.exp(AkC2010Constants.e1 * UU + AkC2010Constants.e2 * SS + AkC2010Constants.e3 * NS + 
					AkC2010Constants.e4 * RS + AkC2010Constants.e7 * (diffMag) + 
					(AkC2010Constants.c1 + AkC2010Constants.c2 * (mag - AkC2010Constants.pga4nl_mag_ref)) * 
					Math.log(termR/AkC2010Constants.r_ref) + AkC2010Constants.c3 * (termR-AkC2010Constants.r_ref));

		}
		return (pga4nl);
	}

	// compute site response function Fs
	private double computeSiteTerm(int iper, double mag, double rJB,
			double vs30, double rake) {
		double Fs = Double.NaN;
		double pga4nl = computePGA4NL(mag, rJB, rake);


		// compute Flin
		double Flin = AkC2010Constants.b1in[iper] * Math.log(vs30 / AkC2010Constants.VS30_REF);

		// compute bnl
		double bnl = 0;
		if (vs30 <= AkC2010Constants.v1)
			bnl = AkC2010Constants.b1[iper];
		else if (vs30 <= AkC2010Constants.v2 && vs30 > AkC2010Constants.v1)
			bnl = (AkC2010Constants.b1[iper] - AkC2010Constants.b2[iper])
			* Math.log(vs30 / AkC2010Constants.v2)
			/ Math.log(AkC2010Constants.v1 / AkC2010Constants.v2)
			+ AkC2010Constants.b2[iper];
		else if (vs30 < AkC2010Constants.VS30_REF && vs30 > AkC2010Constants.v2)
			bnl = AkC2010Constants.b2[iper] * Math.log(vs30 / AkC2010Constants.VS30_REF) 
			      / Math.log(AkC2010Constants.v2 / AkC2010Constants.VS30_REF);
		else
			bnl = 0.0;

		// compute Fnl
		double Fnl;
		if (pga4nl <= AkC2010Constants.pga4nl_a1) {
			Fnl = bnl * Math.log(AkC2010Constants.pga4nl_low / 0.1);
		} else if (pga4nl <= AkC2010Constants.pga4nl_a2
				& pga4nl > AkC2010Constants.pga4nl_a1) {
			double c, d, dX, dY;
			dX = Math.log(AkC2010Constants.pga4nl_a2
					/ AkC2010Constants.pga4nl_a1);
			dY = bnl
			* Math.log(AkC2010Constants.pga4nl_a2
					/ AkC2010Constants.pga4nl_low);
			c = (3 * dY - bnl * dX) / (dX * dX);
			d = -(2 * dY - bnl * dX) / (dX * dX * dX);

			Fnl = bnl * Math.log(AkC2010Constants.pga4nl_low / 0.1)
			+ c * Math.pow(Math.log(pga4nl / AkC2010Constants.pga4nl_a1), 2)
			+ d * Math.pow(Math.log(pga4nl / AkC2010Constants.pga4nl_a1), 3);

		} 

		Fnl = bnl * Math.log(pga4nl / 0.1);


		Fs = Flin + Fnl;
		return Fs;
		
	}

	public double getStdDev(int iper, String stdDevType) {
		if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_NONE))
			return 0;
		else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_INTER))
			return AkC2010Constants.INTER_EVENT_STD[iper];
		else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_INTRA))
			return AkC2010Constants.INTRA_EVENT_STD[iper];
		else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_TOTAL))
			return AkC2010Constants.TOTAL_STD[iper];
		else
			return Double.NaN;
	}

	/**
	 * This provides a URL where more info on this model can be obtained
	 * 
	 * @throws MalformedURLException
	 *             if returned URL is not a valid URL.
	 * @returns the URL to the AttenuationRelationship document on the Web.
	 */
	public URL getInfoURL() throws MalformedURLException {
		return new URL(
		"http://www.opensha.org/documentation/modelsImplemented/attenRel/Akkar_Cagnac_2010.html");
	}

	/**
	 * For testing
	 * 
	 */
//
//	public static void main(String[] args) {
//
//		AkC_2010_AttenRel ar = new AkC_2010_AttenRel(null);
//		for (int i = 0; i < 10; i++) {
//			//			System.out.println("iper =" + AkC2010Constants.PERIOD[i]);
//			System.out.println("iper = " + AkC2010Constants.PERIOD[i] + "       mean_0.1km   =  " 
//					+ Math.exp(ar.getMean(i, 7.00, 10, 760, 20)));
//
//		}
//	}

}
