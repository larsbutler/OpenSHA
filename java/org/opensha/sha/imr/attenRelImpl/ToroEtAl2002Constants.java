package org.opensha.sha.imr.attenRelImpl;

import org.opensha.commons.exceptions.InvalidRangeException;
import org.opensha.commons.param.StringParameter;
import org.opensha.commons.util.FaultUtils;
import org.opensha.sha.earthquake.EqkRupture;

public class ToroEtAl2002Constants {
	/**
	 * Supported period values (s).
	 */
	public static final double[] PERIOD = 
	   { 0.0000,  0.0290,  0.04000, 0.10000, 0.20000, 0.40000, 1.00000, 2.00000};
	public static final double[] FREQ = {Double.POSITIVE_INFINITY,
		35.0000, 25.0000, 10.00000, 5.00000, 2.50000, 1.00000, 0.50000, 0.00000};
	/**
	 * c1 coefficients.
	 */
	public static final double[] c1  = {2.20, 4.00, 3.68, 2.37, 1.73, 1.07, 0.09, -0.74};	
	/**
	 * c2 coefficients.
	 */
	public static final double[] c2  = { 0.81, 0.79, 0.8, 0.81, 0.84, 1.05, 1.42, 1.86};
	/**
	 * c3 coefficients.
	 */
	public static final double[] c3  = { 0.00, 0.00, 0.00, 0.00, 0.00, -0.10, -0.20, -0.31};
	/**
	 * c4 coefficients.
	 */
	public static final double[] c4  = { 1.27, 1.57, 1.46, 1.10, 0.98, 0.93, 0.90, 0.92};
	/**
	 * c5 coefficients.
	 */
	public static final double[] c5  = { 1.16, 1.83, 1.77, 1.02, 0.66, 0.56, 0.49, 0.46};
	/**
	 * c6 coefficients.
	 */
	public static final double[] c6  = { 0.0021, 0.0008, 0.0013, 0.004, 0.0042, 0.0033, 0.0023, 0.0017};
	/**
	 * c7 coefficients.
	 */
	public static final double[] c7  = {9.30, 11.10, 10.50, 8.30, 7.50, 7.10, 6.80, 6.90};
	/**
	 * m50 coefficients.
	 */
	public static final double[] m50= { 0.55, 0.62, 0.62, 0.59, 0.60, 0.63, 0.63, 0.61};
	/**
	 * m55 coefficients.
	 */
	public static final double[] m55= { 0.59, 0.63, 0.63, 0.61, 0.64, 0.68, 0.64, 0.62};
	/**
	 * m80 coefficients.
	 */
	public static final double[] m80= { 0.50, 0.50, 0.50, 0.50, 0.56, 0.64, 0.67, 0.66}; 
	/**
	 * r05 coefficients.
	 */
	public static final double[] r05= { 0.54, 0.62, 0.57, 0.50, 0.45, 0.45, 0.45, 0.45};
	/**
	 * r20 coefficients.
	 */
	public static final double[] r20= { 0.2, 0.35, 0.29, 0.17, 0.12, 0.12, 0.12, 0.12};		
	/**
	 * Rock adjustment coefficients
	 * coefficients to correct the hard-rock soil(vs30>=2800m/sec) to rock (vs30>=800m/sec)
	 * obtained from interpolation of the coefficients presented in table 9 Drouet et al (2010) 
	 */
	public static final	double []  AFrock = 
	   {0.735106, 0.419632, 0.477379, 0.888509, 1.197291, 1.308267, 1.265762, 1.215779};
	/**
	 * Style-of-faulting adjustment coefficients, 
	 * obtained from Table 7 (Drouet et al 2010) and using a cubic spline interpolation 
	 */
	public static final	double []  Frss = 
	   {1.220000, 0.935198, 0.907936, 1.080000, 1.190000, 1.230000, 1.177500, 1.140000};

	public static final	double Fnss = 0.95; 
	public static final	double pN = 0.01;
	public static final	double pR = 0.81;

	/**
	 * Adjustment factors of the total standard deviation when hard rock (vs30 =2800m/s) to 
	 * refference rock (vs30 =800m/s). Interpolated from Table 9 (Drouet et al 2010)
	 * Note: the adjustment factors for periods of 3 and 4 sec are identical with those for
	 * 2sec - SHARE experts decision  
	 */	
	public static final	double []  sig_AFrock = 
	    {0.338916, 0.289785, 0.320650, 0.352442, 0.281552, 0.198424, 0.154327, 0.155520};
	/**
	 * log10 to natural log conversion factor.
	 */
	public static final double LOG10_2_LN = Math.log(10.0);
	/**
	 * Minimum magnitude.
	 */
	public static final Double MAG_WARN_MIN = new Double(5);
	/**
	 * Maximum magnitude.
	 */
	public static final Double MAG_WARN_MAX = new Double(8.0);
	/**
	 * Minimum rupture distance.
	 */
	public static final Double DISTANCE_JB_WARN_MIN = new Double(1.0);
	/**
	 * Maximum rupture distance.
	 */
	public static final Double DISTANCE_JB_WARN_MAX = new Double(1000.0);
	/**
	 * Distance factor R1 (rupture distance)
	 */
	public static final double R1 = 70.0; 
	/**
	 * Distance factor R2 (rupture distance)
	 */
	public static final double R2 = 130.0; 
	/**
	 * SOIL CLASS HARD ROCK Vs30 upper bound
	 */
	public static final double SOIL_TYPE_HARD_ROCK_UPPER_BOUND = 2800.0;
	/**
	 * SOIL CLASS ROCK SOIL Vs30 upper bound
	 */
	public static final double SITE_TYPE_ROCK_UPPER_BOUND = 800.0;
	/**
	 * NORMAL - STYLE of FAULTING (Definition based on rake angle minimum value)  
	 */	
	public static final double FLT_TYPE_NORMAL_RAKE_LOWER =  -120.00;
	/**
	 * NORMAL - STYLE of FAULTING (Definition based on rake angle maximum value)  
	 */	
	public static final double FLT_TYPE_NORMAL_RAKE_UPPER =  -60.00;
	/**
	 * NORMAL - STYLE of FAULTING (Definition based on rake angle minimum value)  
	 */	
	public static final double FLT_TYPE_REVERSE_RAKE_LOWER =  30.00;
	/**
	 * NORMAL - STYLE of FAULTING (Definition based on rake angle maximum value)  
	 */	
	public static final double FLT_TYPE_REVERSE_RAKE_UPPER =  150.00;
	/**
	 * cm/s to g conversion factor.
	 */
	public static final double CMS_TO_G_CONVERSION_FACTOR = 1.0/981.0;
}
