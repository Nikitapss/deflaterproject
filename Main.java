import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Deflate coder = new Deflate();

        if(args.length != 2){System.out.println("Main code/decode filename"); return;}
        switch (args[0]) {
            case "code":
                coder.code(args[1]);
                break;
            case "decode":
                coder.decode(args[1]);
                break;
            default:
                System.out.println("Main code/decode filename");
                break;
        }
    }
}
class Deflate {
    public void code(String filename){
        File f = new File(filename);
        
        try{
            FileInputStream fis = new FileInputStream(f);
            FileOutputStream fos = new FileOutputStream(f+".defl");
            byte[] buff = fis.readNBytes(32768);
            LZ77 lz77coder = new LZ77(buff);
            String[] lz = lz77coder.lz77code();
                // + lz77, huff, etc...
            
            fis.close();
            fos.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
        
        
    }

    public void decode(String filename){
        File f = new File(filename);
        try{
            FileInputStream fis = new FileInputStream(f);
            FileOutputStream fos = new FileOutputStream(f+".defl");
            fis.close();
            fos.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }
}

class LZ77{
    public byte[] buff;
    public LZ77(byte[] buff){
        this.buff = buff;
    }
    public String[] lz77code(){
        //List<String> ans = new ArrayList<>();
        //String currentMatch = "";
        //int matchIndex = 0, tempIndex = 0;
        //StringBuffer searchBuffer = new StringBuffer();

        Vector<String> ans = new Vector<String>();
        for(byte x : buff){
            ans.add(String.valueOf((char)x));
        } 
        for(int i = 255; i >=3; i--){
            System.out.println(i);
            // List<String> ans = new ArrayList<>();
            Vector<String> ans2 = new Vector<String>();
            int matchIndex = 0, tempIndex = 0;
            String currentMatch = "";
            StringBuffer searchBuffer = new StringBuffer();
            //for (int indx = 0; indx < ans.size(); indx++) {
            for(String indx : ans){
                    // поиск
                //if(!ans.get(indx).startsWith("~")){
                //if(!indx.startsWith("~")){
                //byte nextChar = (byte)ans.get(indx).charAt(0);

                
                //byte nextChar = (byte)indx.charAt(0);
                String nextChar = indx;
                //tempIndex = searchBuffer.indexOf(currentMatch + (char)nextChar);
                tempIndex = searchBuffer.indexOf(currentMatch + nextChar);
                if (tempIndex != -1) {
                    //currentMatch += (char)nextChar;
                    currentMatch += nextChar;
                    matchIndex = tempIndex;
                } else {
                    
                    String codedString = "~~~~~~~~~~~~"+matchIndex+"~"+currentMatch.length();
                    for(int q = 1; q <= 12; q++){
                        if(codedString.length() > 15){
                            codedString = codedString.substring(1);
                        }
                    }

                    //String concat = currentMatch + (char)nextChar;
                    String concat = currentMatch + nextChar;
                    if (currentMatch.length() == i) { // если длина совпадения больше окна
                        ans2.add(codedString);
                        //ans2.add(String.valueOf((char)nextChar));
                        ans2.add(String.valueOf(nextChar));
                        searchBuffer.append(concat); // append to the search buffer
                        currentMatch = "";
                        matchIndex = 0;
                    } else {
                    // otherwise, output chars one at a time from
                        // currentMatch until we find a new match or
                    // run out of chars
                        currentMatch = concat; matchIndex = -1;
                        while (currentMatch.length() > 1 && matchIndex == -1) {
                            System.out.println(currentMatch);
                            if(!currentMatch.startsWith("~")){
                                ans2.add(String.valueOf(currentMatch.charAt(0)));
                                searchBuffer.append(currentMatch.charAt(0));
                                currentMatch = currentMatch.substring(1, currentMatch.length());
                                matchIndex = searchBuffer.indexOf(currentMatch);
                            }else{
                                ans2.add(String.valueOf(currentMatch));
                                //searchBuffer.append(currentMatch.charAt(0));
                                currentMatch = currentMatch.substring(15, currentMatch.length());
                                matchIndex = searchBuffer.indexOf(currentMatch);
                            }
                        }
                    }
                }
            //}else{
                //ans2.add(ans.get(indx));
                //ans2.add(indx + "!");
            //}
        }
            if (matchIndex != -1) {
                String codedString = "~~~~~~~~~~~~"+matchIndex+"~"+currentMatch.length();
                    for(int q = 1; q <= 12; q++){
                        if(codedString.length() > 15){
                            codedString = codedString.substring(1);
                        }
                    }
                if(matchIndex!= 0 && currentMatch.length() != 0) ans2.add(codedString);
            }

            ans = new Vector<>(ans2);
            ans2.clear();
            System.out.println(ans);
        }




        Vector<String> coded= new Vector<String>();
        String[] abc = coded.toArray(new String[coded.size()]);
        return abc;
        
    }
    public void lz77decode(){

    }
    
}
class HuffmanTree {
    //public HuffmanNode main;
    private HashMap<Byte, String> data = new HashMap<Byte, String>();
    private void build(){                               // build a tree ( get codes for each element to hashmap )
        
    }
    public void add(byte character, int freq){  // add to tree
    }

    public String getCode(byte a){               // return code of a byte
        return data.get(a);
    }
    public void staticHuffman(){

    }
}
/* 
class HuffmanNode{               
    char a;
    int freq;
    HuffmanNode left;
    HuffmanNode right;
    public HuffmanNode(char a, int freq, HuffmanNode left, HuffmanNode right){
        this.a = a;
        this.freq = freq;
        this.left = left;
        this.right = right;
    }
    public HuffmanNode(char a, int freq){
        this.a = a;
        this.freq = freq;
    }
}*/
