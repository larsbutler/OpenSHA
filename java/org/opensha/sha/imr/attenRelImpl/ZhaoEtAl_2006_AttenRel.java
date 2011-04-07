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
import org.opensha.sha.faultSurface.EvenlyGriddedSurface;
import org.opensha.sha.faultSurface.EvenlyGriddedSurfaceAPI;
import org.opensha.sha.imr.AttenuationRelationship;
import org.opensha.sha.imr.PropagationEffect;
import org.opensha.sha.imr.ScalarIntensityMeasureRelationshipAPI;
import org.opensha.sha.imr.param.EqkRuptureParams.FaultTypeParam;
import org.opensha.sha.imr.param.EqkRuptureParams.FocalDepthParam;
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
import org.opensha.sha.imr.param.OtherParams.TectonicRegionTypeParam;
import org.opensha.sha.imr.param.PropagationEffectParams.DistanceRupParameter;
import org.opensha.sha.imr.param.SiteParams.Vs30_Param;
import org.opensha.sha.util.TectonicRegionType;

public class ZhaoEtAl_2006_AttenRel extends AttenuationRelationship implements
		ScalarIntensityMeasureRelationshipAPI, NamedObjectAPI,
		ParameterChangeListener {

	/** Short name. */
	public static final String SHORT_NAME = "ZhaoEtAl2006";

	/** Full name. */
	public static final String NAME = "Zhao et. al. 2006";

	/** Version number. */
	private static final long serialVersionUID = 1234567890987654353L;

	/** Moment magnitude (Mw) */
	private double mag;

	/** Tectonic region type */
	private String tecRegType;

	/** Focal depth. */
	private double focalDepth;

	/** Vs30. */
	private double vs30;

	/** Closest distance to rupture. */
	private double rRup;

	/** Rake angle. */
	private double rake;

	/** Standard deviation type. */
	private String stdDevType;

	/** Map period-value/period-index. */
	private HashMap<Double, Integer> indexFromPerHashMap;

	/** Period index. */
	private int iper;

	/** For issuing warnings. */
	private transient ParameterChangeWarningListener warningListener = null;

	private String siteType;
	private String focMechType;

	// Site class Definitions -
	private StringParameter siteTypeParam = null;

	/**
	 * Construct attenuation relationship. Initialize parameters and parameter
	 * lists.
	 */
	public ZhaoEtAl_2006_AttenRel(ParameterChangeWarningListener warningListener) {

		// creates exceedProbParam
		super();

		this.warningListener = warningListener;

		initSupportedIntensityMeasureParams();

		indexFromPerHashMap = new HashMap<Double, Integer>();
		for (int i = 0; i < ZhaoEtAl2006Constants.PERIOD.length; i++) {
			indexFromPerHashMap
					.put(new Double(ZhaoEtAl2006Constants.PERIOD[i]),
							new Integer(i));
		}

		initEqkRuptureParams();
		initSiteParams();
		initPropagationEffectParams();
		initOtherParams();
		initIndependentParamLists();
		initParameterEventListeners();
	}

	/**
	 * Creates the two supported IM parameters (PGA and SA), as well as the
	 * independenParameters of SA (periodParam and dampingParam) and adds them
	 * to the supportedIMParams list. Makes the parameters non-editable.
	 */
	protected void initSupportedIntensityMeasureParams() {

		DoubleDiscreteConstraint periodConstraint = new DoubleDiscreteConstraint();
		for (int i = 1; i < ZhaoEtAl2006Constants.PERIOD.length; i++) {
			periodConstraint.addDouble(new Double(
					ZhaoEtAl2006Constants.PERIOD[i]));
		}
		periodConstraint.setNonEditable();
		saPeriodParam = new PeriodParam(periodConstraint);

		saDampingParam = new DampingParam();

		saParam = new SA_Param(saPeriodParam, saDampingParam);
		saParam.setNonEditable();

		pgaParam = new PGA_Param();
		pgaParam.setNonEditable();

		saParam.addParameterChangeWarningListener(warningListener);
		pgaParam.addParameterChangeWarningListener(warningListener);

		supportedIMParams.clear();
		supportedIMParams.addParameter(saParam);
		supportedIMParams.addParameter(pgaParam);
	}

	/**
	 * Initialize earthquake rupture parameter (moment magnitude, tectonic
	 * region type, focal depth, rake angle) and add to eqkRuptureParams list.
	 * Makes the parameters non-editable.
	 */
	protected void initEqkRuptureParams() {

		// moment magnitude
		magParam = new MagParam(ZhaoEtAl2006Constants.MAG_WARN_MIN,
				ZhaoEtAl2006Constants.MAG_WARN_MAX);

		// tectonic region type
		StringConstraint options = new StringConstraint();
		options.addString(TectonicRegionType.ACTIVE_SHALLOW.toString());
		options.addString(TectonicRegionType.SUBDUCTION_INTERFACE.toString());
		options.addString(TectonicRegionType.SUBDUCTION_SLAB.toString());
		tectonicRegionTypeParam = new TectonicRegionTypeParam(options,
				TectonicRegionType.ACTIVE_SHALLOW.toString());

		// focal depth (default zero km)
		focalDepthParam = new FocalDepthParam();
		
		// rake angle (default zero);
		rakeParam = new RakeParam();

		eqkRuptureParams.clear();
		eqkRuptureParams.addParameter(magParam);
	}
	
	/**
	 * Initialize site parameters (vs30) and adds it to the siteParams list.
	 * Makes the parameters non-editable.
	 */
	protected void initSiteParams() {

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
	protected void initPropagationEffectParams() {
		distanceRupParam = new DistanceRupParameter(0.0);
		distanceRupParam.addParameterChangeWarningListener(warningListener);
		DoubleConstraint warn = new DoubleConstraint(
				ZhaoEtAl2006Constants.DISTANCE_RUP_WARN_MIN,
				ZhaoEtAl2006Constants.DISTANCE_RUP_WARN_MAX);
		warn.setNonEditable();
		distanceRupParam.setWarningConstraint(warn);

		distanceRupParam.setNonEditable();
		propagationEffectParams.addParameter(distanceRupParam);
	}
	
	/**
	 * Initialize other Parameters (standard deviation type, component, sigma
	 * truncation type, sigma truncation level).
	 */
	protected void initOtherParams() {

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
	protected void initIndependentParamLists() {

		// params that the mean depends upon
		meanIndependentParams.clear();
		meanIndependentParams.addParameter(magParam);
		meanIndependentParams.addParameter(focalDepthParam);
		meanIndependentParams.addParameter(tectonicRegionTypeParam);
		meanIndependentParams.addParameter(distanceRupParam);
		meanIndependentParams.addParameter(vs30Param);
		meanIndependentParams.addParameter(rakeParam);

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
		imlAtExceedProbIndependentParams
				.addParameterList(exceedProbIndependentParams);
		imlAtExceedProbIndependentParams.addParameter(exceedProbParam);
	}
	
	/**
	 * Adds the parameter change listeners. This allows to listen to when-ever
	 * the parameter is changed.
	 */
	protected void initParameterEventListeners() {
		// earthquake rupture params
		magParam.addParameterChangeListener(this);
		tectonicRegionTypeParam.addParameterChangeListener(this);
		focalDepthParam.addParameterChangeListener(this);
		rakeParam.addParameterChangeListener(this);

		// site params
		vs30Param.addParameterChangeListener(this);

		// propagation effect param
		distanceRupParam.addParameterChangeListener(this);

		// standard deviation type param
		stdDevTypeParam.addParameterChangeListener(this);
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
	public void setEqkRupture(EqkRupture eqkRupture)
			throws InvalidRangeException {
		magParam.setValueIgnoreWarning(new Double(eqkRupture.getMag()));
		this.eqkRupture = eqkRupture;
		setPropagationEffectParams();
		// TODO
		// setFaultTypeFromRake(eqkRupture.getAveRake());
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

		// System.out.println("Zhao et al --->"+site.getParameter(SITE_TYPE_NAME).getValue());

		siteTypeParam.setValue((String) site.getParameter(
				ZhaoEtAl2006Constants.SITE_TYPE_NAME).getValue());
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
		if ((this.site != null) && (this.eqkRupture != null)) {
			distanceRupParam.setValue(eqkRupture, site);
		}
	}

	/**
	 * This sets the site and eqkRupture, and the related parameters, from the
	 * propEffect object passed in. Warning constrains are ingored.
	 * 
	 * @param propEffect
	 * @throws ParameterException
	 *             Thrown if the Site object doesn't contain a Vs30 parameter
	 * @throws InvalidRangeException
	 *             If not valid rake angle
	 */
	public void setPropagationEffect(PropagationEffect propEffect)
			throws ParameterException, InvalidRangeException {

		this.site = propEffect.getSite();
		this.eqkRupture = propEffect.getEqkRupture();
		siteTypeParam.setValue((String) site.getParameter(
				ZhaoEtAl2006Constants.SITE_TYPE_NAME).getValue());
		magParam.setValueIgnoreWarning(new Double(eqkRupture.getMag()));

		// TODO
		// setFaultTypeFromRake(eqkRupture.getAveRake());

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
			throw new ParameterException(
					C
							+ ": updateCoefficients(): "
							+ "The Intensity Measusre Parameter has not been set yet, unable to process.");
		}

		if (im.getName().equalsIgnoreCase(PGA_Param.NAME)) {
			iper = 0;
		} else {
			iper = ((Integer) indexFromPerHashMap.get(saPeriodParam.getValue()))
					.intValue();
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
			setCoeffIndex();// intensityMeasureChanged is set to false in this
							// method
		}

		// Computing the hypocentral depth
		// System.out.println("Zhao et al -->"+this.eqkRupture.getInfo());

		EvenlyGriddedSurfaceAPI surf = this.eqkRupture.getRuptureSurface();

		// ----------------------------------------------------------------------
		// MARCO 2010.03.15
		// Compute the hypocenter as the middle point of the rupture
		double hypoLon = 0.0;
		double hypoLat = 0.0;
		double hypoDep = 0.0;
		double cnt = 0.0;
		for (int j = 0; j < surf.getNumCols(); j++) {
			for (int k = 0; k < surf.getNumRows(); k++) {
				hypoLon += surf.getLocation(k, j).getLongitude();
				hypoLat += surf.getLocation(k, j).getLatitude();
				hypoDep = hypoDep + surf.getLocation(k, j).getDepth();
				cnt += 1;
			}
		}
		double chk = surf.getNumCols() * surf.getNumRows();

		hypoLon = hypoLon / cnt;
		hypoLat = hypoLat / cnt;
		hypoDep = hypoDep / cnt;

		// Return the computed mean
		return getMean(iper, mag, rRup, hypoDep);
	}

	/**
	 * @return The stdDev value
	 */
	public double getStdDev() {

		if (intensityMeasureChanged) {
			setCoeffIndex();// intensityMeasureChanged is set to false in this
							// method
		}
		return getStdDev(iper, stdDevType, tecRegType);
	}

	/**
	 * Allows the user to set the default parameter values for the selected
	 * Attenuation Relationship.
	 */
	public void setParamDefaults() {

		magParam.setValueAsDefault();
		fltTypeParam.setValueAsDefault();
		tectonicRegionTypeParam.setValueAsDefault();
		distanceRupParam.setValueAsDefault();
		saParam.setValueAsDefault();
		saPeriodParam.setValueAsDefault();
		saDampingParam.setValueAsDefault();
		pgaParam.setValueAsDefault();
		stdDevTypeParam.setValueAsDefault();
		siteTypeParam.setValue(ZhaoEtAl2006Constants.SITE_TYPE_DEFAULT);

		mag = ((Double) magParam.getValue()).doubleValue();
		rRup = ((Double) distanceRupParam.getValue()).doubleValue();
		focMechType = fltTypeParam.getValue().toString();
		tecRegType = tectonicRegionTypeParam.getValue().toString();
		siteType = siteTypeParam.getValue().toString();
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
	 * 
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
	public double getMean(int iper, double mag, double rRup, double hypodepth) {

		double flag_sc = 0.0; // This is unity for crustal events - Otherwise 0
		double flag_Fr = 0.0; // This is unity for reverse crustal events -
								// Otherwise 0
		double flag_Si = 0.0; // This is unity for interface events - Otherwise
								// 0
		double flag_Ss = 0.0; // This is unity for slab events - Otherwise 0
		double flag_Ssl = 0.0; // This is unity for slab events - Otherwise 0

		double hc = 125;
		double hlow = 15; // see bottom of left column page 902
		double delta_h = 0.0;
		double mc;
		double pFa = 0.0;
		double qFa = 0.0;
		double wFa = 0.0;
		double m2CorrFact = 0.0;

		// Site term correction
		double soilCoeff = 0.0;
		if (D)
			System.out.println("Site conditions: " + siteType);
		if (siteType.equals(ZhaoEtAl2006Constants.SITE_TYPE_HARD_ROCK)) {
			// Vs30 > 1100
			if (D)
				System.out.println("Hard Rock");
			soilCoeff = ZhaoEtAl2006Constants.Ch[iper];
		} else if (siteType.equals(ZhaoEtAl2006Constants.SITE_TYPE_ROCK)) {
			// 600 < Vs30 < 1100
			if (D)
				System.out.println("Rock");
			soilCoeff = ZhaoEtAl2006Constants.C1[iper];
		} else if (siteType.equals(ZhaoEtAl2006Constants.SITE_TYPE_HARD_SOIL)) {
			// 300 < Vs30 < 600
			if (D)
				System.out.println("Hard soil");
			soilCoeff = ZhaoEtAl2006Constants.C2[iper];
		} else if (siteType.equals(ZhaoEtAl2006Constants.SITE_TYPE_MEDIUM_SOIL)) {
			// 200 < Vs30 < 300
			if (D)
				System.out.println("Medium soil");
			soilCoeff = ZhaoEtAl2006Constants.C3[iper];
		} else if (siteType.equals(ZhaoEtAl2006Constants.SITE_TYPE_SOFT_SOIL)) {
			// Vs30 = 200
			if (D)
				System.out.println("Soft soil");
			soilCoeff = ZhaoEtAl2006Constants.C4[iper];
		} else {
			throw new RuntimeException("\n  Unrecognized site type \n");
		}

		// Setting the flags in order to account for tectonic region and focal
		// mechanism
		if (D)
			System.out.println("getMean: " + tecRegType);
		if (focMechType.equals(ZhaoEtAl2006Constants.FLT_FOC_MECH_REVERSE)
				&& tecRegType.equals(ZhaoEtAl2006Constants.FLT_TEC_ENV_CRUSTAL)) {
			flag_Fr = 1.0;
			//
			mc = 6.3;
			pFa = 0.0;
			qFa = ZhaoEtAl2006Constants.Qc[iper];
			wFa = ZhaoEtAl2006Constants.Wc[iper];
			if (D)
				System.out.println("Crustal - reverse");
		} else if (tecRegType.equals(ZhaoEtAl2006Constants.FLT_TEC_ENV_CRUSTAL)) {
			flag_sc = 1.0;
			//
			mc = 6.3;
			pFa = 0.0;
			qFa = ZhaoEtAl2006Constants.Qc[iper];
			wFa = ZhaoEtAl2006Constants.Wc[iper];
			if (D)
				System.out.println("Crustal - other");
		} else if (tecRegType
				.equals(ZhaoEtAl2006Constants.FLT_TEC_ENV_INTERFACE)) {
			flag_Si = 1.0;
			//
			mc = 6.3;
			pFa = 0.0;
			qFa = ZhaoEtAl2006Constants.Qi[iper];
			wFa = ZhaoEtAl2006Constants.Wi[iper];
			if (D)
				System.out.println("Interface - all");
		} else if (tecRegType.equals(ZhaoEtAl2006Constants.FLT_TEC_ENV_SLAB)) {
			flag_Ss = 1.0;
			flag_Ssl = 1.0;
			//
			mc = 6.5;
			pFa = ZhaoEtAl2006Constants.Ps[iper];
			qFa = ZhaoEtAl2006Constants.Qs[iper];
			wFa = ZhaoEtAl2006Constants.Ws[iper];
			if (D)
				System.out.println("Slab - all");
		} else {
			System.out.println("+++" + tecRegType.toString() + "--");
			System.out.println("+++" + focMechType.toString() + "--");
			throw new RuntimeException(
					"\n  Cannot handle this combination: \n  tectonic region + focal mechanism ");
		}

		// This is used just for verification - DO NOT use it for regular
		// calculations
		// hypodepth = this.eqkRupture.getHypocenterLocation().getDepth();
		// System.out.println("real hypocentral depth:"+hypodepth);
		// ----------------------------------------------------------------------
		// MARCO 2010.03.15

		// Depth dummy variable delta_h for depth term =
		// e[iper]*(hypodepth-hc)*delta_h
		if (hypodepth > hlow) {
			delta_h = 1;
			if (hypodepth < hc) {
				hypodepth = hypodepth - hlow;
			} else {
				hypodepth = hc - hlow;
			}
		} else {
			delta_h = 0;
		}

		// Correction factor
		m2CorrFact = pFa * (mag - mc) + qFa * Math.pow((mag - mc), 2.0) + wFa;
		if (D)
			System.out
					.printf("corr fact: %10.6f mag: %5.2f\n", m2CorrFact, mag);

		// TODO The assignment of this variable mu
		// MARCO
		// System.out.printf("(%.0f) %.0f slab (%.0f %.0f) \n",flag_Fr,flag_Si,flag_Ss,flag_Ssl);
		// System.out.printf("%.3f %.3f %.3f %.3f \n",a[iper],b[iper],c[iper],d[iper]);
		// System.out.printf("%.3f %.3f %.3f %.3f\n",rRup,Qs[iper],Ws[iper],mag);
		// System.out.printf("%.3f %.3f %.3f %.3f %.3f\n",rRup,Qc[iper],Wc[iper],mc,Sr[iper]);
		// System.out.printf("%.3f %.3f %.3f %.3f \n",hypodepth,Qi[iper],Wi[iper],mc);

		double r = rRup + ZhaoEtAl2006Constants.C[iper]
				* Math.exp(ZhaoEtAl2006Constants.D[iper] * mag);
		// System.out.println(hypodepth+" "+rRup+" "+Si[iper]+" "+Qi[iper]+" "+Wi[iper]);
		// System.out.println("  r: "+r);
		// System.out.println("  hypo term: "+e[iper] * hypodepth *
		// delta_h+" hypo dep:"+hypodepth);

		double lnGm = ZhaoEtAl2006Constants.A[iper] * mag
				+ ZhaoEtAl2006Constants.B[iper] * rRup - Math.log(r)
				+ ZhaoEtAl2006Constants.E[iper] * hypodepth * delta_h
				+ flag_Fr
				* ZhaoEtAl2006Constants.Sr[iper]
				+ flag_Si
				* ZhaoEtAl2006Constants.Si[iper]
				+
				// The following options give the same results (the first takes
				// what's written in the
				// BSSA paper the second follows Zhao's code implementation
				flag_Ss * ZhaoEtAl2006Constants.Ss[iper] + flag_Ssl
				* ZhaoEtAl2006Constants.Ssl[iper] * Math.log(rRup) + // Option 1
				// flag_Ss *SsZhao[iper] + flag_Ssl * Ssl[iper] *
				// (Math.log(rRup)-Math.log(125.0)) + // Option 2
				soilCoeff;

		// Return the computed mean value
		lnGm += m2CorrFact;

		// Convert form cm/s2 to g
		return Math.log(Math.exp(lnGm) / 981);
	}

	/**
	 * This gets the standard deviation for specific parameter settings. We
	 * might want another version that takes the actual SA period rather than
	 * the period index.
	 * 
	 * @param iper
	 * @param vs30
	 * @param f_rv
	 * @param f_nm
	 * @param rRup
	 * @param distRupMinusJB_OverRup
	 * @param distRupMinusDistX_OverRup
	 * @param f_hw
	 * @param dip
	 * @param mag
	 * @param depthTop
	 * @param aftershock
	 * @param stdDevType
	 * @param f_meas
	 * @return
	 */
	public double getStdDev(int iper, String stdDevType, String tecRegType) {

		if (tecRegType.equals(ZhaoEtAl2006Constants.FLT_TEC_ENV_CRUSTAL)) {
			if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_TOTAL))
				return Math.sqrt(ZhaoEtAl2006Constants.Tau_c[iper]
						* ZhaoEtAl2006Constants.Tau_c[iper]
						+ ZhaoEtAl2006Constants.sigma[iper]
						* ZhaoEtAl2006Constants.sigma[iper]);
			else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_NONE))
				return 0;
			else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_INTER))
				return ZhaoEtAl2006Constants.sigma[iper];
			else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_INTRA))
				return ZhaoEtAl2006Constants.Tau_c[iper];
			else
				return Double.NaN;
		} else if (tecRegType
				.equals(ZhaoEtAl2006Constants.FLT_TEC_ENV_INTERFACE)) {
			if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_TOTAL))
				return Math.sqrt(ZhaoEtAl2006Constants.Tau_i[iper]
						* ZhaoEtAl2006Constants.Tau_i[iper]
						+ ZhaoEtAl2006Constants.sigma[iper]
						* ZhaoEtAl2006Constants.sigma[iper]);
			else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_NONE))
				return 0;
			else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_INTER))
				return ZhaoEtAl2006Constants.sigma[iper];
			else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_INTRA))
				return ZhaoEtAl2006Constants.Tau_i[iper];
			else
				return Double.NaN;
		} else if (tecRegType.equals(ZhaoEtAl2006Constants.FLT_TEC_ENV_SLAB)) {
			if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_TOTAL))
				return Math.sqrt(ZhaoEtAl2006Constants.Tau_s[iper]
						* ZhaoEtAl2006Constants.Tau_s[iper]
						+ ZhaoEtAl2006Constants.sigma[iper]
						* ZhaoEtAl2006Constants.sigma[iper]);
			else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_NONE))
				return 0;
			else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_INTER))
				return ZhaoEtAl2006Constants.sigma[iper];
			else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_INTRA))
				return ZhaoEtAl2006Constants.Tau_s[iper];
			else
				return Double.NaN;
		}
		return 0;
	}

	/**
	 * This listens for parameter changes and updates the primitive parameters
	 * accordingly
	 * 
	 * @param e
	 *            ParameterChangeEvent
	 */
	public void parameterChange(ParameterChangeEvent e) {

		String pName = e.getParameterName();
		Object val = e.getNewValue();

		if (D)
			System.out.println("Changed param: " + pName);

		if (pName.equals(DistanceRupParameter.NAME)) {
			rRup = ((Double) val).doubleValue();
		} else if (pName.equals(MagParam.NAME)) {
			mag = ((Double) val).doubleValue();
		} else if (pName.equals(StdDevTypeParam.NAME)) {
			stdDevType = (String) val;
		} else if (pName.equals(FaultTypeParam.NAME)) {
			focMechType = fltTypeParam.getValue().toString();
		} else if (pName.equals(TectonicRegionTypeParam.NAME)) {
			tecRegType = tectonicRegionTypeParam.getValue().toString();
			if (D)
				System.out.println("tecRegType new value:" + tecRegType);
		} else if (pName.equals(ZhaoEtAl2006Constants.SITE_TYPE_NAME)) {
			siteType = this.getParameter(ZhaoEtAl2006Constants.SITE_TYPE_NAME)
					.getValue().toString();
		} else if (pName.equals(PeriodParam.NAME)) {
			intensityMeasureChanged = true;
		}
	}

	/**
	 * Allows to reset the change listeners on the parameters
	 */
	public void resetParameterEventListeners() {
		magParam.removeParameterChangeListener(this);
		fltTypeParam.removeParameterChangeListener(this);
		tectonicRegionTypeParam.removeParameterChangeListener(this);
		siteTypeParam.removeParameterChangeListener(this);
		distanceRupParam.removeParameterChangeListener(this);
		stdDevTypeParam.removeParameterChangeListener(this);
		saPeriodParam.removeParameterChangeListener(this);
		this.initParameterEventListeners();
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
				"http://www.opensha.org/documentation/modelsImplemented/attenRel/ZhaoEtAl_2006.html");
	}

}
