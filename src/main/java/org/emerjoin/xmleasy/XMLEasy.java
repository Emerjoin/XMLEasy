package org.emerjoin.xmleasy;

import com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Represents the XMLEasy public API.
 * Object instances of this class are not Thread-safe
 * @author Mario Junior
 */
public class XMLEasy {

    private URL xmlURL;
    private InputStream xmlStream;
    private Document document;
    private Element currentElement;
    private byte[] xmlBytes;
    private boolean frozen;
    private LSResourceResolver resolver;

    /**
     * Creates a new {@link XMLEasy} instance for an XML {@link Element}
     * @param element the XML {@link Element}. This will be set as the current {@link Element}.
     * @return an {@link XMLEasy} instance
     */
    public static XMLEasy easy(Element element){
        if(element==null)
            throw new IllegalArgumentException("Element must not be null");
        return new XMLEasy(element);

    }


    /**
     * Constructs a new {@link XMLEasy} instance for an XML document and sets the root element as the current {@link Element}.
     * @param url the XML Document URL
     */
    public XMLEasy(URL url){
        if(url==null)
            throw new IllegalArgumentException("URL must not be null");
        this.xmlURL = url;
        loadXML();

    }

    /**
     * Constructs a new {@link XMLEasy} instance for an XML document and sets the root element as the current {@link Element}.
     * @param stream the XML Document {@link InputStream}
     */
    public XMLEasy(InputStream stream){
        if(stream==null)
            throw new IllegalArgumentException("InputStream must not be null");
        this.xmlStream = stream;
        loadXML();
    }


    /**
     * Constructs a new {@link XMLEasy} instance for an XML {@link Element}.
     * @param element the XML {@link Element}. This will be set as the current {@link Element}
     */
    public XMLEasy(Element element){
        if(element==null)
            throw new IllegalArgumentException("Element must not be null");
        this.currentElement = element;

    }


    private XMLEasy loadXML(){
        if(xmlURL==null&&xmlStream==null)
            throw new IllegalStateException("No URL or InputStream set");

        try {

            if(xmlStream!=null)
                xmlBytes = IOUtils.toByteArray(xmlStream);
            else xmlBytes = IOUtils.toByteArray(xmlURL);

            xmlStream = new ByteArrayInputStream(xmlBytes);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            document = db.parse(xmlStream);
            currentElement = document.getDocumentElement();


        }catch (Throwable ex){

            throw new XMLException("Failed to load XML",ex);

        }

        return this;

    }


    /**
     * Checks if the current element has child elements.
     * @return true if the current element has child elements, otherwise false.
     */
    public boolean hasChildren(){
        return hasChild();

    }

    private void requireChildNodes(){

        if(!hasChildren())
            throw new IllegalStateException(String.format("No child nodes found in element of type [%s]",currentElement.getTagName()));

    }


    /**
     * Selects the first child element with a specific tag name.
     * @param tag the child element tag name.
     * @return an {@link XMLEasy} instance with the child {@link Element} set as current {@link Element}.
     * @throws IllegalStateException if the current {@link Element} has no matching child element.
     * @throws IllegalArgumentException if the tag name is null or empty.
     */
    public XMLEasy child(String tag){
        validateTagName(tag);
        return firstChild(tag);

    }


    public ChildPresence ifChild(String tag){

        if(hasChild(tag))
            return new ChildPresence(child(tag),this);

        return new ChildPresence(null,this);

    }

    /**
     * Selects the first child element with a specific tag name.
     * @param tag the child element tag name.
     * @return an {@link XMLEasy} instance with the child {@link Element} set as current {@link Element}.
     * @throws IllegalStateException if the current {@link Element} has no matching child element.
     * @throws IllegalArgumentException if the tag name is null or empty.
     */
    public XMLEasy firstChild(String tag){
        validateTagName(tag);
        NodeList nodeList = children(tag);
        return wrap((Element) nodeList.item(0));
    }

