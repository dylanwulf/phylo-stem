package stemTrees;

public class TreeNodeCollapser {
    //private TreeNode node;
    
    public static void main(String[] args){
        //Input: one file of tree in Newick format, another file with all the sequences of the nodes.
        //Then traverse tree looking for nodes which have the same or extremely similar sequence as their parents
        //Erase the parent and put the equivalent child in its place
        //(actually what happens is the child is erased, the parent inherits the child's children, and the parent takes on the child's ID)
        //Keep going until no more changes are made
        
    }
    
    public static void collapse(TreeNode node){
        for (TreeNode o : node.getChildren()){
            collapse(o);
        }
        TreeNode parent = node.getParent();
        if (parent != null && parent.getName().equals(node.getName())){
            parent.setId(node.getId());
            node.remove2();
            System.out.println(node.getId() + "-" + node.getName() + " removed");
        }
    }

}
