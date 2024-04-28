import java.io.*;
import java.util.HashMap;

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
            FileOutputStream fos = new FileOutputStream(f);
            while(true){
                byte[] buff = fis.readNBytes(2048);
                // + lz77, huff, etc...
                
                break;
            }
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
            FileOutputStream fos = new FileOutputStream(f);
            fis.close();
            fos.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }
}

class LZ77{

}
class HuffmanTree {
    public HuffmanNode main;
    private HashMap<Byte, String> data = new HashMap<Byte, String>();
    private void build(){                               // build a tree ( get codes for each element to hashmap )
        
    }
    public void add(byte character, int freq){  // add to tree
    }

    public String getCode(byte a){               // return code of a byte
        return data.get(a);
    }
}
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
}
