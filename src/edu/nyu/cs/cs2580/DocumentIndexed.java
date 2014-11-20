package edu.nyu.cs.cs2580;

import java.util.HashMap;

/**
 * @CS2580: implement this class for HW2 to incorporate any additional
 * information needed for your favorite ranker.
 */
public class DocumentIndexed extends Document {
<<<<<<< HEAD
  private static final long serialVersionUID = 9184892508124423115L;

  public DocumentIndexed(int docid) {
    super(docid);
  }
=======
	private static final long serialVersionUID = 9184892508124423115L;
	private HashMap<String,Integer> snippet = new HashMap<String,Integer>();
	
	public HashMap<String, Integer> getSnippet() {
    return snippet;
  }

  public void setSnippet(HashMap<String, Integer> snippet) {
    this.snippet = snippet;
  }

  public DocumentIndexed(int docid) {
		super(docid);
	}
>>>>>>> origin/master
}
