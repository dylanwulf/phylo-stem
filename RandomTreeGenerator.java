package stemTrees;
import java.util.Random;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

public class RandomTreeGenerator {
    static int howManySequences;
    static int numberOfFeatures;
    static int optionsPerFeature;
    static TreeNode rootNode;
    static final int maxNodes = 5000; //beyond this point the computer may or may not explode
    static double fertility = 100; //Chance of creating children (out of 100)
    static final double fertilityChanger = 15.0; //How much the fertility goes down every new level
    static double[] childChances = {30, 40, 20, 10}; //the chance (out of 100) of having 1, 2, 3, or 4 children; you can modify this to create more or less than 4 children
    static final int[] modChances = {80, 10, 10}; //chances of replacement, insertion, deletion (respectively)
    static final double howMuchMod = 0.22; //determines the max number of modifications to make (0.25 = 25% of the sequence length)
    static int nodesCreated = 0;
    static int leavesCreated = 0; //These two are pretty self explanatory

    public static void main(String[] args) throws FileNotFoundException {
        Scanner userIn = new Scanner(System.in);
        //System.out.println("How many sequences would you like?");
        //howManySequences = getPosInt(userIn);
        System.out.println("How many features in each sequence?");
        numberOfFeatures = getPosInt(userIn);
        System.out.println("How many options for each feature?");
        optionsPerFeature = getPosInt(userIn);
        System.out.println("Which option would you like for the multiple alignment?");
        System.out.println("1. Leaves only.\n2. All nodes.\n3. All leaves and some randomly selected internal nodes.");
        int alignmentSelection = getPosInt(userIn);
        int internalChance = 0;
        if (alignmentSelection == 3){
            System.out.println("About what percentage of internal nodes should be included?");
            internalChance = getPosInt(userIn);
        }
        userIn.close();
        rootNode = makeRootNode();
        makeChildren(rootNode);
        System.out.println(nodesCreated + " nodes created; " + leavesCreated + " leaves created.");
        PrintWriter treeOut = new PrintWriter("treeout.nwk");
        PrintWriter alignmentOut = new PrintWriter("alignmentout.msa");
        PrintWriter fullAlignmentOut = new PrintWriter("fullAlignmentOut.msa");
        treeOut.println(toNewick(rootNode));
        treeOut.close();
        switch(alignmentSelection){
        case 1:
            alignmentOut.println(alignmentLeavesOnly(rootNode));
            break;
        case 2:
            alignmentOut.print(alignmentEverything(rootNode));
            break;
        case 3:
            alignmentOut.print(alignmentSomeInternal(rootNode, internalChance));
            break;
        default:
            alignmentOut.print(alignmentEverything(rootNode));
        }
        fullAlignmentOut.print(alignmentEverything(rootNode));
        alignmentOut.close();
        fullAlignmentOut.close();
        
    }
    
    public static String toNewick(TreeNode node){
        String out = "";
        if (node.hasAnyChildren()){
            out += "(";
            for (TreeNode o : node.getChildren()){
                out += toNewick(o) + ",";
            }
            out += ")";
        }
        out += "N" + node.getId();
        if (node != rootNode)
            out += ":" + node.distanceToParent();
        else{
            out = out.replaceAll(",\\)", "\\)");
            out += ";";
        }
        return out;
    }
    
    public static String alignmentLeavesOnly(TreeNode node){
        String out = "";
        if (node.hasAnyChildren()){
            for (TreeNode o : node.getChildren()){
                out += alignmentLeavesOnly(o);
            }
        }
        else{
            String id = "N" + Integer.toString(node.getId());
            int spaces = 10 - id.length();
            for (int i = 0; i < spaces; i++){
                id += " ";
            }
            out += id + node.getName() + "\n";
        }
        if (node == rootNode){
            String[] outArray = out.split("\n");
            shuffleArray(outArray);
            out = "";
            out += outArray.length;
            int spaces2 = 10 - out.length();
            for (int i = 0; i < spaces2; i++)
                out += " ";
            out += numberOfFeatures + "\n";
            for (String o : outArray){
                out += o + "\n";
            }
        }
        return out;
    }
    
    public static String alignmentEverything(TreeNode node){
        String out = "";
        if (node.hasAnyChildren()){
            for (TreeNode o : node.getChildren()){
                out += alignmentEverything(o);
            }
        }
        String id = "N" + Integer.toString(node.getId());
        int spaces = 10 - id.length();
        for (int i = 0; i < spaces; i++){
            id += " ";
        }
        out += id + node.getName() + "\n";
        if (node == rootNode){
            String[] outArray = out.split("\n");
            shuffleArray(outArray);
            out = "";
            out += outArray.length;
            int spaces2 = 10 - out.length();
            for (int i = 0; i < spaces2; i++)
                out += " ";
            out += numberOfFeatures + "\n";
            for (String o : outArray){
                out += o + "\n";
            }
        }
        return out;
    }
    
    /**
     * This produces the multiple alignment for all the leaf nodes and some randomly selected internal nodes and shuffles them.
     * @param node root node of the tree
     * @param internalChance chance for an internal node to be included
     * @return String containing all sequences
     */
    public static String alignmentSomeInternal(TreeNode node, int internalChance){
        String out = "";
        Random randomGen = new Random();
        String id = "N" + Integer.toString(node.getId());
        int spaces = 10 - id.length();
        for (int i = 0; i < spaces; i++){
            id += " ";
        }
        if (node.hasAnyChildren()){
            for (TreeNode o : node.getChildren()){
                out += alignmentSomeInternal(o, internalChance);
            }
            int includeChance = randomGen.nextInt(100);
            if (includeChance < internalChance){
                out += id + node.getName() + "\n";
            }
        }
        else{
            out += id + node.getName() + "\n";
        }
        if (node == rootNode){
            String[] outArray = out.split("\n");
            shuffleArray(outArray);
            out = "";
            out += outArray.length;
            int spaces2 = 10 - out.length();
            for (int i = 0; i < spaces2; i++)
                out += " ";
            out += numberOfFeatures + "\n";
            for (String o : outArray){
                out += o + "\n";
            }
        }
        return out;
    }
    
