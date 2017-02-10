package graphe;

import java.util.Comparator;

public class ArcComparator implements Comparator<Arc> {
    @Override
    public int compare(Arc a0, Arc a1) {
        double d0 = a0.length; // a0.origine.distance( a0.destination );
        double d1 = a1.length; // a1.origine.distance( a1.destination );
        if( d0 < d1 )
            return -1;
        else if( d0 > d1 )
            return 1;
        else
            return 0;
    }
}
