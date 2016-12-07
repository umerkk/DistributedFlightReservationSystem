package Server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import CInterface.*;
import Client.Logger;
import Models.Enums;

public class FlightServer extends FlightServerInterfacePOA {

	final int REGISTRY_PORT = 4221;
	private HashMap<String, ArrayList<Flights>> flightList = new HashMap<String, ArrayList<Flights>>();
	private HashMap<String, ArrayList<Passenger>> passengerList = new HashMap<String, ArrayList<Passenger>>();
	private HashMap<Integer, String> recordToDateMapper = new HashMap<Integer, String>();
	public String serverLocation = "";
	private int recordId = 1;
	private int passengerId = 0;
	HashMap<String, Integer> udpPorts = new HashMap<String, Integer>();
	public static ORB orb = null;
	Thread udpServerThread = null;
	DatagramSocket udpSocket = null;

	@Override
	public String bookFlight(String firstName, String lastName, String address, String phoneNumber, String destination,
			String deptDate, String flightType) {

		String[] response = new String[2];

		boolean returnVal = false;
		String hashkey = lastName.substring(0, 1);
		if (!passengerList.containsKey(hashkey)) {
			synchronized (this) {
				// Synchronize this block
				passengerList.put(hashkey, new ArrayList<Passenger>());
			}
		}
		Flights availableFlight = checkFlightAvailability(deptDate, "", flightType, destination.toLowerCase());
		if (availableFlight != null) {
			Passenger tempPassenger = new Passenger();
			tempPassenger.passengerId = String.valueOf(++passengerId);
			tempPassenger.firstName = firstName;
			tempPassenger.lastName = lastName;
			tempPassenger.address = address;
			tempPassenger.phoneNumber = phoneNumber;
			tempPassenger.destination = destination;
			tempPassenger.deptDate = deptDate;
			tempPassenger.deptTime = "";
			tempPassenger.flightClass = flightType;
			try {
				// synchronized (this) {
				int currentSeats = availableFlight.seats.get(flightType.toLowerCase());
				if (availableFlight.seats.replace(flightType.toLowerCase(), --currentSeats) != null) {

					ArrayList<Passenger> tmp = passengerList.get(hashkey);
					synchronized (this) {
						tmp.add(tempPassenger);
					}
					response[0] = "true";
					response[1] = tempPassenger.passengerId;
					// returnVal = true;
					Logger.writeLog("Success", "bookFlight", serverLocation, tempPassenger.stringify(), null);
				} else {
					// returnVal = false;
					response[0] = "false";
					//response[1] = "Problem while deducting available seats from the FlightList for the Boooking record ("
							//+ tempPassenger.stringify() + ")";
					Logger.writeLog("false", "bookFlight", serverLocation,
							"Problem while deducting available seat count from FlightList. Extra info: "
									+ tempPassenger.stringify(),
							null);
				}
				// }
			} catch (Exception e) {
				// Log the error
				response[0] = "false";
				//response[1] = "Problem while deducting available seats from the FlightList for the Boooking record ("
						//+ tempPassenger.stringify() + ")";
				Logger.writeLog("false", "bookFlight", serverLocation,
						"Problem while deducting available seat count and adding passenger to booking list. Extra info: "
								+ e.getMessage() + ". Object info: " + tempPassenger.stringify(),
						null);
			}
		} else {
			// Flight is not available.
			// returnVal = false;
			response[0] = "false";
			//response[1] = "There is no flight flying at the desired dates OR there are not enough vacant seats for this reservation. ";

			Logger.writeLog("false", "bookFlight", serverLocation,
					"No available seat in the requested flight (" + deptDate + ' ' + "" + ' ' + flightType + ")", null);
		}

		return String.join(":", response);

		// return true;
	}

	private Flights checkFlightAvailability(String deptDate, String deptTime, String flightType, String destination) {
		if (flightList.containsKey(deptDate)) {
			for (Flights f : flightList.get(deptDate)) {
				if (f.deptTime.equalsIgnoreCase(deptTime) && f.seats.get(flightType.toLowerCase()) > 0
						&& f.arrivalCity.equalsIgnoreCase(destination)) {
					return f;
				}
			}
		}
		return null;
	}

