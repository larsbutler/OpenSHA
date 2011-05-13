package org.opensha.sha.earthquake.rupForecastImpl.GEM1;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opensha.sha.earthquake.rupForecastImpl.GEM1.SourceData.GEMSourceData;

public class GEM1ERFTest {
	private ArrayList<GEMSourceData> srcList;
	
	@Before
	public void setUp(){
		srcList = new ArrayList<GEMSourceData>();
		srcList.add(GEM1ERFTestHelper.getAreaSourceData());
		srcList.add(GEM1ERFTestHelper.getFaultSourceData());
		srcList.add(GEM1ERFTestHelper.getPointSourceData());
		srcList.add(GEM1ERFTestHelper.getSubductionFaultSourceData());
	}
	
	@After
	public void tearDown(){
		srcList = null;
	}

	@Test
	public void testGetNumSources() {
		GEM1ERF erf = new GEM1ERF(srcList);
		assertEquals(4, erf.getNumSources(),0);
	}

	@Test
	public void testGetSource() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSourceList() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetIncludedTectonicRegionTypes() {
		fail("Not yet implemented");
	}

	@Test
	public void testGEM1ERF() {
		fail("Not yet implemented");
	}

	@Test
	public void testGEM1ERFArrayListOfGEMSourceDataArrayListOfGEMSourceDataArrayListOfGEMSourceDataArrayListOfGEMSourceData() {
		fail("Not yet implemented");
	}

	@Test
	public void testGEM1ERFArrayListOfGEMSourceData() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetGEM1ERF() {
		fail("Not yet implemented");
	}

	@Test
	public void testParseSourceListIntoDifferentTypes() {
		fail("Not yet implemented");
	}

	@Test
	public void testInitialize() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateParamList() {
		fail("Not yet implemented");
	}

	@Test
	public void testMkFaultSource() {
		fail("Not yet implemented");
	}

	@Test
	public void testMkSubductionSource() {
		fail("Not yet implemented");
	}

	@Test
	public void testMkAreaSource() {
		fail("Not yet implemented");
	}

	@Test
	public void testMkGridSource() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetName() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateForecast() {
		fail("Not yet implemented");
	}

}
