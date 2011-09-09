package org.opensha.sha.imr.attenRelImpl;

import org.opensha.sha.imr.param.OtherParams.ComponentParam;


public class KG_2004Constants {
	// Coefficients
	// there is no PGV estimation with this GMPE	
	/**
	 * Supported period values (s).
	 */
	public static final double[] PERIOD = {
		    0.00,0.10,0.11,0.12,0.13,0.14,0.15,0.16,0.17,0.18,
			0.19,0.20,0.22,0.24,0.26,0.28,0.30,0.32,0.34,0.36,
			0.38,0.40,0.42,0.44,0.46,0.48,0.50,0.55,0.60,0.65,
			0.70,0.75,0.80,0.85,0.90,0.95,1.00,1.10,1.20,1.30,
			1.40,1.50,1.60,1.70,1.80,1.90,2.00};

	/**
	 * b1 coefficients.
	 */
	public static final double[] b1 =  { 
		    0.39, 1.80, 1.63, 1.11, 1.47, 0.99, 1.53, 1.47, 1.50, 1.50,
			1.47, 1.42, 0.99, 0.74, 0.60, 0.73, 0.80, 0.75, 0.80, 0.59,
			0.49, 0.53, 0.35, 0.05, 0.05,-0.17,-0.15,-0.31,-0.38,-0.49,
			-0.58,-0.65,-0.71,-0.57,-0.52,-0.61,-0.66,-1.33,-1.37,-1.47,
			-1.67,-1.79,-1.89,-1.97,-2.04,-1.97,-2.11};
	/**
	 * b1 coefficients.
	 */
	public static final double[] b2 = {
		    0.58,0.44,0.50,0.72,0.50,0.51,0.51,0.52,0.53,0.55,
			0.58,0.60,0.63,0.65,0.70,0.73,0.75,0.74,0.74,0.75,
			0.76,0.78,0.78,0.78,0.78,0.80,0.83,0.87,0.88,0.90,
			0.91,0.93,0.97,0.79,1.02,1.05,1.07,1.09,1.12,1.16,
			1.17,1.18,1.19,1.20,1.21,1.21,1.20};

	/**
	 * b1 coefficients.
	 */
	public static final double[] b3 = {
		    -0.11,-0.09,-0.09,-0.23,-0.13,-0.11,-0.13,-0.13,-0.12,-0.12,
			-0.11,-0.10,-0.12,-0.11,-0.11,-0.13,-0.15,-0.16,-0.15,-0.14,
			-0.14,-0.15,-0.15,-0.13,-0.16,-0.15,-0.16,-0.16,-0.18,-0.18,
			-0.19,-0.19,-0.18,-0.21,-0.23,-0.23,-0.25,-0.26,-0.27,-0.27,
			-0.26,-0.26,-0.27,-0.27,-0.28,-0.30,-0.30};

	/**
	 * b1 coefficients.
	 */
	public static final double[] b5 = {
		    -0.90,-1.02,-1.03,-0.94,-1.07,-1.03,-1.07,-1.05,-1.06,-1.06,
			-1.06,-1.05,-0.95,-0.89,-0.86,-0.89,-0.91,-0.90,-0.89,-0.87,
			-0.85,-0.86,-0.82,-0.76,-0.75,-0.70,-0.71,-0.70,-0.70,-0.70,
			-0.68,-0.68,-0.68,-0.70,-0.71,-0.70,-0.70,-0.68,-0.69,-0.70,
			-0.67,-0.67,-0.66,-0.66,-0.67,-0.68,-0.66};

	/**
	 * b1 coefficients.
	 */
	public static final double[] bv = {
		    -0.20,-0.05,-0.05,-0.22,-0.30,-0.50,-0.30,-0.30,-0.30,-0.30,
			-0.30,-0.30,-0.30,-0.30,-0.31,-0.30,-0.30,-0.30,-0.27,-0.30,
			-0.30,-0.26,-0.27,-0.27,-0.29,-0.28,-0.27,-0.29,-0.30,-0.30,
			-0.30,-0.30,-0.30,-0.33,-0.31,-0.30,-0.31,-0.50,-0.50,-0.50,
			-0.50,-0.50,-0.50,-0.50,-0.51,-0.50,-0.50};

	/**
	 * b1 coefficients.
	 */
	public static final double[] Va = {
		    1112.00,1112.00,1290.00,1452.00,1953.00,1717.00,1953.00,1954.00,1955.00,1957.00,
			1958.00,1959.00,1959.00,1960.00,1961.00,1963.00,1964.00,1954.00,1968.00,2100.00,
			2103.00,2104.00,2104.00,2103.00,2059.00,2060.00,2064.00,2071.00,2075.00,2100.00,
			2102.00,2104.00,2090.00,1432.00,1431.00,1431.00,1405.00,2103.00,2103.00,2103.00,
			2104.00,2104.00,2102.00,2101.00,2098.00,1713.00,1794.00};

	/**
	 * b1 coefficients.
	 */
	public static final double[] h = {
		    6.91,10.07,10.31,6.91,10.00,9.00,10.00,9.59,9.65,9.40,
			9.23,8.96,6.04,5.16,4.70,5.74,6.49,7.18,8.10,7.90,
			8.00,8.32,7.69,7.00,7.30,6.32,6.22,5.81,6.13,5.80,
			5.70,5.90,5.89,6.27,6.69,6.89,6.89,7.00,6.64,6.00,
			5.44,5.57,5.50,5.30,5.10,5.00,4.86};

	/**
	 * Total standard deviation.
	 */	
	public static final double[] sig = {
		    0.61,0.66,0.64,0.65,0.67,0.62,0.62,0.63,0.65,0.65,
			0.66,0.67,0.68,0.68,0.68,0.67,0.72,0.71,0.72,0.65,
			0.78,0.77,0.81,0.79,0.78,0.79,0.76,0.81,0.83,0.85,
			0.84,0.83,0.84,0.83,0.83,0.84,0.87,0.85,0.84,0.86,
			0.85,0.84,0.83,0.83,0.85,0.86,0.88};

	
	/**
	 * log10 to natural log conversion factor.
	 */
	public static final double LOG10_2_LN = Math.log(10.0);
	/**
	 * Minimum magnitude.
	 */
	protected final static Double MAG_WARN_MIN = new Double(4);
	/**
	 * Maximum magnitude.
	 */
	protected final static Double MAG_WARN_MAX = new Double(7.5);
	/**
	 * Minimum rupture distance.
	 */
	protected final static Double DISTANCE_JB_WARN_MIN = new Double(0.0);
	/**
	 * Maximum rupture distance.
	 */
	protected final static Double DISTANCE_JB_WARN_MAX = new Double(250.0);

	/**
	 * SOIL CLASS ROCK Vs30 upper bound
	 */
	public static final double SOIL_TYPE_ROCK_UPPER_BOUND = 760.0;
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

	protected final static Double VS30_WARN_MIN = new Double(200.0);
	protected final static Double VS30_WARN_MAX = new Double(1500.0);
	
	/**
	 * cm/s to g conversion factor.
	 */
	public static final double CMS2_TO_G_CONVERSION_FACTOR = 1.0/981.0;
}
