import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Semaphore;
public class Dispatcher implements Runnable {
    static ArrayList<Task> readyQueue;
    static Semaphore RQ;
    static Semaphore[] dispatcher;
    int dispID;

    public Dispatcher(int dispID) {
        this.dispID = dispID;
    }

    private static void FCFS(ArrayList<Task> readyQueue, int dispID) throws InterruptedException {
        // Get first task on ready queue, remove task from ready queue, start and finish task
        int taskID = readyQueue.get(0).getTaskID(); // grab the task ID
        int taskMB = readyQueue.get(0).getMaxBurst(); // grab the task Burst time
        readyQueue.remove(0);
        RQ.release();
        // Update burst info ...


        // Task start
        System.out.println("Dispatcher " + dispID + "    | Running process " + taskID);
        System.out.println("Process " + taskID + " On CPU: MB=" + taskMB);
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
            try { // Start this dispatcher
                dispatcher[0].acquire();
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
            } else Task.remainingTasksSem.release();

            try { // Acquire Ready Queue
                RQ.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try {
                FCFS(readyQueue, dispID);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // call algorithm to decide what task to run

            //RQ.release();
        }
    }
}
