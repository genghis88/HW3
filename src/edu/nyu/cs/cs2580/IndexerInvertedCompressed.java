package edu.nyu.cs.cs2580;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.nyu.cs.cs2580.Compress.Compression;
import edu.nyu.cs.cs2580.Compress.GammaCompression;
import edu.nyu.cs.cs2580.SkipPointer.SkipPointer;
import edu.nyu.cs.cs2580.SearchEngine.Options;


/**
 * @CS2580: Implement this class for HW2.
 */
public class IndexerInvertedCompressed extends Indexer implements Serializable{

	private static final long serialVersionUID = -113983643337683565L;
	
	public class PostingList implements Serializable
	{
		private static final long serialVersionUID = -3373610604746592865L;
	
		private int count;
	    private BitSet bits;
	    private Compression compress;
	    private int corpusDocFrequency;
	    private int corpusTermFrequency;

		public BitSet getBits() {
			return bits;
		}

		public void setBits(BitSet bits) {
			this.bits = bits;
		}

	    public PostingList()
	    {
	      count = 0;
	      bits = new BitSet();
	      compress = new GammaCompression();
	    }

	    public int getCount() {
	      return count;
	    }
	    
	    public void setCount(int count) {
	      if(count < this.count)
	    	  System.out.println(count);
	      
	      this.count = count;
	    }   
	    
	    public void add(int x)
	    {
	    	setCount(compress.compress(x, getBits(), count));
	    }
	    
	    public void add(List<Integer> list)
	    {
	    	for(Integer i: list)
	    		setCount(compress.compress(i, getBits(), count));
	    }
	    
	    public int[] get(int pos)
	    {
	    	try {
	    		return compress.deCompress(getBits(), getCount(), pos);
			} catch (Exception e) {
				// TODO: handle exception
				return new int[]{-1,-1};
			}    	
	    }
		
	    public int getCorpusDocFrequency() {
			return corpusDocFrequency;
		}
		
	    public void setCorpusDocFrequency(int corpusDocFrequency) {
			this.corpusDocFrequency = corpusDocFrequency;
		}
		
	    public int getCorpusTermFrequency() {
			return corpusTermFrequency;
		}
		
	    public void setCorpusTermFrequency(int corpusTermFrequency) {
			this.corpusTermFrequency = corpusTermFrequency;
		}
		
		public void increaseCorpusTermFreqency()
		{
			this.corpusTermFrequency++;
		}
		
		public void increaseCorpusDocFreqency()
		{
			this.corpusDocFrequency++;
		}
	  
	}

	private Vector<DocumentIndexed> _documents = new Vector<DocumentIndexed>();
	private HashMap<String,PostingList> index = new HashMap<String,PostingList>();
	private HashMap<String,SkipPointer> skippointermap = new HashMap<String, SkipPointer>();
	private HashMap<String,Integer> allWords = new HashMap<String,Integer>();
	private HashMap<Integer,String> stringIdToWordMap = new HashMap<Integer,String>();
	private List<LinkedHashMap<Integer,Integer>> auxilliaryIndex = new ArrayList<LinkedHashMap<Integer,Integer>>();
	

	private long totalWordsInCorpus = 0;
	private int skipSteps;
	private int maxDocs;
	private HashMap<String, Integer> urlToDocId;
	
	public IndexerInvertedCompressed(Options options) {
		super(options);
		System.out.println("Using Indexer: " + this.getClass().getSimpleName());
	}

	@Override
	public void constructIndex() throws IOException 
	{
		long x = (System.currentTimeMillis());
		urlToDocId = new HashMap<String, Integer>();
		skipSteps = _options.skips;
		maxDocs = _options.maxDocs;
		try {
			parse();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    System.out.println(
	        "Indexed " + Integer.toString(_numDocs) + " docs with " +
	            Long.toString(_totalTermFrequency) + " terms.");


	    
	    //System.gc();
	    String indexFile = _options._indexPrefix + _options._index_file;
	    System.out.println("Store index to: " + indexFile);
	    //In this place we will have cleared index, and the auxilliary index
	    ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(indexFile));
	    writer.writeObject(this);
	    writer.close();

	    System.out.println("Index File Created!");
	    x = (System.currentTimeMillis() - x)/1000/60;
	    System.out.println("Time to load:" + x + " mins.");
	}
	

