package threadpooler;

public interface ThreadTask<T, K> {

    K execute(T input);
}
