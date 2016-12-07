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

public class ServerTest {

	ORB orb = null;
	FlightServerInterface server = null;
	@Before
	public void setUp() throws Exception {
		String[] args = new String[4];
		args[0] = "-ORBInitialPort";
		args[1] = "1050";
		args[2] = "-ORBInitialHost";
		args[3] = "localhost";
		
		String serverName = "WSL";
		
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
		
		//addFlights();
		bookFlights();
		
	}
	
	private void bookFlights()
	{
		String[] response = new String[2];
		response[0] = "Success";
		response[1] = "Your flight has been successfully booked.";
		
		String b1 = server.bookFlight("Muhammad", "Umer", "2250 Rue Guy", "342224352", "Montreal", "22-22-22", "1");
		String b2 = server.bookFlight("Jaya", "Kumar", "Concordia Uni", "342224352", "New Delhi", "23-22-22", "2");
		String b3 = server.bookFlight("Kashif", "Habib", "Johar town", "7654324", "Montreal", "23-22-22", "1");
		
		assertArrayEquals(response, b1.split(","));
		assertArrayEquals(response, b2.split(","));
		assertArrayEquals(response, b3.split(","));
		
	}
	
	private void addFlights()
	{
		String[] response = new String[2];
		response[0] = "Success";
		response[1] = "Flight has been successfully added into our records.";
		
		String a1 = server.editFlightRecord("addFlight", "",
				"Montreal" + "|" + "WSL0001," + "22-22-22" + "," + "" + ","
						+ "5" + "," + "5" + "," + "5");
		
		String a2 = server.editFlightRecord("addFlight", "",
				"New Delhi" + "|" + "WSL0001," + "23-22-22" + "," + "" + ","
						+ "8" + "," + "2" + "," + "3");
		
		String a3 = server.editFlightRecord("addFlight", "",
				"Montreal" + "|" + "WSL0001," + "23-22-22" + "," + "" + ","
						+ "3" + "," + "6" + "," + "3");
		
		
		assertArrayEquals(response, a1.split(","));
		assertArrayEquals(response, a2.split(","));
		assertArrayEquals(response, a3.split(","));
		
	}

}