	@Override
	public void loadIndex() throws IOException, ClassNotFoundException 
	{
		long x = (System.currentTimeMillis());
		String indexFile = _options._indexPrefix + _options._index_file;
	    System.out.println("Load index from: " + indexFile);
	    this.skipSteps = _options.skips;
	    ObjectInputStream reader = new ObjectInputStream(new FileInputStream(indexFile));
	    IndexerInvertedCompressed loaded = (IndexerInvertedCompressed) reader.readObject();

	    this._documents = loaded._documents;
	     //Compute numDocs and totalTermFrequency b/c Indexer is not serializable.
	    this._numDocs = _documents.size();
	    this._totalTermFrequency = loaded._totalTermFrequency;
	    this.skippointermap = loaded.skippointermap;
	    this.totalWordsInCorpus = loaded.totalWordsInCorpus;
//	    this.urlToDocId = LinkDocIDMapGenerator.generateMap(_options._corpusPrefix)
	    this.index = loaded.index;
	    this.skipSteps = loaded.skipSteps;
	    this.maxDocs = loaded.maxDocs;
	    this.stringIdToWordMap = loaded.stringIdToWordMap;
//	    this.index = (HashMap<String, IndexerInvertedCompressed.PostingList>)reader.readObject();
	    reader.close();
	    loadAuxilliaryIndex();
	    x = (System.currentTimeMillis() - x)/1000/60;
	    System.out.println("Time to load:" + x + " mins.");
	}
	
	public void loadAuxilliaryIndex() throws IOException, ClassNotFoundException {
	  String auxilliaryIndexFilePrefix = _options._indexPrefix + _options._aux_index_file;
	  for(int i=0;i<=_documents.size()/maxDocs;i++) {
	    System.out.println("Loading file " + auxilliaryIndexFilePrefix+"_"+i);
	    ObjectInputStream reader = new ObjectInputStream(new FileInputStream(auxilliaryIndexFilePrefix+"_"+i));
	    ArrayList<LinkedHashMap<Integer,Integer>> loaded = (ArrayList<LinkedHashMap<Integer,Integer>>) reader.readObject();
	    auxilliaryIndex.addAll(loaded);
	    loaded.clear();
	    reader.close();
	  }
	}
	
	public LinkedHashMap<String,Integer> getTerms(int m,int docid) {
	  LinkedHashMap<Integer,Integer> termFrequencies = auxilliaryIndex.get(docid);
	  LinkedHashMap<String,Integer> t = new LinkedHashMap<String,Integer>();
	  if(m == -1) {
	    m = termFrequencies.size();
	  }
	  int i = 0;
	  for(Integer key:termFrequencies.keySet()) {
	    if(i == m) {
	      break;
	    }
	    t.put(stringIdToWordMap.get(key), termFrequencies.get(key));
	    i++;
	  }
	  return t;
	}
	
	@Override
	public Document getDoc(int docid) {
		return _documents.get(docid);
	}
	
	  /**
	   * In HW2, you should be using {@link DocumentIndexed}
	   */
	  @Override
	public Document nextDoc(Query query, int docid) {
	   
	   int docids[] = new int[query._tokens.size()];
	   int maxdocid = -1;
	   boolean flag = true;
	   
	   for(int i = 0 ; i < docids.length ; i++)
	   {
		   docids[i] = -1;
	   }
	   
	   for(int i = 0; i < docids.length; i++)
	   {
		   String token = query._tokens.get(i);
		   ArrayList<Integer> posting = next(token, (maxdocid > docid ? maxdocid - 1: docid));
		   if(posting == null)
		   {
			   return null;
		   }
		   
		   docids[i] = posting.get(0);
		   
		   if(docids[i] == -1)
		   {
			   return null;
		   }
		   if(i > 0 && docids[i] != docids[i-1])
		   {
			   flag = false;
		   }
		   if(maxdocid < docids[i])
		   {
			   maxdocid = docids[i];
		   }
	   }
	   if(!flag)
	   {
		   return nextDoc(query, maxdocid - 1);
	   }
	   
	   return getDoc(docids[0]);
	}
	
