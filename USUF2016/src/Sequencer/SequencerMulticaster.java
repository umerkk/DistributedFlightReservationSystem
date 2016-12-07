package Sequencer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import Models.Enums;
import Models.UDPMessage;
import ReliableUDP.Sender;
import StaticContent.StaticContent;
import Utilities.CLogger;
import Utilities.Serializer;

public class SequencerMulticaster {
	// HashMap<String, UdpReplicaServiceHandleRequestThread> dicHandleRequest =
	// new HashMap<String, UdpReplicaServiceHandleRequestThread>();
	private HashMap<String, UDPMessage> bufferRMUlan;
	private HashMap<String, UDPMessage> bufferRMSajjad;
	private HashMap<String, UDPMessage> bufferRMUmer;
	private HashMap<String, UDPMessage> bufferRMFeras;

	private CLogger clogger;

	public SequencerMulticaster(CLogger clogger) {
		this.clogger = clogger;
		bufferRMUlan = new HashMap<String, UDPMessage>();
		bufferRMSajjad = new HashMap<String, UDPMessage>();
		bufferRMUmer = new HashMap<String, UDPMessage>();
		bufferRMFeras = new HashMap<String, UDPMessage>();
	}

	public void multicatMessage(UDPMessage udpMessage) {
		// Update Sender
		udpMessage.setSender(Enums.UDPSender.Sequencer);

		// Buffer msg in All 4 replicas
		bufferRMUlan.put(udpMessage.getSequencerNumber() + "", udpMessage);
		bufferRMSajjad.put(udpMessage.getSequencerNumber() + "", udpMessage);
		bufferRMUmer.put(udpMessage.getSequencerNumber() + "", udpMessage);
		bufferRMFeras.put(udpMessage.getSequencerNumber() + "", udpMessage);

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		System.out.println("Start Sending : " + timestamp);

		int count = 4;
		Thread[] threads = new Thread[count];
		MulticastThread[] multicastThread = new MulticastThread[count];

		// Start threads
		multicastThread[0] = new MulticastThread(StaticContent.REPLICA_ULAN_IP_ADDRESS,
				StaticContent.REPLICA_ULAN_lISTENING_PORT, StaticContent.SEQUENCER_ACK_PORT_FOR_REPLICA_ULAN,
				bufferRMUlan, Enums.UDPSender.ReplicaUlan);
		threads[0] = new Thread(multicastThread[0]);
		threads[0].start();

		multicastThread[1] = new MulticastThread(StaticContent.REPLICA_SAJJAD_IP_ADDRESS,
				StaticContent.REPLICA_SAJJAD_lISTENING_PORT, StaticContent.SEQUENCER_ACK_PORT_FOR_REPLICA_SAJJAD,
				bufferRMSajjad, Enums.UDPSender.ReplicaSajjad);
		threads[1] = new Thread(multicastThread[1]);
		threads[1].start();

		multicastThread[2] = new MulticastThread(StaticContent.REPLICA_UMER_IP_ADDRESS,
				StaticContent.REPLICA_UMER_lISTENING_PORT, StaticContent.SEQUENCER_ACK_PORT_FOR_REPLICA_UMER,
				bufferRMUmer, Enums.UDPSender.ReplicaUmer);
		threads[2] = new Thread(multicastThread[2]);
		threads[2].start();

		multicastThread[3] = new MulticastThread(StaticContent.REPLICA_FERAS_IP_ADDRESS,
				StaticContent.REPLICA_FERAS_lISTENING_PORT, StaticContent.SEQUENCER_ACK_PORT_FOR_REPLICA_FERAS,
				bufferRMFeras, Enums.UDPSender.ReplicaFeras);
		threads[3] = new Thread(multicastThread[3]);
		threads[3].start();

		timestamp = new Timestamp(System.currentTimeMillis());
		System.out.println("After Thread Start : " + timestamp);

		String msg = "";
		// get data from threads
		for (int i = 0; i < count; i++) {
			if (threads[i] != null) {

				try {
					threads[i].join();
					if (multicastThread[i].getStatus()) {
						msg = "Request " + (i + 1) + " reach Successfully.";
					} else {
						msg = "Request " + (i + 1) + " failed to reach.";
					}

					System.out.println(msg);
					clogger.log(msg);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		timestamp = new Timestamp(System.currentTimeMillis());
		System.out.println("At End : " + timestamp);

	}

	public synchronized void clearBuffer(UDPMessage udpMessage) {

		switch (udpMessage.getSender()) {
		case ReplicaUlan:
			bufferRMUlan.remove(udpMessage.getSequencerNumber() + "");
			break;
		case ReplicaSajjad:
			bufferRMSajjad.remove(udpMessage.getSequencerNumber() + "");
			break;
		case ReplicaUmer:
			bufferRMUmer.remove(udpMessage.getSequencerNumber() + "");
			break;
		case ReplicaFeras:
			bufferRMFeras.remove(udpMessage.getSequencerNumber() + "");
			break;

		default:
			break;
		}

	}

	/**
	 * UDP Call (Client call) to other server
	 * 
	 * @param remoteServer
	 * @param ip
	 * @param port
	 * @param operation
	 * @param augs
	 * @return
	 */
	private boolean UPDCall(String destinationIP, int destinationPort, int acknowledgementPort,
			HashMap<String, UDPMessage> udpMessageMap, Enums.UDPSender sendsTo) {
		boolean reply = false;
		String msg = "";
		msg = Enums.UDPSender.Sequencer.toString() + " Sending to " + sendsTo.toString() + " " + destinationIP + ":"
				+ destinationPort + ", with listening Acknowledment on : " + acknowledgementPort;
		System.out.println(msg);
		clogger.log(msg);
		DatagramSocket socket = null;
		DatagramSocket clientSocket = null;

		try {

			UDPMessage udpMessage = null;
			Map map = Collections.synchronizedMap(udpMessageMap);
			Set set = map.entrySet();
			// Get an iterator
			Iterator i = set.iterator();
			clientSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(destinationIP);

			// Send All Messages from Buffer one by one
			while (i.hasNext()) {
				Map.Entry me = (Map.Entry) i.next();
				udpMessage = (UDPMessage) me.getValue();

				// Serialize udpMessage
				byte[] sendData = Serializer.serialize(udpMessage);
				// Compute ChecK sum here

				// Send UDP Message
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, destinationPort);
				clientSocket.send(sendPacket);

				// socket= new DatagramSocket();
				try {
					// Set Time Out, and Wait For Ack
					clientSocket.setSoTimeout(StaticContent.UDP_RECEIVE_TIMEOUT);
					byte[] receiveData = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					clientSocket.receive(receivePacket);
					byte[] message = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
					//
					// Deserialize Data to udpMessage Object.
					UDPMessage udpMessageReceived = Serializer.deserialize(message);
					receiveData = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];
					switch (udpMessageReceived.getSender()) {
					case ReplicaUlan:
					case ReplicaSajjad:
					case ReplicaUmer:
					case ReplicaFeras:
						msg = "Reply FROM " + udpMessageReceived.getSender().toString() + " SERVER:"
								+ udpMessageReceived.getSequencerNumber();
						System.out.println(msg);
						clogger.log(msg);
						if (udpMessageReceived.getSequencerNumber() == udpMessage.getSequencerNumber()) {
							clearBuffer(udpMessageReceived);
							reply = true;
						}

						break;

					default:
						reply = false;
					}

				} catch (SocketTimeoutException e) {
					System.out.println("Time Out occor!");
					reply = false;
					// resend
					// socket.send(data);
					// continue;
				}

			}

		} catch (Exception ex) {
			// reply = "Error: encouter on " + ServerName + ", Message: " +
			// ex.getMessage();
			clogger.logException("on starting UDP Server", ex);
			ex.printStackTrace();
			reply = false;
		}

		finally {
			if (socket != null && !socket.isClosed())
				socket.close();
		}

		return reply;
	}

	private UDPMessage getEldest(HashMap<String, UDPMessage> bufferMap) {
		UDPMessage udpMessage = null;
		Map map = Collections.synchronizedMap(bufferMap);
		Set set = map.entrySet();
		// Set set = passengerData.entrySet();
		// synchronized (map) {
		// Get an iterator
		Iterator i = set.iterator();

		// Display elements
		while (i.hasNext()) {
			Map.Entry me = (Map.Entry) i.next();
			udpMessage = (UDPMessage) me.getValue();
			break;
		}
		return udpMessage;
	}

	class MulticastThread implements Runnable {

		HashMap<String, UDPMessage> udpMessageMap;
		Boolean status = false;
		int destinationPort;
		int acknowledgementPort;
		String destinationIP;
		Enums.UDPSender sendsTo;

		public MulticastThread(String destinationIP, int destinationPort, int acknowledgementPort,
				HashMap<String, UDPMessage> udpMessageMap, Enums.UDPSender sendsTo) {
			// store parameter for later user
			this.destinationIP = destinationIP;
			this.destinationPort = destinationPort;
			this.acknowledgementPort = acknowledgementPort;
			this.udpMessageMap = udpMessageMap;
			this.sendsTo = sendsTo;
		}

		public void run() {
			status = UPDCall(destinationIP, destinationPort, acknowledgementPort, udpMessageMap, sendsTo);
		}

		public Boolean getStatus() {
			return status;
		}
	}
}
