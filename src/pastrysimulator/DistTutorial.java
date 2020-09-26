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
import rice.p2p.past.*;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.commonapi.PastryIdFactory;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;
import rice.persistence.*;

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
      
    // used for generating PastContent object Ids.
    // this implements the "hash function" for our DHT
    PastryIdFactory idf = new PastryIdFactory(env);
    
    String storageDir = "./storage"+node.getId().hashCode();
    
    // create the persistent part
    // Storage stor = new PersistentStorage(idf, storageDirectory, 4 * 1024 * 1024, node
    // .getEnvironment());
    Storage stg = new MemoryStorage(idf);
    // construct a new MyApp
    Past app = new PastImpl(node,new StorageManagerImpl(idf, stg, new LRUCache(
          new MemoryStorage(idf), 512 * 1024, node.getEnvironment())), 2, "");
    
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
    
    // TODO: Hacer envío y recepción de files
    
    
    PastryMenu PastryMenuThread = new PastryMenu(app, idf, env);
    PastryMenuThread.start();
        
  }
    
  
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
