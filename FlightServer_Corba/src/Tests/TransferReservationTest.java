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

public class TransferReservationTest {

	ORB orb = null;
	FlightServerInterface server = null;
	String[] args = new String[4];
	@Before
	public void setUp() throws Exception {
		
		args[0] = "-ORBInitialPort";
		args[1] = "1050";
		args[2] = "-ORBInitialHost";
		args[3] = "localhost";

		String serverName = "NDH";

		orb = ORB.init(args, null);
		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
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
		response[1] = "You passenger with PASSENGERID=1 reservation has been successfully transfered to new delhi";

		String t1 = server.transferReservation("1", "NDH1111", "Montreal");
		assertArrayEquals(response, t1.split(","));
	}

}
