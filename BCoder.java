import java.util.*;
import java.io.*;
import java.security.MessageDigest;


public class BCoder{
    //DEBUGGING
    final boolean printPlainTextAndHash=false;
    final boolean printHMAC=true;
    final boolean printInput=false;
    //CONSTANTS  
    final int DIFF=128;//difference between int value and byte value
    byte[] ipad=null;
    byte[] opad=null;
    //key and message
    public byte[] key;//BOTH
    
    //FUNCTIONS
    /*Constructor with msgfile and keyfile specified.*/
    //public Coder(String msgFile,String keyFile)
    /*Calculate hmac based on a string concatenation*/
    //public String calcHMAC(String msgStr);
    
    public static void main(String[] args){
        BCoder coder=new BCoder("BST/bst_test_data.txt","BST/test_key.txt","BST/bst_hmac.txt"); 
    }
    
    
    /*Major Functions*/
    //COnstructor
    public BCoder(String keyFile){
        int ipad_int[]=new int[64],opad_int[]=new int[64];
        for(int i=0;i<64;i++){
            ipad_int[i]=0x5c;
            opad_int[i]=0x36;
        }
        ipad=getByteSameFromIntArray(ipad_int);
        opad=getByteSameFromIntArray(opad_int);
        key=getKeyInput(keyFile);
    }
    
    //constructor for BCTree
    public BCoder(String msgFile,String keyFile,String hmacFile){
        int ipad_int[]=new int[64],opad_int[]=new int[64];
        for(int i=0;i<64;i++){
            ipad_int[i]=0x5c;
            opad_int[i]=0x36;
        }
        ipad=getByteSameFromIntArray(ipad_int);
        opad=getByteSameFromIntArray(opad_int);
        getMsgInput(msgFile);
        key=getKeyInput(keyFile);
        getHmacInput(hmacFile);
    }
    
    //constructor for HCTREE
    public BCoder(String msgFile,String keyFile){
        getKeyInput(msgFile);
        key=getKeyInput(keyFile);
    }
    
