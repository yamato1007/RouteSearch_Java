package yamato.pathplan.astar;

import yamato.pathplan.AbstractHeuristicPathPlanner;
import yamato.pathplan.FunctionEvaluation;

import java.util.*;

/**
 * Created by k13082kk on 2016/01/12.
 */
public class AStar<T> extends AbstractHeuristicPathPlanner<T>{
    /**
     * 隣接した二点間の移動に必要なコストを計算する関数
     */
    private FunctionEvaluation<T> calcCost;

    /**
     * @param calcCost 二点間の移動コストを返す関数
     * @param heuristic 二点間のヒューリスティック関数
     * @param graph あるノードと、それに隣接するノードのMap
     */
    public AStar (FunctionEvaluation<T> calcCost,FunctionEvaluation<T> heuristic, Map<T,Set<T>> graph){
        super(heuristic,graph);
        this.calcCost = calcCost;
    }

    public FunctionEvaluation<T> getCalcCost(){
        return this.calcCost;
    }

    @Override
    public List<T> calc(T from, Collection<T> targets) {
        //ノードと、そこへ行くための前のノードのMap
        Map<T,List<T>> paths = new HashMap<>();
        //展開するリスト。なお、評価値による昇順ソート済みとする
        List<T> l1 = new LinkedList<>();
        //展開済みリスト
        List<T> l2 = new LinkedList<>();

        //展開するリストの先頭にスタート地点を追加
        l1.add(from);
        //スタート地点からスタート地点へ行くパスはスタート時点そのもの
        List startPath = new LinkedList<>();
        startPath.add(from);
        paths.put(from,startPath);

        //ゴールが見つかるまでループ
        while(!l1.isEmpty()){
            //L1の先頭から評価値最小のノードを取り出す
            T next = l1.remove(0);
            //取り出したノードは展開済みとし、L2に入れる
            l2.add(next);

            //ゴールならパスを返して終了
            if(targets.contains(next)){
                List<T> path = paths.get(next);
                //パスからスタート地点を削除
                //パスは、ゴール地点からスタート地点に隣接するノードまでのリストである
                path.remove(path.size()-1);
                return path;
            }

            //隣接するノードを獲得
            Set<T> neighbors = super.getGraph().get(next);

            for(T neighbor : neighbors){
                boolean addL1 = false;
                //リストに含まれていればスコアの比較をし、今回のスコアのほうが良ければリストから削除
                if(l1.contains(neighbor) || l2.contains(neighbor)){
                    int thisScore = calcEval(paths.get(next), neighbor, targets);
                    int lastScore = calcEval(paths.get(neighbor), targets);
                    if(thisScore < lastScore){
                        //今回のスコアのほうが良ければ、リストから削除
                        if(l1.contains(neighbor)) l1.remove(neighbor);
                        if(l2.contains(neighbor)) l2.remove(neighbor);
                        addL1 = true;
                    }
                }
                //どちらにも含まれてなければ
                else{
                    addL1 = true;
                }
                //展開したノードをL1に追加、Pathをセット
                if(addL1){
                    l1.add(neighbor);
                    List<T> path = new LinkedList<>(paths.get(next));
                    path.add(0,neighbor);
                    paths.put(neighbor,path);
                }
            }

            //l1を昇順ソート
            Collections.sort(l1,
                    (node1,node2) -> {
                        int eval1 = calcEval(paths.get(node1),targets);
                        int eval2 = calcEval(paths.get(node2),targets);
                        return eval1 - eval2;
                    }
            );
        }
        return null;
    }

    //パスが現地点を含まない時につかう評価関数
    private int calcEval(List<T> path, T from, Collection<T> targets){
        int eval;
        //スタート地点からfromの一個前までのパスの距離
        eval = calcPathEval(path);
        //パスの最終地点からfromまでの距離
        eval += this.calcCost.calc(path.get(0), from);
        //fromからtargetsまでのヒューリスティック値
        eval += this.calcHeuristic(from,targets);
        return eval;
    }

    //パスが現地点を含む時につかう評価関数
    private int calcEval(List<T> path, Collection<T> targets){
        int eval;
        //スタート地点からfromの一個前までのパスの距離
        eval = calcPathEval(path);
        //パスの最終地点からtargetsまでのヒューリスティック値
        eval += this.calcHeuristic(path.get(0),targets);
        return eval;
    }

    //パスの距離
    private int calcPathEval(List<T> path){
        int distance = 0;
        //Pathの構成要素の各ノード間の距離を計算して合算
        for(int i=0; i<path.size()-1; i++){
            distance += this.calcCost.calc(path.get(i), path.get(i+1));
        }
        return distance;
    }

    //目的地が複数ある中で最小のヒューリスティック値を返す
    private Integer calcHeuristic(T from, Collection<T> targets){
        Integer bestEval = null;
        for(T target : targets){
            int eval = super.getHeuristic().calc(from,target);
            if(bestEval == null || bestEval > eval){
                bestEval = eval;
            }
        }
        return bestEval;
    }
}
