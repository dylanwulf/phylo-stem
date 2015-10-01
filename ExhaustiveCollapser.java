package stemTrees;
import java.util.LinkedList;
import java.util.Iterator;

//       change TreeNode.distanceToParent() to work with bit sequences instead of char sequences
/**
 * This class exhaustively finds all possible permutations of promoting nodes as high as they can go. 
 * @author Dylan
 *
 */
public class ExhaustiveCollapser {
private static TreeNode root;

    /**
     * This main method is only here to test out the other methods and make sure they work correctly.
     * If you want to use this class with a tree you already have, please call permutateNodePromotions(TreeNode root)
     * @param args command line arguments
     */
    public static void main(String[] args) {
        TreeNode[] noChildren = {};
        long[] rootseq = {0b01L, 0b1010L, 0b101L};
        TreeNode rootNode = new TreeNode(rootseq, 0);
        long[] N1seq = {0b1L, 0b10L, 0b1L};
        TreeNode N1 = new TreeNode(N1seq, 1);
        N1.setChildren(noChildren);
        long[] N2seq = {0b1L, 0b1000L, 0b100L};
        TreeNode N2 = new TreeNode(N2seq, 2);
        TreeNode[] rootChildren = {N1, N2};
        rootNode.setChildren(rootChildren);
        long[] N3seq = {0b1101L, 0b1000L, 0b100L};
        TreeNode N3 = new TreeNode(N3seq, 3);
        long[] N8seq = {0b1L, 0b1001L, 0b100L};
        TreeNode N8 = new TreeNode(N8seq, 8);
        TreeNode[] N2children = {N3, N8};
        N2.setChildren(N2children);
        long[] N4seq = {0b100L, 0b1000L, 0b100L};
        TreeNode N4 = new TreeNode(N4seq, 4);
        N4.setChildren(noChildren);
        long[] N5seq = {0b1001L, 0b1100L, 0b100L};
        TreeNode N5 = new TreeNode(N5seq, 5);
        TreeNode[] N3children = {N4, N5};
        N3.setChildren(N3children);
        long[] N6seq = {0b1L, 0b100L, 0b100L};
        TreeNode N6 = new TreeNode(N6seq, 6);
        N6.setChildren(noChildren);
        long[] N7seq = {0b1000L, 0b1000L, 0b100L};
        TreeNode N7 = new TreeNode(N7seq, 7);
        N7.setChildren(noChildren);
        TreeNode[] N5children = {N6, N7};
        N5.setChildren(N5children);
        long[] N9seq = {0b1L, 0b1000L, 0b100L};
        TreeNode N9 = new TreeNode(N9seq, 9);
        N9.setChildren(noChildren);
        long[] N10seq = {0b1L, 0b1L, 0b100L};
        TreeNode N10 = new TreeNode(N10seq, 10);
        N10.setChildren(noChildren);
        TreeNode[] N8children = {N9, N10};
        N8.setChildren(N8children);
        root = rootNode;
        System.out.println("Original: " + root.toNewick());
        LinkedList<String> list = permutateNodePromotions(rootNode);
        for (String o : list){
           System.out.println(o);
        }
        System.out.println("Original: " + root.toNewick());
    }
    
    /**
     * This method exhaustively finds all possible permutations of promoting nodes as high as they can go.
     * @param rootNode a reference to the root node of the tree you want to collapse. This tree will not be changed.
     * @return a Linked List of strings, each element containing one tree in Newick format.
     */
    public static LinkedList<String> permutateNodePromotions(TreeNode rootNode){
        root = rootNode;
        LinkedList<String> newickList = new LinkedList<String>();
        TreeNode[] promotableNodes = getArrayOfPromotableNodes(rootNode);
        permutateNodePromotions(promotableNodes, newickList);
        return newickList;
    }
    
    /**
     * This is the magic method that does a bunch of recursive mumbo jumbo to find all the different trees.
     * @param promotableNodes an array of nodes in the tree which can be promoted at least once.
     * @param newickList the linked list of strings into which the results will be added.
     */
    private static void permutateNodePromotions(TreeNode[] promotableNodes, LinkedList<String> newickList){
        if (promotableNodes.length > 0){
            TreeNode originalTree = root.copy();
            for (int i = 0; i < promotableNodes.length; i++){
                promote(promotableNodes[i]);
                permutateNodePromotions(getArrayOfPromotableNodes(root), newickList);
                root = originalTree.copy();
                promotableNodes = getArrayOfPromotableNodes(root);
            }
        }
         else{
            newickList.add(root.toNewick());
        }
    }
    
    /**
     * This "promotes" a node up as high as it can go. 
     * What actually happens in each iteration of the while loop is this: 
     * the parent node takes on all characteristics (ID number, sequence, etc) of 'node' and
     * it adopts all the children belonging to 'node'. Then the node 'node' is removed.
     * @param node the node to be promoted
     */
    private static void promote(TreeNode node){
        TreeNode parent;
        while(node.canBePromoted()){
            parent = node.getParent();
            node.promote();
            node = parent;
        }
    }
    
    /**
     * This method gives you all of the nodes in the tree that can be "promoted".
     * All this method actually does is call listPromotableNodes(TreeNode) and turn the result into an array.
     * @param rootNode reference to the root node of the tree
     * @return an array of promotable nodes
     */
    private static TreeNode[] getArrayOfPromotableNodes(TreeNode rootNode){
        LinkedList<TreeNode> list = listPromotableNodes(rootNode);
        TreeNode[] out = new TreeNode[list.size()];
        Iterator<TreeNode> listIt = list.iterator();
        for (int i = 0; i < out.length; i++){
            out[i] = listIt.next();
        }
        return out;
    }
    
    /**
     * This method gives you all of the nodes in the tree that can be "promoted".
     * All this method actually does is call listPromotableNodes(TreeNode, LinkedList<TreeNode>) and return the result.
     * @param rootNode root node of the tree
     * @return a Linked List of TreeNodes which can be promoted in the tree
     */
    private static LinkedList<TreeNode> listPromotableNodes(TreeNode rootNode){
        LinkedList<TreeNode> list = new LinkedList<TreeNode>();
        listPromotableNodes(rootNode, list);
        return list;
    }
    
    /**
     * This method traverses the tree 'node' and puts all promotable nodes into 'list'.
     * @param node the root node of the tree you are examining
     * @param list the Linked List of TreeNodes into which the results will go
     */
    private static void listPromotableNodes(TreeNode node, LinkedList<TreeNode> list){
        if (node.isLeaf()){
            if (node.canBePromoted()){
                list.add(node);
            }
        }
        else{
            for (TreeNode o : node.getChildren()){
                listPromotableNodes(o, list);
            }
        }
    }

}
