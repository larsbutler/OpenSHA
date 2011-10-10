package org.opensha.sha.magdist;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.opensha.sha.magdist.GutenbergRichterMagFreqDist;


public class GutenbergRichterMagFreqDistTest {

    @Test
    public void CheckIncrementMagUpper() {
    	double TCR = 123;
    	double bValue = 2;
    	double min = 10;
    	double max = 20;
    	int numValues = 11;
    	double TMR, a;

    	GutenbergRichterMagFreqDist mfd = new GutenbergRichterMagFreqDist(bValue, TCR, min, max, numValues);
    	assertEquals(20, mfd.getMagUpper(), 1e-5);
    	assertEquals(10, mfd.getMagLower(), 1e-5);
    	assertEquals(11, mfd.getNum());
    	assertEquals(1, mfd.getDelta(), 1e-5);
    	assertEquals(TCR, mfd.getTotCumRate(), 1e-5);
    	TMR = mfd.getTotalMomentRate();
    	a = mfd.get_aValue();

    	mfd.incrementMagUpper(2.4);
    	
    	assertEquals(22, mfd.getMagUpper(), 1e-5);
    	assertEquals(10, mfd.getMagLower(), 1e-5);
    	assertEquals(13, mfd.getNum());
    	assertEquals(1, mfd.getDelta(), 1e-5);
    	
    	// b and TMR should not change
    	assertEquals(2, mfd.get_bValue(), 1e-5);
    	assertEquals(TMR, mfd.getTotalMomentRate(), 1e15);

    	// bringing mMax to the same value of 20
    	mfd.incrementMagUpper(-1.55);
    	
    	assertEquals(20, mfd.getMagUpper(), 1e-5);
    	assertEquals(10, mfd.getMagLower(), 1e-5);
    	assertEquals(11, mfd.getNum());
    	assertEquals(1, mfd.getDelta(), 1e-5);
    	assertEquals(2, mfd.get_bValue(), 1e-5);
    	assertEquals(TMR, mfd.getTotalMomentRate(), 1e15);
    	assertEquals(TCR, mfd.getTotCumRate(), 1e-5);
    	assertEquals(a, mfd.get_aValue(), 0);
    }
    
    @Test
    public void CheckSetMagUpper() {
    	double TCR = 3;
    	double b = 4;
    	double min = 2;
    	double max = 5;
    	int numValues = 81;
    	double TMR, a;
    	double delta = (max - min) / (numValues - 1);

    	GutenbergRichterMagFreqDist mfd = new GutenbergRichterMagFreqDist(b, TCR, min, max, numValues);
    	TMR = mfd.getTotalMomentRate();
    	a = mfd.get_aValue();

    	mfd.setMagUpper(3.7);
    	assertEquals(2, mfd.getMagLower(), 0);
    	assertEquals(3.6875, mfd.getMagUpper(), 0);
    	assertEquals(delta, mfd.getDelta(), 0);
    	assertEquals(46, mfd.getNum());

    	// checks that rates in the mfd object are consistent with the
    	// original data
    	for (int i = 0; i < mfd.getNum(); i++) {
    		double computedOccRate = mfd.getY(i);
    		double expectedOccRate = Math.pow(10, a - b * (mfd.getX(i) - delta / 2))
    								 - Math.pow(10, a - b * (mfd.getX(i) + delta / 2));
    		assertEquals(expectedOccRate, computedOccRate, 1e-5);
    	}

    	// return to the same value
    	mfd.setMagUpper(max);
    	assertEquals(5, mfd.getMagUpper(), 0);
    	assertEquals(4, mfd.get_bValue(), 1e-5);
    	assertEquals(TMR, mfd.getTotalMomentRate(), 1e15);
    	assertEquals(a, mfd.get_aValue(), 1e-2);
    	assertEquals(TCR, mfd.getTotCumRate(), 1e-1);
    }
    
    @Test
    public void CheckGetAValue() {
    	double a = 14.4;
    	double b = 4.5;
    	double min = 3;
    	double max = 6;
    	int numValues = 250;

    	double delta = (max - min + 1) / numValues;
    	assertEquals(delta, 0.016, 1e-3);
    	
    	double TCR = Math.pow(10, a - b * (min - delta/2)) - Math.pow(10, a - b * (max + delta/2));
    	// TCR = 10 ^ (a - b * (min - delta/2)) - 10 ^ (a - b * (max + delta/2))
    	// 8.63 = 10 ^ (14.4 - (4.5 * (3 - 8e-3))) - 10 ^ (14.47 - (4.5 * (6 + 8e-3)))    	
    	assertEquals(TCR, 8.63, 1e-2);
    	
    	GutenbergRichterMagFreqDist mfd = new GutenbergRichterMagFreqDist(b, TCR, min, max, numValues);
    	assertEquals(14.4, mfd.get_aValue(), 1e-2);
    }
    
    @Test
    public void CheckSetAB() {
    	double TCR = 12;
    	double bValue = 7.3;
    	double min = 5;
    	double max = 7;
    	int numValues = 100;

    	GutenbergRichterMagFreqDist mfd = new GutenbergRichterMagFreqDist(bValue, TCR, min, max, numValues);
    	mfd.setAB(22, 15);
    	
    	assertEquals(22, mfd.get_aValue(), 1e-2);
    	assertEquals(15, mfd.get_bValue(), 0);
    	
    	// mMax, mMin and delta should stay the same
    	assertEquals(5, mfd.getMagLower(), 0);
    	assertEquals(7, mfd.getMagUpper(), 0);
    	assertEquals(0.02, mfd.getDelta(), 1e-3);
    }

    @Test
    public void CheckIncrementB() {
    	double TCR = 5;
    	double bValue = 2.2;
    	double min = 1;
    	double max = 4;
    	int numValues = 4;

    	GutenbergRichterMagFreqDist mfd = new GutenbergRichterMagFreqDist(bValue, TCR, min, max, numValues);
    	double oldTMR = mfd.getTotalMomentRate();
    	mfd.incrementB(-0.4);

    	assertEquals(2.2 - 0.4, mfd.get_bValue(), 0);
    	
    	// mMax, mMin, delta and TMR should stay the same
    	assertEquals(1, mfd.getMagLower(), 0);
    	assertEquals(4, mfd.getMagUpper(), 0);
    	assertEquals(1, mfd.getDelta(), 0);
    	assertEquals(oldTMR, mfd.getTotalMomentRate(), 1e8);
    }
}