    /**
     * Selects the first child element.
     * @return an {@link XMLEasy} instance with the child {@link Element} set as current {@link Element}.
     * @throws IllegalStateException if the current {@link Element} has no child element.
     */
    public XMLEasy firstChild(){

        NodeList childNodes = currentElement.getChildNodes();
        for(int i=0;i<childNodes.getLength();i++){

            Node node = childNodes.item(i);
            if(node instanceof Element)
                return wrap((Element) node);

        }

        throw new IllegalStateException("No child element found");

    }

    /**
     * Selects the last child element.
     * @return an {@link XMLEasy} instance with the child {@link Element} set as current {@link Element}.
     * @throws IllegalStateException if the current {@link Element} has no child element.
     */
    public XMLEasy lastChild(){

        List<Element> elements = children();
        if(elements.size()==0)
            throw new IllegalStateException("No child element found");
        return wrap(elements.get(elements.size()-1));

    }


    /**
     * Selects the first child element.
     * @return an {@link XMLEasy} instance with the child {@link Element} set as current {@link Element}.
     * @throws IllegalStateException if the current {@link Element} has no child element.
     */
    public XMLEasy child(){
        NodeList nodeList = currentElement.getChildNodes();
        for(int i = 0; i<nodeList.getLength();i++){

            Node node = nodeList.item(i);
            if(node instanceof Element)
                return wrap((Element) node);

        }

        throw new IllegalStateException(String.format("No Child element found in element [%s]",
                currentElement.getTagName()));

    }

    private NodeList children(String tag){
        requireChildNodes();
        NodeList nodeList = currentElement.getElementsByTagName(tag);
        if(nodeList.getLength()==0)
            throw new IllegalStateException(String.format("No element with tag [%s] found in element [%s]",tag, currentElement.getTagName()));

        return nodeList;

    }

    private List<Element> children(){

        NodeList nodeList = currentElement.getChildNodes();
        List<Element> elements = new ArrayList<>();
        for(int i=0;i<nodeList.getLength();i++){

            Node node = nodeList.item(i);
            if(!(node instanceof Element))
                continue;

            elements.add((Element) node);
        }

        return elements;

    }

    /**
     * Selects the last child element with a specific tag name.
     * @return an {@link XMLEasy} instance with the child {@link Element} set as current {@link Element}.
     * @throws IllegalStateException if the current {@link Element} has no matching child element.
     * @throws IllegalArgumentException if the tag name is null or empty.
     */
    public XMLEasy lastChild(String tag){
        validateTagName(tag);
        NodeList nodeList = children(tag);
        return wrap((Element) nodeList.item(nodeList.getLength()-1));
    }



    /**
     * Gets a list of child elements with a specific tag name.
     * @param tagName child elements tag name.
     * @return a {@link List<Element>} of child elements with the specified tag name.
     * @throws IllegalArgumentException if the tag name is null or empty.
     */
    public List<Element> listChildren(String tagName){
        validateTagName(tagName);
        NodeList nodeList = currentElement.getElementsByTagName(tagName);
        List<Element> elements = new ArrayList<>(nodeList.getLength());
        for(int i=0;i<nodeList.getLength();i++){
            Element element = (Element) nodeList.item(i);
            elements.add(element);
        }

        return elements;

    }

    /**
     * Gets a list of child elements.
     * @return a {@link List<Element>} of child elements.
     */
    public List<Element> listChildren(){

        return children();

    }


    /**
     * Gets a non-parallel stream of child elements with a specific tag name.
     * @param tagName child elements tag name.
     * @return a non-parallel stream of child elements with the specified tag name.
     * @throws IllegalArgumentException if the tag name is null or empty.
     */
    public Stream<Element> streamChildren(String tagName){
        validateTagName(tagName);
        return listChildren(tagName).stream();

    }

