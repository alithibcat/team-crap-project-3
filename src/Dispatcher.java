import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Semaphore;
public class Dispatcher implements Runnable {
    static ArrayList<Task> readyQueue;
    static Semaphore RQ;
    static Semaphore[] dispSem;
    int dispID;

    public Dispatcher(int dispID) {
        this.dispID = dispID;
    }

    private static void FCFS(ArrayList<Task> readyQueue, int dispID) {
        try { // Acquire Ready Queue
            RQ.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (readyQueue.isEmpty()) {
            RQ.release();
            dispSem[dispID].release();
            return;
        }
        // Get first task on ready queue, remove task from ready queue, start and finish task
        int taskID = readyQueue.get(0).getTaskID(); // grab the task ID
        int taskMB = readyQueue.get(0).getMaxBurst(); // grab the task Burst time
        readyQueue.remove(0);
        RQ.release();
        // Task start
        System.out.println("Dispatcher " + dispID + " | Running process " + taskID);
        System.out.println("Process " + taskID + "   | On CPU: MB=" + taskMB
                            + ", CB=0, BT=" + taskMB + ", BG=" + taskMB);
        for (int i = 0; i < taskMB; i++) {
            System.out.println("Process " + taskID + "   | Using CPU " + dispID + "; On burst " + (i+1));
            Task.taskStart[taskID].release();
            // Task finish
            try {
                Task.taskFinished[taskID].acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // Update remaining tasks
        try {
            Task.remainingTasksSem.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Task.remainingTasks--;
        Task.remainingTasksSem.release();
        // Let dispatcher work on another process
        dispSem[dispID].release();
    }

    private static void RR() {

    }

    private static void NSJF() {

    }

    private static void PSJF() {

    }

    @Override
    public void run() {
        while(true) {
            try { // Start this dispatcher
                dispSem[dispID].acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try { // Check remaining Tasks
                Task.remainingTasksSem.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (Task.remainingTasks == 0) { // If no more processes to run, stop dispatcher
                Task.remainingTasksSem.release();
                break;
            }
            Task.remainingTasksSem.release();

            // Use one algorithm to choose task to run
            FCFS(readyQueue, dispID);
        }
        System.out.println("Dispatcher " + dispID + " is DONE");
    }
}