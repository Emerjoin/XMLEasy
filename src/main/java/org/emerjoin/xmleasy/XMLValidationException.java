package org.emerjoin.xmleasy;

/**
 * @author Mário Júnior
 */
public class XMLValidationException extends XMLException {

    public XMLValidationException(String message){
        super(message);
    }

    public XMLValidationException(String message, Throwable cause){
        super(message,cause);
    }

}
