import java.util.concurrent.Semaphore;

public class Task implements Runnable{
    public static int remainingTasks;
    public static Semaphore remainingTasksSem;
    public static int[] currentDispID;
    public final int taskID;
    public final int maxBurst;
    public int burstTime;
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

    public int getBurstTime(){
        return burstTime;
    }

    @Override
    public void run() {
        while (remainingBurst > 0) { // While task still needs to run, do a single burst
            try {
                taskStart[taskID].acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (remainingBurst % Dispatcher.quantumTime == 0)
                burstTime++;
            remainingBurst--;
            taskFinished[taskID].release();
        }
    }
}