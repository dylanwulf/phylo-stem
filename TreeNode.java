package stemTrees;

public class TreeNode {
    private String name;
    private char[] sequence;
    private long[] bitSequence;
    private TreeNode[] children;
    private TreeNode parent;
    private static int totalNodes = 0;
    private int id;
    private boolean hasBeenRemoved = false;
    
    public TreeNode(String name){
        this.name = name;
    }
    
    public TreeNode(String name, char[] sequence){
        id = totalNodes;
        totalNodes++;
        this.name = name;
        this.sequence = sequence;
        bitSequence = new long[sequence.length];
        for (int i = 0; i < sequence.length; i++){
            int bitIndex = sequence[i] - 'A';
            bitSequence[i] = (1L << bitIndex);
        }
    }
    
    public TreeNode(char[] sequence){
        this(new String(sequence), sequence);
    }
    
    public TreeNode(long[] bitSequence){
        id = totalNodes;
        totalNodes++;
        this.bitSequence = bitSequence;
        TreeNode[] emptyChildren = new TreeNode[0];
        this.children = emptyChildren;
    }
    
    public TreeNode(long[] bitSequence, int newId){
        id = newId;
        this.bitSequence = bitSequence;
    }
    
    public void setChildren(TreeNode[] children){
        this.children = children;
        for (TreeNode o : children)
            o.changeParent(this);
    }
    
    public TreeNode copy(){
        TreeNode newRoot = new TreeNode(bitSequence);
        newRoot.setId(id);
        if (children != null && children.length != 0){
            TreeNode[] newChildren = new TreeNode[children.length];
            for (int i = 0; i < children.length; i++){
                newChildren[i] = children[i].copy();
            }
            newRoot.setChildren(newChildren);
        }
        else{
            newRoot.setChildren(new TreeNode[0]);
        }
        return newRoot;
    }
    
    public String toNewick(){
        String out = "";
        if (this.hasAnyChildren()){
            out += "(";
            for (TreeNode o : children){
                out += o.toNewick() + ",";
            }
            out += ")";
        }
        out += "N" + id;
        if (parent != null){
            //out += ":" + distanceToParent();
        }
        else{
            out = out.replaceAll(",\\)", "\\)");
            out += ";";
        }
        return out;
    }
    
    public void changeParent(TreeNode newParent){
        this.parent = newParent;
    }
    
    public void replaceChild(TreeNode newOrphan, TreeNode newChild){
        for (int i = 0; i < children.length; i++){
            if (children[i] == newOrphan){
                children[i] = newChild;
                children[i].changeParent(this);
            }
        }
    }
    
    public void promote(){
        if (!hasBeenRemoved()){
            TreeNode[] parentsChildren = parent.getChildren();

            //new array to contain this node's children and parent's children except for this object
            TreeNode[] parentsNewChildren = new TreeNode[children.length + parentsChildren.length];

            //add this node's children to new array
            for (int i = 0; i < children.length; i++){
                parentsNewChildren[i] = children[i];
            }

            //adds all of parent's children to the new array except for this object
            for (int i = 0; i < parentsChildren.length; i++){
                parentsNewChildren[children.length + i] = parentsChildren[i];
            }
            
            //moves this object to the end of the array so it can easily be discarded
            for (int i = 0; i < parentsNewChildren.length - 1; i++){
                if (parentsNewChildren[i] == this){
                    parentsNewChildren[i] = parentsNewChildren[i + 1];
                    parentsNewChildren[i + 1] = this;
                }
            }
            
            TreeNode[] parentsNewNewChildren = new TreeNode[parentsNewChildren.length - 1];
            for (int i = 0; i < parentsNewNewChildren.length; i++)
                parentsNewNewChildren[i] = parentsNewChildren[i];

            //Finally hand them off to the parent
            parent.setChildren(parentsNewNewChildren);
            parent.setBitSequence(bitSequence);
            parent.setId(this.getId());
        }
    }
    
    public double distanceToParent(){
        double distance = 0;
        if (parent != null){
            char[] parentSequence = parent.getSequence();
            for (int i = 0; i < sequence.length; i++){
                if (sequence[i] != parentSequence[i]){
                    distance += 1;
                }
            }
        }
        if (distance == 0)
            distance = 0.3;
        return distance;
    }
    
    public boolean removeChild(int nodeId){
        for (int i = 0; i < children.length; i++){
            if (children[i].getId() == nodeId){
                children[i] = null;
                for (int j = i; j < children.length - 1; j++){
                    children[j] = children[j + 1];
                }
                TreeNode[] newChildren = new TreeNode[children.length - 1];
                for (int m = 0; m < newChildren.length; m++){
                    newChildren[m] = children[m];
                }
                children = newChildren;
                return true;
            }
        }
        return false;
    }
    
    public boolean canBePromoted(){
        if (parent == null)
            return false;
        long[] parentBitSeq = parent.getBitSequence();
        boolean out = true;
        for (int i = 0; i < bitSequence.length; i++){
            if ((parentBitSeq[i] & bitSequence[i]) == 0){
                out = false;
                i = bitSequence.length;
            }
        }
        return out;
    }
    
    public boolean hasBeenRemoved(){
        return hasBeenRemoved;
    }
    
    public TreeNode[] getChildren(){
        return children;
    }
    
    public TreeNode getParent(){
        return parent;
    }
    
    public char[] getSequence(){
        return sequence;
    }
    
    public void setSequence(char[] sequence){
        this.sequence = sequence;
        bitSequence = new long[sequence.length];
        for (int i = 0; i < sequence.length; i++){
            int bitIndex = sequence[i] - 'A';
            bitSequence[i] = (1L << bitIndex);
        }
    }
    
    public void setBitSequence(long[] bitSequence){
        this.bitSequence = bitSequence;
    }
    
    public long[] getBitSequence(){
        return bitSequence;
    }
    
    public static int getTotalNodes(){
        return totalNodes;
    }
    
    public void setId(int newId){
        id = newId;
    }
    
    public int getId(){
        return id;
    }
    
    public String getName(){
        return this.name;
    }
    
    public boolean hasAnyChildren(){
        return !(children == null || children.length == 0);
    }
    
    public boolean isLeaf(){
        return !hasAnyChildren();
    }

}
