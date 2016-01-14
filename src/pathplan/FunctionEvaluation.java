package pathplan;

/**
 * Created by k13082kk on 2016/01/12.
 */
@FunctionalInterface
public interface FunctionEvaluation<T> {
    abstract public int calc(T start, T goal);
}
