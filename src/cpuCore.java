import java.util.Queue;
import java.util.concurrent.Semaphore;

public class cpuCore implements Runnable{
    private final int CPUID;
    static Semaphore[] CPU;

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

            Task.taskStart[taskID].release();
            Task.taskFinished[taskID].acquire();
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
