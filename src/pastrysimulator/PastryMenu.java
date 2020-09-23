/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pastrysimulator;

import java.util.Scanner;
import rice.p2p.commonapi.Id;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;

/**
 *
 * @author laptop
 */
public class PastryMenu extends Thread {

    PastryNode node;
    NodeIdFactory nidFactory;
    MyApp app;

    PastryMenu(MyApp app, PastryNode node, NodeIdFactory nidFactory) {
        this.app = app;
        this.node = node;
        this.nidFactory = nidFactory;
    }

    public void run() {
        System.out.println("MyThread running");
        try {
            System.out.println("Bienvenido");
            Scanner sc = new Scanner(System.in);
            Scanner scm = new Scanner(System.in);
            int RES = 0;
            while (RES != 2) {
                System.out.println("------------------| Selecciona una opción |------------------");
                System.out.println(node);
                System.out.println("1. Enviar un mensaje");
                System.out.println("2. Salir");
                int in = sc.nextInt();
                if (in != 1 && in != 2) {
                    throw new Exception("Respuesta inválida. Adios");
                } else if (in == 2) {
                    break;
                } else {
                    System.out.println("Puede que el mensaje no le llegue a nadie o le llegue al mismo remitente.");
                    System.out.print("Escribe el mensaje: ");
                    String msg = scm.nextLine();
                    //TODO GET CLOSEST NODES
                    Id randId = nidFactory.generateNodeId();
                    //
                    SendMessage(msg, app, randId);
                }
            }
        } catch (Exception e) {
            System.out.println("Respuesta inválida. Adios");
        } finally {
            System.exit(0);
        }
    }

    private void SendMessage(String cont, MyApp app, Id receiver) {
        app.routeMyMsg(receiver, cont);
    }

}
