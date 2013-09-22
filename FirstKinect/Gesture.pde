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
	Style gestureDetect( Style preState ){
		// one or to hands
		int numOfHand = 0;

		if(hands.size() > 2 || hands.size() == 0 ){
			return Style.NONE; 
		}else if( hands.size() == 2 ){
			numOfHand = 2;
		}else if( hands.size() == 1 ){
			numOfHand = 1;
		}

		HashMap<Integer, float[]> handMove = getDeltas(6);
		ArrayList<float[]> delta = new ArrayList<float[]>();

		for( Object key : handMove.keySet() ){
			delta.add(handMove.get(key));
		}

		if( numOfHand == 1 ){
			
			if(!delta.isEmpty()){
				println("1111111111:" + delta.get(0)[0] + ',' + delta.get(0)[1] + ',' + delta.get(0)[2] );	
				
				if( delta.get(0)[1] > 50 && Math.abs(delta.get(0)[0]) < 5 ){
					return Style.IRONMAN;
				}else if( delta.get(0)[2] > 30 && Math.abs(delta.get(0)[0]) < 10 && Math.abs(delta.get(0)[1]) < 10 ){
					return Style.SHOT;
				}
			}
			
		}
		
		if( numOfHand == 2){
			
			if(delta.size() >= 2){
				println("2222222222-111:" + delta.get(0)[0] + ',' + delta.get(0)[1] + ',' + delta.get(0)[2] );	
				println("2222222222-222:" + delta.get(1)[0] + ',' + delta.get(1)[1] + ',' + delta.get(1)[2] );	

				//wrong distance
				double distance = Math.sqrt(Math.pow( delta.get(0)[0] - delta.get(1)[0], 2 ) + Math.pow( delta.get(0)[1] - delta.get(1)[1], 2 ) );
			}

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

	HashMap<Integer, float[]> getDeltas( int threshold ){
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