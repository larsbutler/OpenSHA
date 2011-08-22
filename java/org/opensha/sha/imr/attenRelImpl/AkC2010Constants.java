package org.opensha.sha.imr.attenRelImpl;


public class AkC2010Constants {

	/**
	 * Supported period values (s).
	 */
	public static final double[] PERIOD = {-1.00, 0.000,0.030,0.050,0.075,0.100,0.150,0.200,0.250,0.300,
		                                   0.400, 0.500,0.750,1.000,1.500,2.000};
	/**
	 * a1 coefficients.
	 */
	public static final double[]     a1 = {5.6093, 8.9242,8.8598,9.0526,9.5667,9.8561,10.437,10.635,10.126,10.127,
		                                   9.4786, 8.9515,8.105,7.6174,7.2043,6.7085};
	/**
	 * a2 coefficients.
	 */
	
	public static final double[]     a2 = {-0.513,-0.513,-0.513,-0.513,-0.513,-0.513,-0.513,-0.513,-0.513,-0.513,
		                                   -0.513,-0.513,-0.513,-0.513,-0.513,-0.513};
	/**
	 * a3 coefficients.
	 */
	
	public static final double[]     a3 = {-0.695,-0.695,-0.695,-0.695,-0.695,-0.695,-0.695,-0.695,-0.695,-0.695,
		                                   -0.695,-0.695,-0.695,-0.695,-0.695,-0.695};
	/**
	 * a4 coefficients.
	 */
	
	public static final double[]     a4 = {-0.25800,-0.18555,-0.17123,-0.15516,-0.13840,-0.11563,-0.17897,-0.21034,-0.25565,-0.2702,
		                                   -0.30498,-0.29877,-0.33490,-0.35366,-0.39858,-0.39528};
	/**
	 * a5 coefficients.
	 */
	
	public static final double[]     a5 = {-0.90393, -1.2559,-1.2513,-1.288,-1.3882,-1.4385,-1.4679,-1.4463,-1.2739,-1.269,
		                                   -1.0979,-1.017,-0.84365,-0.7584,-0.70134,-0.70766};
	/**
	 * a6 coefficients.
	 */
	
	public static final double[]     a6 = {0.21576,0.18105,0.18421,0.1984,0.20246,0.21833,0.15588,0.1159,0.09426,0.08352,
		                                   0.06082,0.09099,0.08647,0.09623,0.11219,0.12032};
	/**
	 * a7 coefficients.
	 */
	
	public static final double[]     a7 = {5.5747,7.3362,7.4697,7.2655,8.0365,8.842,9.3951,9.6087,7.5435,8.0314,
		                                   6.2404,5.6794,4.9384,4.1259,3.4653,3.8822};
	/**
	 * a8 coefficients.
	 */
	
	public static final double[]     a8 = {-0.10481,-0.02125,-0.0134,0.02076,0.07311,0.11044,0.03555,-0.03536,-0.10685,-0.10685,
		                                   -0.11197,-0.10118,-0.0456,-0.01936,-0.02618,-0.03215};
	/**
	 * a9 coefficients.
	 */
	public static final double[]     a9 = {0.07791,0.01851,0.03512,0.01484,0.02492,-0.0062,0.19751,0.18594,0.13574,0.13574,
		                                   0.16555,0.23546,0.10993,0.19729,0.21977,0.20584};

	/** Intra-event standard deviation.
	 */

	 public static final double[] INTRA_EVENT_STD = {0.6154, 0.65247, 0.6484, 0.622, 0.6849, 0.7001, 0.6958, 0.6963, 0.7060, 0.6718, 
		                                             0.6699, 0.6455, 0.6463, 0.6485, 0.6300, 0.6243};
	/**
	 * Intraslab inter event standard deviation.
	 */

	 public static final double[] INTER_EVENT_STD = {0.5260, 0.5163, 0.5148, 0.5049, 0.5144, 0.5182, 0.549, 0.5562, 0.5585, 0.5735, 
		                                             0.5857, 0.5782, 0.6168, 0.6407, 0.6751, 0.6574}; 

