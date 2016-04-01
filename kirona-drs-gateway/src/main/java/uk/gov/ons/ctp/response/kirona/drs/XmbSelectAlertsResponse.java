
package uk.gov.ons.ctp.response.kirona.drs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for xmbSelectAlertsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="xmbSelectAlertsResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://autogenerated.OTWebServiceApi.xmbrace.com/}commandResponse">
 *       &lt;sequence>
 *         &lt;element name="theAlerts" type="{http://autogenerated.OTWebServiceApi.xmbrace.com/}alert" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xmbSelectAlertsResponse", propOrder = {
    "theAlerts"
})
public class XmbSelectAlertsResponse
    extends CommandResponse
{

    protected List<Alert> theAlerts;

    /**
     * Gets the value of the theAlerts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the theAlerts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTheAlerts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Alert }
     * 
     * 
     */
    public List<Alert> getTheAlerts() {
        if (theAlerts == null) {
            theAlerts = new ArrayList<Alert>();
        }
        return this.theAlerts;
    }

}
