package feras.ReplicaManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.TimerTask;

import Models.Enums;
import Models.UDPMessage;
import feras.StaticContent.StaticContent;
import feras.Utilities.Serializer;

public class RMHeartBeat extends TimerTask {

	public void run() {

		DatagramSocket socket;
		try {
			socket = new DatagramSocket();

			UDPMessage hearBeatmsg = new UDPMessage(Enums.UDPSender.ReplicaFeras, -1, Enums.FlightCities.Montreal,
					Enums.Operations.heatBeat, Enums.UDPMessageType.Request);

			byte[] sendData = Serializer.serialize(hearBeatmsg);
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
					InetAddress.getByName(StaticContent.REPLICA_FERAS_IP_ADDRESS),
					StaticContent.REPLICA_FERAS_lISTENING_PORT);

			socket.send(sendPacket);

			// Sender s = new Sender(StaticContent.REPLICA_UMER_IP_ADDRESS,
			// StaticContent.REPLICA_UMER_lISTENING_PORT,
			// false, socket);
			System.out.println("Sending heartbeat on port: " + StaticContent.REPLICA_FERAS_lISTENING_PORT);

			// socket= new DatagramSocket();
			try {
				// Set Time Out, and Wait For Ack
				socket.setSoTimeout(StaticContent.UDP_RECEIVE_TIMEOUT);
				byte[] receiveData = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				socket.receive(receivePacket);
				byte[] message = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());

				//
				// Deserialize Data to udpMessage Object.
				UDPMessage udpMessageReceived = Serializer.deserialize(message);
				receiveData = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];

				if (udpMessageReceived.getStatus()) {
					// release Port
					ReplicaManagerMain.isReplicaAlive = true;
					System.out.println("HeartBeat successfully sent to Replica.");
					if (socket != null && !socket.isClosed())
						socket.close();
				} else {
					// Replica is dead, restart it through that thread.
					ReplicaManagerMain.isReplicaAlive = false;
					ReplicaManagerMain.restartReplica();
				}

			} catch (SocketTimeoutException | ClassNotFoundException e) {
				System.out.println("Time Out occor!");
				// resend
				// socket.send(data);
				// continue;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
