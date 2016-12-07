package Testing;

import java.util.HashMap;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import FE.FEBookingInt;
import FE.FEBookingIntHelper;
import Models.Enums;
import Models.UDPMessage;
import Utilities.InputValidation;



public class CorbaTest {

	public static void main(String[] args) throws InvalidName, NotFound, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName {
		String[] argss = new String[4];
		argss[0] = "-ORBInitialPort";
		argss[1] = "1050";
		argss[2] = "-ORBInitialHost";
		argss[3] = "localhost";

		//String serverName = "WSL";

		ORB orb = ORB.init(argss, null);
		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
		FEBookingInt server = (FEBookingInt) FEBookingIntHelper.narrow(ncRef.resolve_str("FRONTEND"));
		
		String newValue = "";
		String recordID  = "";
		String fieldName = "";
		
		String serverName= Enums.FlightCities.Montreal.toString();
		String managerID= "MTL1114";
		String reply= "";
		
		
		System.out.println("-------------------Createint Flight 1 Montreal to Washington--------------------");
		newValue = "10:10:10:2016/12/06:13;13:"+Enums.FlightCities.Washington.toString();		
		recordID =  serverName+":"+managerID+":-1"; 
		fieldName = Enums.FlightFileds.createFlight.toString();
		reply = server.editFlightRecord(recordID, fieldName, newValue);
		System.out.println("Create Flight Reply : "+reply);		
		//========================================================

		
		System.out.println("-------------------Createint Flight 2 Montreal to NewDelhi --------------------");		
		newValue = "10:10:10:2016/12/08:13;13:"+Enums.FlightCities.NewDelhi.toString();		
		recordID =  serverName+":"+managerID+":-1"; 
		fieldName = Enums.FlightFileds.createFlight.toString();
		reply = server.editFlightRecord(recordID, fieldName, newValue);
		
		System.out.println("Create Flight Reply : "+reply);
	
		//========================================================		
		
		System.out.println("-------------------EDIT Flight 1--------------------");	
		fieldName = Enums.FlightFileds.flightDate.toString();
		newValue = "2016/12/05";
		recordID = "1";
		recordID = serverName+":"+managerID+":"+recordID+":"+Enums.FlightCities.Washington.toString()+":2016/12/06:13;13"; 
		reply = server.editFlightRecord(recordID, fieldName, newValue);				
		System.out.println("EDIT Flight Reply : "+reply);
		
		
		//========================================================
		
		System.out.println("-------------------Delete Flight 1--------------------");		
		
		fieldName = Enums.FlightFileds.deleteFlight.toString();
		newValue = "2";
		recordID = serverName+":"+managerID+":"+Enums.FlightCities.NewDelhi.toString()+":2016/12/08:13;13"; 
		reply = server.editFlightRecord(recordID, fieldName, newValue);				
		System.out.println("Delete Flight Reply : "+reply);
		
		
		//========================================================
		
		System.out.println("-------------------Booking Flight 1--------------------");
		String firstName ="Ulan";
		firstName = serverName+":"+managerID+":"+firstName;
		reply = server.bookFlight(firstName, "Baitassov", "Verdun", "5145606164",Enums.FlightCities.Washington.toString() , "2016/12/05", Enums.Class.Economy.toString());	
		
		System.out.println("Booking Flight Reply : "+reply);
		
		
		
		//========================================================
		
		System.out.println("-------------------Createint Flight 3 NewDelhi to Washington --------------------");
		newValue = "10:10:10:2016/12/05:13;13:"+Enums.FlightCities.Washington.toString();
		managerID = "NDL1114";
		serverName= Enums.FlightCities.NewDelhi.toString();
		recordID =  serverName+":"+managerID+":-1"; 
		fieldName = Enums.FlightFileds.createFlight.toString();
		reply = server.editFlightRecord(recordID, fieldName, newValue);
		System.out.println("Createint Flight 3 NewDelhiReply : "+reply);
//		//========================================================
		
		System.out.println("-------------------Book flight Count on Montreal --------------------");
		managerID = "MTL1114";
		serverName= Enums.FlightCities.Montreal.toString();	
		String recordType = "ALL";
		recordType = serverName+":"+managerID+":"+recordType;
		reply = server.getBookedFlightCount(recordType);
		System.out.println("Book flight Count Reply : "+reply);
//		//========================================================
		
		
		
		System.out.println("-------------------Transfering Booking ftom  Montreal to NewDelhi --------------------");
		managerID = "MTL1114";
		serverName= Enums.FlightCities.Montreal.toString();			
		String passengerID = "1";	
		passengerID =  serverName+":"+managerID+":"+passengerID;
		reply = server.transferReservation(passengerID, Enums.FlightCities.Montreal.toString(), Enums.FlightCities.NewDelhi.toString());
				
		System.out.println("EDIT Flight Reply : "+reply);
		//========================================================
		
		
	

	}
}
