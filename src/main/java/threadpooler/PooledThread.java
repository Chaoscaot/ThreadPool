package threadpooler;

import java.util.List;
import java.util.ListIterator;

public class PooledThread<T, K> {

    private ThreadTask<T, K> task;
    private final Thread thread;
    private List<T> data;
    private int delay = 0;
    private final ThreadPool<T, K> threadPool;
    private boolean finished = false, abort = false;
    private Exception stackTrace = null;

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
            ListIterator<T> dataPoint = data.listIterator();
            while(dataPoint.hasNext()) {
                long start = System.currentTimeMillis();
                if(abort)
                    break;
                T currentData = dataPoint.next();
                K currentResult = task.execute(currentData);
                threadPool.addResult(currentData, currentResult);
                if(!dataPoint.hasNext())
                    break;
                long end = System.currentTimeMillis();
                Thread.sleep(Long.max(0, delay - (end - start)));
            }
            finished = true;
        }catch (Exception e) {
            stackTrace = e;
            threadPool.setException(true);
        }
    }

    public void start(List<T> data) {
        if(task == null) throw new NullPointerException("Task is Null");
        this.data = data;
        thread.start();
    }

    public void start() {
        if(task == null) throw new NullPointerException("Task is Null");
        thread.start();
    }

    public void setData(List<T> data) {
        this.data = data;
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
}
