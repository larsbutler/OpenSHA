package org.opensha.sha.imr.attenRelImpl;


public class FukushimaEtAl2009Constants {

	/**
	 * Supported period values (s).
	 */
	public static final double[] PERIOD = 
		{0.0000, 0.10000, 0.15000, 0.20000, 0.30000, 0.40000, 0.50000, 0.75000, 1.00000, 1.50000, 2.00000};

	public static final double[] FREQ = {Double.POSITIVE_INFINITY,
		34.0000, 10.00000, 6.66700, 5.00000, 3.33300, 2.50000, 2.00000, 1.33300, 1.00000, 0.66700, 0.50000};
	
	/**
	 * a (magnitude) coefficients.
	 */
	public static final double[] a = 
		{0.307	, 0.256	, 0.247	,0.272	, 0.332	, 0.369	,0.410	, 0.444	, 0.485	, 0.535, 0.537};

	/**
	 * b (distance) coefficients.
	 */
	public static final double[] b = 
		{-0.001170, -0.00216, -0.00193, -0.00150, -0.00132, -0.00105, -0.000763, -0.000731, -0.000139, -0.000516, -0.000604};
	
	/**
	 * c soil coefficients 
	 * c1 for rock
	 * c2 for soil
	 */
	public static final double[] c1 = 
         {1.640, 2.325, 2.433, 2.270, 1.780, 1.438, 1.073, 0.671, 0.212, -0.282, -0.435};
	
	public static final double[] c2 = 
           {1.734, 2.338, 2.502, 2.349, 1.906, 1.613, 1.275, 0.893, 0.464, -0.072, -0.268};
		
	/**
	 * d (saturation) coefficients.
	 */
	public static final double[] d = 
           {0.0130, 0.0174, 0.0193, 0.0197, 0.0175, 0.0151, 0.0135, 0.0106, 0.00860, 0.00960, 0.00667};
		
	/**
	 * e coefficients.
	 */
	public static final double[] e = 
		   {0.420, 0.420, 0.420, 0.420, 0.420, 0.420, 0.420, 0.420, 0.420, 0.420, 0.420};
	
	/**
	 * Total standard deviation.
	 */	
	public static final double[] TOTAL_STD = 
           {0.261, 0.280, 0.293, 0.290, 0.301, 0.300, 0.308, 0.324, 0.328, 0.341, 0.345};

	/**
	 * log10 to natural log conversion factor.
	 */
	public static final double LOG10_2_LN = Math.log(10.0);
	/**
	 * Minimum magnitude.
	 */
	public static final Double MAG_WARN_MIN = new Double(5.5);
	/**
	 * Maximum magnitude.
	 */
	public static final Double MAG_WARN_MAX = new Double(7.4);
	/**
	 * Minimum rupture distance.
	 */
	public static final Double DISTANCE_Rup_WARN_MIN = new Double(0.5);
	/**
	 * Maximum rupture distance.
	 */
	public static final Double DISTANCE_Rup_WARN_MAX = new Double(235.0);
	/**
	 * SOIL CLASS ROCK Vs30 upper bound
	 */
	public static final double SOIL_TYPE_ROCK_UPPER_BOUND = 760.0;
	/**
	 * cm/s to g conversion factor.
	 */
	public static final double CMS2_TO_G_CONVERSION_FACTOR = 1.0/981.0;
}
