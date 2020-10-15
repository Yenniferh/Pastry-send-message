/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pastrysimulator;

import rice.p2p.commonapi.NodeHandle;
import rice.p2p.scribe.ScribeContent;

/**
 *
 * @author Yennifer Herrera
 */
public class MyScribeContent implements ScribeContent{
    NodeHandle from;
    String content;

    public MyScribeContent(NodeHandle from, String content) {
        this.from = from;
        this.content = content;
    }
    
    @Override
    public String toString() {
        return "MyContent is "+content+" from: "+from;
    }
}