	private String[] addFlightRecord(String arrivalCity, String deptDate, String deptTime, int economySeats,
			int businessSeats, int firstClassSeats) {

		String[] response = new String[2];
		String managerId = arrivalCity.substring(arrivalCity.indexOf("|") + 1, arrivalCity.length());
		arrivalCity = arrivalCity.substring(0, arrivalCity.indexOf("|"));

		Flights existingRecord = null;
		if (flightList.containsKey(deptDate)) {
			for (Flights f : flightList.get(deptDate)) {
				if (f.deptTime.equalsIgnoreCase(deptTime) && f.arrivalCity.equalsIgnoreCase(arrivalCity)) {
					existingRecord = f;
				}
			}
		}

		if (existingRecord == null) {
			Flights myFlight = new Flights();
			myFlight.recordId = recordId++;
			myFlight.arrivalCity = arrivalCity;
			myFlight.deptDate = deptDate;
			myFlight.deptTime = deptTime;
			myFlight.seats.put("economy", economySeats);
			myFlight.seats.put("business", businessSeats);
			myFlight.seats.put("firstclass", firstClassSeats);

			if (flightList.get(deptDate) == null) {
				synchronized (this) {
					recordToDateMapper.put(myFlight.recordId, deptDate);
					flightList.put(deptDate, new ArrayList<Flights>());
				}
			}
			ArrayList<Flights> tmpList = flightList.get(deptDate);
			synchronized (this) {
				tmpList.add(myFlight);
			}
			Logger.writeLog("Success", "addFlightRecord", serverLocation, myFlight.stringify(), managerId);
			response[0] = "true";
			response[1] = String.valueOf(myFlight.recordId);

		} else {

			Logger.writeLog("false", "addFlightRecord", serverLocation,
					"There already exists another flight with the same flight parameters (" + deptDate + ' ' + deptTime
							+ ' ' + arrivalCity + ")",
					managerId);
			response[0] = "false";
			//response[1] = "There exists anothjer flight with the same flight parameters.";

		}

		return response;

	}

	private String[] removeFlight(String deptDate, int recordId) {
		// TODO Auto-generated method stub

		String[] response = new String[1];
		String managerId = deptDate.substring(deptDate.indexOf("|") + 1, deptDate.length());
		deptDate = deptDate.substring(0, deptDate.indexOf("|"));
		Flights selectedFlight = null;
		boolean isFound = false;
		if (flightList.containsKey(deptDate)) {
			ArrayList<Flights> list = flightList.get(deptDate);
			for (int k = 0; k < list.size(); k++) {
				if (list.get(k).recordId == recordId) {
					selectedFlight = list.get(k);
					synchronized (this) {
						list.remove(k);
					}

					// Remove all passengers as well.
					for (Map.Entry<String, ArrayList<Passenger>> entry : passengerList.entrySet()) {
						ArrayList<Passenger> tmpPassengerList = entry.getValue();
						for (int g = 0; g < tmpPassengerList.size(); g++) {
							if (tmpPassengerList.get(g).deptDate.equalsIgnoreCase(deptDate)
									&& tmpPassengerList.get(g).destination
											.equalsIgnoreCase(selectedFlight.arrivalCity)) {
								synchronized (this) {
									tmpPassengerList.remove(g);
								}
							}
						}

					}

					isFound = true;
					// If Flight list is empty, remove the hashValue as well.
					if (list.size() < 1) {
						synchronized (this) {
							flightList.remove(deptDate);
						}
					}
					Logger.writeLog("true", "removeFlight", serverLocation,
							"DeptDate:" + deptDate + ", RecordID: " + recordId, managerId);

				}
			}
			if (isFound) {
				response[0] = "true";
				// response[1] = "Your flight has been successfully removed.";

			} else {
				response[0] = "false";
				// response[1] = "No flight was found with the specified RECORD
				// ID";

			}
		} else {
			Logger.writeLog("Error", "removeFlight", serverLocation,
					"No Flight exists on the spcified date (" + deptDate + ' ' + recordId + ")", managerId);

			response[0] = "false";
			// response[1] = "No flight on the specified date (" + deptDate + ",
			// " + recordId + ")";
		}
		return response;

	}

