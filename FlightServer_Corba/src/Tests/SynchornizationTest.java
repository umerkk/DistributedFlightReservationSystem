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

public class SynchornizationTest {
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
	public void test() throws InterruptedException {

		Thread t1 = new Thread(new Runnable() {
			public void run() {
				String[] response = new String[2];
				response[0] = "Success";
				response[1] = "Your flight has been successfully booked.";

				String b1 = server.bookFlight("Muhammad", "Umer", "2250 Rue Guy", "342224352", "Montreal", "88-44-44",
						"economy");
				assertArrayEquals(response, b1.split(","));
			}
		});
		
		Thread t2 = new Thread(new Runnable() {
			public void run() {
				String[] response = new String[2];
				response[0] = "Success";
				response[1] = "Your flight has been successfully booked.";
				

				String fieldName = "deptDate";
				int recordId = 1;
				String newValue = "88-44-44|WSL1111";
				
				String[] fieldX = "deptDate,1".split(",");
				//String recordIdX = fieldX[1].trim();
				String fieldNamex = fieldX[0].trim();
				
				String b1 =server.editFlightRecord("editFlightRecord", fieldName + "," + String.valueOf(recordId), newValue);
				
				newValue = newValue.substring(0, newValue.indexOf("|"));
				response[1] = "Flight record is successfully modified (FieldName:" + fieldNamex
						+ ", FieldValue: " + newValue + ")";
				
				assertArrayEquals(response, b1.split(","));
			}
		});
		
		t1.start();
		t2.start();
		t1.join();
		t2.join();

	}

}
