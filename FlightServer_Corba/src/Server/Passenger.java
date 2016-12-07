package Server;


public class Passenger {
	public String passengerId;
	public String firstName;
	public String lastName;
	public String address;
	public String phoneNumber;
	public String destination;
	public String flightClass;
	public String deptDate;	
	public String deptTime;

	public void Passener(){}
	
	public String stringify()
	{
		return "{PassengerID= "+passengerId+", FirstName= "+firstName+", LastName= "+lastName+", Address= "+address+", PhoneNumber= "+phoneNumber+", Destination= "+destination+", FlightClass= "+flightClass+", DeptDate= "+deptDate+", DeptTime= "+deptTime+"}";
		
	}
	
	public String toUDPstringify()
	{
		String[] pass = new String[7];
		pass[0] = firstName;
		pass[1] = lastName;
		pass[2] = address;
		pass[3] = phoneNumber;
		pass[4] = destination;
		pass[5] = flightClass;
		pass[6] = deptDate;
		
		
		return String.join(",", pass);
		
	}
}
