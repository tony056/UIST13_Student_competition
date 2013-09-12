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
FingerTracker fingertracker;
int fingerThreshold = 0;
HashMap<Integer, HandObject> hands;
boolean getFirst = false;
int firstHand = -100;
int secondHand = -100;
boolean move = false;
Serial serial;
int SERIAL_PORT_BAUD_RATE = 9600;
int rate = 100;
int[] protocol = {255, 0,100};
int prev=0,current=0;
PumpController pumpController;
public void setup() {
    //String portName = Serial.list()[0];
    //serial = new Serial(this, portName, SERIAL_PORT_BAUD_RATE);
    kinect = new SimpleOpenNI(this);
    //handsList = new ArrayList<HashMap<Integer, HandObject>>();
    hands = new HashMap<Integer, HandObject>();
    //fingertracker = new FingerTracker(this, 640, 480);
    pumpController = new PumpController(this);
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
        handObject.drawHandSize(handObject.detectMoveIn());
        if(handObject.detectMoveIn()){
          pumpController.orderShot();
          /*if(!move){
            rate = 0;
            move = true;
          }
          else{
            rate = 100;
            move = false;
          }
          if(fingerThreshold<8){
            pumpController.send(fingerThreshold, 150);
            fingerThreshold++;
          }*/
          
            //pumpController.send(handObject.handId%8, rate);
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
    if(key == '0'){
      pumpController.close();
    }else if (key == '1') {
      for(int i=0;i<8;i++)
        pumpController.send(i, 150);
    }
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
    //println("x: "+pos.x+" y: "+pos.y);
    if(hands.containsKey(handId)){
      //insert point
      hands.get(handId).savePoint(pos);

    }
  }
  public void onLostHand(SimpleOpenNI curContext,int handId)
  {
    println("onLostHand - handId: " + handId);

    //handPathList.remove(handId);
  }
  public void onCompletedGesture(SimpleOpenNI curContext,int gestureType, PVector pos)
  {
    println("onCompletedGesture - gestureType: " + gestureType + ", pos: " + pos);
  
    int handId = kinect.startTrackingHand(pos);
    println("hand stracked: " + handId);
  }
  /*void onCreateHands(int handId,PVector position,float time){
    kinect.convertRealWorldToProjective(position, position);
    handPositions.add(position);
  }
  void onUpdateHands(int handId, PVector position, float time){
    kinect.convertRealWorldToProjective(position, position);
    handPositions.add(position);
  }
  void onDestroyHands(int handId, float time){
    handPositions.clear();
    kinect.stopTrackingHand(SimpleOpenNI.GESTURE_HAND_RAISE);
    
  }
  void onRecognizeGesture(String strGesture,PVector idPosition,PVector endPosition)
  {
    kinect.startTrackingHand(endPosition);
    kinect.endGesture(SimpleOpenNI.GESTURE_HAND_RAISE);
  }*/
  public void onNewUser(SimpleOpenNI curContext, int userId)
  {
  println("onNewUser - userId: " + userId);
  println("\tstart tracking skeleton");
  
  curContext.startTrackingSkeleton(userId);
  }

public void onLostUser(SimpleOpenNI curContext, int userId)
{
  println("onLostUser - userId: " + userId);
}

public void onVisibleUser(SimpleOpenNI curContext, int userId)
{
  //println("onVisibleUser - userId: " + userId);
}


class HandObject{
	int handId = 0;
	int traceLength = 10;
	ArrayList<PVector> handPath;
	HandObject(int Id){
		handId = Id;
		handPath = new ArrayList<PVector>();
	}
	public int getId(){
		return handId;
	}
	public void savePoint(PVector point){
		handPath.add(point);
	}
	public boolean detectMoveIn(){
		boolean ans = false;
		int distance = 5;
		float current_z = handPath.get(handPath.size()-1).z;
		float prev_z = handPath.get(handPath.size()-2).z;
		if(prev_z - current_z >= 5)
			return true;
		return false;
	}
	public void drawHandSize(boolean colorChange){
      int semiWidth=100/2,semiHeight=200/2;
      strokeWeight(10);
      if(colorChange){
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


class PumpController{
	Serial serial;
	String portName = Serial.list()[0];
	int SERIAL_PORT_BAUD_RATE = 9600;
	PumpController(PApplet pde){
		serial = new Serial(pde, portName, SERIAL_PORT_BAUD_RATE);
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
		//int pumpId = start % 8;
		int rate = 250;
		int times = 0;
		for(int i=0;i<8;i++){
            if(i>=1)
              send(i-1, 0);
            else
              send(7, 0);
              send(i, rate);
              delay(50);
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
