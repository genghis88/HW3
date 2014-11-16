package edu.nyu.cs.cs2580;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

public class PseudoRelevanceFeedback {
  private Query query = null;
  
  public PseudoRelevanceFeedback(Query q, int m, int k) {
    query = q;
  }
  
  public Map<String,Double> getExpandedTerms(Vector<ScoredDocument> scoredDocs) {
    ArrayList allCounts = new ArrayList<HashMap<String,Integer>>();
    Set<String> words = new HashSet<String>();
    int totalCount = 0;
    Map<String,Double> termScores = new HashMap<String,Double>();
    for(ScoredDocument scoredDoc:scoredDocs) {
      Document d = scoredDoc.get_doc();
      HashMap<String,Integer> scores = d.getSnippetCounts();
      for(String word:scores.keySet()) {
        words.add(word);
        totalCount += scores.get(word);
      }
      allCounts.add(scores);
    }
    for(String word:words) {
      int wordTotal = 0;
      for(int i=0;i<allCounts.size();i++) {
        HashMap<String,Integer> scores = (HashMap) allCounts.get(i);
        wordTotal += scores.get(word);
      }
      termScores.put(word, (double) (wordTotal / totalCount));
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
    return termSortedMap;
  }
}