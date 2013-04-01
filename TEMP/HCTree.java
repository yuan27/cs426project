import java.util.*;

//Huffman coding tree for part 1b
public class HCTree{
    //CONSTANTS  
    final int DIFF=128;//difference between int value and byte value
    final int MAXSIZE=128;
    //Variables
    int occTable[];//SIZE:128
    BCoder bc;
    String msg;
    HCNode root;
    //DEBUGGING
    final boolean printOCCTable=false;
    final boolean printHuffTree=false;
    
    public static void main(String[] args){
        HCTree hct=new HCTree("HCT/huff_test_mssg.txt","HCT/test_key.txt");
        hct.run();
        
    }
    
    public void run(){
        parseMsg();
        if(printOCCTable)printTable();
        constructTree();
        if(printHuffTree)printTree(root);
        printBFSTree();
    }
    
    public void constructTree(){
        //get the priority queue
        PriorityQueue<HCNode> queue=new PriorityQueue<HCNode>();
        for(int i=0;i<MAXSIZE;i++){
            if(occTable[i]!=0){
                HCNode nnode=new HCNode(""+(char)(i),occTable[i], null);
                queue.add(nnode);
            }
        }
        //constructing the Huffman Tree
        HCNode n1=null,n2=null;
        while(((n2=queue.poll())!=null) && ((n1=queue.poll())!=null)){
            HCNode nnode=new HCNode(n1.str+n2.str,n1.occ+n2.occ, null);
            nnode.left=n1;nnode.right=n2;
            n1.parent=nnode;n2.parent=nnode;
            queue.add(nnode);
        }
        if(n1==null&&n2==null){
            System.out.println("oops");
        }
        if(n1!=null)
            root=n1;
        else
            root=n2;
    }
    
    public void reconstructTree(){
        String hmac=bc.calcHMAC(msg);
        LinkedList<HCNode> list=new LinkedList<HCNode>();
        list.add(root);
        HCNode node;
        int count=0;
        while(list.size()>0){
            node=list.getFirst();
            if(node.left==null && node.right==null)continue;
            if(hmac.charAt(count++)=='0'){//swap left and right child
                HCNode temp=node.left;
                node.left=node.right;
                node.right=temp;
            }
            if(node.left!=null)list.addLast(node.left);
            if(node.right!=null)list.addLast(node.right);
            list.removeFirst();
        }
    }
    
    
    
    
    //get occTable
    public void parseMsg(){
        int index;
        for(int i=0;i<msg.length();i++){
            index=(msg.charAt(i)-0);
            occTable[index]+=1;
        }
    }
    
    public HCTree(String msgFile,String keyFile){
        occTable=new int[MAXSIZE];
        bc=new BCoder(keyFile);
        msg=bc.getHmacInput(msgFile);
    }
    
    public class HCNode implements Comparable<HCNode>{
        public HCNode parent,left,right;
        public int occ;
        public String str;
        
        public HCNode(String str, int occ, HCNode p){
            this.occ=occ;
            this.str=str;
            parent=p;left=right=null;
        }
        public int compareTo(HCNode n1) {
            if(n1.occ!=occ){
                return (occ-n1.occ);
            }
            if(n1.str.length()!=str.length()){
                return str.length()-n1.str.length();
            }
            return n1.str.charAt(0)-str.charAt(0);
        }
        
    }
    
    
    
    //Debug function
    public void printTable(){
        for(int i=0;i<MAXSIZE;i++){
            if(occTable[i]!=0)
                System.out.printf("%d:%d\n",i,occTable[i]);
        }
    }
    
    public void printTree(HCNode node){
        if(node==null)
            return;
        System.out.printf("[%s,%d]\n",node.str,node.occ);
        if(node.left!=null){
            System.out.printf("LEFT:\n");
            printTree(node.left);
        }
        if(node.right!=null){
            System.out.printf("RIGHT:\n");
            printTree(node.right);
        }
    }
    
    public void printBFSTree(){
        if(root==null)return;
        String binstr="";
        String valuestr="";
        LinkedList<HCNode> list=new LinkedList<HCNode>();
        list.add(root);
        HCNode node;
        while(list.size()>0){
            node=list.getFirst();
            if(node.left==null && node.right==null){
                binstr=binstr+"0";
                valuestr=valuestr+(node.str.charAt(0)-0)+"\n";
            }else{
                binstr=binstr+"1";
            }
            if(node.left!=null)list.addLast(node.left);
            if(node.right!=null)list.addLast(node.right);
            list.removeFirst();
        }
        System.out.print(binstr+"\n");
        System.out.print(valuestr);
    }
    
    
    
}