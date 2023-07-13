package byow.Core;

import java.io.Serializable;

/**
 * C++ style pair.
 */
class Pair<V1, V2> implements Serializable {
    public V1 first;
    public V2 second;

    public Pair(V1 v1, V2 v2) {
        this.first = v1;
        this.second = v2;
    }
}