    /**
     * Gets a non-parallel stream of child elements.
     * @return a non-parallel stream of child elements.
     */
    public Stream<Element> streamChildren(){

       return listChildren().stream();

    }

    /**
     * Supplies child {@link Element} instances with a specific tag name to a {@link Consumer<Element>}
     * @param tagName child elements tag name
     * @param consumer child elements consumer
     * @return the current {@link XMLEasy} instance.
     * @throws IllegalArgumentException if the tag name is null or empty or the consumer instance is null.
     */
    public XMLEasy eachChildElement(String tagName, Consumer<Element> consumer){
        validateTagName(tagName);
        if(consumer==null)
            throw new IllegalArgumentException("Consumer instance must not be null");
        NodeList nodeList = currentElement.getElementsByTagName(tagName);
        for(int i=0;i<nodeList.getLength();i++){
            Element element = (Element) nodeList.item(i);
            consumer.accept(element);
        }

        return this;
    }

    /**
     * Supplies child {@link Element} instances to a {@link Consumer<Element>}
     * @param consumer child elements consumer
     * @return the current {@link XMLEasy} instance.
     * @throws IllegalArgumentException if the consumer instance is null.
     */
    public XMLEasy eachChildElement(Consumer<Element> consumer){
        if(consumer==null)
            throw new IllegalArgumentException("Consumer instance must not be null");
        NodeList nodeList = currentElement.getChildNodes();
        for(int i=0;i<nodeList.getLength();i++){
            Node node = nodeList.item(i);
            if(!(node instanceof Element))
                continue;

            Element element = (Element) node;
            consumer.accept(element);
        }

        return this;
    }



    /**
     * Checks if the current {@link Element} has child elements with a specific tag name.
     * @param tagName the child elements tag name
     * @return true if the current {@link Element} has child elements with the specified tag name, otherwise false.
     */
    public boolean hasChild(String tagName){
        return  currentElement.getElementsByTagName(tagName)
                .getLength()>0;
    }

    /**
     * Checks if the current {@link Element} has at least one child element.
     * @return true if the current {@link Element} has one or more child elements, otherwise false.
     */
    public boolean hasChild(){

        NodeList nodeList = currentElement.getChildNodes();
        for(int i=0;i<nodeList.getLength();i++){

            Node node = nodeList.item(i);
            if(node instanceof Element)
                return true;

        }

        return false;

    }


    private void validateAttributeName(String name){
        if(name==null||name.isEmpty())
            throw new IllegalArgumentException("Attribute name must not be null or empty");
    }

    /**
     * Gets the value of a non-optional attribute.
     * @param name the attribute's name
     * @return the attribute's value. Will never be null.
     * @throws IllegalStateException if no attribute with the supplied name is found.
     * @throws IllegalArgumentException if the attribute name is null or empty
     */
    public String attribute(String name){
        validateAttributeName(name);
        if(!currentElement.hasAttribute(name))
            throw new IllegalStateException(String.format("There is no such attribute : %s",name));
        return currentElement.getAttribute(name);
    }

    /**
     * Gets the value of an optional attribute.
     * @param name the optional attribute's name
     * @return an {@link Optional<String>} instance for the attribute's value. Will never be null.
     * @throws IllegalArgumentException if the attribute name is null or empty
     */
    public Optional<String> optionalAttribute(String name){
        validateAttributeName(name);
        if(currentElement.hasAttribute(name))
            return Optional.ofNullable(currentElement.getAttribute(name));

        return Optional.empty();

    }


    /**
     * Validates the current XML Document against one XML Schema.
     * @param schema the XML schema to validate the document
     * @return the current {@link XMLEasy} instance.
     * @throws IllegalArgumentException if the xml schema is null
     * @throws IllegalStateException if the current {@link XMLEasy} instance was not created from a {@link URL} or an {@link InputStream}
     * @throws InvalidXMLDocumentException if the XML document does not pass the XML Schema validation.
     */
    public XMLEasy validate(URL schema){
        if(schema==null)
            throw new IllegalArgumentException("XML Schema must not be null");
        return validate(new URL[]{schema});

    }

