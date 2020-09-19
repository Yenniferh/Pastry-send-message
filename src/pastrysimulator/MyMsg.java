package pastrysimulator;


import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;

/**
 * An example message.
 * 
 * @author Jeff Hoye
 */
public class MyMsg implements Message {
  /**
   * Where the Message came from.
   */
  Id from;
  /**
   * Where the Message is going.
   */
  Id to;
  
  String content;
  
  /**
   * Constructor.
   */
  public MyMsg(Id from, Id to, String content) {
    this.from = from;
    this.to = to;
    this.content = content;
  }
  
  public String toString() {
    return "MyMsg from "+from+" to "+to +" content " + content;
  }

  /**
   * Use low priority to prevent interference with overlay maintenance traffic.
   */
  public int getPriority() {
    return Message.LOW_PRIORITY;
  }
}

