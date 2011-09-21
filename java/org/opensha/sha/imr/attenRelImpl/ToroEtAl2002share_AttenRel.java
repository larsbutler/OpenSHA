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
import org.opensha.sha.imr.param.IntensityMeasureParams.PeriodParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.SA_Param;
import org.opensha.sha.imr.param.OtherParams.ComponentParam;
import org.opensha.sha.imr.param.OtherParams.SigmaTruncLevelParam;
import org.opensha.sha.imr.param.OtherParams.SigmaTruncTypeParam;
import org.opensha.sha.imr.param.OtherParams.StdDevTypeParam;
import org.opensha.sha.imr.param.PropagationEffectParams.DistanceJBParameter;
import org.opensha.sha.imr.param.PropagationEffectParams.DistanceRupParameter;

/**
 * <b>Title:</b> Toro_2002_AttenRel
 * <p>
 * 
 * <b>Description:</b> This implements the updated GMPE developed by Toro et al
 * - 2002, (www.riskeng.com/PDF/atten_toro_extended.pdf) The GMPE is adjusted to
 * account the style -of faulting and a default rock soil (Vs30 >=800m/sec) The
 * adjustment coefficients were proposed by S. Drouet [2010]; Supported period
 * values (s). Period 0.5s was obtained as a linear interpolation between 0.4
 * and 1.0s; Warning: The coefficients for periods 3.00 and 4.00sec were
 * obtained as a function of SA(2sec) and ratios between SA(3)/SA(4sec) and
 * SA(2)/SA(4sec) Disclaimer: The adjustment of the SA(3sec) and SA(4sec) are
 * obtained in the framework of SHARE project. Supported Intensity-Measure
 * Parameters:
 * <p>
 * <UL>
 * <LI>saParam - Response Spectral Acceleration
 * <LI>PGA - Peak Ground Acceleration
 * </UL>
 * <p>
 * Other Independent Parameters:
 * <p>
 * <UL>
 * <LI>magParam - moment magnitude
 * <LI>distanceJBParam - JB distance
 * <LI>componentParam - average horizontal, average horizontal (GMRoti50)
 * <LI>rakeParam - rake angle
 * <LI>stdDevTypeParam - total, none
 * </UL>
 * <p>
 * 
 * @author l.danciu
 * @created July, 2011
 * @version 1.0
 */