	/**
	 * Total Standard Deviation
	 */
	
	public static final double[]    TOTAL_STD = {0.8322,0.8279,0.8327,0.8566,0.871,0.8863,0.8912,0.9002,0.8833,0.8898,0.8666,0.8934,0.9116,0.9234,0.9066,0.8096};
	/**
	 * blin coefficients.
	 */
	public static final double[]   b1in = {-0.60, -0.36,-0.33,-0.29,-0.23,-0.25,-0.28,-0.31,-0.39,-0.44,
		                                   -0.50, -0.60,-0.69,-0.70,-0.72,-0.73};
	/**
	 * b1 coefficients.
	 */
	public static final double[]     b1 = {-0.50, -0.64,-0.62,-0.64,-0.64,-0.60,-0.53,-0.52,-0.52,-0.52,
		                                   -0.51, -0.50,-0.47,-0.44,-0.40,-0.38};
	/**
	 * b2 coefficients.
	 */
	public static final double[]     b2 = {-0.06,-0.14,-0.11,-0.11,-0.11,-0.13,-0.18,-0.19,-0.16,-0.14,
		                                   -0.10,-0.06, 0.00, 0.00, 0.00, 0.00};
	/**
	 * period -independent site amplification coefficients (similar to table 4  - BA2003)
	 */
	public static final double pga4nl_a1  = new Double(0.03);	  
	public static final double pga4nl_low  = new Double(0.06);	  
	public static final double pga4nl_a2 = new Double(0.09);
    public static final double pga4nl_mag_ref = new Double(4.5);
    public static final double pga4nl_mag_hinge = new Double(6.75);
//    public static final double[] pga4nl_mh= { 8.50, 6.75, 6.75, 6.75, 6.75, 6.75, 6.75, 6.75, 6.75, 6.75, 
//    	                                      6.75, 6.75, 6.75, 6.75, 6.75, 6.75, 6.75, 6.75, 6.75, 6.75, 
//    	                                      6.75, 8.5, 8.5, 8.5};
    public static final double r_ref = new Double(1.00);	
	public static final double VS30_REF = 760;
	public static final double v1 = 180; 
	public static final double v2 = 300; // m/s
	/**
	 * Coefficients to compute pga4nl from BA2008
	 */
	public static final double c1= -0.66050;
	public static final double c2=  0.11970;
	public static final double c3= -0.01151;
	public static final double h =  1.35000;	  
	public static final double e1= -0.53804;
	public static final double e2= -0.50350;
	public static final double e3= -0.75472;
	public static final double e4= -0.50970;
	public static final double e5=  0.28805;
	public static final double e6= -0.10164;
	public static final double e7=  0.00000;
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
	 * Reference Magnitude to separate the two functional forms (1a) and (1b).
	 */
	public static final Double MAG_REF = new Double(6.50);

	/**
	 * Minimum jb distance.
	 */
	public static final Double DISTANCE_JB_WARN_MIN = new Double(0.0);
	/**
	 * Maximum jb distance.
	 */
	public static final Double DISTANCE_JB_WARN_MAX = new Double(100.0);
	/**
	 * SOIL CLASS ROCK Vs30 upper bound
	 */
	public static final double SOIL_TYPE_ROCK_UPPER_BOUND = 750.0;
	/**
	 * SOIL CLASS STIFF SOIL Vs30 upper bound
	 */
	public static final double SITE_TYPE_STIFF_SOIL_UPPER_BOUND = 360.0;
	/**
	 * SOIL CLASS ROCK Vs30 upper bound
	 */
	public static final double SITE_TYPE_SOFT__UPPER_BOUND  = 180;
	
	/**
	 * cm/s to g conversion factor.
	 */
	public static final double CMS_TO_G_CONVERSION_FACTOR = 1.0/981.0;
}
