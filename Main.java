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
            PrintWriter fos = new PrintWriter(new FileOutputStream(f+".defl"));

            byte[] buff = fis.readNBytes(32768);
            LZ77 lz77coder = new LZ77(buff);
            Vector<String> lz = lz77coder.lz77code();
                // + lz77, huff, etc...
            fos.println(lz);
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
    public Vector<String> lz77code(){                     
        Vector<String> ans = new Vector<String>();      
        for(byte x : buff){                         // moving byte[] to vector
            ans.add(String.valueOf((char)x));
        }

        for(int i = 255; i >=3; i--){               // max match length
            Vector<String> ans2 = new Vector<String>();
            int matchIndex = 0, tempIndex = 0;
            String currentMatch = "";
            StringBuffer searchBuffer = new StringBuffer();
            for(String indx : ans){                   // поиск
                tempIndex = searchBuffer.indexOf(currentMatch + indx);
                if (tempIndex != -1) {
                    currentMatch += indx;
                    matchIndex = tempIndex;
                } else {
                    String codedString = "~~~~~~~~~~~~"+matchIndex+"~"+currentMatch.length();     // кодировка строки под размер ( костыль )
                    for(int q = 1; q <= 12; q++){
                        if(codedString.length() > 15){
                            codedString = codedString.substring(1);
                        }
                    }

                    String concat = currentMatch + indx;
                    if (currentMatch.length() == i) {                               // если длина совпадения наконец попала в размер окна
                        ans2.add(codedString);
                        ans2.add(String.valueOf(indx));
                        searchBuffer.append(concat);                                            // append to the search buffer
                        currentMatch = "";
                        matchIndex = 0;
                    } else {                                                          // разбор буфера ( костыль )
                        currentMatch = concat; matchIndex = -1;
                        while (currentMatch.length() > 1 && matchIndex == -1) {
                            if(!currentMatch.startsWith("~")){
                                ans2.add(String.valueOf(currentMatch.charAt(0)));
                                searchBuffer.append(currentMatch.charAt(0));
                                currentMatch = currentMatch.substring(1, currentMatch.length());
                                matchIndex = searchBuffer.indexOf(currentMatch);
                            }else{
                                ans2.add(String.valueOf(currentMatch));
                                currentMatch = currentMatch.substring(15, currentMatch.length());
                                matchIndex = searchBuffer.indexOf(currentMatch);
                            }
                        }
                    }
                }
            }
            if (matchIndex != -1) {                                                                 // если застряло в конце
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
        }
        return ans;
        
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
