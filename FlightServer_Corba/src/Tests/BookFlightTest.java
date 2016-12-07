package Tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import CInterface.FlightServerInterface;
import CInterface.FlightServerInterfaceHelper;

public class BookFlightTest {

	ORB orb = null;
	FlightServerInterface server = null;
	@Before
	public void setUp() throws Exception {
		String[] args = new String[4];
		args[0] = "-ORBInitialPort";
		args[1] = "1050";
		args[2] = "-ORBInitialHost";
		args[3] = "localhost";
		
		String serverName = "NDH";
		
		orb = ORB.init(args, null);
	    org.omg.CORBA.Object objRef =   orb.resolve_initial_references("NameService");
	    NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	    server = (FlightServerInterface) FlightServerInterfaceHelper.narrow(ncRef.resolve_str(serverName));
	    
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		String[] response = new String[2];
		response[0] = "Success";
		response[1] = "Your flight has been successfully booked.";
		
		String b1 = server.bookFlight("Muhammad", "Kashif", "2250 Rue Guy", "342224352", "Washington", "7-11-2016", "economy");
		//String b2 = server.bookFlight("Jaya", "Kumar", "Concordia Uni", "342224352", "New Delhi", "23-22-22", "business");
		//String b3 = server.bookFlight("Kashif", "Habib", "Johar town", "7654324", "Montreal", "23-22-22", "economy");
		
		assertArrayEquals(response, b1.split(","));
		//assertArrayEquals(response, b2.split(","));
		//assertArrayEquals(response, b3.split(","));
	}

}
