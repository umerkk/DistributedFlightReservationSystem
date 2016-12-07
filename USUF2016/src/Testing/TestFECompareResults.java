package Testing;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import FE.FEBookingImpl;


public class TestFECompareResults {
	
	FEBookingImpl bookingObject = null;
	
	@BeforeClass 
	public static void beforeClass() {
		System.out.println("Before Class Test FE CompareResults");		
	}
	@AfterClass 
	public static void  afterClass() {
		System.out.println("After Class Test FE CompareResults");
	}
	
	@Before 
	public void before() {
		System.out.print("inside ");
		bookingObject = new FEBookingImpl();
	}
	
	@After
	public void after() {
		System.out.println("outside test ");
	}
	
	//CASES WHEN only 1 error , all messages are delivered
	
	@Test
	public void testCompareResults1() {
		System.out.println("testCompareResults1()");		
		String[][] new_resultInfo = new String[4][4];
		new_resultInfo[0][1] = "Sajjad";
		new_resultInfo[1][1] = "Ulan";
		new_resultInfo[2][1] = "Ulan";
		new_resultInfo[3][1] = "Ulan";
		String result = bookingObject.compareResults(new_resultInfo);
		System.out.println(result);
		assertEquals("Ulan",result);
	}
	
	
	@Test
	public void testCompareResults2() {
		System.out.println("testCompareResults2()");
		String[][] new_resultInfo = new String[4][4];
		new_resultInfo[0][1] = "Ulan";
		new_resultInfo[1][1] = "Sajjad";
		new_resultInfo[2][1] = "Ulan";
		new_resultInfo[3][1] = "Ulan";
		String result = bookingObject.compareResults(new_resultInfo);
		System.out.println(result);
		assertEquals("Ulan",result);	
	}
	
	@Test
	public void testCompareResults3() {
		System.out.println("testCompareResults3()");	
		String[][] new_resultInfo = new String[4][4];
		new_resultInfo[0][1] = "Ulan";
		new_resultInfo[1][1] = "Ulan";
		new_resultInfo[2][1] = "Sajjad";
		new_resultInfo[3][1] = "Ulan";
		String result = bookingObject.compareResults(new_resultInfo);
		System.out.println(result);
		assertEquals("Ulan",result);
	}
	
	@Test
	public void testCompareResults4() {
		System.out.println("testCompareResults4()");	
		String[][] new_resultInfo = new String[4][4];
		new_resultInfo[0][1] = "Ulan";
		new_resultInfo[1][1] = "Ulan";
		new_resultInfo[2][1] = "Ulan";
		new_resultInfo[3][1] = "Sajjad";
		String result = bookingObject.compareResults(new_resultInfo);
		System.out.println(result);
		assertEquals("Ulan",result);
	}
	
	//CASES WHEN 1 error and 1 no reply
	
	@Test
	public void testCompareResults5() {
		System.out.println("testCompareResults5()");
		String[][] new_resultInfo = new String[4][4];
		new_resultInfo[0][1] = "X"; //no reply
		new_resultInfo[1][1] = "Sajjad"; //wrong answer
		new_resultInfo[2][1] = "Ulan";
		new_resultInfo[3][1] = "Ulan";
		String result = bookingObject.compareResults(new_resultInfo);
		System.out.println(result);
		assertEquals("Ulan",result);
	}
	
	@Test
	public void testCompareResults6() {
		System.out.println("testCompareResults6()");	
		String[][] new_resultInfo = new String[4][4];
		new_resultInfo[0][1] = "X"; // no reply
		new_resultInfo[1][1] = "Ulan"; 
		new_resultInfo[2][1] = "Sajjad";
		new_resultInfo[3][1] = "Ulan";
		String result = bookingObject.compareResults(new_resultInfo);
		System.out.println(result);
		assertEquals("Ulan",result);
	}
	
	@Test
	public void testCompareResults7() {
		System.out.println("testCompareResults7()");
		String[][] new_resultInfo = new String[4][4];
		new_resultInfo[0][1] = "X"; //no reply
		new_resultInfo[1][1] = "Ulan"; 
		new_resultInfo[2][1] = "Ulan";
		new_resultInfo[3][1] = "Sajjad";
		String result = bookingObject.compareResults(new_resultInfo);
		System.out.println(result);
		assertEquals("Ulan",result);
	}
	
