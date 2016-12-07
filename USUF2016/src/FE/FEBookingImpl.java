package FE;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.omg.CORBA.ORB;

import Models.Enums;
import Models.UDPMessage;
import RUDP.RUDP;
import ReliableUDP.Reciever;
import ReliableUDP.Sender;
import StaticContent.StaticContent;
import Utilities.Serializer;

public class FEBookingImpl extends FEBookingIntPOA {

	private ORB orb;
	// private int portSequencer = 5555;
	// private String addressSequencer = "127.0.0.1";

	public FEBookingImpl() {
	}

	@Override
	public String bookFlight(String firstName, String lastName, String address, String phone, String destination,
			String date, String classFlight) {
		// TODO Auto-generated method stub
		System.out.println("inside bookFlight");

		String[] arr = firstName.split(":");
		// server manager cmd firstName
		// String msg = arr[0] + "|" + arr[1] + ":" + "bookFlight:" + arr[3] +
		// ":" + lastName + ":" + address + ":" + phone
		// + ":" + destination + ":" + date + ":" + classFlight;

		// Fornt End Send Request
		UDPMessage udpMsg = new UDPMessage(Enums.UDPSender.FrontEnd, -1, Enums.getFlightCitiesFromString(arr[0]),
				Enums.Operations.bookFlight, Enums.UDPMessageType.Request);
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("firstName", arr[2]);
		parameterMap.put("lastName", lastName);
		parameterMap.put("address", address);
		parameterMap.put("phone", phone);
		parameterMap.put("destination", destination);
		parameterMap.put("date", date);
		parameterMap.put("classFlight", classFlight);
		udpMsg.setParamters(parameterMap);
		udpMsg.setManagerID(arr[1]);
		udpMsg.setFrontEndPort(-1);

		return send(udpMsg);
	}

	@Override
	public String getBookedFlightCount(String recordType) {
		// TODO Auto-generated method stub
		System.out.println("inside getBookedFlightCount");
		// String msg = "getBookedFlightCount:" + recordType;
		String[] arr = recordType.split(":");

		UDPMessage udpMsg = new UDPMessage(Enums.UDPSender.FrontEnd, -1, Enums.getFlightCitiesFromString(arr[0]),
				Enums.Operations.getBookedFlightCount, Enums.UDPMessageType.Request);
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("recordType", arr[2]);
		udpMsg.setParamters(parameterMap);
		udpMsg.setManagerID(arr[1]);
		udpMsg.setFrontEndPort(-1);

		return send(udpMsg);
	}

	@Override
	public String editFlightRecord(String recordID, String fieldName, String newValue) {
		// TODO Auto-generated method stub
		System.out.println("inside editFlightRecord");
		UDPMessage udpMsg = null;

		if (fieldName.equals("createFlight")) {
			String[] arr = recordID.split(":");

			udpMsg = new UDPMessage(Enums.UDPSender.FrontEnd, -1, Enums.getFlightCitiesFromString(arr[0]),
					Enums.Operations.editFlightRecord, Enums.UDPMessageType.Request);
			HashMap<String, String> parameterMap = new HashMap<String, String>();
			parameterMap.put("recordID", arr[2]);
			parameterMap.put("fieldName", fieldName);
			parameterMap.put("newValue", newValue);
			udpMsg.setParamters(parameterMap);
			udpMsg.setManagerID(arr[1]);
			udpMsg.setFrontEndPort(-1);
			// udpMsg.setReplyMsg(arr[3]+":"+arr[4]+":"+arr[5]);
		} else if (fieldName.equals("deleteFlight")) {
			// Delete Flight
			String[] arr = recordID.split(":");

			udpMsg = new UDPMessage(Enums.UDPSender.FrontEnd, -1, Enums.getFlightCitiesFromString(arr[0]),
					Enums.Operations.editFlightRecord, Enums.UDPMessageType.Request);
			HashMap<String, String> parameterMap = new HashMap<String, String>();
			parameterMap.put("recordID", arr[3]);
			parameterMap.put("fieldName", fieldName);
			parameterMap.put("newValue", newValue);
			udpMsg.setParamters(parameterMap);
			udpMsg.setManagerID(arr[1]);
			udpMsg.setFrontEndPort(-1);
			udpMsg.setReplyMsg(arr[2] + ":" + arr[3] + ":" + arr[4]);
		} else {
			// Edit Flight
			// String msg = "editFlightRecord:" + recordID + ":" + fieldName +
			// ":" +
			// newValue;
			String[] arr = recordID.split(":");

			udpMsg = new UDPMessage(Enums.UDPSender.FrontEnd, -1, Enums.getFlightCitiesFromString(arr[0]),
					Enums.Operations.editFlightRecord, Enums.UDPMessageType.Request);
			HashMap<String, String> parameterMap = new HashMap<String, String>();
			parameterMap.put("recordID", arr[2]);
			parameterMap.put("fieldName", fieldName);
			parameterMap.put("newValue", newValue);
			udpMsg.setParamters(parameterMap);
			udpMsg.setManagerID(arr[1]);
			udpMsg.setFrontEndPort(-1);
			udpMsg.setReplyMsg(arr[3] + ":" + arr[4] + ":" + arr[5]);
		}

		return send(udpMsg);
	}

