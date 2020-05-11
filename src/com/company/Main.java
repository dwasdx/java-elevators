package com.company;
import java.util.Scanner;
import java.util.Random;

public class Main {

    private static void test() {
        int floorsCount = Controller.floorsQuantity;
       Random gen = new Random();
        int a, b;
        for (int i = 0; i < 15; i++) {
            a=gen.nextInt(floorsCount);
            b=gen.nextInt(floorsCount);
            System.out.println("From "+a+" to "+b);
            ElevatorCall call = new ElevatorCall(a, b);
            call.submitCall();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Controller controller = Controller.shared;
        Thread thread = new Thread(controller);
        thread.start();

        int choice;

        Scanner input = new Scanner(System.in);
        test();
        int a = 1;
//        while (true) {
//            System.out.println("Enter one of two options:\n\t1. Call for elevator\n\t2. Check status of all elevators\n\t3. Stop working");
//            choice = input.nextInt();
//
//            if (choice == 1) {
//                int fromFloor, toFloor;
//                System.out.println("Enter the starting point of request from 1 to " + Controller.floorsQuantity);
//                fromFloor = input.nextInt();
//                System.out.println("Enter the ending point of request from 1 to " + Controller.floorsQuantity);
//                toFloor = input.nextInt();
//                ElevatorCall call = new ElevatorCall(fromFloor, toFloor);
//                call.submitCall();
//            } else if (choice == 2) {
//                Controller.printElevatorsDescription();
//            } else if (choice == 3) {
//                thread.interrupt();
//                break;
//            }
//
//        }
    }
}
