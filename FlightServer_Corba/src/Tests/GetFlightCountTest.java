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

public class GetFlightCountTest {

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
		System.out.println(server.getBookedFlightCount("all:WSL0001").trim());
	}

}
