import java.util.ArrayList;
import java.util.Map;
import processing.serial.Serial;
import SimpleOpenNI.*;
import processing.core.PApplet;
import processing.core.PVector;
import fingertracker.*;

SimpleOpenNI kinect;
HashMap<Integer, HandObject> hands; //HandObject is a class in HandObject.pde
boolean move = false;
Serial serial;
PumpController pumpController; //PumpController is a class in PumpController.pde

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
  if(hands!=null){
    for(HandObject handObject : hands.values()){
      handObject.drawHandSize(handObject.detectMoveIn());
      if(handObject.detectMoveIn()){
        pumpController.orderShot();
      }
    }
  }
}
  /*void drawSkeleton(int userId)
  {
    kinect.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND);
  }*/
  //hand events
void keyPressed(){
  if(key == '0'){
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
  }
}
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
