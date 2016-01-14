package pathplan;

import java.util.*;
/**
 * Created by k13082kk on 2016/01/12.
 */
abstract public class AbstractHeuristicPathPlanner<T> extends AbstractPathPlanner<T> {
    private FunctionEvaluation<T> heuristic;

    public AbstractHeuristicPathPlanner(FunctionEvaluation<T> heuristic, Map<T, Set<T>> graph) {
        super(graph);
        this.heuristic = heuristic;
    }

    public FunctionEvaluation<T> getHeuristic(){
        return this.heuristic;
    }
}
