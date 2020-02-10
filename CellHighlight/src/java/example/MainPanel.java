// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());
    JTable table = new JTable(new DefaultTableModel(10, 10)) {
      private transient HighlightListener highlighter;
      @Override public void updateUI() {
        removeMouseListener(highlighter);
        removeMouseMotionListener(highlighter);
        super.updateUI();
        // setAutoCreateRowSorter(true);
        setRowSelectionAllowed(false);

        highlighter = new HighlightListener();
        addMouseListener(highlighter);
        addMouseMotionListener(highlighter);

        setDefaultRenderer(Object.class, new HighlightRenderer(highlighter));
        setDefaultRenderer(Number.class, new HighlightRenderer(highlighter));
      }
    };
    add(new JScrollPane(table));
    setPreferredSize(new Dimension(320, 240));
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(MainPanel::createAndShowGui);
  }

  private static void createAndShowGui() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
      ex.printStackTrace();
      Toolkit.getDefaultToolkit().beep();
    }
    JFrame frame = new JFrame("@title@");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.getContentPane().add(new MainPanel());
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}

class HighlightListener extends MouseAdapter {
  private static final Color HIGHLIGHT1 = new Color(0xC8_C8_FF);
  private static final Color HIGHLIGHT2 = new Color(0xF0_F0_FF);
  private int viewRowIndex = -1;
  private int viewColumnIndex = -1;

  public Optional<Color> getCellHighlightColor(int row, int column) {
    if (this.viewRowIndex == row || this.viewColumnIndex == column) {
      if (this.viewRowIndex == row && this.viewColumnIndex == column) {
        return Optional.of(HIGHLIGHT1);
      } else {
        return Optional.of(HIGHLIGHT2);
      }
    }
    return Optional.empty();
  }

  private void setHighlightTableCell(MouseEvent e) {
    Point pt = e.getPoint();
    Component c = e.getComponent();
    if (c instanceof JTable) {
      JTable table = (JTable) c;
      viewRowIndex = table.rowAtPoint(pt);
      viewColumnIndex = table.columnAtPoint(pt);
      if (viewRowIndex < 0 || viewColumnIndex < 0) {
        viewRowIndex = -1;
        viewColumnIndex = -1;
      }
      table.repaint();
    }
  }

  @Override public void mouseMoved(MouseEvent e) {
    setHighlightTableCell(e);
  }

  @Override public void mouseDragged(MouseEvent e) {
    setHighlightTableCell(e);
  }

  @Override public void mouseExited(MouseEvent e) {
    viewRowIndex = -1;
    viewColumnIndex = -1;
    e.getComponent().repaint();
  }
}

class HighlightRenderer extends DefaultTableCellRenderer {
  private final transient HighlightListener highlighter;

  protected HighlightRenderer(HighlightListener highlighter) {
    super();
    this.highlighter = highlighter;
  }

  @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    setHorizontalAlignment(value instanceof Number ? RIGHT : LEFT);
    setBackground(table.getBackground());
    highlighter.getCellHighlightColor(row, column).ifPresent(this::setBackground);
    return this;
  }
}
