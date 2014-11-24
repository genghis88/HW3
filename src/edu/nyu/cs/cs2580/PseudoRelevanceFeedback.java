package edu.nyu.cs.cs2580;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

public class PseudoRelevanceFeedback {
  private Indexer indexer;
  
  public PseudoRelevanceFeedback(Indexer indexer) {
    this.indexer = indexer;
  }
  
  public Map<String,Double> getExpandedTerms(Vector<ScoredDocument> scoredDocs, int numTerms) {
    Set<String> words = new HashSet<String>();
    int totalCount = 0;
    Map<String,Double> termScores = new HashMap<String,Double>();
    List<Integer> docIds = new ArrayList<Integer>();
    
    for(ScoredDocument scoredDoc:scoredDocs) {
      DocumentIndexed d = (DocumentIndexed) scoredDoc.get_doc();
      docIds.add(d._docid);
    }
    
    Collections.sort(docIds);
    List<LinkedHashMap<String,Integer>> docScores = indexer.getTerms(-1,docIds);
    for(LinkedHashMap<String,Integer> scores: docScores) {
      for(String word:scores.keySet()) {
        words.add(word);
        totalCount += scores.get(word);
      }
    }
    
    for(String word:words) {
      int wordTotal = 0;
      for(int i=0;i<docIds.size();i++) {
        int docId = docIds.get(i);
        HashMap<String,Integer> scores = (HashMap<String,Integer>) docScores.get(i);
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
      System.out.println(word+"\t"+restrictedTermSortedMap.get(word));
    }
    return restrictedTermSortedMap;
  }
}