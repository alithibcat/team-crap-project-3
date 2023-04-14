import java.util.ArrayList;
import java.util.concurrent.Semaphore;
public class Dispatcher implements Runnable {
    static ArrayList<Task> readyQueue;
    static Semaphore RQ;
    static Semaphore[] dispSem;
    int dispID;

    static int C;
    static int quantumTime;

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

    private static void RR(ArrayList<Task> readyQueue, int dispatcherID, int quantumTime) throws InterruptedException {
        RQ.acquire();

        if (readyQueue.isEmpty()) {
            RQ.release();
            dispSem[dispatcherID].release();
            return;
        }

        // Get first task on ready queue, remove task from ready queue,
        // run the task of quantum Time

        // grab the task ID
        int taskID = readyQueue.get(0).getTaskID();
        // grab the task Burst time
        int taskMB = readyQueue.get(0).getRemainingBurst();
        Task t = readyQueue.remove(0);
        RQ.release();

        //Task Start, stops when quantum Time is completed
        for(int i = 0; i < quantumTime; i++){
            if (t.getRemainingBurst() >  0){
                //Starting the task, releasing each one
                Task.taskStart[taskID].release();
            }
            else{
                //Task Finish
                Task.taskFinished[taskID].acquire();
            }
        }

        //Add Task back to the Ready Queue if it isn't finish
        readyQueue.add(t);

        //Update remaining tasks
        Task.remainingTasksSem.acquire();

        Task.remainingTasks--;
        Task.remainingTasksSem.release();
        //Let dispatcher work on another process
        dispSem[dispatcherID].release();
    }

    private static void NSJF() {

    }

    private static void PSJF() {

    }

    static Semaphore barrierMutex = new Semaphore(1);
    static Semaphore barrierSemHold = new Semaphore(0);
    static int barrierThreadCount = 0;

    public void barrier() throws InterruptedException{
        barrierMutex.acquire();
        barrierThreadCount++;
        if(barrierThreadCount == C){
            // the last thread wakes up all previous threads
            for(int i = 0; i < C; i++){
                barrierSemHold.release();
            }
            System.out.println("All Dispatchers are DONE!");
            barrierMutex.release();

        }
        else{
            barrierMutex.release();
            barrierSemHold.acquire();
        }
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
            //FCFS(readyQueue, dispID);
            try {
                RR(readyQueue, dispID, quantumTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //System.out.println("Dispatcher " + dispID + " is DONE");
        try {
            barrier();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}