package bugtrackerprojectsh.GUI;
 
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
 
// ✅ ربط الـ DeveloperFrame بالـ Developer class
import bugtrackerprojectsh.Developer;
import bugtrackerprojectsh.EmailService;
import bugtrackerprojectsh.GUI.DeveloperEmailInboxFrame;
 
public class DeveloperFrame extends JFrame {
 
    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color BG_DEEP    = new Color(6,   11,  24);
    private static final Color BG_CARD    = new Color(13,  22,  44);
    private static final Color BG_ROW_ALT = new Color(17,  28,  54);
    private static final Color ACCENT     = new Color(0,  210, 255);
    private static final Color ACCENT2    = new Color(100, 60, 255);
    private static final Color TEXT_MAIN  = new Color(220, 235, 255);
    private static final Color TEXT_DIM   = new Color(90,  120, 165);
    private static final Color BORDER_CLR = new Color(30,   55, 105);
    private static final Color SEL_BG     = new Color(0,  140, 200, 120);
    private static final Color COLOR_UPD  = new Color(60,  200, 130);
    private static final Color COLOR_DEL  = new Color(220,  60,  80);
    private static final Color PRI_HIGH   = new Color(255,  70, 100);
    private static final Color PRI_MED    = new Color(255, 180,  40);
    private static final Color PRI_LOW    = new Color(0,   210, 140);
 
    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font FONT_SUB    = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  11);
    private static final Font FONT_CELL   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD,  12);
 
    private static final String BUG_FILE = "bug.txt";
    private static final String[] COLUMNS = {
        "ID", "Bug Name", "Type", "Priority", "Level",
        "Project", "Date", "Status", "Tester"
    };
 
    // ✅ Developer object — مصدر كل البيانات والـ logic
    private final Developer developer;
 
    private JTable          table;
    private DefaultTableModel model;
    private JLabel          bugBadge;
    private float           orb1Angle = 0f;
    private Timer           bgTimer;
 
    // ── Constructor ───────────────────────────────────────────────────────────
    public DeveloperFrame(String name) {
        // ✅ إنشاء الـ Developer object
        this.developer = new Developer(0, name);
        initWindow();
        buildUI();
        loadBugs();
        setVisible(true);
    }
 
    // ── Window Setup ──────────────────────────────────────────────────────────
    private void initWindow() {
        setTitle("Bug Tracker — Developer Dashboard");
        setSize(1100, 620);
        setMinimumSize(new Dimension(900, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setBackground(BG_DEEP);
    }
 
    // ── UI Construction ───────────────────────────────────────────────────────
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBackground(g);
            }
        };
        root.setBackground(BG_DEEP);
        root.setOpaque(true);
        setContentPane(root);
 
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
 
        addWindowControls(root);
        makeDraggable(root);
        startBgAnimation(root);
    }
 
    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, getHeight()-1, ACCENT, getWidth(), getHeight()-1, ACCENT2));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 70));
 
        JLabel av = buildAvatar();
        av.setBounds(20, 14, 42, 42);
        header.add(av);
 
        // ✅ الاسم من developer.getDeveloperName()
        JLabel nameLabel = new JLabel(developer.getDeveloperName()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, ACCENT, getWidth(), 0, ACCENT2));
                g2.setFont(getFont());
                g2.drawString(getText(), 0, g2.getFontMetrics().getAscent());
                g2.dispose();
            }
        };
        nameLabel.setFont(FONT_TITLE);
        nameLabel.setBounds(72, 12, 320, 28);
        header.add(nameLabel);
 
        JLabel roleLabel = new JLabel("Developer Dashboard");
        roleLabel.setFont(FONT_SUB);
        roleLabel.setForeground(TEXT_DIM);
        roleLabel.setBounds(73, 42, 300, 16);
        header.add(roleLabel);
 
        // Bug badge — ✅ العدد من developer.countAssignedBugs()
        bugBadge = new JLabel("0 bugs assigned") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 90));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bugBadge.setFont(FONT_SUB);
        bugBadge.setForeground(ACCENT);
        bugBadge.setOpaque(false);
        bugBadge.setBorder(new EmptyBorder(4, 14, 4, 14));
        bugBadge.setBounds(760, 22, 160, 26);
        header.add(bugBadge);
 
        JButton logoutBtn = buildButton("  Logout  ", COLOR_DEL);
        logoutBtn.setBounds(930, 17, 120, 36);
        logoutBtn.addActionListener(e -> showLogoutDialog());
        header.add(logoutBtn);
 
        return header;
    }
 
    private JLabel buildAvatar() {
        String initial = developer.getDeveloperName().isEmpty() ? "?"
            : String.valueOf(developer.getDeveloperName().charAt(0)).toUpperCase();
        JLabel av = new JLabel(initial, SwingConstants.CENTER) {
            private float ringSize  = 2f;
            private float ringAlpha = 0.3f;
            {
                Timer t = new Timer(50, null);
                t.addActionListener(e -> {
                    ringSize  = (ringSize  >= 5f)   ? 2f   : ringSize  + 0.05f;
                    ringAlpha = (ringAlpha <= 0.1f) ? 0.3f : ringAlpha - 0.004f;
                    repaint();
                });
                t.start();
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0f, 0.82f, 1f, ringAlpha));
                int r = (int)(ringSize * 2);
                g2.fillOval(-r/2, -r/2, getWidth()+r, getHeight()+r);
                g2.setPaint(new GradientPaint(0, 0, ACCENT, getWidth(), getHeight(), ACCENT2));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        av.setFont(new Font("Segoe UI", Font.BOLD, 16));
        av.setForeground(Color.WHITE);
        av.setPreferredSize(new Dimension(42, 42));
        av.setOpaque(false);
        return av;
    }
 
    // ── Center ────────────────────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setOpaque(false);
        center.add(buildSearchBar(), BorderLayout.NORTH);
        center.add(buildTablePane(), BorderLayout.CENTER);
        return center;
    }
 
    // ── Search bar ────────────────────────────────────────────────────────────
    private JPanel buildSearchBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));
 
        JTextField search = styledTextField("Search bugs by name, project, type...", 26);
        JComboBox<String> statusFilter   = styledCombo(new String[]{"All Status","Open","In Progress","Closed"});
        JComboBox<String> priorityFilter = styledCombo(new String[]{"All Priority","High","Medium","Low"});
 
        ActionListener filter = e -> filterTable(search.getText(),
            (String) statusFilter.getSelectedItem(),
            (String) priorityFilter.getSelectedItem());
 
        search.addActionListener(filter);
        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filter.actionPerformed(null); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filter.actionPerformed(null); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter.actionPerformed(null); }
        });
        statusFilter.addActionListener(filter);
        priorityFilter.addActionListener(filter);
 
        bar.add(search);
        bar.add(statusFilter);
        bar.add(priorityFilter);
        return bar;
    }
 
    private JTextField styledTextField(String placeholder, int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(FONT_CELL);
        tf.setForeground(TEXT_MAIN);
        tf.setCaretColor(ACCENT);
        tf.setBackground(new Color(10, 18, 42));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT, 1),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));
            }
            @Override public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_CLR, 1),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));
            }
        });
        tf.setUI(new javax.swing.plaf.basic.BasicTextFieldUI() {
            @Override protected void paintSafely(Graphics g) {
                super.paintSafely(g);
                if (tf.getText().isEmpty() && !tf.hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(TEXT_DIM);
                    g2.setFont(FONT_CELL);
                    FontMetrics fm = g2.getFontMetrics();
                    Insets ins = tf.getInsets();
                    g2.drawString(placeholder, ins.left + 2, ins.top + fm.getAscent());
                    g2.dispose();
                }
            }
        });
        return tf;
    }
 
    private JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(FONT_CELL);
        cb.setForeground(TEXT_MAIN);
        cb.setBackground(new Color(10, 18, 42));
        cb.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        cb.setFocusable(false);
        return cb;
    }
 
    // ── Table pane ────────────────────────────────────────────────────────────
    private JPanel buildTablePane() {
        model = new DefaultTableModel(COLUMNS, 0) {
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
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_MAIN);
        table.setSelectionBackground(SEL_BG);
        table.setSelectionForeground(Color.WHITE);
        table.setFocusable(false);
        table.setFillsViewportHeight(true);
 
        // Double-click لعرض تفاصيل الـ bug
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        showBugDetailsDialog(
                            model.getValueAt(row, 0).toString(),
                            model.getValueAt(row, 1).toString(),
                            model.getValueAt(row, 2).toString(),
                            model.getValueAt(row, 3).toString(),
                            model.getValueAt(row, 4).toString(),
                            model.getValueAt(row, 5).toString(),
                            model.getValueAt(row, 6).toString(),
                            model.getValueAt(row, 7).toString(),
                            model.getValueAt(row, 8).toString()
                        );
                    }
                }
            }
        });
 
        // Header
        JTableHeader th = table.getTableHeader();
        th.setFont(FONT_HEADER);
        th.setBackground(new Color(8, 16, 38));
        th.setForeground(ACCENT);
        th.setPreferredSize(new Dimension(0, 38));
        th.setReorderingAllowed(false);
 
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setText(v == null ? "" : v.toString().toUpperCase());
                setBackground(new Color(8, 16, 38));
                setForeground(ACCENT);
                setFont(FONT_HEADER);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_CLR),
                    BorderFactory.createEmptyBorder(0, 10, 0, 10)));
                setHorizontalAlignment(SwingConstants.LEFT);
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
 
        // Priority renderer
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String val = v == null ? "" : v.toString().toLowerCase();
                setForeground(val.contains("critical") || val.contains("high") ? PRI_HIGH
                            : val.contains("med")  ? PRI_MED
                            : val.contains("low")  ? PRI_LOW
                            : TEXT_MAIN);
                if (!sel) setBackground(r % 2 == 0 ? BG_CARD : BG_ROW_ALT);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
 
        // Status renderer
        table.getColumnModel().getColumn(7).setCellRenderer(new StatusRenderer());
 
        // ID renderer
        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setForeground(ACCENT);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                if (!sel) setBackground(r % 2 == 0 ? BG_CARD : BG_ROW_ALT);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
 
        // Default renderer
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        };
        for (int i = 1; i < table.getColumnCount(); i++)
            if (i != 3 && i != 7) table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
 
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
    private void showBugDetailsDialog(String bugId, String bugName, String type,
            String priority, String level, String project,
            String date, String status, String tester) {
 
        JDialog dialog = new JDialog(this, "Bug Details", true);
        dialog.setUndecorated(true);
        dialog.setSize(460, 420);
        dialog.setLocationRelativeTo(this);
 
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
                g2.setPaint(new GradientPaint(0, 0,
                    new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 45),
                    getWidth(), 0,
                    new Color(ACCENT2.getRed(), ACCENT2.getGreen(), ACCENT2.getBlue(), 45)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new GradientPaint(0, getHeight()-1, ACCENT, getWidth(), getHeight()-1, ACCENT2));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        headerStrip.setOpaque(false);
        headerStrip.setBounds(0, 0, 460, 70);
        card.add(headerStrip);
 
        JLabel iconLbl = new JLabel("🐛");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        iconLbl.setBounds(20, 16, 40, 40);
        headerStrip.add(iconLbl);
 
        JLabel idLbl = new JLabel("Bug #" + bugId);
        idLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        idLbl.setForeground(ACCENT);
        idLbl.setBounds(66, 13, 360, 22);
        headerStrip.add(idLbl);
 
        JLabel nameLbl = new JLabel(bugName);
        nameLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nameLbl.setForeground(TEXT_DIM);
        nameLbl.setBounds(67, 37, 370, 18);
        headerStrip.add(nameLbl);
 
        JButton closeX = winBtn("✕");
        closeX.setBounds(422, 10, 28, 24);
        closeX.addActionListener(e -> dialog.dispose());
        card.add(closeX);
 
        String[] labels = {"Type", "Priority", "Level", "Project", "Date", "Status", "Tester"};
        String[] values = { type,   priority,   level,   project,   date,   status,   tester };
 
        int startY = 82, rowH = 38;
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
                rowBg.setBounds(14, y - 2, 432, rowH - 2);
                card.add(rowBg);
            }
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(TEXT_DIM);
            lbl.setBounds(28, y + 8, 110, 18);
            card.add(lbl);
 
            JLabel valLbl = new JLabel(values[i].isEmpty() ? "—" : values[i]);
            valLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            if (labels[i].equals("Priority")) {
                String v = values[i].toLowerCase();
                valLbl.setForeground(v.contains("high") || v.contains("critical") ? PRI_HIGH
                                   : v.contains("med") ? PRI_MED
                                   : v.contains("low") ? PRI_LOW : TEXT_MAIN);
                valLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            } else if (labels[i].equals("Status")) {
                valLbl.setForeground(ACCENT);
            } else {
                valLbl.setForeground(TEXT_MAIN);
            }
            valLbl.setBounds(150, y + 8, 280, 18);
            card.add(valLbl);
 
            JPanel div = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    g.setColor(BORDER_CLR);
                    g.fillRect(0, 0, getWidth(), 1);
                }
            };
            div.setOpaque(false);
            div.setBounds(20, y + rowH - 4, 420, 1);
            card.add(div);
        }
 
        int btnY = startY + labels.length * rowH + 14;
        JButton okBtn = buildButton("  Close  ", ACCENT);
        okBtn.setBounds(160, btnY, 130, 36);
        okBtn.addActionListener(e -> dialog.dispose());
        card.add(okBtn);
 
        dialog.setSize(460, btnY + 60);
        dialog.setLocationRelativeTo(this);
        makeDraggable(card);
        dialog.setContentPane(card);
        dialog.setVisible(true);
    }
 
    // ── Footer ────────────────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
 
        JButton refreshBtn      = buildButton("↻  Refresh",        new Color(60, 180, 120));
        JButton changeStatusBtn = buildButton("✎  Change Status",  new Color(251, 191, 36));
        JButton inboxBtn        = buildButton("📩  Inbox",          ACCENT);
 
        refreshBtn.addActionListener(e -> {
            loadBugs();
            showInfoToast("Bugs refreshed successfully", COLOR_UPD);
        });
        changeStatusBtn.addActionListener(e -> changeStatus());
 
        // ✅ اسم الـ developer من الـ Developer object
        inboxBtn.addActionListener(e -> new DeveloperEmailInboxFrame(developer.getDeveloperName()));
 
        footer.add(refreshBtn);
        footer.add(changeStatusBtn);
        footer.add(inboxBtn);
 
        JPanel fp = new JPanel(new BorderLayout());
        fp.setOpaque(false);
        fp.add(footer, BorderLayout.WEST);
        return fp;
    }
 
    // ── Data Logic — loadBugs يستخدم developer name ──────────────────────────
    private void loadBugs() {
        model.setRowCount(0);
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(BUG_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",", -1);
                // ✅ [9] = Developer name
                if (fields.length >= 10 &&
                    fields[9].trim().equalsIgnoreCase(developer.getDeveloperName())) {
                    String[] row = new String[9];
                    for (int i = 0; i < 9; i++)
                        row[i] = i < fields.length ? fields[i].trim() : "";
                    model.addRow(row);
                    count++;
                }
            }
        } catch (FileNotFoundException e) {
            showInfoToast("Bug file not found: " + BUG_FILE, COLOR_DEL);
        } catch (IOException e) {
            showInfoToast("Failed to read bugs: " + e.getMessage(), COLOR_DEL);
        }
        int n = count;
        bugBadge.setText(n + (n == 1 ? " bug" : " bugs") + " assigned");
        bugBadge.revalidate();
        bugBadge.repaint();
    }
 
    private void filterTable(String query, String statusFilter, String priorityFilter) {
        model.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader(BUG_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] f = line.split(",", -1);
                // ✅ [9] = Developer
                if (f.length < 10 || !f[9].trim().equalsIgnoreCase(developer.getDeveloperName())) continue;
                String[] row = new String[9];
                for (int i = 0; i < 9; i++) row[i] = i < f.length ? f[i].trim() : "";
                boolean matchQ = query.isEmpty()
                    || row[1].toLowerCase().contains(query.toLowerCase())
                    || row[5].toLowerCase().contains(query.toLowerCase())
                    || row[2].toLowerCase().contains(query.toLowerCase());
                boolean matchS = statusFilter == null || statusFilter.startsWith("All")
                    || row[7].equalsIgnoreCase(statusFilter);
                boolean matchP = priorityFilter == null || priorityFilter.startsWith("All")
                    || row[3].equalsIgnoreCase(priorityFilter);
                if (matchQ && matchS && matchP) model.addRow(row);
            }
        } catch (IOException ignored) {}
    }
 
    // ── Change Status Dialog ──────────────────────────────────────────────────
    private void changeStatus() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showInfoToast("Please select a bug first", new Color(251, 191, 36));
            return;
        }
 
        String bugId         = model.getValueAt(selectedRow, 0).toString().trim();
        String bugName       = model.getValueAt(selectedRow, 1).toString().trim();
        String currentStatus = model.getValueAt(selectedRow, 7).toString().trim();
 
        JDialog dialog = new JDialog(this, "Change Status", true);
        dialog.setUndecorated(true);
        dialog.setSize(440, 240);
        dialog.setLocationRelativeTo(this);
 
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setPaint(new GradientPaint(0, 0, new Color(251, 191, 36), getWidth(), getHeight(), ACCENT));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
 
        JPanel headerStrip = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0,
                    new Color(251, 191, 36, 40), getWidth(), 0,
                    new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 40)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new GradientPaint(0, getHeight()-1, new Color(251,191,36), getWidth(), getHeight()-1, ACCENT));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        headerStrip.setOpaque(false);
        headerStrip.setBounds(0, 0, 440, 60);
        card.add(headerStrip);
 
        JLabel icon = new JLabel("✏️");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        icon.setBounds(18, 14, 36, 32);
        headerStrip.add(icon);
 
        JLabel titleLbl = new JLabel("Change Status — Bug #" + bugId);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(new Color(251, 191, 36));
        titleLbl.setBounds(60, 10, 340, 20);
        headerStrip.add(titleLbl);
 
        JLabel subLbl = new JLabel(bugName.length() > 50 ? bugName.substring(0, 47) + "..." : bugName);
        subLbl.setFont(FONT_SUB);
        subLbl.setForeground(TEXT_DIM);
        subLbl.setBounds(61, 32, 340, 16);
        headerStrip.add(subLbl);
 
        JButton closeX = winBtn("✕");
        closeX.setBounds(404, 8, 28, 24);
        closeX.addActionListener(e -> dialog.dispose());
        card.add(closeX);
 
        JLabel lbl = new JLabel("Select new status:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(TEXT_DIM);
        lbl.setBounds(24, 76, 150, 18);
        card.add(lbl);
 
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Open", "In Progress", "Closed"});
        statusCombo.setSelectedItem(currentStatus);
        statusCombo.setFont(FONT_CELL);
        statusCombo.setForeground(TEXT_MAIN);
        statusCombo.setBackground(new Color(10, 18, 42));
        statusCombo.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        statusCombo.setBounds(140, 70, 270, 30);
        card.add(statusCombo);
 
        JButton cancelBtn = buildButton("  Cancel  ", TEXT_DIM);
        cancelBtn.setBounds(135, 158, 130, 36);
        cancelBtn.addActionListener(e -> dialog.dispose());
        card.add(cancelBtn);
 
        JButton saveBtn = buildButton("  Apply  ", new Color(251, 191, 36));
        saveBtn.setBounds(280, 158, 130, 36);
        saveBtn.addActionListener(e -> {
            String newStatus = (String) statusCombo.getSelectedItem();
            if (newStatus == null || newStatus.equalsIgnoreCase(currentStatus)) {
                showInfoToast("Status unchanged", new Color(251, 191, 36));
                dialog.dispose();
                return;
            }
            dialog.dispose();
            // ✅ استخدام developer.updateBugStatus() بدل الـ logic المكررة
            developer.updateBugStatus(bugId, newStatus);
            if (newStatus.equalsIgnoreCase("Closed")) {
                SwingUtilities.invokeLater(() ->
                    showInfoToast("Status updated & Email sent to Tester", COLOR_UPD));
            } else {
                showInfoToast("Bug #" + bugId + " status → " + newStatus, COLOR_UPD);
            }
            loadBugs();
        });
        card.add(saveBtn);
 
        makeDraggable(card);
        dialog.setContentPane(card);
        dialog.setVisible(true);
    }
 
    // ── Logout Dialog ─────────────────────────────────────────────────────────
    private void showLogoutDialog() {
        JDialog dialog = new JDialog(this, "Logout", true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
 
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setPaint(new GradientPaint(0, 0, COLOR_DEL, getWidth(), getHeight(), new Color(180,30,60)));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
 
        JLabel warnIcon = new JLabel("⚠️");
        warnIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 34));
        warnIcon.setBounds(0, 20, 400, 44);
        warnIcon.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(warnIcon);
 
        JLabel titleLbl = new JLabel("Confirm Logout");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(COLOR_DEL);
        titleLbl.setHorizontalAlignment(SwingConstants.CENTER);
        titleLbl.setBounds(0, 68, 400, 22);
        card.add(titleLbl);
 
        JLabel subLbl = new JLabel("Are you sure you want to logout?");
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLbl.setForeground(TEXT_DIM);
        subLbl.setHorizontalAlignment(SwingConstants.CENTER);
        subLbl.setBounds(0, 94, 400, 18);
        card.add(subLbl);
 
        JButton cancelBtn = buildButton("  Cancel  ", TEXT_DIM);
        cancelBtn.setBounds(100, 138, 120, 36);
        cancelBtn.addActionListener(e -> dialog.dispose());
        card.add(cancelBtn);
 
        JButton logoutBtn = buildButton("  Logout  ", COLOR_DEL);
        logoutBtn.setBounds(238, 138, 120, 36);
        logoutBtn.addActionListener(e -> {
            dialog.dispose();
            showInfoToast("Logged out. Redirecting...", COLOR_UPD);
            Timer t = new Timer(1200, ev -> {
                if (bgTimer != null) bgTimer.stop();
                dispose();
                new LoginFrame();
            });
            t.setRepeats(false);
            t.start();
        });
        card.add(logoutBtn);
 
        makeDraggable(card);
        dialog.setContentPane(card);
        dialog.setVisible(true);
    }
 
    // ── Toast ─────────────────────────────────────────────────────────────────
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
 
    // ── Background ────────────────────────────────────────────────────────────
    private void drawBackground(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BG_DEEP);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(new Color(25, 45, 90, 12));
        for (int x = 0; x < getWidth();  x += 32) g2.drawLine(x, 0, x, getHeight());
        for (int y = 0; y < getHeight(); y += 32) g2.drawLine(0, y, getWidth(), y);
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
                ht = new Timer(16, ev -> {
                    hov += (t - hov) * 0.2f;
                    if (Math.abs(hov - t) < 0.01f) { hov = t; ht.stop(); }
                    repaint();
                });
                ht.start();
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0,
                    new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 200),
                    getWidth(), getHeight(),
                    new Color(accent.getRed()/2, accent.getGreen()/2, accent.getBlue()/2 + 80, 200)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                if (hov > 0) {
                    g2.setColor(new Color(255, 255, 255, (int)(hov * 30)));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.setColor(Color.WHITE);
                g2.setFont(FONT_BTN);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()  - fm.stringWidth(getText())) / 2,
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
 
    // ── Status Renderer ───────────────────────────────────────────────────────
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(
                JTable t, Object value, boolean selected, boolean focused, int row, int col) {
            JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            cell.setOpaque(true);
            cell.setBackground(row % 2 == 0 ? BG_CARD : BG_ROW_ALT);
            if (selected) cell.setBackground(SEL_BG);
 
            String status = value == null ? "" : value.toString().trim();
            Color fg;
            switch (status.toLowerCase()) {
                case "open"        -> fg = new Color(0, 210, 255);
                case "closed"      -> fg = PRI_LOW;
                case "in progress" -> fg = PRI_MED;
                default            -> fg = TEXT_DIM;
            }
            final Color _fg = fg;
 
            JLabel badge = new JLabel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(_fg.getRed(), _fg.getGreen(), _fg.getBlue(), 35));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                    g2.setColor(new Color(_fg.getRed(), _fg.getGreen(), _fg.getBlue(), 120));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, getHeight(), getHeight());
                    g2.setColor(_fg);
                    g2.fillOval(8, getHeight()/2 - 3, 6, 6);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(status, 20, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                    g2.dispose();
                }
            };
            badge.setPreferredSize(new Dimension(
                status.equalsIgnoreCase("in progress") ? 100 : 72, 22));
            badge.setOpaque(false);
            cell.add(badge);
            return cell;
        }
    }
 
    // ── Custom scroll bar ─────────────────────────────────────────────────────
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
