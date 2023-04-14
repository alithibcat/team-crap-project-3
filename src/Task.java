import java.util.concurrent.Semaphore;

public class Task implements Runnable{
    public static int remainingTasks;
    public static Semaphore remainingTasksSem;
    public static int[] currentDispID;
    public final int taskID;
    public final int maxBurst;
    private int remainingBurst;
    public static Semaphore[] taskStart;
    public static Semaphore[] taskFinished;
    private final int arrivalTime;

    public Task(int taskID, int maxBurst, int arrivalTime) {
        this.taskID = taskID;
        this.maxBurst = maxBurst;
        this.remainingBurst = maxBurst;
        this.arrivalTime = (int) (Math.random() * (25 - 1) + 1);
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
        for (int i = 0; i < arrivalTime; i++)
            Thread.yield();
        //Dispatcher.readyQueue.add();
        while (remainingBurst > 0) { // While task still needs to run, do a single burst
            try {
                taskStart[taskID].acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            remainingBurst--;
            taskFinished[taskID].release();
        }
    }
}