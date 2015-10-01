package stemTrees;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * This class creates a dissimilarity matrix from a sequence alignment file
 * @author Dylan
 *
 */
public class DisMatrixMaker{
    static int[][] disMatrix;
    static int rows;
    static int columns;
    static String[] names;
    static char[][] alignment; //alignment[rows][columns] ie alignment[taxa][features]

    /**
     * Main method. Takes in the sequence alignment file name as standard input.
     * @param args
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException{
        Scanner userIn = new Scanner(System.in);
        System.out.println("Enter the file name.");
        String filename = userIn.next();
        Scanner fileIn = new Scanner(new File(filename));
        processAlignmentFile(fileIn);
        writeOutput();
        userIn.close();
    }
    
    /**
     * Reads the chars in alignment[][] and uses the values to determine distances. 
     * These distances are saved in disMatrix[][]
     */
    static void findDissimilarities(){
        for (int currentColumn = 0; currentColumn < columns; currentColumn++){
            for (int currentTaxa = 0; currentTaxa < rows - 1; currentTaxa++){
                for (int compTaxa = currentTaxa + 1; compTaxa < rows; compTaxa++){
                    if (alignment[currentTaxa][currentColumn] != alignment[compTaxa][currentColumn]){
                        disMatrix[currentTaxa][compTaxa]++;
                        disMatrix[compTaxa][currentTaxa]++;
                    }
                }
            }
        }
    }

    /**
     * Initializes the arrays and saves the sequences as a 2-dimensional array of chars (alignment[][])
     * @param fileIn
     */
    static void processAlignmentFile(Scanner fileIn){
        rows = fileIn.nextInt();
        columns = fileIn.nextInt();
        disMatrix = new int[rows][rows];
        alignment = new char[rows][columns];
        names = new String[rows];
        for (int currentRow = 0; currentRow < rows; currentRow++){
            names[currentRow] = fileIn.next();
            String line = fileIn.next();
            alignment[currentRow] = line.toCharArray();
        }
        findDissimilarities();
    }

    /**
     * Writes disMatrix[][] to a text file
     * @throws FileNotFoundException
     */
    static void writeOutput() throws FileNotFoundException{
        PrintWriter out = new PrintWriter("dissimilarityMatrix.txt");
        for (int i = 0; i < disMatrix.length; i++){
            for (int j = 0; j < disMatrix[i].length; j++){
                out.print(disMatrix[i][j]);
                if (j < disMatrix[i].length - 1)
                    out.print("\t");
            }
            out.println();
        }
        out.close();
        
        PrintWriter namesOut = new PrintWriter("names.txt");
        for (String o : names){
            namesOut.println(o);
        }
        namesOut.close();
    }
}
