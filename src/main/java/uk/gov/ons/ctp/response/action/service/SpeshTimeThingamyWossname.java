package uk.gov.ons.ctp.response.action.service;

public class SpeshTimeThingamyWossname {
  public static long partyTime = 0;
  public static long caseTime = 0;
  public static long decorationTime = 0;

  public static synchronized void addToPartyTime(long value) {
    partyTime += value;
  }

  public static synchronized void addToCaseTime(long value) {
    caseTime += value;
  }

  public static synchronized void addToDecorationTime(long value) {
    decorationTime += value;
  }
}
