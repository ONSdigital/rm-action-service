
package uk.gov.ons.ctp.response.kirona.drs;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for dataTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="dataTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="undefined"/>
 *     &lt;enumeration value="string"/>
 *     &lt;enumeration value="numeric"/>
 *     &lt;enumeration value="date"/>
 *     &lt;enumeration value="datetime"/>
 *     &lt;enumeration value="time"/>
 *     &lt;enumeration value="bool"/>
 *     &lt;enumeration value="url"/>
 *     &lt;enumeration value="fixedUrl"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "dataTypeType")
@XmlEnum
public enum DataTypeType {

    @XmlEnumValue("undefined")
    UNDEFINED("undefined"),
    @XmlEnumValue("string")
    STRING("string"),
    @XmlEnumValue("numeric")
    NUMERIC("numeric"),
    @XmlEnumValue("date")
    DATE("date"),
    @XmlEnumValue("datetime")
    DATETIME("datetime"),
    @XmlEnumValue("time")
    TIME("time"),
    @XmlEnumValue("bool")
    BOOL("bool"),
    @XmlEnumValue("url")
    URL("url"),
    @XmlEnumValue("fixedUrl")
    FIXED_URL("fixedUrl");
    private final String value;

    DataTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DataTypeType fromValue(String v) {
        for (DataTypeType c: DataTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
