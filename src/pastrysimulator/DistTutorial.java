/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pastrysimulator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Scanner;

import rice.environment.Environment;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandleSet;
import rice.pastry.NodeHandle;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.leafset.LeafSet;
import rice.pastry.routing.RoutingTable;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;

/**
 * This tutorial shows how to setup a FreePastry node using the Socket Protocol.
 * 
 * @author Jeff Hoye
 * @author Yennifer Herrera
 */
public class DistTutorial {

  /**
   * This constructor sets up a PastryNode.  It will bootstrap to an 
   * existing ring if it can find one at the specified location, otherwise
   * it will start a new ring.
   * 
   * @param bindport the local port to bind to 
   * @param bootaddress the IP:port of the node to boot from
   * @param env the environment for these nodes
   */
  public DistTutorial(int bindport, InetSocketAddress bootaddress, Environment env) throws Exception {
    
    // Generate the NodeIds Randomly
    NodeIdFactory nidFactory = new RandomNodeIdFactory(env);
    
    // construct the PastryNodeFactory, this is how we use rice.pastry.socket
    PastryNodeFactory factory = new SocketPastryNodeFactory(nidFactory, bindport, env);

    // construct a node
    PastryNode node = factory.newNode();
      
    // construct a new MyApp
    MyApp app = new MyApp(node);    
    
    node.boot(bootaddress);
    
    // the node may require sending several messages to fully boot into the ring
    synchronized(node) {
      while(!node.isReady() && !node.joinFailed()) {
        // delay so we don't busy-wait
        node.wait(500);
        
        // abort if can't join
        if (node.joinFailed()) {
          throw new IOException("Could not join the FreePastry ring.  Reason:"+node.joinFailedReason()); 
        }
      }       
    }
    
    System.out.println("Finished creating new node "+node);
    

     
    // wait 10 seconds
    env.getTimeSource().sleep(10000);
    
    try {
        System.out.println("Bienvenido");
        Scanner sc = new Scanner(System.in);
        Scanner scm = new Scanner(System.in);
        int RES = 0;
        while(RES !=2){
            System.out.println("------------------| Selecciona una opción |------------------");
            System.out.println("1. Enviar un mensaje");
            System.out.println("2. Salir");
            int in = sc.nextInt();
            if(in != 1 && in != 2){
                throw new Exception("Respuesta inválida. Adios");
            }else if(in == 2){
                break;
            }else {
                System.out.println("Puede que el mensaje no le llegue a nadie o le llegue al mismo remitente.");
                System.out.print("Escribe el mensaje: ");
                String msg = scm.nextLine();
                Id randId = nidFactory.generateNodeId();
                SendMessage(msg, app, randId);
            }
        }
    }catch(Exception e){
        System.out.println("Respuesta inválida. Adios");
    }finally {
        System.exit(0);
    }
        
  }
    
    /**
     * Send a message from one node to another
     * @param cont message content
     * @param app an app instance, the sender
     * @param receiver id to route the message
     */
    private void SendMessage(String cont, MyApp app, Id receiver){
      app.routeMyMsg(receiver, cont);
    }
  
//  private void SendMessageNh(String cont, MyApp app, NodeHandle nh){
//      app.routeMyMsgDirect(nh, cont);
//  }
  
  
  /**
   * Usage: 
   * java [-cp FreePastry-<version>.jar] rice.tutorial.lesson3.DistTutorial localbindport bootIP bootPort
   * example java rice.tutorial.DistTutorial 9001 pokey.cs.almamater.edu 9001
   */
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
      DistTutorial dt = new DistTutorial(bindport, bootaddress, env);
    } catch (Exception e) {
      // remind user how to use
      System.out.println("Usage:"); 
      System.out.println("java [-cp FreePastry-<version>.jar] rice.tutorial.lesson3.DistTutorial localbindport bootIP bootPort");
      System.out.println("example java rice.tutorial.DistTutorial 9001 pokey.cs.almamater.edu 9001");
      throw e; 
    }
  }
}
