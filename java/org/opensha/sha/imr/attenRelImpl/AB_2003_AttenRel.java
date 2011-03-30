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
import org.opensha.sha.faultSurface.EvenlyGriddedSurfaceAPI;
import org.opensha.sha.imr.AttenuationRelationship;
import org.opensha.sha.imr.PropagationEffect;
import org.opensha.sha.imr.ScalarIntensityMeasureRelationshipAPI;
import org.opensha.sha.imr.param.EqkRuptureParams.MagParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.DampingParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.PGA_Param;
import org.opensha.sha.imr.param.IntensityMeasureParams.PeriodParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.SA_Param;
import org.opensha.sha.imr.param.OtherParams.ComponentParam;
import org.opensha.sha.imr.param.OtherParams.StdDevTypeParam;
import org.opensha.sha.imr.param.OtherParams.TectonicRegionTypeParam;
import org.opensha.sha.imr.param.PropagationEffectParams.DistanceRupParameter;
import org.opensha.sha.util.TectonicRegionType;

/**
 * <b>Title:</b> AB_2003_AttenRel
 * <p>
 * <b>Description:</b> Class implementing attenuation relationship described in:
 * "Empirical Ground Motion Relations for Subduction-Zone Earthquakes and their
 * application to Cascadia and other regions" Bulletin of the Seismological
 * Society of America, Vol. 93, No. 4, pp 1703-1729, 2003. The class implements
 * the global model but not the corrections for Japan/Cascadia.
 * <p>
 * Supported Intensity-Measure Parameters:
 * <p>
 * <UL>
 * <LI>pgaParam - Peak Ground Acceleration
 * <LI>saParam - Response Spectral Acceleration
 * </UL>
 * <p>
 * Other Independent Parameters:
 * <p>
 * <UL>
 * <LI>magParam - moment magnitude
 * <LI>distanceRupParam - closest distance to rupture surface
 * <LI>siteTypeParam - "NEHRP B", "NEHRP C", "NEHRP D", "NEHRP E"
 * <LI>fltTypeParam - style of faulting "Interface" and "InSlab"
 * <LI>componentParam - random horizontal component
 * <LI>stdDevTypeParam - total, inter-event, intra-event.
 * </UL>
 * <p>
 *
 * <p>
 *
 * Verification - This model has been validated (see {@link AB_2003_test})
 * against tables provided by Céline Beauval
 * (<celine.beauval@obs.ujf-grenoble.fr>) using mathSHA. Tests were implemented
 * to check median PGA and SA (1Hz) for interface and intraslab events, at
 * different magnitude (7.0,8.0.8.8), for different site types (NERPH B, C, D).
 *
 *
 * </p>
 *
 **
 * @author L. Danciu
 * @version 1.0, December 2010
 */