	@Override
	public int corpusDocFrequencyByTerm(String term) {
		return 0;
	}
	
	@Override
	public int corpusTermFrequency(String term) {
		return 0;
	}
	
	  /**
	   * @CS2580: Implement this to work with your RankerFavorite.
	   */
	@Override
	public int documentTermFrequency(String term, int docid) {
		return 0;
	}
	
	
	public void parse() throws Exception 
	{
	    String corpusDirectoryString = _options._corpusPrefix;
	    System.out.println("Construct index from: " + corpusDirectoryString);
	    
	    HashMap<String,Integer> skipnumberlist = new HashMap<String,Integer>();
	    HashMap<String,Integer> posinpostinglist = new HashMap<String,Integer>();
	    HashMap<String,Integer> lastdocinserted = new HashMap<String,Integer>();
	    
	    
	    final File corpusDirectory = new File(corpusDirectoryString);
	    int i = 0;
	    
	    String auxilliaryIndexFilePrefix = _options._indexPrefix + _options._aux_index_file;
	    int docIdIndex = 0;
	    for (final File fileEntry : corpusDirectory.listFiles()) 
	    {
	    	
	    	//if(i == 100)
	    	//	break;
	    	i++;
	    	if (!fileEntry.isDirectory()) 
	    	{
	    		String url = fileEntry.getName();
	    		String title = null;
	    		StringBuilder sb = new StringBuilder();
	    		Element doc = Jsoup.parse(fileEntry, "UTF-8").body();
	    		Element head = doc.select("h1[id=firstHeading]").first();
	    		if(head != null && head.text() != null) 
	    		{
	    			//System.out.println(head.text().trim());
	    			title = head.text().trim();
	    			sb.append(title.toLowerCase());
	    			sb.append(' ');
	    		}
	    		Elements content_text = doc.select("div[id=mw-content-text]");
	    		
	    		for (Element elem : content_text) 
	    		{
	    			Elements paras = elem.getElementsByTag("p");
//	    			Elements headers = elem.getElementsByTag("h2");
//	
//	    			for (Element header : headers) 
//	    			{
//	    				Element firsthead = header.getElementsByClass("mw-headline").first();
//	    				if(firsthead != null) 
//	    				{
//	    					sb.append(firsthead.text());
//	    					sb.append(' ');
//	    				}
//	    			}
	    			for (Element para : paras) 
	    			{
	    				//System.out.println(para.text().trim());
	    				if(para.text() != null) 
	    				{
	    					sb.append(para.text());
	    					sb.append(' ');
	    				}
	    			}
	    		}	
	    		doc = null;
	    		int docid = createDocument(title, url);
	    		processDocument(docid, sb.toString().toLowerCase().replaceAll("[^a-zA-Z0-9 ]", ""),
	    	    	    skipnumberlist, 
	    	    	    posinpostinglist, 
	    	    	    lastdocinserted); 
	    		if(docid % 1000 == 0)
	    		{
	    			System.out.println(docid + " completed");
	    			//System.gc();
	    		}
	    		if((docid + 1) % maxDocs == 0 && docid != 0) {
            System.out.println("Storing index for doc id " 
                + (docIdIndex * maxDocs) + " to " + docid);
            String auxilliaryIndexFile = auxilliaryIndexFilePrefix + "_" + docIdIndex;
            ObjectOutputStream writer = new ObjectOutputStream(
                new FileOutputStream(auxilliaryIndexFile));
            writer.writeObject(auxilliaryIndex);
            writer.close();
            auxilliaryIndex.clear();
            auxilliaryIndex = new ArrayList<LinkedHashMap<Integer,Integer>>();
            docIdIndex++;
          }
	    	}
	    }
	    if(auxilliaryIndex.size() > 0) {
	      System.out.println("Storing index for doc id " 
            + (docIdIndex * maxDocs) + " to " + _documents.size());
        String auxilliaryIndexFile = auxilliaryIndexFilePrefix + "_" + docIdIndex;
        ObjectOutputStream writer = new ObjectOutputStream(
            new FileOutputStream(auxilliaryIndexFile));
        writer.writeObject(auxilliaryIndex);
        writer.close();
        auxilliaryIndex.clear();
        auxilliaryIndex = new ArrayList<LinkedHashMap<Integer,Integer>>();
	    }
	    allWords.clear();
  	}
	
