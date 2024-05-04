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
        LZ77 lz77coder = new LZ77();
        Huffman huff = new Huffman();
        try{
            FileInputStream fis = new FileInputStream(f);
            FileOutputStream fos = new FileOutputStream(f+".dat");
            //int BLOCK_SIZE = 35000;
            //byte[] buff = new byte[BLOCK_SIZE]; // нельзя менять тк лимит на сдвиг в хаффмане
            while (true) {
                byte[] buff = fis.readNBytes(35000);
                if(buff.length == 0){
                    break;
                }
                Vector<String> lz = lz77coder.lz77code(buff);
                List<Byte> done = huff.code(lz);
                for(byte x : done){
                    fos.write(x);
                }
            }
            fis.close();
            fos.close();
        }
        catch(Exception e){
            System.out.println("In/Out file error"+ e);
        }
    }

    public void decode(String filename){
        File f = new File(filename);
        LZ77 lz77coder = new LZ77();
        Huffman huff = new Huffman();
        try{
            FileInputStream fis = new FileInputStream(f+".dat");
            FileOutputStream fos = new FileOutputStream("temp.dat");
            ArrayList<String> data = huff.decode(fis);
            fis.close();
            fos.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }
}

class LZ77{
    public Vector<String> lz77code(byte[] buff){                     
        Vector<String> ans = new Vector<String>();      
        for(byte x : buff){                         // moving byte[] to vector
            ans.add(String.valueOf((char)x));
        }
        Vector<String> ans2 = new Vector<String>();
        int matchIndex = 0, tempIndex = 0;
        String currentMatch = "";
        StringBuffer searchBuffer = new StringBuffer();
        for(String indx : ans){                   // поиск           // fix later
            tempIndex = searchBuffer.indexOf(currentMatch + indx);
            if (tempIndex != -1) {
                currentMatch += indx;
                matchIndex = tempIndex;
            } else {
                String codedString = matchIndex+"~"+currentMatch.length();     // кодировка строки под размер ( костыль )
                for(int q = 1; q <= 12; q++){
                    if(codedString.length() > 15){
                        codedString = codedString.substring(1);
                    }
                }
                String concat = currentMatch + indx;
                if (currentMatch.length() >= 3) {                               // если длина совпадения >3
                    ans2.add(codedString);
                    ans2.add(String.valueOf(indx));
                    searchBuffer.append(concat);                                            // append to the search buffer
                    currentMatch = "";
                    matchIndex = 0;
                } else {                                                          // разбор буфера ( костыль )
                    currentMatch = concat; matchIndex = -1;
                    while (currentMatch.length() > 1 && matchIndex == -1) {
                        ans2.add(String.valueOf(currentMatch.charAt(0)));
                        searchBuffer.append(currentMatch.charAt(0));
                        currentMatch = currentMatch.substring(1, currentMatch.length());
                        matchIndex = searchBuffer.indexOf(currentMatch); 
                    }
                }
            }
        }
        while (currentMatch != "") {
            ans2.add(currentMatch.substring(0, 1));
            currentMatch = currentMatch.substring(1);
        }
        return ans2;
        
    }
    public void lz77decode(){

    }
    
}
class Huffman {

    public List<Byte> code(Vector<String>data){
        List<Byte> block = new ArrayList<>();
        data.add("256");
        String buffer = "";
        for(String s : data){
            buffer += getCode(s); 
            while (buffer.length() > 8) {
                block.add((byte)Integer.parseInt(buffer.substring(0, 8),2));
                buffer = buffer.substring(8);
            }
        }
        if(buffer.length() != 0){
            buffer = toBitCount(buffer, 8);
        }
        //block.add((byte)Integer.parseInt(buffer));
                                                    // 10010001 10010010 10010011 10010100 00000000
                                                    // 
                                                    // 10010001 10010001 10010001 0000001 00000 10010010 0000000
        //System.out.println(block);                // 
        System.out.println(buffer);
        return block;
    }
    private ArrayList<String> decoded = new ArrayList<>();
    public ArrayList<String> decode(FileInputStream fis){
        int b = 0;
        ///////////////
        StringBuffer data = new StringBuffer(); 
        try{
            while ((b = fis.read()) != -1){
                data.append(toBitCount(Integer.toBinaryString(b), 8));
                if(b == 0){
                    data = flushData(data);
                }
            }        // TODO Слить остатки и перекод
        }catch(Exception e){
            System.out.println("error reading a byte");
        }
        
        return decoded;
    }

