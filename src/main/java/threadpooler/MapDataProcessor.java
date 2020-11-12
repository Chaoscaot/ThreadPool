package threadpooler;

import java.util.Map;

public interface MapDataProcessor<T, K> {

    void execute(Map<T, K> input);
}
