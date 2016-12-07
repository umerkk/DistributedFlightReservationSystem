package Testing;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import FE.TimeOutTask;
import Models.Enums;
import Models.UDPMessage;
import ReliableUDP.Reciever;
import ReliableUDP.Sender;
import StaticContent.StaticContent;
import Utilities.Serializer;

public class Temp4Servers {

	public static void main(String[] args) {

		send(StaticContent.REPLICA_ULAN_IP_ADDRESS, StaticContent.REPLICA_ULAN_lISTENING_PORT,
				StaticContent.SEQUENCER_ACK_PORT_FOR_REPLICA_ULAN, "Ulan");
		send(StaticContent.REPLICA_SAJJAD_IP_ADDRESS, StaticContent.REPLICA_SAJJAD_lISTENING_PORT,
				StaticContent.SEQUENCER_ACK_PORT_FOR_REPLICA_SAJJAD, "Sajjad");
		send(StaticContent.REPLICA_UMER_IP_ADDRESS, StaticContent.REPLICA_UMER_lISTENING_PORT,
				StaticContent.SEQUENCER_ACK_PORT_FOR_REPLICA_UMER, "Ulan");
		send(StaticContent.REPLICA_FERAS_IP_ADDRESS, StaticContent.REPLICA_FERAS_lISTENING_PORT,
				StaticContent.SEQUENCER_ACK_PORT_FOR_REPLICA_FERAS, "Ulan");

	}

	public static void send(final String ip_address, final int port, final int acknowledgementPort,
			final String new_ans) {
		System.out.println("server is up: " + ip_address + " , port: " + port);

		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("server is waiting, ip: " + ip_address + " , port: " + port);
					DatagramSocket scoketReceiver = null;
					scoketReceiver = new DatagramSocket(port);

					Reciever r = new Reciever(scoketReceiver);
					UDPMessage udpMessage = r.getData();
					System.out.println("the data received is : ");
					System.out.println("sender: " + r.getData().getSender());
					System.out.println("front end ip: " + r.getData().getFrontEndIP());
					System.out.println("front end port: " + r.getData().getFrontEndPort());
					System.out.println("server name: " + r.getData().getServerName());
					System.out.println("manager id: " + r.getData().getManagerID());
					System.out.println("sequence number: " + r.getData().getSequencerNumber());
					System.out.println("operation: " + r.getData().getOpernation());
					System.out.println("reply message: " + r.getData().getReplyMsg());
					System.out.println("message parameters: " + r.getData().getParamters());
					scoketReceiver.close();

					InetAddress aHostFE;

					aHostFE = r.getData().getFrontEndIP();
					int portFE = r.getData().getFrontEndPort();
					
					System.out.println("Now I am sending for Foront End  : " + portFE);
					
					
				//	Sender s = new Sender(StaticContent.FRONT_END_IP_ADDRESS, portFE, true, socket);
				
					
		//			UDPMessage udpMessageReply = new  UDPMessage(Enums.UDPSender.ReplicaUlan, udpMessage.getSequencerNumber(), udpMessage.getServerName(), udpMessage.getOpernation(), Enums.UDPMessageType.Reply);
		//			udpMessageReply.setReplyMsg("true:"+new_ans);
					
					String udpMessageReply = "true:"+new_ans;
					
		//			s.send(udpMessage);
						
		//			byte[] sendData = Serializer.serialize(udpMessageReply);
										
					DatagramSocket socket = new DatagramSocket();
					System.out.println("my socket is : "+ socket.getLocalPort());
				
			//		DatagramPacket replyPacket = new DatagramPacket(sendData, sendData.length, aHostFE, portFE);
					DatagramPacket replyPacket = new DatagramPacket(udpMessageReply.getBytes(), udpMessageReply.length(), aHostFE, portFE);
					socket.send(replyPacket);
			//		sendData = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];
								
					if(socket!=null){
						socket.close();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		t2.start();

	}

}