public class ToroEtAl2002share_AttenRel extends AttenuationRelationship
		implements ScalarIntensityMeasureRelationshipAPI, NamedObjectAPI,
		ParameterChangeListener {

	public final static String SHORT_NAME = "ToroEtAl2002";
	private static final long serialVersionUID = 1234567890987654353L;

	public final static String NAME = "Toro et al. (2002)";

	/** Period index. */
	private int iper;

	/** Moment magnitude. */
	private double mag;

	/** rake angle. */
	private double rake;

	/** Rupture distance. */
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
	public ToroEtAl2002share_AttenRel(
			ParameterChangeWarningListener warningListener) {

		super();

		this.warningListener = warningListener;

		initSupportedIntensityMeasureParams();
		indexFromPerHashMap = new HashMap<Double, Integer>();
		for (int i = 1; i < ToroEtAl2002Constants.PERIOD.length; i++) {
			indexFromPerHashMap
					.put(new Double(ToroEtAl2002Constants.PERIOD[i]),
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
	 * Creates the two supported IM parameters (PGA and SA), as well as the
	 * independenParameters of SA (periodParam and dampingParam) and adds them
	 * to the supportedIMParams list. Makes the parameters non-editable.
	 */
	protected final void initSupportedIntensityMeasureParams() {

		// set supported periods for spectral acceleration
		DoubleDiscreteConstraint periodConstraint = new DoubleDiscreteConstraint();
		for (int i = 1; i < ToroEtAl2002Constants.PERIOD.length; i++) {
			periodConstraint.addDouble(new Double(
					ToroEtAl2002Constants.PERIOD[i]));
		}
		periodConstraint.setNonEditable();
		// set period param (default is 1s, which is provided by Toro et al 2002
		// GMPE)
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
	 * Initialize earthquake rupture parameter (moment magnitude, rake) and add
	 * to eqkRuptureParams list. Makes the parameters non-editable.
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
	 * Initialize site parameters.
	 */
	protected final void initSiteParams() {
		siteParams.clear();
	}

	/**
	 * Initialize Propagation Effect parameters (JB distance) and adds them to
	 * the propagationEffectParams list. Makes the parameters non-editable.
	 */
	protected final void initPropagationEffectParams() {

		distanceJBParam = new DistanceJBParameter(
				ToroEtAl2002Constants.DISTANCE_JB_WARN_MIN);
		distanceJBParam.addParameterChangeWarningListener(warningListener);
		DoubleConstraint warn = new DoubleConstraint(new Double(0.00),
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
		constraint.addString(ComponentParam.COMPONENT_GMRotI50);
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
	 * This sets the eqkRupture related parameters (moment magnitude, rake)
	 * based on the eqkRupture passed in. The internally held eqkRupture object
	 * is also set as that passed in.
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
	 * Sets the internally held Site object as that passed in.
	 */
	public final void setSite(final Site site) {
		this.site = site;
		setPropagationEffectParams();
	}

	/**
	 * This calculates the Rupture Distance propagation effect parameter based
	 * on the current site and eqkRupture.
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
	 * Compute mean. Applies correction for style of faulting and generic rock -
	 * Vs30 >= 800m/s .
	 */
	public double getMean() {
		if (rJB > USER_MAX_DISTANCE) {
			return VERY_SMALL_MEAN;
		} else {
			setPeriodIndex();
			return getMean(iper, mag, rJB, rake);
		}
	}

	public double getStdDev() {
		if (intensityMeasureChanged) {
			setPeriodIndex();
		}
		return getStdDev(iper, mag, rJB, stdDevType);
	}

	/**
	 * This computes the mean ln(Y)
	 */
	public double getMean(final int iper, double mag, double rJB, double rake) {
		/**
		 * This is to avoid very small values for rJB
		 * 
		 * */
		if (rJB < 1e-3) {
			rJB = 1;
		}

		double lnY_rock_adj = Double.NaN;
		double[] f = computeStyleOfFaultingTerm(iper, rake);
		double lnY_Hrock = computeHardRockResponse(iper, mag, rJB);

		/**
		 * This extends the model to SA(3sec) by multiplying the SA(2sec) with
		 * the ratio SA(3sec)/SA(2sec) obtained as an average from the following
		 * GMPEs: CF2008 CY2008 ZH2006 Campbell2003 AkB2010
		 * */
		if (ToroEtAl2002Constants.PERIOD[iper] == 3.00) {
			lnY_rock_adj = Math.exp(lnY_Hrock)
					* ToroEtAl2002Constants.T2sec_TO_T3sec_factor * f[2]
					* ToroEtAl2002Constants.AFrock[iper];
			/**
			 * This extends the model to SA(4sec) by multiplying the SA(3sec)
			 * with the ratio SA(4sec)/SA(3sec) obtained as an average from the
			 * following GMPEs: CF2008 CY2008 ZH2006 Campbell2003
			 * 
			 * */
		} else if (ToroEtAl2002Constants.PERIOD[iper] == 4.00) {
			lnY_rock_adj = Math.exp(lnY_Hrock)
					* ToroEtAl2002Constants.T3sec_TO_T4sec_factor * f[2]
					* ToroEtAl2002Constants.AFrock[iper];
		} else {
			lnY_rock_adj = Math.exp(lnY_Hrock) * f[2]
					* ToroEtAl2002Constants.AFrock[iper];
		}
		return Math.log(lnY_rock_adj);
	}

	private double computeHardRockResponse(int iper, double mag, double rJB) {

		double magDiff = mag - 6.0;

		double rM = Math.sqrt(rJB * rJB + ToroEtAl2002Constants.c7[iper]
				* ToroEtAl2002Constants.c7[iper]
				* Math.pow(Math.exp(-1.25 + 0.227 * mag), 2));

		double f1 = ToroEtAl2002Constants.c1[iper]
				+ ToroEtAl2002Constants.c2[iper] * magDiff
				+ ToroEtAl2002Constants.c3[iper] * magDiff * magDiff;

		double f2 = ToroEtAl2002Constants.c4[iper] * Math.log(rM);

		double f3 = (ToroEtAl2002Constants.c5[iper] - ToroEtAl2002Constants.c4[iper])
				* Math.max(Math.log(rM / 100), 0);

		double f4 = ToroEtAl2002Constants.c6[iper] * rM;

		double lnY = f1 - f2 - f3 - f4;

		return lnY;
	}

	/**
	 * Compute style-of-faulting adjustment
	 **/
	public double[] computeStyleOfFaultingTerm(final int iper, final double rake) {
		double[] f = new double[3];
		if (rake > ToroEtAl2002Constants.FLT_TYPE_NORMAL_RAKE_LOWER
				&& rake <= ToroEtAl2002Constants.FLT_TYPE_NORMAL_RAKE_UPPER) {
			f[0] = 1.0;
			f[1] = 0.0;
			f[2] = f[0]
					* Math.pow(ToroEtAl2002Constants.Frss[iper],
							(1 - ToroEtAl2002Constants.pR))
					* Math.pow(ToroEtAl2002Constants.Fnss,
							-ToroEtAl2002Constants.pN);
		} else if (rake > ToroEtAl2002Constants.FLT_TYPE_REVERSE_RAKE_LOWER
				&& rake <= ToroEtAl2002Constants.FLT_TYPE_REVERSE_RAKE_UPPER) {
			f[0] = 0.0;
			f[1] = 1.0;
			f[2] = f[1]
					* Math.pow(ToroEtAl2002Constants.Frss[iper],
							-ToroEtAl2002Constants.pR)
					* Math.pow(ToroEtAl2002Constants.Fnss,
							(1 - ToroEtAl2002Constants.pN));
		} else {
			f[0] = 0.0;
			f[1] = 0.0;
			f[2] = Math.pow(ToroEtAl2002Constants.Frss[iper],
					-ToroEtAl2002Constants.pR)
					* Math.pow(ToroEtAl2002Constants.Fnss,
							-ToroEtAl2002Constants.pN);
		}
		return f;
	}

	public double getStdDev(int iper, double mag, double rJB, String stdDevType) {
		double sigmaaM = Double.NaN;
		double sigmaaR = Double.NaN;
		double sigmae = Double.NaN;
		double sigmatot = Double.NaN;

		if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_NONE))
			return 0;
		else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_TOTAL)) {
			if (mag < 5.0) {
				sigmaaM = ToroEtAl2002Constants.m50[iper];
			} else if ((mag >= 5.0) && (mag <= 5.5)) {
				sigmaaM = ToroEtAl2002Constants.m50[iper]
						+ (ToroEtAl2002Constants.m55[iper] - ToroEtAl2002Constants.m50[iper])
						/ (5.5 - 5.0) * (mag - 5.0);
			} else if ((mag > 5.5) && (mag < 8.0)) {
				sigmaaM = ToroEtAl2002Constants.m55[iper]
						+ (ToroEtAl2002Constants.m80[iper] - ToroEtAl2002Constants.m55[iper])
						/ (8.0 - 5.5) * (mag - 5.5);
			} else {
				sigmaaM = ToroEtAl2002Constants.m80[iper];
			}

			if (rJB < 5.0) {
				sigmaaR = ToroEtAl2002Constants.r05[iper];
			} else if ((rJB >= 5.0) && (rJB <= 20.0)) {
				sigmaaR = ToroEtAl2002Constants.r05[iper]
						+ (ToroEtAl2002Constants.r20[iper] - ToroEtAl2002Constants.r05[iper])
						/ (20.0 - 5.0) * (rJB - 5.0);
			} else {
				sigmaaR = ToroEtAl2002Constants.r20[iper];
			}

			if (ToroEtAl2002Constants.PERIOD[iper] >= 2.0) {
				sigmae = 0.34 + 0.06 * (mag - 6.0);
			} else {
				sigmae = 0.36 + 0.07 * (mag - 6.0);
			}
			double sigmaatot = Math.sqrt(sigmaaM * sigmaaM + sigmaaR * sigmaaR);

			sigmatot = (Math.sqrt(sigmaatot * sigmaatot + sigmae * sigmae))
					* ToroEtAl2002Constants.sig_AFrock[iper];

			return sigmatot;
		} else
			throw new RuntimeException("Standard deviation type: " + stdDevType
					+ " not recognized");
	}

	/**
	 * Allows the user to set the default parameter values for the selected
	 * Attenuation Relationship.
	 */
	public final void setParamDefaults() {

		magParam.setValueAsDefault();
		rakeParam.setValueAsDefault();
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
		} else if (pName.equals(RakeParam.NAME)) {
			rake = ((Double) val).doubleValue();
		} else if (pName.equals(DistanceRupParameter.NAME)) {
			rJB = ((Double) val).doubleValue();
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
		stdDevTypeParam.removeParameterChangeListener(this);
		this.initParameterEventListeners();
	}

	/**
	 * Adds the parameter change listeners. This allows to listen to when-ever
	 * the parameter is changed.
	 */
	protected void initParameterEventListeners() {

		magParam.addParameterChangeListener(this);
		rakeParam.addParameterChangeListener(this);
		distanceJBParam.addParameterChangeListener(this);
		stdDevTypeParam.addParameterChangeListener(this);
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
	 * Returns the URL of the AttenuationRelationship documentation.
	 * Currently returns null because not URL has been created.
	 */
	public URL getAttenuationRelationshipURL() throws MalformedURLException {
		return null;
	}
}
