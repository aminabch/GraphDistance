package arbres;

import java.util.*;

import graphe.*;


public class SpanningTree {
    
    public static Collection<Arc> kruskal(UnionFind u, GrapheEuclidien g){
    	// Q2
    	Collection <Arc> C =new LinkedList <Arc>();
    	LinkedList <Arc> l = (LinkedList<Arc>) g.tousLesArcs();
    	Comparator<Arc> a = new ArcComparator ();
    	Collections.sort(l,a);

     	for(Arc v :l) {
    				
    		Ville f = u.find(v.source);
    		Ville t =u.find(v.target);
		
    		if(!f.equals(t)){
    		C.add(v);
    		u.union(v.source, v.target);    						 	   		
    		}		
    	}
    	return C;
    }
    
    public static Collection<Collection<Arc>> kruskal(GrapheEuclidien g){
    	HashMap<Ville,Collection<Arc>> arclist = new HashMap<Ville,Collection<Arc>>();
    	UnionFind u = new UnionFind();
    	Collection <Arc> C = kruskal(u,  g);
      	
    	for(Arc a : C){
    		Ville v = u.find(a.source);
    		arclist.put(v, new LinkedList<Arc>());
    	
    	}	
    	for(Arc E : C){
    		Ville v = u.find(E.source);
    		arclist.get(v).add(E);
    	}
    
    	return arclist.values();
    }
    
    public static Collection<Arc> primTree(HashSet<Ville> nonVisited, Ville start, GrapheEuclidien g){
  
    	Comparator<Arc> c = new ArcComparator();
    	PriorityQueue <Arc> q = new PriorityQueue<Arc>(c);
    	Collection<Arc> tree = new LinkedList<Arc>();
    	//Thealgorithm starts at a Ville v,puts all arcs connected to v into q, 
    	//and removes v from the set of non-visited elements. 
    	//Then as long as q is not empty 
    	List <Arc> L = g.arcsSortant(start);   	
    	q.addAll(L);
    	nonVisited.remove(start); 	
    	while (!q.isEmpty()){
    		Arc e = q.poll();
    		Ville u = e.target;
    		if(nonVisited.contains(u)){
    			List <Arc> l = g.arcsSortant(u);
    	    	q.addAll(l);
    			nonVisited.remove(u);
    			tree.add(e);
    		}   	
    	}
    	return tree;  	
    }
    
    public static Ville peek (HashSet<Ville>  c){
    	
    	int size = c.size();
    	int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
    	int i = 0;
    	for(Ville obj : c)
    	{
    	    if (i == item)
    	        return obj;
    	    i++;
    	}
    	return null;
    }
    
    
    
    public static Collection<Collection<Arc>> primForest(GrapheEuclidien g){
    	HashSet<Ville> nonVisited = new HashSet<Ville>();
    	Collection<Collection<Arc>> result = new LinkedList<Collection<Arc>>();
    	
    	List<Arc> l = g.tousLesArcs();
    	for(Arc a :l){
    		nonVisited.add(a.source);
    		nonVisited.add(a.target);
    	}
    	 	
    	while(!nonVisited.isEmpty()){
    		Ville start = peek(nonVisited);
    		result.add(primTree(nonVisited, start, g));
    	}	
    	return result;
    }
    
   
}
