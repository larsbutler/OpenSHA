/*******************************************************************************
 * Copyright 2009 OpenSHA.org in partnership with
 * the Southern California Earthquake Center (SCEC, http://www.scec.org)
 * at the University of Southern California and the UnitedStates Geological
 * Survey (USGS; http://www.usgs.gov)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.opensha.sha.imr.attenRelImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.opensha.sha.imr.AttenuationRelationship;
import org.opensha.sha.imr.PropagationEffect;
import org.opensha.sha.imr.ScalarIntensityMeasureRelationshipAPI;
import org.opensha.sha.imr.param.EqkRuptureParams.FaultTypeParam;
import org.opensha.sha.imr.param.EqkRuptureParams.MagParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.DampingParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.PGA_Param;
import org.opensha.sha.imr.param.IntensityMeasureParams.PGV_Param;
import org.opensha.sha.imr.param.IntensityMeasureParams.PeriodParam;
import org.opensha.sha.imr.param.IntensityMeasureParams.SA_Param;
import org.opensha.sha.imr.param.OtherParams.ComponentParam;
import org.opensha.sha.imr.param.OtherParams.StdDevTypeParam;
import org.opensha.sha.imr.param.PropagationEffectParams.DistanceJBParameter;


/**
 * <b>Title:</b> Akkar_Bommer_2010_AttenRel<p>
 *
 * <b>Description:</b> This implements the  Attenuation Relationship 
 * developed by Akkar and Bommer ("Empirical Equations for the Prediction of PGA
 * , PGV, and Spectral Accelerations in Europe, the Mediterranean Region, and the Middle East" 
 * Seismological Research Letters Vol. 81, No 2, pp 195-206)<p>
 *
 * Supported Intensity-Measure Parameters:<p>
 * <UL>
 * <LI>pgaParam - Peak Ground Acceleration
 * <LI>pgvParam - Peak Ground Velocity
 * <LI>saParam - Response Spectral Acceleration
 * </UL><p>
 * Other Independent Parameters:<p>
 * <UL>
 * <LI>magParam - moment Magnitude
 * <LI>distanceJBParam - Joyner-Boore distance
 * <LI>siteTypeParam - "Rock", "Stiff Soil", "Soft Soil"
 * <LI>fltTypeParam - Style of faulting "Normal" and "Reverse"
 * <LI>componentParam - Component of shaking
 * <LI>stdDevTypeParam - The type of standard deviation
 * </UL><p>
 * 
 *<p>
 *
 * Verification - This model MUST be validated against: 1) a verification file generated independently by L. Danciu,
 * implemented in the JUnit test class DT_2010_test; 
 * 
 * 
 * 
 *</p>
 *
 **
 * @author     L. Danciu
 * @created    August 20, 2010
 * @version    1.0
 */