	@Override
	public String getBookedFlightCount(String managerId) {
		// TODO Auto-generated method stub
		String ip[] = managerId.split(":");

		String res = "";
		res += this.getActualBookedFlightCount(ip[0].toLowerCase());
		res += ",";

		for (Map.Entry<String, Integer> entry : udpPorts.entrySet()) {
			if (!entry.getKey().equalsIgnoreCase(serverLocation)) {
				String ep = "";
				if (ip[0].equalsIgnoreCase("all")) {
					ep = sendUDPRequest(entry.getValue(), ip[1], "flightCount-all").trim();
				} else if (ip[0].equalsIgnoreCase("economy")) {
					ep = sendUDPRequest(entry.getValue(), ip[1], "flightCount-economy").trim();

				} else if (ip[0].equalsIgnoreCase("business")) {
					ep = sendUDPRequest(entry.getValue(), ip[1], "flightCount-business").trim();

				} else if (ip[0].equalsIgnoreCase("firstclass")) {
					ep = sendUDPRequest(entry.getValue(), ip[1], "flightCount-first").trim();

				}
				// String ep = sendUDPRequest(entry.getValue(), ip[1],
				// "flightCount").trim();
				if (!ep.equals("")) {
					res += ep;
					res += ",";
				}

			}
		}
		String  reply =  res;//res.substring(0, res.length() - 1);
		//MTL 0, WST 0,NDL 0,
		// return new String[2];
		
		return res.replace(Enums.FlightCities.Montreal.toString(), "MTL").replace(Enums.FlightCities.Washington.toString(), "WST").replace(Enums.FlightCities.NewDelhi.toString(), "NDL");

	}

	private String getActualBookedFlightCount(String type) {
		int totalBooking = 0;
		if (type.equalsIgnoreCase("all")) {
			for (Map.Entry<String, ArrayList<Passenger>> entry : passengerList.entrySet()) {
				totalBooking += entry.getValue().size();
			}
		} else if (type.equalsIgnoreCase("economy")) {
			for (Map.Entry<String, ArrayList<Passenger>> entry : passengerList.entrySet()) {
				ArrayList<Passenger> tempVal = entry.getValue();
				for (int k = 0; k < tempVal.size(); k++) {
					if (tempVal.get(k).flightClass.equalsIgnoreCase("economy"))
						totalBooking++;
				}

			}
		} else if (type.equalsIgnoreCase("business")) {
			for (Map.Entry<String, ArrayList<Passenger>> entry : passengerList.entrySet()) {
				ArrayList<Passenger> tempVal = entry.getValue();
				for (int k = 0; k < tempVal.size(); k++) {
					if (tempVal.get(k).flightClass.equalsIgnoreCase("business"))
						totalBooking++;
				}

			}
		} else if (type.equalsIgnoreCase("firstclass")) {
			for (Map.Entry<String, ArrayList<Passenger>> entry : passengerList.entrySet()) {
				ArrayList<Passenger> tempVal = entry.getValue();
				for (int k = 0; k < tempVal.size(); k++) {
					if (tempVal.get(k).flightClass.equalsIgnoreCase("firstclass"))
						totalBooking++;
				}

			}
		}

		return serverLocation + " " + totalBooking;
	}

