# XMLEasy
A simple Java XML library to validate and read XML Documents using the SAX Parser.

## Java docs
[https://emerjoin.github.io/XMLEasy/java-docs/](https://emerjoin.github.io/XMLEasy/java-docs/)

## Usage examples

Consider the following XML document
```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <Carwash xmlns="whatever.namespace/you/want">
        <cars>
            <car wash-date="12/12/2014" color="red">
                <brand>Toyota</brand>
                <model>Supra</model>
            </car>
            <car color="black">
                <brand>BMW</brand>
                <model>325</model>
            </car>
        </cars>
        <clients>
            <client>
                <name>John Doe</name>
            </client>
        </clients>
        <washers>
            <washer>
                <name>
                    <first-name>Mario</first-name>
    
                    <surname>Junior</surname>
                </name>
                <age>24</age>
            </washer>
            <washer>
                <name>
                    <first-name>Romildo</first-name>
                    <surname>Cumbe</surname>
                </name>
                <age>21</age>
            </washer>
            <washer>
                <name>
                    <first-name>Aurio</first-name>
                    <surname>Tino</surname>
                </name>
                <age>21</age>
            </washer>
        </washers>
    </Carwash>
```

### Listing car records

```java
   
   URL xmlDocument = //whatever
   List<Element> cars = new XMLEasy(xmlDocument).firstChild().listChildren();   

```

### Transforming car elements into Car object instances using java 8 Streams API

```java
   
   URL xmlDocument = //whatever
   List<Car> cars = new XMLEasy(xmlDocument).firstChild().streamChildren()
        .map(element-> new Car(element))
        .collect(Collectors.toList());

```



### Find car elements already washed (with wash-date attribute) using java 8 Streams API

```java
    
    //Static method import
    import static org.emerjoin.xmleasy.XMLEasy.easy;
    
    //...
    
    URL xmlDocument = //whatever
       List<Element> cars = new XMLEasy(xmlDocument).firstChild().streamChildren()
            .filter(element -> easy(element).optionalAttribute("wash-date").isPresent())
            .collect(Collectors.toList());
    

```


### Find car records with yellow color using java 8 Streams API

```java
    
    //Static method import
    import static org.emerjoin.xmleasy.XMLEasy.easy;
    
    //...
    
    URL xmlDocument = //whatever
       List<Element> cars = new XMLEasy(xmlDocument).firstChild().streamChildren()
            .filter(element -> easy(element).optionalAttribute("wash-date").isPresent())
            .collect(Collectors.toList());
    

```


### Get the full name of the last washer element using java 8 Streams API


### Validate the document against one XML Schema


### Validate the document against multiple XML Schemas


### Validate the document and get the model of the last car element (methods chaining)