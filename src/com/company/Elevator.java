package com.company;
import java.util.*;

public class Elevator implements Runnable{

    private boolean isWorking;
    private int id;
    private ElevatorState state;
    private int floor;

    private NavigableSet<Integer> stops;
    public Map<ElevatorState, NavigableSet<Integer>> stopsMap;

    public Elevator(int id){
        this.id = id;
        setWorking(true);
    }

    public int getId() {
        return id;
    }

    public ElevatorState getState() {
        return state;
    }

    public int getFloor() {
        return floor;
    }

    public void setState(ElevatorState state) {
        this.state = state;
    }

    public boolean isWorking(){
        return this.isWorking;
    }

    public void setWorking(boolean state){
        this.isWorking = state;

        if(!state){
            setState(ElevatorState.NOT_WORKING);
            this.stops.clear();
        } else {
            setState(ElevatorState.WAITING);
            this.stopsMap = new LinkedHashMap<>();
            Controller.updateElevatorLists(this);
        }

        setFloor(0);
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public void move(){
            Iterator<ElevatorState> iter = stopsMap.keySet().iterator();

            while(iter.hasNext()){
                try {
                    state = iter.next();
                } catch (ConcurrentModificationException e) {
                    break;
                }

                stops = stopsMap.get(state);
                iter.remove();
                Integer currFlr;
                Integer nextFlr ;

                while (!stops.isEmpty()) {

                    if (state.equals(ElevatorState.UP)) {
                        currFlr = stops.pollFirst();
                        nextFlr = stops.higher(currFlr);

                    } else if (state.equals(ElevatorState.DOWN)) {
                        currFlr = stops.pollLast();
                        nextFlr = stops.lower(currFlr);
                    } else {
                        return;
                    }

                    floor = currFlr;

                    if (nextFlr != null) {
                        createFloorsRow(currFlr, nextFlr);
                    } else {
                        setState(ElevatorState.WAITING);
                        Controller.updateElevatorLists(this);
                    }

                    System.out.println(this);

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            //try {
               // Controller.shared.wait();
          ///  } catch (InterruptedException e) {
           //     e.printStackTrace();
          //  }

    }

    private void createFloorsRow(int initial, int target){
        if(initial == target){
            return;
        }

        if(Math.abs(initial - target) == 1){
            return;
        }

        int step = target - initial < 0 ? -1 : 1;
        while(initial != target){
            initial += step;
            stops.add(initial);
        }
    }

    @Override
    public void run() {
        while(true){
            if(isWorking()){
                move();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
    }

    @Override
    public String toString() {
        return "Elevator ID " + this.id + " | Current floor - " + getFloor() + " | next move - " + getState();
    }
}
