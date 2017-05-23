package org.emerjoin.xmleasy;

/**
 * @author Mário Júnior
 */
public class XMLException extends RuntimeException {

    public XMLException(String message){
        super(message);
    }

    public XMLException(String message, Throwable cause){
        super(message,cause);
    }

}
