package RUDP;

import java.net.SocketException;
import java.util.HashMap;

import Models.Enums;
import Models.UDPMessage;

public class MainRUDP {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			RUDP socket = new RUDP();
			
			System.out.println("my port: "+socket.getLocalPort());
			
			UDPMessage udpMsg = new UDPMessage(Enums.UDPSender.FrontEnd, -1, Enums.getFlightCitiesFromString("Montreal"),
					Enums.Operations.bookFlight, Enums.UDPMessageType.Request);
			HashMap<String, String> parameterMap = new HashMap<String, String>();
			parameterMap.put("firstName", "Ulan");
			parameterMap.put("lastName", "Baitassov");
			parameterMap.put("address", "Verdun");
			parameterMap.put("phone", "5145606164");
			parameterMap.put("destination", "Washington");
			parameterMap.put("date", "02/12/2016");
			parameterMap.put("classFlight", "economy");
			udpMsg.setParamters(parameterMap);		
			udpMsg.setManagerID("-1");		
			udpMsg.setFrontEndPort(-1);
			
			
			String s = socket.generateCheckSum(udpMsg.toString());
			
			System.out.println("msg: "+udpMsg.toString());
			
			System.out.println("checksum = "+ s);
			//14745A057FB5B110B5F1A606336924B3530FA2F8
			//9F861BBC69D919AE1AC869FE82FA5CBBB2BAABBB
			//D5C69119C8DD6DF023A330A2E7817883930F1938
			
			boolean res = socket.checkCheckSum(s, udpMsg.toString());
			
			System.out.println(res);
			
			socket.close();
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
