package Sequencer;

import java.util.logging.Logger;

import ReliableUDP.Reciever;
import StaticContent.StaticContent;
import Utilities.CLogger;

public class SequencerMain {

	private static CLogger clogger;
	private final static Logger LOGGER = Logger.getLogger(SequencerMain.class.getName());
	

	public static void main(String[] args) {
		String msg ="";
		try {
			// initialize logger
			clogger = new CLogger(LOGGER, "Sequencer/Sequencer.log");
			msg = "Sequencer is UP!";
			clogger.log(msg);
			System.out.println(msg);

			// Start UDP Server
			SequencerListner server = new SequencerListner(clogger);
			server.start();
//			//server.executeTestMessage();
//			server.join();			

		} catch (Exception e) {
			System.out.println("Sequencer Exception: " + e.getMessage());
		}
	}
}
