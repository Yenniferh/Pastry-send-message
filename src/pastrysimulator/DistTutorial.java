/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pastrysimulator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import rice.environment.Environment;
import rice.pastry.NodeHandle;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;
import rice.tutorial.lesson1.DistTutorial;

/**
 *
 * @author Yennifer Herrera
 */


public class PastrySimulator {

    /**
     * Se configura un PastryNode. Si puede encontrar un anillo ya creado en
     * locación especificada, de lo contrario crea uno.
     * 
     * @param bindport el puerto local de enlace
     * @param bootaddress IP:puerto del nodo que inicia el anillo (génesis)
     * @param env entorno para los nodos
     * @throws Exception si el nodo no puede unirse al anillo satisfactoriamente
     */
    public PastrySimulator(int bindport, InetSocketAddress bootaddress, 
            Environment env) throws Exception {
        
        // Genera el NodeId aleatoriamente para un entorno específico.
        NodeIdFactory nidFactory = new RandomNodeIdFactory(env);
        
        // Construye el PastryNodeFactory, quien se encarga de construir, 
        // inicializa y configurar el Pastry Node
        PastryNodeFactory factory = new SocketPastryNodeFactory(nidFactory, bindport, env);

        // Retorna null si aún no hay anillo
        NodeHandle bootHandle = ((SocketPastryNodeFactory)factory).getNodeHandle(bootaddress);

        System.out.println("BootHandle: " + bootHandle);
        // Construye un nodo. Si el boothandle es null, crea un nuevo anillo
        PastryNode node = factory.newNode(bootHandle);

        // Puede que el nodo requira enviar múltiples mensaje para unirse
        // Completamente al anillo
        synchronized(node) {
          while(!node.isReady() && !node.joinFailed()) {
            node.wait(500);

            // Se suspende si el nodo no pudo unirse
            if (node.joinFailed()) {
              throw new IOException("Could not join the FreePastry ring.  Reason:"+node.joinFailedReason()); 
            }
          }       
        }

        System.out.println("Finished creating new node "+node);  
        
    }
    
    public static void main(String[] args) throws Exception {
        // Loads pastry settings
        Environment env = new Environment();

        // disable the UPnP setting (in case you are testing this on a NATted LAN)
        env.getParameters().setString("nat_search_policy","never");

        try {
            // the port to use locally
            int bindport = Integer.parseInt(args[0]);

            // build the bootaddress from the command line args
            InetAddress bootaddr = InetAddress.getByName(args[1]);
            int bootport = Integer.parseInt(args[2]);
            InetSocketAddress bootaddress = new InetSocketAddress(bootaddr,bootport);

            // launch our node!
            PastrySimulator ps = new PastrySimulator(bindport, bootaddress, env);
        }catch(Exception e) {
            System.out.println("Error");
            throw e; 
        }
    }
}