    public StringBuffer flushData(StringBuffer data){
        int i = 0;
        while (i < data.length()) {
            if (i + 9 <= data.length() && inRange(data, i, 9,1)) {
                String code = data.substring(i, i + 9);
                decoded.add(String.valueOf((char)(Integer.parseInt(code,2) - 256)));
                i += 9;

                // TODO Экстра биты

            } else if (i + 8 <= data.length() && inRange(data, i, 8,1)) {
                String code = data.substring(i, i + 8);
                System.out.println("8-bit code: " + String.valueOf((char)(Integer.parseInt(code,2) - 0x30)));
                decoded.add(String.valueOf((char)(Integer.parseInt(code,2) - 0x30)));
                i += 8;
            }else if (i + 8 <= data.length() && inRange(data, i, 8,2)) {
                String code = data.substring(i, i + 8);
                System.out.println("8-bit code: " + String.valueOf((char)(Integer.parseInt(code,2) + 88))); // - 88
                decoded.add(String.valueOf((char)(Integer.parseInt(code,2) - 88)));
                i += 8;
            }else if (i + 7 <= data.length() && inRange(data, i, 7,1)) { // -256
                String code = data.substring(i, i + 7);
                System.out.println("7-bit code: " + String.valueOf((Integer.parseInt(code,2) + 256)));
                int a = Integer.parseInt(code,2) - 256;
                if(a == 256){
                    decoded.add("EOB");
                }else{
                    int extraBits = lenExtraBits(a);

                }

                i += 7;
            } else {
                break;
            }
        }
        return data;
    }

    private int lenExtraBits(int litValue){
        if(litValue <= 264) return 0;
        if(litValue >= 265 && litValue <= 268) return 1;
        if(litValue >= 269 && litValue <= 272) return 2;
        if(litValue >= 273 && litValue <= 276) return 3;
        if(litValue >= 277 && litValue <= 279) return 4;
        System.out.println("ERROR IN LENEXTRABITS");
        return 0;
    }


    private boolean inRange(StringBuffer encodedString, int i, int length, int typeOf8Bit) {
        if (i + length > encodedString.length()) {
            return false;
        }
        int start_7_bit = 0b0000000;
        int end_7_bit = 0b0010111;
        int start_8_bit1 = 0b00110000;
        int end_8_bit1 = 0b10111111;
        int start_8_bit2 = 0b11000000;
        int end_8_bit2 = 0b11000111;
        int start_9_bit = 0b110010000;
        int end_9_bit = 0b111111111;
        int litCode = Integer.parseInt(encodedString.substring(i, i + length),2);
        switch (length) {
            case 7:
                return ((litCode >= start_7_bit )&&(litCode <= end_7_bit));
            case 8:
                return typeOf8Bit == 1 ? ((litCode >= start_8_bit1)&&(litCode <= end_8_bit1)) : ((litCode >= start_8_bit2)&&(litCode <= end_8_bit2));
            case 9:
                return ((litCode >= start_9_bit )&&(litCode <= end_9_bit));
        }
        return false;
    }

