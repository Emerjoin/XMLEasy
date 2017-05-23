# XMLEasy
A simple functional Java 8 XML library to validate and read XML Documents using the SAX Parser.

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
    
    
    URL xmlDocument = //whatever
       List<Element> cars = new XMLEasy(xmlDocument).firstChild().streamChildren()
            .filter(element -> XMLEasy.easy(element).optionalAttribute("wash-date").isPresent())
            .collect(Collectors.toList());
    

```


### Find one car record with yellow color using java 8 Streams API

```java
       
    URL xmlDocument = //whatever
       Optional<Element> carElement = new XMLEasy(xmlDocument).firstChild().streamChildren()
            .filter(element -> XMLEasy.easy(element).attribute("color").equals("yellow"))
            .findFirst();  

```


### Get the full name of the last washer element using java 8 Streams API


```java

     URL xmlDocument = //whatever
           String fullName = new XMLEasy(xmlDocument).lastChild()
           .streamChildren()
           .map(element -> XMLEasy.easy(element).firstChild().getContent() 
           + " " +XMLEasy.easy(element).lastChild().getContent())
           .findFirst().get();
         
           
```


### Validate the document against one XML Schema

```java

     URL xmlDocument = //whatever
     URL xsdUrl = //whatever
     new XMLEasy(xmlDocument).validate(xsdUrl);

```

### Validate the document against multiple XML Schemas

```java

     URL xmlDocument = //whatever
     URL[] xsdUrls = //whatever
     new XMLEasy(xmlDocument).validate(xsdUrls);

```



## Freezing XMLEasy instances

Consider the following xml document:

```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <Online-Library>
        <Books>
            <book>  
                <isbn>100000131313</isbn>
                <title>The Life of a Coder</title>
                <available>false</available>
            </book>
            <book>  
                 <isbn>134000451320</isbn>
                 <title>The Glory of life</title>
                 <available>true</available>
            </book>
        </Books>
        <Readers>
            <Africa>
                <Mozambique>
                    <Maputo-Cidade>
                        <Reader>
                            <name>Mario Junior</name>
                            <age>24</age>
                            <occupation>Freelancer</occupation>
                        </Reader>
                    </Maputo-Cidade>
                </Mozambique>
            </Africa>
            <America>
                <Brasil>
                    <Sao-Paulo>
                        <Reader>
                            <name>Rodrigo Faro</name>
                            <age>47</age>
                            <occupation>Actor</occupation>
                        </Reader>
                       
                    </Sao-Paulo
                <Brasil>
            </America>
        </Readers>
    </Online-Library>

```

Lets use XMLEasy to get to the only reader in Maputo-Cidade (Mario Junior)

```java
    
     URL documentURL = //whatever
     XMLEasy xml = new XMLEasy(documentURL) //<Online-Library>
                        .lastChild() //<Readers>
                        .firstChild() //<Africa>
                        .firstChild() //<Mozambique>
                        .firstChild() //<Maputo-Cidade>
                        .firstChild(); //<Reader>
     
    
```


The code we just wrote creates exactly 1 XMLEasy object instance
and changes its internal state as we navigate.

By default, the same XMLEasy instance is returned when invoking any of the following methods: 
[child()](https://emerjoin.github.io/XMLEasy/java-docs/org/emerjoin/xmleasy/XMLEasy.html#child--), [child(String)](https://emerjoin.github.io/XMLEasy/java-docs/org/emerjoin/xmleasy/XMLEasy.html#child-java.lang.String-), [firstChild()](https://emerjoin.github.io/XMLEasy/java-docs/org/emerjoin/xmleasy/XMLEasy.html#firstChild--), [firstChild(String)](https://emerjoin.github.io/XMLEasy/java-docs/org/emerjoin/xmleasy/XMLEasy.html#firstChild-java.lang.String-), [lastChild(String)](https://emerjoin.github.io/XMLEasy/java-docs/org/emerjoin/xmleasy/XMLEasy.html#lastChild-java.lang.String-) and [lastChild()](https://emerjoin.github.io/XMLEasy/java-docs/org/emerjoin/xmleasy/XMLEasy.html#lastChild--).


```java
    
     URL documentURL = //whatever
     XMLEasy xml = new XMLEasy(documentURL) //new XMLEasy instance
                        .lastChild() //same XMLEasy instance
                        .firstChild() //same XMLEasy instance
                        .firstChild() //same XMLEasy instance
                        .firstChild() //same XMLEasy instance
                        .firstChild(); //same XMLEasy instance
                        
      
```

Sometimes we might want to use the same XMLEasy instance in multiple operations. If so, then, we must make sure its internal state is 
not modified: make it immutable. Making it immutable is to freeze it,
meaning it's internal state will be preserved and any navigation method invocation will result in a new XMLEasy instance.

Consider we wanted to get XMLEasy instances of the only two book readers in our XML document. The one from Maputo-Cidade and the other from Sao-Paulo.
Here is how we would do it:

```java
    
     URL documentURL = //whatever
     
     XMLEasy xml = new XMLEasy(documentURL) //new XMLEasy instance
            .freeze(); //Make this XMLEasy instance immutable
     
     XMLEasy maputoBookReader = xml.lastChild() //new XMLEasy instance : <Readers>
                        .firstChild() //<Africa>
                        .firstChild() //<Mozambique>
                        .firstChild() //<Maputo-Cidade>
                        .firstChild();//<Reader>
                        
                        
     XMLEasy saoPauloBookReader = xml.lastChild() //new XMLEasy instance : <Readers>
                             .lastChild() //<America>
                             .firstChild() //<Brasil>
                             .firstChild() //<Sao-Paulo>
                             .firstChild();//<Reader>
     
                            
      
```

