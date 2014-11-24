package edu.nyu.cs.cs2580;

import java.util.Scanner;

/**
 * @CS2580: implement this class for HW2 to handle phrase. If the raw query is
 * ["new york city"], the presence of the phrase "new york city" must be
 * recorded here and be used in indexing and ranking.
 */
public class QueryPhrase extends Query {

  public QueryPhrase(String query) {
    super(query);
  }

  @Override
  public void processQuery() 
  {
    if (_query == null) 
    {
      return;
    }
    PorterStemming stemmer = new PorterStemming();
    String[] tokens = _query.split("\"");
    for(int i = 0; i < tokens.length; i++)
    {
      if(i%2 == 0 && tokens[i].length() > 0)
      {
        Scanner sc = new Scanner(tokens[i]);
        while (sc.hasNext()) {
          String s = sc.next();
          s = Stopwords.removeStopWords(s);
          if(s != null) {
            String string = stemmer.stem(s);
            if(string != null) {
              _tokens.add(string);
            }
          }
        }
        sc.close();
      }
      else if(tokens[i].length() > 0)
      {
        Scanner sc = new Scanner(tokens[i].trim());
        StringBuilder sb = new StringBuilder();
        while (sc.hasNext()) {
          String s = sc.next();
          s = Stopwords.removeStopWords(s);
          if(s != null) {
            String string = stemmer.stem(s);
            if(string != null) {
              sb.append(string);
              sb.append(' ');
            }
          }
        }
        sc.close();

        _tokens.add(sb.toString().trim());
      }
    }
  }
}