    public String getCode(String a){
        int litValue = 0;
        String completeCoded = "";
        if(a.equals("256")){
            return "0000000";
        }
        if(a.length() == 1){            // 0 - 255
            char[] buf = a.toCharArray();
            byte b = (byte)buf[0];
            litValue = b & 0xFF;
            litValue = litValue <= 143 ? litValue + 0x30 : litValue + 256;
            completeCoded = toBitCount(Integer.toBinaryString(litValue), 8);
        }

        // problem


        if(a.length() > 1){
            String[] buf = a.split("~");
            List<String>duo = new ArrayList<>();
            for(String b : buf){
                if(b != ""){
                    duo.add(b);
                }
            }
            int mov = Integer.parseInt(duo.getFirst());
            int len = Integer.parseInt(duo.getLast());
            String dopBit = "";
            if(len <= 10){ 
                litValue = len+254;
            }
            if(len >= 11 && len <= 18){
                if(len >= 11 && len <= 12){litValue = 265; dopBit = len == 11 ? "0":"1";} // 0 ? = 11    or 1 ? = 12
                if(len >= 13 && len <= 14){litValue = 266; dopBit = len == 13 ? "0":"1";} // 0 ? = 13    or 1 ? = 14
                if(len >= 15 && len <= 16){litValue = 267; dopBit = len == 15 ? "0":"1";}
                if(len >= 17 && len <= 18){litValue = 268; dopBit = len == 17 ? "0":"1";}
            }
            if(len >= 19 && len <= 34){
                if(len >= 19 && len <= 22){litValue = 269; dopBit = Integer.toBinaryString(len-19);} // 00 - 19, 01 - 20, 10 - 21, 11 - 22
                if(len >= 23 && len <= 26){litValue = 270; dopBit = Integer.toBinaryString(len-23);} // 
                if(len >= 27 && len <= 30){litValue = 271; dopBit = Integer.toBinaryString(len-27);} //
                if(len >= 31 && len <= 34){litValue = 272; dopBit = Integer.toBinaryString(len-31);} //
                while(dopBit.length() != 2){
                    dopBit = "0" + dopBit;
                }
            }
            if(len >= 35 && len <= 66){
                if(len >= 35 && len <= 42){litValue = 273; dopBit = Integer.toBinaryString(len-35);} // 000 - 35, 001 - 36, 010 - 37, 011 - 38, 100 - 39, 101 - 40, 110 - 41, 111 - 42
                if(len >= 43 && len <= 50){litValue = 274; dopBit = Integer.toBinaryString(len-43);} // 
                if(len >= 51 && len <= 58){litValue = 275; dopBit = Integer.toBinaryString(len-51);} // 
                if(len >= 59 && len <= 66){litValue = 276; dopBit = Integer.toBinaryString(len-59);} // 
                while(dopBit.length() != 3){
                    dopBit = "0" + dopBit;
                }
            }
            if(len >= 67 && len <= 130){
                if(len >= 67 && len <= 82){litValue = 277; dopBit = Integer.toBinaryString(len-67);} // 
                if(len >= 83 && len <= 98){litValue = 278; dopBit = Integer.toBinaryString(len-83);} // 
                if(len >= 99 && len <= 114){litValue = 279; dopBit = Integer.toBinaryString(len-99);} // 
                if(len >= 115 && len <= 130){litValue = 280; dopBit = Integer.toBinaryString(len-115);} // 
                while(dopBit.length() != 4){
                    dopBit = "0" + dopBit;
                }
            }  
            if(len >= 131 && len <= 257){
                if(len >= 131 && len <= 162){litValue = 281; dopBit = Integer.toBinaryString(len-131);}
                if(len >= 163 && len <= 194){litValue = 282; dopBit = Integer.toBinaryString(len-163);}
                if(len >= 195 && len <= 226){litValue = 283; dopBit = Integer.toBinaryString(len-195);}
                if(len >= 227 && len <= 257){litValue = 284; dopBit = Integer.toBinaryString(len-227);}
                while(dopBit.length() != 5){
                    dopBit = "0" + dopBit;
                }
            }
            if(len == 258){
                litValue = 255;
            }  
            String movBinary = "";
            String movBinaryExtra = "";
            mov++; // min dist = 1,
            if(mov == 1){movBinary = "00000";}
            if(mov == 2){movBinary = "00001";}
            if(mov == 3){movBinary = "00010";}
            if(mov == 4){movBinary = "00011";}
            if(mov >=5 && mov <=6){movBinary = "00100"; movBinaryExtra = mov == 5 ? "0" : "1";}
            if(mov >=7 && mov <=8){movBinary = "00101"; movBinaryExtra = mov == 7 ? "0" : "1";}
            if(mov >=9 && mov <=12){movBinary = "00110"; movBinaryExtra = Integer.toBinaryString(mov-9); movBinaryExtra = toBitCount(movBinaryExtra, 2);}
            if(mov >=13 && mov <=16){movBinary = "00111"; movBinaryExtra = Integer.toBinaryString(mov-13); movBinaryExtra = toBitCount(movBinaryExtra, 2);}
            if(mov >=17 && mov <=24){movBinary = "01000"; movBinaryExtra = Integer.toBinaryString(mov-17); movBinaryExtra = toBitCount(movBinaryExtra, 3);}
            if(mov >=25 && mov <=32){movBinary = "01001"; movBinaryExtra = Integer.toBinaryString(mov-25); movBinaryExtra = toBitCount(movBinaryExtra, 3);}
            if(mov >=33 && mov <=48){movBinary = "01010"; movBinaryExtra = Integer.toBinaryString(mov-33); movBinaryExtra = toBitCount(movBinaryExtra, 4);}
            if(mov >=49 && mov <=64){movBinary = "01011"; movBinaryExtra = Integer.toBinaryString(mov-49); movBinaryExtra = toBitCount(movBinaryExtra, 4);}
            if(mov >=65 && mov <=96){movBinary = "01100"; movBinaryExtra = Integer.toBinaryString(mov-65); movBinaryExtra = toBitCount(movBinaryExtra, 5);}
            if(mov >=97 && mov <=128){movBinary = "01101"; movBinaryExtra = Integer.toBinaryString(mov-97); movBinaryExtra = toBitCount(movBinaryExtra, 5);}
            if(mov >=129 && mov <=192){movBinary = "01110"; movBinaryExtra = Integer.toBinaryString(mov-129); movBinaryExtra = toBitCount(movBinaryExtra, 6);}
            if(mov >=193 && mov <=256){movBinary = "01111"; movBinaryExtra = Integer.toBinaryString(mov-193); movBinaryExtra = toBitCount(movBinaryExtra, 6);}
            if(mov >=257 && mov <=384){movBinary = "10000"; movBinaryExtra = Integer.toBinaryString(mov-257); movBinaryExtra = toBitCount(movBinaryExtra, 7);}
            if(mov >=385 && mov <=512){movBinary = "10001"; movBinaryExtra = Integer.toBinaryString(mov-385); movBinaryExtra = toBitCount(movBinaryExtra, 7);}
            if(mov >=513 && mov <=768){movBinary = "10010"; movBinaryExtra = Integer.toBinaryString(mov-513); movBinaryExtra = toBitCount(movBinaryExtra, 8);}
            if(mov >=769 && mov <=1024){movBinary = "10011"; movBinaryExtra = Integer.toBinaryString(mov-769); movBinaryExtra = toBitCount(movBinaryExtra, 8);}
            if(mov >=1025 && mov <=1536){movBinary = "10100"; movBinaryExtra = Integer.toBinaryString(mov-1025); movBinaryExtra = toBitCount(movBinaryExtra, 9);}
            if(mov >=1537 && mov <=2048){movBinary = "10101"; movBinaryExtra = Integer.toBinaryString(mov-1537); movBinaryExtra = toBitCount(movBinaryExtra, 9);}
            if(mov >=2049 && mov <=3072){movBinary = "10110"; movBinaryExtra = Integer.toBinaryString(mov-2049); movBinaryExtra = toBitCount(movBinaryExtra, 10);}
            if(mov >=3073 && mov <=4096){movBinary = "10111"; movBinaryExtra = Integer.toBinaryString(mov-3073); movBinaryExtra = toBitCount(movBinaryExtra, 10);}
            if(mov >=4097 && mov <=6144){movBinary = "11000"; movBinaryExtra = Integer.toBinaryString(mov-4097); movBinaryExtra = toBitCount(movBinaryExtra, 11);}
            if(mov >=6145 && mov <=8192){movBinary = "11001"; movBinaryExtra = Integer.toBinaryString(mov-6145); movBinaryExtra = toBitCount(movBinaryExtra, 11);}
            if(mov >=8193 && mov <=12288){movBinary = "11010"; movBinaryExtra = Integer.toBinaryString(mov-8193); movBinaryExtra = toBitCount(movBinaryExtra, 12);}
            if(mov >=12289 && mov <=16384){movBinary = "11011"; movBinaryExtra = Integer.toBinaryString(mov-12289); movBinaryExtra = toBitCount(movBinaryExtra, 12);}
            if(mov >=16385 && mov <=24576){movBinary = "11100"; movBinaryExtra = Integer.toBinaryString(mov-16385); movBinaryExtra = toBitCount(movBinaryExtra, 13);}
            if(mov >=24577 && mov <=32768){movBinary = "11101"; movBinaryExtra = Integer.toBinaryString(mov-24577); movBinaryExtra = toBitCount(movBinaryExtra, 13);}
            // lit + dop bit + 5 bit mov + dop bit mov
            String litBinary = "";
            
            if(litValue >= 256 && litValue <= 279){
                litBinary = Integer.toBinaryString(litValue-256);
                litBinary = toBitCount(litBinary, 7);
            }
            if(litValue >= 280 && litValue <= 287){
                litBinary = Integer.toBinaryString(litValue-88);
            }
            
            //System.err.println(" ----- ");
            //System.err.printf("[%s,%s]",len,mov);
            //System.out.println();
            //System.out.println(litValue + "/" + litBinary + "-" + dopBit + "-" + movBinary + "-" + movBinaryExtra);
            //System.out.println(" --- ");
            completeCoded = litBinary + dopBit + movBinary + movBinaryExtra;
        }
        
        //  256 ignore
        //  257-264  -- длины, 3-10
        //  265-268  -- пары длин, от 11,12 до 17,18. Следующий бит позволяет выбрать число из пары
        //  269-272  -- четвёрки длин, от 19-22 до 31-34. Следующие два бита позволяют выбрать
        //  273-276  -- восьмёрки длин, от 35-42 до 59-66. Следующие три бита позволяют...
        //  277-280  -- наборы по шестнадцать длин, от 67-82 до 115-130. Следующие четыре бита...
        //  281-284  -- наборы по тридцать две длины, от 131-162 до 227-257. Следующие пять...
        //  285      -- длина 258

        //  256 - 279     7 bit      от   0000000 до   0010111
        //  280 - 287     8 bit      от  11000000 до  11000111

        // Длина ( + доп биты из таблицы ) + Смещение 5 бит ( + доп биты из таблицы ) 

        return completeCoded;
    }
    private String toBitCount(String a, int cnt){
        while(a.length() < cnt){
            a = "0" + a;
        }
        return a;
    }
}