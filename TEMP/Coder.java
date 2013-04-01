import java.util.*;
import java.io.*;
import java.security.MessageDigest;


public class Coder{
  //DEBUGGING
  final boolean printPlainTextAndHash=false;
  final boolean printHMAC=true;
  final boolean printInput=false;
  //CONSTANTS  
  final int DIFF=128;//difference between int value and byte value
  int[] ipad=null;
  int[] opad=null;
  //key and message
  public int[] origMsg;
  int[] key;
  int[] msg;
  public String hmac;
  
  //FUNCTIONS
  /*Constructor with msgfile and keyfile specified.*/
  //public Coder(String msgFile,String keyFile)
  /*Calculate hmac based on a string concatenation*/
  //public String calcHMAC(String msgStr);
  
  public static void main(String[] args){
    Coder coder=new Coder("BST/bst_test_data.txt","BST/test_key.txt","BST/bst_hmac.txt"); 
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
  
  public Coder(String msgFile,String keyFile,String hmacFile){
      ipad=new int[64];opad=new int[64];
      for(int i=0;i<64;i++){
          ipad[i]=0x5c;
          opad[i]=0x36;
      } 
      getMsgInput(msgFile);
      getKeyInput(keyFile);
      getHmacInput(hmacFile);
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

  //Calculate HMAC(M,H,K)
  public String calcHMAC(){
    int[] h1=getCipher(orOfIntArrays(msg,xorOfIntArrays(key,ipad)));
    int[] h2=getCipher(orOfIntArrays(h1,xorOfIntArrays(key,opad)));
    hmac=getBinStr(h2);
    if(printHMAC){
      System.out.println("HMAC: "+Arrays.toString(h2));
      System.out.println("HMAC in Binary: "+hmac);
    }
    return hmac;
  }
  
  public String calcHMAC(String msgStr){
      byte[] byteArr=msgStr.getBytes();
      int[] intArr=getIntFromByteArray(byteArr);
      int[] h1=getCipher(orOfIntArrays(intArr,xorOfIntArrays(key,ipad)));
      int[] h2=getCipher(orOfIntArrays(h1,xorOfIntArrays(key,opad)));
      hmac=getBinStr(h2);
      if(printHMAC){
          System.out.println("HMAC in Binary: "+hmac);
      }
      return hmac;   
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
      String msgStr="";
      while (in.hasNextInt()) {
        int tmp=in.nextInt();
        text.add(tmp);
        msgStr=msgStr+tmp;
      }
      file.close();
      in.close();
      byte[] byteArr=msgStr.getBytes();
      int [] intArr=getIntFromByteArray(byteArr);
      //Get msg and origMsg
      msg=intArr;
      origMsg=getIntPrimitive(text);
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
  public int[] getKeyInput(String fileName){   
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
      int [] intArr=getIntFromByteArray(byteArr);
      if(printInput){
        System.out.println();
        System.out.printf("input - %s: ",fileName);
        for(int i=0;i<intArr.length;i++){
            if(i==0){
                System.out.printf("[%d",intArr[i]);
            }else if (i==(intArr.length-1)){
                System.out.printf(", %d]\n",intArr[i]);
            }else{
                System.out.printf(", %d",intArr[i]);
            }
        }
      }
      key=intArr;
      return intArr;
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
      hmac=msgStr;
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
  
  //6.Get the binary String for an int array
  public String getBinStr(int[] arr){
      String str="";
      for(int i=0;i<arr.length;i++){
          str=str+Integer.toBinaryString(arr[i]);
      }
      return str;
  }
  
}