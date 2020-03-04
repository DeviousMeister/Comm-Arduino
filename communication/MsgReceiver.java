package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import jssc.*;

public class MsgReceiver {
	
	final private SerialComm port;
	
	public MsgReceiver(String portname) throws SerialPortException {
		port = new SerialComm(portname);
	}
	enum State{
		magic,
		key,
		info,
		error,
		timeStamp,
		potentio,
		tempRead,
	};
	
	//bit shifting, !!!!error thing, potentiometer, wiring lol.
	

	int filterCount = 7;
	long count = 0;
	float[] temps = new float [filterCount];
	
	public void run() throws IOException {
		// insert FSM code here to read msgs from port
		// and write to console
		State state = State.magic;
		while(true) {	
			switch(state) {
			case magic:
				if(port.available()) {
					if(port.readByte()=='!') {
						state = State.key;
					}
					else {
						System.out.println("	!!!!!!! 1st Error");
					}
				}
				break;
			case key:
				byte place = port.readByte();
				if(place==0x30) {
					state = State.info;
				}
				else if(place==0x32) {
					state = State.timeStamp;
				}
				else if(place==0x33) {
					state = State.potentio;
				}
				else if(place==0x34) {
					state = State.tempRead;
				}
				else if(place==0x31) {
					state = State.error;
				}
				else {
					System.out.println("	!!!!!!! 2nd Error");
				}
				break;
			case info:
				int first = port.readByte() & 0xff;
				int second = port.readByte() & 0xff;
				first = (first << 8) | second;
				byte[] array = new byte[first];
				for(int i = 0; i<array.length; ++i) {
					array[i] = port.readByte();
				}
				String myString = new String(array, StandardCharsets.UTF_8);
				System.out.println("Info String: " + myString);
				state = State.key;
				break;
			case timeStamp:
				int a = port.readByte() & 0xff;
				int b = port.readByte() & 0xff;
				int c = port.readByte() & 0xff;
				int d = port.readByte() & 0xff;
				a = (a << 24) | (b << 16);
				c = (c << 8) | d;
				a = a | c;
				System.out.println("Time Stamp Int: " + a);
				state = State.key;
				break;
			case potentio:
				int a1 = (int)port.readByte() & 0xff;
				int b1 = (int)port.readByte() & 0xff;
//				a1 = (a1 << 8) + b1;
				System.out.println(a1);
				System.out.println(b1);
				a1 = a1 << 8;
				a1 += b1;
				System.out.println("Potentiometer Reading Int: " + a1);
				state = State.key;
				break;
			case tempRead:
				int a11 = port.readByte() & 0xff;
				int b11 = port.readByte() & 0xff;
				a11 = (a11 << 8) | (b11);
				float val = (float)a11;
				float input = (float) ((5.0/1023.0)*val);
				float temp = (float) (25 + ((input -.75)*100));
				temps[(int) (count % filterCount)] = temp;
				  float sum = 0;
				  for(int i = 0; i<filterCount; ++i){
				    sum = temps[i] + sum;
				  }
				  float filtered = sum/filterCount;
				  count += 1 ;
				System.out.println("Raw Temp Reading Int: " + a11 + " Converted Temp: " + temp  + " Filtered Temp: " + filtered);
				state = State.key;
				break;
			case error:
				int first1 = port.readByte() & 0xff;
				int second1 = port.readByte() & 0xff;
				first1 = (first1 << 8) | second1;
				byte[] array1 = new byte[first1];
				for(int i = 0; i<array1.length; ++i) {
					array1[i] = port.readByte();
				}
				String myString1 = new String(array1, StandardCharsets.UTF_8);
				System.out.println("Error String: " + myString1);
				state = State.key;
				break;
			default:
				state = State.magic;
				break;
			
			}
		}
		
		

	}
	public static void main(String[] args) throws SerialPortException, IOException {
		MsgReceiver msgr = new MsgReceiver("COM3"); // Adjust this to be the right port for your machine
		msgr.run();
	}
}
