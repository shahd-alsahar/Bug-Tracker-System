package bugtrackerprojectsh.GUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
import javax.swing.text.AbstractDocument;

public class UserManagementFrame extends JFrame {

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color BG_DEEP      = new Color(6,   11,  24);
    private static final Color BG_CARD      = new Color(13,  22,  44);
    private static final Color BG_ROW_ALT   = new Color(17,  28,  54);
    private static final Color ACCENT       = new Color(0,  210, 255);
    private static final Color ACCENT2      = new Color(100, 60, 255);
    private static final Color COLOR_CLOS   = new Color(0,   210, 140);
    private static final Color COLOR_DANGER = new Color(220,  60,  80);
    private static final Color COLOR_WARN   = new Color(255, 120,  60);
    private static final Color COLOR_PM     = new Color(160,  80, 255);
    private static final Color COLOR_DEV    = new Color(0,   210, 255);
    private static final Color COLOR_TEST   = new Color(255, 180,  40);
    private static final Color TEXT_MAIN    = new Color(220, 235, 255);
    private static final Color TEXT_DIM     = new Color(90,  120, 165);
    private static final Color BORDER_CLR   = new Color(30,   55, 105);
    private static final Color SEL_BG       = new Color(0,  140, 200, 120);

    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font FONT_SUB    = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  11);
    private static final Font FONT_CELL   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font FONT_BADGE  = new Font("Segoe UI", Font.BOLD,   9);

    private static final String USER_FILE = "user.txt";

    // ── State ─────────────────────────────────────────────────────────────────
    private JPanel root, bgLayer;
    private float  orbAngle = 0f;
    private Timer  bgTimer;

    private DefaultTableModel tableModel;
    private JTable table;
    private List<String[]> userList = new ArrayList<>();

    // Stat labels
    private JLabel totalLbl, adminLbl, pmLbl, devLbl, testerLbl;

    // Form fields
    private JTextField fldId, fldName, fldEmail;
    private JPasswordField fldPassword;
    private JComboBox<String> fldRole;

    // ── Constructors ──────────────────────────────────────────────────────────
    public UserManagementFrame() {
        initWindow(); buildUI(); loadUsers(); setVisible(true);
    }

    public UserManagementFrame(String callerName) {
        initWindow(); buildUI(); loadUsers(); setVisible(true);
    }

    // ── Window ────────────────────────────────────────────────────────────────
    private void initWindow() {
        setTitle("Bug Tracker — User Management");
        setSize(1200, 720);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setBackground(BG_DEEP);
    }

    // ── UI ────────────────────────────────────────────────────────────────────
    private void buildUI() {
        JLayeredPane layered = new JLayeredPane();
        setContentPane(layered);

        bgLayer = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBackground(g);
            }
        };
        bgLayer.setOpaque(true);
        bgLayer.setBackground(BG_DEEP);
        layered.add(bgLayer, JLayeredPane.DEFAULT_LAYER);

        root = new JPanel(new BorderLayout(0, 0));
        root.setOpaque(false);
        layered.add(root, JLayeredPane.PALETTE_LAYER);

        bgLayer.setBounds(0, 0, getWidth(), getHeight());
        root.setBounds(0, 0, getWidth(), getHeight());

        layered.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                Dimension s = layered.getSize();
                bgLayer.setBounds(0, 0, s.width, s.height);
                root.setBounds(0, 0, s.width, s.height);
            }
        });

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

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
                g2.setPaint(new GradientPaint(0, getHeight()-1, ACCENT, getWidth(), getHeight()-1, ACCENT2));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 70));

        JLabel av = new JLabel("UM", SwingConstants.CENTER) {
            private float ring = 2f, ralpha = 0.3f;
            { Timer t = new Timer(50, e -> { ring=(ring>=5f)?2f:ring+0.05f; ralpha=(ralpha<=0.1f)?0.3f:ralpha-0.004f; repaint(); }); t.start(); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0f, 0.82f, 1f, ralpha));
                int r = (int)(ring*2);
                g2.fillOval(-r/2, -r/2, getWidth()+r, getHeight()+r);
                g2.setPaint(new GradientPaint(0, 0, ACCENT, getWidth(), getHeight(), ACCENT2));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        av.setFont(new Font("Segoe UI", Font.BOLD, 12));
        av.setForeground(Color.WHITE);
        av.setOpaque(false);
        av.setBounds(20, 14, 42, 42);
        header.add(av);

        JLabel title = new JLabel("User Management") {
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
        title.setBounds(72, 12, 340, 28);
        header.add(title);

        JLabel sub = new JLabel("View, Add, Update & Delete System Users");
        sub.setFont(FONT_SUB);
        sub.setForeground(TEXT_DIM);
        sub.setBounds(73, 42, 380, 16);
        header.add(sub);

        JButton backBtn = new JButton("← Admin Dashboard") {
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
                    hov += (t - hov) * 0.18f;
                    if (Math.abs(hov - t) < 0.01f) { hov = t; ht.stop(); }
                    repaint();
                });
                ht.start();
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(100, 60, 255, (int)(20 + hov * 40)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                if (hov > 0) {
                    g2.setPaint(new GradientPaint(0, 0, new Color(255,255,255,(int)(hov*15)), getWidth(), getHeight(), new Color(255,255,255,0)));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                }
                g2.setPaint(new GradientPaint(0, 0, ACCENT2, getWidth(), 0, ACCENT));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
                g2.setPaint(new GradientPaint(0, 0, ACCENT2, getWidth(), 0, ACCENT));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setBounds(870, 17, 180, 36);
        backBtn.addActionListener(e -> dispose());
        header.add(backBtn);

        return header;
    }

    // ── Center ────────────────────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));

        center.add(buildStatCards(), BorderLayout.NORTH);

        JPanel split = new JPanel(new GridLayout(1, 2, 14, 0));
        split.setOpaque(false);
        split.add(buildTableCard());
        split.add(buildFormCard());
        center.add(split, BorderLayout.CENTER);

        return center;
    }

    // ── Stat Cards ────────────────────────────────────────────────────────────
    private JPanel buildStatCards() {
        JPanel row = new JPanel(new GridLayout(1, 5, 10, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 72));

        totalLbl  = mkStatLbl("0", ACCENT);
        adminLbl  = mkStatLbl("0", COLOR_DANGER);
        pmLbl     = mkStatLbl("0", COLOR_PM);
        devLbl    = mkStatLbl("0", COLOR_DEV);
        testerLbl = mkStatLbl("0", COLOR_TEST);

        row.add(wrapStat(totalLbl,  "Total Users", ACCENT));
        row.add(wrapStat(adminLbl,  "Admins",      COLOR_DANGER));
        row.add(wrapStat(pmLbl,     "PMs",         COLOR_PM));
        row.add(wrapStat(devLbl,    "Developers",  COLOR_DEV));
        row.add(wrapStat(testerLbl, "Testers",     COLOR_TEST));
        return row;
    }

    private JLabel mkStatLbl(String v, Color c) {
        JLabel l = new JLabel(v, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 24));
        l.setForeground(c);
        return l;
    }

    private JPanel wrapStat(JLabel lbl, String title, Color accent) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 55));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.setPaint(new GradientPaint(0,0,accent,getWidth(),0,new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),70)));
                g2.fillRoundRect(0, 0, getWidth(), 3, 3, 3);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(FONT_BADGE);
        t.setForeground(TEXT_DIM);
        t.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
        card.add(t,   BorderLayout.NORTH);
        card.add(lbl, BorderLayout.CENTER);
        return card;
    }

    // ── Table Card ────────────────────────────────────────────────────────────
    private JPanel buildTableCard() {
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Email", "Role"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
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
        table.setFillsViewportHeight(true);
        table.setFocusable(false);

        JTableHeader th = table.getTableHeader();
        th.setFont(FONT_HEADER);
        th.setBackground(new Color(8, 16, 38));
        th.setForeground(ACCENT);
        th.setPreferredSize(new Dimension(0, 36));
        th.setReorderingAllowed(false);

        DefaultTableCellRenderer hRend = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = new JLabel(v == null ? "" : v.toString().toUpperCase());
                lbl.setFont(FONT_HEADER); lbl.setForeground(ACCENT);
                lbl.setBackground(new Color(8, 16, 38)); lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_CLR),
                    BorderFactory.createEmptyBorder(0, 10, 0, 10)));
                return lbl;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setHeaderRenderer(hRend);

        DefaultTableCellRenderer def = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = new JLabel(v == null ? "" : v.toString());
                lbl.setForeground(TEXT_MAIN);
                lbl.setBackground(s ? SEL_BG : r%2==0 ? BG_CARD : BG_ROW_ALT);
                lbl.setOpaque(true); lbl.setFont(FONT_CELL);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return lbl;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setCellRenderer(def);

        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                String val = v == null ? "" : v.toString().toLowerCase();
                JLabel lbl = new JLabel(v == null ? "" : v.toString());
                lbl.setForeground(roleColor(val));
                lbl.setBackground(s ? SEL_BG : r%2==0 ? BG_CARD : BG_ROW_ALT);
                lbl.setOpaque(true);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return lbl;
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(140);
        table.getColumnModel().getColumn(2).setPreferredWidth(210);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                if (row < userList.size()) fillForm(userList.get(row));
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JTextField searchField = buildField("🔍  Search by name, email or role...");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filterTable(searchField.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filterTable(searchField.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(searchField.getText()); }
        });

        JPanel card = buildRoundCard();
        card.setLayout(new BorderLayout(0, 0));

        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 8, 10));

        JLabel heading = new JLabel("  👥  All Users");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 13));
        heading.setForeground(TEXT_MAIN);
        topBar.add(heading,     BorderLayout.WEST);
        topBar.add(searchField, BorderLayout.CENTER);

        card.add(topBar,  BorderLayout.NORTH);
        card.add(scroll,  BorderLayout.CENTER);
        return card;
    }

    private void filterTable(String query) {
        String q = query.trim().toLowerCase();
        tableModel.setRowCount(0);
        for (String[] u : userList) {
            if (q.isEmpty()
                || u[1].toLowerCase().contains(q)
                || u[2].toLowerCase().contains(q)
                || u[4].toLowerCase().contains(q)) {
                tableModel.addRow(new Object[]{u[0], u[1], u[2], u[4]});
            }
        }
    }

    // ── Form Card ─────────────────────────────────────────────────────────────
    private JPanel buildFormCard() {
        JPanel card = buildRoundCard();
        card.setLayout(new BorderLayout());

        JLabel heading = new JLabel("  ✏️  User Details");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 13));
        heading.setForeground(TEXT_MAIN);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
        card.add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(8, 22, 8, 22));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(7, 4, 7, 4);

        // ID
        gc.gridx=0; gc.gridy=0; gc.weightx=0.3;
        form.add(formLabel("ID  (auto)"), gc);
        gc.gridx=1; gc.weightx=0.7;
        fldId = buildField("Auto-generated");
        fldId.setEditable(false);
        fldId.setForeground(TEXT_DIM);
        form.add(fldId, gc);

        // Full Name (no digits)
        gc.gridx=0; gc.gridy=1; gc.weightx=0.3;
        form.add(formLabel("Full Name"), gc);
        gc.gridx=1; gc.weightx=0.7;
        fldName = buildField("Enter full name...");
        ((AbstractDocument) fldName.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr)
                    throws javax.swing.text.BadLocationException {
                if (string != null && string.matches(".*\\d.*")) {
                    showToast("the name should not contain numbers!", COLOR_WARN);
                    return;
                }
                super.insertString(fb, offset, string, attr);
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs)
                    throws javax.swing.text.BadLocationException {
                if (text != null && text.matches(".*\\d.*")) {
                    showToast("the name should not contain numbers!", COLOR_WARN);
                    return;
                }
                super.replace(fb, offset, length, text, attrs);
            }
        });
        form.add(fldName, gc);

        // Email
        gc.gridx=0; gc.gridy=2; gc.weightx=0.3;
        form.add(formLabel("Email"), gc);
        gc.gridx=1; gc.weightx=0.7;
        fldEmail = buildField("example@gmail.com");
        form.add(fldEmail, gc);

        // Password + Eye Button
        gc.gridx=0; gc.gridy=3; gc.weightx=0.3;
        form.add(formLabel("Password"), gc);
        gc.gridx=1; gc.weightx=0.7;
        form.add(buildPasswordPanel(), gc);

        // Role
        gc.gridx=0; gc.gridy=4; gc.weightx=0.3;
        form.add(formLabel("Role"), gc);
        gc.gridx=1; gc.weightx=0.7;
        fldRole = new JComboBox<>(new String[]{"admin", "pm", "developer", "tester"});
        styleCombo(fldRole);
        form.add(fldRole, gc);

        // Spacer
        gc.gridx=0; gc.gridy=5; gc.gridwidth=2;
        gc.weighty=1.0; gc.fill=GridBagConstraints.BOTH;
        form.add(Box.createVerticalGlue(), gc);
        gc.weighty=0; gc.fill=GridBagConstraints.HORIZONTAL;

        // Buttons row 1
        gc.gridy=6; gc.insets=new Insets(4, 4, 6, 4);
        JPanel btnRow1 = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow1.setOpaque(false);
        JButton addBtn    = buildBtn("＋  Add User", COLOR_CLOS);
        JButton updateBtn = buildBtn("✎  Update",    ACCENT);
        btnRow1.add(addBtn);
        btnRow1.add(updateBtn);
        form.add(btnRow1, gc);

        gc.gridy=7; gc.insets=new Insets(0, 4, 6, 4);
        JPanel btnRow2 = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow2.setOpaque(false);
        JButton deleteBtn = buildBtn("✕  Delete", COLOR_DANGER);
        JButton clearBtn  = buildBtn("↺  Clear",  TEXT_DIM);
        btnRow2.add(deleteBtn);
        btnRow2.add(clearBtn);
        form.add(btnRow2, gc);

        addBtn.addActionListener(e    -> handleAdd());
        updateBtn.addActionListener(e -> handleUpdate());
        deleteBtn.addActionListener(e -> handleDelete());
        clearBtn.addActionListener(e  -> clearForm());

        card.add(form, BorderLayout.CENTER);
        return card;
    }

    // ── Password Panel with Eye Toggle ────────────────────────────────────────
    private JPanel buildPasswordPanel() {

        // ── حقل الباسورد ──────────────────────────────────────────────────────
        fldPassword = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(8, 16, 38));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        fldPassword.setOpaque(false);
        fldPassword.setFont(FONT_CELL);
        fldPassword.setForeground(TEXT_MAIN);
        fldPassword.setCaretColor(ACCENT);
        fldPassword.setEchoChar('●');
        fldPassword.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 6));

        // ── زرار العين ────────────────────────────────────────────────────────
        final boolean[] visible = {false};

        JButton eyeBtn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // خلفية الزرار
                g2.setColor(new Color(8, 16, 38));
                g2.fillRect(0, 0, getWidth(), getHeight());

                // رسم أيقونة العين يدوياً بـ Graphics2D
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                Color eyeColor = visible[0] ? ACCENT : TEXT_DIM;
                g2.setColor(eyeColor);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // شكل العين (قوس علوي + قوس سفلي)
                g2.drawArc(cx - 7, cy - 4, 14, 9,   0, 180);
                g2.drawArc(cx - 7, cy - 5, 14, 9, 180, 180);

                // البؤبؤ
                g2.fillOval(cx - 2, cy - 2, 5, 5);

                // خط مائل لو الباسورد مخفي
                if (!visible[0]) {
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(TEXT_DIM);
                    g2.drawLine(cx - 7, cy + 5, cx + 7, cy - 5);
                }
                g2.dispose();
            }
        };
        eyeBtn.setContentAreaFilled(false);
        eyeBtn.setBorderPainted(false);
        eyeBtn.setFocusPainted(false);
        eyeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eyeBtn.setPreferredSize(new Dimension(32, 34));

        eyeBtn.addActionListener(e -> {
            visible[0] = !visible[0];
            fldPassword.setEchoChar(visible[0] ? (char) 0 : '●');
            eyeBtn.repaint();
            fldPassword.requestFocus();
        });

        // ── Wrapper: حقل + زرار بحد موحد ─────────────────────────────────────
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(8, 16, 38));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        wrapper.setOpaque(false);
        wrapper.setBorder(new RoundBorder(BORDER_CLR, 8));
        wrapper.setPreferredSize(new Dimension(0, 34));
        wrapper.add(fldPassword, BorderLayout.CENTER);
        wrapper.add(eyeBtn,      BorderLayout.EAST);

        return wrapper;
    }

    // ── Email Validation ──────────────────────────────────────────────────────
    private boolean isValidEmail(String email) {
        return email != null && email.toLowerCase().endsWith("@gmail.com") && email.length() > 10;
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────
    private void handleAdd() {
        String name  = fldName.getText().trim();
        String email = fldEmail.getText().trim();
        String pass  = new String(fldPassword.getPassword()).trim();
        String role  = (String) fldRole.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            showToast("Name, Email and Password are required!", COLOR_WARN); return;
        }
        if (!isValidEmail(email)) {
            showToast("the email must be in the format: example@gmail.com", COLOR_WARN); return;
        }
        for (String[] u : userList) {
            if (u[2].equalsIgnoreCase(email)) {
                showToast("Email already exists!", COLOR_WARN); return;
            }
        }

        int nextId = userList.stream()
            .mapToInt(u -> { try { return Integer.parseInt(u[0]); } catch(Exception ex){ return 0; } })
            .max().orElse(0) + 1;

        userList.add(new String[]{String.valueOf(nextId), name, email, pass, role});
        saveUsers();
        refreshTable();
        clearForm();
        showToast("User added: " + name, COLOR_CLOS);
    }

    private void handleUpdate() {
        int sel = table.getSelectedRow();
        if (sel < 0 || fldId.getText().trim().isEmpty()) {
            showToast("Select a user from the table first!", COLOR_WARN); return;
        }
        String id    = fldId.getText().trim();
        String name  = fldName.getText().trim();
        String email = fldEmail.getText().trim();
        String pass  = new String(fldPassword.getPassword()).trim();
        String role  = (String) fldRole.getSelectedItem();

        if (name.isEmpty() || email.isEmpty()) {
            showToast("Name and Email are required!", COLOR_WARN); return;
        }
        if (!isValidEmail(email)) {
            showToast("the email must be in the format: example@gmail.com", COLOR_WARN); return;
        }

        for (String[] u : userList) {
            if (u[0].equals(id)) {
                u[1] = name; u[2] = email;
                if (!pass.isEmpty()) u[3] = pass;
                u[4] = role;
                break;
            }
        }
        saveUsers();
        refreshTable();
        showToast("User updated: " + name, ACCENT);
    }

    private void handleDelete() {
        String id = fldId.getText().trim();
        if (id.isEmpty()) { showToast("Select a user from the table first!", COLOR_WARN); return; }

        String name = fldName.getText().trim();
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete user \"" + name + "\"?", "Confirm Delete",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        userList.removeIf(u -> u[0].equals(id));
        saveUsers();
        refreshTable();
        clearForm();
        showToast("User deleted: " + name, COLOR_DANGER);
    }

    private void fillForm(String[] u) {
        fldId.setText(u[0]);
        fldName.setText(u[1]);
        fldEmail.setText(u[2]);
        fldPassword.setText(u[3]);
        String role = u[4].toLowerCase();
        for (int i = 0; i < fldRole.getItemCount(); i++) {
            if (fldRole.getItemAt(i).equalsIgnoreCase(role)) { fldRole.setSelectedIndex(i); break; }
        }
    }

    private void clearForm() {
        fldId.setText("");
        fldName.setText("");
        fldEmail.setText("");
        fldPassword.setText("");
        fldRole.setSelectedIndex(2);
        table.clearSelection();
    }

    // ── Load / Save ───────────────────────────────────────────────────────────
    private void loadUsers() {
        userList.clear();
        File f = new File(USER_FILE);
        if (!f.exists()) { refreshTable(); return; }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split(",", -1);
                if (p.length >= 5) {
                    userList.add(new String[]{p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim(), p[4].trim()});
                } else if (p.length >= 3) {
                    int nextId = userList.size() + 1;
                    userList.add(new String[]{
                        String.valueOf(nextId),
                        p[0].trim(),
                        p[0].trim() + "@gmail.com",
                        p[1].trim(),
                        p[2].trim()
                    });
                }
            }
        } catch (IOException e) {
            showToast("Error reading users: " + e.getMessage(), COLOR_DANGER);
        }
        refreshTable();
    }

    private void saveUsers() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USER_FILE))) {
            for (String[] u : userList)
                pw.println(u[0] + "," + u[1] + "," + u[2] + "," + u[3] + "," + u[4]);
        } catch (IOException e) {
            showToast("Error saving users: " + e.getMessage(), COLOR_DANGER);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        int total=0, admins=0, pms=0, devs=0, testers=0;
        for (String[] u : userList) {
            tableModel.addRow(new Object[]{u[0], u[1], u[2], u[4]});
            total++;
            switch (u[4].toLowerCase()) {
                case "admin"     -> admins++;
                case "pm"        -> pms++;
                case "developer" -> devs++;
                case "tester"    -> testers++;
            }
        }
        int t=total, a=admins, p=pms, d=devs, ts=testers;
        SwingUtilities.invokeLater(() -> {
            totalLbl.setText(String.valueOf(t));
            adminLbl.setText(String.valueOf(a));
            pmLbl.setText(String.valueOf(p));
            devLbl.setText(String.valueOf(d));
            testerLbl.setText(String.valueOf(ts));
        });
    }

    // ── Footer ────────────────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));

        JButton backBtn  = buildBtn("←  Back",  ACCENT2);
        JButton closeBtn = buildBtn("✕  Close", COLOR_DANGER);

        backBtn.addActionListener(e  -> dispose());
        closeBtn.addActionListener(e -> { if(bgTimer!=null) bgTimer.stop(); dispose(); });

        footer.add(backBtn);
        footer.add(closeBtn);

        JPanel fp = new JPanel(new BorderLayout());
        fp.setOpaque(false);
        fp.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR));

        JLabel status = new JLabel("🟢  Bug Tracker Pro  ·  User Management", SwingConstants.RIGHT);
        status.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        status.setForeground(TEXT_DIM);
        status.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 18));

        fp.add(footer, BorderLayout.WEST);
        fp.add(status, BorderLayout.EAST);
        return fp;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private Color roleColor(String role) {
        return switch (role) {
            case "admin"     -> COLOR_DANGER;
            case "pm"        -> COLOR_PM;
            case "developer" -> COLOR_DEV;
            case "tester"    -> COLOR_TEST;
            default          -> TEXT_DIM;
        };
    }

    private JPanel buildRoundCard() {
        return new JPanel() {
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
    }

    private JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(TEXT_DIM);
        return l;
    }

    private JTextField buildField(String placeholder) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(8, 16, 38));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setOpaque(false);
        f.setFont(FONT_CELL);
        f.setForeground(TEXT_MAIN);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(BORDER_CLR, 8),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        f.setPreferredSize(new Dimension(0, 34));
        return f;
    }

    private void styleCombo(JComboBox<String> cb) {
        cb.setFont(FONT_CELL);
        cb.setForeground(TEXT_MAIN);
        cb.setBackground(new Color(8, 16, 38));
        cb.setPreferredSize(new Dimension(0, 34));
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object v, int i, boolean sel, boolean foc) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, v, i, sel, foc);
                lbl.setBackground(sel ? SEL_BG : new Color(8, 16, 38));
                lbl.setForeground(v != null ? roleColor(v.toString().toLowerCase()) : TEXT_MAIN);
                lbl.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
                return lbl;
            }
        });
    }

    private static class RoundBorder extends AbstractBorder {
        private final Color color; private final int radius;
        RoundBorder(Color c, int r) { color=c; radius=r; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w-1, h-1, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(2,2,2,2); }
    }

    private JButton buildBtn(String text, Color accent) {
        JButton b = new JButton(text) {
            private float hov=0f; private Timer ht;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e){anim(1f);}
                public void mouseExited(MouseEvent e){anim(0f);}
            }); }
            void anim(float t){ if(ht!=null)ht.stop(); ht=new Timer(16,ev->{hov+=(t-hov)*0.2f;if(Math.abs(hov-t)<0.01f){hov=t;ht.stop();}repaint();}); ht.start(); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0,new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),200),
                    getWidth(),getHeight(),new Color(accent.getRed()/2,accent.getGreen()/2,accent.getBlue()/2+80,200)));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                if(hov>0){g2.setColor(new Color(255,255,255,(int)(hov*30)));g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);}
                g2.setColor(Color.WHITE); g2.setFont(FONT_BTN);
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(148, 36));
        b.setContentAreaFilled(false); b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void showToast(String msg, Color accent) {
        JWindow toast = new JWindow(this);
        toast.setBackground(new Color(0,0,0,0));
        JPanel p = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(13,22,44,230));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(accent); g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,12,12);
                g2.dispose();
            }
        };
        p.setOpaque(false); p.setPreferredSize(new Dimension(380,42));
        JLabel l = new JLabel("  " + msg);
        l.setFont(new Font("Segoe UI",Font.BOLD,12)); l.setForeground(accent);
        l.setBounds(14,0,350,42); p.add(l);
        toast.setContentPane(p); toast.pack();
        Point loc = getLocation();
        toast.setLocation(loc.x+(getWidth()-380)/2, loc.y+getHeight()-70);
        toast.setVisible(true);
        new Timer(3000, e -> toast.dispose()) {{ setRepeats(false); start(); }};
    }

    private void addWindowControls() {
        JPanel glass = new JPanel(null);
        glass.setOpaque(false);
        setGlassPane(glass);
        glass.setVisible(true);

        JButton closeBtn = winBtn("✕");
        closeBtn.setBounds(1162, 6, 28, 22);
        closeBtn.addActionListener(e -> { if(bgTimer!=null) bgTimer.stop(); dispose(); });

        JButton miniBtn = winBtn("─");
        miniBtn.setBounds(1130, 6, 28, 22);
        miniBtn.addActionListener(e -> setState(Frame.ICONIFIED));

        glass.add(closeBtn);
        glass.add(miniBtn);
    }

    private JButton winBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI",Font.PLAIN,11));
        b.setForeground(TEXT_DIM);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){b.setForeground(TEXT_MAIN);}
            public void mouseExited(MouseEvent e){b.setForeground(TEXT_DIM);}
        });
        return b;
    }

    private void makeDraggable(JComponent comp) {
        final Point[] drag = {null};
        comp.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){drag[0]=e.getPoint();}
            public void mouseReleased(MouseEvent e){drag[0]=null;}
        });
        comp.addMouseMotionListener(new MouseMotionAdapter(){
            public void mouseDragged(MouseEvent e){
                if(drag[0]==null)return;
                Point loc=getLocation();
                setLocation(loc.x+e.getX()-drag[0].x,loc.y+e.getY()-drag[0].y);
            }
        });
    }

    private void drawBackground(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BG_DEEP);
        g2.fillRect(0,0,getWidth(),getHeight());
        g2.setColor(new Color(25,45,90,12));
        for(int x=0;x<getWidth();x+=32) g2.drawLine(x,0,x,getHeight());
        for(int y=0;y<getHeight();y+=32) g2.drawLine(0,y,getWidth(),y);
        double r = Math.toRadians(orbAngle);
        drawOrb(g2,(int)(getWidth()*0.07+Math.cos(r)*50),(int)(getHeight()*0.13+Math.sin(r)*35),220,ACCENT,30);
        drawOrb(g2,(int)(getWidth()*0.93+Math.sin(r)*40),(int)(getHeight()*0.87+Math.cos(r)*30),200,ACCENT2,28);
        g2.dispose();
    }

    private void drawOrb(Graphics2D g2,int cx,int cy,int r,Color c,int max) {
        for(int i=r;i>0;i-=14){
            int alpha=(int)(max*(1f-(float)i/r));
            g2.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),alpha));
            g2.fillOval(cx-i,cy-i,i*2,i*2);
        }
    }

    private void startBgAnim() {
        bgTimer = new Timer(30, e -> { orbAngle=(orbAngle+0.5f)%360; bgLayer.repaint(); });
        bgTimer.start();
    }
}
