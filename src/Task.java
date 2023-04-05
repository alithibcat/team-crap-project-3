import java.util.concurrent.Semaphore;

public class Task implements Runnable{
    public final int taskID;
    public final int maxBurst;
    private int remainingBurst = 0;
    static Semaphore[] taskStart;
    static Semaphore[] taskFinished;

    public Task(int taskID, int maxBurst) {
        this.taskID = taskID;
        this.maxBurst = maxBurst;
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
//        System.out.println("Thread " + threadNum + "  | On CPU: MB="
//                + maxBurst /*+ ", CB=" + currentBurst + ", BT=" + timeQuantum
//                + ", BG= ????"*/);
        while (remainingBurst <= maxBurst) {
            try {
                taskStart[taskID].acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            remainingBurst++;
            taskFinished[taskID].release();
        }
    }
}
