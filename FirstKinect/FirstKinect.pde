import java.util.ArrayList;
import java.util.Map;
import processing.serial.Serial;
import SimpleOpenNI.*;
import processing.core.PApplet;
import processing.core.PVector;
import fingertracker.*;

SimpleOpenNI kinect;
FingerTracker fingertracker;
int fingerThreshold = 625;
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
void setup() {
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

 void draw() {
    kinect.update();
    image(kinect.depthImage(), 0, 0);
    for(HandObject handObject : hands.values()){
      handObject.drawHandSize(handObject.detectMoveIn());
      if(handObject.detectMoveIn()){
        pumpController.orderShot(handObject.handId);
      }
    }
  }
  /*void drawSkeleton(int userId)
  {
    kinect.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND);
  }*/
  //hand events
  void onNewHand(SimpleOpenNI curKinect, int handId, PVector pos){
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
  void onTrackedHand(SimpleOpenNI curKinect, int handId, PVector pos){
    //println("onTrackedHand!!!!"+"x: "+pos.x+" y: "+pos.y);
    kinect.convertRealWorldToProjective(pos,pos);
    //println("x: "+pos.x+" y: "+pos.y);
    if(hands.containsKey(handId)){
      //insert point
      hands.get(handId).savePoint(pos);

    }
  }
  void onLostHand(SimpleOpenNI curContext,int handId)
  {
    println("onLostHand - handId: " + handId);

    //handPathList.remove(handId);
  }
  void onCompletedGesture(SimpleOpenNI curContext,int gestureType, PVector pos)
  {
    println("onCompletedGesture - gestureType: " + gestureType + ", pos: " + pos);
  
    int handId = kinect.startTrackingHand(pos);
    println("hand stracked: " + handId);
  }
  void drawHandSize(float mid_x, float mid_y){
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
  boolean judgeMove(float prev, float current){
    println("prev: "+prev+" current: "+current);
    if(prev - current > 5){
      rate = 100 + int((current - prev))%254;
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
  //println("onVisibleUser - userId: " + userId);
}
