HW3
===
SearchEngine
============
1. URL to DocID mapping
2. Page Rank
    Store files comlun-wise (inlinks).
    Create an in-memeory map: key-docid value-number of outlinks
    file to store page ranks
    
3.  Num-Views ///Handle case for no logs for a URL (the file should have some entry for that url) 
    Go through logs find num-views and normalize
    Store in a file
    
Return results with page rank and numviews along with rank    
    
4.  PSR
    While creating index store snippet in DocumnetIndexed class ( ask prof about using snippet and how big or small it can/should be)
    Snippet is a map : key-word value-count in snippet
    
    Get top k docs for a query
    Calculate PSR for terms in snippet of the top k docs (renormalize these probabilities)
    return top m PSR terms and app
    
5. Bhattacharya 
    Write java class
    test the shell script privided in assignment
    
4.

HW2
  Generate array of all the words
  Generate 1 file per posting list (Merging of postinglist for each doc may be required)
  Cache API for posting list
  
  NEED TO CHECK IF CONSTURUCTION WORKS FOR THE MEMORY LIMIT OF 512
  IF IT DOESNOT WORK
    for each doc create postinglist per word 
    for each word mrege the postinglists accross all docs
  
  STEMMING (Ask prof) , STRING NORMALIZED, 