	@Test
	public void testCompareResults8() {
		System.out.println("testCompareResults8()");
		String[][] new_resultInfo = new String[4][4];
		new_resultInfo[0][1] = "Sajjad";
		new_resultInfo[1][1] = "X"; //no reply
		new_resultInfo[2][1] = "Ulan";
		new_resultInfo[3][1] = "Ulan";
		String result = bookingObject.compareResults(new_resultInfo);
		System.out.println(result);
		assertEquals("Ulan",result);	
	}
	
	@Test
	public void testCompareResults9() {
		System.out.println("testCompareResults9()");
		String[][] new_resultInfo = new String[4][4];
		new_resultInfo[0][1] = "Ulan";
		new_resultInfo[1][1] = "X"; //no reply
		new_resultInfo[2][1] = "Sajjad";
		new_resultInfo[3][1] = "Ulan";
		String result = bookingObject.compareResults(new_resultInfo);
		System.out.println(result);
		assertEquals("Ulan",result);
	}
	
	@Test
	public void testCompareResults10() {
		System.out.println("testCompareResults10()");
		String[][] new_resultInfo = new String[4][4];
		new_resultInfo[0][1] = "Ulan";
		new_resultInfo[1][1] = "X"; //no reply
		new_resultInfo[2][1] = "Ulan";
		new_resultInfo[3][1] = "Sajjad";
		String result = bookingObject.compareResults(new_resultInfo);
		System.out.println(result);
		assertEquals("Ulan",result);
	}
	
	@Test
	public void testCompareResults11() {
		System.out.println("testCompareResults11()");
		String[][] new_resultInfo = new String[4][4];
		new_resultInfo[0][1] = "Sajjad";
		new_resultInfo[1][1] = "Ulan"; 
		new_resultInfo[2][1] = "X"; //no reply
		new_resultInfo[3][1] = "Ulan";
		String result = bookingObject.compareResults(new_resultInfo);
		System.out.println(result);
		assertEquals("Ulan",result);
	}
	
	@Test
	public void testCompareResults12() {
		System.out.println("testCompareResults12()");
		String[][] new_resultInfo = new String[4][4];
		new_resultInfo[0][1] = "Ulan";
		new_resultInfo[1][1] = "Sajjad"; 
		new_resultInfo[2][1] = "X"; //no reply
		new_resultInfo[3][1] = "Ulan";
		String result = bookingObject.compareResults(new_resultInfo);
		System.out.println(result);
		assertEquals("Ulan",result);
	}
	
	@Test
	public void testCompareResults13() {
		System.out.println("testCompareResults13()");
		String[][] new_resultInfo = new String[4][4];
		new_resultInfo[0][1] = "Ulan";
		new_resultInfo[1][1] = "Ulan"; 
		new_resultInfo[2][1] = "X"; //no reply
		new_resultInfo[3][1] = "Sajjad";
		String result = bookingObject.compareResults(new_resultInfo);
		System.out.println(result);
		assertEquals("Ulan",result);
	}
	
	@Test
	public void testCompareResults14() {
		System.out.println("testCompareResults14()");
		String[][] new_resultInfo = new String[4][4];
		new_resultInfo[0][1] = "Sajjad";
		new_resultInfo[1][1] = "Ulan"; 
		new_resultInfo[2][1] = "Ulan"; 
		new_resultInfo[3][1] = "X"; //no reply
		String result = bookingObject.compareResults(new_resultInfo);
		System.out.println(result);
		assertEquals("Ulan",result);
	}
	
	@Test
	public void testCompareResults15() {
		System.out.println("testCompareResults15()");
		String[][] new_resultInfo = new String[4][4];
		new_resultInfo[0][1] = "Ulan";
		new_resultInfo[1][1] = "Sajjad"; 
		new_resultInfo[2][1] = "Ulan"; 
		new_resultInfo[3][1] = "X"; //no reply
		String result = bookingObject.compareResults(new_resultInfo);
		System.out.println(result);
		assertEquals("Ulan",result);
	}
	
	@Test
	public void testCompareResults16() {
		System.out.println("testCompareResults16()");
		String[][] new_resultInfo = new String[4][4];
		new_resultInfo[0][1] = "Ulan";
		new_resultInfo[1][1] = "Ulan"; 
		new_resultInfo[2][1] = "Sajjad"; 
		new_resultInfo[3][1] = "X"; //no reply
		String result = bookingObject.compareResults(new_resultInfo);
		System.out.println(result);
		assertEquals("Ulan",result);
	}
	
}
