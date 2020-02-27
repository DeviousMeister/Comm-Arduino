package communication;

import jssc.*;

public class MsgReceiver {
	
	final private SerialComm port;
	
	public MsgReceiver(String portname) throws SerialPortException {
		port = new SerialComm(portname);
	}

	public void run() {
		// insert FSM code here to read msgs from port
		// and write to console


	}

	public static void main(String[] args) throws SerialPortException {
		MsgReceiver msgr = new MsgReceiver("COM4"); // Adjust this to be the right port for your machine
		msgr.run();
	}
}
