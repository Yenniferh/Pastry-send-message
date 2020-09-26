/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pastrysimulator;

import rice.p2p.commonapi.Id;
import rice.p2p.past.ContentHashPastContent;

/**
 *
 * @author Yennifer Herrera
 */
public class MyPastContent extends ContentHashPastContent{
    byte[] content;
    String name;

    public MyPastContent(Id id, byte[] content, String name){
        super(id);
        this.content = content;
        this.name = name;
    }
    
    @Override
    public String toString(){
        String out = "File name: " +name;
        return out;
    }
}
