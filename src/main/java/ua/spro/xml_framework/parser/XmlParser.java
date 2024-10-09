package ua.spro.xml_framework.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import ua.spro.xml_framework.annotations.XmlAttribute;
import ua.spro.xml_framework.annotations.XmlElement;
import ua.spro.xml_framework.util.ReflectionUtil;

public class XmlParser {

  private static final Logger log = Logger.getLogger(XmlParser.class.getName());

  public Object unmarshal(File xmlFile, Class<?> clazz) {
    try {
      // create a new instance of the given class
      Object instance = clazz.getDeclaredConstructor().newInstance();

      // StAX setup
      XMLEventReader eventReader = createEventReader(xmlFile);

      while (eventReader.hasNext()) {
        XMLEvent event = eventReader.nextEvent();

        if (event.isStartElement()) {
          processStartElementEvent(clazz, event, instance, eventReader);
        }
      }
      return instance;
    } catch (Exception e) {
      log.severe(e.getMessage());
    }
    return null;
  }

  private void processStartElementEvent(
      Class<?> clazz, XMLEvent event, Object instance, XMLEventReader eventReader) {
    StartElement startElement = event.asStartElement();
    String elementName = startElement.getName().getLocalPart();

    // process attributes
    processAttributes(startElement, clazz, instance);

    // find the matching field in the class
    List<Field> fields = ReflectionUtil.getAnnotatedFields(clazz, XmlElement.class);

    fields.stream()
        .filter(
            field -> {
              XmlElement xmlElement = field.getAnnotation(XmlElement.class);
              String fieldName = xmlElement.name().isEmpty() ? field.getName() : xmlElement.name();
              return fieldName.equals(elementName);
            })
        .forEach(
            field -> {
              try {
                setFieldValue(instance, field, eventReader);
              } catch (IllegalAccessException | XMLStreamException e) {
                log.severe(e.getMessage());
              }
            });
  }

  private void processAttributes(StartElement startElement, Class<?> clazz, Object instance) {
    List<Field> fields = ReflectionUtil.getAnnotatedFields(clazz, XmlAttribute.class);
    fields.forEach(
        field -> {
          XmlAttribute xmlAttribute = field.getAnnotation(XmlAttribute.class);
          String attributeName =
              xmlAttribute.name().isEmpty() ? field.getName() : xmlAttribute.name();
          Iterator<Attribute> attributes = startElement.getAttributes();
          while (attributes.hasNext()) {
            Attribute attribute = attributes.next();
            if (attribute.getName().getLocalPart().equals(attributeName)) {
              try {
                field.setAccessible(true);
                field.set(instance, convertValue(field, attribute.getValue()));
              } catch (IllegalAccessException e) {
                log.severe(e.getMessage());
              }
            }
          }
        });
  }

  private void setFieldValue(Object instance, Field field, XMLEventReader eventReader)
      throws IllegalAccessException, XMLStreamException {
    XMLEvent nextEvent = eventReader.nextEvent();
    if (nextEvent instanceof Characters characters) {
      String value = characters.getData();

      field.setAccessible(true);
      field.set(instance, convertValue(field, value));
    }
  }

  private XMLEventReader createEventReader(File xmlFile)
      throws FileNotFoundException, XMLStreamException {
    XMLInputFactory factory = XMLInputFactory.newInstance();
    FileInputStream fileInputStream = new FileInputStream(xmlFile);
    return factory.createXMLEventReader(fileInputStream);
  }

  private Object convertValue(Field field, String value) {
    Class<?> type = field.getType();
    return switch (type.getSimpleName()) {
      case "Integer", "int" -> Integer.parseInt(value);
      case "Boolean", "boolean" -> Boolean.parseBoolean(value);
      case "Double", "double" -> Double.parseDouble(value);
      case "Float", "float" -> Float.parseFloat(value);
      case "Long", "long" -> Long.parseLong(value);
      case "BigDecimal" -> new BigDecimal(value);
      case "Date" -> {
        try {
          yield new SimpleDateFormat("yyyy-MM-dd").parse(value);
        } catch (ParseException e) {
          log.severe(e.getMessage());
          yield null;
        }
      }
      default -> value;
    };
  }
}