	@Override
	public String transferReservation(String passengerID, String currentCity, String otherCity) {
		// TODO Auto-generated method stub
		System.out.println("inside transferReservation");

		// String msg = "transferReservation:" + passengerID + ":" + currentCity
		// + ":" + otherCity;
		String[] arr = passengerID.split(":");
		UDPMessage udpMsg = new UDPMessage(Enums.UDPSender.FrontEnd, -1, Enums.getFlightCitiesFromString(arr[0]),
				Enums.Operations.transferReservation, Enums.UDPMessageType.Request);
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("passengerID", arr[2]);
		parameterMap.put("currentCity", currentCity);
		parameterMap.put("otherCity", otherCity);
		udpMsg.setParamters(parameterMap);
		udpMsg.setManagerID(arr[1]);
		udpMsg.setFrontEndPort(-1);

		return send(udpMsg);
	}

	/**
	 * This method sets the orb
	 * 
	 * @param new_orb
	 */
	public void setORB(ORB new_orb) {
		orb = new_orb;
	}

	/**
	 * This method shut downs the orb
	 */
	public void shutdown() {
		orb.shutdown(false);
	}

	public String send(UDPMessage new_msg) {

		String result = "0";
		final String[][] resultInfo = new String[4][4];
		// System.out.println("a");

		try {
			final DatagramSocket socket = new DatagramSocket();

			// Sender s = new Sender(StaticContent.SEQUENCER_IP_ADDRESS,
			// StaticContent.SEQUENCER_lISTENING_PORT,
			// StaticContent.FRONT_END_ACK_PORT, false, socket);
			System.out.println("my socket port :" + socket.getLocalPort());

			// Sender s = new Sender(StaticContent.SEQUENCER_IP_ADDRESS,
			// StaticContent.SEQUENCER_lISTENING_PORT, false, socket);

			new_msg.setFrontEndPort(socket.getLocalPort());
			new_msg.setFrontEndIP(InetAddress.getByName(StaticContent.FRONT_END_IP_ADDRESS));

			InetAddress aHost = InetAddress.getByName(StaticContent.SEQUENCER_IP_ADDRESS);
			byte[] sendData = Serializer.serialize(new_msg);
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, aHost,
					StaticContent.SEQUENCER_lISTENING_PORT);
			socket.send(sendPacket);

			byte[] receiveData = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			socket.receive(receivePacket);
			byte[] message = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());

