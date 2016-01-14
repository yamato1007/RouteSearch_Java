package yamato.pathplan.rtastar;

import yamato.pathplan.FunctionEvaluation;

import java.util.*;
/**
 * Created by k13082kk on 2016/01/14.
 */
public class Heuristic<T> {
    /**
     * 評価関数そのもの。
     * RTA*ではヒューリスティック値を更新する必要が有るためMapでヒューリスティック関数を実装する。
     */
    private Map<T, Integer> heuristicMap;
    /**
     * ヒューリスティック関数。
     * RTA*ではヒューリスティック値は変動するため、この関数はヒューリスティック値の初期値を求める関数ということになる
     */
    private FunctionEvaluation<T> heuristicFunc;
    /**
     * 前回計算時の目的地。
     * RTA*ではヒューリスティック値が目的地に依存する
     * 目的地が変わったことを検知するため前回の目的地を格納しておく必要がある
     */
    private Collection<T> beforeTargets;
    /**
     * 今回計算時の目的地
     */
    private Collection<T> targets;


    /**
     * コンストラクタ
     * @param heuristic ヒューリスティック関数
     */
    public Heuristic(FunctionEvaluation<T> heuristic){
        this.heuristicFunc = heuristic;
        this.targets = null;
    }

    /**
     * 目的地の設定。
     * 目的地が前回と変われば、ヒューリスティック値を初期化する。
     * @param targets 目的地
     */
    public void setTargets(Collection<T> targets){
        this.beforeTargets = targets;
        this.targets = targets;
        //前回と目的地が一致しなければ
        if( !this.beforeTargets.equals(this.targets) ){
            this.heuristicMap = new HashMap<>();
        }
    }

    /**
     * ヒューリスティック値を得る
     * @param from 出発地点
     * @return ヒューリスティック値
     */
    public int calc(T from){
        return this.get(from);
    }


    /**
     * ヒューリスティック地を得る関数
     * 値がheuristicMapに登録されてなければ、heuristicFuncを用いて初期値を生成、登録し、返す
     * @param from 出発地点
     * @return ヒューリスティック値
     */
    public int get(T from){
        if(!this.heuristicMap.containsKey(from)){
            Integer minCost = null;
            for(T target : this.targets){
                int cost = this.heuristicFunc.calc(from, target);
                if(minCost == null || minCost > cost){
                    minCost = cost;
                }
            }
            this.heuristicMap.put(from, minCost);
        }
        return this.heuristicMap.get(from);
    }

    public void put(T from, int eval){
        this.heuristicMap.put(from,eval);
    }
}
