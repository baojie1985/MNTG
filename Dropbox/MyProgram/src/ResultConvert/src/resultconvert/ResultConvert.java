/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resultconvert;

/**
 *
 * @author baojie
 */
public class ResultConvert {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        RoadNetworks RN = new RoadNetworks();
          
        String folder = "F:\\Dropbox\\MyProgram\\RoadTrans";

        RN.ReadParameters(folder + "\\parameter639.txt");
        RN.ReadNodeFile(folder + "\\output.node");
        RN.ReadEdgeFile(folder + "\\output.edge");
    }
}
