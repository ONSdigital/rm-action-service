
package uk.gov.ons.ctp.response.kirona.drs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for entity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="entity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "entity")
@XmlSeeAlso({
    Location.class,
    DaySlotsInfo.class,
    Sector.class,
    Resource.class,
    BusinessDataDefinition.class,
    ResourceTemplate.class,
    UnavailabilityReason.class,
    Order.class,
    OrderPattern.class,
    AvailablePeriod.class,
    LocationLine.class,
    SlotInfo.class,
    AvailableDay.class,
    Alert.class,
    BookingCode.class,
    BookingCodeDefinition.class,
    Unavailability.class,
    WorkingHour.class,
    CommandResponse.class,
    BusinessData.class,
    Booking.class,
    Command.class,
    Ability.class
})
public class Entity {


}
