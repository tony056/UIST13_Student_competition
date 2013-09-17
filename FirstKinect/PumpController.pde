import processing.serial.Serial;
import processing.core.PApplet;
class PumpController extends Thread{
	Serial serial;
	String portName = Serial.list()[0];
	int SERIAL_PORT_BAUD_RATE = 9600;
	boolean runBit = false;
	PumpController(PApplet pde){
		serial = new Serial(pde, portName, SERIAL_PORT_BAUD_RATE);
		runBit = true;
	}
	void start(){ //necessary function for thread
		super.start();
	}
	void run(){ //necessary function for thread. 
		//if run was done, the thread would finish. 
		while(runBit){

		}
		println("pump thread done!");
	}
	void send(int pumpId, int rate){
		if(pumpId >= 0 && pumpId <= 7){
			pumpId = pumpId;
		}else{
			pumpId = 0;
		}
		if(rate <= 254 && rate >= 0){
			rate = rate;	
		}else if(rate > 254){
			rate = 254;
		}else{
			rate = 0;
		}
		int[] protocol = {255, pumpId, rate};
		for(int i = 0;i<3;i++){
			serial.write(protocol[i]);	
		}
	}
	void close(){
		for(int i=0;i<8;i++)
			send(i, 0);
		println("Done");
	}
	void orderShot(){
		//int pumpId = start % 8;
		int rate = 250;
		int times = 0;
		for(int i=0;i<8;i++){
            if(i>=1)
              send(i-1, 0);
            else
              send(7, 0);
            send(i, rate);
            delay(100);
         }
	}
}