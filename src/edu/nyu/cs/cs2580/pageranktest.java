package edu.nyu.cs.cs2580;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

public class pageranktest {

	/**
	 * @param args
	 */
	public static final String column_path = "./docs/docid_";
	public static final String column_dirpath = "./docs/";
	public static final String pagerank_dirpath = "./pagerank/";
	private Map<Integer, Integer> outlinkcount = new HashMap<Integer, Integer>();
	public LinkedList<Double> pagerank = new LinkedList<Double>();
	
	public static void deleteDirectory(File file)
	    	throws IOException{
	 
	    	if(file.isDirectory()){
	 
	    		//directory is empty, then delete it
	    		if(file.list().length==0){
	 
	    		   file.delete();
	    		   //System.out.println("Directory is deleted : " + file.getAbsolutePath());
	 
	    		}else{
	 
	    		   //list all the directory contents
	        	   String files[] = file.list();
	 
	        	   for (String temp : files) {
	        	      //construct the file structure
	        	      File fileDelete = new File(file, temp);
	 
	        	      //recursive delete
	        	     deleteDirectory(fileDelete);
	        	   }
	 
	        	   //check the directory again, if empty then delete it
	        	   if(file.list().length==0){
	           	     file.delete();
	        	     //System.out.println("Directory is deleted : " + file.getAbsolutePath());
	        	   }
	    		}
	 
	    	}else{
	    		//if file, then delete it
	    		file.delete();
	    		//System.out.println("File is deleted : " + file.getAbsolutePath());
	    	}
	    }

	public void makeColumns(int docid, int links[]) throws IOException
	{
		pagerank.add(1.0);
		for(int i = 0; i < links.length; i++)
		{	
			
			int linkeddoc = links[i];
			if(outlinkcount.containsKey(docid))
			{
				outlinkcount.put(docid, outlinkcount.get(docid) + 1);
			}
			else
			{
				outlinkcount.put(docid, 1);
			}
			File file = new File(column_path + linkeddoc);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(Integer.toString(docid));
			bw.newLine();
			bw.close();				
		}
	}
	
	public void initDirectory()
	{
			File directory = new File(column_dirpath);			
			if(!directory.exists()){
				 
		           System.out.println("Directory does not exist.");
		           directory.mkdir();
		    }
			else{	 
				try{
					deleteDirectory(directory);
					directory.mkdir();
				}
				catch(IOException e)
				{
					e.printStackTrace();
		            System.exit(0);
		        }
		    }
	}
	
	@SuppressWarnings("unchecked")
	public void calculatePageRank(double lambda, int noofdocs, int iter) throws FileNotFoundException
	{
		double gfactor = (1.0-lambda)/noofdocs;
		for(int x = 0; x < iter ; x++)
		{	
			LinkedList<Double> newpagerank = new LinkedList<Double>();
			for(int i = 0; i < noofdocs; i++)
			{
				double rank = 0.0;
				File column = new File(column_path + i);
				if(!column.exists())
				{
					rank = 1.0 - lambda;
				}
				else
				{
					Scanner sc = new Scanner(column);
					Set<Integer> col = new HashSet<Integer>();
					while(sc.hasNext())
					{
						int docid = Integer.parseInt(sc.nextLine());
						col.add(docid);
					}
					for(int j = 0; j < noofdocs; j++)
					{
						if(!col.contains(j))
						{
							rank += (pagerank.get(j) * gfactor);	
						}
						else
						{
							rank += (pagerank.get(j) * ((lambda / outlinkcount.get(j) + gfactor)));					
						}
					}
					sc.close();
				}			
				newpagerank.add(rank);
			}
			pagerank = (LinkedList<Double>) newpagerank.clone();
		}
	}
	