public class AB_2003_AttenRel extends AttenuationRelationship implements
		ScalarIntensityMeasureRelationshipAPI, NamedObjectAPI,
		ParameterChangeListener {

	/**
	 * Class name.
	 */
	private static final String C = "AB_2003_AttenRel";
	/**
	 * Short name.
	 */
	public static final String SHORT_NAME = "AB2003";

	/**
	 * Attenuation relationship name.
	 */
	public static final String NAME = "Atkinson & Boore 2003";

	/**
	 * Version number.
	 */
	private static final long serialVersionUID = 1234567890987654353L;

	/**
	 * Supported frequency values.
	 */
	private static final double[] FREQ = {0.00000, 25.0000, 10.0000,
		5.00000, 2.50000, 1.00000, 0.50000, 0.33000};
	/**
	 * Supported period values.
	 */
	private static final double[] PERIOD = {0.00000, 0.04000, 0.10000,
		0.20000, 0.40000, 1.00000, 2.00000, 3.00000 };
	/**
	 * Interface c1 coefficients.
	 */
	private static final double[] INTER_C1 = {2.99100, 2.87530, 2.77890,
		2.66380, 2.52490, 2.14420, 2.19070, 2.30100 };
	/**
	 * Interface c2 coefficients.
	 */
	private static final double[] INTER_C2 = {0.03525, 0.07052, 0.09841,
		0.12386, 0.14770, 0.13450, 0.07148, 0.02237 };
	/**
	 * Interface c3 coefficients.
	 */
	private static final double[] INTER_C3 = {0.00759, 0.01004, 0.00974,
		0.00884, 0.00728, 0.00521, 0.00224, 0.00012 };
	/**
	 * Interface c4 coefficients.
	 */
	private static final double[] INTER_C4 = {-0.00206, -0.00278, -0.00287,
		-0.00280, -0.00235, -0.00110, 0.00000, 0.00000 };
	/**
	 * Interface c5 coefficients.
	 */
	private static final double[] INTER_C5 = {0.19000, 0.15000, 0.15000,
		0.15000, 0.13000, 0.10000, 0.10000, 0.10000 };
	/**
	 * Interface c6 coefficients.
	 */
	private static final double[] INTER_C6 = {0.24000, 0.20000, 0.23000,
		0.27000, 0.37000, 0.30000, 0.25000, 0.25000 };
	/**
	 * Interface c7 coefficients.
	 */
	private static final double[] INTER_C7 = {0.29000, 0.20000, 0.20000,
		0.25000, 0.38000, 0.55000, 0.40000, 0.36000 };
	/**
	 * Interface total standard deviation.
	 */
	private static final double[] INTER_TOTAL_STD = {0.23000, 0.26000,
		0.27000, 0.28000, 0.29000, 0.34000, 0.34000, 0.36000 };
	/**
	 * Interface intra-event standard deviation.
	 */
	private static final double[] INTER_INTRAEVENT_STD = {0.20000, 0.22000,
		0.25000, 0.25000, 0.25000, 0.28000, 0.29000, 0.31000 };
	/**
	 * Interface inter-event standard deviation.
	 */
	private static final double[] INTER_INTEREVENT_STD = {0.11000, 0.14000,
		0.10000, 0.13000, 0.15000, 0.19000, 0.18000, 0.18000 };
	/**
	 * Intraslab c1 coefficients.
	 */
	private static final double[] INTRA_C1 = {-0.04713, 0.50697, 0.43928,
		0.51589, 0.00545, -1.02133, -2.39234, -3.70012 };
	/**
	 * Intraslab c2 coefficients.
	 */
	private static final double[] INTRA_C2 = {0.69090, 0.63273, 0.66675,
		0.69186, 0.77270, 0.87890, 0.99640, 1.11690 };
	/**
	 * Intraslab c3 coefficients.
	 */
	private static final double[] INTRA_C3 = {0.01130, 0.01275, 0.01080,
		0.00572, 0.00173, 0.00130, 0.00364, 0.00615 };
	/**
	 * Intraslab c4 coefficients.
	 */
	private static final double[] INTRA_C4 = {-0.00202, -0.00234,
		-0.00219, -0.00192, -0.00178, -0.00173, -0.00118, -0.00045 };
	/**
	 * Intraslab c5 coefficients.
	 */
	private static final double[] INTRA_C5 = {0.19000, 0.15000, 0.15000,
		0.15000, 0.13000, 0.10000, 0.10000, 0.10000 };
	/**
	 * Intraslab c6 coefficients.
	 */
	private static final double[] INTRA_C6 = {0.24000, 0.20000, 0.23000,
		0.27000, 0.37000, 0.30000, 0.25000, 0.25000 };
	/**
	 * Intraslab c7 coefficients.
	 */
	private static final double[] INTRA_C7 = {0.29000, 0.20000, 0.20000,
		0.25000, 0.38000, 0.55000, 0.40000, 0.36000 };
	/**
	 * Intraslab total standard deviation.
	 */
	private static final double[] INTRA_TOTAL_STD = {0.27000, 0.25000,
		0.28000, 0.28000, 0.28000, 0.29000, 0.30000, 0.30000 };
	/**
	 * Intraslab intra event standard deviation.
	 */
	private static final double[] INTRA_INTRAEVENT_STD = {0.23000, 0.24000,
		0.27000, 0.26000, 0.26000, 0.27000, 0.28000, 0.29000 };
	/**
	 * Intraslab inter event standard deviation.
	 */
	private static final double[] INTRA_INTEREVENT_STD = {0.14000, 0.07000,
		0.07000, 0.10000, 0.10000, 0.11000, 0.11000, 0.08000 };

	/**
	 * Map period value - period index.
	 */
	private HashMap<Double, Integer> indexFromPerHashMap;
	/**
	 * Period index.
	 */
	private int iper;
	/**
	 * Moment magnitude.
	 */
	private double mag;
	/**
	 * Closest distance to rupture.
	 */
	private double rRup;
	/**
	 * Delta factor (for distance calculation).
	 */
	private double delta;
	/**
	 * sl factor (for soil amplification).
	 */
	private double sl;
	/**
	 * Site type.
	 */
	private String siteType;
	/**
	 * Standard deviation type.
	 */
	private String stdDevType;
	/**
	 * Tectonic region type.
	 */
	private String tecRegType;
	/**
	 * log10 to natural log conversion factor.
	 */
	private static final double LOG_2_LN = 2.302585;

	/**
	 * Site type.
	 */
	private StringParameter siteTypeParam = null;
	/**
	 * Site type info string.
	 */
	public static final String SITE_TYPE_INFO =
		"Geological conditions at the site";
	/**
	 * Site type name.
	 */
	public static final String SITE_TYPE_NAME = "AB et al 2003 Site Type";
	/**
	 * Rock description: Vs30 > 760m/s NEHRP Class B.
	 */
	public static final String SITE_TYPE_ROCK = "NEHRP B";
	/**
	 *  Hard rock description: 360< Vs30 = 760m/s NEHRP Class C.
	 */
	public static final String SITE_TYPE_HARD_SOIL = "NEHRP C";
	/**
	 *  Hard rock description: 180< Vs30 = 360m/s NEHRP Class D.
	 */
	public static final String SITE_TYPE_MEDIUM_SOIL = "NEHRP D";
	/**
	 *  Hard rock description: Vs30 < 180m/s NEHRP Class E.
	 */
	public static final String SITE_TYPE_SOFT_SOIL = "NEHRP E";
	/**
	 * Default site type.
	 */
	public static final String SITE_TYPE_DEFAULT = SITE_TYPE_ROCK;

	/**
	 * Subduction interface faulting.
	 */
	public static final String FLT_TEC_ENV_INTERFACE =
		TectonicRegionType.SUBDUCTION_INTERFACE
			.toString();
	/**
	 * Subduction intraslab faulting.
	 */
	public static final String FLT_TEC_ENV_INSLAB =
		TectonicRegionType.SUBDUCTION_SLAB
			.toString();

	/**
	 * Minimum magnitude.
	 */
	protected static final Double MAG_WARN_MIN = new Double(5.5);
	/**
	 * Maximum magnitude.
	 */
	protected static final Double MAG_WARN_MAX = new Double(8.5);

	/**
	 * Minimum rupture distance.
	 */
	protected static final Double DISTANCE_RUP_WARN_MIN = new Double(0.0);
	/**
	 * Maximum rupture distance.
	 */
	protected static final Double DISTANCE_RUP_WARN_MAX = new Double(500.0);

	/**
	 * Minimum hypocentral depth.
	 */
	protected static final Double DEPTH_HYPO_WARN_MIN = new Double(0.0);
	/**
	 * Maximum hypocentral depth.
	 */
	protected static final Double DEPTH_HYPO_WARN_MAX = new Double(125.0);

	/**
	 * For issuing warnings.
	 */
	private transient ParameterChangeWarningListener warningListener = null;

	/**
	 * Contruct attenuation relationship. Initialize parameter lists.
	 */
	public AB_2003_AttenRel(final ParameterChangeWarningListener warningListener) {
		super();

		this.warningListener = warningListener;

		initSupportedIntensityMeasureParams();

		indexFromPerHashMap = new HashMap<Double, Integer>();
		for (int i = 0; i < PERIOD.length; i++) {
			indexFromPerHashMap.put(new Double(PERIOD[i]), new Integer(i));
		}

		initEqkRuptureParams();
		initSiteParams();
		initPropagationEffectParams();
		initOtherParams();
		initIndependentParamLists();
		initParameterEventListeners();
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
	public final void setEqkRupture(final EqkRupture eqkRupture){
		magParam.setValueIgnoreWarning(new Double(eqkRupture.getMag()));
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
	public final void setSite(final Site site) throws ParameterException {
		siteTypeParam.setValue((String) site.getParameter(SITE_TYPE_NAME)
				.getValue());
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

	public final void setPropagationEffectParams() {
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
	public final void setPropagationEffect(final PropagationEffect propEffect)
			throws ParameterException, InvalidRangeException {

		this.site = propEffect.getSite();
		this.eqkRupture = propEffect.getEqkRupture();
		siteTypeParam.setValue((String) site.getParameter(SITE_TYPE_NAME)
				.getValue());
		magParam.setValueIgnoreWarning(new Double(eqkRupture.getMag()));

		// set the distance param
		propEffect.setParamValue(distanceRupParam);
	}

	/**
	 *
	 * @throws ParameterException
	 */
	protected final void setCoeffIndex() throws ParameterException {

		if (im == null) {
			throw new ParameterException(
					C
							+ ": updateCoefficients(): "
							+ "The Intensity Measusre Parameter" +
							" has not been set yet, unable to process.");
		}

		if (im.getName().equalsIgnoreCase(PGA_Param.NAME)) {
			iper = 0;
		} else {
			iper = ((Integer) indexFromPerHashMap.
					get(saPeriodParam.getValue()))
					.intValue();
		}
		intensityMeasureChanged = false;
	}

	/**
	 *
	 */
	public final double getMean() {

		// Check if distance is beyond the user specified max
		if (rRup > USER_MAX_DISTANCE) {
			return VERY_SMALL_MEAN;
		}

		if (intensityMeasureChanged) {
			setCoeffIndex();// intensityMeasureChanged is set to false in this
							// method
		}

		// Computing the hypocentral depth
		// System.out.println("AB2003 et al -->"+this.eqkRupture.getInfo());
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
		return getMean(iper, mag, rRup, siteType, tecRegType, hypoDep);

	}

	/**
	 * @return The stdDev value
	 */
	public final double getStdDev() {

		if (intensityMeasureChanged) {
			setCoeffIndex();
		}
		return getStdDev(iper, stdDevType, tecRegType);
	}

	/**
	 * Allows the user to set the default parameter values for the selected
	 * Attenuation Relationship.
	 */
	public final void setParamDefaults() {

		magParam.setValueAsDefault();
		tectonicRegionTypeParam.setValueAsDefault();
		distanceRupParam.setValueAsDefault();
		saParam.setValueAsDefault();
		saPeriodParam.setValueAsDefault();
		saDampingParam.setValueAsDefault();
		pgaParam.setValueAsDefault();
		stdDevTypeParam.setValueAsDefault();
		siteTypeParam.setValue(SITE_TYPE_DEFAULT);

		mag = ((Double) magParam.getValue()).doubleValue();
		rRup = ((Double) distanceRupParam.getValue()).doubleValue();
		tecRegType = tectonicRegionTypeParam.getValue().toString();
		siteType = siteTypeParam.getValue().toString();
	}

	/**
	 * This is a simple aproach to set the hypodepth value. This has to be
	 * further investigated, to be consistent with the approach applied in Zhao
	 * et al 2006. We are not allowed to use the common OpenSHA ParameterAPI
	 * approach;
	 */
	// public void setHypodepth(double value){
	// hypodepth = value;
	// }

	/**
	 * Creates the two Potential Earthquake parameters (magParam and
	 * fltTypeParam) and adds them to the eqkRuptureParams list. Makes the
	 * parameters non-editable.
	 */
	protected final void initEqkRuptureParams() {

		if (D) {
			System.out.println("--- initEqkRuptureParams");
		}

		// Magnitude parameter
		magParam = new MagParam(MAG_WARN_MIN, MAG_WARN_MAX);
		// Add parameters
		eqkRuptureParams.clear();
		eqkRuptureParams.addParameter(magParam);
		// eqkRuptureParams.addParameter(tectonicRegionTypeParam);

		if (D) {
			System.out.println("--- initEqkRuptureParams end");
		}
	}

	/**
	 * Creates the Site-Type parameter and adds it to the siteParams list.
	 *  Makes the parameters non-edit-able.
	 */
	protected final void initSiteParams() {
		//
		StringConstraint siteConstraint = new StringConstraint();

		siteConstraint.addString(SITE_TYPE_ROCK);
		siteConstraint.addString(SITE_TYPE_HARD_SOIL);
		siteConstraint.addString(SITE_TYPE_MEDIUM_SOIL);
		siteConstraint.addString(SITE_TYPE_SOFT_SOIL);
		siteConstraint.setNonEditable();
		//
		siteTypeParam = new StringParameter(SITE_TYPE_NAME, siteConstraint,
				null);
		siteTypeParam.setInfo(SITE_TYPE_INFO);
		siteTypeParam.setNonEditable();
		siteParams.clear();
		siteParams.addParameter(siteTypeParam);
	}

	/**
	 * Creates the Propagation Effect parameters and adds them to the
	 * propagationEffectParams list. Makes the parameters non-editable.
	 */
	protected final void initPropagationEffectParams() {
		distanceRupParam = new DistanceRupParameter(0.0);
		distanceRupParam.addParameterChangeWarningListener(warningListener);
		DoubleConstraint warn = new DoubleConstraint(DISTANCE_RUP_WARN_MIN,
				DISTANCE_RUP_WARN_MAX);
		warn.setNonEditable();
		distanceRupParam.setWarningConstraint(warn);

		distanceRupParam.setNonEditable();
		propagationEffectParams.addParameter(distanceRupParam);
	}

	/**
	 * Creates other Parameters that the mean or stdDev depends upon, such as
	 * the Component or StdDevType parameters.
	 */
	protected final void initOtherParams() {

		if (D) {
			System.out.println("--- initOtherParams");
		}

		// init other params defined in parent class
		super.initOtherParams();

		// The stdDevType Parameter
		StringConstraint stdDevTypeConstraint = new StringConstraint();
		stdDevTypeConstraint.addString(StdDevTypeParam.STD_DEV_TYPE_TOTAL);
		stdDevTypeConstraint.addString(StdDevTypeParam.STD_DEV_TYPE_NONE);
		stdDevTypeConstraint.addString(StdDevTypeParam.STD_DEV_TYPE_INTER);
		stdDevTypeConstraint.addString(StdDevTypeParam.STD_DEV_TYPE_INTRA);
		stdDevTypeConstraint.setNonEditable();
		stdDevTypeParam = new StdDevTypeParam(stdDevTypeConstraint);

		// The Component Parameter
		StringConstraint constraint = new StringConstraint();
		constraint.addString(ComponentParam.COMPONENT_RANDOM_HORZ);
		constraint.addString(ComponentParam.COMPONENT_AVE_HORZ);
		constraint.setNonEditable();
		componentParam = new ComponentParam(constraint,
				ComponentParam.COMPONENT_RANDOM_HORZ);
		componentParam = new ComponentParam(constraint,
				ComponentParam.COMPONENT_AVE_HORZ);
		// tecRegType
		StringConstraint tecRegConstr = new StringConstraint();
		tecRegConstr.addString(FLT_TEC_ENV_INTERFACE);
		tecRegConstr.addString(FLT_TEC_ENV_INSLAB);
		tectonicRegionTypeParam = new TectonicRegionTypeParam(tecRegConstr,
				FLT_TEC_ENV_INTERFACE);
		// tectonicRegionTypeParam = new
		// TectonicRegionTypeParam(tecRegConstr,FLT_TEC_ENV_INSLAB);

		// add these to the list
		otherParams.addParameter(stdDevTypeParam);
		otherParams.addParameter(componentParam);
		// otherParams.addParameter(tectonicRegionTypeParam);

		otherParams.replaceParameter(tectonicRegionTypeParam.NAME,
				tectonicRegionTypeParam);

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
		meanIndependentParams.addParameter(tectonicRegionTypeParam);
		meanIndependentParams.addParameter(distanceRupParam);
		meanIndependentParams.addParameter(siteTypeParam);

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
	 * Creates the two supported IM parameters (PGA and SA), as well as the
	 * independenParameters of SA (periodParam and dampingParam) and adds them
	 * to the supportedIMParams list. Makes the parameters non-editable.
	 */
	protected final void initSupportedIntensityMeasureParams() {
		// Create saParam:
		DoubleDiscreteConstraint periodConstraint = 
			new DoubleDiscreteConstraint();
		for (int i = 1; i < PERIOD.length; i++) {
			periodConstraint.addDouble(new Double(PERIOD[i]));
		}
		periodConstraint.setNonEditable();
		saPeriodParam = new PeriodParam(periodConstraint);
		saDampingParam = new DampingParam();
		saParam = new SA_Param(saPeriodParam, saDampingParam);
		saParam.setNonEditable();

		// Create PGA Parameter (pgaParam):
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
	 * Compute mean.
	 */
	public final double getMean(final int iper, double mag, final double rRup, final String siteType,
			final String tecRegType, final double hypoDep) {
		double hypodepth;
		double g = 0.0; // this is unity for
		double flag_Sc = 0.0; // This is unity for soil NEHRP C - Otherwise 0
		double flag_Sd = 0.0; // This is unity for soil NEHRP D - Otherwise 0
		double flag_Se = 0.0; // This is unity for soil NEHRP E - Otherwise 0
		// NEHRP B all soil coefficients equal zero
		double mean = 0.00;

		// set the depth for events with hdepth > 100km
		if (hypoDep > 100) {
			hypodepth = 100;
		} else {
			hypodepth = hypoDep;
		}

		double R = Double.NaN;
		double PGArx = 0.00;

		if (tecRegType.equals(FLT_TEC_ENV_INTERFACE)) {
			if (mag >= 8.5) {
				mag = 8.5;
			} else {
				this.mag = mag;
			}
			delta = 0.00724 * Math.pow(10, 0.507 * mag);
			R = Math.sqrt(rRup * rRup + delta * delta);
			g = Math.pow(10, 1.2 - 0.18 * mag);
			PGArx = Math.pow(10, INTER_C1[0] + INTER_C2[0] * mag + INTER_C3[0] * hypodepth
					+ INTER_C4[0] * R - g * Math.log10(R));
		} else if (tecRegType.equals(FLT_TEC_ENV_INSLAB)) {
			if (mag >= 8.0) {
				mag = 8.0;
			} else {
				this.mag = mag;
			}
			delta = 0.00724 * Math.pow(10, 0.507 * mag);
			R = Math.sqrt(rRup * rRup + delta * delta);
			g = Math.pow(10, 0.301 - 0.01 * mag);
			PGArx = Math.pow(10, INTRA_C1[0] + INTRA_C2[0] * mag + INTRA_C3[0] * hypodepth
					+ INTRA_C4[0] * R - g * Math.log10(R));
		} else {
			System.out.println("+++" + tecRegType.toString() + "--");
			throw new RuntimeException(
					"\n  Cannot handle this combination: \n  tectonic region");
		}

		System.out.println("PGArx: " + PGArx);

		if (im.getName().equals(PGA_Param.NAME) && FREQ[iper] >= 2) {
			if ((100 < PGArx) && (PGArx < 500)) {
				sl = 1.00 - (PGArx - 100) / 400;
			} else if (PGArx >= 500)
			 {
				sl = 0.00;
			// System.out.println("case0");
			}
		} else if (FREQ[iper] <= 1 || 100 < PGArx) {
			// if (100 < PGArx)
			sl = 1.00;
			// System.out.println("case1");
		} else if ((1 < FREQ[iper]) && (FREQ[iper] < 2)) {
			if ((100 < PGArx) && (PGArx < 500)) {
				sl = 1.00 - (FREQ[iper] - 1) * (PGArx - 100) / 400;
			} else if (PGArx >= 500)
			 {
				sl = 1.00 - (FREQ[iper] - 1);
			// System.out.println("case2");
			}
		} else {
			throw new RuntimeException(
					"\n  Unrecognized nonlinear soil effect \n");
		}

		// Site term correction
		if (D) {
			System.out.println("Site conditions: " + siteType);
		}
		if (siteType.equals(SITE_TYPE_ROCK)) {
			// Vs30 > 760
			if (D) {
				System.out.println("NEHRP B");
			}
			flag_Sc = 0.0;
			flag_Sd = 0.0;
			flag_Se = 0.0;
		} else if (siteType.equals(SITE_TYPE_HARD_SOIL)) {
			// 360 < Vs30 < 760
			if (D) {
				System.out.println("NEHRP C");
			}
			flag_Sc = 1.0;
			flag_Sd = 0.0;
			flag_Se = 0.0;
		} else if (siteType.equals(SITE_TYPE_MEDIUM_SOIL)) {
			// 180 < Vs30 < 360
			if (D) {
				System.out.println("NEHRP D");
			}
			flag_Sc = 0.0;
			flag_Sd = 1.0;
			flag_Se = 0.0;
		} else if (siteType.equals(SITE_TYPE_SOFT_SOIL)) {
			// Vs30 < 180
			if (D) {
				System.out.println("NEHRP E");
			}
			flag_Sc = 0.0;
			flag_Sd = 0.0;
			flag_Se = 1.0;
		} else {
			throw new RuntimeException("\n  Unrecognized site type \n");
		}

		double logY = 0.00;
		// compute the mean
		if (tecRegType.equals(FLT_TEC_ENV_INTERFACE)) {
			// add soil nonlinearity effect

			logY = INTER_C1[iper] + INTER_C2[iper] * mag + INTER_C3[iper] * hypodepth + INTER_C4[iper]
					* R - g * Math.log10(R) + flag_Sc * INTER_C5[iper] * sl + flag_Sd
					* INTER_C6[iper] * sl + flag_Se * INTER_C7[iper] * sl;
			// correction from erratum AB2008
			double logY02 = INTER_C1[3] + INTER_C2[3] * mag + INTER_C3[3] * hypodepth + INTER_C4[3] * R
					- g * Math.log10(R) + flag_Sc * INTER_C5[3] * sl + flag_Sd
					* INTER_C6[3] * sl + flag_Se * INTER_C7[3] * sl;
			double logY04 = INTER_C1[4] + INTER_C2[4] * mag + INTER_C3[4] * hypodepth + INTER_C4[4] * R
					- g * Math.log10(R) + flag_Sc * INTER_C5[4] * sl + flag_Sd
					* INTER_C6[4] * sl + flag_Se * INTER_C7[4] * sl;
			if (PERIOD[iper] == 0.2) {
				// add correction
				logY = 0.333 * logY02 + 0.667 * logY04;
			} else if (PERIOD[iper] == 0.4) {
				// add correction
				logY = 0.333 * logY04 + 0.667 * logY02;
			}
			logY *= LOG_2_LN;
		} else if (tecRegType.equals(FLT_TEC_ENV_INSLAB)) {
			logY = INTRA_C1[iper] + INTRA_C2[iper] * mag + INTRA_C3[iper] * hypodepth
					+ INTRA_C4[iper] * R - g * Math.log10(R) + flag_Sc * INTRA_C5[iper]
					* sl + flag_Sd * INTRA_C6[iper] * sl + flag_Se * INTRA_C7[iper] * sl;
			logY *= LOG_2_LN;
		}
		return Math.log(Math.exp(logY) / 981);
	}

	/**
	 * This gets the standard deviation for specific parameter settings. We
	 * might want another version that takes the actual SA period rather than
	 * the period index.
	 * 
	 * @param iper
	 * @param rRup
	 * @param mag
	 * @param stdDevType
	 * @return
	 */
	public final double getStdDev(final int iper, final String stdDevType, final String tecRegType) {

		if (tecRegType.equals(FLT_TEC_ENV_INTERFACE)) {
			if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_TOTAL)) {
				return LOG_2_LN * INTER_TOTAL_STD[iper];
			} else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_NONE)) {
				return 0;
			} else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_INTER)) {
				return LOG_2_LN * INTER_INTEREVENT_STD[iper];
			} else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_INTRA)) {
				return LOG_2_LN * INTER_INTRAEVENT_STD[iper];
			} else {
				return Double.NaN;
			}
		} else {
			if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_TOTAL)) {
				return LOG_2_LN * INTRA_TOTAL_STD[iper];
			} else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_NONE)) {
				return 0;
			} else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_INTER)) {
				return LOG_2_LN * INTRA_INTRAEVENT_STD[iper];
			} else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_INTRA)) {
				return LOG_2_LN * INTRA_INTEREVENT_STD[iper];
			} else {
				return Double.NaN;
			}
		}
	}

	/**
	 * This listens for parameter changes and updates the primitive parameters
	 * accordingly
	 * 
	 * @param e
	 *            ParameterChangeEvent
	 */
	public final void parameterChange(final ParameterChangeEvent e) {

		String pName = e.getParameterName();
		Object val = e.getNewValue();

		if (D) {
			System.out.println("Changed param: " + pName);
		}

		if (pName.equals(DistanceRupParameter.NAME)) {
			rRup = ((Double) val).doubleValue();
		} else if (pName.equals(MagParam.NAME)) {
			mag = ((Double) val).doubleValue();
		} else if (pName.equals(StdDevTypeParam.NAME)) {
			stdDevType = (String) val;
		} else if (pName.equals(TectonicRegionTypeParam.NAME)) {
			tecRegType = tectonicRegionTypeParam.getValue().toString();
			if (D) {
				System.out.println("tecRegType new value:" + tecRegType);
			}
		} else if (pName.equals(SITE_TYPE_NAME)) {
			siteType = this.getParameter(this.SITE_TYPE_NAME).getValue()
					.toString();
		} else if (pName.equals(PeriodParam.NAME)) {
			intensityMeasureChanged = true;
		}
	}

	/**
	 * Allows to reset the change listeners on the parameters
	 */
	public final void resetParameterEventListeners() {
		magParam.removeParameterChangeListener(this);
		tectonicRegionTypeParam.removeParameterChangeListener(this);
		siteTypeParam.removeParameterChangeListener(this);
		distanceRupParam.removeParameterChangeListener(this);
		stdDevTypeParam.removeParameterChangeListener(this);
		saPeriodParam.removeParameterChangeListener(this);
		this.initParameterEventListeners();
	}

	/**
	 * Adds the parameter change listeners. This allows to listen to when-ever
	 * the parameter is changed.
	 */
	protected final void initParameterEventListeners() {
		if (D) {
			System.out.println("--- initParameterEventListeners begin");
		}

		magParam.addParameterChangeListener(this);
		tectonicRegionTypeParam.addParameterChangeListener(this);
		siteTypeParam.addParameterChangeListener(this);
		distanceRupParam.addParameterChangeListener(this);
		stdDevTypeParam.addParameterChangeListener(this);
		saPeriodParam.addParameterChangeListener(this);

		if (D) {
			System.out.println("--- initParameterEventListeners end");
		}
	}

	/**
	 * This provides a URL where more info on this model can be obtained
	 * 
	 * @throws MalformedURLException
	 *             if returned URL is not a valid URL.
	 * @returns the URL to the AttenuationRelationship document on the Web.
	 */
	public final URL getInfoURL() throws MalformedURLException {
		return new URL(
				"http://www.opensha.org/documentation/modelsImplemented/attenRel/AB_2003.html");
	}

}
