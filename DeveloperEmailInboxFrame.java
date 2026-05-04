package bugtrackerprojectsh.GUI;
 
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
 
public class DeveloperEmailInboxFrame extends JFrame {
 
    // ── Palette (matches DeveloperFrame) ─────────────────────────────────────
    private static final Color BG_DEEP    = new Color(6,   11,  24);
    private static final Color BG_CARD    = new Color(13,  22,  44);
    private static final Color ACCENT     = new Color(0,  210, 255);
    private static final Color ACCENT2    = new Color(100, 60, 255);
    private static final Color EMAIL_CLR  = new Color(0,  210, 255);   // developer = cyan
    private static final Color EMAIL_CLR2 = new Color(100,  60, 255);  // developer = purple
    private static final Color TEXT_MAIN  = new Color(220, 235, 255);
    private static final Color TEXT_DIM   = new Color(90,  120, 165);
    private static final Color BORDER_CLR = new Color(30,   55, 105);
    private static final Color ROW_EVEN   = new Color(13,  22,  44);
    private static final Color ROW_ODD    = new Color(18,  30,  58);
    private static final Color ROW_SEL    = new Color(0,   80, 130, 180);
 
    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 10);
    private static final Font FONT_BADGE  = new Font("Segoe UI", Font.BOLD,   9);
 
    // ── State ─────────────────────────────────────────────────────────────────
    private String currentUser;
    private DefaultTableModel model;
    private JTable table;
    private JPanel detailPanel;
    private JLabel detailFrom, detailTo, detailSubject, detailMessage;
    private JSeparator detailSep;
    private JLabel hintLabel, emailIcon;
    private float orbAngle = 0f;
    private Timer bgTimer, fadeTimer, rowTimer;
    private JPanel root;
    private float fadeAlpha = 0f;
    private int animatedRows = 0;
    // allEmails: [0]=from, [1]=to, [2]=subject, [3]=message
    private List<String[]> allEmails = new ArrayList<>();
 
    // ─────────────────────────────────────────────────────────────────────────
    public DeveloperEmailInboxFrame() { this(null); }
 
    public DeveloperEmailInboxFrame(String name) {
        this.currentUser = name;
 
        setTitle("Bug Tracker Pro — Developer Email Inbox");
        setSize(860, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setBackground(BG_DEEP);
 
        root = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBackground(g);
            }
        };
        root.setBackground(BG_DEEP);
        setContentPane(root);
 
        buildTopBar();
        buildEmailPanel();
        buildDetailPanel();
        buildStatusBar();
        buildWindowControls();
 
        makeDraggable(root);
        startBgAnim();
        startFadeIn();
        setVisible(true);
    }
 
    // ── Top Bar ───────────────────────────────────────────────────────────────
    private void buildTopBar() {
        JPanel bar = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(13, 22, 44, 210));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // gradient bottom line matching DeveloperFrame header
                GradientPaint gp = new GradientPaint(0, getHeight()-1, ACCENT, getWidth(), getHeight()-1, ACCENT2);
                g2.setPaint(gp);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setBounds(0, 0, 860, 52);
        root.add(bar);
 
        JLabel icon = new JLabel("📧");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        icon.setBounds(18, 13, 28, 24);
        bar.add(icon);
 
        JLabel appTitle = new JLabel("Email Inbox");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        appTitle.setForeground(TEXT_MAIN);
        appTitle.setBounds(50, 16, 120, 20);
        bar.add(appTitle);
 
        // DEVELOPER badge (cyan instead of orange)
        JLabel roleBadge = pillLabel("DEVELOPER", ACCENT);
        roleBadge.setBounds(178, 17, 76, 18);
        bar.add(roleBadge);
 
        if (currentUser != null) {
            JLabel userLabel = new JLabel("👤  " + currentUser);
            userLabel.setFont(FONT_SMALL);
            userLabel.setForeground(TEXT_DIM);
            userLabel.setBounds(266, 18, 200, 16);
            bar.add(userLabel);
        }
 
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, MMM dd yyyy"));
        JLabel dateLabel = new JLabel(today);
        dateLabel.setFont(FONT_SMALL);
        dateLabel.setForeground(TEXT_DIM);
        dateLabel.setBounds(476, 18, 160, 16);
        bar.add(dateLabel);
 
        JButton backBtn = new JButton("← Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        backBtn.setForeground(TEXT_DIM);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setBounds(670, 14, 80, 24);
        backBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { backBtn.setForeground(ACCENT); }
            public void mouseExited (MouseEvent e) { backBtn.setForeground(TEXT_DIM); }
        });
        backBtn.addActionListener(e -> dispose());
        bar.add(backBtn);
    }
 
    // ── Email List Panel ──────────────────────────────────────────────────────
    private void buildEmailPanel() {
        JLabel sectionTitle = new JLabel("📥  Inbox") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                LinearGradientPaint gp = new LinearGradientPaint(0, 0, getWidth(), 0,
                    new float[]{0f, 1f}, new Color[]{EMAIL_CLR, EMAIL_CLR2});
                g2.setPaint(gp);
                g2.setFont(getFont());
                g2.drawString(getText(), 0, g2.getFontMetrics().getAscent());
                g2.dispose();
            }
        };
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sectionTitle.setForeground(EMAIL_CLR);
        sectionTitle.setBounds(30, 66, 220, 26);
        root.add(sectionTitle);
 
        JLabel countBadge = pillLabel("Loading...", TEXT_DIM);
        countBadge.setBounds(30, 96, 90, 16);
        root.add(countBadge);
 
        // ── Table: From | To | Subject | Preview ─────────────────────────────
        model = new DefaultTableModel(new String[]{"From", "To", "Subject", "Preview"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
 
        table = new JTable(model) {
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(isRowSelected(row) ? ROW_SEL : (row % 2 == 0 ? ROW_EVEN : ROW_ODD));
                c.setForeground(TEXT_MAIN);
                return c;
            }
        };
        table.setBackground(ROW_EVEN);
        table.setForeground(TEXT_MAIN);
        table.setRowHeight(36);
        table.setGridColor(new Color(25, 40, 75));
        table.setFont(FONT_BODY);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 
        table.getColumnModel().getColumn(0).setPreferredWidth(120); // From
        table.getColumnModel().getColumn(1).setPreferredWidth(110); // To
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Subject
        table.getColumnModel().getColumn(3).setPreferredWidth(160); // Preview
 
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(10, 18, 40));
        header.setForeground(ACCENT);
        header.setFont(FONT_HEADER);
        header.setPreferredSize(new Dimension(0, 34));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
 
        // Cell renderer
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setBackground(sel ? ROW_SEL : (row % 2 == 0 ? ROW_EVEN : ROW_ODD));
                setHorizontalAlignment(SwingConstants.LEFT);
                if (col == 0) {                                              // From
                    setForeground(EMAIL_CLR);
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else if (col == 1) {                                       // To
                    setForeground(ACCENT2);
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else if (col == 2) {                                       // Subject
                    setForeground(TEXT_MAIN);
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {                                                      // Preview
                    setForeground(TEXT_DIM);
                    setFont(FONT_SMALL);
                }
                return this;
            }
        };
        for (int i = 0; i < 4; i++) table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
 
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0)
                showDetail(table.getSelectedRow());
        });
 
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(ROW_EVEN);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        scroll.setBounds(22, 118, 560, 360);
        root.add(scroll);
 
        loadEmails(countBadge);
    }
 
    // ── Detail Panel ──────────────────────────────────────────────────────────
    private void buildDetailPanel() {
        detailPanel = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(13, 22, 44, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                // left accent bar: cyan→purple (developer colors)
                LinearGradientPaint lp = new LinearGradientPaint(0, 0, 0, getHeight(),
                    new float[]{0f, 1f}, new Color[]{EMAIL_CLR, EMAIL_CLR2});
                g2.setPaint(lp);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.setColor(BORDER_CLR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        detailPanel.setOpaque(false);
        detailPanel.setBounds(594, 118, 244, 360);
        root.add(detailPanel);
 
        // Placeholder
        emailIcon = new JLabel("📨", SwingConstants.CENTER);
        emailIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        emailIcon.setBounds(0, 120, 244, 44);
        detailPanel.add(emailIcon);
 
        hintLabel = new JLabel("Select an email to read", SwingConstants.CENTER);
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hintLabel.setForeground(TEXT_DIM);
        hintLabel.setBounds(0, 168, 244, 20);
        detailPanel.add(hintLabel);
 
        // Detail fields
        detailFrom = new JLabel("");
        detailFrom.setFont(FONT_SMALL);
        detailFrom.setForeground(EMAIL_CLR);
        detailFrom.setBounds(16, 16, 212, 18);
        detailPanel.add(detailFrom);
 
        detailTo = new JLabel("");
        detailTo.setFont(FONT_SMALL);
        detailTo.setForeground(TEXT_DIM);
        detailTo.setBounds(16, 34, 212, 18);
        detailPanel.add(detailTo);
 
        detailSubject = new JLabel("");
        detailSubject.setFont(new Font("Segoe UI", Font.BOLD, 13));
        detailSubject.setForeground(TEXT_MAIN);
        detailSubject.setBounds(16, 56, 212, 22);
        detailPanel.add(detailSubject);
 
        detailSep = new JSeparator();
        detailSep.setForeground(BORDER_CLR);
        detailSep.setBounds(16, 84, 212, 2);
        detailPanel.add(detailSep);
 
        detailMessage = new JLabel("");
        detailMessage.setFont(FONT_BODY);
        detailMessage.setForeground(TEXT_MAIN);
        detailMessage.setVerticalAlignment(SwingConstants.TOP);
        detailMessage.setBounds(16, 92, 212, 254);
        detailPanel.add(detailMessage);
 
        // Initially hidden
        detailFrom.setVisible(false);
        detailTo.setVisible(false);
        detailSubject.setVisible(false);
        detailSep.setVisible(false);
        detailMessage.setVisible(false);
    }
 
    private void showDetail(int row) {
        if (row < 0 || row >= allEmails.size()) return;
        // [0]=from, [1]=to, [2]=subject, [3]=message
        String[] email = allEmails.get(row);
 
        hintLabel.setVisible(false);
        emailIcon.setVisible(false);
 
        detailFrom.setText("  From: " + email[0]);
        detailTo.setText("  To:      " + email[1]);
        detailSubject.setText("  " + email[2]);
        detailMessage.setText("<html><body style='width:200px; color:#DCE5FF;"
            + "font-family:Segoe UI; font-size:11px; padding:4px'>"
            + email[3].replace("\n", "<br>") + "</body></html>");
 
        detailFrom.setVisible(true);
        detailTo.setVisible(true);
        detailSubject.setVisible(true);
        detailSep.setVisible(true);
        detailMessage.setVisible(true);
 
        detailPanel.repaint();
    }
 
    // ── Status Bar ────────────────────────────────────────────────────────────
    private void buildStatusBar() {
        JPanel bar = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(13, 22, 44, 180));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BORDER_CLR);
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setBounds(0, 518, 860, 42);
        root.add(bar);
 
        JLabel left = new JLabel("🟢  System Online  ·  Bug Tracker v2.0");
        left.setFont(FONT_SMALL);
        left.setForeground(TEXT_DIM);
        left.setBounds(18, 13, 280, 16);
        bar.add(left);
 
        JLabel right = new JLabel("Developer Portal  ·  " +
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")));
        right.setFont(FONT_SMALL);
        right.setForeground(TEXT_DIM);
        right.setBounds(660, 13, 180, 16);
        bar.add(right);
    }
 
    // ── Window Controls ───────────────────────────────────────────────────────
    private void buildWindowControls() {
        JButton closeBtn = windowBtn("✕", new Color(255, 70, 100));
        closeBtn.setBounds(820, 10, 28, 22);
        closeBtn.addActionListener(e -> dispose());
        root.add(closeBtn);
 
        JButton miniBtn = windowBtn("─", TEXT_DIM);
        miniBtn.setBounds(788, 10, 28, 22);
        miniBtn.addActionListener(e -> setState(Frame.ICONIFIED));
        root.add(miniBtn);
    }
 
    // ── Load Emails ───────────────────────────────────────────────────────────
    private void loadEmails(JLabel countBadge) {
        allEmails.clear();
 
        try (BufferedReader br = new BufferedReader(new FileReader("emails.txt"))) {
            String from = "", to = "", subject = "", message = "";
            String line;
 
            while ((line = br.readLine()) != null) {
                line = line.trim();
 
                if (line.startsWith("From:")) {
                    from = line.substring("From:".length()).trim();
                } else if (line.startsWith("To:")) {
                    to = line.substring("To:".length()).trim();
                } else if (line.startsWith("Subject:")) {
                    subject = line.substring("Subject:".length()).trim();
                } else if (line.startsWith("Message:")) {
                    message = line.substring("Message:".length()).trim();
                } else if (line.startsWith("---")) {
                    if (!to.isEmpty()) {
                        String userEmail = currentUser + "@gmail.com";

                   if (currentUser == null || to.equalsIgnoreCase(userEmail)) {

                            allEmails.add(new String[]{
                                from.isEmpty() ? "Unknown" : from,
                                to, subject, message
                            });
                        }
                    }
                    from = to = subject = message = "";
                }
            }
 
            // حفظ آخر record لو الملف ما ختمش بـ ---
            if (!to.isEmpty()) {
                if (currentUser == null || to.equalsIgnoreCase(currentUser)) {
                    allEmails.add(new String[]{
                        from.isEmpty() ? "Unknown" : from,
                        to, subject, message
                    });
                }
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        model.setRowCount(0);
        animatedRows = 0;
 
        rowTimer = new Timer(90, null);
        rowTimer.addActionListener(e -> {
            if (animatedRows < allEmails.size()) {
                String[] email = allEmails.get(animatedRows);
                // [0]=from, [1]=to, [2]=subject, [3]=message
                String preview = email[3].length() > 45
                        ? email[3].substring(0, 45) + "…"
                        : email[3];
                model.addRow(new Object[]{email[0], email[1], email[2], preview});
                animatedRows++;
            } else {
                rowTimer.stop();
            }
        });
        rowTimer.start();
 
        int count = allEmails.size();
        SwingUtilities.invokeLater(() -> {
            countBadge.setText(count + (count == 1 ? " message" : " messages"));
            countBadge.setForeground(count > 0 ? EMAIL_CLR : TEXT_DIM);
        });
    }
 
    // ── Background Animation ──────────────────────────────────────────────────
    private void drawBackground(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BG_DEEP);
        g2.fillRect(0, 0, getWidth(), getHeight());
 
        g2.setColor(new Color(25, 45, 90, 12));
        for (int x = 0; x < getWidth(); x += 32) g2.drawLine(x, 0, x, getHeight());
        for (int y = 0; y < getHeight(); y += 32) g2.drawLine(0, y, getWidth(), y);
 
        double r = Math.toRadians(orbAngle);
        // cyan/purple orbs matching DeveloperFrame
        drawOrb(g2, (int)(getWidth()*0.10 + Math.cos(r)*50), (int)(getHeight()*0.22 + Math.sin(r)*30), 200, ACCENT,  22);
        drawOrb(g2, (int)(getWidth()*0.90 + Math.sin(r)*40), (int)(getHeight()*0.78 + Math.cos(r)*25), 180, ACCENT2, 20);
 
        g2.setColor(BORDER_CLR);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 18, 18);
        g2.dispose();
    }
 
    private void drawOrb(Graphics2D g2, int cx, int cy, int r, Color c, int max) {
        for (int i = r; i > 0; i -= 14) {
            int alpha = (int)(max * (1 - (float)i / r));
            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
            g2.fillOval(cx-i, cy-i, i*2, i*2);
        }
    }
 
    private void startBgAnim() {
        bgTimer = new Timer(30, e -> {
            orbAngle = (orbAngle + 0.5f) % 360;
            root.repaint();
        });
        bgTimer.start();
    }
 
    // ── Fade-in on open ───────────────────────────────────────────────────────
    private void startFadeIn() {
        setOpacity(0f);
        fadeTimer = new Timer(18, null);
        fadeTimer.addActionListener(e -> {
            fadeAlpha += 0.07f;
            if (fadeAlpha >= 1f) { fadeAlpha = 1f; fadeTimer.stop(); }
            setOpacity(fadeAlpha);
        });
        fadeTimer.start();
    }
 
    // ── Helpers ───────────────────────────────────────────────────────────────
    private JLabel pillLabel(String text, Color color) {
        JLabel l = new JLabel(text, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(FONT_BADGE);
        l.setForeground(color);
        l.setOpaque(false);
        return l;
    }
 
    private JButton windowBtn(String text, Color hoverColor) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        b.setForeground(TEXT_DIM);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setForeground(hoverColor); }
            public void mouseExited (MouseEvent e) { b.setForeground(TEXT_DIM); }
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
}