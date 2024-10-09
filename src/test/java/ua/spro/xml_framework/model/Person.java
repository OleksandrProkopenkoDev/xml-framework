package ua.spro.xml_framework.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import ua.spro.xml_framework.annotations.XmlAttribute;
import ua.spro.xml_framework.annotations.XmlElement;
import ua.spro.xml_framework.annotations.XmlRootElement;

@XmlRootElement(name = "person")
public class Person {
  @XmlAttribute private int id;
  @XmlElement private String firstName;
  @XmlAttribute private String email;
  @XmlElement private Integer age;

  @XmlElement private LocalDate birthDate;

  @XmlElement private Boolean isEmployed;
  @XmlElement private BigDecimal salary;

  public Person() {}

  public Person(
      int id,
      String firstName,
      String email,
      Integer age,
      LocalDate birthDate,
      Boolean isEmployed,
      BigDecimal salary) {
    this.age = age;
    this.birthDate = birthDate;
    this.email = email;
    this.firstName = firstName;
    this.id = id;
    this.isEmployed = isEmployed;
    this.salary = salary;
  }

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public String getBirthDateAsString() {
    return birthDate != null ? birthDate.format(DATE_FORMATTER) : null;
  }


  @Override
  public String toString() {
    return "Person{"
        + "age="
        + age
        + ", id="
        + id
        + ", firstName='"
        + firstName
        + '\''
        + ", email='"
        + email
        + '\''
        + ", birthDate="
        + getBirthDateAsString()
        + ", isEmployed="
        + isEmployed
        + ", salary="
        + salary
        + '}';
  }
}