	@Override
	public String editFlightRecord(String recordId, String fieldName, String newValue) {
		// TODO Auto-generated method stub

		switch (fieldName) {
		case "deptDate":
		case "deptTime":
		case "arrivalCity":
		case "firstclass":
		case "business":
		case "economy": {
			String[] fieldX = fieldName.split(",");
			String recordIdX = recordId.trim();
			String fieldNamex = fieldX[0].trim();
			String[] response = new String[1];
			String managerId = newValue.substring(newValue.indexOf("|") + 1, newValue.length());
			newValue = newValue.substring(0, newValue.indexOf("|"));

			String deptDate = "";
			boolean returnVal = false;
			if (recordToDateMapper.containsKey(Integer.parseInt(recordIdX))) {
				deptDate = recordToDateMapper.get(Integer.parseInt(recordIdX));

				ArrayList<Flights> list = flightList.get(deptDate);
				for (int k = 0; k < list.size(); k++) {
					Flights temp = list.get(k);
					if (temp.recordId == Integer.parseInt(recordIdX)) {
						if (fieldNamex.equalsIgnoreCase("economy") || fieldNamex.equalsIgnoreCase("business")
								|| fieldNamex.equalsIgnoreCase("firstclass")) {
							try {
								synchronized (this) {
									temp.seats.replace(fieldNamex.toLowerCase(), Integer.parseInt(newValue));
								}
								Logger.writeLog("Success", "editFlightRecord", serverLocation,
										"FieldName:" + fieldNamex + ", FieldValue: " + newValue, managerId);
								response[0] = "true";
								// response[1] = "Flight record is successfully
								// modified (FieldName:" + fieldNamex+ ",
								// FieldValue: " + newValue + ")";
							} catch (NumberFormatException nmb) {
								Logger.writeLog("Error", "editFlightRecord", serverLocation,
										"Invalid value for the field name (" + fieldNamex
												+ ") is provided. Server is expecting a number for seat value ",
										managerId);
								// return false;
								response[0] = "false";
								// response[1] = "Invalid value for the field
								// name (" + fieldNamex
								// + ") is provided. Server is expecting a
								// number for seat value ";

							}

						} else if (fieldNamex.equalsIgnoreCase("deptDate")) {
							try {

								java.lang.reflect.Field field = temp.getClass().getField(fieldNamex);
								field.set(temp, newValue);

								String[] res = this.addFlightRecord(temp.arrivalCity + "|" + managerId, temp.deptDate,
										temp.deptTime, temp.seats.get("economy"), temp.seats.get("business"),
										temp.seats.get("firstclass"));

								if (res[0].equalsIgnoreCase("true")) {
									synchronized (this) {
										recordToDateMapper.remove(Integer.parseInt(recordIdX));
										list.remove(k);
									}
									response[0] = "true";
									// response[1] = "Flight record is
									// successfully modified (FieldName:" +
									// fieldNamex
									// + ", FieldValue: " + newValue + ")";
								}

							} catch (NoSuchFieldException e) {
								Logger.writeLog("Error", "editFlightRecord", serverLocation,
										"Invalid field id (" + fieldNamex + ")", managerId);
								response[0] = "false";
								// response[1] = "Invalid field id (" +
								// fieldNamex + ")";

							} catch (IllegalArgumentException e) {
								Logger.writeLog("Error", "editFlightRecord", serverLocation,
										"IllegalArgumentException occured in (" + fieldNamex + ")", managerId);
								response[0] = "false";
								// response[1] = "IllegalArgumentException
								// occured in (" + fieldNamex + ")";

							} catch (IllegalAccessException e) {
								Logger.writeLog("Error", "editFlightRecord", serverLocation,
										"IllegaAccessException occured in field id (" + fieldNamex + ")", managerId);
								response[0] = "false";
								// response[1] = "IllegaAccessException occured
								// in field id (" + fieldNamex + ")";
							}
						} else {
							try {

								java.lang.reflect.Field field = temp.getClass().getField(fieldNamex);
								field.set(temp, newValue);
								Logger.writeLog("Success", "editFlightRecord", serverLocation,
										"FieldName:" + fieldNamex + ", FieldValue: " + newValue, managerId);
								response[0] = "true";
								// response[1] = "Flight record is successfully
								// modified (FieldName:" + fieldNamex
								// + ", FieldValue: " + newValue + ")";
							} catch (NoSuchFieldException e) {
								Logger.writeLog("Error", "editFlightRecord", serverLocation,
										"Invalid field id (" + fieldNamex + ")", managerId);
								response[0] = "false";
								// response[1] = "Invalid field id (" +
								// fieldNamex + ")";

							} catch (IllegalArgumentException e) {
								Logger.writeLog("Error", "editFlightRecord", serverLocation,
										"IllegalArgumentException occured in (" + fieldNamex + ")", managerId);
								response[0] = "false";
								// response[1] = "IllegalArgumentException
								// occured in (" + fieldNamex + ")";

							} catch (IllegalAccessException e) {
								Logger.writeLog("Error", "editFlightRecord", serverLocation,
										"IllegaAccessException occured in field id (" + fieldNamex + ")", managerId);
								response[0] = "false";
								// response[1] = "IllegaAccessException occured
								// in field id (" + fieldNamex + ")";
							}
						}

					}
				}

			} else {
				Logger.writeLog("Error", "editFlightRecord", serverLocation,
						"No record exists with the specified recordId (" + recordIdX + ")", managerId);
				response[0] = "false";
				// response[1] = "There is no such record exists for the
				// specified Record ID (" + recordIdX + ")";
			}

			return String.join(":", response);

		}

		case "createFlight": {

			String[] record = newValue.split(":");

			String[] response = addFlightRecord(record[5], record[3], "", Integer.parseInt(record[2]),
					Integer.parseInt(record[1]), Integer.parseInt(record[0]));

			return String.join(":", response);

		}

		case "deleteFlight": {

			// RecordID = date
			// NewValue = recordID.
			// FieldName = deleteFlight

			// Function expections
			// 12/22/22|MTL1234 , 4
			String managerId = newValue.substring(newValue.indexOf("|") + 1, newValue.length());
			int actualRecordId = Integer.parseInt(newValue.substring(0, newValue.indexOf("|")));

			String[] response = removeFlight(recordId + "|" + managerId, actualRecordId);
			return String.join(":", response);

		}

		}

		String[] res = new String[2];
		res[0] = "false";
		// res[1] = "Invalid switch sent to server";
		return String.join(":", res);

	}

