package arbres;

import graphe.Ville;

import java.util.HashMap;

// Q1

public class UnionFind {
	//parent relation, parent.put(src,dst) indicates that src points to dst
    private HashMap<Ville,Ville> parent;
    
    public UnionFind( ){
    	this.parent=new HashMap<Ville,Ville>();
    }
    
    public Ville find( Ville src ){
    	Ville anc = parent.get(src);
    	
    	if(anc==null)
    		return src;
    	
    	
    		Ville r = find(parent.get(src));
    		this.parent.put(src, r);
    		return r;
    	

    	
    }
    
    public void union( Ville v0, Ville v1 ){
        Ville irep = find(v0);
        Ville jrep = find(v1);
        if(!jrep.equals(irep)){
        this.parent.put(jrep, irep);
        }
        
    	return;
    	
    }
}
