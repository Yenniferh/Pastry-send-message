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

    PastryNode node;
    PastryIdFactory localFactory;
    Past app;
    ArrayList<Id> sendedData;
    ArrayList<Id> storedData;
    Environment env;

    PastryMenu(Past app, PastryIdFactory localFactory, Environment env) {
        sendedData = new ArrayList();
        storedData = new ArrayList();
        this.app = app;
        this.localFactory = localFactory;
        this.env = env;
    }

    @Override
    public void run() {
        System.out.println("MyThread running");
        // TODO: implement getData
        try {
            System.out.println("Bienvenido");
            Scanner sc = new Scanner(System.in);
            Scanner scm = new Scanner(System.in);
            int RES = 0;
            while (RES != 2) {
                System.out.println("------------------| Selecciona una opción |------------------");
                System.out.println("1. Enviar un archivo");
                System.out.println("2. Obtener un archivo");
                System.out.println("3. Salir");
                int in = sc.nextInt();
                if (in != 1 && in != 2 && in != 3) {
                    throw new Exception("Respuesta inválida. Adios");
                } else if (in == 1) {
                    // System.out.println("Puede que el mensaje no le llegue a nadie o le llegue al mismo remitente.");
                    System.out.print("Escribe la ruta del archivo: ");
                    String path = scm.nextLine();
                    if(path.isEmpty()){
                        throw new Exception("Respuesta inválida. Adios");
                    }
                    
                    storeData(path);
                    
                } else if (in == 2) {
                    getData();
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

//    private void SendMessage(String cont, MyApp app, Id receiver) {
//        app.routeMyMsg(receiver, cont);
//    }
    
    private void storeData(String path) throws IOException{
        System.out.println("Sending a file...");
        File f = new File(path);
        byte[] fileContent = Files.readAllBytes(f.toPath());
        final PastContent content = new MyPastContent(localFactory.buildId(fileContent), fileContent, f.getName());
        sendedData.add(content.getId());
        app.insert(content, new Continuation<Boolean[], Exception>() {
        // the result is an Array of Booleans for each insert
        @Override
        public void receiveResult(Boolean[] results) {          
            int numSuccessfulStores = 0;
            for (Boolean result : results) {
                if (result) {
                    numSuccessfulStores++;
                }
            }
            System.out.println(content + " successfully stored at " + 
                    numSuccessfulStores + " locations.");
        }
  
        @Override
        public void receiveException(Exception result) {
          System.out.println("Error storing "+content);
          result.printStackTrace();
        }
      });
        
    }
    
    public void getData(){
        System.out.println("Looking up the file");
        final Id lookupKey = sendedData.get(0);
        app.lookup(lookupKey, new Continuation<PastContent, Exception>() {
        @Override
        public void receiveResult(PastContent result) {
          System.out.println("Successfully looked up " + result + " for key "+lookupKey+".");
        }
  
        @Override
        public void receiveException(Exception result) {
          System.out.println("Error looking up "+lookupKey);
          result.printStackTrace();
        }
      });
    }
}
