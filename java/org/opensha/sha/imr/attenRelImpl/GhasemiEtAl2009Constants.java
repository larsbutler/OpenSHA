package org.opensha.sha.imr.attenRelImpl;


public class GhasemiEtAl2009Constants {

	/**
	 * Supported period values (s).
	 */
	public static final double[] PERIOD = 
		{0.000	,0.060	,0.070	,0.080	,0.090	,0.100	,0.200	,0.300	,0.400	,0.500	,
		 0.600  ,0.700	,0.800	,0.900	,1.000	,2.000	,3.000};
	
	/**
	 * a1 coefficients.
	 */
	public static final double[] a1 = 
		{0.868	,0.906	,0.957	,0.700	,0.966	,0.904	,0.786	,0.432	,0.246	,0.003,
		-0.118	,-0.234	,-0.331	,-0.459	,-0.567	,-1.209	,-1.436};

	/**
	 * a2 coefficients.
	 */
	public static final double[] a2 = 
		{0.405 ,0.398	,0.394	,0.387	,0.384	,0.380	,0.425	,0.474	,0.528	,0.571,
		 0.608 ,0.635	,0.673	,0.706	,0.727	,0.876	,0.920};

	/**
	 * a3 coefficients.
	 */
	public static final double[] a3 = 
         {-1.424 ,-1.440 ,-1.449 ,-1.427 ,-1.413 ,-1.396 ,-1.215 ,-1.134 ,-1.080 ,-1.069,
		  -1.053 ,-1.034 ,-1.083 ,-1.092 ,-1.071 ,-1.104 ,-1.151};
	
	/**
	 * a4 coefficients.
	 */
	public static final double[] a4 = 
           {0.014	,0.015	,0.015	,0.015	,0.016	,0.016	,0.015	,0.014	,0.011	,0.010	,0.010,
		    0.009	,0.010	,0.011	,0.011	,0.011	,0.012};
		
	/**
	 * a5 coefficients.
	 */
	public static final double[] a5 = 
           {0.420	,0.420	,0.420	,0.420	,0.420	,0.420	,0.420	,0.420	,0.420	,0.420,
		    0.420	,0.420	,0.420	,0.420	,0.420	,0.420	,0.420};
		
	/**
	 * a6 coefficients.
	 */
	public static final double[] a6 = 
		   {0.859	,0.944	, 0.978	, 1.282	, 1.046	, 1.136	, 0.663, 0.477, 0.135, 0.002,
		   -0.209	,-0.361	,-0.450	,-0.570	,-0.678	,-1.291	,-1.515};
		
	/**
	 * a7 coefficients.
	 */
	public static final double[] a7 = 
           {0.836	, 0.911	, 0.937	, 1.238	, 1.005	, 1.096	, 0.748	,0.605	,0.289	,0.173,
		   -0.037	,-0.194	,-0.300	,-0.424	,-0.533	,-1.183	,-1.411};
	
	/**
	 * Total standard deviation.
	 */	
	public static final double[] TOTAL_STD = 
           {0.319, 0.322, 0.325, 0.325, 0.326, 0.331, 0.319, 0.318, 0.327, 0.333, 
		    0.337, 0.347, 0.336, 0.335, 0.336, 0.363, 0.370};

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
	public static final Double MAG_WARN_MAX = new Double(7.6);
	/**
	 * Minimum rupture distance.
	 */
	public static final Double DISTANCE_Rup_WARN_MIN = new Double(0.0);
	/**
	 * Maximum rupture distance.
	 */
	public static final Double DISTANCE_Rup_WARN_MAX = new Double(100.0);
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
	/**
	 * cm/s to g conversion factor.
	 */
	public static final double CMS2_TO_G_CONVERSION_FACTOR = 1.0/981.0;
}
