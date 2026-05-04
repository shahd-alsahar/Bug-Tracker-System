package bugtrackerprojectsh.GUI;
 
import bugtrackerprojectsh.ProjectManager;           
import bugtrackerprojectsh.ProjectManager.ReportData; // ✅ inner class
 
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.Timer;
 
public class PmFrame extends JFrame {
 
    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color BG_DEEP    = new Color(6,   11,  24);
    private static final Color BG_CARD    = new Color(13,  22,  44);
    private static final Color BG_ROW_ALT = new Color(17,  28,  54);
    private static final Color ACCENT     = new Color(0,  210, 255);
    private static final Color ACCENT2    = new Color(100, 60, 255);
    private static final Color COLOR_OPEN = new Color(255, 180,  40);
    private static final Color COLOR_CLOS = new Color(0,   210, 140);
    private static final Color COLOR_PROG = new Color(0,   210, 255);
    private static final Color TEXT_MAIN  = new Color(220, 235, 255);
    private static final Color TEXT_DIM   = new Color(90,  120, 165);
    private static final Color BORDER_CLR = new Color(30,   55, 105);
    private static final Color SEL_BG     = new Color(0,  140, 200, 120);
 
    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font FONT_SUB    = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  11);
    private static final Font FONT_CELL   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font FONT_BADGE  = new Font("Segoe UI", Font.BOLD,   9);
 
    // ── Business Logic Delegate ───────────────────────────────────────────────
    private final ProjectManager pm = new ProjectManager(); // ✅ single instance
 
    // ── State ─────────────────────────────────────────────────────────────────
    private DefaultTableModel model;
    private JLabel openBadge, closedBadge, progressBadge, totalBadge;
    private float  orbAngle = 0f;
    private Timer  bgTimer;
    private JPanel root;
    private BarChartPanel chartPanel;
 
    // ── Constructor ───────────────────────────────────────────────────────────
    public PmFrame() {
        initWindow();
        buildUI();
        loadBugs();
        setVisible(true);
    }
 
    // ── Window Setup ──────────────────────────────────────────────────────────
    private void initWindow() {
        setTitle("Bug Tracker — Project Manager Dashboard");
        setSize(1100, 660);
        setMinimumSize(new Dimension(900, 550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setBackground(BG_DEEP);
    }
 
    // ── UI ────────────────────────────────────────────────────────────────────
    private void buildUI() {
        root = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBackground(g);
            }
        };
        root.setBackground(BG_DEEP);
        root.setOpaque(true);
        setContentPane(root);
 
        root.add(buildHeader(),  BorderLayout.NORTH);
        root.add(buildCenter(),  BorderLayout.CENTER);
        root.add(buildFooter(),  BorderLayout.SOUTH);
 
        addWindowControls();
        makeDraggable(root);
        startBgAnim();
    }
 
    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, getHeight()-1, ACCENT,
                    getWidth(), getHeight()-1, ACCENT2));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 70));
 
        JLabel av = buildAvatar("PM");
        av.setBounds(20, 14, 42, 42);
        header.add(av);
 
        JLabel title = new JLabel("Project Manager") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, ACCENT, getWidth(), 0, ACCENT2));
                g2.setFont(getFont());
                g2.drawString(getText(), 0, g2.getFontMetrics().getAscent());
                g2.dispose();
            }
        };
        title.setFont(FONT_TITLE);
        title.setBounds(72, 12, 300, 28);
        header.add(title);
 
        JLabel sub = new JLabel("Dashboard  ·  Bug Overview & Performance");
        sub.setFont(FONT_SUB);
        sub.setForeground(TEXT_DIM);
        sub.setBounds(73, 42, 360, 16);
        header.add(sub);
 
        String today = LocalDate.now().format(
            DateTimeFormatter.ofPattern("EEE, MMM dd yyyy"));
        JLabel dateLabel = new JLabel(today);
        dateLabel.setFont(FONT_SUB);
        dateLabel.setForeground(TEXT_DIM);
        dateLabel.setBounds(700, 27, 200, 16);
        header.add(dateLabel);
 
        return header;
    }
 
    private JLabel buildAvatar(String initials) {
        JLabel av = new JLabel(initials, SwingConstants.CENTER) {
            private float ring = 2f, ralpha = 0.3f;
            {
                Timer t = new Timer(50, e -> {
                    ring   = (ring   >= 5f)    ? 2f   : ring   + 0.05f;
                    ralpha = (ralpha <= 0.1f)  ? 0.3f : ralpha - 0.004f;
                    repaint();
                });
                t.start();
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0f, 0.82f, 1f, ralpha));
                int r = (int)(ring * 2);
                g2.fillOval(-r/2, -r/2, getWidth()+r, getHeight()+r);
                g2.setPaint(new GradientPaint(0, 0, ACCENT,
                    getWidth(), getHeight(), ACCENT2));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        av.setFont(new Font("Segoe UI", Font.BOLD, 13));
        av.setForeground(Color.WHITE);
        av.setOpaque(false);
        return av;
    }
 
    // ── Center ────────────────────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setOpaque(false);
        center.add(buildStatCards(), BorderLayout.NORTH);
 
        JPanel split = new JPanel(new GridLayout(1, 2, 12, 0));
        split.setOpaque(false);
        split.setBorder(BorderFactory.createEmptyBorder(10, 18, 0, 18));
        split.add(buildTableCard());
        split.add(buildChartCard());
        center.add(split, BorderLayout.CENTER);
        return center;
    }
 
    // ── Stat Cards ────────────────────────────────────────────────────────────
    private JPanel buildStatCards() {
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(14, 18, 10, 18));
 
        totalBadge    = statLabel("0", ACCENT);
        openBadge     = statLabel("0", COLOR_OPEN);
        progressBadge = statLabel("0", COLOR_PROG);
        closedBadge   = statLabel("0", COLOR_CLOS);
 
        row.add(wrapCard(totalBadge,    "Total Bugs",  ACCENT));
        row.add(wrapCard(openBadge,     "Open",        COLOR_OPEN));
        row.add(wrapCard(progressBadge, "In Progress", COLOR_PROG));
        row.add(wrapCard(closedBadge,   "Closed",      COLOR_CLOS));
        return row;
    }
 
    private JLabel statLabel(String val, Color c) {
        JLabel l = new JLabel(val, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 28));
        l.setForeground(c);
        return l;
    }
 
    private JPanel wrapCard(JLabel valueLbl, String title, Color accent) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(),
                    accent.getBlue(), 60));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.setPaint(new GradientPaint(0, 0, accent, getWidth(), 0,
                    new Color(accent.getRed(), accent.getGreen(),
                        accent.getBlue(), 80)));
                g2.fillRoundRect(0, 0, getWidth(), 3, 3, 3);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(0, 80));
 
        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(FONT_BADGE);
        titleLbl.setForeground(TEXT_DIM);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
 
        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);
        return card;
    }
 
    // ── Table Card ────────────────────────────────────────────────────────────
    private JPanel buildTableCard() {
        model = new DefaultTableModel(
            new String[]{"ID", "Title", "Assigned To", "Status", "Priority"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
 
        JTable table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r,
                    int row, int col) {
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
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_MAIN);
        table.setSelectionBackground(SEL_BG);
        table.setFillsViewportHeight(true);
        table.setFocusable(false);
 
        JTableHeader th = table.getTableHeader();
        th.setFont(FONT_HEADER);
        th.setBackground(new Color(8, 16, 38));
        th.setForeground(ACCENT);
        th.setPreferredSize(new Dimension(0, 34));
        th.setReorderingAllowed(false);
 
        DefaultTableCellRenderer hRend = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t,
                    Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = new JLabel(v == null ? "" : v.toString().toUpperCase());
                lbl.setFont(FONT_HEADER);
                lbl.setForeground(ACCENT);
                lbl.setBackground(new Color(8, 16, 38));
                lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_CLR),
                    BorderFactory.createEmptyBorder(0, 8, 0, 8)));
                lbl.setHorizontalAlignment(LEFT);
                return lbl;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setHeaderRenderer(hRend);
 
        // Status renderer
        table.getColumnModel().getColumn(3).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(JTable t,
                        Object v, boolean s, boolean f, int r, int c) {
                    JLabel lbl = new JLabel(v == null ? "" : v.toString());
                    String val = v == null ? "" : v.toString().toLowerCase();
                    lbl.setForeground(
                        val.contains("closed")   ? COLOR_CLOS :
                        val.contains("open")     ? COLOR_OPEN :
                        val.contains("progress") ? COLOR_PROG : TEXT_DIM);
                    lbl.setBackground(s ? SEL_BG : r%2==0 ? BG_CARD : BG_ROW_ALT);
                    lbl.setOpaque(true);
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    lbl.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                    return lbl;
                }
            });
 
        // Default cell renderer
        DefaultTableCellRenderer def = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t,
                    Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = new JLabel(v == null ? "" : v.toString());
                lbl.setForeground(TEXT_MAIN);
                lbl.setBackground(s ? SEL_BG : r%2==0 ? BG_CARD : BG_ROW_ALT);
                lbl.setOpaque(true);
                lbl.setFont(FONT_CELL);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return lbl;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++)
            if (i != 3) table.getColumnModel().getColumn(i).setCellRenderer(def);
 
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(BorderFactory.createEmptyBorder());
 
        JPanel card = buildRoundCard();
        card.setLayout(new BorderLayout());
        JLabel heading = new JLabel("  📋  All Bugs");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 13));
        heading.setForeground(TEXT_MAIN);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 8, 8, 8));
        card.add(heading, BorderLayout.NORTH);
        card.add(scroll,  BorderLayout.CENTER);
        return card;
    }
 
    // ── Chart Card ────────────────────────────────────────────────────────────
    private JPanel buildChartCard() {
        JPanel card = buildRoundCard();
        card.setLayout(new BorderLayout());
        JLabel heading = new JLabel("  📊  Developer Performance");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 13));
        heading.setForeground(TEXT_MAIN);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 8, 6, 8));
        card.add(heading, BorderLayout.NORTH);
        chartPanel = new BarChartPanel();
        card.add(chartPanel, BorderLayout.CENTER);
        return card;
    }
 
    // ── Animated Bar Chart ────────────────────────────────────────────────────
    private class BarChartPanel extends JPanel {
 
        private LinkedHashMap<String, Integer> data = new LinkedHashMap<>();
        private float[]  animProgress;
        private Timer    animTimer;
        private String   hoveredBar = null;
 
        BarChartPanel() {
            setOpaque(false);
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    hoveredBar = getBarAt(e.getX(), e.getY());
                    repaint();
                }
            });
        }
 
        void setData(LinkedHashMap<String, Integer> newData) {
            this.data = newData;
            animProgress = new float[data.size()];
            if (animTimer != null) animTimer.stop();
            animTimer = new Timer(16, null);
            final long start = System.currentTimeMillis();
            animTimer.addActionListener(e -> {
                float elapsed = (System.currentTimeMillis() - start) / 700f;
                boolean done = true;
                int i = 0;
                for (String key : data.keySet()) {
                    float t = Math.max(0, Math.min(1f, elapsed - i * 0.15f));
                    animProgress[i] = Math.max(animProgress[i], easeOut(t));
                    if (animProgress[i] < 1f) done = false;
                    i++;
                }
                repaint();
                if (done) animTimer.stop();
            });
            animTimer.start();
        }
 
        private float easeOut(float t) { return 1 - (1 - t) * (1 - t); }
 
        private String getBarAt(int mx, int my) {
            if (data.isEmpty()) return null;
            int n    = data.size();
            int padL = 50, padR = 20;
            int w    = getWidth() - padL - padR;
            int barW = Math.max(18, w / n - 10);
            int gap  = (w - barW * n) / (n + 1);
            int i    = 0;
            for (String key : data.keySet()) {
                int x = padL + gap + i * (barW + gap);
                if (mx >= x && mx <= x + barW) return key;
                i++;
            }
            return null;
        }
 
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
 
            if (data.isEmpty()) {
                g2.setColor(TEXT_DIM);
                g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                String msg = "No performance data yet";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(msg, (getWidth()-fm.stringWidth(msg))/2, getHeight()/2);
                g2.dispose();
                return;
            }
 
            int n      = data.size();
            int padL   = 50, padR = 20, padT = 20, padB = 40;
            int chartW = getWidth()  - padL - padR;
            int chartH = getHeight() - padT - padB;
            int maxVal = data.values().stream().mapToInt(Integer::intValue).max().orElse(1);
 
            // Grid lines
            g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10f, new float[]{4f, 4f}, 0f));
            for (int gi = 0; gi <= 5; gi++) {
                int y   = padT + (int)(chartH * gi / 5f);
                int val = (int)(maxVal * (5 - gi) / 5f);
                g2.setColor(new Color(30, 55, 105, 80));
                g2.drawLine(padL, y, padL + chartW, y);
                g2.setColor(TEXT_DIM);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(String.valueOf(val),
                    padL - fm.stringWidth(String.valueOf(val)) - 4,
                    y + fm.getAscent()/2);
            }
 
            // Bars
            int barW = Math.max(18, chartW / n - 10);
            int gap  = (chartW - barW * n) / (n + 1);
            String[]  keys = data.keySet().toArray(new String[0]);
            Integer[] vals = data.values().toArray(new Integer[0]);
            Color[] palette = {
                new Color(0,   210, 255), new Color(100,  60, 255),
                new Color(0,   210, 140), new Color(255, 180,  40),
                new Color(255,  70, 100), new Color(120, 200,  80),
                new Color(255, 120,  60),
            };
            g2.setStroke(new BasicStroke(1f));
 
            for (int i = 0; i < n; i++) {
                float prog  = (animProgress != null && i < animProgress.length)
                              ? animProgress[i] : 1f;
                int   barH  = (int)(chartH * (vals[i] / (float) maxVal) * prog);
                int   x     = padL + gap + i * (barW + gap);
                int   y     = padT + chartH - barH;
                Color color = palette[i % palette.length];
                boolean hov = keys[i].equals(hoveredBar);
 
                if (hov) {
                    for (int glow = 8; glow > 0; glow -= 2) {
                        g2.setColor(new Color(color.getRed(), color.getGreen(),
                            color.getBlue(), 15));
                        g2.fillRoundRect(x-glow, y-glow/2,
                            barW+glow*2, barH+glow/2, 8, 8);
                    }
                }
                g2.setPaint(new GradientPaint(x, y, hov ? color.brighter() : color,
                    x, y+barH, new Color(color.getRed()/3,
                        color.getGreen()/3, color.getBlue()/3+30, 200)));
                g2.fillRoundRect(x, y, barW, barH, 8, 8);
                g2.setColor(new Color(color.getRed(), color.getGreen(),
                    color.getBlue(), 160));
                g2.drawRoundRect(x, y, barW, barH, 8, 8);
 
                if (prog > 0.6f) {
                    g2.setColor(hov ? Color.WHITE : TEXT_MAIN);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    FontMetrics fm = g2.getFontMetrics();
                    String vs = String.valueOf(vals[i]);
                    g2.drawString(vs, x+(barW-fm.stringWidth(vs))/2, y-4);
                }
                g2.setColor(hov ? color : TEXT_DIM);
                g2.setFont(new Font("Segoe UI", hov ? Font.BOLD : Font.PLAIN, 10));
                FontMetrics fm = g2.getFontMetrics();
                String name = keys[i].length() > 8
                    ? keys[i].substring(0, 7)+"." : keys[i];
                g2.drawString(name, x+(barW-fm.stringWidth(name))/2,
                    padT+chartH+16);
            }
 
            g2.setColor(BORDER_CLR);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(padL, padT+chartH, padL+chartW, padT+chartH);
 
            if (hoveredBar != null && data.containsKey(hoveredBar)) {
                String tip = hoveredBar + " → " + data.get(hoveredBar) + " bugs fixed";
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(tip)+20, th2 = 26;
                int tx = Math.min(getWidth()-tw-4, getWidth()/2-tw/2);
                int ty = 4;
                g2.setColor(new Color(13, 22, 44, 220));
                g2.fillRoundRect(tx, ty, tw, th2, 8, 8);
                g2.setColor(ACCENT);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(tx, ty, tw, th2, 8, 8);
                g2.setColor(TEXT_MAIN);
                g2.drawString(tip, tx+10, ty+th2/2+fm.getAscent()/2-1);
            }
            g2.dispose();
        }
    }
 
    // ── Footer ────────────────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
 
        JButton refreshBtn = buildButton("↻  Refresh", new Color(60,  180, 120));
        JButton logoutBtn  = buildButton("⏻  Logout",  new Color(255, 120,  60));
        JButton closeBtn   = buildButton("✕  Close",   new Color(220,  60,  80));
 
        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            loadBugs();
        });
 
        logoutBtn.addActionListener(e -> {
            if (bgTimer != null) bgTimer.stop();
            dispose();
            SwingUtilities.invokeLater(() -> {
                try {
                    Class<?> cls = Class.forName("bugtrackerprojectsh.GUI.LoginFrame");
                    cls.getDeclaredConstructor().newInstance();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,
                        "Could not open login:\n" + ex.getMessage(),
                        "Logout Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        });
 
        closeBtn.addActionListener(e -> {
            if (bgTimer != null) bgTimer.stop();
            dispose();
        });
 
        footer.add(refreshBtn);
        footer.add(logoutBtn);
        footer.add(closeBtn);
 
        JPanel fp = new JPanel(new BorderLayout());
        fp.setOpaque(false);
        fp.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR));
 
        JLabel status = new JLabel("🟢  Bug Tracker Pro  ·  PM View",
            SwingConstants.RIGHT);
        status.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        status.setForeground(TEXT_DIM);
        status.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 18));
 
        fp.add(footer, BorderLayout.WEST);
        fp.add(status, BorderLayout.EAST);
        return fp;
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  LOAD BUGS — delegated to ProjectManager
    // ══════════════════════════════════════════════════════════════════════════
 
    private void loadBugs() {
        // ✅ delegation → ProjectManager.getReportData()
        ReportData report = pm.getReportData();
 
        if (report.bugRows.isEmpty()) {
            showToast("No bugs found (check bug.txt)", new Color(220, 60, 80));
        }
 
        // Populate table
        for (ProjectManager.BugRow row : report.bugRows) {
            model.addRow(new Object[]{
                row.id, row.title, row.assignedTo, row.status, row.priority
            });
        }
 
        // Update stat cards and chart on EDT
        SwingUtilities.invokeLater(() -> {
            totalBadge   .setText(String.valueOf(report.stats.total));
            openBadge    .setText(String.valueOf(report.stats.open));
            closedBadge  .setText(String.valueOf(report.stats.closed));
            progressBadge.setText(String.valueOf(report.stats.inProgress));
            totalBadge.getParent().repaint();
 
            // ✅ devPerf is already sorted descending inside ProjectManager
            chartPanel.setData(report.devPerf);
        });
    }
 
    // ── Helpers ───────────────────────────────────────────────────────────────
    private JPanel buildRoundCard() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER_CLR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
    }
 
    private void showToast(String msg, Color accent) {
        JWindow toast = new JWindow(this);
        toast.setBackground(new Color(0, 0, 0, 0));
        JPanel p = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(13, 22, 44, 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 12, 12);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(320, 42));
        JLabel l = new JLabel("⚠  " + msg);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(accent);
        l.setBounds(14, 0, 296, 42);
        p.add(l);
        toast.setContentPane(p);
        toast.pack();
        Point loc = getLocation();
        toast.setLocation(loc.x + (getWidth()-320)/2, loc.y + getHeight() - 70);
        toast.setVisible(true);
        new Timer(3000, e -> toast.dispose()) {{ setRepeats(false); start(); }};
    }
 
    private JButton buildButton(String text, Color accent) {
        JButton b = new JButton(text) {
            private float hov = 0f;
            private Timer ht;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { anim(1f); }
                    public void mouseExited(MouseEvent e)  { anim(0f); }
                });
            }
            void anim(float t) {
                if (ht != null) ht.stop();
                ht = new Timer(16, ev -> {
                    hov += (t - hov) * 0.2f;
                    if (Math.abs(hov - t) < 0.01f) { hov = t; ht.stop(); }
                    repaint();
                });
                ht.start();
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0,
                    new Color(accent.getRed(), accent.getGreen(),
                        accent.getBlue(), 200),
                    getWidth(), getHeight(),
                    new Color(accent.getRed()/2, accent.getGreen()/2,
                        accent.getBlue()/2+80, 200)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                if (hov > 0) {
                    g2.setColor(new Color(255, 255, 255, (int)(hov * 30)));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.setColor(Color.WHITE);
                g2.setFont(FONT_BTN);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(148, 36));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
 
    private void addWindowControls() {
        JPanel glass = new JPanel(null);
        glass.setOpaque(false);
        setGlassPane(glass);
        glass.setVisible(true);
 
        JButton closeBtn = winBtn("✕");
        closeBtn.setBounds(1062, 6, 28, 22);
        closeBtn.addActionListener(e -> { if (bgTimer != null) bgTimer.stop(); dispose(); });
 
        JButton miniBtn = winBtn("─");
        miniBtn.setBounds(1030, 6, 28, 22);
        miniBtn.addActionListener(e -> setState(Frame.ICONIFIED));
 
        glass.add(closeBtn);
        glass.add(miniBtn);
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
            public void mouseExited(MouseEvent e)  { b.setForeground(TEXT_DIM);  }
        });
        return b;
    }
 
    private void makeDraggable(JComponent comp) {
        final Point[] drag = {null};
        comp.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e)  { drag[0] = e.getPoint(); }
            public void mouseReleased(MouseEvent e) { drag[0] = null; }
        });
        comp.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (drag[0] == null) return;
                Point loc = getLocation();
                setLocation(loc.x + e.getX() - drag[0].x,
                            loc.y + e.getY() - drag[0].y);
            }
        });
    }
 
    // ── Background ────────────────────────────────────────────────────────────
    private void drawBackground(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BG_DEEP);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(new Color(25, 45, 90, 12));
        for (int x = 0; x < getWidth();  x += 32) g2.drawLine(x, 0, x, getHeight());
        for (int y = 0; y < getHeight(); y += 32) g2.drawLine(0, y, getWidth(), y);
        double r = Math.toRadians(orbAngle);
        drawOrb(g2,
            (int)(getWidth()  * 0.08 + Math.cos(r) * 50),
            (int)(getHeight() * 0.15 + Math.sin(r) * 35),
            220, ACCENT, 30);
        drawOrb(g2,
            (int)(getWidth()  * 0.92 + Math.sin(r) * 40),
            (int)(getHeight() * 0.85 + Math.cos(r) * 30),
            200, ACCENT2, 28);
        g2.dispose();
    }
 
    private void drawOrb(Graphics2D g2, int cx, int cy, int r, Color c, int max) {
        for (int i = r; i > 0; i -= 14) {
            int alpha = (int)(max * (1f - (float) i / r));
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
}