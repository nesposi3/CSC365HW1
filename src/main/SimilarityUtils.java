package main;
import java.math.*;
import java.util.HashMap;
import java.util.Map;

import javafx.css.Match;
import main.HashTable.*;
public class SimilarityUtils {
    //The hashtable for the entered url is an n-dimensional vector that we apply tf-idf to, and cosine simiarity
    // We must go through each word on each document and apply these functions

    /**
     * TF Algorithm gives us word frequency in a document
     * @param key The word to check frequency, String
     * @param doc The document with which to check, Represented by a HashTable
     * @return The frequency of key
     */
    private static double TF(String key,HashTable doc){
        HashTable.Node n = doc.get(key);
        if(n!=null){
            double top = n.wordFrequency;
            double bottom = doc.totalWordCount();
            return top / (bottom);
        }else{
            return 0;
        }
    }

    /**
     * IDF Algorithm helps determine how rare a word is in a corpus, with more rarity yielding a higher value
     * @param key The word to check, a String
     * @param docList The corpus, represented as an array of HashTables
     * @return
     */
    private static double IDF(String key, HashTable[] docList){
        int numWithKey = 0;
        for (int i = 0; i <docList.length ; i++) {
            if(docList[i].contains(key)){
                numWithKey++;
            }
        }
        if(numWithKey>0){
            double quotient = ((double) docList.length )/ (numWithKey);
            return Math.log(quotient);
        }else{
            return 0;
        }

    }

    /**
     * This method produces the most similar document of a corpus to the entered document
     * Uses TF and IDF
     * @param enteredDoc The Document that is being compared to, a  HashTable
     * @param cachedDocs The Corpus with which to check
     * @return A string with the title of the most similar webpage
     */
    public static String findMostSimilar(HashTable enteredDoc, HashTable[] cachedDocs){
        //This hashmap stores tf-idf vectors for each word in the entered document
        HashMap<String, Double[]> cachedDocumentVectors = new HashMap<String, Double[]>();
        //This hashmap represents the tfidf vector of the query
        HashMap<String,Double> queryVector = new HashMap<>();

        enteredDoc.forEach((Node n)-> {
            Double queryTF = TF(n.key,enteredDoc);
            Double queryIDF = IDF(n.key,cachedDocs);
            Double queryTFIDF = queryTF * queryIDF;
            queryVector.put(n.key,queryTFIDF);
            Double[] wordVector = new Double[enteredDoc.size()];
            for (int i = 0; i <cachedDocs.length ; i++) {
                double tf = TF(n.key,cachedDocs[i]);
                 double idf = IDF(n.key,cachedDocs);
                wordVector[i] = tf * idf;
            }
            cachedDocumentVectors.put(n.key,wordVector);
        });
        //Cosine similarity for each document
        double maxSimilarity =0;
        String closestSite = "";
        for (int i = 0; i < cachedDocs.length; i++) {
            HashTable doc = cachedDocs[i];
            double top = 0;
            double queryBottom = 0;
            double cachedBottom = 0;
            for(Map.Entry<String,Double> entry:queryVector.entrySet()){
                double tfidfQuery = entry.getValue();
                double cachedTFIDF = cachedDocumentVectors.get(entry.getKey())[i];
                queryBottom += tfidfQuery * (tfidfQuery);
                cachedBottom += cachedTFIDF * cachedTFIDF;
                double dotProductPart = tfidfQuery * cachedTFIDF;
                top += dotProductPart;
            }
            double bottom = Math.sqrt(queryBottom) + Math.sqrt(cachedBottom);
            double similarity = top/bottom;
            if(similarity>maxSimilarity){
                maxSimilarity = similarity;
                closestSite = doc.getName();
            }
        }
        return closestSite;

    }
}
