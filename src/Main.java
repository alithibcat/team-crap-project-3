import java.util.*;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) {

        //thread creation and population of RQ by Collin
        int T = (int) (Math.random() * (25 - 1) + 1); // Number Task Threads
        int C = 3; // Number Cores
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

        Dispatcher.RQ = RQ;
        Dispatcher.dispSem = dispSem;
        Task.taskStart = taskStart;
        Task.taskFinished = taskFinished;
        Task.remainingTasksSem = remainingTasksSem;
        Task.remainingTasks = T;
        Task.currentDispID = currentDispID;

        //Side note, perhaps to use ArrayList to test later if it's easier
        ArrayList<Task> readyQueue = new ArrayList<>();

        //Queue is just only good for FCFS, not good for the rest of the algo
        //Queue<Task> readyQueue = new Queue<Task>()

        //Start tasks and add them to ready queue
        for (int i = 0; i < T; i++){
            int B =  (int) (Math.random() * (50 - 1) + 1); // Max Burst Time
            Task task = new Task(i,B);
            Thread thread = new Thread(task);
            // If PSJF, randomize ready queue
            readyQueue.add(task);
            thread.start();
        }
        Dispatcher.readyQueue = readyQueue;

        //Start dispatchers
        for (int i = 0; i < C; i++) {
            Dispatcher disp = new Dispatcher(i);
            Thread thread = new Thread(disp);
            thread.start();
        }
    }
}