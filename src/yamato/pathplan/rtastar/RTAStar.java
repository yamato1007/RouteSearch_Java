package yamato.pathplan.rtastar;

import yamato.pathplan.AbstractHeuristicPathPlanner;
import yamato.pathplan.FunctionEvaluation;
import yamato.util.Tuple;

import java.util.*;

/**
 * Created by k13082kk on 2016/01/14.
 */
public class RTAStar<T> extends AbstractHeuristicPathPlanner<T>{

    private static int INFINITEEVAL = 1000000000;

    /**
     * 隣接した二点間の移動に必要なコストを計算する関数
     */
    private FunctionEvaluation<T> calcCost;

    /**
     * ヒューリスティック関数。
     * 評価値の書き換えが必要なためHeuristicクラスでヒューリスティック値を実装
     */
    private Heuristic<T> heuristicMap;

    /**
     * @param calcCost 二点間の移動コストを返す関数
     * @param heuristic 二点間のヒューリスティック関数
     * @param graph あるノードと、それに隣接するノードのMap
     */
    public RTAStar (FunctionEvaluation<T> calcCost,FunctionEvaluation<T> heuristic, Map<T,Set<T>> graph){
        super(heuristic,graph);
        this.calcCost = calcCost;
        this.heuristicMap = new Heuristic<>(heuristic);
    }

    /**
     * RTA*アルゴリズムで最善の次のノードを返す
     * 返り値はリストとなるが、アルゴリズムの仕様上隣接するノード一つのみ格納されたリストを返すことになる
     * @param from 出発地点
     * @param targets 目的地
     * @return パス。正確には隣接した一つのノードのみからなるList。隣接するノードが一つもない場合計算失敗として空のOptionalを返す。
     */
    @Override
    public Optional<List<T>> calc(T from, Collection<T> targets) {
        //ヒューリスティック関数に目的地を設定
        this.heuristicMap.setTargets(targets);

        //現在地がゴールならば何も含まないリスト（パス）を返す
        if(targets.contains(from)){
            return Optional.of(new LinkedList<>());
        }

        //現在地から隣接するノードを獲得
        Set<T> neighbors = super.getGraph().get(from);

        //隣接するノードから最善のノードを計算
        Tuple<T,Integer> bestNode = null;   //最善のノード
        Tuple<T,Integer> secondNode = null; //次善のノード
        for(T neighbor : neighbors){
            int eval = this.calcEval(from,neighbor);
            //もし一番良い評価値が得られたら
            if(bestNode == null || bestNode.snd > eval){
                secondNode = bestNode;
                bestNode = new Tuple(neighbor, eval);
            }
            //2番めに良い評価値が得られたら
            else if(secondNode == null || secondNode.snd > eval){
                secondNode = new Tuple(neighbor, eval);
            }
        }

        //ヒューリスティック値を二番目に良い評価値に更新
        int updateHeuristic = secondNode != null ? secondNode.snd : this.INFINITEEVAL;
        this.heuristicMap.put(from, updateHeuristic);


        //最善のノードただひとつを持つリストを返す
        //隣接するノードがなかった場合bestNodeはNullなので、戻り値は空のOptionalになる
        return Optional.ofNullable(bestNode).map(
                (node) -> {
                    List<T> path = new LinkedList<>();
                    path.add(node.fst);
                    return path;
                }
        );
    }

    /**
     * 評価値を計算する関数。メインのメソッドから手続きを一部分離しただけのメソッド。
     * @param from 出発地
     * @param next 評価値を計算する隣接するノード
     * @return 評価値
     */
    private int calcEval(T from, T next){
        int distance = this.calcCost.calc(from, next);
        int heuristic = this.heuristicMap.calc(next);
        return distance + heuristic;
    }
}
