package com.company;

public class ElevatorCall {
    private int fromFloor, toFloor;

    public ElevatorCall(int fromFloor, int toFloor) {
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
    }

    public int getFromFloor() {
        return fromFloor;
    }

    public int getToFloor() {
        return toFloor;
    }

    public Elevator submitCall() {
        return Controller.shared.selectElevator(this);
    }
}
