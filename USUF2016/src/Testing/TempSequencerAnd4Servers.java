package Testing;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import FE.TimeOutTask;
import ReliableUDP.Reciever;
import StaticContent.StaticContent;

public class TempSequencerAnd4Servers {
	
	static String addressFE = "";
	static int portFE = 0;
	
	public static void main(String[] args){
		
		
		int port = StaticContent.SEQUENCER_lISTENING_PORT;
		DatagramSocket socket;
		try {
			socket = new DatagramSocket(port);
			System.out.println("Temp sequencer is up and running.");
		boolean isWaiting = true;
		
		
		
		while(isWaiting){
			
		//	try {
				Reciever r = new Reciever(socket);
				
				
				
				InetAddress aHost = r.getData().getFrontEndIP();
				portFE = r.getData().getFrontEndPort();
							
			//	String msgACK = "Ack:0";
			//	DatagramPacket replyPacket2 = new DatagramPacket(msgACK.getBytes(), msgACK.length(), aHost, portFE);
			//	socket.send(replyPacket2);
				isWaiting = false;
		//	} catch (IOException e) {
				//TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
		}
		
		socket.close();
		
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		send("true:Sajjad");
		send("true:Ulan");
		send("true:Ulan");
		send("true:Ulan");
		
		
		
		
		
	}
	
	public static void send(String new_msg){
		
		try {
			
		DatagramSocket socket = new DatagramSocket();
		System.out.println("my port:"+ socket.getLocalPort());
		
		InetAddress aHost = InetAddress.getByName("127.0.0.1");
		
		
		DatagramPacket requestPacket = new DatagramPacket(new_msg.getBytes(), new_msg.length(), aHost, portFE);
		socket.send(requestPacket);
		System.out.println("Request sent to FE: " + new String(requestPacket.getData()));
		
	//	byte[] buffer1 = new byte[1000];
	//	DatagramPacket replyPacket = new DatagramPacket(buffer1, buffer1.length);	
	//	socket.receive(replyPacket);
	//	System.out.println("Reply Ack received from FE: " + new String(replyPacket.getData()));	
		
		
		
		}catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
