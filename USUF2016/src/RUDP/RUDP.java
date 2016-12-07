package RUDP;

import java.math.BigInteger;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class RUDP extends DatagramSocket {

	public RUDP() throws SocketException {
		super();
		// TODO Auto-generated constructor stub
	}

	public String generateCheckSum(String msg) {

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA");

			md.update(msg.getBytes());

			return DatatypeConverter.printHexBinary(md.digest());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "none";

	}

	public boolean checkCheckSum(String checksum, String msg) {

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA");

			md.update(msg.getBytes());

			if (DatatypeConverter.printHexBinary(md.digest()).toString().equals(checksum)) {
				return true;
			}

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;

	}

}
