package org.opensha.sha.imr.attenRelImpl;


public class BindiEtAl2010Constants {
	/**
	 * Supported period values (s).
	 */
	public static final	double [] PERIOD ={-1.0000, 0.0000, 0.0300, 0.0400, 0.0700, 0.1000, 0.1500, 0.2000, 0.2500, 0.3000, 
			                                0.3500, 0.4000, 0.4500, 0.5000, 0.6000, 0.7000, 0.8000, 0.9000, 1.0000, 1.2500, 
			                                1.5000, 1.7500, 2.0000};
	/**
	 * a coefficients.
	 * a_jb  for JB distance
	 * a_epi for Epicentral Distance
	 */
	public static final	double []a_jb = { 2.5740, 3.7691, 3.8802, 3.8569, 4.0050, 4.0176, 4.1000, 4.0808, 3.9805, 3.9016, 
		                                  3.8185, 3.6578, 3.5972, 3.5304, 3.3531, 3.2126, 3.0980, 3.0472, 3.0311, 2.8210, 
		                                  2.8348, 2.8610, 2.7506};
    public static final	double []a_epi= { 2.5830, 3.7500, 3.8636, 3.8461, 3.9944, 3.9926, 4.0596, 4.0725, 3.9793, 3.8899, 
    	                                  3.8082, 3.6486, 3.5930, 3.5320, 3.3660, 3.2342, 3.1072, 3.0662, 3.0468, 2.8175, 
    	                                  2.8253, 2.8399, 2.7171};
	/**
	 * b1 coefficients.
	 * b1_jb  for JB distance
	 * b1_epi for Epicentral Distance
	 */
    public static final	double []b1_jb= { 0.0496, 0.0523, 0.0086, 0.0395, 0.0479, 0.0619, 0.0930, 0.0633, 0.1333, 0.1224, 
    	                                  0.1167, 0.1583, 0.1656, 0.2035, 0.2456, 0.2754, 0.2949, 0.3500, 0.3555, 0.3621, 
    	                                  0.2498, 0.1834, 0.2056};
    public static final	double []b1_epi= {0.0890, 0.1180, 0.0723, 0.1056, 0.1111, 0.1360, 0.1713, 0.1252, 0.1856, 0.1709,
    	 								  0.1670, 0.2120,  0.2225, 0.2612, 0.3051, 0.3272, 0.3532, 0.4113, 0.4210, 0.4168, 
    	 								  0.2885, 0.2188,  0.2378};
	/**
	 * b1 coefficients.
	 * b1_jb  for JB distance
	 * b1_epi for Epicentral Distance
	 */
    public static final	double []b2_jb  ={ -0.0982, -0.1389, -0.1287, -0.1255, -0.1232, -0.1120, -0.1330, -0.1358, -0.1418, -0.1407,
    	                                   -0.1366, -0.1470, -0.1342, -0.1320, -0.1181, -0.1209, -0.0963, -0.0952, -0.0962, -0.0963,
    	                                   -0.1103, -0.1040, -0.1139};
    public static final	double []b2_epi ={ -0.0771, -0.1147, -0.1043, -0.0993, -0.0962, -0.0839, -0.1061, -0.1124, -0.1177, -0.1186,
    	                                   -0.1153, -0.1262, -0.1119, -0.1081, -0.0931, -0.0968, -0.0708, -0.0663, -0.0666, -0.0708,
    	                                   -0.0923, -0.0878, -0.0990};
	/**
	 * c1 coefficients.
	 * c1_jb  for JB distance
	 * c1_epi for Epicentral Distance
	 */	
    public static final	double []c1_jb ={ -2.0846, -1.9383, -1.9720, -1.9300, -1.9197, -1.8599, -1.8769, -1.8833, -1.8756, -1.8908,
    	                                  -1.8992, -1.8521, -1.8678, -1.8728, -1.8463, -1.8299, -1.8318, -1.8627, -1.9011, -1.8780,
    	                                  -1.9787, -2.0899, -2.0976};
    public static final	double []c1_epi={ -2.0896, -1.9267, -1.9618, -1.9232, -1.9128, -1.8447, -1.8525, -1.8780, -1.8747, -1.8837,
    	                                  -1.8929, -1.8463, -1.8654, -1.8738, -1.8534, -1.8420, -1.8367, -1.8733, -1.9096, -1.8762,
    	                                  -1.9731, -2.0782, -2.0787};
	/**
	 * c2 coefficients.
	 * c2_jb  for JB distance
	 * c2_epi for Epicentral Distance
	 */	
    public static final	double[]c2_jb ={ 0.5283, 0.4661, 0.4710, 0.4431, 0.4212, 0.3949, 0.4125, 0.4546, 0.4318, 0.4551,
    	                                 0.4740, 0.4727, 0.4665, 0.4519, 0.4414, 0.4396, 0.4255, 0.3992, 0.4036, 0.4151, 
    	                                 0.5216, 0.5880, 0.5953};
    public static final	double[]c2_epi={ 0.5106, 0.4285, 0.4346, 0.4040, 0.3835, 0.3492, 0.3646, 0.4187, 0.4025, 0.4298,
    	                                 0.4480, 0.4439, 0.4352, 0.4193, 0.4061, 0.4096, 0.3911, 0.3612, 0.3627, 0.3838,
    	                                 0.5055, 0.5756, 0.5868};
	/**
	 * h coefficients.
	 * h_jb  for JB distance
	 * h_epi for Epicentral Distance
	 */	
    public static final	double []h_jb   ={ 10.4844, 10.1057, 10.5940, 10.0362, 10.2414, 10.4222, 10.7824, 10.5949, 10.2248, 9.7928, 
    	                                   9.47140, 9.26900, 9.34370, 9.28420, 9.03070, 8.87940, 8.74810, 9.14140, 9.60440, 9.5829,
    	                                   9.9923, 10.8928, 10.5615};
    public static final	double []h_epi  ={ 10.5886, 10.0497, 10.5707, 10.0637, 10.2906, 10.3528, 10.6030, 10.6263, 10.3088, 9.7877,
    	                                    9.4708,  9.2789,  9.3832,  9.3706,  9.2463,  9.1689,  8.9420,  9.4254,  9.8637, 9.6593, 
    	                                    9.9835, 10.7942, 10.3772};
	/**
	 * C0 coefficients.
	 * C0_jb  for JB distance
	 * C0_epi for Epicentral Distance
	 */	
    public static final	double [] C0_jb  ={ 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 
    	                                    0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 
    	                                    0.0000, 0.0000, 0.0000};
    public static final	double 	[]C0_epi ={ 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000,
    	                                    0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000,
    	                                    0.0000, 0.0000, 0.0000};
	/**
	 * C1 coefficients.
	 * C1_jb  for JB distance
	 * C1_epi for Epicentral Distance
	 */	
    public static final	double []C1_jb  ={ 0.1462, 0.2260, 0.2176, 0.2221, 0.2082, 0.2572, 0.2631, 0.2126, 0.1618, 0.1409,
    									   0.1289, 0.1146, 0.0946, 0.0763, 0.0539, 0.0447, 0.0436, 0.0400, 0.0347, 0.0233,
    									  -0.0006, -0.0002, -0.0065};
    public static final	double []C1_epi ={ 0.1496, 0.2297, 0.2211, 0.2260, 0.2122, 0.2609, 0.2667, 0.2158, 0.1654, 0.1448, 
    	                                   0.1329, 0.1185, 0.0983, 0.0802, 0.0581, 0.0489, 0.0481, 0.0445, 0.0389, 0.0285, 
    	                                   0.0035, 0.0041, -0.0012};
	/**
	 * C2 coefficients.
	 * C2_jb  for JB distance
	 * C2_epi for Epicentral Distance
	 */	
    public static final	double []C2_jb  ={ 0.2701, 0.1043, 0.0866, 0.0764, 0.0390, 0.0580, 0.0632, 0.1212, 0.1454, 0.1630, 
    	                                   0.1892, 0.2190, 0.2632, 0.2741, 0.2973, 0.3217, 0.3406, 0.3663, 0.3791, 0.4091, 
    	                                   0.4111, 0.4133, 0.3836};
    public static final	double []C2_epi ={ 0.2673, 0.1022, 0.0842, 0.0742, 0.0367, 0.0560, 0.0610, 0.1191, 0.1431, 0.1605, 
    	                                   0.1868, 0.2168, 0.2612, 0.2722, 0.2955, 0.3197, 0.3387, 0.3645, 0.3772, 0.4079, 
    	                                   0.4089, 0.4124, 0.3830};
	/**
	 * Inter-Event standard deviation
	 * INTER_EVENT_STD     for JB distance
	 * INTER_EVENT_STD_EPI for Epicentral Distance
	 */	
    public static final	double []INTER_EVENT_STD     ={0.2314, 0.2084, 0.2083, 0.2158, 0.1999, 0.2045, 0.2099, 0.2149, 0.2090, 0.2219, 
    	                                               0.2430, 0.2213, 0.2215, 0.2197, 0.2360, 0.2379, 0.2375, 0.2393, 0.2471, 0.2605, 
    	                                               0.2223, 0.2359, 0.2242};
    public static final	double []INTER_EVENT_STD_EPI ={0.2344, 0.2103, 0.2102, 0.2177, 0.2017, 0.2059, 0.2114, 0.2164, 0.2108, 0.2247, 
    												   0.2244, 0.2237, 0.2239, 0.2220, 0.2391, 0.2412, 0.2409, 0.2429, 0.2504, 0.2402, 
    												   0.2249, 0.2161, 0.2058};
	/**
	 * INTRA-EVENT Standard Deviation
	 * INTRA_EVENT_STD     for JB distance
	 * INTRA_EVENT_STD_EPI for Epicentral Distance
	 */	
    public static final	double []INTRA_EVENT_STD     ={0.2819, 0.2634, 0.2603, 0.2656, 0.2887, 0.2970, 0.2941, 0.2840, 0.2647, 0.2446,
    												   0.2479, 0.2291, 0.2327, 0.2300, 0.2290, 0.2311, 0.2308, 0.2302, 0.2560, 0.2619,
    												   0.2654, 0.2390, 0.2112};
    public static final	double []INTRA_EVENT_STD_EPI ={0.2454, 0.2666, 0.2638, 0.2690, 0.2926, 0.2781, 0.2971, 0.2859, 0.2671, 0.2474,
    												   0.2506, 0.2314, 0.2146, 0.2322, 0.2121, 0.2141, 0.2138, 0.2333, 0.2386, 0.2444,
    												   0.2477, 0.2424, 0.1947};
	/**
	 * Total Standard Deviation.
	 * TOTAL_STD  for JB distance
	 * TOTAL_STD_EPI for Epicentral Distance
	 */	
    public static final	double []TOTAL_STD     ={0.3659, 0.3523, 0.3521, 0.3648, 0.3649, 0.3734, 0.3832, 0.3924, 0.3815, 0.3750, 
    	                                         0.3842, 0.3740, 0.3744, 0.3713, 0.3732, 0.3761, 0.3756, 0.3784, 0.3907, 0.4119,
    	                                         0.4059, 0.3987, 0.3790};
    public static final	double []TOTAL_STD_EPI ={0.3707, 0.3555, 0.3553, 0.3680, 0.3682, 0.3759, 0.3859, 0.3951, 0.3849, 0.3798, 
    											 0.3794, 0.3782, 0.3784, 0.3753, 0.3781, 0.3813, 0.3809, 0.3840, 0.3959, 0.4059, 
    											 0.4106, 0.3946, 0.3758};

