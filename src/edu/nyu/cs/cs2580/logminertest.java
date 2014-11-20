package edu.nyu.cs.cs2580;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

public class logminertest {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static String decode(String s) throws UnsupportedEncodingException
	{
		return URLDecoder.decode(s, "UTF-8");		
	}
	
	public static void loadfile() throws IOException
	{
		LinkDocIDMapGenerator.generateMap("data/wiki/");
		
		Map<String, Integer> map = LinkDocIDMapGenerator.get();
		
		ArrayList<Integer> numviews = null;
		File file = new File("data/index/errorlogs.txt");
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		
		BufferedWriter bw = new BufferedWriter(fw);
	
		//Map<String, Integer> map = LinkDocIDMapGenerator.get();
		numviews = new ArrayList<Integer>(map.size());
		for (int i = 0; i < map.size(); i++) {
			  numviews.add(0);
		}
		
		File f = new File("data/log/20140601-160000.log");
		Scanner sc = new Scanner(f);
		while(sc.hasNext())
		{
			String line[] = sc.nextLine().split(" ");
			String link  = null;
			try{
					link = decode(line[1]).trim();			
			}
			catch(Exception e)
			{
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				
				bw.write(Arrays.toString(line));
				bw.newLine();
				bw.write("==========Decode error trace===========");
				bw.newLine();
				bw.write(errors.toString());
				bw.newLine();
				bw.write("=====================");
				bw.newLine();
				bw.newLine();
				
			}
			if(link == null)
				continue;
			
			int numview = 0;
			try
			{
				numview = Integer.parseInt(line[2]);
				if(map.containsKey(link))
				{
					int docid = map.get(link);
					numviews.set(docid, numviews.get(docid) + numview);
				}
			}
			catch(Exception e)
			{
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				bw.write(Arrays.toString(line));
				bw.newLine();
				bw.write("==========parse error trace===========");
				bw.newLine();
				bw.write(errors.toString());
				bw.newLine();
				bw.write("=====================");
				bw.newLine();
				bw.newLine();
			}
			
		}
		bw.close();
		sc.close();
		for(int i = 0; i < map.size(); i++)
		{
			System.out.println(numviews.get(i));
		}	
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		loadfile();
	}

}
