import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.ArrayList; 
import java.util.Map; 
import processing.serial.Serial; 
import SimpleOpenNI.*; 
import processing.core.PApplet; 
import processing.core.PVector; 
import fingertracker.*; 
import processing.core.PVector; 
import java.util.ArrayList; 
import processing.serial.Serial; 
import processing.core.PApplet; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class FirstKinect extends PApplet {









SimpleOpenNI kinect;
HashMap<Integer, HandObject> hands; //HandObject is a class in HandObject.pde
boolean move = false;
Serial serial;
PumpController pumpController; //PumpController is a class in PumpController.pde

public void setup() {
    //String portName = Serial.list()[0];
    //serial = new Serial(this, portName, SERIAL_PORT_BAUD_RATE);
    kinect = new SimpleOpenNI(this);
    //handsList = new ArrayList<HashMap<Integer, HandObject>>();
    hands = new HashMap<Integer, HandObject>();
    //fingertracker = new FingerTracker(this, 640, 480);
    //pumpController = new PumpController(this);
    if(kinect.isInit()==false){
      println("kinect can not find");
      exit();
      return;
    }
    kinect.setMirror(true);
    kinect.enableDepth();
    kinect.enableHand();
    kinect.enableUser();
    //kinect.enableGesture();
    //fingertracker.setMeltFactor(100);
    kinect.startGesture(SimpleOpenNI.GESTURE_WAVE);
    /*stroke(255,0,0);
    strokeWeight(2);*/
    background(200,0,0);
    stroke(0,0,255);
    strokeWeight(3);
    smooth();
    size(kinect.depthWidth(), kinect.depthHeight());
}

public void draw() {
  kinect.update();
  image(kinect.depthImage(), 0, 0);
  if(hands!=null){
    for(HandObject handObject : hands.values()){
      if(handObject.handPath.size()>=5){
        int prev = handObject.handPath.size()-4;
        int current = handObject.handPath.size()-1;
        handObject.drawHandSize(handObject.motionDetect(prev, current));
        if(handObject.motionDetect(prev, current)!=0){
          //pumpController.orderShot();
          text(binary(handObject.motionDetect(prev, current),6),10,20);
        }
      }
    }
  }
}
  /*void drawSkeleton(int userId)
  {
    kinect.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND);
  }*/
  //hand events
public void keyPressed(){
  /*if(key == '0'){
    pumpController.close();
  }else if (key == '1') {
      
      pumpController.send(1, 254);
  }else if (key == '2') {
      
     pumpController.send(2, 254);
  }
  else if (key == '3') {
      
    pumpController.send(3, 254);
  }
  else if (key == '4') {
      
    pumpController.send(4, 254);
  }else if (key == '5') {
      
    pumpController.send(5, 254);
  }
  else if (key == '6') {
      
    pumpController.send(6, 254);
  }
  else if (key == '7') {
      
    pumpController.send(7, 254);
  }
  else if (key == '9') {
      
    pumpController.send(0, 254);
  }*/
}
public void onNewHand(SimpleOpenNI curKinect, int handId, PVector pos){
  println("onNewHand -- handId:" + handId + ", pos" + pos);
  kinect.convertRealWorldToProjective(pos,pos);
  if(!hands.containsKey(handId)){ //the id is not inserted
    HandObject hand =  new HandObject(handId);
    hand.savePoint(pos);
    hands.put(handId, hand);
  }else {
    println("the id is exist");
  }
}
public void onTrackedHand(SimpleOpenNI curKinect, int handId, PVector pos){
  //println("onTrackedHand!!!!"+"x: "+pos.x+" y: "+pos.y);
  kinect.convertRealWorldToProjective(pos,pos);
  if(hands.containsKey(handId)){
    //insert point
    hands.get(handId).savePoint(pos);

  }
}
public void onLostHand(SimpleOpenNI curContext,int handId)
{
  println("onLostHand - handId: " + handId);
  HandObject handToRemove = hands.get(handId);
  handToRemove.live = false;
  hands.remove(handId);
  println(hands);
    //handPathList.remove(handId);
}
public void onCompletedGesture(SimpleOpenNI curContext,int gestureType, PVector pos)
{
  println("onCompletedGesture - gestureType: " + gestureType + ", pos: " + pos);
  
  int handId = kinect.startTrackingHand(pos);
  println("hand stracked: " + handId);
}

// for tracking user skeleton
/*
void onNewUser(SimpleOpenNI curContext, int userId)
{
  println("onNewUser - userId: " + userId);
  println("\tstart tracking skeleton");
  
  curContext.startTrackingSkeleton(userId);
}

void onLostUser(SimpleOpenNI curContext, int userId)
{
  println("onLostUser - userId: " + userId);
}

void onVisibleUser(SimpleOpenNI curContext, int userId)
{
  println("onVisibleUser - userId: " + userId);
}*/



class HandObject extends Thread{
	int handId = 0;
	int traceLength = 10;
	ArrayList<PVector> handPath;
	int delta_threshold = 5;
	boolean live = false;
	HandObject(int Id){
		handId = Id;
		handPath = new ArrayList<PVector>();
		live = true;
	}
	public void start(){
		super.start();
	}
	public void run(){
		while(live){

		}
		println(handId+" is dead");
	}
	public void savePoint(PVector point){
		handPath.add(point);
	}
	public int motionDetect(int prev, int current){
		PVector prevVector = handPath.get(prev);
		PVector currentVector = handPath.get(current);
		float[] delta = { 0.0f, 0.0f, 0.0f};	
		String result = "";
		int value = 0;
		delta[0] = currentVector.x - prevVector.x;
		delta[1] = currentVector.y - prevVector.y;
		delta[2] = currentVector.z - prevVector.z;
		if(delta[0] > delta_threshold){ //x axis positive movement
			result+= "x+";
			value += 2;
		}
		else if(delta[0] < 0 && abs(delta[0]) > delta_threshold){ //x axis negative movement
			result+= "x-";
			value += 1;
		}
		else {
			result+= "x0";
		}
		if(delta[1] > delta_threshold){ //y axis positive movement
			result+= " y+";
			value += 8;
		}
		else if(delta[1] < 0 && abs(delta[1]) > delta_threshold){ //y axis negative movement
			result+= " y-";
			value += 4;
		}
		else {
			result+= " y0";
		}
		if(delta[2] > delta_threshold){ //z axis positive movement
			result+= " z+";
			value += 32;
		}
		else if (delta[2] < 0 && abs(delta[2]) > delta_threshold) { //z axis negative movement
			result+= " z-";
			value += 16;
		}
		else {
			result+= " z0";
		}
		println(result);
		return value;
	}
	public boolean detectMoveIn(){
		boolean ans = false;
		int distance = 5;
		float current_z = handPath.get(handPath.size()-1).z;
		float prev_z = handPath.get(handPath.size()-2).z;
		if(prev_z - current_z >= distance)
			return true;
		return false;
	}
	public void drawHandSize(int colorChange){
      int semiWidth=100/2,semiHeight=200/2;
      strokeWeight(10);
      if(colorChange!=0){
      	if(colorChange == 32)
      		stroke(0, 255, 255);
      	else if (colorChange == 8) 
      		stroke(0, 255, 0);
      	else if (colorChange == 2)
      		stroke(255, 0, 0);
      	else 
      		stroke(255, 255, 0);
      }else{
      	stroke(0, 0, 255);
      }
      float mid_x = handPath.get(handPath.size()-1).x;
      float mid_y = handPath.get(handPath.size()-1).y;
      line(mid_x - semiWidth, mid_y + semiHeight, mid_x - semiWidth, mid_y - semiHeight);
      line(mid_x - semiWidth, mid_y - semiHeight, mid_x + semiWidth, mid_y - semiHeight);
      line(mid_x + semiWidth, mid_y - semiHeight, mid_x + semiWidth, mid_y + semiHeight);
      line(mid_x + semiWidth, mid_y + semiHeight, mid_x - semiWidth, mid_y + semiHeight);
  	}
}


class PumpController extends Thread{
	Serial serial;
	String portName = Serial.list()[0];
	int SERIAL_PORT_BAUD_RATE = 9600;
	boolean runBit = false;
	PumpController(PApplet pde){
		serial = new Serial(pde, portName, SERIAL_PORT_BAUD_RATE);
		runBit = true;
	}
	public void start(){ //necessary function for thread
		super.start();
	}
	public void run(){ //necessary function for thread. 
		//if run was done, the thread would finish. 
		while(runBit){

		}
		println("pump thread done!");
	}
	public void send(int pumpId, int rate){
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
	public void close(){
		for(int i=0;i<8;i++)
			send(i, 0);
		println("Done");
	}
	public void orderShot(){
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
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "FirstKinect" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