	private int createDocument(String title, String url)
	{
		int docid = _documents.size();
		DocumentIndexed doc = new DocumentIndexed(docid);
	    doc.setTitle(title);
	    doc.setUrl(url);	    
	    	    
	    _documents.add(doc);	    
	    ++_numDocs;
	    
	    urlToDocId.put(url, doc._docid);
	    
	    //doc._normfactor = Math.sqrt(normfactor);
	    //doc._numwords = totalwords;
	    return docid;
	}
	
	private void processDocument(int docid, String content,
		    HashMap<String,Integer> skipnumberlist, 
		    HashMap<String,Integer> posinpostinglist, 
		    HashMap<String,Integer> lastdocinserted) 
	{
	    HashMap<String, List<Integer>> tokens = new HashMap<String, List<Integer>>();
	    //double normfactor = 0; 
	    
	    int totalwords = readTermVector(docid, content,
	    	    skipnumberlist, 
	    	    posinpostinglist, 
	    	    lastdocinserted);

	    //updateIndex(tokens, docid);   

	    for(String token: tokens.keySet()) 
	    {
//	    	int x = tokens.get(token).get(0);
//	    	normfactor += x * x;
	    	index.get(token).increaseCorpusDocFreqency();
	    }	    
	}

	
	private int readTermVector(int docid, String content,
		    HashMap<String,Integer> skipnumberlist, 
		    HashMap<String,Integer> posinpostinglist, 
		    HashMap<String,Integer> lastdocinserted
			) 
	{
		HashMap<String, List<Integer>> tokens = new HashMap<String, List<Integer>>();
		
		Scanner s = new Scanner(content);  // Uses white space by default.
	    
		int wordcount = 1;
	    
	    HashMap<String, Integer> lastwordpos = new HashMap<String, Integer>();
	    PorterStemming stemmer = new PorterStemming();
	    LinkedHashMap<Integer,Integer> stringToCountMap = new LinkedHashMap<Integer,Integer>();
	    
	    while (s.hasNext()) 
	    {
	    	totalWordsInCorpus++;
	    	String word = s.next();
	    	
	    	/*
	    	Stemming and Stopword
	    		    	
	    		    	
	    	*/
	    	
	    	String text = Stopwords.removeStopWords(word);
	        if(text != null) {
	          text = stemmer.stem(text);
	          if(text == null) {
	            continue;
	          }
	          //take care of auxilliary structure HERE
	          if(!allWords.containsKey(text)) {
	            int stringId = allWords.size();
	            allWords.put(text, stringId);
	            stringIdToWordMap.put(stringId, text);
	          }
	          int stringId = allWords.get(text);
	          int stringCount = 0;
	          if(stringToCountMap.containsKey(stringId)) {
	            stringCount = stringToCountMap.get(stringId);
	          }
	          stringToCountMap.put(stringId, stringCount+1);
	        }
	        else {
	          continue;
	        }
		    	
	        word = text;
	    	
	    	if(index.containsKey(word))
	    		index.get(word).increaseCorpusTermFreqency();
	    	else
	    	{
	    		
	    		PostingList p = new PostingList();
	    		index.put(word, p);
	    		p.increaseCorpusTermFreqency();
	    	}
	    	
	    	if(tokens.containsKey(word)) 
	    	{
	    		List<Integer> positions = tokens.get(word);
	    		positions.add(wordcount - lastwordpos.get(word));
	    		lastwordpos.put(word, wordcount);
	    		tokens.put(word, positions);
	    	}
	    	else 
	    	{
	    		List<Integer> positions = new ArrayList<Integer>();
	    		positions.add(wordcount);
	    		lastwordpos.put(word, wordcount);
	    		tokens.put(word, positions);
	    	}
	    	wordcount++;
	    }
	    s.close();
	    
	    //sort the map based on value and store
	    //in linked hashmap in that order
	    List<Map.Entry<Integer, Integer>> entries =
	        new ArrayList<Map.Entry<Integer, Integer>>(stringToCountMap.entrySet());
	    Collections.sort(entries, new Comparator<Map.Entry<Integer, Integer>>() {
	      public int compare(Map.Entry<Integer, Integer> a, Map.Entry<Integer, Integer> b){
	        //intentionally comparing b to a to sort in decreasing order
	        return b.getValue().compareTo(a.getValue());
	      }
	    });
	    
	    stringToCountMap.clear();
	    stringToCountMap = new LinkedHashMap<Integer,Integer>();
	    for(Map.Entry<Integer, Integer> entry:entries) {
	      stringToCountMap.put(entry.getKey(),entry.getValue());
	    }
	    auxilliaryIndex.add(stringToCountMap);
	    updateIndex(tokens, docid,
	    	    skipnumberlist, 
	    	    posinpostinglist, 
	    	    lastdocinserted);	    
	    return wordcount-1;
	}
	
