import java.util.ArrayList;
import java.util.Map;
import processing.serial.Serial;
import SimpleOpenNI.*;
import processing.core.PApplet;
import processing.core.PVector;


SimpleOpenNI kinect;
Gesture gesture; //Gesture is a class in Gesture.pde
PumpController pumpController; //PumpController is a class in PumpController.pde

void setup() {
    kinect = new SimpleOpenNI(this);

    gesture = new Gesture();

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
    kinect.startGesture(SimpleOpenNI.GESTURE_HAND_RAISE);
    

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
  Style gestureState = Style.NONE;
  
  gesture.outlineHands();
  gestureState = gesture.gestureDetect( gestureState );
  

  if( gestureState == Style.IRONMAN ){
    println("IIIIIIRRRRRRRRRONIIIIIIRRRRRRRRRONIIIIIIRRRRRRRRRON");
  }else if( gestureState == Style.REL_KAMA ){
    println("kamehamehakamehamehakamehamehakamehamehakamehamehak");
  }else if( gestureState == Style.SHOT ){
    println("SHOTSHOTSHOTSHOTSHOTSHOTSHOTSHOTSHOTSHOTSHOTSHOTSHOT");
  }

}
  

//hand events


void onNewHand(SimpleOpenNI curKinect, int handId, PVector pos){
  println("onNewHand -- handId:" + handId + ", pos" + pos);
  kinect.convertRealWorldToProjective(pos,pos);

  if(!gesture.getHands().containsKey(handId)){ //the id is not inserted
    HandObject hand =  new HandObject(handId);
    hand.savePoint(pos);
    gesture.addHand(handId, hand);
  }else {
    println("the id is exist");
  }
}


void onTrackedHand(SimpleOpenNI curKinect, int handId, PVector pos){
  // println("onTrackedHand!!!!"+"x: "+pos.x+" y: "+pos.y);
  kinect.convertRealWorldToProjective(pos,pos);

  if( gesture.getHands().containsKey(handId) ){
    //insert point
    gesture.getHand(handId).savePoint(pos);

  }
}

void onLostHand(SimpleOpenNI curContext,int handId)
{
  println("onLostHand - handId: " + handId);

  HandObject handToRemove = gesture.getHand(handId);
  handToRemove.live = false;
  gesture.removeHand(handId);

}

//callback after start gesture
void onCompletedGesture(SimpleOpenNI curContext,int gestureType, PVector pos)
{
  println("onCompletedGesture - gestureType: " + gestureType + ", pos: " + pos);
  
  int handId = kinect.startTrackingHand(pos);
  println("hand stracked: " + handId);
}

