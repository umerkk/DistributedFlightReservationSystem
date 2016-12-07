package FE;

import java.util.TimerTask;

public class TimeOutTask extends TimerTask{
	
	boolean timeOut = false;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		timeOut = true;
		System.err.println("time our has occured!");
	}
	
	public boolean getTimeOut(){
		return timeOut;
	}

}
