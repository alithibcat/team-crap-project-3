import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) {

        //thread creation and population of RQ by Collin
        int T = (int) (Math.random() * (25 - 1) + 1); // Number Task Threads
        Queue<Thread> readyQueue = new LinkedList<>();
        for(int i = 0; i < T; i++){
            int B =  (int) (Math.random() * (50 - 1) + 1); // Max Burst Time
            Task task = new Task(i,B);
            Thread thread = new Thread(task);
            readyQueue.add(thread);
        }
        int C = 1; // Number Cores

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

        Dispatcher.dispatcher = dispatcher;
        cpuCore.CPU = CPU;
        Task.taskStart = taskStart;
        Task.taskFinished = taskFinished;
    }
}