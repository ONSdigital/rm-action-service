package uk.gov.ons.ctp.response.lib.common.time;

import java.sql.Timestamp;
import net.sourceforge.cobertura.CoverageIgnore;

/** Centralized DateTime handling for CTP */
@CoverageIgnore
public class DateTimeUtil {

  /**
   * Looks like overkill I know - but this ensures that we consistently stamp model objects with UTC
   * datetime
   *
   * @return The current time in UTC
   */
  public static Timestamp nowUTC() {
    return new Timestamp(System.currentTimeMillis());
  }
}
