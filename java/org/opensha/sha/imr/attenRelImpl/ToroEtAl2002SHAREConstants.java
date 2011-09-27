package org.opensha.sha.imr.attenRelImpl;

public class ToroEtAl2002SHAREConstants {
	
	/**
	 * Rock adjustment coefficients
	 * coefficients to correct the hard-rock soil(vs30>=2800m/sec) to rock (vs30>=800m/sec)
	 * obtained from interpolation of the coefficients presented in table 9 Drouet et al (2010) 
	 */
	public static final	double []  AFrock = { 0.735106, 0.419632, 0.477379, 0.888509, 1.197291, 1.308267, 1.30118, 
		1.265762, 1.215779,	1.215779,1.215779};

	/**
	 * Adjustment factors of the total standard deviation when hard rock (vs30 =2800m/s) to 
	 * refference rock (vs30 =800m/s). Interpolated from Table 9 (Drouet et al 2010)
	 * Note: the adjustment factors for periods of 3 and 4 sec are identical with those for
	 * 2sec - SHARE experts decision  
	 */	
	public static final	double []  sig_AFrock = {0.338916, 0.289785, 0.320650, 0.352442, 0.281552, 0.198424, 0.1910745,
		0.154327, 0.155520,	0.155520,0.155520};
}
