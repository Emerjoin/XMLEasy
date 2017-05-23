package org.emerjoin.xmleasy;

import org.xml.sax.SAXException;

import java.net.URL;

/**
 * @author Mario Junior
 */
public class InvalidXMLDocumentException extends XMLValidationException {

    public InvalidXMLDocumentException(SAXException cause){
        super(String.format("XML Document is not valid"),cause);
    }

}
