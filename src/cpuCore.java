import java.util.Queue;
import java.util.concurrent.Semaphore;

public class cpuCore implements Runnable{
    static Semaphore[] CPU;

    public cpuCore() {

    }

    @Override
    public void run() {
        while(!Dispatcher.readyQueue.isEmpty()) {

        }
    }
}
