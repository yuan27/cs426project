import java.util.*;
import java.io.*;

public class readerTest{
    //Get all strings from a file
    public String getAllInput(String fileName){   
        try {
            FileInputStream file = new FileInputStream(fileName);
            if (file == null) {
                System.out.printf("  Error reading the file %s.\n",fileName);
                System.exit(0);
            }
            byte b;
            String str="";
            while((b=(byte)file.read())!=-1){
                str=str+(char)b;
            }
            file.close();
            return str;
        }catch(java.io.IOException e){
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    } 
    
    
    public static void main(String[] args){
        readerTest rd=new readerTest();
        String str=rd.getAllInput("../HCT/huff_test_mssg.txt");
        System.out.println(str);
    }
}