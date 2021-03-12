package uptick;




public interface Pair<K, V> extends Comparable<Pair<K,V>>
{
    K getVal1();

    V getVal2();

}
