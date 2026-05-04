package bugtrackerprojectsh.GUI;
 
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer;
 
public class BugsTableFrame extends JFrame {
 
    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color BG_DEEP    = new Color(6,  11,  24);
    private static final Color BG_CARD    = new Color(13, 22,  44);
    private static final Color BG_ROW_ALT = new Color(17, 28,  54);
    private static final Color ACCENT     = new Color(0,  210, 255);
    private static final Color ACCENT2    = new Color(100, 60, 255);
    private static final Color TEXT_MAIN  = new Color(220, 235, 255);
    private static final Color TEXT_DIM   = new Color(90,  120, 165);
    private static final Color BORDER_CLR = new Color(30,  55, 105);
    private static final Color SEL_BG     = new Color(0,  140, 200, 120);
    private static final Color COLOR_DEL  = new Color(220, 60,  80);
    private static final Color COLOR_UPD  = new Color(60,  200, 130);
    private static final Color COLOR_CLOSE= new Color(255, 160,  30); // ── لون زرار Close Bug
 
    // Priority colours
    private static final Color PRI_HIGH   = new Color(255,  70, 100);
    private static final Color PRI_MED    = new Color(255, 180,  40);
    private static final Color PRI_LOW    = new Color(0,   210, 140);
 
    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font FONT_SUB    = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  11);
    private static final Font FONT_CELL   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD,  12);
 
    // ── State ─────────────────────────────────────────────────────────────────
    private JTable            table;
    private DefaultTableModel model;
    private float             orb1Angle = 0f;
    private Timer             bgTimer;
    private final String      testerName;
 
    // ─────────────────────────────────────────────────────────────────────────
    public BugsTableFrame(String name) {
        this.testerName = name;
 
        setTitle("Bug Tracker — All Bugs");
        setSize(1100, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setBackground(BG_DEEP);
 
        JPanel root = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBackground(g);
            }
        };
        root.setBackground(BG_DEEP);
        root.setOpaque(true);
        setContentPane(root);
 
        root.add(buildHeader(),     BorderLayout.NORTH);
        root.add(buildTablePanel(), BorderLayout.CENTER);
        root.add(buildFooter(name), BorderLayout.SOUTH);
 
        addWindowControls(root);
        makeDraggable(root);
 
        loadBugs();
        setVisible(true);
        startBgAnimation(root);
    }
 
    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, getHeight()-1, ACCENT, getWidth(), getHeight()-1, ACCENT2);
                g2.setPaint(gp);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 70));
 
        JLabel icon = new JLabel("🐛");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        icon.setBounds(20, 12, 44, 44);
        header.add(icon);
 
        JLabel title = new JLabel("All Bugs") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, ACCENT, getWidth(), 0, ACCENT2));
                g2.setFont(getFont());
                g2.drawString(getText(), 0, g2.getFontMetrics().getAscent());
                g2.dispose();
            }
        };
        title.setFont(FONT_TITLE);
        title.setBounds(72, 12, 300, 32);
        header.add(title);
 
        JLabel sub = new JLabel("Logged in as: " + testerName);
        sub.setFont(FONT_SUB);
        sub.setForeground(TEXT_DIM);
        sub.setBounds(73, 44, 400, 16);
        header.add(sub);
 
        return header;
    }
 
    // ── Table panel ───────────────────────────────────────────────────────────
    private JPanel buildTablePanel() {
        String[] cols = {
            "ID", "Name", "Type", "Priority", "Level",
            "Project", "Date", "Status", "Tester", "Dev", "Screenshot"
        };
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
 
        table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? BG_CARD : BG_ROW_ALT);
                    c.setForeground(TEXT_MAIN);
                } else {
                    c.setBackground(SEL_BG);
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        };
 
        table.setFont(FONT_CELL);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setOpaque(true);
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_MAIN);
        table.setSelectionBackground(SEL_BG);
        table.setSelectionForeground(Color.WHITE);
        table.setFocusable(false);
 
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String[] rowData = new String[model.getColumnCount()];
                        for (int i = 0; i < model.getColumnCount(); i++) {
                            Object val = model.getValueAt(row, i);
                            rowData[i] = val == null ? "" : val.toString();
                        }
                        showBugDetailsDialog(rowData);
                    }
                }
            }
        });
 
        JTableHeader th = table.getTableHeader();
        th.setFont(FONT_HEADER);
        th.setBackground(new Color(8, 16, 38));
        th.setForeground(ACCENT);
        th.setPreferredSize(new Dimension(0, 36));
        th.setReorderingAllowed(false);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0,
            new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 80)));
 
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setBackground(new Color(8, 16, 38));
                setForeground(ACCENT);
                setFont(FONT_HEADER);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_CLR),
                    BorderFactory.createEmptyBorder(0, 8, 0, 8)
                ));
                setHorizontalAlignment(SwingConstants.LEFT);
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
 
        // ── Status cell renderer — بيلوّن الـ Closed باللون الأخضر ───────────
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String val = v == null ? "" : v.toString();
                if (val.equalsIgnoreCase("Closed")) {
                    setForeground(PRI_LOW);
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else if (val.equalsIgnoreCase("Open")) {
                    setForeground(PRI_HIGH);
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    setForeground(TEXT_MAIN);
                    setFont(FONT_CELL);
                }
                if (!sel) setBackground(r % 2 == 0 ? BG_CARD : BG_ROW_ALT);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
 
        // Priority renderer
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String val = v == null ? "" : v.toString().toLowerCase();
                setForeground(val.contains("high") ? PRI_HIGH
                            : val.contains("med")  ? PRI_MED
                            : val.contains("low")  ? PRI_LOW
                            : TEXT_MAIN);
                if (!sel) setBackground(r % 2 == 0 ? BG_CARD : BG_ROW_ALT);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
 
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 3 && i != 7) table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
 
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(BG_CARD);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
 
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER_CLR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.add(scroll, BorderLayout.CENTER);
 
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(12, 18, 0, 18));
        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }
 
    // ── Bug Details Dialog ────────────────────────────────────────────────────
    private void showBugDetailsDialog(String[] data) {
        JDialog dialog = new JDialog(this, "Bug Details", true);
        dialog.setUndecorated(true);
        dialog.setSize(480, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setBackground(new Color(0, 0, 0, 0));
 
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setPaint(new GradientPaint(0, 0, ACCENT, getWidth(), getHeight(), ACCENT2));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
 
        JPanel headerStrip = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(
                    0, 0, new Color(ACCENT.getRed(),  ACCENT.getGreen(),  ACCENT.getBlue(),  45),
                    getWidth(), 0, new Color(ACCENT2.getRed(), ACCENT2.getGreen(), ACCENT2.getBlue(), 45)
                ));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new GradientPaint(0, getHeight()-1, ACCENT, getWidth(), getHeight()-1, ACCENT2));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        headerStrip.setOpaque(false);
        headerStrip.setBounds(0, 0, 480, 72);
        card.add(headerStrip);
 
        JLabel iconLbl = new JLabel("🐛");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        iconLbl.setBounds(20, 16, 40, 40);
        headerStrip.add(iconLbl);
 
        JLabel idLbl = new JLabel("Bug #" + (data.length > 0 ? data[0] : "?"));
        idLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        idLbl.setForeground(ACCENT);
        idLbl.setBounds(66, 13, 360, 22);
        headerStrip.add(idLbl);
 
        JLabel bugNameLbl = new JLabel(data.length > 1 ? data[1] : "");
        bugNameLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bugNameLbl.setForeground(TEXT_DIM);
        bugNameLbl.setBounds(67, 37, 380, 18);
        headerStrip.add(bugNameLbl);
 
        JButton closeX = winBtn("✕");
        closeX.setBounds(440, 10, 28, 24);
        closeX.addActionListener(e -> dialog.dispose());
        card.add(closeX);
 
        String[] labels = {"Type", "Priority", "Level", "Project", "Date", "Status", "Tester", "Developer"};
        int startY = 84;
        int rowH   = 42;
 
        for (int i = 0; i < labels.length; i++) {
            int y = startY + i * rowH;
            if (i % 2 == 0) {
                JPanel rowBg = new JPanel() {
                    @Override protected void paintComponent(Graphics g) {
                        g.setColor(new Color(255, 255, 255, 7));
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                };
                rowBg.setOpaque(false);
                rowBg.setBounds(14, y - 2, 452, rowH - 2);
                card.add(rowBg);
            }
 
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(TEXT_DIM);
            lbl.setBounds(28, y + 10, 110, 18);
            card.add(lbl);
 
            String val = (i + 2 < data.length) ? data[i + 2] : "";
            JLabel valLbl = new JLabel(val.isEmpty() ? "—" : val);
            valLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
 
            if (labels[i].equals("Priority")) {
                String v = val.toLowerCase();
                valLbl.setForeground(v.contains("high") ? PRI_HIGH
                                   : v.contains("med")  ? PRI_MED
                                   : v.contains("low")  ? PRI_LOW
                                   : TEXT_MAIN);
                valLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            } else if (labels[i].equals("Status")) {
                valLbl.setForeground(val.equalsIgnoreCase("Closed") ? PRI_LOW : PRI_HIGH);
                valLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            } else {
                valLbl.setForeground(TEXT_MAIN);
            }
 
            valLbl.setBounds(155, y + 10, 295, 18);
            card.add(valLbl);
 
            JPanel div = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    g.setColor(BORDER_CLR);
                    g.fillRect(0, 0, getWidth(), 1);
                }
            };
            div.setOpaque(false);
            div.setBounds(20, y + rowH - 4, 440, 1);
            card.add(div);
        }
 
        int imgSectionY = startY + labels.length * rowH + 6;
 
        JLabel ssLbl = new JLabel("Screenshot");
        ssLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        ssLbl.setForeground(TEXT_DIM);
        ssLbl.setBounds(28, imgSectionY, 120, 18);
        card.add(ssLbl);
 
        String imgPath = data.length > 10 ? data[10] : "";
        int previewY = imgSectionY + 24;
        int previewH = 140;
 
        JLabel imgBox = new JLabel("No screenshot available", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(10, 18, 38));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDER_CLR);
                g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[]{5, 4}, 0));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        imgBox.setOpaque(false);
        imgBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        imgBox.setForeground(TEXT_DIM);
        imgBox.setBounds(20, previewY, 440, previewH);
        card.add(imgBox);
 
        if (!imgPath.isEmpty()) {
            try {
                java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(new java.io.File(imgPath));
                if (img != null) {
                    int maxW = 436, maxH = 136;
                    double scale = Math.min((double) maxW / img.getWidth(), (double) maxH / img.getHeight());
                    int newW = (int)(img.getWidth()  * scale);
                    int newH = (int)(img.getHeight() * scale);
                    java.awt.Image scaled = img.getScaledInstance(newW, newH, java.awt.Image.SCALE_SMOOTH);
                    imgBox.setIcon(new ImageIcon(scaled));
                    imgBox.setText("");
                } else {
                    imgBox.setText("⚠ Cannot read image file");
                }
            } catch (Exception ex) {
                imgBox.setText("⚠ " + new java.io.File(imgPath).getName());
            }
        }
 
        int btnY = previewY + previewH + 14;
        JButton okBtn = buildButton("  Close  ", ACCENT);
        okBtn.setBounds(175, btnY, 130, 36);
        okBtn.addActionListener(e -> dialog.dispose());
        card.add(okBtn);
 
        int totalH = btnY + 36 + 16;
        dialog.setSize(480, totalH);
        dialog.setLocationRelativeTo(this);
        makeDraggable(card);
        dialog.setContentPane(card);
        dialog.setVisible(true);
    }
 
    // ── Update Dialog ─────────────────────────────────────────────────────────
    private void showUpdateDialog(int row) {
        String[] data = new String[model.getColumnCount()];
        for (int i = 0; i < model.getColumnCount(); i++) {
            Object val = model.getValueAt(row, i);
            data[i] = val == null ? "" : val.toString();
        }
 
        JDialog dialog = new JDialog(this, "Update Bug", true);
        dialog.setUndecorated(true);
        dialog.setSize(500, 620);
        dialog.setLocationRelativeTo(this);
 
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setPaint(new GradientPaint(0, 0, COLOR_UPD, getWidth(), getHeight(), ACCENT));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
 
        JPanel headerStrip = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(
                    0, 0, new Color(COLOR_UPD.getRed(), COLOR_UPD.getGreen(), COLOR_UPD.getBlue(), 40),
                    getWidth(), 0, new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 40)
                ));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new GradientPaint(0, getHeight()-1, COLOR_UPD, getWidth(), getHeight()-1, ACCENT));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        headerStrip.setOpaque(false);
        headerStrip.setBounds(0, 0, 500, 70);
        card.add(headerStrip);
 
        JLabel icon = new JLabel("✏️");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        icon.setBounds(20, 16, 38, 38);
        headerStrip.add(icon);
 
        JLabel titleLbl = new JLabel("Update Bug #" + data[0]);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(COLOR_UPD);
        titleLbl.setBounds(64, 14, 380, 22);
        headerStrip.add(titleLbl);
 
        JLabel subLbl = new JLabel("Edit the fields below and save changes");
        subLbl.setFont(FONT_SUB);
        subLbl.setForeground(TEXT_DIM);
        subLbl.setBounds(65, 38, 380, 16);
        headerStrip.add(subLbl);
 
        JButton closeX = winBtn("✕");
        closeX.setBounds(460, 10, 28, 24);
        closeX.addActionListener(e -> dialog.dispose());
        card.add(closeX);
 
        String[] fieldLabels = {"Name", "Type", "Priority", "Level", "Project", "Date", "Status", "Dev", "Screenshot"};
        int[]    dataIndices = {1, 2, 3, 4, 5, 6, 7, 9, 10};
        JTextField[] fields = new JTextField[fieldLabels.length];
 
        int startY = 82;
        int rowH   = 48;
 
        for (int i = 0; i < fieldLabels.length; i++) {
            int y = startY + i * rowH;
 
            JLabel lbl = new JLabel(fieldLabels[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(TEXT_DIM);
            lbl.setBounds(24, y, 110, 18);
            card.add(lbl);
 
            JTextField tf = new JTextField(data[dataIndices[i]]);
            tf.setFont(FONT_CELL);
            tf.setForeground(TEXT_MAIN);
            tf.setBackground(new Color(10, 18, 42));
            tf.setCaretColor(ACCENT);
            tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
            tf.setBounds(140, y - 2, 336, 30);
            tf.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) {
                    tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_UPD, 1),
                        BorderFactory.createEmptyBorder(4, 8, 4, 8)));
                }
                @Override public void focusLost(FocusEvent e) {
                    tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_CLR, 1),
                        BorderFactory.createEmptyBorder(4, 8, 4, 8)));
                }
            });
            card.add(tf);
            fields[i] = tf;
 
            JPanel div = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    g.setColor(BORDER_CLR);
                    g.fillRect(0, 0, getWidth(), 1);
                }
            };
            div.setOpaque(false);
            div.setBounds(20, y + 36, 456, 1);
            card.add(div);
        }
 
        int btnY = startY + fieldLabels.length * rowH + 10;
 
        JButton cancelBtn = buildButton("  Cancel  ", TEXT_DIM);
        cancelBtn.setBounds(160, btnY, 130, 36);
        cancelBtn.addActionListener(e -> dialog.dispose());
        card.add(cancelBtn);
 
        JButton saveBtn = buildButton("  Save  ", COLOR_UPD);
        saveBtn.setBounds(306, btnY, 130, 36);
        saveBtn.addActionListener(e -> {
            String[] updated = new String[model.getColumnCount()];
            updated[0] = data[0]; // ID stays
            updated[8] = data[8]; // Tester stays
            for (int i = 0; i < fieldLabels.length; i++) {
                updated[dataIndices[i]] = fields[i].getText().trim();
            }
            for (int c = 0; c < model.getColumnCount(); c++) {
                model.setValueAt(updated[c], row, c);
            }
            saveBugsToFile();
            // ✅ Refresh الجدول من الفايل بعد الحفظ
            loadBugs();
            dialog.dispose();
            showInfoToast("Bug #" + data[0] + " updated successfully!", COLOR_UPD);
        });
        card.add(saveBtn);
 
        int totalH = btnY + 36 + 20;
        dialog.setSize(500, totalH);
        dialog.setLocationRelativeTo(this);
        makeDraggable(card);
        dialog.setContentPane(card);
        dialog.setVisible(true);
    }
 
    // ── ✅ Close Bug Dialog ────────────────────────────────────────────────────
    private void showCloseBugDialog(int row) {
        String bugId   = model.getValueAt(row, 0).toString();
        String bugName = model.getValueAt(row, 1).toString();
        String status  = model.getValueAt(row, 7).toString();
 
        // لو الـ bug بالفعل مغلق
        if (status.equalsIgnoreCase("Closed")) {
            showInfoToast("Bug #" + bugId + " is already Closed.", COLOR_CLOSE);
            return;
        }
 
        JDialog dialog = new JDialog(this, "Close Bug", true);
        dialog.setUndecorated(true);
        dialog.setSize(420, 220);
        dialog.setLocationRelativeTo(this);
 
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setPaint(new GradientPaint(0, 0, COLOR_CLOSE, getWidth(), getHeight(), new Color(200, 100, 0)));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
 
        JLabel warnIcon = new JLabel("🔒", SwingConstants.CENTER);
        warnIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        warnIcon.setBounds(0, 20, 420, 44);
        card.add(warnIcon);
 
        JLabel titleLbl = new JLabel("Close Bug #" + bugId + "?", SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(COLOR_CLOSE);
        titleLbl.setBounds(0, 68, 420, 22);
        card.add(titleLbl);
 
        String displayName = bugName.length() > 45 ? bugName.substring(0, 42) + "..." : bugName;
        JLabel nameLbl = new JLabel("\"" + displayName + "\"", SwingConstants.CENTER);
        nameLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nameLbl.setForeground(TEXT_DIM);
        nameLbl.setBounds(0, 94, 420, 18);
        card.add(nameLbl);
 
        JLabel subLbl = new JLabel("Status will be changed to  \"Closed\".", SwingConstants.CENTER);
        subLbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        subLbl.setForeground(new Color(180, 140, 60));
        subLbl.setBounds(0, 116, 420, 16);
        card.add(subLbl);
 
        JButton cancelBtn = buildButton("  Cancel  ", TEXT_DIM);
        cancelBtn.setBounds(100, 150, 120, 36);
        cancelBtn.addActionListener(e -> dialog.dispose());
        card.add(cancelBtn);
 
        JButton closeBtn = buildButton("  Close Bug  ", COLOR_CLOSE);
        closeBtn.setBounds(235, 150, 130, 36);
        closeBtn.addActionListener(e -> {
            // ✅ غيّر الـ Status في الـ model
            model.setValueAt("Closed", row, 7);
            // ✅ احفظ في الـ file
            saveBugsToFile();
            // ✅ أعد تحميل الجدول من الـ file عشان يتحدث فوراً
            loadBugs();
            dialog.dispose();
            showInfoToast("Bug #" + bugId + " has been Closed.", COLOR_CLOSE);
        });
        card.add(closeBtn);
 
        makeDraggable(card);
        dialog.setContentPane(card);
        dialog.setVisible(true);
    }
 
    // ── Delete Confirmation Dialog ────────────────────────────────────────────
    private void showDeleteDialog(int row) {
        String bugId   = model.getValueAt(row, 0).toString();
        String bugName = model.getValueAt(row, 1).toString();
 
        JDialog dialog = new JDialog(this, "Delete Bug", true);
        dialog.setUndecorated(true);
        dialog.setSize(420, 230);
        dialog.setLocationRelativeTo(this);
 
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setPaint(new GradientPaint(0, 0, COLOR_DEL, getWidth(), getHeight(), new Color(180, 30, 60)));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
 
        JLabel warnIcon = new JLabel("⚠️", SwingConstants.CENTER);
        warnIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        warnIcon.setBounds(0, 24, 420, 44);
        card.add(warnIcon);
 
        JLabel titleLbl = new JLabel("Delete Bug #" + bugId + "?", SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(COLOR_DEL);
        titleLbl.setBounds(0, 74, 420, 22);
        card.add(titleLbl);
 
        String displayName = bugName.length() > 45 ? bugName.substring(0, 42) + "..." : bugName;
        JLabel nameLbl = new JLabel("\"" + displayName + "\"", SwingConstants.CENTER);
        nameLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nameLbl.setForeground(TEXT_DIM);
        nameLbl.setBounds(0, 100, 420, 18);
        card.add(nameLbl);
 
        JLabel warnLbl = new JLabel("This action cannot be undone.", SwingConstants.CENTER);
        warnLbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        warnLbl.setForeground(new Color(180, 80, 90));
        warnLbl.setBounds(0, 122, 420, 16);
        card.add(warnLbl);
 
        JButton cancelBtn = buildButton("  Cancel  ", TEXT_DIM);
        cancelBtn.setBounds(105, 158, 120, 36);
        cancelBtn.addActionListener(e -> dialog.dispose());
        card.add(cancelBtn);
 
        JButton deleteBtn = buildButton("  Delete  ", COLOR_DEL);
        deleteBtn.setBounds(240, 158, 120, 36);
        deleteBtn.addActionListener(e -> {
            model.removeRow(row);
            saveBugsToFile();
            // ✅ Refresh بعد الحذف كمان
            loadBugs();
            dialog.dispose();
            showInfoToast("Bug #" + bugId + " deleted.", COLOR_DEL);
        });
        card.add(deleteBtn);
 
        makeDraggable(card);
        dialog.setContentPane(card);
        dialog.setVisible(true);
    }
 
    // ── Toast notification ────────────────────────────────────────────────────
    private void showInfoToast(String message, Color accent) {
        JWindow toast = new JWindow(this);
        toast.setBackground(new Color(0, 0, 0, 0));
 
        JPanel panel = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(BG_CARD.getRed(), BG_CARD.getGreen(), BG_CARD.getBlue(), 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 12, 12);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(300, 42));
 
        JLabel lbl = new JLabel("✓  " + message);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(accent);
        lbl.setBounds(14, 0, 272, 42);
        panel.add(lbl);
 
        toast.setContentPane(panel);
        toast.pack();
        Point loc = getLocation();
        toast.setLocation(loc.x + (getWidth() - 300) / 2, loc.y + getHeight() - 70);
        toast.setVisible(true);
        new Timer(2500, e -> toast.dispose()) {{ setRepeats(false); start(); }};
    }
 
    // ── Footer ────────────────────────────────────────────────────────────────
    private JPanel buildFooter(String name) {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
 
        JButton back = buildButton("⬅  Back", ACCENT);
        back.addActionListener(e -> { new TesterFrame(name); dispose(); });
 
        JButton refresh = buildButton("↻  Refresh", new Color(60, 180, 120));
        refresh.addActionListener(e -> loadBugs());
 
        JButton updateBtn = buildButton("✏  Update", COLOR_UPD);
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) showInfoToast("Please select a bug to update.", ACCENT);
            else           showUpdateDialog(row);
        });
 
        // ✅ زرار Close Bug الجديد
        JButton closeBugBtn = buildButton("🔒  Close Bug", COLOR_CLOSE);
        closeBugBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) showInfoToast("Please select a bug to close.", COLOR_CLOSE);
            else           showCloseBugDialog(row);
        });
 
        JButton deleteBtn = buildButton("🗑  Delete", COLOR_DEL);
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) showInfoToast("Please select a bug to delete.", ACCENT);
            else           showDeleteDialog(row);
        });
 
        footer.add(back);
        footer.add(refresh);
        footer.add(updateBtn);
        footer.add(closeBugBtn);  // ✅ أضفناه هنا
        footer.add(deleteBtn);
 
        JPanel fp = new JPanel(new BorderLayout());
        fp.setOpaque(false);
        fp.add(footer, BorderLayout.WEST);
        return fp;
    }
 
    // ── Load & Save ───────────────────────────────────────────────────────────
    private void loadBugs() {
        model.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader("bug.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 11) model.addRow(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    private void saveBugsToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("bug.txt", false))) {
            for (int r = 0; r < model.getRowCount(); r++) {
                StringBuilder sb = new StringBuilder();
                for (int c = 0; c < model.getColumnCount(); c++) {
                    if (c > 0) sb.append(",");
                    Object val = model.getValueAt(r, c);
                    sb.append(val == null ? "" : val.toString());
                }
                pw.println(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    // ── Background ────────────────────────────────────────────────────────────
    private void drawBackground(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BG_DEEP);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(new Color(25, 45, 90, 12));
        for (int x = 0; x < getWidth(); x += 32)  g2.drawLine(x, 0, x, getHeight());
        for (int y = 0; y < getHeight(); y += 32)  g2.drawLine(0, y, getWidth(), y);
        double r1 = Math.toRadians(orb1Angle);
        drawOrb(g2, (int)(getWidth()*0.08 + Math.cos(r1)*50), (int)(getHeight()*0.15 + Math.sin(r1)*35), 220, ACCENT,  30);
        drawOrb(g2, (int)(getWidth()*0.92 + Math.sin(r1)*40), (int)(getHeight()*0.85 + Math.cos(r1)*30), 200, ACCENT2, 28);
        g2.dispose();
    }
 
    private void drawOrb(Graphics2D g2, int cx, int cy, int r, Color c, int maxAlpha) {
        for (int i = r; i > 0; i -= 14) {
            int alpha = (int)(maxAlpha * (1f - (float)i / r));
            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
            g2.fillOval(cx - i, cy - i, i*2, i*2);
        }
    }
 
    private void startBgAnimation(JPanel root) {
        bgTimer = new Timer(30, e -> { orb1Angle = (orb1Angle + 0.6f) % 360; root.repaint(); });
        bgTimer.start();
    }
 
    // ── Button builder ────────────────────────────────────────────────────────
    private JButton buildButton(String text, Color accent) {
        JButton b = new JButton(text) {
            private float hov = 0f;
            private Timer ht;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { anim(1f); }
                    public void mouseExited (MouseEvent e) { anim(0f); }
                });
            }
            void anim(float t) {
                if (ht != null) ht.stop();
                ht = new Timer(16, ev -> { hov += (t-hov)*0.2f; if(Math.abs(hov-t)<0.01f){hov=t;ht.stop();} repaint(); });
                ht.start();
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0,
                    new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 200),
                    getWidth(), getHeight(),
                    new Color(accent.getRed()/2, accent.getGreen()/2, accent.getBlue()/2+80, 200)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                if (hov > 0) {
                    g2.setColor(new Color(255,255,255,(int)(hov*30)));
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                }
                g2.setColor(Color.WHITE);
                g2.setFont(FONT_BTN);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(140, 36));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
 
    // ── Window controls ───────────────────────────────────────────────────────
    private void addWindowControls(JPanel root) {
        JPanel glass = new JPanel(null);
        glass.setOpaque(false);
        setGlassPane(glass);
        glass.setVisible(true);
 
        JButton closeG = winBtn("✕");
        closeG.setBounds(1062, 6, 28, 22);
        closeG.addActionListener(e -> { if (bgTimer != null) bgTimer.stop(); dispose(); });
 
        JButton miniG = winBtn("─");
        miniG.setBounds(1030, 6, 28, 22);
        miniG.addActionListener(e -> setState(Frame.ICONIFIED));
 
        glass.add(closeG);
        glass.add(miniG);
    }
 
    private JButton winBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        b.setForeground(TEXT_DIM);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setForeground(TEXT_MAIN); }
            public void mouseExited (MouseEvent e) { b.setForeground(TEXT_DIM);  }
        });
        return b;
    }
 
    private void makeDraggable(JComponent comp) {
        final Point[] drag = {null};
        comp.addMouseListener(new MouseAdapter() {
            public void mousePressed (MouseEvent e) { drag[0] = e.getPoint(); }
            public void mouseReleased(MouseEvent e) { drag[0] = null; }
        });
        comp.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (drag[0] == null) return;
                Point loc = getLocation();
                setLocation(loc.x + e.getX() - drag[0].x, loc.y + e.getY() - drag[0].y);
            }
        });
    }
 
    // ── Scroll bar ────────────────────────────────────────────────────────────
    private static class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            thumbColor = new Color(0, 150, 200, 120);
            trackColor = new Color(13, 22, 44);
        }
        @Override protected JButton createDecreaseButton(int o) { return invisBtn(); }
        @Override protected JButton createIncreaseButton(int o) { return invisBtn(); }
        private JButton invisBtn() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            return b;
        }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(r.x+2, r.y+2, r.width-4, r.height-4, 6, 6);
            g2.dispose();
        }
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(trackColor);
            g.fillRect(r.x, r.y, r.width, r.height);
        }
    }
}

