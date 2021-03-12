package uptick;


import org.apache.log4j.Logger;


public final class ImmutablePair<K,V> implements Pair<K,V>
{
    private static final Logger log = Logger.getLogger(ImmutablePair.class);
    private K k;
    private V v;

    public ImmutablePair()
    {
    }

    public ImmutablePair(final K k, final V v)
    {
        this.k = k;
        this.v = v;
    }



    @Override
    public K getVal1()
    {
        return k;
    }

    @Override
    public V getVal2()
    {
        return v;
    }

    @Override
    public int compareTo(final Pair<K, V> o)
    {

        int cmp = cmp(this.k, o.getVal1());
        if (cmp != 0)
            return cmp;
        return cmp(this.v, o.getVal2());
    }

    static <K> int cmp(final K k1, K k2)
    {
        final String val1 = k1 == null ? "" : k1.toString();
        final String val2 = k2 == null ? "" : k2.toString();
        return val1.compareTo(val2);
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ImmutablePair<?, ?> that = (ImmutablePair<?, ?>) o;

        if (k != null ? !k.equals(that.k) : that.k != null) return false;
        return v != null ? v.equals(that.v) : that.v == null;
    }

    @Override
    public int hashCode()
    {
        int result = k != null ? k.hashCode() : 0;
        result = 31 * result + (v != null ? v.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("ImmutablePair{");
        sb.append("k=").append(k);
        sb.append(", v=").append(v);
        sb.append('}');
        return sb.toString();
    }

}

