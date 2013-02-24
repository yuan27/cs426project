import java.util.*;


public class HuffmanTree{
  
  public class Symbol implements Comparable<Symbol>{
    byte value;
    int occr;
    public Symbol(byte _value, int _occr){value=_value;occr=_occr;}
    public byte getVal(){return value;}
    public int getOccr(){return occr;}
    public int compareTo(Symbol s) {return (occr-s.getOccr());}
  }
  
  public int[] getAlphabet(byte[] plaintext){
    int totalNum=0;
    int[] cipher=new int[256]; 
    for(int i=0;i<text.length;i++){
      if(numArr[text[i]]==0){totalNum++;}
      numArr[text[i]]++;
    }
    printIntArray(numArr);
    //Create a priority queue
    PriorityQueue<Symbol> queue= new PriorityQueue<Symbol>(totalNum);
    for(int i=0;i<numArr.length;i++){
      if(numArr[i]!=0){
        queue.add(new Symbol((byte)i,numArr[i]));
      }
    }
    //Construct the huffman tree
    return numArr;
  }
  

  public static void main(String[] args){
    byte [] txt={1,2,3,4,5,5,4,6,7,8,127};
    HuffmanTree tree=new HuffmanTree();
    tree.getAlphabet(txt);
    
  }
}