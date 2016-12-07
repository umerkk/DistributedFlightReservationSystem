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

public class EditRecordTest {
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
	public void test() {
		String[] response = new String[3];
		response[0] = "Success";
		//response[1] = "Your flight has been successfully booked.";
		

		String fieldName = "deptDate";
		int recordId = 2;
		String newValue = "25-44-44|WSL1111";
		
		String[] fieldX = "deptDate,1".split(",");
		//String recordIdX = fieldX[1].trim();
		String fieldNamex = fieldX[0].trim();
		
		String b1 =server.editFlightRecord("editFlightRecord", fieldName + "," + String.valueOf(recordId), newValue);
		
		newValue = newValue.substring(0, newValue.indexOf("|"));
		response[1] = "Flight record is successfully modified (FieldName:" + fieldNamex
				+ ", ";
		response[2]  = "FieldValue: " + newValue + ")";
		String[] t = b1.split(",");
		System.out.println(String.join(";",t));
		System.out.println(String.join(";",response));
		
		assertEquals(response[0], t[0]);
	}

}
