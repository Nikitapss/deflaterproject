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
            //LZ77 lz77coder = new LZ77(buff);
            LZ77 lz77coder = new LZ77();
            Vector<String> lz = lz77coder.lz77code(buff);
            Huffman huff = new Huffman();
            List<Byte> done = huff.code(lz, true);
            System.out.println(done);
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
    public Vector<String> lz77code(byte[] buff){                     
        Vector<String> ans = new Vector<String>();      
        for(byte x : buff){                         // moving byte[] to vector
            ans.add(String.valueOf((char)x));
        }

        for(int i = 258; i >=3; i--){               // max match length
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
class Huffman {

    public List<Byte> code(Vector<String>data, boolean end){
        List<Byte> block = new ArrayList<>();
        String buffer = end ? "1" : "0";            // чтение справа налево
        for(String s : data){
            buffer = getCode(s) + buffer;
            if(buffer.length() > 8){
                block.add((byte)Integer.parseInt(buffer.substring(0, 9)));
                buffer = buffer.substring(9);
            }
        }
        buffer = "0000000" + buffer;
        return block;
    }
    public String getCode(String a){
        int litValue = 0;
        if(a.length() == 1){            // 0 - 255
            char[] buf = a.toCharArray();
            byte b = (byte)buf[0];
            litValue = b & 0xFF;
        }
        if(a.length() > 1){
            String value = "";
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
            if(mov ==1){movBinary = "00000";}
            if(mov == 2){movBinary = "00001";}
            if(mov == 3){movBinary = "00010";}
            if(mov == 4){movBinary = "00011";}
            if(mov >=5 && mov <=6){movBinary = "00100"; movBinaryExtra = mov == 5 ? "0" : "1";}
            if(mov >=7 && mov <=8){movBinary = "00101"; movBinaryExtra = mov == 7 ? "0" : "1";}
            if(mov >=9 && mov <=12){movBinary = "00110"; movBinaryExtra = Integer.toBinaryString(mov-9);}
            if(mov >=13 && mov <=16){movBinary = "00111"; movBinaryExtra = Integer.toBinaryString(mov-13);}
            if(mov >=17 && mov <=24){movBinary = "01000"; movBinaryExtra = Integer.toBinaryString(mov-17);}
            if(mov >=25 && mov <=32){movBinary = "01001"; movBinaryExtra = Integer.toBinaryString(mov-25);}
            if(mov >=33 && mov <=48){movBinary = "01010"; movBinaryExtra = Integer.toBinaryString(mov-33);}
            if(mov >=49 && mov <=64){movBinary = "01011"; movBinaryExtra = Integer.toBinaryString(mov-49);}
            if(mov >=65 && mov <=96){movBinary = "01100"; movBinaryExtra = Integer.toBinaryString(mov-65);}
            if(mov >=97 && mov <=128){movBinary = "01101"; movBinaryExtra = Integer.toBinaryString(mov-97);}
            if(mov >=129 && mov <=192){movBinary = "01110"; movBinaryExtra = Integer.toBinaryString(mov-129);}
            if(mov >=193 && mov <=256){movBinary = "01111"; movBinaryExtra = Integer.toBinaryString(mov-193);}
            if(mov >=257 && mov <=384){movBinary = "10000"; movBinaryExtra = Integer.toBinaryString(mov-257);}
            if(mov >=385 && mov <=512){movBinary = "10001"; movBinaryExtra = Integer.toBinaryString(mov-385);}
            if(mov >=513 && mov <=768){movBinary = "10010"; movBinaryExtra = Integer.toBinaryString(mov-513);}
            if(mov >=769 && mov <=1024){movBinary = "10011"; movBinaryExtra = Integer.toBinaryString(mov-769);}
            if(mov >=1025 && mov <=1536){movBinary = "10100"; movBinaryExtra = Integer.toBinaryString(mov-1025);}
            if(mov >=1537 && mov <=2048){movBinary = "10101"; movBinaryExtra = Integer.toBinaryString(mov-1537);}
            if(mov >=2049 && mov <=3072){movBinary = "10110"; movBinaryExtra = Integer.toBinaryString(mov-2049);}
            if(mov >=3073 && mov <=4096){movBinary = "10111"; movBinaryExtra = Integer.toBinaryString(mov-3073);}
            if(mov >=4097 && mov <=6144){movBinary = "11000"; movBinaryExtra = Integer.toBinaryString(mov-4097);}
            if(mov >=6145 && mov <=8192){movBinary = "11001"; movBinaryExtra = Integer.toBinaryString(mov-6145);}
            if(mov >=8193 && mov <=12288){movBinary = "11010"; movBinaryExtra = Integer.toBinaryString(mov-8193);}
            if(mov >=12289 && mov <=16384){movBinary = "11011"; movBinaryExtra = Integer.toBinaryString(mov-12289);}
            if(mov >=16385 && mov <=24576){movBinary = "11100"; movBinaryExtra = Integer.toBinaryString(mov-16385);}
            if(mov >=24577 && mov <=32768){movBinary = "11101"; movBinaryExtra = Integer.toBinaryString(mov-24577);}
            

            System.out.println(duo);
            System.out.println(dopBit);
            System.out.println(" --- ");
            // lit + dop bit + 5 bit mov + dop bit mov
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

        
        return "10101010";
    }
}