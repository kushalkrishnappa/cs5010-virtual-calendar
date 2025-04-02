package controller;

public class CalendarFeaturesAdaptor implements CalendarFeatures {

  final private CalendarFeatures controller;

  public CalendarFeaturesAdaptor(CalendarFeatures controller) {
    this.controller = controller;
  }

  @Override
  public void importCalendarFromFile(String filePath) {
    controller.importCalendarFromFile(filePath);
  }
}