    public static <T> void shuffleArray(T[] array){
        Random shuffler = new Random();
        for (int i = 0; i < array.length; i++){
            int r = i + shuffler.nextInt(array.length - i);
            T temp = array[i];
            array[i] = array[r];
            array[r] = temp;
        }
    }
    
    /**
     * This is a recursive function that makes all the children for the tree.
     * @param node The root node of the tree
     */
    public static void makeChildren(TreeNode node){
        nodesCreated += 1;
        fertility -= fertilityChanger;
        
        randomlyCreateChildren(node);
        if (node.hasAnyChildren()){
            for (TreeNode o : node.getChildren()){
                makeChildren(o);
            }
        }
        fertility += fertilityChanger;
    }
    
    public static void randomlyCreateChildren(TreeNode node){
        Random randomGenerator = new Random();
        int howManyChildren = 0;
        boolean willMakeChildren  = false;
        int chance = randomGenerator.nextInt(100);
        if (nodesCreated < maxNodes && (chance < fertility || node == rootNode)){
            willMakeChildren = true;
        }
        if (willMakeChildren){
            int chance2 = randomGenerator.nextInt(100);
            int chanceToCreateAnother = 100;
            for (int i = 0; i < childChances.length; i++){
                if (chance2 < chanceToCreateAnother){
                    howManyChildren += 1;
                    chanceToCreateAnother -= childChances[i];
                }
            }
        }
        else
            leavesCreated++;
        TreeNode[] babyChilluns = new TreeNode[howManyChildren];
        for (int i = 0; i < babyChilluns.length; i++){
            char[] newSequence = randomlyChangeSequence(node.getSequence());
            babyChilluns[i] = new TreeNode(newSequence);
        }
        node.setChildren(babyChilluns);
    }
    
    
    /**
     * Randomly changes some of the characters in the sequence. Does not modify 'original'.
     * @param original input sequence
     * @return new sequence which is the input sequence but randomly modified.
     */
    public static char[] randomlyChangeSequence(char[] original){
        Random randomGenerator = new Random();
        char[] out = new char[original.length];
        for (int i = 0; i < out.length; i++){
            out[i] = original[i];
        }
        int maxMods = (int) Math.ceil(howMuchMod * out.length);
        int howManyMods = randomGenerator.nextInt(maxMods + 1);
        for (int i = 0; i < howManyMods; i++){
            int whichOp = randomGenerator.nextInt(100);
            if (whichOp >= 0 && whichOp < modChances[0])
                randomlyReplace(out);
            else if (whichOp >= modChances[0] && whichOp < modChances[0] + modChances[1])
                randomlyInsert(out);
            else
                randomlyDelete(out);
        }
        return out;
    }
    
    public static void randomlyReplace(char[] in){
        Random randomGenerator = new Random();
        int whichIndex;
        int count = 0;
        do{
            whichIndex = randomGenerator.nextInt(in.length);
            count++;
        } while (in[whichIndex] == '~' && count < numberOfFeatures * 2);
        char replacement = (char) (randomGenerator.nextInt(optionsPerFeature) + 'A');
        in[whichIndex] = replacement;
    }
    
    //Inserts a random character in the leftmost spot that contains a '~'
    //Replaces a random character if the whole sequence is full.
    //There is probably a better way to simulate insertions but this is good enough for now
    public static void randomlyInsert(char[] in){
        Random randomGenerator = new Random();
        int insertIndex = -1;
        for (int i = 0; i < in.length; i++){
            if (in[i] == '~'){
                insertIndex = i;
                i = in.length; //break the loop
            }
        }
        if (insertIndex >= 0){
            char insertion = (char) (randomGenerator.nextInt(optionsPerFeature) + 'A');
            in[insertIndex] = insertion;
        }
        else{
            randomlyReplace(in);
        }
    }
    
    public static void randomlyDelete(char[] in){
        Random randomGenerator = new Random();
        int whichIndex;
        int count = 0;
        do{
            whichIndex = randomGenerator.nextInt(in.length);
            count++;
        } while (in[whichIndex] == '~' && count < numberOfFeatures * 2);
        in[whichIndex] = '~';
    }
    
    public static TreeNode makeRootNode(){
        Random randomGenerator = new Random();
        //makes a char array of length 'numberOfFeatures' and puts 'A' in every location
        char[] startingSequence = new char[numberOfFeatures];
        for (int i = 0; i < startingSequence.length; i++){
            startingSequence[i] = 'A';
        }
        
        //"Deletes" one random character in the starting sequence by replacing it with a '~'
        startingSequence[randomGenerator.nextInt(startingSequence.length)] = '~';
        
        return new TreeNode(startingSequence);
    }
    
    public static int getPosInt(Scanner userIn){
        int in = 1;
        boolean tryAgain = true;
        while (tryAgain){
            int test;
            tryAgain = false;
            if (!userIn.hasNextInt()){
                System.out.println("Please only enter a positive integer.");
                userIn.next();
                tryAgain = true;
            }
            else if ((test = userIn.nextInt()) <= 0){
                System.out.println("Please only enter a positive integer.");
                tryAgain = true;
            }
            else
                in = test;
        }
        return in;
    }
}
