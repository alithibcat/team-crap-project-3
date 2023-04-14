import java.util.ArrayList;
import java.util.concurrent.Semaphore;
public class Dispatcher implements Runnable {
    static ArrayList<Task> readyQueue;
    static Semaphore RQ;
    static Semaphore[] dispSem;
    static int barrierThreadCount;
    static Semaphore barrierMutex;
    static Semaphore barrierSemHold;
    static int barrierThreadCount2;
    static Semaphore barrierMutex2;
    static Semaphore barrierSemHold2;
    private final int dispID;

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
        System.out.println("\nDispatcher " + dispID + " | Running process " + taskID
                + "\nProcess " + taskID + "   | On CPU: MB=" + taskMB
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

        System.out.println("Dispatcher " + dispatcherID + " | Running process " + taskID);
        System.out.println("Process " + taskID + "   | On CPU: MB=" + taskMB
                + ", CB=0, BT=" + taskMB + ", BG=" + taskMB);
        for(int i = 0; i < quantumTime; i++){
            if (t.getRemainingBurst() >  0){
                //Starting the task, releasing each one
                Task.taskStart[taskID].release();

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

    private static void PSJF(ArrayList<Task> readyQueue, int dispID) throws InterruptedException {
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

        //find the task with the shortest burst time
        int taskID;
        int taskMB;
        Task shortestTask = readyQueue.get(0);
        for (int i = 0; i < readyQueue.size(); i++){
            if (readyQueue.get(i).getRemainingBurst() < shortestTask.getRemainingBurst()){
                shortestTask = readyQueue.get(i);
            }
        }
        taskID = shortestTask.getTaskID();
        taskMB = shortestTask.getMaxBurst();
        readyQueue.remove(shortestTask);
        RQ.release();

        //task start
        System.out.println("\nDispatcher " + dispID + " | Running process " + taskID
                + "\nProcess " + taskID + "   | On CPU: MB=" + taskMB
                + ", CB=0, BT=" + taskMB + ", BG=" + taskMB);

        for(int i = 0; i < shortestTask.getRemainingBurst(); i++){
            shortestTask.taskStart[taskID].release();
            shortestTask.taskFinished[taskID].acquire();
        }
    }

    public void barrierStart() throws InterruptedException {
        barrierMutex2.acquire();
        barrierThreadCount2++;
        if(barrierThreadCount2 == C){
            // the last thread wakes up all previous threads
            System.out.println("Dispatcher " + dispID + " | Now releasing dispatchers.\n");
            for(int i = 0; i < C; i++)
                barrierSemHold2.release();
            barrierMutex2.release();
        } else{
            barrierMutex2.release();
            barrierSemHold2.acquire();
        }
    }
    public void barrierEnd() throws InterruptedException{
        barrierMutex.acquire();
        barrierThreadCount++;
        if(barrierThreadCount == C){
            // the last thread wakes up all previous threads
            for(int i = 0; i < C; i++)
                barrierSemHold.release();
            System.out.println("\nAll Dispatchers are DONE!");
            barrierMutex.release();
        } else{
            barrierMutex.release();
            barrierSemHold.acquire();
        }
    }

    @Override
    public void run() {
        try { // Release dispatchers once all have been forked
            barrierStart();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Dispatcher " + dispID + " | Running FCFS algorithm");
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

        try { // Print when all dispatchers have finished
            barrierEnd();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}