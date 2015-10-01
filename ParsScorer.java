package stemTrees;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class ParsScorer {

    /**
     * The main method which takes the names of the input files in as standard input.
     * @param args command line arguments
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException{
        String newickFileName;
        String alignmentFileName;
        TreeNode tree;
        Scanner userIn = new Scanner(System.in);
        System.out.println("Please enter the name of the newick tree file.");
        newickFileName = userIn.nextLine();
        System.out.println("Please enter the name of the alignment file.");
        alignmentFileName = userIn.nextLine();
        userIn.close();
        tree = getTree(newickFileName);
        putSequencesIntoNodes(alignmentFileName, tree);
        System.out.println("Pars score: " + getParsimonyScore(tree));

    }
    
    /**
     * A magical recursive method which finds the parsimony score of the tree 
     * (after the tree has been interpreted from the Newick and sequence files)
     * @param node a reference to the root node of the tree which you want to score
     * @return the parsimony score
     */
    static int getParsimonyScore(TreeNode node){
        if (!node.hasAnyChildren()){
            return 0;
        }
        else{
            int childrenTotal = 0;
            for (TreeNode o : node.getChildren()){
                childrenTotal += getParsimonyScore(o);
                childrenTotal += o.distanceToParent();
            }
            return childrenTotal;
        }
    }
    
    /**
     * This method reads all the information from the alignment file and then calls traverseAndSetSeq(TreeNode, char[][], String[])
     * which puts all the sequences into the correct nodes in the tree structure.
     * @param alignmentFileName name of the text file which contains the sequence alignments
     * @param tree reference to the root node of the tree structure
     * @throws FileNotFoundException
     */
    static void putSequencesIntoNodes(String alignmentFileName, TreeNode tree) throws FileNotFoundException{
        File alignmentFile = new File(alignmentFileName);
        Scanner fileScanner = new Scanner(alignmentFile);
        char[][] sequences;
        String[] sequenceNames;
        int taxa = fileScanner.nextInt();
        int sequenceLength = fileScanner.nextInt();
        sequences = new char[taxa][sequenceLength];
        sequenceNames = new String[taxa];
        for (int i = 0; i < taxa; i++){
            sequenceNames[i] = fileScanner.next();
            sequences[i] = fileScanner.next().toCharArray();
        }
        fileScanner.close();
        traverseAndSetSeq(tree, sequences, sequenceNames);
    }
    
    /**
     * This is a magical recursive method which takes all the sequences from the arrays and puts them into 
     * their corresponding nodes in the tree.
     * @param node the root node of the tree
     * @param sequences the sequences which will be put into the nodes
     * @param sequenceNames the names which are used to match each node to its own sequence
     */
    static void traverseAndSetSeq(TreeNode node, char[][] sequences, String[] sequenceNames){
        int sequenceIndex = 0;
        
        //Look through all the sequence names for one that matches node's name. 
        //the sequence itself will be at the same index in sequences[][]
        for (int i = 0; i < sequenceNames.length; i++){
            if (node.getName().equals(sequenceNames[i])){
                sequenceIndex = i;
                i = sequenceNames.length; //break the loop when we find the value
            }
        }
        
        //set the sequence for this node and then call this method for its children
        node.setSequence(sequences[sequenceIndex]);
        if (node.hasAnyChildren()){
            for (TreeNode o : node.getChildren()){
                traverseAndSetSeq(o, sequences, sequenceNames);
            }
        }
    }
    
    /**
     * This method gets the tree's structure from the Newick file using the NewickInterpreter class
     * @param fileName name of the text file in Newick format
     * @return reference to the root of the tree structure
     * @throws FileNotFoundException
     */
    static TreeNode getTree(String fileName) throws FileNotFoundException{
        File treeFile = new File(fileName);
        Scanner fileScanner = new Scanner(treeFile);
        String newickString = fileScanner.nextLine();
        fileScanner.close();
        NewickInterpreter interp = new NewickInterpreter(newickString);
        return interp.interpret();
    }
    

}
