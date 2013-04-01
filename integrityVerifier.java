import javax.swing.*; 
import java.util.*; 
import java.io.*; 
import java.awt.*;
import java.awt.event.*; 
import javax.swing.border.*;


public class integrityVerifier extends JFrame implements ActionListener{
    // The preferred size of the editor
    final int PREFERRED_WIDTH = 1000;
    final int PREFERRED_HEIGHT = 700;
    
    int mode=2;
    
    //Display Panels
    JPanel _settings=new JPanel();
    
    JScrollPane _rightTextPane=new JScrollPane();
    JTextArea _rightTextarea;
    
    JScrollPane _upTextPane=new JScrollPane();
    JTextArea _upTextarea;
    
    /*Edit Panel*/
    JPanel upper=new JPanel();
    
    JPanel editor=new JPanel();
    //menus
    JButton part1a=new JButton("Balanced Search Tree");
    JButton part1b=new JButton("Huffman Tree");
    //JButton part2=new JButton("Part-2");
    JButton help=new JButton("Help");
    
    
    /*Setting Panel*/
    JLabel currentTree=new JLabel("Huffman Tree");
    JLabel dummy=new JLabel("");
    
    JPanel box1=new JPanel();
    TitledBorder border1=BorderFactory.createTitledBorder("Huffman Tree Construction");
    
    JLabel msgFileLabel=new JLabel("Message File:");
    JTextField msgFileTextField=new JTextField(30);
    JButton msgFileButton=new JButton("Open");
    
    JLabel keyFileLabel=new JLabel("Key File:");
    JTextField keyFileTextField=new JTextField(30);
    JButton keyFileButton=new JButton("Open");
    
    JLabel out1FileLabel=new JLabel("Output File:");
    JTextField out1FileTextField=new JTextField(30);
    
    
    JButton saveButton1=new JButton("Construct the Huffman Tree!");
    
    JPanel box2=new JPanel();
    TitledBorder border2=BorderFactory.createTitledBorder("Huffman Tree Verification");
    
    JLabel treeFileLabel=new JLabel("Tree File:");
    JTextField treeFileTextField=new JTextField(30);
    JButton treeFileButton=new JButton("Open");
    
    JLabel out2FileLabel=new JLabel("Output File:");
    JTextField out2FileTextField=new JTextField(30);
    
    
    JButton saveButton2=new JButton("Verify the Huffman Tree file!");
    
    /*Huffman Section*/
    String huff_msgFile;
    String huff_keyFile;
    String huff_outFile1="";
    String huff_treeFile;
    String huff_outFile2="";
    
    
    
    
    
    
    public integrityVerifier(){
        //set frame properties
        setTitle("Integrity Verifier - part 1b - Huffman Tree");
        setSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// Close when closed.For reals.
        
        /*Setting Panel*/
        _settings.setPreferredSize(new Dimension(400, 300));
        _settings.add(currentTree);
        _settings.add(box1);
        _settings.add(saveButton1);
        _settings.add(box2);
        _settings.add(saveButton2);
        

        box1.setBorder(border1);
        box1.setLayout(new SpringLayout());
        box1.setPreferredSize(new Dimension(370, 140));
        
        
        box2.setBorder(border2);
        box2.setLayout(new SpringLayout());
        box2.setPreferredSize(new Dimension(370, 110));
        
        box1.add(msgFileLabel);
        box1.add(msgFileTextField);
        box1.add(msgFileButton);
        box1.add(keyFileLabel);
        box1.add(keyFileTextField);
        box1.add(keyFileButton);
        box1.add(out1FileLabel);
        box1.add(out1FileTextField);
        box1.add(dummy);
        SpringUtilities.makeCompactGrid(box1, 3, 3, //rows, cols
                                        6, 6,        //initX, initY
                                        6, 6);       //xPad, yPad
        
        box2.add(treeFileLabel);
        box2.add(treeFileTextField);
        box2.add(treeFileButton);
        box2.add(out2FileLabel);
        box2.add(out2FileTextField);
        box2.add(dummy);
        SpringUtilities.makeCompactGrid(box2, 2, 3, //rows, cols
                                        6, 6,        //initX, initY
                                        6, 6);       //xPad, yPad
        
        /*Text area*/
        _rightTextarea=new JTextArea("", 20, 20);
        _rightTextarea.setEditable(false);
        _rightTextPane.getViewport().add(_rightTextarea);
        
        _upTextarea=new JTextArea("Welcome to Integrity Verifier!\nPlease start by selecting a part from 'Project' menu.", 4, 20);
        _upTextarea.setEditable(false);
        _upTextPane.getViewport().add(_upTextarea);
        _upTextPane.setPreferredSize(new Dimension(700, 68));
        
        //add components to frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(upper,BorderLayout.NORTH);
        getContentPane().add(_settings,BorderLayout.WEST);
        getContentPane().add(_rightTextPane,BorderLayout.CENTER);
        
        //add actionlistener to the menuitems
        part1a.addActionListener(this);
        part1b.addActionListener(this);
        //part2.addActionListener(this);
        help.addActionListener(this);
        msgFileButton.addActionListener(this);
        keyFileButton.addActionListener(this);
        treeFileButton.addActionListener(this);
        saveButton1.addActionListener(this);
        saveButton2.addActionListener(this);
        
        //add menuitems to menus
        editor.add(part1a);
        editor.add(part1b);
        editor.add(help);
      
        
        //set up the upper part
        upper.add(editor);
        upper.add(_upTextPane);
        upper.setPreferredSize(new Dimension(700, 133));
        
        changeTo1A();
    }
    
