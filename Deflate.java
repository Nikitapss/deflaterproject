import java.io.*;

public class Deflate {
    public static void main(String[] args) {
        try{
            BetterInputStream bis = new BetterInputStream("test.txt");
            System.out.println(bis.read());
            bis.close();
        }catch (Exception e){
        }
    }
}

class LZ77Code {
}
class HoffmanCode {
}
class HoffmanNode{
    char a;
    HoffmanNode left;
    HoffmanNode right;
    public HoffmanNode(char a){
        this.a = a;
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
            a = fis.read();
        } catch(Exception e){

        }
        return a;
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
    public void close() throws IOException{
        fos.close();
    }
}