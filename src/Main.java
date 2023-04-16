import java.util.*;
import java.util.concurrent.Semaphore;

public class Main {
    private static String quantumString;
    public static void preemptiveRQ(Semaphore RQ, int T) throws InterruptedException {
        RQ.acquire();
        //Start tasks and add them to ready queue
        for (int i = 0; i < T; i++) {
            int B = (int) (Math.random() * (50 - 1) + 1); // Max Burst Time
            int A = (int) (Math.random() * (50 - 1) + 1); // RQ Arrival Time
            TaskRQAdder taskRQAdder = new TaskRQAdder(i,B,A);
            Thread thread = new Thread(taskRQAdder);
            thread.start();
        }
        RQ.release();
    }

    public static void nonPreemptiveRQ(ArrayList<Task> readyQueue, int T) {
        //Start tasks and add them to ready queue
        for (int i = 0; i < T; i++) {
            int B = (int) (Math.random() * (50 - 1) + 1); // Max Burst Time
            Task task = new Task(i,B);
            readyQueue.add(task);
            Thread thread = new Thread(task);
            thread.start();
        }
        System.out.println("\n--------------- Ready Queue ---------------");
        for (int i = 0; i < readyQueue.size(); i++)
            System.out.println("ID:" + readyQueue.get(i).getTaskID() + ", Max Burst:" + readyQueue.get(i).getMaxBurst() + ", Current Burst:"
                    + (readyQueue.get(i).getMaxBurst() - readyQueue.get(i).getRemainingBurst()));
        System.out.println("-------------------------------------------\n");
    }
    public static void main(String[] args) throws InterruptedException {

        if (args.length == 5){
            quantumString = args[2];
            //round robin executes
            getParameters(args[0] + " " + args[1] + " " + args[2] + " " + args[3] + " " + args[4]);

        }
        else if (args.length == 2){
            //used for inputs where only -S and the selected algorithm are chosen, no core count provided
            getParameters(args[0] + " " + args[1]);
        }
        else if (args.length == 3){
            quantumString = args[2];
            getParameters(args[0] + " " + args[1] + " " + args[2]);
        }
        else {
            //used for the rest of the algorithms
            getParameters(args[0] + " " + args[1] + " " + args[2] + " " + args[3]);
        }

    }


    public static void setup(int cores, int algorithm, String quantumString) throws InterruptedException {
        //thread creation and population of RQ by Collin
        int T = (int) (Math.random() * (25 - 1) + 1); // Number Task Threads
        int C = cores; // Number Cores
        int quantumTime = Integer.parseInt(quantumString); // Quantum Time
        System.out.println("Task Threads: " + T + "\nCores: " + C);

        int[] currentDispID = new int[T];
        Semaphore RQ = new Semaphore(1);
        Semaphore[] dispSem = new Semaphore[C];
        Semaphore[] taskStart = new Semaphore[T];
        Semaphore[] taskFinished = new Semaphore[T];
        Semaphore remainingTasksSem = new Semaphore(1);
        for (int i = 0; i < T; i++) {
            taskStart[i] = new Semaphore(0);
            taskFinished[i] = new Semaphore(0);
        }
        for (int i = 0; i < C; i++)
            dispSem[i] = new Semaphore(1);
        Semaphore barrierMutex = new Semaphore(1);
        Semaphore barrierSemHold = new Semaphore(0);
        int barrierThreadCount = 0;
        Semaphore barrierMutex2 = new Semaphore(1);
        Semaphore barrierSemHold2 = new Semaphore(0);
        int barrierThreadCount2 = 0;

        Dispatcher.RQ = RQ;
        Dispatcher.dispSem = dispSem;
        Dispatcher.C = C;
        Dispatcher.barrierMutex = barrierMutex;
        Dispatcher.barrierSemHold = barrierSemHold;
        Dispatcher.barrierThreadCount = barrierThreadCount;
        Dispatcher.barrierMutex2 = barrierMutex2;
        Dispatcher.barrierSemHold2 = barrierSemHold2;
        Dispatcher.barrierThreadCount2 = barrierThreadCount2;
        Dispatcher.quantumTime = quantumTime;
        Task.taskStart = taskStart;
        Task.taskFinished = taskFinished;
        Task.remainingTasksSem = remainingTasksSem;
        Task.remainingTasks = T;
        Task.currentDispID = currentDispID;

        ArrayList<Task> readyQueue = new ArrayList<>();

        if(algorithm == 4)
            preemptiveRQ(RQ,T);
        else
            nonPreemptiveRQ(readyQueue,T);

        Dispatcher.readyQueue = readyQueue;

        //Start dispatchers
        for (int i = 0; i < C; i++) {
            System.out.println("Main thread  | Forking dispatcher " + i + "\nDispatcher " + i + " | Using CPU " + i);
            Dispatcher disp = new Dispatcher(i, algorithm);
            Thread thread = new Thread(disp);
            thread.start();
        }
    }



