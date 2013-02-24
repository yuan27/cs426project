import java.util.*;
import java.io.*;
import java.security.MessageDigest;


public class Coder{
  //DEBUGGING
  final boolean printPlainTextAndHash=false;
  final boolean printHMAC=true;
  final boolean printInput=true;
  //CONSTANTS  
  final int DIFF=128;//difference between int value and byte value
  int[] ipad=null;
  int[] opad=null;
  //key and message
  int[] key;
  int[] msg;
  
  //FUNCTIONS
  /*Constructor with msgfile and keyfile specified.*/
  //public Coder(String msgFile,String keyFile)
  
  /*Get unsigned cipher from unsigned plaintext*/
  //public int[] getCipher(int[] plainText);
  
  /*Get an array of integers from a file*/
  //public int[] getInput(String fileName)

  /*Calculate HMAC(M,H,K)*/
  //public int[] calcHMAC(int[] msg, int[] key)
  
  public static void main(String[] args){
    Coder coder=new Coder("msg.txt","key.txt");
    coder.calcHMAC();
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  /*Major Functions*/
  //COnstructor
  public Coder(){
    ipad=new int[64];opad=new int[64];
    for(int i=0;i<64;i++){
      ipad[i]=0x5c;
      opad[i]=0x36;
    }
  }
  
  public Coder(String msgFile,String keyFile){
      ipad=new int[64];opad=new int[64];
      for(int i=0;i<64;i++){
          ipad[i]=0x5c;
          opad[i]=0x36;
      }    
      msg=getInput(msgFile);
      key=getInput(keyFile);
  }
  
  //Get unsigned cipher from unsigned plaintext
  public int[] getCipher(int[] plainText){
    if(printPlainTextAndHash){
      System.out.println("\n---Start hashing:");                  
      System.out.println("unsigned plaintext: "+Arrays.toString(plainText));
    }
    byte[] bytePlainText=getByteFromIntArray(plainText);   
    //Encoding it using SHA-256
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] cipher=md.digest(bytePlainText);
      int[] intCipher=getIntFromByteArray(cipher);
      if(printPlainTextAndHash){
        System.out.println("unsigned cipher: "+Arrays.toString(intCipher));
        System.out.println("---End hashing.\n");
      }
      return intCipher;
    } catch (java.security.NoSuchAlgorithmException e) {
      e.getStackTrace();
      System.exit(0);
    }
    return null;
  }
  
  //Get an array of integers from a file
  public int[] getInput(String fileName){   
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
      if(printInput)
        System.out.printf("input - %s: %s\n",fileName,text.toString());
      return getPrimitive(text);
    }catch(java.io.IOException e){
      e.printStackTrace();
      System.exit(0);
    }
    return null;
  }
  
  //Calculate HMAC(M,H,K)
  public int[] calcHMAC(){
    int[] h1=getCipher(orOfIntArrays(msg,xorOfIntArrays(key,ipad)));
    int[] h2=getCipher(orOfIntArrays(h1,xorOfIntArrays(key,opad)));
    if(printHMAC)
      System.out.println("HMAC: "+Arrays.toString(h2));
    return h2;
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
  
  //3.Get primitive type array from an arraylist
  public int[] getPrimitive(ArrayList<Integer> alist){
    int len=alist.size();
    int[] list=new int[len];
    for(int i=0;i<len;i++){
      list[i]=alist.get(i).intValue();
    }
    return list;
  }
  
  //4.Get the result of "or"ing two arrays 
  public int[] orOfIntArrays(int[] arr1,int[] arr2){
    int len1=arr1.length,len2=arr2.length,len=(len1>len2)?len1:len2;
    int arr[]=new int[len];
    for(int i=0;i<len;i++){
      arr[i]=arr1[i%len1] | arr2[i%len2];
    }
    return arr;
  }
  
  //5.Get the result of "xor"ing two arrays 
  public int[] xorOfIntArrays(int[] arr1,int[] arr2){
    int len1=arr1.length,len2=arr2.length,len=(len1>len2)?len1:len2;
    int arr[]=new int[len];
    for(int i=0;i<len;i++){
      arr[i]=arr1[i%len1] ^ arr2[i%len2];
    }
    return arr;
  }
  
}