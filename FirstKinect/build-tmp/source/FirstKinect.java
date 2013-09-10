import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.ArrayList; 
import processing.serial.Serial; 
import SimpleOpenNI.*; 
import processing.core.PApplet; 
import processing.core.PVector; 
import fingertracker.*; 
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
int fingerThreshold = 625;
  ArrayList<PVector> handPositions,handPositionsSec;
  PVector currentHand;
  PVector previousHand;
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
    handPositions = new ArrayList<PVector>();
    handPositionsSec = new ArrayList<PVector>();
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
    //fingertracker.setThreshold(fingerThreshold);
    //if(kinect.isTrackingSkeleton(
    /*for(int i=1;i<handPositions.size();i++){
      currentHand = handPositions.get(i);
      previousHand = handPositions.get(i-1);
      line(currentHand.x,currentHand.y, previousHand.x, previousHand.y);
      println("line!!!!!!!!!");
    }*/
    /*int[] depthMap = kinect.depthMap();
    fingertracker.update(depthMap);
    stroke(0, 255, 0);

    if(prev==0){
      prev = fingertracker.getNumFingers();
    }else {
        prev = current;
    }
    current = fingertracker.getNumFingers();
    println("pre: "+prev+" cur: "+current);
    for (int k = 0; k < fingertracker.getNumContours(); k++) {
      //println("haha");  
      fingertracker.drawContour(k);
    }
    noStroke();
    fill(255,0,0);
    for (int i = 0; i < fingertracker.getNumFingers(); i++) {
      PVector position = fingertracker.getFinger(i);
      ellipse(position.x - 5, position.y -5, 10, 10);
    }*/
    /*if(prev - current >=5){
      rate=0;
      for(int i=0;i<3;i++){
        protocol[2]=0;
        serial.write(protocol[i]);
      }
      text(prev - current,10,20);
    }else if(current - prev >=3){
      rate=150;
      for(int i=0;i<3;i++){
        serial.write(protocol[i]);
      }
    }else{
      protocol[2]=rate;
      for(int i=0;i<3;i++){
        serial.write(protocol[i]);
      }
    }*/
    if(handPositions.size()>=2){
      previousHand = handPositions.get(handPositions.size()-2);
      currentHand = handPositions.get(handPositions.size()-1);
      move = judgeMove(previousHand.z, currentHand.z);
      drawHandSize(currentHand.x, currentHand.y);
      pumpController.send(0, rate);
    }
    /*if(handPositionsSec.size()>=2){
      previousHand = handPositionsSec.get(handPositionsSec.size()-2);
      currentHand = handPositionsSec.get(handPositionsSec.size()-1);
      move = judgeMove(previousHand.z, currentHand.z);
      drawHandSize(currentHand.x, currentHand.y);
    }*/
  }
  /*void drawSkeleton(int userId)
  {
    kinect.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND);
  }*/
  //hand events
  public void onNewHand(SimpleOpenNI curKinect, int handId, PVector pos){
    println("onNewHand -- handId:" + handId + ", pos" + pos);
    kinect.convertRealWorldToProjective(pos,pos);
    if(getFirst!=true){
      handPositions.add(pos);
      getFirst=true;
      firstHand = handId;
    }else{
      handPositionsSec.add(pos);
      secondHand = handId;
    }
  }
  public void onTrackedHand(SimpleOpenNI curKinect, int handId, PVector pos){
    //println("onTrackedHand!!!!"+"x: "+pos.x+" y: "+pos.y);
    kinect.convertRealWorldToProjective(pos,pos);
    //println("x: "+pos.x+" y: "+pos.y);
    if(handId == firstHand)
      handPositions.add(pos);
    else if(handId == secondHand)
      handPositionsSec.add(pos); 
  }
  public void onLostHand(SimpleOpenNI curContext,int handId)
  {
    println("onLostHand - handId: " + handId);
    if(handId == firstHand){
      firstHand = -100;
      getFirst = false;
      handPositions.clear();
    }
    else if (handId == secondHand) {
      secondHand = -100;
      handPositionsSec.clear();
    }
    //handPathList.remove(handId);
  }
  public void onCompletedGesture(SimpleOpenNI curContext,int gestureType, PVector pos)
  {
    println("onCompletedGesture - gestureType: " + gestureType + ", pos: " + pos);
  
    int handId = kinect.startTrackingHand(pos);
    println("hand stracked: " + handId);
  }
  public void drawHandSize(float mid_x, float mid_y){
      int semiWidth=100/2,semiHeight=200/2;
      if(move){
        stroke(255,255,0);
      }else{
        stroke(0,0,255);
      }
      strokeWeight(10);
      line(mid_x - semiWidth, mid_y + semiHeight, mid_x - semiWidth, mid_y - semiHeight);
      line(mid_x - semiWidth, mid_y - semiHeight, mid_x + semiWidth, mid_y - semiHeight);
      line(mid_x + semiWidth, mid_y - semiHeight, mid_x + semiWidth, mid_y + semiHeight);
      line(mid_x + semiWidth, mid_y + semiHeight, mid_x - semiWidth, mid_y + semiHeight);
  }
  public boolean judgeMove(float prev, float current){
    println("prev: "+prev+" current: "+current);
    if(prev - current > 5){
      rate = 100 + PApplet.parseInt((current - prev))%254;
      return true;
    }
    else{
      rate = 50;
    }
    return false;
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
		serial.stop();
		println("Done");
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