	/**
	 * log10 to natural log conversion factor.
	 */
	public static final double LOG10_2_LN = Math.log(10.0);
	/**
	 * Minimum magnitude.
	 */
	public static final Double MAG_WARN_MIN = new Double(4);
	/**
	 * Maximum magnitude.
	 */
	public static final Double MAG_WARN_MAX = new Double(6.9);
	/**
	 * Refference Magnitude.
	 */
	public static final Double MAG_Ref = new Double(4.5);
	/**
	 * Minimum rupture distance.
	 */
	public static final Double DISTANCE_JB_WARN_MIN = new Double(0.0);
	public static final Double DISTANCE_EPI_WARN_MIN = new Double(0.0);
	/**
	 * Maximum rupture distance.
	 */
	public static final Double DISTANCE_JB_WARN_MAX = new Double(100.0);
	public static final Double DISTANCE_EPI_WARN_MAX = new Double(100.0);
	/**
	 * STIFF SOIL Vs30 upper bound
	 */
	public static final double ROCK_SOIL_LOWER_BOUND = 760.0;
	/**
	 * STIFF SOIL Vs30 upper bound
	 */
	public static final double STIFF_SOIL_UPPER_BOUND = 360.0;
	/**
	 * SOFT SOIL Vs30 upper bound
	 */
	public static final double SOFT_SOIL_UPPER_BOUND  = 180.0;
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