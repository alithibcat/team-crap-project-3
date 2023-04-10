import java.util.concurrent.Semaphore;

public class Task implements Runnable{
    public static int remainingTasks;
    public static Semaphore remainingTasksSem;
    public static int currentDispID[];
    public final int taskID;
    public final int maxBurst;
    private int remainingBurst;
    public static Semaphore[] taskStart;
    public static Semaphore[] taskFinished;

    public Task(int taskID, int maxBurst) {
        this.taskID = taskID;
        this.maxBurst = maxBurst;
        this.remainingBurst = maxBurst;
    }

    public int getTaskID() {
        return taskID;
    }

    public int getMaxBurst() {
        return maxBurst;
    }

    public int getRemainingBurst() {
        return remainingBurst;
    }

    @Override
    public void run() {
        while (remainingBurst > 0) { // While task still needs to run
            try {
                taskStart[taskID].acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //System.out.println("Task " + taskID + " runs.");
            remainingBurst--;
            taskFinished[taskID].release();
        }
        Dispatcher.dispatcher[currentDispID[taskID]].release(); // Task has no more bursts to run
        // Update remaining tasks
        try {
            remainingTasksSem.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        remainingTasks--;
        remainingTasksSem.release();
    }
}