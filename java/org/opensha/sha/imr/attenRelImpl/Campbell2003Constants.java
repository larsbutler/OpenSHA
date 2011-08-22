package org.opensha.sha.imr.attenRelImpl;

import org.opensha.commons.exceptions.InvalidRangeException;
import org.opensha.commons.param.StringParameter;
import org.opensha.commons.util.FaultUtils;
import org.opensha.sha.earthquake.EqkRupture;

public class Campbell2003Constants {
	/**
	 * Supported period values (s).
	 */
	public static final double[] PERIOD = 
	   {0.01000, 0.02000, 0.03000, 0.05000, 0.07500, 0.10000, 0.15000, 0.20000, 0.30000, 0.50000, 
		0.75000, 1.00000, 1.50000, 2.00000, 3.00000, 4.00000};	
	/**
	 * c1 coefficients.
	 */
	public static final double[] c1  = 
	    {0.03050, 1.35350, 1.18600, 0.37360, -0.03950, -0.14750, -0.19010, -0.43280, -0.69060, -0.59070, 
		-0.54290, -0.61040, -0.96660, -1.43060, -2.23310, -2.79750};	
	/**
	 * c2 coefficients.
	 */
	public static final double[] c2  = 
	   {0.63300, 0.63000, 0.62200, 0.61600, 0.61500, 0.61300, 0.61600, 0.61700, 0.60900, 0.53400, 
		0.48000, 0.45100, 0.44100, 0.45900, 0.49200, 0.50700};
	/**
	 * c3 coefficients.
	 */
	public static final double[] c3  = 
	   {-0.04270, -0.04040, -0.03620, -0.03530, -0.03530, -0.03530, -0.04780, -0.05860, -0.07860, -0.13790, 
		-0.18060, -0.20900, -0.24050, -0.25520, -0.26460, -0.27380};
	/**
	 * c4 coefficients.
	 */
	public static final double[] c4  = {-1.59100, -1.78700, -1.69100, -1.46900, -1.38300, -1.36900, -1.36800, -1.32000, -1.28000, -1.21600, 
		-1.18400, -1.15800, -1.13500, -1.12400, -1.12100, -1.11900};
	/**
	 * c5 coefficients.
	 */
	public static final double[] c5  = 
	   {-0.00428, -0.00388, -0.00367, -0.00378, -0.00421, -0.00454, -0.00473, -0.00460, -0.00414, -0.00341, 
		-0.00288, -0.00255, -0.00213, -0.00187, -0.00154, -0.00135};
	/**
	 * c6 coefficients.
	 */
	public static final double[] c6  = 
	   {0.00048, 0.00050, 0.00050, 0.00050, 0.00049, 0.00046, 0.00039, 0.00034, 0.00026, 0.00019, 
		0.00016, 0.00014, 0.00012, 0.00010, 0.00008, 0.00007};
	/**
	 * c7 coefficients.
	 */
	public static final double[] c7  = 
	   {0.68300, 1.02000, 0.92200, 0.63000, 0.49100, 0.48400, 0.46100, 0.39900, 0.34900, 0.31800,  
		0.30400, 0.29900, 0.30400, 0.31000, 0.31000, 0.29400};
	/**
	 * c8 coefficients.
	 */
	public static final double[] c8  = 
	   {0.41600, 0.36300, 0.37600, 0.42300, 0.46300, 0.46700, 0.47800, 0.49300, 0.50200, 0.50300, 
		0.50400, 0.50300, 0.50000, 0.49900, 0.49900, 0.50600};
	/**
	 * c9 coefficients.
	 */
	public static final double[] c9  = 
	    {1.14000, 0.85100, 0.75900, 0.77100, 0.95500, 1.09600, 1.23900, 1.25000, 1.24100, 1.16600,
		1.11000, 1.06700, 1.02900, 1.01500, 1.01400, 1.01800};
	/**
	 * c10 coefficients.
	 */
	public static final double[] c10  = 
	   {-0.87300, -0.71500, -0.92200, -1.23900, -1.34900, -1.28400, -1.07900, -0.92800, -0.75300,
		-0.60600, -0.52600, -0.48200, -0.43800, -0.41700, -0.39300, -0.38600}; 
	/**
	 * c11 coefficients.
	 */
	public static final double[] c11  =
       {1.03000, 1.03000, 1.03000, 1.04200, 1.05200, 1.05900, 1.06800, 1.07700, 1.08100, 1.09800,
		1.10500, 1.11000, 1.09900, 1.09300, 1.09000, 1.09200};
	/**
	 * c12 coefficients.
	 */
	public static final double[] c12  =
	  {-0.08600, -0.08600, -0.08600, -0.08380, -0.08380, -0.08380, -0.08380, -0.08380, -0.08380,
       -0.08240, -0.08060, -0.07930, -0.07710, -0.07580, -0.07370, -0.07220};		
	/**
	 * c13 coefficients.
	 */
	public static final	double [] c13  = 
	  {0.41400, 0.41400, 0.41400, 0.44300, 0.45300, 0.46000, 0.46900, 0.47800, 0.48200, 0.50800,
	   0.52800, 0.54300, 0.54700, 0.55100, 0.56200, 0.57500};
	/**
	 * Rock adjustment coefficients
	 * coefficients to correct the hard-rock soil(vs30>=2800m/sec) to rock (vs30>=800m/sec)
	 * obtained from interpolation of the coefficients presented in table 9 Drouet et al (2010) 
	 */
	public static final	double []  AFrock = 
	   {0.735106, 0.474275,	0.423049, 0.550323,	0.730061, 0.888509,	1.094622, 1.197291, 1.288309, 1.311421,
		1.298212, 1.265762, 1.197583, 1.215779, 1.215779, 1.215779};
	/**
	 * Style-of-faulting adjustment coefficients, 
	 * obtained from Table 7 (Drouet et al 2010) and using a cubic spline interpolation 
	 */
	public static final	double []  Frss = 
		{1.220000, 1.080745, 0.986646, 0.931209, 0.910332, 0.985998, 1.080000, 1.150000, 1.190000, 1.230000,
		 1.230000, 1.191702, 1.177500, 1.194705, 1.140000, 1.140000, 1.140000};

	public static final	double Fnss = 0.95; 
	public static final	double pN = 0.01;
	public static final	double pR = 0.81;

	/**
	 * Total standard deviation.
	 */	
	public static final double[] TOTAL_STD = 
		{0.338916, 0.338916, 0.289785, 0.320650, 0.352442, 0.352442, 0.352442, 0.281552, 0.281552, 0.198424,
		0.154327, 0.154327, 0.154327, 0.155520, 0.155520, 0.155520};
	/**
	 * Adjustment factors of the total standard deviation when hard rock (vs30 =2800m/s) to 
	 * refference rock (vs30 =800m/s). Interpolated from Table 9 (Drouet et al 2010)
	 * Note: the adjustment factors for periods of 3 and 4 sec are identical with those for
	 * 2sec - SHARE experts decision  
	 */	
	public static final	double []  sig_AFrock = 
	   {0.338916, 0.283461, 0.289785, 0.345375,	0.365490, 0.352442,	0.315477, 0.281552,	0.231117, 0.175678,
		0.149567, 0.154327, 0.191149, 0.155520, 0.155520, 0.155520};

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
	public static final Double MAG_WARN_MAX = new Double(8.5);
	/**
	 * Minimum rupture distance.
	 */
	public static final Double DISTANCE_RUP_WARN_MIN = new Double(0.0);
	/**
	 * Maximum rupture distance.
	 */
	public static final Double DISTANCE_RUP_WARN_MAX = new Double(1000.0);
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
