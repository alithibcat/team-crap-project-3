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
    private final int algorithm;
    static int C;
    static int quantumTime;
    static int burstGoal = 0;


    public Dispatcher(int dispID, int algorithm) {
        this.dispID = dispID;
        this.algorithm = algorithm;
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

        System.out.println("\n--------------- Ready Queue ---------------");
        for (int i = 0; i < readyQueue.size(); i++)
            System.out.println("ID:" + readyQueue.get(i).getTaskID() + ", Max Burst:" + readyQueue.get(i).getMaxBurst() + ", Current Burst:"
                    + (readyQueue.get(i).getMaxBurst() - readyQueue.get(i).getRemainingBurst()));
        System.out.println("-------------------------------------------\n");

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
            System.out.println("Process " + taskID + "   | Using CPU " + dispID + "; On burst " + (i + 1));
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

        System.out.println( "Dispatcher " + dispatcherID + " \t" +
                "| Running RR algorithm, Time Quantum = " + quantumTime);
        System.out.println( "Process " + taskID + "   " +
                "| On CPU: MB=" + taskMB +
                ", CB=" + (taskMB - t.getRemainingBurst()) +
                ", BT=" + t.getBurstTime() + ", " +
                ", BG=" + (taskMB - t.getRemainingBurst() + quantumTime));
        for(int i = 0; i < quantumTime; i++){
            if (t.getRemainingBurst() >  0){
                System.out.println("Process " + taskID + "   | Using CPU " + dispatcherID + "; On burst " + (i+1));
                //Starting the task, releasing each one
                Task.taskStart[taskID].release();

                //Task Finish
                Task.taskFinished[taskID].acquire();
            }
        }

        //Add Task back to the Ready Queue if it isn't finish
        //Add Task back to the Ready Queue if it isn't finish
        if(t.getRemainingBurst() > 0){
            RQ.acquire();
            readyQueue.add(t);
            RQ.release();
            //Let dispatcher work on another process
            dispSem[dispatcherID].release();
            return;
        }

        //Update remaining tasks
        Task.remainingTasksSem.acquire();

        Task.remainingTasks--;
        Task.remainingTasksSem.release();
        //Let dispatcher work on another process
        dispSem[dispatcherID].release();
    }

    private static void NSJF(ArrayList<Task> readyQueue, int dispID) throws InterruptedException {
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

        System.out.println("\n--------------- Ready Queue ---------------");
        for (int i = 0; i < readyQueue.size(); i++)
            System.out.println("ID:" + readyQueue.get(i).getTaskID() + ", Max Burst:" + readyQueue.get(i).getMaxBurst() + ", Current Burst:"
                    + (readyQueue.get(i).getMaxBurst() - readyQueue.get(i).getRemainingBurst()));
        System.out.println("-------------------------------------------\n");

        //find the task with the shortest burst time
        Task shortestTask = readyQueue.get(0);
        for (int i = 0; i < readyQueue.size(); i++){
            if (readyQueue.get(i).getRemainingBurst() < shortestTask.getRemainingBurst()){
                shortestTask = readyQueue.get(i);
            }
        }
        int taskID = shortestTask.getTaskID();
        int taskMB = shortestTask.getMaxBurst();
        readyQueue.remove(shortestTask);


        System.out.println("\nDispatcher " + dispID + " | Running process " + taskID
                + "\nProcess " + taskID + "   | On CPU: MB=" + taskMB
                + ", CB=0, BT=" + taskMB + ", BG=" + taskMB);

        while(shortestTask.getRemainingBurst() > 0){
            System.out.println("Process " + taskID + "   | Using CPU " + dispID + "; On burst " + (taskMB - shortestTask.getRemainingBurst() + 1));
            //task start
            shortestTask.taskStart[taskID].release();

            //task finish
            shortestTask.taskFinished[taskID].acquire();

        }
        RQ.release();
        Task.remainingTasksSem.acquire();
        Task.remainingTasks--;
        Task.remainingTasksSem.release();
        dispSem[dispID].release(); // Task finished normally
    }

    private static void PSJF(ArrayList<Task> readyQueue, int dispID) throws InterruptedException {
        if (readyQueue.isEmpty()) {
            dispSem[dispID].release();
            return;
        }
        System.out.println("\n--------------- Ready Queue ---------------");
        for (int i = 0; i < readyQueue.size(); i++)
            System.out.println("ID:" + readyQueue.get(i).getTaskID() + ", Max Burst:" + readyQueue.get(i).getMaxBurst() + ", Current Burst:"
                    + (readyQueue.get(i).getMaxBurst() - readyQueue.get(i).getRemainingBurst()));
        System.out.println("-------------------------------------------\n");

        //find the task with the shortest burst time
        Task shortestTask = readyQueue.get(0);
        for (int i = 0; i < readyQueue.size(); i++){
            if (readyQueue.get(i).getRemainingBurst() < shortestTask.getRemainingBurst()){
                shortestTask = readyQueue.get(i);
            }
        }
        int taskID = shortestTask.getTaskID();
        int taskMB = shortestTask.getMaxBurst();
        readyQueue.remove(shortestTask);

        System.out.println("\nDispatcher " + dispID + " | Running process " + taskID
                + "\nProcess " + taskID + "   | On CPU: MB=" + taskMB
                + ", CB=0, BT=" + taskMB + ", BG=" + taskMB);

        boolean bool = false;
        while(shortestTask.getRemainingBurst() > 0){
            for (int j = 0; j < readyQueue.size(); j++){ // Check if current task is no longer shortest task
                if (readyQueue.get(j).getRemainingBurst() < shortestTask.getRemainingBurst())
                    bool = true;
            }
            if (bool) {
                System.out.println("Preemption!");
                if(shortestTask.getRemainingBurst() != 0) // If task not done when preempted
                    readyQueue.add(shortestTask);
                dispSem[dispID].release();
                return;
            }
            System.out.println("Process " + shortestTask.getTaskID() + "   | Using CPU " + dispID + "; On burst " + (shortestTask.getMaxBurst() - shortestTask.getRemainingBurst() + 1));
            //task start
            shortestTask.taskStart[shortestTask.getTaskID()].release();

            //task finish
            shortestTask.taskFinished[shortestTask.getTaskID()].acquire();
            if (shortestTask.getRemainingBurst() < 1) {
                Task.remainingTasksSem.acquire();
                Task.remainingTasks--;
                Task.remainingTasksSem.release();
                dispSem[dispID].release();
                return;
            }
        }
        dispSem[dispID].release(); // Task finished normally
    }


    public void barrierStart() throws InterruptedException {
        barrierMutex2.acquire();
        barrierThreadCount2++;
        if (barrierThreadCount2 == C) {
            // the last thread wakes up all previous threads
            System.out.println("Dispatcher " + dispID + " | Now releasing dispatchers.\n");
            for (int i = 0; i < C; i++)
                barrierSemHold2.release();
            barrierMutex2.release();
        } else {
            barrierMutex2.release();
            barrierSemHold2.acquire();
        }
    }

    public void barrierEnd() throws InterruptedException {
        barrierMutex.acquire();
        barrierThreadCount++;
        if (barrierThreadCount == C) {
            // the last thread wakes up all previous threads
            for (int i = 0; i < C; i++)
                barrierSemHold.release();
            System.out.println("\nAll Dispatchers are DONE!");
            barrierMutex.release();
        } else {
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
        //System.out.println("Dispatcher " + dispID + " | Running FCFS algorithm");
        //System.out.println("Dispatcher " + dispID + " | Running RR algorithm, Time Quantum = " + quantumTime);
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
            switch (algorithm){
                case 1:
                    FCFS(readyQueue, dispID);
                    break;
                case 2:
                    try {
                        RR(readyQueue, dispID, quantumTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        NSJF(readyQueue, dispID);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    try {
                        PSJF(readyQueue, dispID);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("Something went wrong");
            }
        }

        try { // Print when all dispatchers have finished
            barrierEnd();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}