package ammo;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.HashSet;

public class ComputeEnrichments {


    HashSet<String> terms;

    HashSet<String> abs;
    
    HashMap<String,HashSet<String>> absGenes;

    HashMap<String,HashSet<String>> termGenes;

    HashSet<String> totalGenes ;
    int totalnumGenes;

    public ComputeEnrichments(String ontology, String data) {
	try {
	    totalnumGenes = 0;
	    totalGenes = new HashSet<String> ();
	    terms = new HashSet<String> ();
	    abs = new HashSet<String> ();
	    absGenes = new HashMap<String, HashSet<String>> ();
	    HashSet<String> tgenes = new HashSet<String> ();
	    termGenes = new HashMap<String, HashSet<String>> ();

	
	    SerializableGraphADS sga = new SerializableGraphADS();
	    GraphADS ga = sga.restoreSerializableObject("serialObjectsOBS_Roots/" + ontology + ".obj");
	    System.out.println(ga.name);
	    IdMaps im = new IdMaps("idMaps/" + ontology  + "idMaps.obj");

	    HashMap<Long,HashSet<Long>> hm = ga.adjacencyList;

	    Set set = hm.entrySet();
	    Iterator i = set.iterator();
	
	    while(i.hasNext()){
		Map.Entry me = (Map.Entry)i.next();
		Long s = (Long) me.getKey();
		HashSet<Long> l = (HashSet<Long>) me.getValue();
	    
		Iterator j = l.iterator();
		while(j.hasNext()) {
		    Long d = (Long) j.next();
		    terms.add(im.getName(s));
		    terms.add(im.getName(d));
		}
	    
           }

	    File file = new File(data);
	    FileInputStream fis = new FileInputStream(file);
	    BufferedInputStream bis = new BufferedInputStream(fis);
	    DataInputStream dis = new DataInputStream(bis);

	    while (dis.available() != 0) {
		String record = dis.readLine();
		String [] parts = record.split("\t");
		String a = parts[0];
		if (!abs.contains(a)) {
		    abs.add(a);
		    String [] genes = parts[1].split(",");
		    for(int j=0; j < genes.length; j++)
			tgenes.add(genes[j]);
		    absGenes.put(a,tgenes);
		    union(totalGenes, tgenes);
		}
	    }

	    //  System.out.println(absGenes);
	
	    fis.close();
	    bis.close();
	    dis.close();

	} catch (Exception e) {
	    System.out.println(e.getMessage());

	}

    }

    public void populateOntology() {
	Iterator i = terms.iterator();

	while(i.hasNext()) {
	    String term = (String)i.next();
	    HashSet<String> genes = getAccessions(term);
	    termGenes.put(term,genes);
	}

	//	System.out.println(termGenes);

    }

    private HashSet<String> getAccessions(String term) {
	HashSet<String> genes = new HashSet<String>();;
	int y = 0;
	String [] parts = term.split(" ");
	
	Iterator i = abs.iterator();
	while(i.hasNext()) {
	    String ab = (String)i.next();
	    for (int j=0; j < parts.length; j++) {
		if (ab.indexOf(parts[j]) != -1) {
		    y += 1;
		}
	    }
	    if (y >= parts.length)	{
		union(genes,absGenes.get(ab));
	    }
	}

	return genes;
			    
    }

    private void union(HashSet<String> main, HashSet<String> child) {
	if (child == null) return; 	
	Iterator i = child.iterator();
	while(i.hasNext()) {
	    String gene = (String)i.next();
	    main.add(gene);
	}
    }

    private int intersect(HashSet<String> termGene, HashSet<String> geneList) {
	int count = 0;

	Iterator i = geneList.iterator();
	while(i.hasNext()) {
	    String gene = (String)i.next();
	    if (termGene.contains(gene))
		count ++;
	}
	return count;

    }

    private double fact(int n) {
	int i = n;
	double ans = 1.0;
	while(i > 0) {
	    ans *= (double) i;
	    i = i-1;
	}
	return ans;
    }

    private double fisher(int totalTermGene, int totalGene, int specificGene, int geneListSize) {
	
	double pvalue = (fact(specificGene + totalTermGene)*fact(totalGene + geneListSize)*fact(totalTermGene+totalGene)*fact(specificGene+geneListSize))/(fact(totalTermGene)*fact(totalGene)*fact(specificGene)*fact(geneListSize)*fact(totalTermGene+totalGene+specificGene+geneListSize));
	
	return pvalue;

    }

private double chi(int totalTermGene, int totalGene, int specificGene, int geneListSize) {
    
    double chi = 0;

    double t1 = (double) (specificGene*totalGene - totalTermGene*geneListSize);
    double t2 = (double) (specificGene + totalGene + totalTermGene + geneListSize);
    double t3 = (double) (specificGene + geneListSize) * (double) (totalTermGene + totalGene) * (double) (specificGene + totalTermGene) * (double) (totalGene + geneListSize);
    if (t3 != 0)
	chi = (t1*t1*t2)/t3;

    return chi;
}

    private double istermEnriched(String term, HashSet<String> geneList) {
	HashSet<String> termGeneList = termGenes.get(term);
	int totalTermGene = termGeneList.size();
	int totalGene = totalGenes.size() - totalTermGene + 1;

	int specificGene = intersect(termGeneList, geneList);
	int geneListSize = geneList.size() - specificGene + 1;

	//	System.out.println(totalTermGene + "," + totalGene + "," + specificGene + "," + geneListSize);

	//	double pvalue = fisher(totalTermGene, totalGene, specificGene, geneListSize);
	
	double pvalue = chi(totalTermGene, totalGene, specificGene, geneListSize);
	return pvalue;
    }

    public void isEnriched(HashSet<String> geneList) {

	Iterator i = terms.iterator();
	while(i.hasNext()) {
	    String term = (String)i.next();
	    double pvalue = istermEnriched(term,geneList);
	    //    System.out.println(pvalue);
	    if ( pvalue > 7.0 && pvalue < 10.0) {
		System.out.println("Given User Gene List " + geneList + " is enriched for term " + term + " with p-value between 0.01 and 0.001"); 
	} else if (pvalue > 10.0) {
	    System.out.println("Given User Gene List " + geneList + " is enriched for term " + term + " with p-value less than 0.001");
	}

	}

    }

    public HashSet<String> convertToSet(String listPath) {

	HashSet<String> geneList = new HashSet<String>();
	try {

	    File file = new File(listPath);
	    FileInputStream fis = new FileInputStream(file);
	    BufferedInputStream bis = new BufferedInputStream(fis);
	    DataInputStream dis = new DataInputStream(bis);

	    while (dis.available() != 0) {
		String gene = dis.readLine();
		geneList.add(gene);
	    }
	    //	    System.out.println(geneList);
	    return geneList;

	} catch (Exception e) {
	    System.out.println(e.getMessage());
	    return geneList;
	}

	
    }


    public static void main(String args[]) {
	ComputeEnrichments ce = new ComputeEnrichments(args[0],args[1]);
	ce.populateOntology();
	ce.isEnriched(ce.convertToSet(args[2]));

    }
   



}