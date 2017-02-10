package graphe;


public class Arc {
  public final Ville source, target;
  public double length;

  public Arc(Ville o, Ville d, double c) {
    source = o;
    target = d;
    length = c;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Arc))
      return false;
    Ville o2 = ((Arc) o).source;
    Ville d2 = ((Arc) o).target;
    return (source == null && o2 == null || source.equals(o2))
        && (target == null && d2 == null || target.equals(d2))
        || (source == null && d2 == null || source.equals(d2))
        && (target == null && o2 == null || target.equals(o2));
  }

  @Override
  public int hashCode() {
    int ho = source == null ? 0 : source.hashCode();
    int hd = target == null ? 0 : target.hashCode();
    return ho ^ (ho - 1) * hd ^ (hd - 1);
  }

  @Override
  public String toString() {
    return source + " -- " + target;

  }
}
