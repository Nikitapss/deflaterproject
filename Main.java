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

        Deflate coder = new Deflate(data[]);

        /*try{
            DataOutputStream a = new DataOutputStream(new FileOutputStream("a.dat"));
            byte b = 1;
            a.writeByte(b);
            a.writeByte(3);
            a.close();
        }catch (Exception e){
            System.err.println(e);
        }*/
    }
}
class Deflate {
    public void code(byte[] data){
        LZ77Code lz77 = new LZ77Code();
        HuffmanTree ht = new HuffmanTree(data);
    }
}
class LZ77Code {
}
class HuffmanTree {
    private HuffmanNode main;
    private HashMap<Byte, Integer> map = new HashMap<Byte, Integer>();
    public byte[] data;
    public HuffmanTree(byte[] data){
        this.data = data;
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
    public int read(){
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
    public void read(int i){
        // block size
    }
    public void close() throws IOException{
        fis.close();
    }
}
class BetterOutputStream extends OutputStream {
    private FileOutputStream fos;
    public BetterOutputStream(String path) throws FileNotFoundException{
        this.fos = new FileOutputStream(path);
    }
    public void write(int i){

    }
    public void write(int[] i){

    }
    public void close() throws IOException{
        fos.close();
    }
}