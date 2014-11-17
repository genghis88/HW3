package edu.nyu.cs.cs2580;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

public class PseudoRelevanceFeedback {
  private Query query = null;
  private int numDocs;
  private int numTerms;
  
  public PseudoRelevanceFeedback(Query q, int m, int k) {
    query = q;
    numDocs = m;
    numTerms = k;
  }
  
  public Map<String,Double> getExpandedTerms(Vector<ScoredDocument> scoredDocs) {
    ArrayList allCounts = new ArrayList<HashMap<String,Integer>>();
    Set<String> words = new HashSet<String>();
    int totalCount = 0;
    Map<String,Double> termScores = new HashMap<String,Double>();
    for(ScoredDocument scoredDoc:scoredDocs) {
      DocumentIndexed d = (DocumentIndexed) scoredDoc.get_doc();
      HashMap<String,Integer> scores = d.getSnippet();
      HashMap<String,Integer> scores1 = new HashMap<String,Integer>();
      for(String word:scores.keySet()) {
        words.add(word);
        scores1.put(word, scores.get(word));
        totalCount += scores.get(word);
      }
      allCounts.add(scores);
    }
    for(String word:words) {
      int wordTotal = 0;
      for(int i=0;i<allCounts.size();i++) {
        HashMap<String,Integer> scores = (HashMap) allCounts.get(i);
        if(scores.containsKey(word)) {
          wordTotal += scores.get(word);
        }
      }
      //System.out.println("WORD TOTAL "+wordTotal + " total count " + totalCount);
      termScores.put(word, (double) (wordTotal * 1.0 / totalCount));
    }
    
    class ValueComparator implements Comparator<String> {

      Map<String, Double> base;
      public ValueComparator(Map<String, Double> base) {
        this.base = base;
      }

      // Note: this comparator imposes orderings that are inconsistent with equals.    
      public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
          return -1;
        } else {
          return 1;
        } // returning 0 would merge keys
      }
    }
    ValueComparator bvc =  new ValueComparator(termScores);
    Map<String,Double> termSortedMap = new TreeMap<String,Double>(bvc);
    termSortedMap.putAll(termScores);
    Map<String,Double> restrictedTermSortedMap = new LinkedHashMap<String,Double>();
    int j = 0;
    double total = 0.0;
    for(Map.Entry<String, Double> e:termSortedMap.entrySet()) {
      restrictedTermSortedMap.put(e.getKey(), e.getValue());
      total += e.getValue();
      j++;
      if(j == numTerms) {
        break;
      }
    }
    for(String word:restrictedTermSortedMap.keySet()) {
      restrictedTermSortedMap.put(word, restrictedTermSortedMap.get(word) / total);
    }
    return restrictedTermSortedMap;
  }
}