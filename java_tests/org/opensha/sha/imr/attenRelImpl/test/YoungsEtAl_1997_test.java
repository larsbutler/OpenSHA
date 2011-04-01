package org.opensha.sha.imr.attenRelImpl.test;

import org.opensha.sha.imr.attenRelImpl.YoungsEtAl_1997_AttenRel;

/**
 * Class providing methods for testing {@link YoungsEtAl_1997_AttenRel}.
 */
public class YoungsEtAl_1997_test {
	
	/** Youngs et al. 1997 GMPE */
	private YoungsEtAl_1997_AttenRel youngsEtAl1997AttenRel= null;
	
	/** Table for peak ground acceleration interface test. */
	private static final String PGA_INTERFACE_TABLE_FILE =
		"Youngs-interface-PGA-g.dat";
	
	/** Table for spectral acceleration interface test. */
	private static final String SA_INTERFACE_TABLE_FILE =
		"Youngs-interface-1Hz-g.dat";
	
	/** Table for peak ground acceleration intraslab test. */
	private static final String PGA_INTRASLAB_TABLE_FILE =
		"Youngs-intraslab-PGA-g.dat";
	
	/** Table for spectral acceleration intraslab test. */
	private static final String SA_INTRASLAB_TABLE_FILE =
		"Youngs-intraslab-1Hz-g.dat";
}
