import processing.core.PVector;
import java.util.ArrayList;

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
	void start(){
		super.start();
	}
	void run(){
		while(live){

		}
		println(handId+" is dead");
	}
	void savePoint(PVector point){
		handPath.add(point);
	}
	int motionDetect(int prev, int current){
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
	boolean detectMoveIn(){
		boolean ans = false;
		int distance = 5;
		float current_z = handPath.get(handPath.size()-1).z;
		float prev_z = handPath.get(handPath.size()-2).z;
		if(prev_z - current_z >= distance)
			return true;
		return false;
	}
	void drawHandSize(int colorChange){
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