	private void updateIndex( HashMap<String,List<Integer>> tokens, int docid,
		    HashMap<String,Integer> skipnumberlist, 
		    HashMap<String,Integer> posinpostinglist, 
		    HashMap<String,Integer> lastdocinserted
			) 
	{
		for(String word : tokens.keySet()) 
		{
			List<Integer> postions = tokens.get(word);
		    PostingList pl = null;
		    if(index.containsKey(word)) 
		    {
		    	pl = index.get(word);
		    }
		    else
		    {
		    	pl = new PostingList();
		        index.put(word, pl);
		    }
		      
		    int lastdocid = 0;
		    if(lastdocinserted.containsKey(word)) 
		    {
		        lastdocid = lastdocinserted.get(word);
		    }
		      
		    pl.add(docid - lastdocid);
		    pl.add(postions.size());
		    pl.add(postions);
		    
		    lastdocinserted.put(word, docid);
		   
		    int lastupdated = 0;
		    
//		    if(skipnumberlist.containsKey(word)) 
//		    {
//		        lastupdated = skipnumberlist.get(word);		        
//		    }    		    
//		    
//	        if(lastupdated%skipSteps == 0) 
//	        {
//	        	lastupdated = 0;
//	        	SkipPointer skippointer = null;
//	        	if(skippointermap.containsKey(word)) 
//	        	{
//	                skippointer = skippointermap.get(word);
//	            }
//	            else 
//	            {
//	                skippointer = new SkipPointer();
//	                skippointermap.put(word, skippointer);
//	            }
//	        	skippointer.addPointer(docid, pl.getCount());
//	        }
//	        
//	        skipnumberlist.put(word, lastupdated + 1);
		    
		    posinpostinglist.put(word, pl.getCount());      
		}
		return;
	}	

