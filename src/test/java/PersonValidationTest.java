import org.emerjoin.xmleasy.InvalidXMLDocumentException;
import org.emerjoin.xmleasy.XMLEasy;
import org.junit.Test;

import java.io.File;

import static org.emerjoin.xmleasy.Paths.*;

/**
 * @author Mário Júnior
 */
public class PersonValidationTest {

    private static final String BASE_PATH = "test-cases"+File.separator+"person"+File.separator;
    private static final String[] XML_SCHEMAS = {BASE_PATH +"person.xsd", BASE_PATH +"details.xsd"};


    @Test
    public void xml_document_validation_must_pass() throws Exception {

        String documentPath = BASE_PATH +"document1.xml";
        XMLEasy instance = new XMLEasy(url(documentPath));
        instance.validate(urls(XML_SCHEMAS));

    }


    @Test(expected = InvalidXMLDocumentException.class)
    public void xml_document_validation_must_fail() throws Exception {

        String documentPath = BASE_PATH +"document2.xml";

        XMLEasy instance = new XMLEasy(url(documentPath));
        instance.validate(urls(XML_SCHEMAS));

    }


}
