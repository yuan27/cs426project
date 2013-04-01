public class stringComparator{
    public void compare(String s1, String s2){
        System.out.println(s1.equals(s2));
        int len1=s1.length(),len2=s2.length();
        if(len1!=len2){
            System.out.println("Lengths are not the same.");
            return;
        }
        for(int i=0;i<len1;i++){
            if(s1.charAt(i)!=s2.charAt(i)){
                System.out.println(i); 
            }
        }
        System.out.println();  
    }
}