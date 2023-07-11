package byow.Core;

/**
 * C++ style pair.
 */
class Pair<V1, V2> {
    public V1 first;
    public V2 second;

    public Pair(V1 v1, V2 v2) {
        this.first = v1;
        this.second = v2;
    }
}
