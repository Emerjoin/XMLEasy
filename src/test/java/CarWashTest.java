import org.emerjoin.xmleasy.XMLEasy;
import org.junit.Test;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static org.emerjoin.xmleasy.Paths.*;
import static org.junit.Assert.*;
import static org.emerjoin.xmleasy.XMLEasy.*;

/**
 * @author Mário Júnior
 */
public class CarWashTest {

    private static final String BASE_PATH = "test-cases"+File.separator+"carwash"+File.separator;
    private static final String XML_DOCUMENT_PATH = BASE_PATH+"Document.xml";

    protected XMLEasy getInstance() throws Exception{
        return new XMLEasy(url(XML_DOCUMENT_PATH));
    }

    @Test
    public void two_cars_must_be_found() throws Exception{

        XMLEasy xml = getInstance();
        int carsFound = xml.child("cars").listChildren().size();
        assertEquals(2,carsFound);


    }

    @Test
    public void two_cars_must_be_found_using_tag_names() throws Exception{

        XMLEasy xml = getInstance();
        int carsFound = xml.child("cars").listChildren("car").size();
        assertEquals(2,carsFound);


    }


    @Test
    public void one_car_with_bmw_brand_must_be_found() throws Exception{

        XMLEasy xml = getInstance();
        Optional<Element> element = xml.child("cars").streamChildren()
                .filter(el -> easy(el).child("brand").getContent().equals("BMW"))
                .findFirst();

        assertTrue(element.isPresent());
        Element domElement = element.get();
        assertEquals("car",domElement.getTagName());

    }

    @Test
    public void one_car_with_bmw_brand_must_be_found_using_tag_names() throws Exception{

        XMLEasy xml = getInstance();
        Optional<Element> element = xml.child("cars").streamChildren("car")
                .filter(el -> easy(el).child("brand").getContent().equals("BMW"))
                .findFirst();

        assertTrue(element.isPresent());
        Element domElement = element.get();
        assertEquals("car",domElement.getTagName());

    }

    @Test
    public void three_washers_must_be_found_using_tag_names() throws Exception{

        assertEquals(3,getInstance()
                .lastChild().listChildren().size());

    }

    @Test
    public void three_washers_must_be_found() throws Exception{

        assertEquals(3,getInstance()
                .child("washers").listChildren("washer").size());

    }


    @Test
    public void one_washer_with_24_years_old_must_be_found_using_tag_names() throws Exception{

        XMLEasy xml = getInstance();
        Optional<Element> washer = xml.streamChildren("washers")
                .filter((el -> easy(el).child("age").getContent().equals("24")))
                .findFirst();

        assertTrue(washer.isPresent());
        XMLEasy washerElement = easy(washer.get());
        assertEquals("Mario",washerElement.child("name").child("first-name").getContent());
        assertEquals("Junior",washerElement.child("name").child("surname").getContent());

    }
    @Test
    public void one_washer_with_24_years_old_must_be_found() throws Exception{

        XMLEasy xml = getInstance();
        Optional<Element> washer = xml.lastChild().streamChildren()
                .filter((el -> easy(el).lastChild().getContent().equals("24")))
                .findFirst();

        assertTrue(washer.isPresent());
        XMLEasy washerElement = easy(washer.get());
        assertEquals("Mario",washerElement.firstChild().firstChild().getContent());
        assertEquals("Junior",washerElement.firstChild().lastChild().getContent());

    }


    @Test
    public void eachChildElementWithTagNameTest() throws Exception{

        XMLEasy xml = getInstance();
        Collection<String> watcherNames = new ArrayList<>();
        xml.child("washers").eachChildElement("washer",el -> watcherNames.add(easy(el).child("name").getContent()));
        assertEquals(3,watcherNames.size());

    }

    @Test
    public void eachChildElementWithNoTagNameTest() throws Exception{

        XMLEasy xml = getInstance();
        Collection<String> watcherNames = new ArrayList<>();
        xml.lastChild().eachChildElement(el -> watcherNames.add(easy(el).firstChild().firstChild().getContent()));
        assertEquals(3,watcherNames.size());

    }


    @Test
    public void child_and_first_child_traverse_with_tag_name_test() throws Exception{

        XMLEasy xml = getInstance().traverse();
        xml.child("cars").firstChild("car").firstChild("brand");
        assertEquals("brand",xml.getTag());
        assertEquals("Toyota",xml.getContent());

    }

    @Test
    public void child_and_first_child_traverse_test() throws Exception{

        XMLEasy xml = getInstance().traverse();
        xml.firstChild().firstChild().firstChild();
        assertEquals("brand",xml.getTag());
        assertEquals("Toyota",xml.getContent());

    }

    @Test
    public void child_and_last_child_and_first_child_traverse_with_tag_name_test() throws Exception{

        XMLEasy xml = getInstance().traverse();
        xml.child("cars").lastChild("car").firstChild("model");
        assertEquals("model",xml.getTag());
        assertEquals("325",xml.getContent());

    }

    @Test
    public void child_and_last_child_and_first_child_traverse_test() throws Exception{

        XMLEasy xml = getInstance().traverse().firstChild().lastChild().lastChild();
        assertEquals("model",xml.getTag());
        assertEquals("325",xml.getContent());

    }

    @Test
    public void single_child_traverse_with_tag_names_test() throws Exception{

        XMLEasy xml = getInstance().traverse();
        xml.child("clients").child().child();
        assertEquals("name",xml.getTag());
        assertEquals("John Doe",xml.getContent());

    }

    @Test
    public void single_child_traverse_test() throws Exception{

        Optional<Element> element = getInstance().streamChildren()
                .filter((el-> el.getTagName().equals("clients")))
                .findFirst();
        assertTrue(element.isPresent());
        XMLEasy xml = easy(element.get()).traverse().firstChild().child();
        assertEquals("name",xml.getTag());
        assertEquals("John Doe",xml.getContent());

    }


    @Test
    public void get_attribute_test() throws Exception{

        String firstColor = getInstance().traverse().firstChild().firstChild().attribute("color");
        assertEquals("red",firstColor);
        String secondColor = getInstance().traverse().firstChild().lastChild().attribute("color");
        assertEquals("black",secondColor);

    }


    @Test
    public void get_optional_attribute_test() throws Exception{

        Optional<String> firstWashDate = getInstance().traverse().firstChild()
                .firstChild().optionalAttribute("wash-date");
        assertTrue(firstWashDate.isPresent());
        assertEquals("12/12/2014",firstWashDate.get());
        Optional<String> secondWashDate = getInstance().traverse().firstChild()
                .lastChild().optionalAttribute("wash-date");
        assertFalse(secondWashDate.isPresent());


    }


    @Test
    public void has_children_must_return_true_for_one_child_element() throws Exception{

        assertTrue(getInstance().child("clients").hasChildren());

    }


    @Test
    public void has_children_must_return_true_for_one_or_more_child_elements() throws Exception{

        assertTrue(getInstance().hasChildren());

    }

    @Test
    public void has_child_must_return_true_for_one_child_element() throws Exception{

        assertTrue(getInstance().child("clients").hasChild());


    }


    @Test
    public void has_child_must_return_true_for_one_or_more_child_elements() throws Exception{

        assertTrue(getInstance().hasChild());

    }

}
