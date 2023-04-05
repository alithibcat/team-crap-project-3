import java.util.concurrent.Semaphore;

public class Task implements Runnable{
    int taskID;
    int maxBurst;
    int remainingBurst = 0;
    static Semaphore[] taskStart;
    static Semaphore[] taskFinished;

    public Task(int taskID, int maxBurst) {
        this.taskID = taskID;
        this.maxBurst = maxBurst;
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
