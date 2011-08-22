package org.opensha.sha.imr.attenRelImpl;


public class FaccioliEtAl_2010Constants {

		/**
		 * Supported period values (s).
		 */
	public static final double[] PERIOD =
          {0.00, 0.05, 0.10, 0.20, 0.30, 0.40, 0.50, 0.60, 0.70, 0.80,
		   0.90, 1.00, 1.25, 1.50, 2.00, 2.50, 3.00, 4.00, 5.00, 7.50, 
		   10.00, 15.00, 20.00};
		/**
		 * a1 coefficients.
		 */
	public static final double[] a1 = 
	       {-1.18, -2.96, -2.02,-1.97,-2.11,-2.23,-2.35,-2.46, -2.50,
		    -2.57, -2.63, -2.68,-2.84,-2.95,-3.09,-3.14,-3.20, -3.49,
		    -3.71, -4.15, -4.28,-4.17,-4.02};
	/**
	 * a2 coefficients.
	 */
	public static final double[] a2 = 
	       {0.559,0.604,0.559,0.527,0.570,0.596,0.613,0.641,0.664,0.693,
		    0.717,0.731,0.767,0.801,0.870,0.904,0.933,1.014,1.069,1.097,
		    1.068,1.021,0.993};
	/**
	 * a3 coefficients.
	 */
	public static final double[] a3 = 
	      { -1.624,-1.878,-1.837,-1.512,-1.421,-1.355,-1.295,-1.282,-1.293,-1.313,
		    -1.334,-1.335,-1.320,-1.342,-1.424,-1.454,-1.470,-1.496,-1.497,-1.320,
		    -1.187,-1.143,-1.167};
	/**
	 * a4 coefficients.
	 */
	public static final double[] a4 = 
	      { 0.018,0.052,0.070,0.031,0.009,0.005,0.001,0.001,0.001,
		    0.002,0.003,0.002,0.001,0.002,0.018,0.078,0.262,0.387,0.527,
		    0.455,0.210,0.089,0.065};
	/**
	 * a5 coefficients.
	 */
	public static final double[] a5 = 
	      { 0.445, 0.396, 0.373, 0.391, 0.459, 0.478, 0.556, 0.566, 0.580,
		    0.536, 0.526, 0.529, 0.581, 0.529, 0.423, 0.342, 0.272, 0.268, 0.260, 
		    0.266, 0.298, 0.334, 0.343};
	/**
	 * aB coefficients.
	 */
	public static final double[] aB = 
	       {0.25, 0.20, 0.26, 0.30, 0.23, 0.19, 0.20, 0.19, 0.17,
		    0.17, 0.17, 0.17, 0.16, 0.15, 0.12, 0.11, 0.11, 0.11, 0.10,
		    0.09, 0.08, 0.09, 0.11};
	/**
	 * aC coefficients.
	 */
	public static final double[] aC = 
	       {0.31, 0.21, 0.24, 0.42, 0.42, 0.42, 0.42, 0.42, 0.42, 
		    0.41, 0.42, 0.42, 0.40, 0.39, 0.34, 0.31, 0.29, 0.27, 0.24, 
		    0.22, 0.20, 0.19, 0.21};
	/**
	 * aD coefficients.
	 */
	public static final double[] aD = 
	       {0.33, 0.18, 0.19, 0.40, 0.45, 0.53, 0.62, 0.68, 0.70,
		    0.72, 0.73, 0.72, 0.67, 0.63, 0.55, 0.50, 0.49, 0.44, 0.39,
		    0.34, 0.32, 0.32, 0.33};

	/**
	 * aN coefficients.
	 */
	public static final double[] aN = 
	       {-0.01,-0.02, 0.01, 0.04, 0.02, 0.04, 0.05, 0.06, 0.07,
		    0.06, 0.07, 0.08, 0.09, 0.09, 0.04, 0.02, 0.02, 0.01, 0.01,
		    0.04, 0.05, 0.07, 0.08 };
	/**
	 * aR coefficients.
	 */
	public static final double[] aR = 
	      {0.09, 0.08, 0.08, 0.05, 0.03, 0.01, 0.00, -0.01,-0.02,
		  -0.03,-0.04,-0.04,-0.05,-0.05,-0.04,-0.03,-0.02, -0.03,-0.05,
		  -0.09,-0.11,-0.11,-0.11};
	/**
	 * aS coefficients.
	 */
	public static final double[] aS = 
	      {-0.05, -0.03, -0.05, -0.05, -0.03, -0.02, -0.02, -0.02, -0.02,
		  -0.01, -0.01, -0.01, -0.01, -0.01,  0.01,  0.01,  0.00,  0.01,  0.02,
		   0.04,  0.04,  0.04,  0.03};
	/**
	 * Total standard deviation to be used when the focal mechanism is specified
	 */	
	public static final double[] TOTAL_STD = 
	       {0.36, 0.38, 0.40, 0.40, 0.40, 0.41, 0.41, 0.41, 0.41,
		    0.40, 0.40, 0.40, 0.40, 0.40, 0.40, 0.39, 0.38, 0.37, 0.36,
		    0.33, 0.31, 0.29, 0.30 };
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
		 * Minimum hypocenter distance.
		 */
		public static final Double DISTANCE_RUP_WARN_MIN = new Double(15.0);
		/**
		 * Maximum hypocenter distance.
		 */
		public static final Double DISTANCE_RUP_WARN_MAX = new Double(150.0);
		/**
		 * Minimum hypocenter depth
		 */
		public static final Double DEPTH_HYPO_WARN_MIN = new Double(10.0);
		/**
		 * Maximum hypocenter depth
		 */
		public static final Double DEPTH_HYPO_WARN_MAX = new Double(60.0);

		/**
		 * SOIL CLASS ROCK Vs30 upper bound
		 */
		public static final double SOIL_TYPE_ROCK_UPPER_BOUND = 800.0;
		/**
		 * SOIL CLASS STIFF SOIL Vs30 upper bound
		 */
		public static final double SITE_TYPE_STIFF_SOIL_UPPER_BOUND = 360.0;
		/**
		 * SOIL CLASS SOFT Vs30 upper bound
		 */
		public static final double SITE_TYPE_SOFT_UPPER_BOUND  = 180;
		/**
		 * NORMAL - STYLE of FAULTING (Definition based on rake angle minimum value)  
		 */	
		public static final double FLT_TYPE_NORMAL_RAKE_LOWER =  -150.00;
		/**
		 * NORMAL - STYLE of FAULTING (Definition based on rake angle maximum value)  
		 */	
		public static final double FLT_TYPE_NORMAL_RAKE_UPPER =  -30.00;
		/**
		 * REVERSE - STYLE of FAULTING (Definition based on rake angle minimum value)  
		 */	
		public static final double FLT_TYPE_REVERSE_RAKE_LOWER =  30.00;
		/**
		 * REVERSE - STYLE of FAULTING (Definition based on rake angle maximum value)  
		 */	
		public static final double FLT_TYPE_REVERSE_RAKE_UPPER =  150.00;
		/**
		 * cm/s^2 to g conversion factor.
		 */
		public static final double CMS2_TO_G_CONVERSION_FACTOR = 1.0/981.0;
		/**
		 * m/s^2 to g conversion factor.
		 */
		public static final double MS2_TO_G_CONVERSION_FACTOR = 1.0/9.810;

	}