package org.emerjoin.xmleasy;

/**
 * @author Mario Junior
 */
public class XMLValidationException extends XMLException {

    public XMLValidationException(String message){
        super(message);
    }

    public XMLValidationException(String message, Throwable cause){
        super(message,cause);
    }

}
