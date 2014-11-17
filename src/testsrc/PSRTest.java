package testsrc;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;


import edu.nyu.cs.cs2580.Query;
import edu.nyu.cs.cs2580.DocumentIndexed;
import edu.nyu.cs.cs2580.PseudoRelevanceFeedback;
import edu.nyu.cs.cs2580.ScoredDocument;
import edu.nyu.cs.cs2580.Document;

public class PSRTest {
  public static void main(String[] args) {
    Vector<ScoredDocument> vector = new Vector<ScoredDocument>();
    for(int i=0;i<10;i++) {
      HashMap<String,Integer> snippet = new HashMap<String,Integer>();
      if(i == 0) {
        snippet.put("sachin",5);
        snippet.put("tendulkar",5);
        snippet.put("brian",2);
        snippet.put("lara",2);
        snippet.put("mathew",1);
        snippet.put("hayden",1);
        snippet.put("cricket",4);
        snippet.put("lords",4);
      }
      else if(i == 1) {
        snippet.put("fia",5);
        snippet.put("formula",5);
        snippet.put("one",5);
        snippet.put("racing",2);
        snippet.put("circuit",2);
        snippet.put("grand",2);
        snippet.put("prix",2);
        snippet.put("drivers",1);
      }
      else if(i == 2) {
        snippet.put("auto",5);
        snippet.put("racing",3);
        snippet.put("automobile",5);
        snippet.put("competition",2);
        snippet.put("gasoline",2);
        snippet.put("fueled",2);
        snippet.put("quadricycle",2);
        snippet.put("drivers",1);
      }
      else if(i == 3) {
        snippet.put("fia",5);
        snippet.put("formula",5);
        snippet.put("one",5);
        snippet.put("racing",2);
        snippet.put("circuit",2);
        snippet.put("grand",2);
        snippet.put("prix",2);
        snippet.put("drivers",1);
      }
      else if(i == 4) {
        snippet.put("fia",5);
        snippet.put("formula",5);
        snippet.put("one",5);
        snippet.put("racing",2);
        snippet.put("circuit",2);
        snippet.put("grand",2);
        snippet.put("prix",2);
        snippet.put("drivers",1);
      }
      else if(i == 5) {
        snippet.put("fia",5);
        snippet.put("formula",5);
        snippet.put("one",5);
        snippet.put("racing",2);
        snippet.put("circuit",2);
        snippet.put("grand",2);
        snippet.put("prix",2);
        snippet.put("drivers",1);
      }
      else if(i == 6) {
        snippet.put("fia",5);
        snippet.put("formula",5);
        snippet.put("one",5);
        snippet.put("racing",2);
        snippet.put("circuit",2);
        snippet.put("grand",2);
        snippet.put("prix",2);
        snippet.put("drivers",1);
      }
      else if(i == 7) {
        snippet.put("fia",5);
        snippet.put("formula",5);
        snippet.put("one",5);
        snippet.put("racing",2);
        snippet.put("circuit",2);
        snippet.put("grand",2);
        snippet.put("prix",2);
        snippet.put("drivers",1);
      }
      else if(i == 8) {
        snippet.put("fia",5);
        snippet.put("formula",5);
        snippet.put("one",5);
        snippet.put("racing",2);
        snippet.put("circuit",2);
        snippet.put("grand",2);
        snippet.put("prix",2);
        snippet.put("drivers",1);
      }
      else if(i == 9) {
        snippet.put("fia",5);
        snippet.put("formula",5);
        snippet.put("one",5);
        snippet.put("racing",2);
        snippet.put("circuit",2);
        snippet.put("grand",2);
        snippet.put("prix",2);
        snippet.put("drivers",1);
      }
      DocumentIndexed doc = new DocumentIndexed(i);
      doc.setSnippet(snippet);
      ScoredDocument sd = new ScoredDocument(doc,(10 - i) * 0.09);
      vector.add(sd);
    }
    Query q = new Query("cricket");
    PseudoRelevanceFeedback prf = new PseudoRelevanceFeedback(q, 10, 10);
    Map<String,Double> termSortedMap = prf.getExpandedTerms(vector);
    System.out.println(termSortedMap);
  }
}
