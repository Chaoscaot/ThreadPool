package threadpooler;

public interface DataProcessor<T, K> {

    void execute(T input, K result);
}
