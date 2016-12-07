package Testing;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

import Models.UDPMessage;
import ReliableUDP.Reciever;
import ReliableUDP.Sender;
import StaticContent.StaticContent;
import Utilities.Serializer;

public class TempSequencer {

	public static void main(String[] args) throws SocketException {

		System.out.println("Temp sequencer is up and running.");

		try {

			boolean isWaiting = true;
			DatagramSocket socketReceiver = new DatagramSocket(StaticContent.SEQUENCER_lISTENING_PORT);
			while (isWaiting) {

				// Reciever r = new Reciever(scoketReceiver);
				// System.out.println("the data received is : "+
				// r.getData().getServerName());
				// r.getData().setFrontEndPort(frontEndPort)

				byte[] receiveData = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				socketReceiver.receive(receivePacket);

				byte[] message = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
				// Deserialize Data to udpMessage Object.
				UDPMessage udpMessageReceived = Serializer.deserialize(message);
				receiveData = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];
				
				
				System.out.println(udpMessageReceived.getParamters());

				udpMessageReceived.setReplyMsg("ACK");
				
				// Serialize udpMessage
				byte[] sendData = Serializer.serialize(udpMessageReceived);
				//Send UDP Message
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
				socketReceiver.send(sendPacket);
				
				
				udpMessageReceived.setReplyMsg("Ulan");
				sendDoc(udpMessageReceived, StaticContent.REPLICA_ULAN_IP_ADDRESS,
						StaticContent.REPLICA_ULAN_lISTENING_PORT,
						StaticContent.SEQUENCER_ACK_PORT_FOR_REPLICA_ULAN);
				
				udpMessageReceived.setReplyMsg("Sajjad");
				sendDoc(udpMessageReceived, StaticContent.REPLICA_SAJJAD_IP_ADDRESS,
						StaticContent.REPLICA_SAJJAD_lISTENING_PORT,
						StaticContent.SEQUENCER_ACK_PORT_FOR_REPLICA_SAJJAD);

				udpMessageReceived.setReplyMsg("Ulan");
				sendDoc(udpMessageReceived, StaticContent.REPLICA_UMER_IP_ADDRESS,
						StaticContent.REPLICA_UMER_lISTENING_PORT,
						StaticContent.SEQUENCER_ACK_PORT_FOR_REPLICA_UMER);

				udpMessageReceived.setReplyMsg("Ulan");
		//		sendDoc(udpMessageReceived, StaticContent.REPLICA_FERAS_IP_ADDRESS,
		//				StaticContent.REPLICA_FERAS_lISTENING_PORT,
		//				StaticContent.SEQUENCER_ACK_PORT_FOR_REPLICA_FERAS);

			}
			socketReceiver.close();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void sendDoc(UDPMessage udpMsg, String aHost, int aPort,
			int ackPort) throws SocketException {

	//	Sender s = new Sender(aHost, aPort, false, new DatagramSocket());
	//	s.send(udpMsg);
		try {
		DatagramSocket socket = new DatagramSocket(aPort);
		
		int portFE = udpMsg.getFrontEndPort();
		InetAddress aHostFE = udpMsg.getFrontEndIP();
		
		byte[] sendData = Serializer.serialize(udpMsg);
		//Send UDP Message
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, aHostFE, portFE);
		socket.send(sendPacket);
		
		
		
		
		byte[] receiveData = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		socket.receive(receivePacket);
		

		byte[] message = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
		// Deserialize Data to udpMessage Object.
		UDPMessage udpMessageReceived = Serializer.deserialize(message);
		receiveData = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