	public void setORB(ORB orb_val) {
		orb = orb_val;
	}

	// implement shutdown() method
	public void shutdown() {
		orb.shutdown(false);
	}

	public void initServer(ORB orb, String[] args, String serverName) throws InvalidName, AdapterInactive,
			ServantNotActive, WrongPolicy, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound, CannotProceed {
		/*
		 * Registry myRegistry = null;
		 * 
		 * try { myRegistry = LocateRegistry.createRegistry(REGISTRY_PORT); }
		 * catch (java.rmi.server.ExportException ne) { // If we are running in
		 * tango server mode, there may be a registry // already existing.
		 * myRegistry = LocateRegistry.getRegistry(REGISTRY_PORT); }
		 * 
		 * Remote serverObject = UnicastRemoteObject.exportObject(this, port);
		 * myRegistry.bind(serverName, serverObject); this.serverLocation =
		 * serverName;
		 */

		// get reference to rootpoa &amp;
		POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
		// activate the POAManager
		rootpoa.the_POAManager().activate();

		this.setORB(orb);

		// get object reference from the servant
		org.omg.CORBA.Object ref = rootpoa.servant_to_reference(this);
		FlightServerInterface href = FlightServerInterfaceHelper.narrow(ref);

		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

		NameComponent path[] = ncRef.to_name(serverName);
		ncRef.rebind(path, href);
		this.serverLocation = serverName;
		System.out.println("Corba: '" + serverLocation + "' server is up and running");

	}

