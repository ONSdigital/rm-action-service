
package uk.gov.ons.ctp.response.kirona.drs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for xmbGetScheduleResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="xmbGetScheduleResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://autogenerated.OTWebServiceApi.xmbrace.com/}commandResponse">
 *       &lt;sequence>
 *         &lt;element name="theBookings" type="{http://autogenerated.OTWebServiceApi.xmbrace.com/}booking" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xmbGetScheduleResponse", propOrder = {
    "theBookings"
})
public class XmbGetScheduleResponse
    extends CommandResponse
{

    protected List<Booking> theBookings;

    /**
     * Gets the value of the theBookings property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the theBookings property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTheBookings().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Booking }
     * 
     * 
     */
    public List<Booking> getTheBookings() {
        if (theBookings == null) {
            theBookings = new ArrayList<Booking>();
        }
        return this.theBookings;
    }

}