	private ArrayList<Integer> next(String token, int docid)
	{
		if(token.contains(" "))
		{
			Query query = new Query(token);
			query.processQuery();
			return nextPhraseDoc(query,docid);
		}
		PostingList pl = index.get(token);
	    if(pl == null)
	    {
	    	return null;
	    }
	    
	    int startpos = 0;
	    ArrayList<Integer> posting = new ArrayList<Integer>(); 
	    startpos = getPosting(pl, startpos, posting);
	    int offset = posting.get(0);
	    
	    if(offset > docid)
	    {
	    	return posting;
	    }
//	    SkipPointer.Pair pair = null;
//	    try {
//	    	 pair = pl.getSp().search(docid);
//		} catch (Exception e) {
//			// TODO: handle exception]
//			e.printStackTrace();
//		}
	    
	    	
//	   	if(pair != null)
//	   	{
//	   		startpos = (int)pair.getPos();
//	    	offset = pair.getDocid();
//    		if(offset == -1 || offset > docid)
//	   		{
//	   			posting.clear();
//	    		startpos = getPosting(pl ,startpos, posting);
//    			offset = posting.get(0);
//	    	}
//		}
	    
//	    
	    if(offset > docid)
	    {
	    	return posting;
	    }
	    
	    while(startpos < pl.getCount() && startpos != -2) 
	    {
	    	posting.clear();
	   	  	startpos = getPosting(pl, startpos, posting);
	    	
	   	  	if(startpos == -2)
	    	{
	    		break;
	   	  	}
	   	  	offset += posting.get(0);
	   	  	posting.set(0, offset);
	   	  	if(offset > docid)
	   	  	{
	   	  		break;
	   	  	}
	    }	    
	    
	    if(startpos >= pl.getCount() || startpos == -2) 
	    {
	        return null;
	    }
	    if(offset >= _documents.size())
	    {
	        return null;
	    }
	    return posting;
	}
	
	private int getPosting(PostingList pl, int startpos, ArrayList<Integer> posting)
	{
		 if(startpos == -1)
		 {
			 return -2;
		 }
	     int pair[] = pl.get(startpos);
		 int docid = pair[0];
		 pair = pl.get(pair[1]);
		 int count = pair[0];
		    
		 posting.add(docid);
		 posting.add(count);
		    
		 int position = 0;
		 for(int i=0;i<count;i++) 
		 {
			 pair = pl.get(pair[1]);
			 position += pair[0];
			 posting.add(position);
		 }
		 return pair[1];
	}

	private ArrayList<Integer> nextPhraseDoc(Query query, int docid)
	{
		ArrayList<ArrayList<Integer>> postinglist = new ArrayList<ArrayList<Integer>>();
		int docids[] = new int[query._tokens.size()];
		int maxdocid = -1;
		boolean flag = true;
		for(int i = 0; i < query._tokens.size(); i++)
		{
			String token = query._tokens.get(i);
			ArrayList<Integer> posting = next(token, maxdocid > docid ? maxdocid - 1 : docid);
			if(posting == null)
			{
				return null;
			}
			
			docids[i] = posting.get(0);
			
			try {
				postinglist.add( new ArrayList<Integer>(posting.subList(2, posting.size())));				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
				
			
			
			if(i > 0 && docids[i] != docids[i-1])
			{
				   flag = false;
			}
			if(maxdocid < docids[i])
			{
				   maxdocid = docids[i];
			}
		}
		if(!flag)
		{
			return nextPhraseDoc(query, maxdocid - 1);
		}
		int size = postinglist.size(); 
		for(int i = 1; i < size; i++)
		{
			decreasePosting(postinglist.get(i), i);
		}
		
		ArrayList<Integer> phraseposting = postinglist.get(0);
		for(int i = 1; i < size; i++)
		{
			phraseposting.retainAll(postinglist.get(i));
		}
		if(phraseposting.isEmpty())
		{
			return nextPhraseDoc(query, maxdocid);
		}
		
		phraseposting.add(0, phraseposting.size());
		phraseposting.add(0, docids[0]);
		return phraseposting;
	}
	
	private void decreasePosting(ArrayList<Integer> list, int number)
	{
		for (int i = 0; i < list.size(); i++)
		{
		       int item = list.get(i);
		       list.set(i,item - number);
		}
	}
}
