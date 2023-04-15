import java.util.*;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) {

        //thread creation and population of RQ by Collin
        int T = 5;//(int) (Math.random() * (25 - 1) + 1); // Number Task Threads
        int C = 1; // Number Cores
        int quantumTime = 3; // Quantum Time
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

        //Start tasks and add them to ready queue
        for (int i = 0; i < T; i++) {
            int B = (int) (Math.random() * (50 - 1) + 1); // Max Burst Time
            int A = (int) (Math.random() * (50 - 1) + 1); // RQ Arrival Time
            TaskRQAdder taskRQAdder = new TaskRQAdder(i,B,A);
            Thread thread = new Thread(taskRQAdder);
            thread.start();
        }
        Dispatcher.readyQueue = readyQueue;

        //Start dispatchers
        for (int i = 0; i < C; i++) {
            System.out.println("Main thread  | Forking dispatcher " + i + "\nDispatcher " + i + " | Using CPU " + i);
            Dispatcher disp = new Dispatcher(i);
            Thread thread = new Thread(disp);
            thread.start();
        }
    }
}