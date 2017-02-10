
import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import arbres.*;
import carte.*;
import graphe.*;

public class Test {
	static GrapheEuclidien g;
	static Ville v1;
	static Ville v2;
	static Carte carte;

	static Ville getFirst(Collection<Ville> c) {
		for (Ville v : c)
			return v;
		return null;
	}

	public static void initFrance(int distance){
		carte = new Carte("fr.txt");
		v1 = carte.premiereVille("Palaiseau");
		g = new GrapheEuclidien(distance);
		for (Ville v : carte.villes()) {
			g.ajouterVille(v);
		}
		g.connectNeighbours(distance);
	}

	public static boolean existeArc(GrapheEuclidien g, Ville s, Ville t) {
		for (Arc a : g.arcsSortant(s))
			if (a.target.equals(t))
				return true;
		return false;
	}

	static final Color TRANSLUCENT_GRAY = new Color(192, 192, 192, 64);
	static final Color TRANSLUCENT_ORANGE = new Color(255, 200, 0, 128);

	public static void test0() {
		initFrance(4000);
		// test de reciprocite
		for (Ville s : g.villes())
			for (Arc a : g.arcsSortant(s)) {
				if (!existeArc(g, a.target, s))
					throw new Error("l'arc " + a + " n'a pas de reciproque");
			}
		// affichage graphique
		Visualisation f = new Visualisation("Test 2");
		f.setOvalRadius(4);
		f.definirGraphe(g);
		Map<Arc, Color> couleurs = new HashMap<Arc, Color>();
		for (Ville s : g.villes())
			for (Arc a : g.arcsSortant(s))
				if (a.length < 1500)
					couleurs.put(a, Color.green);
				else couleurs.put(a, Color.red);
		f.couleurArcs(couleurs);
		int arcs = 0;
		for (Ville v : g.villes())
			arcs += g.arcsSortant(v).size();
		System.out.println("There are " + arcs + " arcs in this graph.");
	}
	
    // Q1: UnionFind
    public static void test1( ){
    	carte = new Carte("fr.txt");
        UnionFind u = new UnionFind( );
        Ville v0 = carte.premiereVille( "Palaiseau" );
        Ville v1 = carte.premiereVille( "Paris" );
        Ville v2 = carte.premiereVille( "Melun" );
        Ville v3 = carte.premiereVille( "Orly" );
        Ville v4 = carte.premiereVille( "Massy" );
        Ville v5 = carte.premiereVille( "Clamart" );
       
        u.union( v0, v1 );
        u.union( v2, v3 );
        u.union( v4, v5 );
        u.union( v0, v3 );
       
        System.out.println( u.find( v0 ).equals( u.find( v2 ) ) );
        System.out.println( u.find( v0 ).equals( u.find( v1 ) ) );
        System.out.println( u.find( v3 ).equals( u.find( v5 ) ) );
    }
	
    public static void analyzeMonoColor( Collection<Arc> arcs, boolean display, String title ) {
		int length = 0;
		Set<Ville> villes = new HashSet<Ville>();
		for (Arc a : arcs) {
			length += a.length;
			villes.add(a.source);
			villes.add(a.target);
		}
		System.out.println("Total length: " + " : " + length + " m");
		System.out.println(villes.size() + " connected cities");
		if (!display)
			return;
		Map<Ville, Color> couleurVilles = new HashMap<Ville, Color>();
		Map<Arc, Color> couleurArcs = new HashMap<Arc,
				Color>();
		for (Ville v : villes)
			couleurVilles.put(v, TRANSLUCENT_ORANGE);
	
		for (Arc a : arcs)
			couleurArcs.put(a, TRANSLUCENT_ORANGE);
		Visualisation f = new Visualisation(title);
		f.couleurVilles(couleurVilles);
		f.couleurArcs(couleurArcs);
		f.definirGraphe(g);
	}
	


    public static void analyzeMultiColor( Collection<Collection<Arc>> listes, boolean display, String title ) {
		int length = 0;
		Set<Ville> villes = new HashSet<Ville>();
		for (Collection<Arc> arcs:listes){
			for (Arc a : arcs) {
				length += a.length;
				villes.add(a.source);
				villes.add(a.target);
			}
		}
		System.out.println("Total length " + " : " + length + " m");
		System.out.println(villes.size() + " connected cities");
		System.out.println(listes.size()+" components in the forest");
		if (!display)
			return;
		Map<Ville, Color> couleurVilles = new HashMap<Ville, Color>();
		Map<Arc, Color> couleurArcs = new HashMap<Arc,Color>();
		for (Ville v : villes)
			couleurVilles.put(v, TRANSLUCENT_ORANGE);
		Random rand=new Random();
		for (Collection<Arc> arcs:listes){
			Color c=new Color(rand.nextInt(256),rand.nextInt(256),rand.nextInt(256));
			for (Arc a : arcs)
				couleurArcs.put(a, c);
		}

		Visualisation f = new Visualisation(title);
		f.couleurVilles(couleurVilles);
		f.couleurArcs(couleurArcs);
		f.definirGraphe(g);
	}
		
    // Q2: Kruskal all components together
    public static void test2(){
		initFrance(4000);
		long t0 = System.currentTimeMillis();
		Collection<Arc> arcs = SpanningTree.kruskal(new UnionFind(), g);
		long tf = System.currentTimeMillis();
		System.out.println("Computation time (Kruskal first version) : " + (tf - t0) + " ms");
		analyzeMonoColor(arcs, true, "Kruskal first version");
	}
    
    // Q3: Kruskal separating the components
    public static void test3(){
		initFrance(4000);
		long t0 = System.currentTimeMillis();
		Collection<Collection<Arc>> listes = SpanningTree.kruskal(g);      
		long tf = System.currentTimeMillis();
		System.out.println("Computation time (Kruskal second version) : " + (tf - t0) + " ms");
		analyzeMultiColor(listes, true, "Kruskal second version");
	}
	
    // Q4: Prim tree
    public static void test4(){
		initFrance(4000);
		HashSet<Ville> nonVisited=new HashSet<Ville>();
		for (Ville v:g.villes()) nonVisited.add(v);
		long t0 = System.currentTimeMillis();
		Ville start=carte.premiereVille("Palaiseau");
		Collection<Arc> arcs = SpanningTree.primTree(nonVisited,start, g);
		long tf = System.currentTimeMillis();
		System.out.println("Computation time (Prim first version) : " + (tf - t0) + " ms");
		analyzeMonoColor(arcs, true, "Prim first version");
	}

	// Q8: Prim forest
    public static void test5(){
		initFrance(4000);
		long t0 = System.currentTimeMillis();
		Collection<Collection<Arc>> listes = SpanningTree.primForest(g);
		long tf = System.currentTimeMillis();
		System.out.println("Computation time (Prim second version) : " + (tf - t0) + " ms");
		analyzeMultiColor(listes, true, "Prim second version");
    }
    
    public static void main(String[] args) {
    	//test0();
    	//test1();
    	// test2();
    	//test3();
    	// test4();
        test5();
    }
}