public class AkB_2010_AttenRel 
  extends AttenuationRelationship 
  implements ScalarIntensityMeasureRelationshipAPI, NamedObjectAPI, ParameterChangeListener {
	
	private static Log logger = LogFactory.getLog(AkB_2010_AttenRel.class);


	
	
	
  public final static String C = "Akkar_Bommer_2010_AttenRel";
  private final static boolean D = false;
  public final static String NAME = "Akkar & Bommer 2010";
  public final static String SHORT_NAME = "AkB_2010";
  private static final long serialVersionUID = 007L;

  /**
   * This provides a URL where more info on this model can be obtained
   * @throws MalformedURLException if returned URL is not a valid URL.
   * @returns the URL to the AttenuationRelationship document on the Web.
   */
  public URL getInfoURL() throws MalformedURLException{
	  return new URL("http://www.opensha.org/documentation/modelsImplemented/attenRel/Akkar_Bommer_2010.html");
  }


  // coefficients:
  //    note that 
  //    index 0 below is for PGA
  //    index -1 is for PGV 
  double[]period = {-1.00000, 0.00000, 0.01000, 0.02000, 0.03000, 0.04000, 0.05000, 0.10000, 0.15000, 0.20000, 
		             0.25000, 0.30000, 0.35000, 0.40000, 0.45000, 0.50000, 0.55000, 0.60000, 0.65000, 0.70000, 
		             0.75000, 0.80000, 0.85000, 0.90000, 0.95000, 1.00000, 1.05000, 1.10000, 1.15000, 1.20000, 
		             1.25000, 1.30000, 1.35000, 1.40000, 1.45000, 1.50000, 1.55000, 1.60000, 1.65000, 1.70000, 
		             1.75000, 1.80000, 1.85000, 1.90000, 1.95000, 2.00000, 2.05000, 2.10000, 2.15000, 2.20000, 
		             2.25000, 2.30000, 2.35000, 2.40000, 2.45000, 2.50000, 2.55000, 2.60000, 2.65000, 2.70000, 
		             2.75000, 2.80000, 2.85000, 2.90000, 2.95000, 3.00000};

double[]b1 = { -2.12833,   1.43525,  1.43153,  1.48690,  1.64821,  2.08925,  2.49228,  2.11994,  1.64489, 0.92065,
		        0.13978,  -0.84006, -1.32207, -1.70320, -1.97201, -2.76925, -3.51672, -3.92759, -4.49490,-4.62925,
		        -4.95053, -5.32863, -5.75799, -5.82689, -5.90592, -6.17066, -6.60337, -6.90379, -6.96180,-6.99236,
		        -6.74613, -6.51719, -6.55821, -6.61945, -6.62737, -6.71787, -6.80776, -6.83632, -6.88684,-6.94600,
		        -7.09166, -7.22818, -7.29772, -7.35522, -7.40716, -7.50404, -7.55598, -7.53463, -7.50811,-8.09168,
		        -8.11057, -8.16272, -7.94704, -7.96679, -7.97878, -7.88403, -7.68101, -7.72574,  -7.53288,-7.41587,
		        -7.34541, -7.24561, -7.07107, -6.99332, -6.95669,  -6.92924};

double[]b2 = {1.21448, 0.74866, 0.75258, 0.75966, 0.73507, 0.65032, 0.58575, 0.75179, 0.83683, 0.96815, 
		      1.13068, 1.37439, 1.47055, 1.55930, 1.61645, 1.83268, 2.02523, 2.08471, 2.21154, 2.21764,
		      2.29142, 2.38389, 2.50635, 2.50287, 2.51405, 2.58558, 2.69584, 2.77044, 2.75857, 2.73427,
		      2.62375, 2.51869, 2.52238, 2.52611, 2.49858, 2.49486, 2.50291, 2.51009, 2.54048, 2.57151,
		      2.62938, 2.66824, 2.67565, 2.67749, 2.68206, 2.71004, 2.72737, 2.71709, 2.71035, 2.91159,
		      2.92087, 2.93325, 2.85328, 2.85363, 2.84900, 2.81817, 2.75720, 2.82043, 2.74824, 2.69012,
		      2.65352, 2.61028, 2.56123, 2.52699, 2.51006, 2.45899};

double[]b3 = {-0.08137,-0.06520, -0.06557, -0.06767, -0.06700, -0.06218, -0.06043, -0.07448, -0.07544, -0.07903,
		      -0.08761, -0.10349, -0.10873, -0.11388,-0.11742, -0.13202, -0.14495, -0.14648, -0.15522, -0.15491,
		      -0.15983, -0.16571, -0.17479, -0.17367,-0.17417, -0.17938, -0.18646, -0.19171, -0.18890, -0.18491,
		      -0.17392, -0.16330, -0.16307, -0.16274,-0.15910, -0.15689, -0.15629, -0.15676, -0.15995, -0.16294,
		      -0.16794, -0.17057, -0.17004, -0.16934,-0.16906, -0.17130, -0.17291, -0.17221, -0.17212, -0.18920,
		      -0.19044, -0.19155, -0.18539, -0.18561,-0.18527, -0.18320, -0.17905, -0.18717, -0.18142, -0.17632,
		      -0.17313, -0.16951, -0.16616, -0.16303,-0.16142,-0.15513};

double[]b4 = {-2.46942, -2.72950, -2.73290, -2.82146, -2.89764, -3.02618, -3.20215, -3.10538, -2.75848, -2.49264,
		      -2.33824, -2.19123, -2.12993, -2.12718, -2.16619,	-2.12969, -2.04211,	-1.88144, -1.79031,	-1.79800,
		      -1.81321,	-1.77273, -1.77068,	-1.76295, -1.79854,	-1.80717, -1.73843,	-1.71109, -1.66588,	-1.59120,
		      -1.52886,	-1.46527, -1.48223,	-1.48257, -1.43310,	-1.35301, -1.31227,	-1.33260, -1.40931,	-1.47676,
		      -1.54037,	-1.54273, -1.50936,	-1.46988, -1.43816,	-1.44395, -1.45794,	-1.46662, -1.49679,	-1.55644,
		      -1.59537,	-1.60461, -1.57428,	-1.57833, -1.57728,	-1.60381, -1.65212,	-1.88782, -1.89525,	-1.87041,
		      -1.86079,	-1.85612, -1.90422,	-1.89704, -1.90132,	-1.76801};

double[]b5 = {0.22349, 0.25139, 0.25170, 0.26510, 0.27607, 0.28999, 0.31485, 0.30253, 0.25490, 0.21790, 
		      0.20089, 0.18139, 0.17485, 0.17137, 0.17700, 0.16877,	0.15617, 0.13621, 0.12916, 0.13495,
		      0.13920, 0.13273,	0.13096, 0.13059, 0.13535, 0.13599, 0.12485, 0.12227, 0.11447, 0.10265,
		      0.09129, 0.08005,	0.08173, 0.08213, 0.07577, 0.06379,	0.05697, 0.05870, 0.06860, 0.07672,
		      0.08428, 0.08325,	0.07663, 0.07065, 0.06525, 0.06602,	0.06774, 0.06940, 0.07429, 0.08428,
		      0.09052, 0.09284,	0.09077, 0.09288, 0.09428, 0.09887,	0.10680, 0.14049, 0.14356, 0.14283,
		      0.14340, 0.14444,	0.15127, 0.15039, 0.15081, 0.13314};

double[]b6 = {6.41443, 7.74959, 7.73304, 7.20661, 6.87179, 7.42328, 7.75532, 8.21405, 8.31786, 
		      8.21914, 7.20688, 6.54299, 6.24751, 6.57173,
			  6.78082, 7.17423,	6.76170, 6.10103, 5.19135, 4.46323,	4.27945, 4.37011, 4.62192, 4.65393,
			  4.84540, 4.97596,	5.04489, 5.00975, 5.08902, 5.03274, 5.08347, 5.14423, 5.29006, 5.33490, 
			  5.19412, 5.15750, 5.27441, 5.54539, 5.93828, 6.36599, 6.82292, 7.11603, 7.31928, 7.25988,
			  7.25344, 7.26059,	7.40320, 7.46168, 7.51273, 7.77062,	7.87702, 7.91753, 7.61956, 7.59643,
			  7.50338, 7.53947,	7.61893, 8.12248, 7.92236, 7.49999,	7.26668, 7.11861, 7.36277, 7.45038,
			  7.60234, 7.21950};

double[]b7 = {0.20354,	0.08320,    0.08105,    0.07825,    0.06376,    0.05045,    0.03798,	0.02667,	0.02578,	
		      0.06557,	0.09810,	0.12847,	0.16213,	0.21222,
		      0.24121,	0.25944,	0.26498,	0.27718,	0.28574,	0.30348,	0.31516,	0.32153,	0.33520,	0.34849,
		      0.35919,	0.36619,	0.37278,	0.37756,	0.38149,	0.38120,	0.38782,	0.38862,	0.38677,	0.38625,
		      0.38285,	0.37867,	0.37267,	0.36952,	0.36531,	0.35936,	0.35284,	0.34775,	0.34561,	0.34142,
		      0.33720,	0.33298,	0.33010,	0.32645,	0.32439,	0.31354,	0.30997,	0.30826,	0.32071,	0.31801,
		      0.31401,	0.31104,	0.30875,	0.31122,	0.30935,	0.30688,	0.30635,	0.30534,	0.30508,	0.30362,
		      0.29987,	0.29772};

double[]b8 = {0.08484,	0.007660,   0.00745,    0.00618,  -0.00528,    -0.02091,   -0.03143,   -0.00062,	0.01703,	
		      0.02105,	0.03919,	0.04340,	0.06695,	0.09201,
		      0.11675,	0.13562,	0.14446,	0.15156,	0.15239,	0.15652,	0.16333,	0.17366,	0.18480,	0.19061,
		      0.19411,	0.19519,    0.19461,	0.19423,	0.19402,	0.19309,	0.19392,	0.19273,	0.19082,	0.19285,
		      0.19161,	0.18812,	0.18568,	0.18149,    0.17617,	0.17301,	0.16945,	0.16743,	0.16730,	0.16325,
		      0.16171,	0.15839,	0.15496,	0.15337,	0.15264,	0.14430,    0.14430,	0.14412,	0.14321,	0.14301,
		      0.14324,	0.14332,	0.14343,	0.14255,	0.14223,	0.14074,	0.14052,	0.13923,    0.13933,	0.13776,
		      0.13584,	0.13198};

double[]b9 = {-0.05856,	-0.05823,   -0.05886,   -0.06111,   -0.06189,   -0.06278,   -0.06708,	-0.04906,	-0.04184,
		      -0.02098,	-0.04853,	-0.05554,	-0.04722,	-0.05145,
		      -0.05202,	-0.04283,	-0.04259,	-0.03853,	-0.03423,	-0.04146,	-0.04050,	-0.03946,	-0.03786,	-0.02884,
		      -0.02209,	-0.02269,	-0.02613,	-0.02655,	-0.02088,	-0.01623,	-0.01826,	-0.01902,	-0.01842,	-0.01607,
		      -0.01288,	-0.01208,	-0.00845,	-0.00533,	-0.00852,	-0.01204,	-0.01386,	-0.01402,	-0.01526,	-0.01563,
		      -0.01848,	-0.02258,	-0.02626,	-0.02920,	-0.03484,	-0.03985,	-0.04155,	-0.04238,	-0.04963,	-0.04910,
		      -0.04812,	-0.04710,	-0.04607,	-0.05106,	-0.05024,	-0.04887,	-0.04743,	-0.04731,	-0.04522,	-0.04203,
		      -0.03863,	-0.03855};

double[]b10 = {0.01305,	0.07087,    0.07169,    0.06756,    0.06529,    0.05935,    0.06382,	0.07910,	0.07840,
		       0.08438,	0.08577,	0.09221,	0.09003,	0.09903,
		       0.09943,	0.08579,	0.06945,	0.05932,	0.05111,	0.04661,	0.04253,	0.03373,	0.02867,	0.02475,
		       0.02502, 0.02121,	0.01115,	0.00140,	0.00148,	0.00413,	0.00413,	-0.00369,	-0.00897,	-0.00876,
		      -0.00564,	-0.00215,	-0.00047,	-0.00006,	-0.00301,	-0.00744,	-0.01387,	-0.01492,	-0.01192,	-0.00703,
		      -0.00351,	-0.00486,	-0.00731,	-0.00871,	-0.01225,	-0.01927,	-0.02322,	-0.02626,	-0.02342,	-0.02570,
		      -0.02643,	-0.02769,	-0.02819,	-0.02966,	-0.02930,	-0.02963,	-0.02919,	-0.02751,	-0.02776,	-0.02615,
		      -0.02487,	-0.02469};

double[]sig1 = {0.25620,	0.26110,    0.26160,    0.26350,    0.26750,    0.27090,    0.27280,	0.27280,	0.27880,	
		        0.28210,	0.28710,	0.29020,	0.29830,	0.29980,
		        0.30370,	0.30780,	0.30700,	0.30070,	0.30040,	0.29780,	0.29730,	0.29270,    0.29170,	0.29150,
		        0.29120,	0.28950,	0.28880,	0.28960,	0.28710,	0.28780,	0.28630,	0.28690,	0.28850,    0.28750,
		        0.28570,	0.28390,	0.28450,	0.28440,	0.28410,	0.28400,	0.28400,	0.28340,	0.28280,	0.28260,
		        0.28320,	0.28350,	0.28360,	0.28320,	0.28300,	0.28300,	0.28300,	0.28290,	0.28150,	0.28260,
		        0.28250,	0.28180,	0.28180,	0.28380,	0.28450,	0.28540,	0.28620,	0.28670,	0.28690,	0.28740,
		        0.28720,	0.28760};

double[]sig2 = {0.10830,	0.10560,    0.10510,    0.11140,    0.11370,    0.11520,    0.11810,	0.11670,	0.11920,
		        0.10810,	0.09900,	0.09760,	0.10540,	0.11010,
		        0.11230,	0.11630,	0.12740,	0.14300,	0.15460,	0.16260,	0.16020,	0.15840,	0.15430,	0.15210,
		        0.14840,	0.14830,	0.14650,	0.14270,	0.14350,	0.14390,	0.14530,	0.14270,	0.14280,	0.14580,
		        0.14770,	0.14680,	0.14500,	0.14570,	0.15030,	0.15370,	0.15580,	0.15820,	0.15920,	0.16110,
		        0.16420,	0.16570,	0.16650,	0.16630,	0.16610,	0.16270,	0.16270,	0.16330,	0.16320,	0.16450,
		        0.16650,	0.16810,	0.16880,	0.17410,	0.17590,	0.17720,	0.17830,	0.17940,	0.17880,	0.17840,
		        0.17830,	0.17850};

double[]sigtot = {0.27810,	0.28160,    0.28192,    0.28608,    0.29066,    0.29438,    0.29727,	0.29671,	0.30321,    0.30210,
		          0.30369,	0.30617,	0.31637,	0.31938,    0.32380,	0.32904,	0.33238,	0.33297,	0.33785,	0.33930,
		          0.33771,	0.33281,	0.33000,	0.32880,    0.32683,	0.32527,	0.32383,	0.32280,	0.32097,	0.32177,
		          0.32106,	0.32043,	0.32191,	0.32236,    0.32162,	0.31961,	0.31932,	0.31950,	0.32141,	0.32292,
		          0.32393,	0.32457,	0.32453,	0.32529,    0.32736,	0.32837,	0.32886,	0.32842,	0.32814,	0.32644,
		          0.32644,	0.32665,	0.32539,	0.32699,    0.32792,	0.32813,	0.32849,	0.33290,	0.33449,	0.33594,
		          0.33720,	0.33820,	0.33810,	0.33827,    0.33800,	0.33849};
  /**
   * The current set of coefficients based on the selected intensityMeasure
   */

	// Hashmap

	private HashMap<Double, Integer> indexFromPerHashMap;

	  private int iper;
	  private double rJB, mag;
	  private String fltType;
      private String siteType; 
	  private String stdDevType; 
 	  private double log2ln = 2.302585;

	  private boolean parameterChange;
	  private PropagationEffect propagationEffect;	
	
	  /**
	   * Site Type Parameter ("Rock";"Stiff-Soil";"Soft-Soil")
	   */
	  private StringParameter siteTypeParam = null;
	  public final static String SITE_TYPE_NAME = "AkB Site Type";
	  // no units
	  public final static String SITE_TYPE_INFO = "Geological conditions as the site";
	  public final static String SITE_TYPE_ROCK  = "Rock";
	  public final static String SITE_TYPE_STIFF_SOIL = "Stiff-Soil";
	  public final static String SITE_TYPE_SOFT_SOIL  = "Soft-Soil";
      // Default soil category - ROCK - defined as having Vs30>750m/s 
	  public final static String SITE_TYPE_DEFAULT = "Rock";
		 // style of faulting options
	  public final static String FLT_TYPE_NORMAL = "Normal";
	  public final static String FLT_TYPE_REVERSE = "Reverse";
	  public final static String FLT_TYPE_STRIKE_SLIP = "Strike Slip";
	  // warning constraints:
	  protected final static Double MAG_WARN_MIN = new Double(5);
	  protected final static Double MAG_WARN_MAX = new Double(7.6);
	  protected final static Double DISTANCE_JB_WARN_MIN = new Double(0.0);
	  protected final static Double DISTANCE_JB_WARN_MAX = new Double(100.0);
	  
  // for issuing warnings:
  private transient ParameterChangeWarningListener warningListener = null;

	/**
	 *  This initializes several ParameterList objects.
	 */
	public AkB_2010_AttenRel(ParameterChangeWarningListener warningListener){
		super();

		this.warningListener = warningListener;
		initSupportedIntensityMeasureParams();

		// Create an Hash map that links the period with its index
		indexFromPerHashMap = new HashMap<Double, Integer>();
		for (int i = 0; i < period.length; i++) { 
			indexFromPerHashMap.put(new Double(period[i]), new Integer(i));
		}

		// Initialize earthquake Rupture parameters (e.g. magnitude)
		initEqkRuptureParams();
		// Initialize Propagation Effect Parameters (e.g. source-site distance)
		initPropagationEffectParams();
		// Initialize site parameters (e.g. vs30)
		initSiteParams();	
		// Initialize other parameters (e.g. stress drop)
		initOtherParams();
		// Initialize the independent parameters list 
		initIndependentParamLists();
		// Initialize the parameter change listeners
		initParameterEventListeners();
		if (D) System.out.println("--- Akkar_Bommer_2010_AttenRel end");

	}
	/**
	 * This initializes the parameter characterizing the earthquake rupture such as the magnitude
	 * and the style-of-fault mechanism.
	 */
	protected void initEqkRuptureParams() {
		if (D) System.out.println("--- initEqkRuptureParams");
		// Magnitude parameter
		magParam = new MagParam(MAG_WARN_MIN, MAG_WARN_MAX);
		// Focal mechanism
		StringConstraint fltConstr = new StringConstraint(); 
		fltConstr.addString(FLT_TYPE_NORMAL);
		fltConstr.addString(FLT_TYPE_REVERSE);
		fltConstr.addString(FLT_TYPE_STRIKE_SLIP);
		fltTypeParam = new FaultTypeParam(fltConstr,FLT_TYPE_NORMAL);
		// Add parameters 
		eqkRuptureParams.clear();
		eqkRuptureParams.addParameter(magParam);
		eqkRuptureParams.addParameter(fltTypeParam);
		if (D) System.out.println("--- initEqkRuptureParams end");
	}
	/**
	 *  Creates the Propagation Effect parameters and adds them to the
	 *  propagationEffectParams list. Makes the parameters non-editable.
	 */
	protected void initPropagationEffectParams() {
		if (D) System.out.println("--- initPropagationEffectParams");
		distanceJBParam = new DistanceJBParameter(0.0);
		distanceJBParam.addParameterChangeWarningListener(warningListener);
		DoubleConstraint warn = new DoubleConstraint(DISTANCE_JB_WARN_MIN, DISTANCE_JB_WARN_MAX);
		warn.setNonEditable();
		distanceJBParam.setWarningConstraint(warn);
		distanceJBParam.setNonEditable();
		// Add parameters 
		propagationEffectParams.addParameter(distanceJBParam);
		if (D) System.out.println("--- initPropagationEffectParams end");
	}
	/**
	 *  Creates the Site-Type parameter and adds it to the siteParams list.
	 *  Makes the parameters non-edit-able.
	 */
	protected void initSiteParams() {
		// 
		StringConstraint siteConstraint = new StringConstraint();
		siteConstraint.addString(SITE_TYPE_ROCK);
		siteConstraint.addString(SITE_TYPE_STIFF_SOIL);
		siteConstraint.addString(SITE_TYPE_SOFT_SOIL);
		siteConstraint.setNonEditable();
		siteTypeParam = new StringParameter(SITE_TYPE_NAME, siteConstraint, null);
		siteTypeParam.setInfo(SITE_TYPE_INFO);
        siteTypeParam.setDefaultValue(SITE_TYPE_ROCK);
		siteTypeParam.setNonEditable();
		// Add siteTypeParam to the set of parameters describing the site
		siteParams.clear();
		siteParams.addParameter(siteTypeParam);  
	}
	/**
	 *  Creates other Parameters that the mean or stdDev depends upon,
	 *  such as the Component or StdDevType parameters.
	 */
	protected void initOtherParams() {
		if (D) System.out.println("--- initOtherParams");
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
	    // SHARE WP4 uniform GMPE : Geometric mean
	    constraint.addString(ComponentParam.COMPONENT_AVE_HORZ);
	    constraint.setNonEditable();
	    componentParam = new ComponentParam(constraint,ComponentParam.COMPONENT_AVE_HORZ);
		// add these to the list
		otherParams.addParameter(stdDevTypeParam);
		otherParams.addParameter(componentParam);
	}	

	/**
	 * This creates the lists of independent parameters that the various dependent
	 * parameters (mean, standard deviation, exceedance probability, and IML at
	 * exceedance probability) depend upon. NOTE: these lists do not include anything
	 * about the intensity-measure parameters or any of their internal
	 * independentParamaters.
	 */
	protected void initIndependentParamLists() {
		// params that the mean depends upon
		meanIndependentParams.clear();
		meanIndependentParams.addParameter(magParam);
		meanIndependentParams.addParameter(fltTypeParam);
		meanIndependentParams.addParameter(distanceJBParam);
		meanIndependentParams.addParameter(siteTypeParam);
		// params that the stdDev depends upon
		stdDevIndependentParams.clear();
		stdDevIndependentParams.addParameter(saPeriodParam);
		stdDevIndependentParams.addParameter(stdDevTypeParam);
		
		// params that the exceed. prob. depends upon
		exceedProbIndependentParams.clear();
		exceedProbIndependentParams.addParameterList(meanIndependentParams);
		exceedProbIndependentParams.addParameter(stdDevTypeParam);
		exceedProbIndependentParams.addParameter(sigmaTruncTypeParam);
		exceedProbIndependentParams.addParameter(sigmaTruncLevelParam);

		// params that the IML at exceed. prob. depends upon
		imlAtExceedProbIndependentParams.addParameterList(exceedProbIndependentParams);
		imlAtExceedProbIndependentParams.addParameter(exceedProbParam);
	}
	/**
	 *  Creates the two supported IM parameters (PGA and SA), as well as the
	 *  independenParameters of SA (periodParam and dampingParam) and adds
	 *  them to the supportedIMParams list. Makes the parameters non-editable.
	 */
	protected void initSupportedIntensityMeasureParams() {
		// Create saParam:
		DoubleDiscreteConstraint periodConstraint = new DoubleDiscreteConstraint();
		for (int i = 1; i < period.length; i++) {
			periodConstraint.addDouble(new Double(period[i]));
		}
		periodConstraint.setNonEditable();
		saPeriodParam = new PeriodParam(periodConstraint);
		saDampingParam = new DampingParam();
		saParam = new SA_Param(saPeriodParam, saDampingParam);
		saParam.setNonEditable();

		//  Create PGA Parameter (pgaParam):
		pgaParam = new PGA_Param();
		pgaParam.setNonEditable();

		//  Create PGV Parameter (pgvParam):
		pgvParam = new PGV_Param();
		pgvParam.setNonEditable();
		
		// Add the warning listeners:
		saParam.addParameterChangeWarningListener(warningListener);
		pgaParam.addParameterChangeWarningListener(warningListener);
		pgvParam.addParameterChangeWarningListener(warningListener);

		// Put parameters in the supportedIMParams list:
		supportedIMParams.clear();
		supportedIMParams.addParameter(saParam);
		supportedIMParams.addParameter(pgaParam);
		supportedIMParams.addParameter(pgvParam);	

	}
	/**
	 * This sets the eqkRupture related parameters (magParam and fltTypeParam)
	 * based on the eqkRupture passed in. The internally held eqkRupture object
	 * is also set as that passed in. Warning constrains are ignored.
	 * 
	 * @param eqkRupture -  The new eqkRupture value
	 * @throws InvalidRangeException - If not valid rake angle
	 */
	public void setEqkRupture(EqkRupture eqkRupture) throws InvalidRangeException {
		magParam.setValueIgnoreWarning(new Double(eqkRupture.getMag()));
		setFaultTypeFromRake(eqkRupture.getAveRake());
		this.eqkRupture = eqkRupture;
		setPropagationEffectParams();
	}
	
    /**
     * Determines the style of faulting from the rake angle. Their report is not
     * explicit, so these ranges come from an email that told us to decide, but
     * that within 30-degrees of horz for SS was how the NGA data were defined.
     * 
     * @param rake
     *            in degrees
     * @throws InvalidRangeException
     *             If not valid rake angle
     */
    protected void setFaultTypeFromRake(double rake)
            throws InvalidRangeException {
        if (rake <= 30 && rake >= -30)
            fltTypeParam.setValue(FLT_TYPE_STRIKE_SLIP);
        else if (rake <= -150 || rake >= 150)
            fltTypeParam.setValue(FLT_TYPE_STRIKE_SLIP);
        else if (rake > 30 && rake < 150)
            fltTypeParam.setValue(FLT_TYPE_REVERSE);
        else if (rake > -150 && rake < -30)
            fltTypeParam.setValue(FLT_TYPE_NORMAL);
        else
            throw new RuntimeException("No rake defined!");
    }
	  /**
	   * This calculates the JB Distance  propagation effect parameter based
	   * on the current site and eqkRupture. <P>
	   */
	  protected void setPropagationEffectParams() {

	    if ( (this.site != null) && (this.eqkRupture != null)) {
	      distanceJBParam.setValue(eqkRupture, site);
	    }
	  }

	/**
		 * This sets the site-related parameter (siteTypeParam) based on what is in
		 * the Site object passed in (the Site object must have a parameter with the
		 * same name as that in siteTypeParam). This also sets the internally held
		 * Site object as that passed in.
		 * @param site - The new site object
		 * @throws ParameterException - Thrown if the Site object doesn't contain a Vs30 parameter
		 */
		public void setSite(Site site) throws ParameterException {
			siteTypeParam.setValue((String) site.getParameter(SITE_TYPE_NAME).getValue());
			this.site = site;
			setPropagationEffectParams();
		}
		/**
		 * This sets the coefficient index. This index is used to get from the arrays initialized at 
		 * the beginning of this class the parameters necessary to calculate the  
		 * @throws ParameterException
		 */
		protected void setCoeffIndex() throws ParameterException {
			//
			if (im == null){
				throw new ParameterException(C+": updateCoefficients():"+"The Intensity Measure"+ 
						" Parameter has not been set yet, unable to process");
			}
			// 
			if (im.getName().equalsIgnoreCase(PGV_Param.NAME)) {
				iper = 0;
			} else if (im.getName().equalsIgnoreCase(PGA_Param.NAME)) {
				iper = 1;
			} else {
			// Note: this gives the index of the period contained in the period array populated at 
			// the beginning of this class. 
				iper = ((Integer) indexFromPerHashMap.get(saPeriodParam.getValue())).intValue();
			}
			parameterChange = true;
			intensityMeasureChanged = false;
		}
		/**
		 * This returns the mean ground motion value given Earthquake Rupture and Site 
		 */
		public double getMean(){
			if (rJB > USER_MAX_DISTANCE) return VERY_SMALL_MEAN;
			if (intensityMeasureChanged) {
				setCoeffIndex();
			};
			return getMean (iper, mag, rJB, siteType, fltType);
		}
		/**
		 * This computes the mean ln(Y) 
		 * @param iper
		 * @param rJB
		 * @param mag
		 */
		public double getMean(int iper, double mag, double rJB, String siteType, String fltType){
			//
			double logY, mean = 0;
			double ss;
			double sa;
			double fn;
			double fr;
			double soil_factor = 0.00;
			double flt_factor = 0.00;
			if (siteType.equals(SITE_TYPE_ROCK)) {
				if (D) System.out.println("Rock");
				ss = 0;
				sa = 0;
				soil_factor =0.00;
			} else if (siteType.equals(SITE_TYPE_STIFF_SOIL)) {
				if (D) System.out.println("Stiff soil");
				ss = 0;
				sa = 1;
				soil_factor = sa*b8[iper];
			} else if (siteType.equals(SITE_TYPE_SOFT_SOIL)) {
				if (D) System.out.println("Soft soil");
				ss = 1;
				sa = 0;
				soil_factor = ss*b7[iper];

			} else {
				throw new RuntimeException("\n  Unrecognized site type \n");
			}
			if (fltType.equals(FLT_TYPE_NORMAL)) {
				if (D) System.out.println("Normal Style-of-Faulting");
				fn = 1;
				fr = 0;
				flt_factor = fn* b9[iper];
			} else if (fltType.equals(FLT_TYPE_REVERSE)) {
				if (D) System.out.println("Reverse Style-of-Faulting");
				fn = 0;
				fr = 1;
				flt_factor = fr*b10[iper];
			} else if (fltType.equals(FLT_TYPE_STRIKE_SLIP)) {
				if (D) System.out.println("Strike Slip Style-of-Faulting");
				fn = 0;
				fr = 0;
				flt_factor = 0.00;
		
			} else {
				System.out.println("+++"+fltType.toString()+"--");
				throw new RuntimeException("\n  Cannot handle this combination: Style of faulting ");
			}
			
		// computation for PGV(iper=0) PGA (iper=1) log10(Y) [cm/s2 or cm/s]
		logY = b1[iper] + b2[iper] * mag + b3[iper] * (mag*mag) + (b4[iper]+b5[iper]*mag)*Math.log10(Math.sqrt(rJB * rJB + b6[iper]*b6[iper])) + 
		        + soil_factor + flt_factor;
       // convert to log10 to ln
        logY*=log2ln;
		 if (im.getName().equals(PGV_Param.NAME) && iper == 0) {
			 mean = Math.exp(logY);
		 }
		 else mean = Math.exp(logY)/981;
		 // return the result
		 return Math.log(mean);

//	    return mean;
	  }
		
		/**
		 *  St.Dev
		 */
		public double getStdDev() {
			if (intensityMeasureChanged){
				setCoeffIndex();
			}
			return getStdDev(iper, stdDevType);
		}
		
		public double getStdDev(int iper, String stdDevType) {
		    if(stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_NONE))
					  return 0;
			else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_INTER))
			    	  return log2ln*sig2[iper];
			else if (stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_INTRA))
					  return log2ln*sig1[iper];
			else if(stdDevType.equals(StdDevTypeParam.STD_DEV_TYPE_TOTAL))
			    	  return log2ln*sigtot[iper];
		    else 
					  return Double.NaN;
			}
		/**
		 * get the name of this IMR
		 *
		 * @returns the name of this IMR
		 */
		public String getName() {
			return NAME;
		}

		/**
		 * Returns the Short Name of each AttenuationRelationship
		 * @return String
		 */
		public String getShortName() {
			return SHORT_NAME;
		}
		/**
		 * Allows the user to set the default parameter values for the selected ground motion prediction equation
		 */
		public void setParamDefaults() {

			magParam.setValueAsDefault();
			fltTypeParam.setValueAsDefault();
			tectonicRegionTypeParam.setValueAsDefault();
			distanceJBParam.setValueAsDefault();
			saParam.setValueAsDefault();
			saPeriodParam.setValueAsDefault();
			saDampingParam.setValueAsDefault();
			pgaParam.setValueAsDefault();
			pgvParam.setValueAsDefault();
			stdDevTypeParam.setValueAsDefault();
			siteTypeParam.setValue(SITE_TYPE_DEFAULT);

			mag = ((Double) magParam.getValue()).doubleValue();
			rJB = ((Double) distanceJBParam.getValue()).doubleValue();
			fltType = fltTypeParam.getValue().toString();
			siteType = siteTypeParam.getValue().toString();
		}

		/**
		 * This listens for parameter changes and updates the primitive parameters accordingly
		 * @param e ParameterChangeEvent
		 */
		public void parameterChange(ParameterChangeEvent e) {

			String pName = e.getParameterName();
			Object val = e.getNewValue();
			if (pName.equals(DistanceJBParameter.NAME)) {
				rJB = ( (Double) val).doubleValue();
			}
			else if (pName.equals(MagParam.NAME)) {
				mag = ( (Double) val).doubleValue();
			}
			else if (pName.equals(StdDevTypeParam.NAME)) {
				stdDevType = (String) val;
			}
			else if (pName.equals(SITE_TYPE_NAME)) {
				siteType = (String)siteTypeParam.getValue();
			}
			else if (pName.equals(FaultTypeParam.NAME)) {
				fltType = (String)fltTypeParam.getValue();
			}
	
			else if (pName.equals(PeriodParam.NAME)) {
				intensityMeasureChanged = true;
			}
		}		
		/**
		 * Allows to reset the change listeners on the parameters
		 */
		public void resetParameterEventListeners(){
			magParam.removeParameterChangeListener(this);
			fltTypeParam.removeParameterChangeListener(this);
			siteTypeParam.removeParameterChangeListener(this);
			distanceJBParam.removeParameterChangeListener(this);
			stdDevTypeParam.removeParameterChangeListener(this);
			saPeriodParam.removeParameterChangeListener(this);
			this.initParameterEventListeners();
		}
		/**
		 * Adds the parameter change listeners. This allows to listen to when-ever the
		 * parameter is changed.
		 */
		protected void initParameterEventListeners() {
			if (D) System.out.println("--- initParameterEventListeners begin");
			
			magParam.addParameterChangeListener(this);
			fltTypeParam.addParameterChangeListener(this);
			siteTypeParam.addParameterChangeListener(this);
			distanceJBParam.addParameterChangeListener(this);
			stdDevTypeParam.addParameterChangeListener(this);
			saPeriodParam.addParameterChangeListener(this);
			
			if (D) System.out.println("--- initParameterEventListeners end");
		}
		// prepare Table
		public static void main(String[] args) {
			AkB_2010_AttenRel  ar = new AkB_2010_AttenRel(null);
			ar.setIntensityMeasure(PGV_Param.NAME);
			ar.setIntensityMeasure(PGA_Param.NAME);
			System.out.print(Math.exp(ar.getMean(0, 5.00,  1.00, SITE_TYPE_ROCK, FLT_TYPE_STRIKE_SLIP)));
			System.out.print(" ");
			System.out.print(Math.exp(ar.getMean(0, 5.00,  1.74, SITE_TYPE_ROCK, FLT_TYPE_STRIKE_SLIP)));
			System.out.print(" ");
			System.out.print(Math.exp(ar.getMean(0, 5.00,  3.04, SITE_TYPE_ROCK, FLT_TYPE_STRIKE_SLIP)));
			System.out.print(" ");
			System.out.print(Math.exp(ar.getMean(0, 5.00,  5.31, SITE_TYPE_ROCK, FLT_TYPE_STRIKE_SLIP)));
			System.out.print(" ");
			System.out.print(Math.exp(ar.getMean(0, 5.00,  9.27, SITE_TYPE_ROCK, FLT_TYPE_STRIKE_SLIP)));
			System.out.print(" ");
			System.out.print(Math.exp(ar.getMean(0, 5.00, 16.18, SITE_TYPE_ROCK, FLT_TYPE_STRIKE_SLIP)));
			System.out.print(" ");
			System.out.print(Math.exp(ar.getMean(0, 5.00, 28.23, SITE_TYPE_ROCK, FLT_TYPE_STRIKE_SLIP)));
			System.out.print(" ");
			System.out.print(Math.exp(ar.getMean(0, 5.00, 49.26, SITE_TYPE_ROCK, FLT_TYPE_STRIKE_SLIP)));
			System.out.print(" ");
			System.out.print(Math.exp(ar.getMean(0, 5.00, 85.96, SITE_TYPE_ROCK, FLT_TYPE_STRIKE_SLIP)));
			System.out.print(" ");
			System.out.print(Math.exp(ar.getMean(0, 5.00, 150.0, SITE_TYPE_ROCK, FLT_TYPE_STRIKE_SLIP)));
			System.out.println();

		}	
}
