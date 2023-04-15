public class TaskRQAdder implements Runnable {
    private final int taskID;
    private final int maxBurst;
    private final int arrivalTime;
    public TaskRQAdder(int taskID, int maxBurst, int arrivalTime) {
        this.taskID = taskID;
        this.maxBurst = maxBurst;
        this.arrivalTime = arrivalTime;
    }
    @Override
    public void run() {
        Task task = new Task(taskID, maxBurst);
        Thread myThread = new Thread(task);
        myThread.start();
        for (int i = 0; i < arrivalTime*5000; i++)
            Thread.yield();
        System.out.println("Main thread  | Creating process thread " + taskID + " with max burst " + maxBurst);
        Dispatcher.readyQueue.add(task);
    }
}
