/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roadconverter;

/**
 *
 * @author baojie
 * 
 * Convert the text format into binary
 * 
 * Details in RoadNetworks class
 */
public class RoadConverter {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        String Path =args[0];
        RoadNetworks RN=new RoadNetworks(Path+"node.txt", Path+"edge.txt");
        System.out.println(RN.uplat+"\t"+RN.uplng+"\t"+RN.lwlat+"\t"+RN.lwlng);
        RN.output(Path);
        //RN.outputjavascript();
    }
}
