import processing.core.PVector;
import java.util.ArrayList;
class HandObject{
	int handId = 0;
	int traceLength = 10;
	ArrayList<PVector> handPath;
	HandObject(int Id){
		handId = Id;
		handPath = new ArrayList<PVector>();
	}
	int getId(){
		return handId;
	}
	void savePoint(PVector point){
		handPath.add(point);
	}
	boolean detectMoveIn(){
		boolean ans = false;
		int distance = 5;
		float current_z = handPath.get(handPath.size()-1).z;
		float prev_z = handPath.get(handPath.size()-2).z;
		if(prev_z - current_z >= 5)
			return true;
		return false;
	}
	void drawHandSize(boolean colorChange){
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