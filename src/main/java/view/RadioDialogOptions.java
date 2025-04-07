package view;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class RadioDialogOptions {

  public static String show(Component parentComponent, String title, String[] options) {

    // create a Panel to hold the radio buttons
    JPanel radioPanel = new JPanel();
    radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));

    // create a ButtonGroup
    ButtonGroup group = new ButtonGroup();

    // create and add a JRadioButton for each option string
    for (int i = 0; i < options.length; i++) {
      JRadioButton radioButton = new JRadioButton(options[i]);
      radioButton.setActionCommand(options[i]); // Store the option string

      // select the first button by default
      if (i == 0) {
        radioButton.setSelected(true);
      }

      group.add(radioButton);
      radioPanel.add(radioButton);
      // vertical space between buttons
      if (i < options.length - 1) {
        radioPanel.add(Box.createRigidArea(new Dimension(0, 5)));
      }
    }

    // show the panel in a JOptionPane dialog
    int result = JOptionPane.showOptionDialog(
        parentComponent,
        radioPanel,
        title,
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE,
        null,
        null,
        null
    );

    // process the result
    if (result == JOptionPane.OK_OPTION) {
      for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements(); ) {
        AbstractButton button = buttons.nextElement();
        if (button.isSelected()) {
          return button.getActionCommand();
        }
      }
      // return null as a fallback
      return null;
    } else {
      // user clicked cancel or closed the dialog
      return null;
    }
  }
}