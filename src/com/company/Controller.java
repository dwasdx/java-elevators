package com.company;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

public final class Controller implements Runnable {

    public static int floorsQuantity;
    public static int numberOfElevators;
    private boolean stopController;

    private static Map<Integer, Elevator> elevatorsUp = new HashMap<>();
    private static Map<Integer, Elevator> elevatorsDown = new HashMap<>();
    private static List<Elevator> elevators;

    public static final Controller shared = new Controller(4, 30);

    private Controller(int capacity, int floorsQuantity){
        elevators = new ArrayList<>();

        Controller.numberOfElevators = capacity;
        Controller.floorsQuantity = floorsQuantity;
        stopController = false;
        initializeElevators();
    }

    public Elevator selectElevator(ElevatorCall elevatorRequest) {
        ElevatorState elevatorState = getElevatorDirection(elevatorRequest);
        return findElevator(elevatorState, elevatorRequest);
    }

    private static void initializeElevators(){
        for(int i = 0; i < numberOfElevators; i++){
            Elevator e = new Elevator(i);
            Thread t = new Thread(e);
            t.start();

            elevators.add(e);
        }
    }

    private static ElevatorState getElevatorDirection(ElevatorCall elevatorRequest){
        return elevatorRequest.getToFloor() - elevatorRequest.getFromFloor() > 0 ? ElevatorState.UP : ElevatorState.DOWN;
    }

    private static Elevator findElevatorHelper(ElevatorState state, ElevatorCall call, Map<Integer, Elevator> elevatorMap) {
        int fromFloor = call.getFromFloor();
        TreeMap<Integer, Integer> map = new TreeMap<>();

        for(Map.Entry<Integer, Elevator> elvMap : elevatorMap.entrySet()){
            Elevator elv = elvMap.getValue();

            int diff = fromFloor - elv.getFloor();

            if(!(diff < 0 && elv.getState().equals(state))) {
                map.put(Math.abs(diff), elv.getId());
            }
        }

        int id = map.firstEntry().getValue();
        return elevatorMap.get(id);
    }

    private static void findElevatorDirectionsHelper(Elevator e, int fromFloor, int toFloor, ElevatorState state) {
        NavigableSet<Integer> floorSet = e.stopsMap.get(state);
        if (floorSet == null) {
            floorSet = new ConcurrentSkipListSet<>();
        }
        floorSet.add(fromFloor);
        floorSet.add(toFloor);
        e.stopsMap.put(state, floorSet);
    }

    private static Elevator findElevator(ElevatorState state, ElevatorCall call) {
        int fromFloor = call.getFromFloor();
        int toFloor = call.getToFloor();
        Elevator elevator = null;

        if(state.equals(ElevatorState.UP)){
            elevator = findElevatorHelper(state, call, elevatorsUp);
        } else if(state.equals(ElevatorState.DOWN)){
            elevator = findElevatorHelper(state, call, elevatorsDown);
        }

        ElevatorCall newCall = new ElevatorCall(elevator.getFloor(), fromFloor);
        ElevatorState elevatorDirection = getElevatorDirection(newCall);
        findElevatorDirectionsHelper(elevator, elevator.getFloor(), fromFloor, elevatorDirection);

        ElevatorState elevatorDirection2 = getElevatorDirection(call);
        findElevatorDirectionsHelper(elevator, fromFloor, toFloor, elevatorDirection2);

        return elevator;
    }

    public static synchronized void updateElevatorLists(Elevator elevator){
        if(elevator.getState().equals(ElevatorState.UP)){
            elevatorsUp.put(elevator.getId(), elevator);
            elevatorsDown.remove(elevator.getId());
        } else if(elevator.getState().equals(ElevatorState.DOWN)){
            elevatorsDown.put(elevator.getId(), elevator);
            elevatorsUp.remove(elevator.getId());
        } else if (elevator.getState().equals(ElevatorState.WAITING)){
            elevatorsUp.put(elevator.getId(), elevator);
            elevatorsDown.put(elevator.getId(),elevator);
        } else if (elevator.getState().equals(ElevatorState.NOT_WORKING)){
            elevatorsUp.remove(elevator.getId());
            elevatorsDown.remove(elevator.getId());
        }
    }

    @Override
    public void run() {
        stopController =  false;
        while(true){
            try {
                Thread.sleep(100);
                if(stopController){
                    break;
                }
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public static void printElevatorsDescription() {
        for (Elevator e : elevators) {
            System.out.println(e);
        }
    }

    public static int getElevatorsQuantity() {
        return elevators.size();
    }

    public static Elevator getElevatorByID(int id) {
        return elevators.get(id);
    }

    public void setStopController(boolean stop){
        this.stopController = stop;

    }

    public synchronized List<Elevator> getElevatorList() {
        return elevators;
    }

    public boolean isStopController() {
        return stopController;
    }
}