    private byte[] getBytes(InputStream input) throws IOException{
        byte[] xmlBytes = new byte[input.available()];
        int offset = 0;
        while(input.available()>0)
          offset += input.read(xmlBytes,offset,input.available());
        return xmlBytes;
    }


    private void validateTagName(String tag){

        if(tag==null||tag.isEmpty())
            throw new IllegalArgumentException("Tag name must not be null or empty");
    }


    /**
     * Validates the current XML Document against one or more XML Schemas.
     * @param schemas the XML schemas array
     * @return the current {@link XMLEasy} instance.
     * @throws IllegalArgumentException if the xml schemas array is empty or null
     * @throws IllegalStateException if the current {@link XMLEasy} instance was not created from a {@link URL} or an {@link InputStream}
     * @throws InvalidXMLDocumentException if the XML document does not pass the XML Schemas validation.
     */
    public  XMLEasy validate(URL[] schemas){
        if(schemas==null||schemas.length==0)
            throw new IllegalArgumentException("XML schemas array must not be empty");

        if(xmlStream==null&&xmlURL==null)
            throw new IllegalStateException("Xml URL or Stream required");

        InputStream xml = new ByteArrayInputStream(xmlBytes);

        try {

            byte[] buffer = new byte[xml.available()];
            xml.read(buffer);

            ByteArrayInputStream byteArray1 = new ByteArrayInputStream(buffer);

            Source[] schemaSources = new Source[schemas.length];
            for(int i=0;i<schemaSources.length;i++)
                schemaSources[i] = new StreamSource(schemas[i].openStream());

            XMLSchemaFactory factory =
                    new XMLSchemaFactory();

            if(resolver!=null)
                factory.setResourceResolver(resolver);

            Source xmlSource = new StreamSource(byteArray1);
            Schema schema = factory.newSchema(schemaSources);
            Validator validator = schema.newValidator();
            validator.validate(xmlSource);

        }catch (SAXException  ex){

            throw new InvalidXMLDocumentException(ex);

        }catch (IOException ex){

            throw new XMLValidationException(String.format("Validation of XML document in [%s] failed",
                    xmlURL.toString()),ex);

        }

        return this;

    }


    /**
     * Set's an XML Schemas resource resolver to be used during validation.
     * @param resolver - the LSResourceResolver to be used during validation.
     */
    public XMLEasy setResolver(LSResourceResolver resolver){
        if(resolver==null)
            throw new IllegalArgumentException("Resources resolver instance must not be null");
        this.resolver = resolver;
        return this;

    }

    /**
     * Gets the text content of the current XML {@link Element}.
     * @return the text content of the current XML {@link Element}.
     */
    public String getContent(){
        return currentElement.getTextContent();
    }


    /**
     * Gets the current XML {@link Element}.
     * @return the current XML {@link Element}. Will never return null.
     */
    public Element getElement(){

        return currentElement;

    }

    /**
     * Gets the tag name of the current XML element.
     * @return the tag name of the current XML element. Will never return null.
     */
    public String getTag(){
        return currentElement.getTagName();

    }

    /**
     * Makes this {@link XMLEasy} instance immutable, meaning any invocation of a single-child element navigation methods will
     * return a new {@link XMLEasy} instance. Are considered single-child elements navigation methods, the following:
     * {@link #child()}, {@link #child(String)}, {@link #firstChild()}, {@link #firstChild(String)}, {@link #lastChild(String)} and {@link #lastChild()}.
     * @return the current {@link XMLEasy} instance.
     */
    public XMLEasy freeze(){

        this.frozen = true;
        return this;

    }

    private XMLEasy wrap(Element element){

        if(frozen)
            return easy(element);

        this.currentElement = element;
        return this;
    }





}
