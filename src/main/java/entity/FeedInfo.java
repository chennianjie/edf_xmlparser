/*
 * FeedInfo.java
 *
 * Copyright Reuters Ltd. 2000
 */

package entity;

/**
 * Provides static access to the feed name and feed database schema
 */

public class FeedInfo {

  private String feedName;
  String controlSchema;

  public FeedInfo() {}

  /**
   * Gets the name of the feed
   *
   * @return     the name of the feed
   */
  public String getFeedName() {
    return feedName;
  }

  /**
   * Sets the name of the feed
   */
  public void setFeedName(String n) {
    feedName = n;
  }

  /**
   * Gets the database schema used by the feed for control tables
   *
   * @return      the database schema
   */
  public String getControlSchema() {
    return controlSchema;
  }

  /**
   * Sets the name of the control schema
   */
  public void setControlSchema(String cs) {
    controlSchema = cs;
  }
  
}

