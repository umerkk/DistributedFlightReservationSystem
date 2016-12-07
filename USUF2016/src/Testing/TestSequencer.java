package Testing;

import static org.junit.Assert.assertTrue;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import Models.Enums;
import Models.UDPMessage;
import StaticContent.StaticContent;
import Utilities.Serializer;


/**
 * 
 * @author SajjadAshrafCan
 *
 */
public class TestSequencer {
	long oldSequenceNumber = 0;
	@BeforeClass
	public static void beforeClass() {
	}

	@AfterClass
	public static void afterClass() {
	}

	@Before
	public void before() {
	}

	@After
	public void after() {
	}

	// To Test Sequencer First you need Start Sequencer then run the Test Case.
	@Test
	public void testSequencerMultiCast() {
		
		
		//Start REplicas
		new Thread(new ReplicaListner(StaticContent.REPLICA_ULAN_lISTENING_PORT, Enums.UDPSender.ReplicaUlan)).start();
		new Thread(new ReplicaListner(StaticContent.REPLICA_SAJJAD_lISTENING_PORT, Enums.UDPSender.ReplicaSajjad)).start();
		new Thread(new ReplicaListner(StaticContent.REPLICA_UMER_lISTENING_PORT, Enums.UDPSender.ReplicaUmer)).start();
		new Thread(new ReplicaListner(StaticContent.REPLICA_FERAS_lISTENING_PORT, Enums.UDPSender.ReplicaFeras)).start();
		
		
		
		// Fornt End Send Request
		UDPMessage udpMsg = new UDPMessage(Enums.UDPSender.FrontEnd, -1, Enums.FlightCities.Montreal, Enums.Operations.bookFlight, Enums.UDPMessageType.Request);
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("firstName", "abc");
		parameterMap.put("lastName", "asdasd");
		parameterMap.put("address", "asdasd");
		parameterMap.put("phone", "1234567890");
		parameterMap.put("destination", Enums.FlightCities.Montreal.toString());
		parameterMap.put("date", "2016/12/1");
		parameterMap.put("classFlight", Enums.Class.Economy.toString());
		udpMsg.setParamters(parameterMap);
		
		boolean rqt1 = UPDCall(StaticContent.SEQUENCER_IP_ADDRESS, StaticContent.SEQUENCER_lISTENING_PORT, udpMsg);		
		
		udpMsg = new UDPMessage(Enums.UDPSender.FrontEnd, -1, Enums.FlightCities.Washington, Enums.Operations.editFlightRecord, Enums.UDPMessageType.Request);
		boolean rqt2 = UPDCall(StaticContent.SEQUENCER_IP_ADDRESS, StaticContent.SEQUENCER_lISTENING_PORT, udpMsg);
		
		udpMsg = new UDPMessage(Enums.UDPSender.FrontEnd, -1, Enums.FlightCities.NewDelhi, Enums.Operations.transferReservation, Enums.UDPMessageType.Request);
		boolean rqt3 = UPDCall(StaticContent.SEQUENCER_IP_ADDRESS, StaticContent.SEQUENCER_lISTENING_PORT, udpMsg);		
		Boolean status = (rqt1 && rqt2 && rqt3);
		System.out.println("status:" + status);
		assertTrue(status);
	}
	
	//Temporary FE Sender
	private Boolean UPDCall(String ip, int port, UDPMessage udpMsg) {
		Boolean reply = false;
		String msg = "";
		try {
			DatagramSocket clientSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(ip);
						
			// Serialize udpMessage
//			UDPMessage udpMsg = new UDPMessage(Enums.UDPSender.FrontEnd, -1, Enums.FlightCities.Montreal, Enums.Operations.bookFlight, Enums.UDPMessageType.Request);
//			HashMap<String, String> parameterMap = new HashMap<String, String>();
//			parameterMap.put("firstName", "abc");
//			parameterMap.put("lastName", "asdasd");
//			parameterMap.put("address", "asdasd");
//			parameterMap.put("phone", "1234567890");
//			parameterMap.put("destination", Enums.FlightCities.Montreal.toString());
//			parameterMap.put("date", "2016/12/1");
//			parameterMap.put("classFlight", Enums.Class.Economy.toString());
			
			byte[] sendData = Serializer.serialize(udpMsg);
			//Send UDP Message
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			clientSocket.send(sendPacket);
			
			//Set Time Out, and Wait For Ack
			clientSocket.setSoTimeout(StaticContent.UDP_RECEIVE_TIMEOUT);			
			byte[] receiveData = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
			byte[] message = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
			receiveData = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];
			
			//Deserialize Data to udpMessage Object.
			UDPMessage udpMessageReceived = Serializer.deserialize(message);
			//boolean check = udpMessageReceived.getMessageType().equals(Enums.UDPMessageType.Reply);
			//check = udpMessageReceived.getSender().equals(Enums.UDPSender.Sequencer);
			//check = udpMessageReceived.getSequencerNumber() > oldSequenceNumber;
			
			if(udpMessageReceived.getMessageType().equals(Enums.UDPMessageType.Reply) && udpMessageReceived.getSender().equals(Enums.UDPSender.Sequencer)  &&  udpMessageReceived.getSequencerNumber() > oldSequenceNumber){
				oldSequenceNumber = udpMessageReceived.getSequencerNumber();
				reply = true;	
			}
			
			
			clientSocket.close();
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
			reply = false;
		}

		return reply;
	}
	
	//Temporary Replica Linter
	public class ReplicaListner implements Runnable {
		private DatagramSocket serverSocket;
		private Thread t = null;
		private boolean continueUDP = true;		
		private int port ;
		private Enums.UDPSender machineName;
		public ReplicaListner(int replicaListeningPort, Enums.UDPSender machineName) {
			this.port =replicaListeningPort;
			this.machineName = machineName;
		}

		@Override
		public void run() {
			try {
				serverSocket = new DatagramSocket(this.port);
				byte[] receiveData = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];
				//byte[] sendData = new byte[SIZE_BUFFER_REQUEST];
				String msg = this.machineName.toString() + " UDP Server Is UP!";

				System.out.println(msg);
				while (continueUDP) {

					// Read request
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					serverSocket.receive(receivePacket);
					
					byte[] message = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
			        UDPMessage udpMessage = Serializer.deserialize(message);
					// Clear received buffer
					receiveData = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];
					boolean receivedStatus = false;
					switch (udpMessage.getSender()) {
					case Sequencer:						
						receivedStatus = true;
						// Perform Operations.
						System.out.println("Executing Opernation : "+udpMessage.getOpernation()+", on Server :"+ udpMessage.getServerName());
						break;						

					default:
						System.out.println("Unknow Sender : "+ udpMessage.getSender());
						break;
					}
					
					if(receivedStatus)
					{
						//Send Acknowledge.
						UDPMessage ackMessage = new UDPMessage(this.machineName, udpMessage.getSequencerNumber(), udpMessage.getServerName(), udpMessage.getOpernation(), Enums.UDPMessageType.Reply);
						ackMessage.setStatus(true);
						byte[] sendData = Serializer.serialize(ackMessage);
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
						serverSocket.send(sendPacket);

						// Clear Send buffer
						sendData = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];
					}

				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		
		/**
		 * Start the server thread
		 */
		public void start()
		{
			t = new Thread(this);
			t.start();
		}
		
		/**
		 * Execute a join on the thread
		 * @throws InterruptedException 
		 */
		public void join() throws InterruptedException 
		{
			if(t == null)
				return;
			
			t.join();
		}
		
		/**
		 * Stop the server thread
		 */
		public void stop()
		{
			continueUDP = false;
		}
	}
}
