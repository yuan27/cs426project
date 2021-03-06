import java.util.*;
import java.io.*;
//Huffman coding tree for part 1b
//read input as array of bytes
public class HCTree{
    //CONSTANTS  
    final int DIFF=128;//difference between int value and byte value
    final int MAXSIZE=256;
    
    HCNode root;
    //a.Variables
    int occTable[];//SIZE:MAXSIZE
    BCoder bc;
    String msg;
    //b.Variables
    String filecodestr="";
    String treecodestr="";
    String[] codeTable;
    byte[] msgarr;
    
    //DEBUGGING
    final boolean printOCCTable=false;
    final boolean printHuffTree=false;
    final boolean NOZERO=false; 
    
    /*3 LOCATIONS TO CHANGE IN SIMPLE VERSION
     * 1. in main, change the filename;
     * 2. above, change NOZERO=true;
     * 3. in reconstructTree(), change HMAC;
     */
    
    
    public static void main(String[] args){
        //a
        /*HCTree hct=new HCTree("../HCT/huff_test_mssg.txt","../HCT/test_key.txt");
         hct.buildTreeFromMessage();*/
        //b
        HCTree hct=new HCTree("../HCT/huff_test_mssg.txt","../HCT/test_key.txt","../HCT/huff_test_tree_clean.txt");
        hct.verify();
    }
    
    //Constrution of TREE from file
    public String verify(){
        String res="";
        int msglen=msgarr.length;
        for(int i=0;i<msglen;i++){
            treecodestr=treecodestr+codeTable[msgarr[i]+DIFF];
        }
        //Now compare treecodestr and filecodestr
        boolean same=treecodestr.equals(filecodestr);
        if(same){
            res=":) -- This tree has not been modified.";
        }else{
            res=":( -- This tree has been modified.";
        }
        System.out.println(res);
        return res;
    }
    
    public HCTree(String msgFile,String keyFile,String treeFile){
        bc=new BCoder(keyFile);
        msgarr=bc.getAllInput(msgFile).getBytes();
        codeTable=new String[MAXSIZE];
        
        //Begin parsing the tree
        String treeStructure="";
        int len=0;
        ArrayList<Integer> values = new ArrayList<Integer>();
        //Start parsing file
        try {
            FileInputStream file = new FileInputStream(treeFile);
            Scanner in=new Scanner(file);
            if (file == null || in==null) {
                System.out.printf("  Error reading the file %s.\n",treeFile);
                System.exit(0);
            }
            treeStructure=in.next();
            len=numberOfZero(treeStructure);
            for(int i=0;i<len;i++){
                values.add(in.nextInt());
            }
            filecodestr=in.next();
            file.close();
            in.close();
        }catch(java.io.IOException e){
            e.printStackTrace();
            System.exit(0);
        }
        //Now have treeStructure, values, and filecodestr. Need to construct tree from this.
        root=new HCNode("","",null);
        LinkedList<HCNode> list=new LinkedList<HCNode>();
        root.code="";
        list.add(root);
        HCNode node;
        int count=0,maxcount=treeStructure.length()-1,childNum=0;
        while(count<=maxcount){
            node=list.getFirst();
            if(treeStructure.charAt(count)=='1'){//is a parent
                HCNode left=new HCNode("",node.code+"0",node);
                HCNode right=new HCNode("",node.code+"1",node);
                node.left=left;node.right=right;
                list.addLast(left);list.addLast(right);
            }else{//is a child
                int temp=values.get(childNum++);
                node.str=""+temp;
                codeTable[temp+DIFF]=node.code;
            }
            list.removeFirst();
            count++;
        }
        if(len!=childNum){
            System.out.println("Something went wrong");
        }
    }
    
    
    
    
    
    
    //Constrution of TREE from message file
    public String buildTreeFromMessage(){
        parseMsg();
        if(printOCCTable)printTable();
        constructTree();
        reconstructTree();
        if(printHuffTree)
            printTree();
        return printBFSTree();
    }
    
    
    public HCTree(String msgFile,String keyFile){
        occTable=new int[MAXSIZE];
        bc=new BCoder(keyFile);
        msg=bc.getAllInput(msgFile);
    }
    