	private void startUdpServer() {
		this.setUDPPorts();
		int myUdpPort = this.getUDPPorts(this.serverLocation);
		if (myUdpPort != -1) {

			udpServerThread = new Thread(new Runnable() {
				public void run() {
					try {
						// Thread it
						udpSocket = new DatagramSocket(myUdpPort);
						System.out.println("UDP: '" + FlightServer.this.serverLocation
								+ "' server is up and running on port " + myUdpPort);

						while (true) {
							byte[] receiveData = new byte[1024];
							byte[] sendData = new byte[1024];
							DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
							udpSocket.receive(receivePacket);

							InetAddress senderIp = receivePacket.getAddress();
							int senderPort = receivePacket.getPort();

							String requestType = new String(receiveData).trim();
							// reset
							receiveData = new byte[1024];

							if (requestType.equalsIgnoreCase("flightCount-all")) {
								String res = FlightServer.this.getActualBookedFlightCount("all");
								sendData = res.getBytes();

							} else if (requestType.equalsIgnoreCase("flightCount-economy")) {
								String res = FlightServer.this.getActualBookedFlightCount("economy");
								sendData = res.getBytes();

							} else if (requestType.equalsIgnoreCase("flightCount-business")) {
								String res = FlightServer.this.getActualBookedFlightCount("business");
								sendData = res.getBytes();

							} else if (requestType.equalsIgnoreCase("flightCount-first")) {
								String res = FlightServer.this.getActualBookedFlightCount("firstclass");
								sendData = res.getBytes();

							} else if (requestType.equalsIgnoreCase("killMe")) {
								sendData = "true".getBytes();
								DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, senderIp,
										senderPort);
								udpSocket.send(sendPacket);
								// reset
								sendData = new byte[1024];
								udpSocket.close();
								break;

							} else {
								String[] data = requestType.split(";"); // Split
																		// the
																		// concatenated
																		// requestType
																		// and
																		// passenger
																		// data.
								String[] passenger = data[1].split(","); // Split
																			// the
																			// passenger
																			// UDP
																			// sting
																			// and
																			// get
																			// an
																			// array

								String rep = FlightServer.this.bookFlight(passenger[0].trim(), passenger[1].trim(),
										passenger[2].trim(), passenger[3].trim(), passenger[4].trim(),
										passenger[6].trim(), passenger[5].trim());

								sendData = rep.getBytes();

							}
							Logger.writeLog("Suceess Response", "UDpRequest", serverLocation,
									"The Return of requestType " + requestType + "is successful", "UDPRequests");

							DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, senderIp,
									senderPort);
							udpSocket.send(sendPacket);
							// reset
							sendData = new byte[1024];
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			udpServerThread.start();
		}
	}

	private void setUDPPorts() {
		this.udpPorts.put(Enums.FlightCities.Washington.toString(), 10007);
		this.udpPorts.put(Enums.FlightCities.Montreal.toString(), 10008);
		this.udpPorts.put(Enums.FlightCities.NewDelhi.toString(), 10009);
	}

	private int getUDPPorts(String serverName) {
		if (udpPorts.containsKey(serverName))
			return udpPorts.get(serverName);
		else
			return -1;
	}

	public String sendUDPRequest(int serverPort, String managerId, String requestType) {
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		// sendData = "getBookedFlightCount".getBytes();

		sendData = requestType.getBytes();

		try {
			DatagramSocket clientSocket = new DatagramSocket();
			// clientSocket.setSoTimeout(5000);
			InetAddress IPAddress = InetAddress.getByName("localhost");

			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, serverPort);
			clientSocket.send(sendPacket);

			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);

			String returnCount = new String(receivePacket.getData());
			Logger.writeLog("Success", "getFlightCount", serverLocation,
					"UDP Request of GetFlightCount returned (" + returnCount + ") at port (" + serverPort + ")",
					managerId);

			clientSocket.close();

			return returnCount;

		} catch (Exception e) {

			Logger.writeLog("Error", "getFlightCount", serverLocation, "UDP Request of GetFlightCount returned ERROR: ("
					+ e.getMessage() + ") at port (" + serverPort + ")", managerId);

			return "0";
		}
	}

	public String sendUDPRequest(int serverPort, String managerId, String requestType, String passengerObj) {
		byte[] sendData = new byte[4096];
		byte[] receiveData = new byte[4096];
		// sendData = "getBookedFlightCount".getBytes();

		sendData = (requestType + ";" + passengerObj).getBytes();

		try {
			DatagramSocket clientSocket = new DatagramSocket();
			// clientSocket.setSoTimeout(5000);
			InetAddress IPAddress = InetAddress.getByName("localhost");

			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, serverPort);
			clientSocket.send(sendPacket);

			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);

			String returnCount = new String(receivePacket.getData());

			Logger.writeLog("Success", "transferReservation", serverLocation,
					"UDP Request of transferReservation returned (" + returnCount + ") at port (" + serverPort + ")",
					managerId);

			clientSocket.close();

			return returnCount;

		} catch (Exception e) {
			if (requestType.equalsIgnoreCase("flightCount")) {

				Logger.writeLog("Error", "getFlightCount", serverLocation,
						"UDP Request of GetFlightCount returned ERROR: (" + e.getMessage() + ") at port (" + serverPort
								+ ")",
						managerId);
			} else if (requestType.equalsIgnoreCase("transferReservation")) {

				Logger.writeLog("Error", "transferReservation", serverLocation,
						"UDP Request of transferReservation returned ERROR: (" + e.getMessage() + ") at port ("
								+ serverPort + ")",
						managerId);
			}

			return "0";
		}
	}

	public static void main(String[] args) {
		final String serverLocation = args[0];
		String[] corbaArgs = new String[4];

		try {

			corbaArgs[0] = args[1];
			corbaArgs[1] = args[2];
			corbaArgs[2] = args[3];
			corbaArgs[3] = args[4];

			FlightServer myServer = new FlightServer();
			// FlightServer montrealServer = new FlightServer();
			// FlightServer ndhServer = new FlightServer();

			// create and initialize the ORB
			ORB orb = ORB.init(corbaArgs, null);

			myServer.initServer(orb, corbaArgs, serverLocation);
			myServer.startUdpServer();

			/*
			 * montrealServer.initServer(orb, corbaArgs, "MTL");
			 * montrealServer.startUdpServer();
			 * 
			 * ndhServer.initServer(orb, corbaArgs, "NDH");
			 * ndhServer.startUdpServer();
			 */

			while (true) {
				orb.run();
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void shutDownServer() {
		if (udpServerThread != null) {
			udpSocket.close();
			udpServerThread.stop();
		}
		if (orb != null) {
			orb.shutdown(false);
		}
	}

	public void startServer(String serverName, String UDPPort, String[] orbArgs) {
		// FlightServer myServer = new FlightServer();

		if (udpServerThread != null) {
			udpSocket.close();
			udpServerThread.stop();
		}
		if (orb != null) {
			orb.shutdown(false);
		}

		// create and initialize the ORB
		orb = ORB.init(orbArgs, null);

		try {
			this.initServer(orb, orbArgs, serverName);
		} catch (InvalidName | AdapterInactive | ServantNotActive | WrongPolicy
				| org.omg.CosNaming.NamingContextPackage.InvalidName | NotFound | CannotProceed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.startUdpServer();

		Thread orbRunThread = new Thread(new Runnable() {
			public void run() {
				orb.run();
			}
		});

	}

	public static byte[] toBytes(int i) {
		byte[] result = new byte[4];

		result[0] = (byte) (i >> 24);
		result[1] = (byte) (i >> 16);
		result[2] = (byte) (i >> 8);
		result[3] = (byte) (i /* >> 0 */);

		return result;
	}

	@Override
	public String transferReservation(String passengerID, String currentCity, String otherCity) {

		String[] response = new String[2];
		String managerId = currentCity;
		Passenger tempPassenger = null;
		int udpPort = 0;
		// String cityacro = "";
		// if (otherCity.equalsIgnoreCase("new delhi"))
		// cityacro = "NDH";
		// else if (otherCity.equalsIgnoreCase("montreal"))
		// cityacro = "MTL";
		// else if (otherCity.equalsIgnoreCase("washington"))
		// cityacro = "WSL";

		if (!udpPorts.containsKey(otherCity)) {
			response[0] = "Error";
			response[1] = "No server exists for the desired new city.";

		} else {

			udpPort = udpPorts.get(otherCity);

			// Iterate through passenger and find the record
			for (Map.Entry<String, ArrayList<Passenger>> entry : passengerList.entrySet()) {
				ArrayList<Passenger> tmpPassengerList = entry.getValue();
				for (int g = 0; g < tmpPassengerList.size(); g++) {
					if (tmpPassengerList.get(g).passengerId.equalsIgnoreCase(passengerID)) {
						tempPassenger = tmpPassengerList.get(g);
					}
				}
			}

			if (tempPassenger != null) {
				if (tempPassenger.destination.equalsIgnoreCase(otherCity)) {
					Logger.writeLog("Error", "transferReservation", serverLocation,
							"The passenger departure and arrival city is same, cannot transfer this passenger.",
							managerId);
					response[0] = "false";
					// response[1] = "The passenger current Arrival City and new
					// Departure city are same. A Passenger cannot travel to
					// same city.";
				} else {

					// Request to book flight on newCity server.
					String[] ep = sendUDPRequest(udpPort, managerId, "transferReservation",
							tempPassenger.toUDPstringify()).split(",");

					if (ep[0].equals("Success")) {

						Logger.writeLog("Success", "transferReservation", serverLocation,
								"Your passenger with PASSENGERID=" + passengerID + " has been partially transferred to "
										+ otherCity + ". Now we will delete it from our local server",
								managerId);

						// Now Remove from this server.
						// Iterate through passenger and find the record
						for (Map.Entry<String, ArrayList<Passenger>> entry : passengerList.entrySet()) {
							ArrayList<Passenger> tmpPassengerList = entry.getValue();
							for (int g = 0; g < tmpPassengerList.size(); g++) {
								if (tmpPassengerList.get(g).passengerId.equalsIgnoreCase(passengerID)) {
									synchronized (this) {
										tmpPassengerList.remove(g);
									}
									Logger.writeLog("Success",
											"transferReservation", serverLocation, "Your passenger with PASSENGERID="
													+ passengerID + " has been FULLY transferred to " + otherCity + ".",
											managerId);
									response[0] = "true";
									// response[1] = "You passenger with
									// PASSENGERID=" + passengerID
									// + " reservation has been successfully
									// transfered to " + otherCity;
								}
							}
						}

					} else {
						response = ep;
						Logger.writeLog("Error",
								"transferReservation", serverLocation, "Error occured while transferring PASSENGERID="
										+ passengerID + " to " + otherCity + ", please see the other server logs",
								managerId);
					}

				}
			} else {
				response[0] = "false";
				Logger.writeLog("Error",
						"transferReservation", serverLocation, "No passenger exists with the PASSENGERID="
								+ passengerID + " to " + otherCity + ", please see the other server logs",
						managerId);
			}

		}

		return String.join(",", response);
	}

}
