import java.util.*;

//Balanced Search Tree for part1a.
public class BSTree{
    //DEBUGGING
    final boolean printConstruct=false;
    final boolean printWholeTree=false;
    final boolean printHMACInput=false;
    //Variables
    Node root;
    //message info
    int msglen;
    int[] msg;
    //hmac info
    String m;
    int mlen;
    int pos;//position of next char in m to use
    //string concat of the tree
    public String catstr;
    BCoder bc;
    
    
    
    public static void main(String[] args){
        /*//simple test
        String m="0001100011000";
        int[] arr={0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
        BSTree bst=new BSTree(arr,m);
        bst.run();*/
        /*BSTree bst=new BSTree("BST/bst_test_data.txt","BST/test_key.txt","BST/bst_hmac.txt");
        bst.encryptWithSpecifiedHmac();*/
        BSTree bst=new BSTree("BST/bst_test_data.txt","BST/test_key.txt");
        bst.encryptWithoutSpecifiedHmac();
        
    }
    
    public String encryptWithSpecifiedHmac(){
        if(printConstruct)System.out.printf("pos n\t n2\t n4\t count\t start\t low\t upp\n");
        constructTree();
        if(printWholeTree)printTree(root);
        String res=printBFSTree();
        bc.calcHMAC(catstr);
        return res;
    }
    
    public String encryptWithoutSpecifiedHmac(){
        if(printConstruct)System.out.printf("pos n\t n2\t n4\t count\t start\t low\t upp\n");
        constructTree();
        if(printWholeTree)printTree(root);
        String res=printBFSTree();
        
        return res;
    }
    
    
    //Node class
    public class Node{
        public Node parent,left,right;
        public int low,upp;
        public Node(int low,int upp,Node p){
            this.low=low;this.upp=upp;this.parent=p;
        }
    }
    
    //Constructor
    //for testing purposes
    public BSTree(int[] arr, String hmac){
        msg=arr;
        msglen=msg.length;
        m=hmac;
        mlen=m.length();
        pos=0;
        root=new Node(0,msglen-1,null);
        catstr="";
        if(printHMACInput){
            System.out.println("HMAC: "+m);
        }
    }
    
    //for final use
    public BSTree(String msgFile,String keyFile){
        bc=new BCoder(keyFile);
        msg=bc.getMsgInput(msgFile);
        msglen=msg.length;
        pos=0;
        root=new Node(0,msglen-1,null);
        m=bc.calcHMAC(bc.getMsgStringInput(msgFile));
        mlen=m.length();
    }
    
    //for tesing purposes
    public BSTree(String msgFile,String keyFile, String hmacFile){
        bc=new BCoder(keyFile);
        msg=bc.getMsgInput(msgFile);
        msglen=msg.length;
        m=bc.getHmacInput(hmacFile);
        mlen=m.length();
        pos=0;
        root=new Node(0,msglen-1,null);
        catstr="";
        if(printHMACInput){
            System.out.println("HMAC: "+m);
        }
    }
   
    public void constructNode(Node node){
        if(node==null)return;
        int n,n2,n4,count,temp,addition,logn2,start;
        n=node.upp-node.low+1;
        if(n==1)return;
        if(n==2){
            node.left=new Node(node.low,node.low,node);
            node.right=new Node(node.upp,node.upp,node);
            return;
        }
        n2=(int)Math.ceil(n/2.0);
        n4=(int)Math.ceil(n/4.0);
        count=(int)Math.floor(Math.log(n2)/Math.log(2));
        //get start
        addition=0;
        for(int i=0;i<count;i++){
            temp=(m.charAt((pos)%mlen)=='0')?0:1;
            pos++;
            addition=2*addition+temp;
        }
        start=n4+addition;
        //split and construct
        if(printConstruct)
            System.out.printf("\n%d\t %d\t %d\t %d\t %d\t %d\t %d\t %d\t\n",pos,n,n2,n4,count,start,node.low,node.upp);
        Node leftNode=new Node(node.low,node.low+start-1,node);
        Node rightNode=new Node(node.low+start,node.upp,node);
        node.left=leftNode;node.right=rightNode;
    }
    
    public void constructTree(){
        LinkedList<Node> list=new LinkedList<Node>();
        list.add(root);
        Node node;
        while(list.size()>0){
            node=list.getFirst();
            constructNode(node);
            if(node.left!=null)list.addLast(node.left);
            if(node.right!=null)list.addLast(node.right);
            list.removeFirst();
        }
    }
    
    
    public void printTree(Node node){
        if(node==null)
            return;
        System.out.printf("[%d,%d]\n",node.low,node.upp);
        System.out.println("LEFT:\n");
        printTree(node.left);
        System.out.println("RIGHT:\n");
        printTree(node.right);
    }
    
    public String printBFSTree(){
        if(root==null)return "";
        String binstr="";
        String valuestr="";
        LinkedList<Node> list=new LinkedList<Node>();
        list.add(root);
        Node node;
        while(list.size()>0){
            node=list.getFirst();
            if(node.low==node.upp){
                binstr=binstr+"0";
                catstr=catstr+msg[node.low];
                valuestr=valuestr+msg[node.low]+"\n";
            }else{
                binstr=binstr+"1";
            }
            if(node.left!=null)list.addLast(node.left);
            if(node.right!=null)list.addLast(node.right);
            list.removeFirst();
        }
        System.out.print(binstr+"\n");
        System.out.print(valuestr);
        String res=binstr+"\n"+valuestr;
        return res;
    }
    
    //construct a new tree based on the existing tree
    /*public BSTree(String treeFile,String keyFile,boolean detect){
        bc=new BCoder(keyFile);
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
            
            file.close();
            in.close();
        }catch(java.io.IOException e){
            e.printStackTrace();
            System.exit(0);
        }
        //Now have treeStructure, values, and filecodestr. Need to construct tree from this.
        int count=0,maxcount=treeStructure.length()-1,childNum=0;
        root=new BSNode(0,maxcount,null);
        LinkedList<BSNode> list=new LinkedList<BSNode>();
        root.code="";
        list.add(root);
        BSNode node;
        
        while(count<=maxcount){
            node=list.getFirst();
            if(treeStructure.charAt(count)=='1'){//is a parent
                BSNode left=new BSNode(-1,-1,node);
                BSNode right=new BSNode(-1,-1,node);
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
    }*/
    
    
}