	public void storePageRank() throws FileNotFoundException, IOException
	{
		File f = new File(pagerank_dirpath);
		if(!f.isDirectory())
			f.mkdir();
		System.out.println("Store pagerank to: " + pagerank_dirpath);
		String prFile = pagerank_dirpath+"corpus.pr";
		ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(prFile));
		writer.writeObject(this.pagerank);
		writer.close();
		System.out.println("Storing complete");
	}
	
	@SuppressWarnings("unchecked")
	public void retrievePageRank() throws FileNotFoundException, IOException, ClassNotFoundException
	{
		String prFile = pagerank_dirpath+"corpus.pr";
		ObjectInputStream reader = new ObjectInputStream(new FileInputStream(prFile));
		pagerank = (LinkedList<Double>) reader.readObject();
		reader.close();
	}
	
	@SuppressWarnings("unchecked")
	public static void makeColumns(int links[][]) throws IOException
	{
		File directory = new File(column_dirpath);
		
		Map<Integer, Integer> outlinkcount = new HashMap<Integer, Integer>();
		
		if(!directory.exists()){
			 
	           System.out.println("Directory does not exist.");
	           directory.mkdir();
	    }
		else{	 
			try{
				deleteDirectory(directory);
				directory.mkdir();
			}
			catch(IOException e)
			{
				e.printStackTrace();
	            System.exit(0);
	        }
	    }
		
		double lambda = 0.5;
		double gfactor = (1.0-lambda)/(links.length);
		LinkedList<Double> pagerank = new LinkedList<Double>();
		
		for(int i = 0; i < links.length ; i ++)
		{
			int docid = links[i][0];
			pagerank.add(1.0);
			for(int j = 1; j < links[i].length; j++)
			{
				int linkeddoc = links[i][j];
				if(outlinkcount.containsKey(docid))
				{
					outlinkcount.put(docid, outlinkcount.get(docid) + 1);
				}
				else
				{
					outlinkcount.put(docid, 1);
				}
				File file = new File(column_path + linkeddoc);
				if (!file.exists()) {
					file.createNewFile();
				}
				FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(Integer.toString(docid));
				bw.newLine();
				bw.close();				
			}
		}
		for(int x = 0; x < 2 ; x++)
		{	
			LinkedList<Double> newpagerank = new LinkedList<Double>();
			for(int i = 0; i < links.length; i++)
			{
				double rank = 0.0;
				File column = new File(column_path + i);
				if(!column.exists())
				{
					rank = 1.0 - lambda;
				}
				else
				{
					Scanner sc = new Scanner(column);
					Set<Integer> col = new HashSet<Integer>();
					while(sc.hasNext())
					{
						int docid = Integer.parseInt(sc.nextLine());
						col.add(docid);
						//rank += (pagerank.get(docid) * ((lambda / outlinkcount.get(docid) + gfactor)));
					}
					for(int j = 0; j < links.length; j++)
					{
						if(!col.contains(j))
						{
							rank += (pagerank.get(j) * gfactor);	
						}
						else
						{
							rank += (pagerank.get(j) * ((lambda / outlinkcount.get(j) + gfactor)));					
						}
					}
					sc.close();
				}			
				newpagerank.add(rank);
			}
			pagerank = (LinkedList<Double>) newpagerank.clone();
			System.out.println(pagerank);
		}
		
		
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		//makeColumns(links);
//		int links2[][] = {{2,3},
//				{0},
//				{0},
//				{0,1}};
//		
//		pageranktest test= new pageranktest();
//		test.initDirectory();
//		for(int i = 0; i < links2.length; i++)
//		{
//			test.makeColumns(i, links2[i]);
//		}
//		test.calculatePageRank(0.5, links2.length, 2);
//		test.storePageRank();
//		test.pagerank=null;
//		test.retrievePageRank();
//		System.out.println(test.pagerank);
		
//		String prFile = "data/index/"+"corpus.pr";
//		ObjectInputStream reader = new ObjectInputStream(new FileInputStream(prFile));
//		ArrayList<Double>pagerank = (ArrayList<Double>) reader.readObject();
//		reader.close();
//		System.out.println(pagerank.size());
//		for( int i = 0; i < 10; i++)
//		{
//			System.out.println(pagerank.get(i));
//		}
		
		Pattern p = Pattern.compile("0; url=+(.+).*");
		Matcher m = p.matcher("<p>0; url=Symphony_No._5_(Beethoven).html<p>");
		p = Pattern.compile("<p>+(.+).* <p>");
		if (m.find()) {
		    System.out.println(m.group(1));
		}
		checkRedirectLink("Symphony_No._5_(Beethoven)");
	}

	
	/*
	 1	0	0	0.5	0.5
	 2	1	0	0	0
	 3	1	0	0	0
	 4  0.5	0.5	0	0
	 */
	public static String checkRedirectLink(String outlinktitle) throws IOException
	{
		Pattern p = Pattern.compile("0; url=+(.+).*");
		Matcher m;
		final File corpusDirectory = new File("data/wiki/");
		for (final File fileEntry : corpusDirectory.listFiles()) {
		      if (!fileEntry.isDirectory()) {

		        org.jsoup.nodes.Document doc = Jsoup.parse(fileEntry, "UTF-8");
		        Elements metalinks = doc.select("meta[http-equiv=\"refresh\"]");
		        if(metalinks.size() != 0)
		        {
		        	m = p.matcher(metalinks.attr("content"));
		        	if (m.find()) {
		    		    System.out.println(m.group(1));
		    		}
		        }
		      }
		}
		
		return null;
	}
	
	static Pattern p = Pattern.compile("0; url=+(.+).*");
	static Matcher m;
	
	public static String getRedirectLink(String outlinktitle) throws IOException
	{
		File f = new File("data/wiki/");
		org.jsoup.nodes.Document doc = Jsoup.parse(f, "UTF-8");
		Elements metalinks = doc.select("meta[http-equiv=\"refresh\"]");
        if(metalinks.size() != 0)
        {
        	m = p.matcher(metalinks.attr("content"));
        	if (m.find()) {
    		    return getRedirectLink(m.group(1));
        	}
        	else
        		return null;
        }
        else
        {
        	return outlinktitle;
        }       	
	}

}

