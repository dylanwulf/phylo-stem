package stemTrees;

public class NewickInterpreter {
    private String newickString;
    
    public NewickInterpreter(String newickString){
        this.newickString = newickString;
    }
    
    public TreeNode interpret(){
        return interpret(newickString);
    }
    
    private TreeNode interpret(String nStr){
        if (nStr.contains(")")){
            TreeNode[] children;
            String name = nStr.substring(nStr.lastIndexOf(")") + 1, nStr.length());
            String parentRemoved = nStr.substring(1, nStr.lastIndexOf(")"));
            String[] childrenStrings = splitAtOutsideCommas(parentRemoved);
            children = new TreeNode[childrenStrings.length];
            for (int i = 0; i < children.length; i++){
                children[i] = interpret(childrenStrings[i]);
            }
            TreeNode node = new TreeNode(name);
            node.setChildren(children);
            return node;
        }
        else{
            return new TreeNode(nStr);
        }
    }
    
    private String[] splitAtOutsideCommas(String in){
        String[] out;
        
        //Find out how many outside commas there are so we know how big
        //to make out[]
        int parenthesesCounter = 0;
        int commas = 0;
        for (int i = 0; i < in.length(); i++){
            if (in.charAt(i) == '(')
                parenthesesCounter++;
            else if (in.charAt(i) == ')')
                parenthesesCounter--;
            else if (in.charAt(i) == ',' && parenthesesCounter == 0)
                commas++;
        }
        out = new String[commas + 1];
        
        //Split string at outside commas and store the substrings in out[]
        int outIndexCounter = 0;
        parenthesesCounter = 0;
        int previousCommaIndex = -1;
        for (int i = 0; i < in.length(); i++){
            if (in.charAt(i) == '(')
                parenthesesCounter++;
            else if (in.charAt(i) == ')')
                parenthesesCounter--;
            else if (in.charAt(i) == ',' && parenthesesCounter == 0){
                out[outIndexCounter] = in.substring(previousCommaIndex + 1, i);
                previousCommaIndex = i;
                outIndexCounter++;
            }
        }
        
        //This gets all the stuff after the last comma
        out[outIndexCounter] = in.substring(previousCommaIndex + 1, in.length());
        
        return out;
    }

}