			// Deserialize Data to udpMessage Object.
			UDPMessage udpMessageReceived = Serializer.deserialize(message);
			receiveData = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];
			System.out.println("Ack from Sequencer: " + udpMessageReceived.getReplyMsg());

			// Boolean status = s.send(new_msg);
			// System.out.println(status);
			// socket.close();

			Thread t2 = new Thread(new Runnable() {
				@Override
				public void run() {
					int i = 0;
					boolean isWaiting2 = true;
					while (isWaiting2) {
						try {
							System.out.println("waiting for UDP message i: " + i);

							// Reciever r = new Reciever(socket);
							// String message = r.getData().getReplyMsg();

							byte[] receiveData = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];
							DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
							socket.receive(receivePacket);

							byte[] message = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
							// Deserialize Data to udpMessage Object.
							String reevied = new String(message); 
							//System.out.println("reevied : "+reevied);
							UDPMessage udpMessageReceived = null;
							try{
								udpMessageReceived = Serializer.deserialize(message);
							}
							catch (Exception e) {
								// TODO Auto-generated catch block
								System.out.println("Parsing issue:");
								reevied ="";
							}
							
							if(reevied!= null && reevied.trim().length() > 0)
							{
								
								receiveData = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];
								
								if(udpMessageReceived.getOpernation() != null)
								System.out.println("Openeration "+ udpMessageReceived.getOpernation().toString());

								// if(receivePacket.getPort()!=StaticContent.SEQUENCER_lISTENING_PORT){
								// System.out.println("received:"+new
								// String(receivePacket.getData()));
								// String[] arr = new
								// String(receivePacket.getData()).split(":");

								if (udpMessageReceived != null) {
									Enums.UDPSender sender = udpMessageReceived.getSender();
									System.out.println("Received Sender : " + sender);
									switch (sender) {
									case ReplicaUlan:
										resultInfo[i][0] = "0";
										// resultInfo[i][0] = arr[0];
										resultInfo[i][1] = udpMessageReceived.getReplyMsg().trim();
										// resultInfo[i][1] = arr[1];
										resultInfo[i][2] = StaticContent.RM1_IP_ADDRESS;// receivePacket.getAddress().toString();
										resultInfo[i][3] = Integer.toString(StaticContent.RM1_lISTENING_PORT);// Integer.toString(receivePacket.getPort());
										i++;
										break;

									case ReplicaSajjad:
										resultInfo[i][0] = "0";
										// resultInfo[i][0] = arr[0];
										resultInfo[i][1] = udpMessageReceived.getReplyMsg().trim();
										// resultInfo[i][1] = arr[1];
										resultInfo[i][2] = StaticContent.RM2_IP_ADDRESS;// receivePacket.getAddress().toString();
										resultInfo[i][3] = Integer.toString(StaticContent.RM2_lISTENING_PORT);// Integer.toString(receivePacket.getPort());
										i++;
										break;
									case ReplicaUmer:
										resultInfo[i][0] = "0";
										// resultInfo[i][0] = arr[0];
										resultInfo[i][1] = udpMessageReceived.getReplyMsg().trim();
										// resultInfo[i][1] = arr[1];
										resultInfo[i][2] = StaticContent.RM3_IP_ADDRESS;// receivePacket.getAddress().toString();
										resultInfo[i][3] = Integer.toString(StaticContent.RM3_lISTENING_PORT);// Integer.toString(receivePacket.getPort());
										i++;
										break;

									case ReplicaFeras:
										resultInfo[i][0] = "0";
										// resultInfo[i][0] = arr[0];
										resultInfo[i][1] = udpMessageReceived.getReplyMsg().trim();
										// resultInfo[i][1] = arr[1];
										resultInfo[i][2] = StaticContent.RM4_IP_ADDRESS;// receivePacket.getAddress().toString();
										resultInfo[i][3] = Integer.toString(StaticContent.RM4_lISTENING_PORT);// Integer.toString(receivePacket.getPort());
										i++;
										break;

									default:
										break;
									}
								}

								udpMessageReceived.setReplyMsg("ACK");
								udpMessageReceived.setSender(Enums.UDPSender.FrontEnd);
								byte[] sendData = Serializer.serialize(udpMessageReceived);
								DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
										receivePacket.getAddress(), receivePacket.getPort());
								socket.send(sendPacket);
							}
							
							

							if (i == 4) {
								isWaiting2 = false;
								System.out.println("i = " + i + ", isWaiting2 set to: " + isWaiting2);
								break;
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
			t2.start();

			Timer timer = new Timer();
			TimeOutTask timeOutTask = null;
			boolean isWaiting = true;

			timer.schedule(timeOutTask = new TimeOutTask(), StaticContent.FRONT_END_SERVER_REPLY_TIME_OUT);

			int count = 0;
			while (isWaiting) {
				count++;
				// System.out.print(count + " ");
				if (count % 20 == 0) {
					// System.out.println();
				}

				if (timeOutTask.getTimeOut() || !t2.isAlive()) {
					isWaiting = false;
					t2.stop();

					if (timeOutTask.getTimeOut()) {
						System.out.println("time out has occured:");
					} else if (!t2.isAlive()) {
						System.out.println("All Messages arrived.");
					}

					for (int k = 0; k < 4; k++) {
						if (resultInfo[k][1] == null) {
							resultInfo[k][0] = "false";
							resultInfo[k][1] = "X";
							resultInfo[k][2] = "X";
							resultInfo[k][3] = "X";
						}
						System.out.println("resultInfo[" + k + "][0] = " + resultInfo[k][0]);
						System.out.println("resultInfo[" + k + "][1] = " + resultInfo[k][1]);
						System.out.println("resultInfo[" + k + "][2] = " + resultInfo[k][2]);
						System.out.println("resultInfo[" + k + "][3] = " + resultInfo[k][3]);
					}
				}

			}

			result = compareResults(resultInfo).trim();
			System.out.println("final result: " + result);

			String[] addressPortRM = new String[4];
			addressPortRM[0] = StaticContent.RM1_IP_ADDRESS + ":" + Integer.toString(StaticContent.RM1_lISTENING_PORT);
			addressPortRM[1] = StaticContent.RM2_IP_ADDRESS + ":" + Integer.toString(StaticContent.RM2_lISTENING_PORT);
			addressPortRM[2] = StaticContent.RM3_IP_ADDRESS + ":" + Integer.toString(StaticContent.RM3_lISTENING_PORT);
			addressPortRM[3] = StaticContent.RM4_IP_ADDRESS + ":" + Integer.toString(StaticContent.RM4_lISTENING_PORT);

			UDPMessage udpMsg2Rm = new UDPMessage(Enums.UDPSender.FrontEnd, -1, Enums.FlightCities.Montreal,
					Enums.Operations.softwareFailure, Enums.UDPMessageType.Request);

			for (int k = 0; k < 4; k++) {
				String msgRM = "none";
				if (!resultInfo[k][1].trim().equalsIgnoreCase(result)) {
					System.err.println("right here: " + resultInfo[k][1]);
					if (!resultInfo[k][1].equalsIgnoreCase("X")) {
						// msgRM =
						// "notcorrect:"+resultInfo[k][2]+":"+resultInfo[k][3];
						udpMsg2Rm.setOpernation(Enums.Operations.softwareFailure);
					} else {
						// msgRM =
						// "noresultsent:"+resultInfo[k][2]+":"+resultInfo[k][3];
						udpMsg2Rm.setOpernation(Enums.Operations.hardwareFailure);
					}
					//for (int j = 0; j < 4; j++) {
						//String[] arr = addressPortRM[j].split(":");
						//InetAddress aHostRM = InetAddress.getByName(arr[0]);
						//int portN = Integer.parseInt(arr[1]);

						// udpMsg2Rm.setFrontEndIP(InetAddress.getByName(resultInfo[k][2].replace("/",
						// "")));
						System.out.println("Sending Message :" + udpMsg2Rm.getOpernation().toString() + ", TO : "
								+ resultInfo[k][2] + ":" + resultInfo[k][3]);

						udpMsg2Rm.setFrontEndIP(InetAddress.getByName(resultInfo[k][2].replace("/", "")));
						udpMsg2Rm.setFrontEndPort(Integer.parseInt(resultInfo[k][3]));
						udpMsg2Rm.setSender(Enums.UDPSender.FrontEnd);
						
						byte[] sendData2 = Serializer.serialize(udpMsg2Rm);
						//DatagramPacket sendPacket2 = new DatagramPacket(sendData2, sendData2.length, aHostRM, portN);
						DatagramPacket sendPacket2 = new DatagramPacket(sendData2, sendData2.length, InetAddress.getByName(resultInfo[k][2]), Integer.parseInt(resultInfo[k][3]));
						socket.send(sendPacket2);

						// DatagramPacket packetToRM = new
						// DatagramPacket(msgRM.getBytes(), msgRM.length(),
						// aHostRM, portN);
						// socket.send(packetToRM);
						// System.out.println("Packet sent to RM"+j+": "+ new
						// String(packetToRM.getData()));

						// byte[] buffer2 = new byte[1000];
						// DatagramPacket replyPacket = new
						// DatagramPacket(buffer2, buffer2.length);
						// socket.receive(replyPacket);
						// System.out.println("Ack received from RM"+j+": " +
						// new String(replyPacket.getData()));

						byte[] receiveData2 = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];
						DatagramPacket receivePacket2 = new DatagramPacket(receiveData2, receiveData2.length);
						socket.receive(receivePacket);

						byte[] message2 = Arrays.copyOf(receivePacket2.getData(), receivePacket2.getLength());
						// Deserialize Data to udpMessage Object.
						String replay = new String(message2);
						System.out.println("Recevied Reply :  " + new String(message2));
						if (replay != null && replay.trim().length() > 0) {
							UDPMessage udpMessageReceived2 = Serializer.deserialize(message2);
							receiveData2 = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];

							System.out.println(udpMessageReceived2.getReplyMsg());
						} else {
							System.out.println("No Reply Get.");
						}

					//}
				}
			}

			socket.close();

		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return result;
	}

	/**
	 * This method compares 4 results that was sent by replicas to verify the
	 * correct one even if one is incorrect and another is lost.
	 * 
	 * @param new_resultInfo
	 *            information of replicas
	 * @return correct result
	 */
	public String compareResults(String[][] new_resultInfo) {

		String finalResult = "";
		int j = 0;
		if (!new_resultInfo[0][1].equalsIgnoreCase("X")) {
			finalResult = new_resultInfo[j][1];
		} else {
			j++;
			finalResult = new_resultInfo[j][1];
		}

		int count = 0;
		for (int i = j + 1; i < 4; i++) {
			if (finalResult.equalsIgnoreCase(new_resultInfo[i][1])) {
				count++;
			}
		}

		if (count == 0) {
			if (!new_resultInfo[j + 1][1].equalsIgnoreCase("X")) {
				finalResult = new_resultInfo[j + 1][1];
			} else {
				finalResult = new_resultInfo[j + 2][1];
			}
		}

		return finalResult;
	}

}
