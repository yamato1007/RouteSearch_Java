package yamato.util;

import java.lang.reflect.*;

/**
 * Created by k13082kk on 2016/01/14.
 */
public class Tuple<A,B> implements Comparable<Tuple<A,B>> ,Cloneable{
    final public A fst;
    final public B snd;

    public Tuple(A a, B b){
        this.fst = a;
        this.snd = b;
    }

    @Override
    public String toString(){
        return new StringBuilder("(").append(this.fst.toString()).append(",").append(this.snd.toString()).append(")").toString();
    }

    @Override
    public boolean equals(Object a){
        if(a == this) return true;
        if(a == null) return false;

        if(!(a instanceof Tuple)) return false;
        Tuple<?,?> b = (Tuple<?,?>) a;
        if(b.fst.getClass() != this.fst.getClass()) return false;
        if(b.snd.getClass() != this.snd.getClass()) return false;
        Tuple<A,B> c = (Tuple<A,B>) b;

        if(this.fst.equals(c.fst) && this.snd.equals(c.snd)){
            return true;
        }

        return false;
    }

    @Override
    public int hashCode(){
        int hash = 37;
        hash *= 31 + this.fst.hashCode();
        hash *= 31 + this.snd.hashCode();
        return hash;
    }

    @Override
    public int compareTo(Tuple<A,B> o) {
        int c = this.compare(this.fst, o.fst);
        if(c == 0){
            c = this.compare(this.snd, o.snd);
        }
        return c;
    }

    /**
     * 任意のオブジェクト２つを比較する関数
     * そのオブジェクトがCompalableであれば比較するが
     * そうでなければ比較できず、等しいを示す0を返す
     * @return 比較の結果
     */
    private int compare(Object a, Object b){
        return a instanceof Comparable ? ((Comparable) a).compareTo(b) : 0;
    }


    @Override
    public Tuple<A,B> clone(){
        A a = (A)this.cloneOne(this.fst);
        B b = (B)this.cloneOne(this.snd);
        return new Tuple<>(a,b);
    }

    /**
     * 任意のオブジェクトがCloneableであれば、クローンを生成し返す
     * Cloneableでなければ、そのままオブジェクトを返す
     * @param a クローンの基
     * @return クローン
     */
    private Object cloneOne(Object a){
        Object clone;
        if(a instanceof Cloneable){
            try {
                Method cloneMethod = a.getClass().getMethod("clone");
                clone = cloneMethod.invoke(a);
            } catch (Exception e) {
                clone = a;
            }
        }
        else{
            clone = a;
        }
        return clone;
    }
}
