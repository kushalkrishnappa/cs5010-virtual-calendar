package controller;

public class CalendarFeaturesAdaptor implements CalendarFeatures {

  final private CalendarFeatures controller;

  public CalendarFeaturesAdaptor(CalendarFeatures controller) {
    this.controller = controller;
  }
}
