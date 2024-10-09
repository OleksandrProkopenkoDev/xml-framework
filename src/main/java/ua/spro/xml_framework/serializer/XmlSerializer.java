package ua.spro.xml_framework.serializer;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Logger;
import ua.spro.xml_framework.annotations.XmlAttribute;
import ua.spro.xml_framework.annotations.XmlElement;
import ua.spro.xml_framework.annotations.XmlRootElement;
import ua.spro.xml_framework.util.ReflectionUtil;

public class XmlSerializer {

  private static final Logger log = Logger.getLogger(XmlSerializer.class.getName());

  public String marshal(Object obj) {
    Class<?> clazz = obj.getClass();

    String rootElementName = getRootElementName(clazz);

    StringBuilder xmlBuilder = new StringBuilder();
    xmlBuilder.append("<").append(rootElementName);

    // Append attributes
    appendAttributes(obj, clazz, xmlBuilder);
    xmlBuilder.append(">");

    // Append elements
    appendElements(obj, clazz, xmlBuilder);

    xmlBuilder.append("</").append(rootElementName).append(">");
    return xmlBuilder.toString();
  }

  private void appendElements(Object obj, Class<?> clazz, StringBuilder xmlBuilder) {
    // process elements
    List<Field> elementFields = ReflectionUtil.getAnnotatedFields(clazz, XmlElement.class);
    elementFields.forEach(
        field -> {
          try {
            field.setAccessible(true);
            XmlElement xmlElement = field.getAnnotation(XmlElement.class);
            String elementName = xmlElement.name().isEmpty() ? field.getName() : xmlElement.name();
            Object value = field.get(obj);
            if (value != null) {
              xmlBuilder
                  .append("<")
                  .append(elementName)
                  .append(">")
                  .append(value)
                  .append("</")
                  .append(elementName)
                  .append(">");
            }
          } catch (IllegalAccessException e) {
            log.severe(e.getMessage());
            throw new RuntimeException("Error accessing field: "+ field.getName(), e);
          }
        });
  }

  private void appendAttributes(Object obj, Class<?> clazz, StringBuilder xmlBuilder) {
    // process attributes
    List<Field> attributeFields = ReflectionUtil.getAnnotatedFields(clazz, XmlAttribute.class);

    attributeFields.forEach(
        field -> {
          try {
            field.setAccessible(true);
            XmlAttribute xmlAttribute = field.getAnnotation(XmlAttribute.class);
            String attributeName =
                xmlAttribute.name().isEmpty() ? field.getName() : xmlAttribute.name();
            Object value = field.get(obj);
            if (value != null) {
              xmlBuilder.append(" ").append(attributeName).append("=\"").append(value).append("\"");
            }
          } catch (IllegalAccessException e) {
            throw new RuntimeException("Error accessing field: "+ field.getName(), e);
          }
        });
  }

  private String getRootElementName(Class<?> clazz) {
    // check if the class has @XmlRootElement annotation
    if (!clazz.isAnnotationPresent(XmlRootElement.class)) {
      throw new IllegalArgumentException(
          "Class " + clazz.getName() + " must be annotated with @XmlRootElement");
    }

    XmlRootElement rootElement = clazz.getAnnotation(XmlRootElement.class);
    return rootElement.name().isEmpty() ? clazz.getSimpleName().toLowerCase() : rootElement.name();
  }
}
