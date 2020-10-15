/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pastrysimulator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;
import rice.Continuation;
import rice.environment.Environment;
import rice.p2p.commonapi.Id;
import rice.p2p.past.Past;
import rice.p2p.past.PastContent;
import rice.pastry.PastryNode;
import rice.pastry.commonapi.PastryIdFactory;

/**
 *
 * @author laptop
 */
public class PastryMenu extends Thread {
    MyScribeClient client;
    PastryNode node;
    Environment env;
    

    PastryMenu(MyScribeClient client, PastryNode node, Environment env) {
        this.client = client;
        this.node = node;
        this.env = env;
    }

    @Override
    public void run() {
        System.out.println("MyThread running");
        try {
            System.out.println("Bienvenido");
            Scanner sc = new Scanner(System.in);
            Scanner scm = new Scanner(System.in);
            int RES = 0;
            while (RES != 2) {
                System.out.println("------------------| Selecciona una opción |------------------");
                System.out.println("1. Suscribirme");
                System.out.println("2. Enviar mensaje multicast");
                System.out.println("3. Salir");
                int in = sc.nextInt();
                if (in != 1 && in != 2 && in != 3) {
                    throw new Exception("Respuesta inválida. Adios");
                } else if (in == 1) {
                    // System.out.println("Puede que el mensaje no le llegue a nadie o le llegue al mismo remitente.");
                    client.subscribe();
                } else if (in == 2) {
                    System.out.print("Escribe el mensaje: ");
                    String msg = scm.nextLine();
                    if(msg.isEmpty()){
                        throw new Exception("Respuesta inválida. Adios");
                    }
                    client.sendMulticast(msg);
                }else {
                    break;
                }
            }
        }
        catch(IOException io){
            System.out.println("Hubo un problema con el archivo");
        }catch (Exception e) {
            System.out.println("Respuesta inválida. Adios");
        } finally {
            env.destroy();
            System.exit(1);
        }
    }

}
