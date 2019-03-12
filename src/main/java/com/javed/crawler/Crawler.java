package com.javed.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Crawler {

    private HashSet<String> links;
    private static long count;
    private static FileWriter metadataWriter;

    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
    private static final String REFERRER = "http://www.google.com";
    private static final String OUTPUT_DIRECTORY = "output/";
    private static final int MAX_DEPTH = 2;

    public Crawler() throws IOException {
        links = new HashSet<>();
        count = 0;
        metadataWriter = new FileWriter(OUTPUT_DIRECTORY+"metadata.txt", true);
    }

    public void getPageLinks(String URL, int depth) throws IOException {
        if (!links.contains(URL) && (depth < MAX_DEPTH)) {
            try {
                Document document = getDocument(URL);
                String docText = document.body().text();
                logDocumenttext(docText, URL);
                
                Elements otherLinks = document.select("a[href^=\"https://en.wikipedia.org/\"]");			// will get all href which starts only with "https://en.wikipedia.org"
                //Elements otherLinks = document.select("a[href^=\"https://en.wikipedia.org/\"], a[href^=/]");	// will get all href which starts with "https://en.wikipedia.org" and points to any of the context path of the target URL
                //Elements otherLinks = document.select("a");			// Will get all href mentioned in <a> tags.
                depth++;
                for (Element page : otherLinks) {
                    if (links.add(URL)) {
                        System.out.println(URL);
                        getPageLinks(page.attr("abs:href"),depth);
                    }
                }
            } catch (IOException e) {
                System.err.println(e.getMessage()+ " : "+URL);
            }
        }
        metadataWriter.close();
    }

    private Document getDocument(String URL) throws IOException {
        return Jsoup.connect(URL)
                .userAgent(USER_AGENT)
                .referrer(REFERRER)
                .get();
    }

    public void logDocumenttext(String text, String url){
    	count++;
    	String fName = OUTPUT_DIRECTORY+"File-"+count+".txt";
    	try {
    		FileWriter writer = new FileWriter(fName);
        	writer.write(text);
        	writer.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
    	
    	try{
    		if(count == 1)
    			metadataWriter.flush();
    		metadataWriter.write(fName.replace(OUTPUT_DIRECTORY,"")+" : "+url+"\n");
    	}catch(Exception e){
    		System.err.println(e.getMessage());
    	}    	
    }

    public static void main(String[] args) throws IOException {
        Crawler bwc = new Crawler();
        bwc.getPageLinks("https://en.wikipedia.org/wiki/Main_Page",0);
    }
}
