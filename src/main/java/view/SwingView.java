package view;

import controller.CalendarFeatures;
import java.awt.BorderLayout;
import javax.swing.JFrame;

public class SwingView extends JFrame implements IGUIView {

  private BannerPanel bannerPanel;

  private OperationButtonsPanel operationButtonsPanel;

  private DatesPanel datesPanel;

  public SwingView() {
    // setup the main JFrame
    setTitle("Calendar Application");
    setSize(1000, 650);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // init the components
    initComponents();
    layoutComponents();

    // make the UI visible
    setVisible(true);
  }

  private void initComponents() {
    bannerPanel = new BannerPanel();
    operationButtonsPanel = new OperationButtonsPanel();
    datesPanel = new DatesPanel();
  }

  private void layoutComponents() {
    add(bannerPanel, BorderLayout.NORTH);
  }

  @Override
  public void setAvailableCalendars(String[] calendars) {
    bannerPanel.setCalendarSelector(calendars);
  }

  @Override
  public void setFeatures(CalendarFeatures features) {
    bannerPanel.setFeatures(features);
  }

  @Override
  public void displayMessage(String output) {
    // TODO: Implement message display
  }

  @Override
  public void displayError(String error) {
    // TODO: Implement error display
  }

  @Override
  public Readable getInputStream() {
    throw new UnsupportedOperationException("Not supported in GUI mode.");
  }
}
