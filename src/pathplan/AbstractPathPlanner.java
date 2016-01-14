package pathplan;

import java.util.*;

/**
 * Created by k13082kk on 2016/01/12.
 */
abstract public class AbstractPathPlanner<T> {
    private Map<T,Set<T>> graph;

    public AbstractPathPlanner(Map<T,Set<T>> graph){
        this.graph = graph;
    }

    abstract public List<T> calc(T from, Collection<T> targets);

    public Map<T,Set<T>> getGraph(){
        return this.graph;
    }
}
