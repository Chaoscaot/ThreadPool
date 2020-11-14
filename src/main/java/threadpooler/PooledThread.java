package threadpooler;

public class PooledThread<T, K> {

    private ThreadTask<T, K> task;
    private final Thread thread;
    private int delay = 0;
    private final ThreadPool<T, K> threadPool;
    private boolean finished = false, abort = false;
    private Exception stackTrace = null;
    private boolean waitForStop = false, run = true;

    public PooledThread(ThreadPool<T, K> threadPool, ThreadTask<T, K> task) {
        this.threadPool = threadPool;
        this.task = task;
        thread = new Thread(this::run, "PooledThread-" + Thread.activeCount());
    }

    public PooledThread(ThreadPool<T, K> threadPool) {
        this.threadPool = threadPool;
        thread = new Thread(this::run, "PooledThread-" + Thread.activeCount());
    }

    private void run() {
        try {
            T currentData;
            while(run) {
                if((currentData = threadPool.getData()) == null)
                    if(waitForStop)
                        continue;
                    else
                        break;
                if(abort)
                    break;
                K currentResult = task.execute(currentData);
                threadPool.addResult(currentData, currentResult);
            }
            finished = true;
        }catch (Exception e) {
            stackTrace = e;
            threadPool.setException(true);
        }
    }

    public void start() {
        if(task == null)
            throw new NullPointerException("Task is Null");
        thread.start();
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setTask(ThreadTask<T, K> task) {
        this.task = task;
    }

    public boolean isFinished() {
        return finished;
    }

    public Exception getStackTrace() {
        return stackTrace;
    }

    public void abort() {
        abort = true;
    }

    public boolean isWaitForStop() {
        return waitForStop;
    }

    public void setWaitForStop(boolean waitForStop) {
        this.waitForStop = waitForStop;
    }

    protected void callStop() {
        run = false;
    }
}
