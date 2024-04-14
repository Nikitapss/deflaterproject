import java.io.*;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {

        try{
            BetterInputStream bis = new BetterInputStream("test.txt");
            System.out.println(bis.read());
            bis.close();
        }catch (Exception e){
            System.err.println(e);
        }

        Deflate coder = new Deflate();

        // hz, mnogo methods
    }
}
class Deflate {
    public void code(byte[] data){
        HuffmanTree ht = new HuffmanTree();
    }
    // hz, mnogo methods
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
    public HuffmanNode(char a, int freq){
        this.a = a;
        this.freq = freq;
        this.left = null;
        this.right = null;
    }
}
class BetterInputStream extends InputStream{
    private FileInputStream fis;
    public BetterInputStream(String path) throws FileNotFoundException{
        this.fis = new FileInputStream(path);
    }
    public int read(){    // basic read
        int a = 0;

        try{
            while(a != -1){
                return 2;
            }
        } catch(Exception e){
            System.err.println(e);
        }
        return a;
    }
    public void read(int i){  // read + block size
    }
    public void close() throws IOException{ // close
        fis.close();
    }
}
class BetterOutputStream extends OutputStream {
    private FileOutputStream fos;
    public BetterOutputStream(String path) throws FileNotFoundException{
        this.fos = new FileOutputStream(path);
    }
    public void write(int i){ // write single int

    }
    public void write(byte i){ //write a byte

    }
    public void write(byte[] i){ // write a byte array

    }
    public void close() throws IOException{ // close
        fos.close();
    }
}