    public static void getParameters(String parameter) throws InterruptedException {
        switch (parameter){
            case "-S 1 -C 1":
            case "-C 1 -S 1":
            case "-S 1":
                //setup
                System.out.println("FCFS Algorithm Starting");
                setup(1, 1, "2");
                break;
            case "-S 1 -C 2":
            case "-C 2 -S 1":
                //setup
                System.out.println("FCFS Algorithm Starting");
                setup(2, 1, "2");
                break;
            case "-S 1 -C 3":
            case "-C 3 -S 1":
                System.out.println("FCFS Algorithm Starting");
                setup(3, 1, "2");
                break;
            case "-S 1 -C 4":
            case "-C 4 -S 1":
                //setup
                System.out.println("FCFS Algorithm Starting");
                setup(4, 1, "2");
                break;
            case "-S 2 2 -C 1":
            case "-S 2 3 -C 1":
            case "-S 2 4 -C 1":
            case "-S 2 5 -C 1":
            case "-S 2 6 -C 1":
            case "-S 2 7 -C 1":
            case "-S 2 8 -C 1":
            case "-S 2 9 -C 1":
            case "-S 2 10 -C 1":
            case "-C 1 2 -S 2":
            case "-C 1 3 -S 2":
            case "-C 1 4 -S 2":
            case "-C 1 5 -S 2":
            case "-C 1 6 -S 2":
            case "-C 1 7 -S 2":
            case "-C 1 8 -S 2":
            case "-C 1 9 -S 2":
            case "-C 1 10 -S 2":
            case "-S 2 2":
            case "-S 2 3":
            case "-S 2 4":
            case "-S 2 5":
            case "-S 2 6":
            case "-S 2 7":
            case "-S 2 8":
            case "-S 2 9":
            case "-S 2 10":
                //setup
                System.out.println("RR Algorithm Starting");
                setup(1, 2, quantumString);
                break;
            case "-S 2":
                System.out.println("RR Algorithm Starting");
                System.out.println("Quantum Time is default to 10, if no user input is specified");
                setup(1, 2, "10");
                break;
            case "-S 2 2 -C 2":
            case "-S 2 3 -C 2":
            case "-S 2 4 -C 2":
            case "-S 2 5 -C 2":
            case "-S 2 6 -C 2":
            case "-S 2 7 -C 2":
            case "-S 2 8 -C 2":
            case "-S 2 9 -C 2":
            case "-S 2 10 -C 2":
            case "-C 2 2 -S 2":
            case "-C 2 3 -S 2":
            case "-C 2 4 -S 2":
            case "-C 2 5 -S 2":
            case "-C 2 6 -S 2":
            case "-C 2 7 -S 2":
            case "-C 2 8 -S 2":
            case "-C 2 9 -S 2":
            case "-C 2 10 -S 2":
                //setup
                System.out.println("RR Algorithm Starting");
                setup(2, 2, quantumString);
                break;
            case "-S 2 2 -C 3":
            case "-S 2 3 -C 3":
            case "-S 2 4 -C 3":
            case "-S 2 5 -C 3":
            case "-S 2 6 -C 3":
            case "-S 2 7 -C 3":
            case "-S 2 8 -C 3":
            case "-S 2 9 -C 3":
            case "-S 2 10 -C 3":
            case "-C 3 2 -S 2":
            case "-C 3 3 -S 2":
            case "-C 3 4 -S 2":
            case "-C 3 5 -S 2":
            case "-C 3 6 -S 2":
            case "-C 3 7 -S 2":
            case "-C 3 8 -S 2":
            case "-C 3 9 -S 2":
            case "-C 3 10 -S 2":
                //setup
                System.out.println("RR Algorithm Starting");
                setup(3, 2, quantumString);
                break;
            case "-S 2 2 -C 4":
            case "-S 2 3 -C 4":
            case "-S 2 4 -C 4":
            case "-S 2 5 -C 4":
            case "-S 2 6 -C 4":
            case "-S 2 7 -C 4":
            case "-S 2 8 -C 4":
            case "-S 2 9 -C 4":
            case "-S 2 10 -C 4":
            case "-C 4 2 -S 2":
            case "-C 4 3 -S 2":
            case "-C 4 4 -S 2":
            case "-C 4 5 -S 2":
            case "-C 4 6 -S 2":
            case "-C 4 7 -S 2":
            case "-C 4 8 -S 2":
            case "-C 4 9 -S 2":
            case "-C 4 10 -S 2":
                //setup
                System.out.println("RR Algorithm Starting");
                setup(4, 2, quantumString);
                break;
            case "-S 3 -C 1":
            case "-C 1 -S 3":
            case "-S 3":
                //setup
                System.out.println("Non-preemptive Shortest Job First Algorithm Starting");
                setup(1, 3, "2");
                break;
            case "-S 3 -C 2":
            case "-C 2 -S 3":
                //setup
                System.out.println("Non-preemptive Shortest Job First Algorithm Starting");
                setup(2, 3, "2");
                break;
            case "-S 3 -C 3":
            case "-C 3 -S 3":
                //setup
                System.out.println("Non-preemptive Shortest Job First Algorithm Starting");
                setup(3, 3, "2");
                break;
            case "-S 3 -C 4":
            case "-C 4 -S 3":
                System.out.println("Non-preemptive Shortest Job First Algorithm Starting");
                setup(4, 3, "2");
                break;
            case "-S 4 -C 1":
            case "-S 4 -C 2":
            case "-S 4 -C 3":
            case "-S 4 -C 4":
            case "-C 1 -S 4":
            case "-C 2 -S 4":
            case "-C 3 -S 4":
            case "-C 4 -S 4":
            case "-S 4":
                //setup
                System.out.println("Preemptive Shortest Job First Algorithm Starting");
                setup(1, 4, "2");
                break;
            default:
                System.out.println("\nYou did not enter an accepted parameter. Please enter your inputs in an accepted format.");


        }
    }

}