    public static void main(String[] args) { 
        integrityVerifier intver = new integrityVerifier(); 
        intver.setVisible(true);
    }  
    
    public void actionPerformed(ActionEvent e){
        Object source=e.getSource();
        if(source==msgFileButton){
            
            String[] res=chooseFile("Please choose message file.");
            msgFileTextField.setText(res[0]);
            huff_msgFile=res[1];
        }else if(source==keyFileButton){
            
            String[] res=chooseFile("Please choose key file.");
            keyFileTextField.setText(res[0]);
            huff_keyFile=res[1];
        }else if(source==treeFileButton){
            
            String[] res=chooseFile("Please choose tree file.");
            treeFileTextField.setText(res[0]);
            huff_treeFile=res[1];
        }else if(source==saveButton1){
            if(mode==1){
                BSTree bst=new BSTree(huff_msgFile,huff_keyFile);
                String res=bst.encryptWithoutSpecifiedHmac();
                _rightTextarea.setText(res);
                _upTextarea.setText("Tree is successfully constructed.\nPlease look at the output file or textfield below to see the result.");
                printStringToFile(res,out1FileTextField.getText());
            }else if(mode==2){
                HCTree hct=new HCTree(huff_msgFile,huff_keyFile);
                String res=hct.buildTreeFromMessage();
                _rightTextarea.setText(res);
                _upTextarea.setText("Tree is successfully constructed.\nPlease look at the output file or textfield below to see the result.");
                printStringToFile(res,out1FileTextField.getText());
            }
            
        }else if(source==saveButton2){
            if(mode==1){
                _rightTextarea.setText("Not supported Yet.");
            }else if(mode==2){
                HCTree hct=new HCTree(huff_msgFile,huff_keyFile,huff_treeFile);
                String res=hct.verify();
                _rightTextarea.setText(res);
                _upTextarea.setText("Tree is successfully verified.\nPlease look at the output file or textfield below to see the result.");
                printStringToFile(res,out2FileTextField.getText());
            }
        }else if(source==part1a){
            changeTo1A();
        }else if(source==part1b){
            changeTo1B();
        }else if(source==help){
            changeToHelp();
            
        }
        
    }
    
    public String[] chooseFile(String prompt){
        JFileChooser fc=new JFileChooser(System.getProperty("user.dir"));
        fc.setDialogTitle(prompt);
        fc.setDragEnabled(false);
        
        String s[]=new String[2];
        s[0]="";s[1]="";
        
        int val=fc.showOpenDialog(this);
        if(val==JFileChooser.CANCEL_OPTION || val==JFileChooser.ERROR_OPTION){
            return s;
        }
        
        File file = fc.getSelectedFile();
        s[0]=file.getName();
        s[1]=file.getPath();
        return s;
    }
    
    public boolean printStringToFile(String str, String filename){
        if(filename.length()==0)
            return false;
        try{
            PrintWriter out = new PrintWriter(filename);
            out.print(str);
            out.close();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public void changeTo1A(){

        mode=1;
        setTitle("Integrity Verifier - part 1a - Balanced Search Tree");
        _upTextarea.setText("Balanced Search Tree.");
        border1.setTitle("Balanced Search Tree Construction"); 
        border2.setTitle("Balanced Search Tree Verification");
        
        currentTree.setText("Balanced Search Tree");
        msgFileLabel.setText("Data File:");
        saveButton1.setText("Construct the Balanced Search Tree!");
        saveButton2.setText("Verify the Balanced Search Tree file!");
        
    }
    
    public void changeTo1B(){
       
        
        
        mode=2;
        setTitle("Integrity Verifier - part 1b - Huffman Tree");
        _upTextarea.setText("Huffman Tree.");
        border1.setTitle("Huffman Tree Construction"); 
        border2.setTitle("Huffman Tree Verification");
        
        currentTree.setText("Huffman Tree");
        msgFileLabel.setText("Message File:");
        saveButton1.setText("Construct the Huffman Tree!");
        saveButton2.setText("Verify the Huffman Tree file!");
        
    }
    
    public void changeToHelp(){
        
        
        setTitle("Integrity Verifier - Help");
        _upTextarea.setText("Help:\nCall 123-456-789 if you have any questions!!");
        _rightTextarea.setText("Help:\n1. ***\n 2. ###\n 3. @@@\n");
    }
    
}