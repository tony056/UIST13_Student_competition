import processing.core.PVector;
import java.util.ArrayList;
import java.util.Map;


class Gesture extends Thread
{
	
	HashMap<Integer, HandObject> hands;	

	Gesture(){
		hands = new HashMap<Integer, HandObject>();
	} 

	void start(){
		super.start();
	}

	void run(){
		while(true){

		}
	}

	//---------------getter and setter------------------

	HashMap<Integer, HandObject> getHands(){
		return this.hands;
	}

	void setHands( HashMap<Integer, HandObject> hands ){
		this.hands = hands;
	}

	//---------------Manipulate A Hand--------------------

	void addHand( int handId, HandObject hand ){
		hands.put(handId, hand);
	}

	void removeHand( int handId ){
		hands.remove(handId);
	}

	HandObject getHand( int handId ){
		return hands.get(handId);
	}

	//--------------------------------------------
	Style gestureDetect(){
		// one or to hands
		int numOfHand;

		if(hands.size() > 2 || hands.size() == 0 ){
			return Style.NONE; 
		}else if( hands.size() == 2 ){
			numOfHand = 2;
		}else if( hands.size() == 1 ){
			numOfHand = 1;
		}

					

		return Style.NONE; 
	}
	
	//-----------Debug mode---------------

	void outlineHands(){
		for(HandObject handObject : hands.values()){
	      if(handObject.handPath.size()>=5){
	        int prev = handObject.handPath.size()-4;
	        int current = handObject.handPath.size()-1;
	        //give border color
	        handObject.drawHandSize(0);
	      }
	    }
	}

	//----------------Helper--------------------------

	HashMap<Integer, float[]> getDelta( int threshold ){
		HashMap<Integer, float[]> deltas = new HashMap<Integer, float[]>();
		for(HandObject handObject : hands.values()){
			if(handObject.handPath.size() >= threshold ){
				int prev = handObject.handPath.size()-(threshold-1);
				int current = handObject.handPath.size()-1;
			
				deltas.put( handObject.handId, handObject.motionDetect(prev, current) );
			}
		}
		return deltas;
	}

}