    //Get unsigned cipher from unsigned plaintext
    public byte[] getCipher(byte[] plainText){
        if(printPlainTextAndHash){
            System.out.println("\n---Start hashing:");                  
            System.out.println("signed plaintext: "+Arrays.toString(plainText));
        } 
        //Encoding it using SHA-256
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] cipher=md.digest(plainText);
            if(printPlainTextAndHash){
                System.out.println("signed cipher: "+Arrays.toString(cipher));
                System.out.println("---End hashing.\n");
            }
            return cipher;
        } catch (java.security.NoSuchAlgorithmException e) {
            e.getStackTrace();
            System.exit(0);
        }
        return null;
    }
    
    public String calcHMAC(String msgStr){
        byte[] byteArr=msgStr.getBytes();
        byte[] h1=getCipher(orOfByteArrays(byteArr,xorOfByteArrays(key,ipad)));
        byte[] h2=getCipher(orOfByteArrays(h1,xorOfByteArrays(key,opad)));
        String hmac=getBinStr(h2);
        if(printHMAC){
            System.out.println("HMAC in Binary: "+hmac);
        }
        return hmac;   
    }
    
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
    
    
    //Get an array of integers from a file
    public int[] getMsgInput(String fileName){   
        try {
            FileInputStream file = new FileInputStream(fileName);
            Scanner in=new Scanner(file);
            ArrayList<Integer> text = new ArrayList<Integer>();
            if (file == null || in==null) {
                System.out.printf("  Error reading the file %s.\n",fileName);
                System.exit(0);
            }
            while (in.hasNextInt()) {
                text.add(in.nextInt());
            }
            file.close();
            in.close();
            int[] origMsg=getIntPrimitive(text);
            if(printInput)
                System.out.printf("input - %s: %s\n",fileName,text.toString());
            return origMsg;
        }catch(java.io.IOException e){
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }
    
    
    //Get an array of bytes from a file
    public byte[] getKeyInput(String fileName){   
        try {
            FileInputStream file = new FileInputStream(fileName);
            Scanner in=new Scanner(file);
            if (file == null || in==null) {
                System.out.printf("  Error reading the file %s.\n",fileName);
                System.exit(0);
            }
            String text="";
            while (in.hasNext()) {
                text=text+in.next();
            }
            file.close();
            in.close();
            byte[] byteArr=text.getBytes();
            if(printInput){
                System.out.println();
                System.out.printf("input - %s: ",fileName);
                for(int i=0;i<byteArr.length;i++){
                    if(i==0){
                        System.out.printf("[%b",byteArr[i]);
                    }else if (i==(byteArr.length-1)){
                        System.out.printf(", %b]\n",byteArr[i]);
                    }else{
                        System.out.printf(", %b",byteArr[i]);
                    }
                }
            }
            return byteArr;
        }catch(java.io.IOException e){
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }
    
    //Get an concatenation of data from a file
    public String getMsgStringInput(String fileName){   
        try {
            FileInputStream file = new FileInputStream(fileName);
            Scanner in=new Scanner(file);
            if (file == null || in==null) {
                System.out.printf("  Error reading the file %s.\n",fileName);
                System.exit(0);
            }
            String text="";
            while (in.hasNext()) {
                text=text+in.next();
            }
            file.close();
            in.close();
            return text;
        }catch(java.io.IOException e){
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }
    
    //Get an array of integers from a file
    public String getHmacInput(String fileName){   
        try {
            FileInputStream file = new FileInputStream(fileName);
            Scanner in=new Scanner(file);
            if (file == null || in==null) {
                System.out.printf("  Error reading the file %s.\n",fileName);
                System.exit(0);
            }
            String msgStr="";
            while (in.hasNext()) {
                msgStr=msgStr+in.next();
            }
            file.close();
            in.close();
            String hmac=msgStr;
            return hmac;
        }catch(java.io.IOException e){
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }
    
    
    
    /*Helper functions*/
    
    //1.Get an unsigned integer array (0-255) from a byte array (-128-127)
    public int[] getIntFromByteArray(byte[] barr){
        int len=barr.length;
        int[] iarr=new int[len];
        for(int i=0;i<len;i++){
            int tmp=barr[i]+DIFF;
            if(tmp>=0 && tmp<=255)
                iarr[i]=tmp;
            else{
                System.out.printf("  Error: invalid input %b: (must be between -127 and 128)\n",tmp);
                System.exit(0);
            }
        }
        return iarr;
    }
    
    //2.Get a byte array (-128-127) from an int array (0-255)
    public byte[] getByteFromIntArray(int[] iarr){
        int len=iarr.length;
        byte[] barr=new byte[len];
        for(int i=0;i<len;i++){
            int tmp=iarr[i]-DIFF;
            if(tmp>=-128 && tmp<=127)
                barr[i]=(byte)tmp;
            else{
                System.out.printf("  Error: invalid input %d: (must be between 0 and 255)\n",tmp);
                System.exit(0);
            }
        }
        return barr;
    }
    
    //2.Get a byte array (-128-127) from an int array (0-127)
    public byte[] getByteSameFromIntArray(int[] iarr){
        int len=iarr.length;
        byte[] barr=new byte[len];
        for(int i=0;i<len;i++){
            int tmp=iarr[i];
            if(tmp>=-128 && tmp<=127)
                barr[i]=(byte)tmp;
            else{
                System.out.printf("  Error: invalid input %d: (must be between 0 and 255)\n",tmp);
                System.exit(0);
            }
        }
        return barr;
    }
    
    //3a.Get primitive type array from an arraylist of int
    public int[] getIntPrimitive(ArrayList<Integer> alist){
        int len=alist.size();
        int[] list=new int[len];
        for(int i=0;i<len;i++){
            list[i]=alist.get(i).intValue();
        }
        return list;
    }
    
    //3b.Get primitive type array from an arraylist of byte
    public byte[] getBytePrimitive(ArrayList<Byte> alist){
        int len=alist.size();
        byte[] list=new byte[len];
        for(int i=0;i<len;i++){
            list[i]=alist.get(i).byteValue();
        }
        return list;
    }
    
    //4a.Get the result of "or"ing two int arrays 
    public int[] orOfIntArrays(int[] arr1,int[] arr2){
        int len1=arr1.length,len2=arr2.length,len=(len1>len2)?len1:len2;
        int arr[]=new int[len];
        for(int i=0;i<len;i++){
            arr[i]=arr1[i%len1] | arr2[i%len2];
        }
        return arr;
    }
    
    //4b.Get the result of "or"ing two byte arrays, arr2 has size 64
    public byte[] orOfByteArrays(byte[] arr1,byte[] arr2){
        int len1=arr1.length,len2=arr2.length,len=(len1>len2)?len1:len2;
        byte arr[]=new byte[len];
        for(int i=0;i<len;i++){
            if(i>=64){
                arr[i]=(byte)(arr1[i%len1] | 0); 
            }else{
                arr[i]=(byte)(arr1[i%len1] | arr2[i%len2]);
            }
        }
        return arr;
    }
    
    //5a.Get the result of "xor"ing two int arrays 
    public int[] xorOfIntArrays(int[] arr1,int[] arr2){
        int len1=arr1.length,len2=arr2.length,len=(len1>len2)?len1:len2;
        int arr[]=new int[len];
        for(int i=0;i<len;i++){
            arr[i]=arr1[i%len1] ^ arr2[i%len2];
        }
        return arr;
    }
    
    //5b.Get the result of "xor"ing two byte arrays 
    public byte[] xorOfByteArrays(byte[] arr1,byte[] arr2){
        int len1=arr1.length,len2=arr2.length,len=(len1>len2)?len1:len2;
        byte arr[]=new byte[len];
        for(int i=0;i<len;i++){
            arr[i]=(byte)(arr1[i%len1] ^ arr2[i%len2]);
        }
        return arr;
    }
    
    //6.Get the binary String for an int array
    public String getBinStr(int[] arr){
        String str="";
        for(int i=0;i<arr.length;i++){
            str=str+Integer.toBinaryString(arr[i]);
        }
        return str;
    }
    
    public String getBinStr(byte[] arr){
        String str="";
        for(int i=0;i<arr.length;i++){
            str=str+Integer.toBinaryString((int)arr[i]);
        }
        return str;
    }
    
}