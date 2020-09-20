/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pastrysimulator;

/**
 *
 * @author Yennifer Herrera
 */
public class Main {
    
    // Another main class, only for testing purpose
    public static void main(String[] args) throws Exception {
        String[] node1 = {"9000", "192.168.1.2", "9000"};
        DistTutorial.main(node1);
        String[] node2 = {"9001", "192.168.1.2", "9000"};
        DistTutorial.main(node2);
    }
}
