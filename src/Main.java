import java.util.*;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) {

        //thread creation and population of RQ by Collin
        int T = (int) (Math.random() * (25 - 1) + 1); // Number Task Threads
        System.out.println("Task Threads: " + T);
        int C = 1; // Number Cores

        Semaphore RQ = new Semaphore(1);
        Semaphore[] dispatcher = new Semaphore[C];
        Semaphore[] CPU = new Semaphore[C];
        Semaphore[] taskStart = new Semaphore[T];
        Semaphore[] taskFinished = new Semaphore[T];
        for (int i = 0; i < C; i++) {
            dispatcher[i] = new Semaphore(1);
            CPU[i] = new Semaphore(0);
        }
        for (int i = 0; i < T; i++) {
            taskStart[i] = new Semaphore(0);
            taskFinished[i] = new Semaphore(0);
        }

        Dispatcher.RQ = RQ;
        Dispatcher.dispatcher = dispatcher;
        cpuCore.CPU = CPU;
        Task.taskStart = taskStart;
        Task.taskFinished = taskFinished;


        //Side note, perhaps to use ArrayList to test later if it's easier
        ArrayList<Task> readyQueue = new ArrayList<>();

        //Queue is just only good for FCFS, not good for the rest of the algo
        //Queue<Task> readyQueue = new Queue<Task>()

        //Start the task and burst time
        for (int i = 0; i < T; i++){
            int B =  (int) (Math.random() * (50 - 1) + 1); // Max Burst Time
            Task task = new Task(i,B);
            Thread thread = new Thread(task);
            readyQueue.add(task);
            thread.start();
        }
        Dispatcher.readyQueue = readyQueue;
        cpuCore.readyQueue = readyQueue;

        for (int i = 0; i < C; i++) {
            cpuCore cpuCore = new cpuCore(i);
            Thread thread = new Thread(cpuCore);
            thread.start();
        }
    }
}