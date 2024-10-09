package ua.spro.xml_framework.integration_test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import ua.spro.xml_framework.model.Person;
import ua.spro.xml_framework.parser.XmlParser;
import ua.spro.xml_framework.serializer.XmlSerializer;

public class XmlSerializerIntegrationTest {

  private static final Logger log = Logger.getLogger(XmlSerializerIntegrationTest.class.getName());

  @Test
  void testSerializationAndDeserialization() throws IOException {
    Person person =
        new Person(
            1,
            "John",
            "john.doe@example.com",
            30,
            LocalDate.now(),
            true,
            new BigDecimal("50000.00"));
    log.info("Initial person: \n" + person);

    XmlSerializer xmlSerializer = new XmlSerializer();
    String xmlContent = xmlSerializer.marshal(person);
    log.info("Serialized xml: \n" + xmlContent);

    // Write xml content to a file
    String filePath = "output/person.xml";
    Files.write(Paths.get(filePath), xmlContent.getBytes());

    // Deserialize xml back to a person object
    XmlParser parser = new XmlParser();
    Person deserializedPerson = (Person) parser.unmarshal(new File(filePath), Person.class);
    log.info("Deserialized person: \n" + deserializedPerson);

    assertEquals(person.toString(), deserializedPerson.toString());
  }
}
