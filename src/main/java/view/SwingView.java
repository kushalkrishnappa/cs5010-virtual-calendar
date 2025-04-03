package view;

import controller.CalendarFeatures;
import java.awt.BorderLayout;
import java.time.LocalDate;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

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
    add(operationButtonsPanel, BorderLayout.WEST);
    add(datesPanel, BorderLayout.CENTER);
  }

  @Override
  public void showDayViewDialog(LocalDate date) {
    // TODO - implement a pop up dialog
    System.out.println("Pop up a dialog");
  }

  @Override
  public void setAvailableCalendars(String[] calendars) {
    bannerPanel.setCalendarSelector(calendars);
  }

  @Override
  public void setFeatures(CalendarFeatures features) {
    bannerPanel.setFeatures(features);
    datesPanel.setFeatures(features);
  }

  @Override
  public void displayMessage(String output) {
    System.out.println(output);
    JOptionPane.showMessageDialog(this, output, "", JOptionPane.PLAIN_MESSAGE);
  }

  @Override
  public void displayError(String error) {
    JOptionPane.showMessageDialog(this, error, "", JOptionPane.ERROR_MESSAGE);
  }

  @Override
  public Readable getInputStream() {
    throw new UnsupportedOperationException("Not supported in GUI mode.");
  }
}
