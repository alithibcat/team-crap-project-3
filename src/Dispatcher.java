import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Semaphore;
public class Dispatcher implements Runnable {
    static ArrayList<Task> readyQueue;
    static Semaphore RQ;
    static Semaphore[] dispatcher;

    public Dispatcher(int dispID) {
    }

    private static void FCFS(ArrayList<Task> readyQueue, int dispID) throws InterruptedException {
        // 1) Access ready queue to get next Task to run
        // 2) Decide the allotted burst for this Task
        int taskID = readyQueue.get(0).getTaskID(); //grab the task ID
        int taskMB = readyQueue.get(0).getMaxBurst(); // grab the task Burst time

        // Update burst info ...

        // Task start
        Task.taskStart[taskID].release();
        // Task finish
        try {
            Task.taskFinished[taskID].acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
            try { // Acquire Ready Queue
                RQ.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try { // Check remaining Tasks
                Task.remainingTasksSem.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (Task.remainingTasks == 0) { // If no more processes to run
                Task.remainingTasksSem.release();
                break;
            }


            // call algorithm to decide what task to run

            RQ.release();
        }
    }
}
