package Tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import CInterface.FlightServerInterface;
import CInterface.FlightServerInterfaceHelper;

public class AddFlightTest {

	ORB orb = null;
	FlightServerInterface server = null;
	String[] args = new String[4];
	@Before
	public void setUp() throws Exception {
		
		args[0] = "-ORBInitialPort";
		args[1] = "1050";
		args[2] = "-ORBInitialHost";
		args[3] = "localhost";

		String serverName = "WSL";

		orb = ORB.init(args, null);
		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
		server = (FlightServerInterface) FlightServerInterfaceHelper.narrow(ncRef.resolve_str(serverName));

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		String[] response = new String[2];
		response[0] = "Success";
		response[1] = "Flight has been successfully added into our records.";

//		String a1 = server.editFlightRecord("addFlight", "",
//				"Montreal" + "|" + "WSL0001," + "22-22-22" + "," + "" + "," + "5" + "," + "5" + "," + "5");
//
//		String a2 = server.editFlightRecord("addFlight", "",
//				"New Delhi" + "|" + "WSL0001," + "23-22-22" + "," + "" + "," + "8" + "," + "2" + "," + "3");
//
//		String a3 = server.editFlightRecord("addFlight", "",
//				"Montreal" + "|" + "WSL0001," + "23-22-22" + "," + "" + "," + "3" + "," + "6" + "," + "3");
//
//		assertArrayEquals(response, a1.split(","));
//		assertArrayEquals(response, a2.split(","));
//		assertArrayEquals(response, a3.split(","));

		String serverName = "MTL";
//
		orb = ORB.init(args, null);
		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
		server = (FlightServerInterface) FlightServerInterfaceHelper.narrow(ncRef.resolve_str(serverName));
		
//		
//		String m1 = server.editFlightRecord("addFlight", "",
//				"Washington" + "|" + "MTL0001," + "7-11-2016" + "," + "" + "," + "5" + "," + "5" + "," + "5");
//
////		String m2 = server.editFlightRecord("addFlight", "",
////				"New Delhi" + "|" + "MTL0001," + "22-22-22" + "," + "" + "," + "8" + "," + "2" + "," + "3");
////
////		String m3 = server.editFlightRecord("addFlight", "",
////				"New Delhi" + "|" + "MTL0001," + "23-22-22" + "," + "" + "," + "3" + "," + "6" + "," + "3");
//
//		assertArrayEquals(response, m1.split(","));
		//assertArrayEquals(response, m2.split(","));
		//assertArrayEquals(response, m3.split(","));
		
		
		serverName = "NDH";

		orb = ORB.init(args, null);
		objRef = orb.resolve_initial_references("NameService");
		ncRef = NamingContextExtHelper.narrow(objRef);
		server = (FlightServerInterface) FlightServerInterfaceHelper.narrow(ncRef.resolve_str(serverName));
		
		
		String n1 = server.editFlightRecord("addFlight", "",
				"Washington" + "|" + "NDH0001," + "7-11-2016" + "," + "" + "," + "5" + "," + "5" + "," + "5");

//		String n2 = server.editFlightRecord("addFlight", "",
//				"Washington" + "|" + "NDH0001," + "22-22-22" + "," + "" + "," + "8" + "," + "2" + "," + "3");
//
//		String n3 = server.editFlightRecord("addFlight", "",
//				"Montreal" + "|" + "NDH0001," + "23-22-22" + "," + "" + "," + "3" + "," + "6" + "," + "3");

		assertArrayEquals(response, n1.split(","));
		//assertArrayEquals(response, n2.split(","));
		//assertArrayEquals(response, n3.split(","));
		
	}

}
