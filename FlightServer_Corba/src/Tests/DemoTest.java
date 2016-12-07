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

public class DemoTest {
	ORB orb = null;
	FlightServerInterface server = null;
	String[] args = new String[4];
	org.omg.CORBA.Object objRef = null;
	NamingContextExt ncRef = null;

	@Before
	public void setUp() throws Exception {

		args[0] = "-ORBInitialPort";
		args[1] = "1050";
		args[2] = "-ORBInitialHost";
		args[3] = "localhost";

		String serverName = "WSL";

		orb = ORB.init(args, null);
		objRef = orb.resolve_initial_references("NameService");
		ncRef = NamingContextExtHelper.narrow(objRef);
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

		String serverName = "MTL";

		orb = ORB.init(args, null);
		objRef = orb.resolve_initial_references("NameService");
		ncRef = NamingContextExtHelper.narrow(objRef);
		server = (FlightServerInterface) FlightServerInterfaceHelper.narrow(ncRef.resolve_str(serverName));

		String m1 = server.editFlightRecord("addFlight", "",
				"Washington" + "|" + "MTL0001," + "7-11-2016" + "," + "" + "," + "5" + "," + "5" + "," + "5");

		assertArrayEquals(response, m1.split(","));

		// ================================
		serverName = "NDH";

		orb = ORB.init(args, null);
		objRef = orb.resolve_initial_references("NameService");
		ncRef = NamingContextExtHelper.narrow(objRef);
		server = (FlightServerInterface) FlightServerInterfaceHelper.narrow(ncRef.resolve_str(serverName));

		String n1 = server.editFlightRecord("addFlight", "",
				"Washington" + "|" + "NDH0001," + "7-11-2016" + "," + "" + "," + "5" + "," + "5" + "," + "5");

		assertArrayEquals(response, n1.split(","));

		// ==================================

		serverName = "MTL";

		orb = ORB.init(args, null);
		objRef = orb.resolve_initial_references("NameService");
		ncRef = NamingContextExtHelper.narrow(objRef);
		server = (FlightServerInterface) FlightServerInterfaceHelper.narrow(ncRef.resolve_str(serverName));

		response = new String[2];
		response[0] = "Success";
		response[1] = "Your flight has been successfully booked.";

		String b1 = server.bookFlight("Muhammad", "Umer", "2250 Rue Guy", "342224352", "Washington", "7-11-2016",
				"economy");
		// String b2 = server.bookFlight("Jaya", "Kumar", "Concordia Uni",
		// "342224352", "New Delhi", "23-22-22", "business");
		// String b3 = server.bookFlight("Kashif", "Habib", "Johar town",
		// "7654324", "Montreal", "23-22-22", "economy");

		assertArrayEquals(response, b1.split(","));

		// ==================================

		serverName = "NDH";

		orb = ORB.init(args, null);
		objRef = orb.resolve_initial_references("NameService");
		ncRef = NamingContextExtHelper.narrow(objRef);
		server = (FlightServerInterface) FlightServerInterfaceHelper.narrow(ncRef.resolve_str(serverName));

		response = new String[2];
		response[0] = "Success";
		response[1] = "Your flight has been successfully booked.";

		String p1 = server.bookFlight("Muhammad", "Kashif", "2250 Rue Guy", "342224352", "Washington", "7-11-2016",
				"economy");
		// String b2 = server.bookFlight("Jaya", "Kumar", "Concordia Uni",
		// "342224352", "New Delhi", "23-22-22", "business");
		// String b3 = server.bookFlight("Kashif", "Habib", "Johar town",
		// "7654324", "Montreal", "23-22-22", "economy");

		assertArrayEquals(response, p1.split(","));

		// ==================================
		
		//TRANSFER
		
		response[0] = "Success";
		response[1] = "You passenger with PASSENGERID=1 reservation has been successfully transfered to new delhi";

		String t1 = server.transferReservation("1", "NDH1111", "Montreal");
		System.out.println(t1);
		//assertArrayEquals(response, t1.split(","));
		
		
		serverName = "WSL";
		
		orb = ORB.init(args, null);
	    org.omg.CORBA.Object objRef =   orb.resolve_initial_references("NameService");
	    NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	    server = (FlightServerInterface) FlightServerInterfaceHelper.narrow(ncRef.resolve_str(serverName));
	    
	    System.out.println(server.getBookedFlightCount("WSL0001").trim());
		
		
	}

}
