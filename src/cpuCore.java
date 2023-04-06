import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class cpuCore implements Runnable{
    static ArrayList<Task> readyQueue;
    private final int CPUID; // Same as dispatcher ID
    public static Semaphore[] CPU;

    public cpuCore(int CPUID) {
        this.CPUID = CPUID;
    }

    @Override
    public void run() {
        while(true) {
            try {
                CPU[CPUID].acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // update assigned task's allotted burst
            //Not looping, just one for there rn
            Task.taskStart[readyQueue.get(1).getTaskID()].release();


            try {
                Task.taskFinished[targetTask.getID].acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Dispatcher.dispatcher[CPUID].release();

            // Check if all tasks are finished
            try {
                Dispatcher.RQ.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (Dispatcher.readyQueue.isEmpty()) break;
            Dispatcher.RQ.release();
        }
        Dispatcher.RQ.release();
    }
}
