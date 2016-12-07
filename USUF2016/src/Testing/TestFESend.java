package Testing;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import FE.FEBookingImpl;
import Models.Enums;
import Models.UDPMessage;


public class TestFESend {
	
	FEBookingImpl bookingObject = null;
	
	@BeforeClass 
	public static void beforeClass() {
		System.out.println("Before Class Test FE Send");		
	}
	@AfterClass 
	public static void  afterClass() {
		System.out.println("After Class Test FE Send");
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
	
	//Test when 3 server sends the result, and 1 doesn't
	@Test
	public void testSend() {
		//System.out.println("testSend");
		
		System.out.println("Create a Flight");
		UDPMessage udpMsg = new UDPMessage(Enums.UDPSender.FrontEnd, -1, Enums.getFlightCitiesFromString("Montreal"),
				Enums.Operations.editFlightRecord, Enums.UDPMessageType.Request);
		HashMap<String, String> parameterMap = new HashMap<String, String>();		
		parameterMap.put("recordID", "NONE:MTL1113");
		parameterMap.put("fieldName", "createFlight");
		parameterMap.put("newValue", "10:10:10:2016/12/20:14;23:Montreal");
		udpMsg.setManagerID("MTL1113");
		udpMsg.setParamters(parameterMap);
		udpMsg.setFrontEndPort(-1);				
		String result =  bookingObject.send(udpMsg).trim();
		
		
//		udpMsg = new UDPMessage(Enums.UDPSender.FrontEnd, -1, Enums.getFlightCitiesFromString("Montreal"),
//				Enums.Operations.bookFlight, Enums.UDPMessageType.Request);
//		parameterMap = new HashMap<String, String>();
//		parameterMap.put("firstName", "Ulan");
//		parameterMap.put("lastName", "Baitassov");
//		parameterMap.put("address", "Verdun");
//		parameterMap.put("phone", "5145606164");
//		parameterMap.put("destination", "Washington");
//		parameterMap.put("date", "02/12/2016");
//		parameterMap.put("classFlight", "economy");
//		udpMsg.setParamters(parameterMap);		
//		udpMsg.setManagerID("-1");		
//		udpMsg.setFrontEndPort(-1);		
		
		result = bookingObject.send(udpMsg).trim();
		
		System.out.println("result:" +result);
		assertTrue(result.equalsIgnoreCase("Ulan"));
	}
	
}
