package org.emerjoin.xmleasy;

import org.w3c.dom.Element;

import java.util.function.Consumer;

/**
 * @author Mário Júnior
 */
public class ChildPresence {

    private Consumer<XMLEasy> consumer;
    private Runnable runnable;
    private XMLEasy xmlEasy;
    private XMLEasy parent;


    public ChildPresence(XMLEasy xmlEasy, XMLEasy parent){

        this.xmlEasy = xmlEasy;
        this.parent = parent;

    }


    public ChildPresence then(Consumer<XMLEasy> consumer){
        if(consumer==null)
            throw new IllegalArgumentException("Consumer instance must not be null");
        this.consumer = consumer;
        return this;
    }

    public ChildPresence otherwise(Runnable runnable){
        if(runnable==null)
            throw new IllegalArgumentException("Runnable instance must not be null");
        this.runnable = runnable;
        return this;
    }


    public XMLEasy eval(){

        if(runnable==null&&consumer==null)
            throw new IllegalStateException("Neither the Runnable nor the Consumer are set");

        if(xmlEasy==null&&runnable==null)
            return parent;

        else if(runnable!=null&&xmlEasy==null)
            runnable.run();

        if(consumer!=null&&xmlEasy!=null)
            consumer.accept(xmlEasy);

        return parent;

    }

}
