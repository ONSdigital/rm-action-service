
package uk.gov.ons.ctp.response.kirona.drs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for checkAvailability complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="checkAvailability">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="checkAvailability" type="{http://autogenerated.OTWebServiceApi.xmbrace.com/}xmbCheckAvailability" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "checkAvailability", propOrder = {
    "checkAvailability"
})
public class CheckAvailability {

    protected XmbCheckAvailability checkAvailability;

    /**
     * Gets the value of the checkAvailability property.
     * 
     * @return
     *     possible object is
     *     {@link XmbCheckAvailability }
     *     
     */
    public XmbCheckAvailability getCheckAvailability() {
        return checkAvailability;
    }

    /**
     * Sets the value of the checkAvailability property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmbCheckAvailability }
     *     
     */
    public void setCheckAvailability(XmbCheckAvailability value) {
        this.checkAvailability = value;
    }

}