    public class HCNode implements Comparable<HCNode>{
        public HCNode parent,left,right;
        public int occ;
        public String str;
        public String code;
        
        public HCNode(String str, int occ, HCNode p){
            this.occ=occ;
            this.str=str;
            parent=p;left=right=null;
        }
        
        public HCNode(String str, String code, HCNode p){
            this.code=code;
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
    
    //get occTable
    public void parseMsg(){
        /*byte[] bytearr=getByteArrFromFile("../HCT/huff_test_mssg.txt");*/
        byte[] bytearr=msg.getBytes();
        for(int i=0;i<bytearr.length;i++){
            int index=(bytearr[i]+DIFF);
            occTable[index]+=1;
        }
        
    }
    
    public void constructTree(){
        //get the priority queue
        PriorityQueue<HCNode> queue=new PriorityQueue<HCNode>();
        
        for(int i=0;i<MAXSIZE;i++){
            if(NOZERO){
                if(occTable[i]!=0){
                    HCNode nnode=new HCNode(""+(char)(i-DIFF),occTable[i], null);
                    queue.add(nnode);
                }
            }else{
                HCNode nnode=new HCNode(""+(char)(i-DIFF),occTable[i], null);
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
        String hmac;
        //hmac=bc.calcHMAC(msg);
        hmac="1111111111111111111111111011111111111111111111111111111111111000000000000000000000000010100000000000000000000000000000000010001111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111100000000000000000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        //hmac="0001100";
        
        LinkedList<HCNode> list=new LinkedList<HCNode>();
        list.add(root);
        HCNode node,temp;
        int count=0;
        while(list.size()>0){
            node=list.getFirst();
            if(node.left==null && node.right==null){
                list.removeFirst();
                continue;
            }
            if(hmac.charAt((count++)%256)=='0'){//swap left and right child
                temp=node.left;
                node.left=node.right;
                node.right=temp;
            }
            if(node.left!=null)list.addLast(node.left);
            if(node.right!=null)list.addLast(node.right);
            list.removeFirst();
        }
    }
    
    
    
    
    
    //Debug function
    public void printTable(){
        int sum=0;
        for(int i=0;i<MAXSIZE;i++){
            if(occTable[i]!=0){
                sum+=occTable[i];
                System.out.printf("%d:%d\n",i-DIFF,occTable[i]);
            }
        }
        System.out.printf("SUM:%d\n",sum);
    }
    
    public void printTree(){
        printNode(root);
    }
    
    public void printNode(HCNode node){
        if(node==null)
            return;
        System.out.printf("[%s,%d]\n",node.str,node.occ);
        if(node.left!=null){
            System.out.printf("LEFT:\n");
            printNode(node.left);
        }
        if(node.right!=null){
            System.out.printf("RIGHT:\n");
            printNode(node.right);
        }
    }
    
    public String printBFSTree(){
        //encode the tree here
        if(root==null)return "";
        String binstr="";
        String valuestr="";
        String codestr="";
        LinkedList<HCNode> list=new LinkedList<HCNode>();
        root.code="";
        list.add(root);
        HCNode node;
        while(list.size()>0){
            node=list.getFirst();
            if(node.left==null && node.right==null){
                binstr=binstr+"0";
                valuestr=valuestr+((byte)node.str.charAt(0))+"\n";
                codestr=codestr+node.code;
            }else{
                binstr=binstr+"1";
            }
            if(node.left!=null){node.left.code=node.left.parent.code+"0";list.addLast(node.left);}
            if(node.right!=null){node.right.code=node.right.parent.code+"1";list.addLast(node.right);}
            list.removeFirst();
        }
        String res=binstr+"\n"+valuestr+codestr+"\n";
        System.out.println(binstr);
        System.out.print(valuestr);
        System.out.println(codestr);
        return res;
    }
    
    //Helper function
    int numberOfZero(String str){
        int slen=str.length(),count=0;
        for(int i=0;i<slen;i++){
            if(str.charAt(i)=='0')
                count++;
        }
        return count;
    }
    
    
}