package bugtrackerprojectsh.GUI;
 
import bugtrackerprojectsh.Admin;           
 
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.Timer;
 
public class AdminFrame extends JFrame {
 
    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color BG_DEEP      = new Color(6,   11,  24);
    private static final Color BG_CARD      = new Color(13,  22,  44);
    private static final Color BG_ROW_ALT   = new Color(17,  28,  54);
    private static final Color ACCENT       = new Color(0,  210, 255);
    private static final Color ACCENT2      = new Color(100, 60, 255);
    private static final Color COLOR_OPEN   = new Color(255, 180,  40);
    private static final Color COLOR_CLOS   = new Color(0,   210, 140);
    private static final Color COLOR_PROG   = new Color(0,   210, 255);
    private static final Color COLOR_DANGER = new Color(220,  60,  80);
    private static final Color COLOR_WARN   = new Color(255, 120,  60);
    private static final Color TEXT_MAIN    = new Color(220, 235, 255);
    private static final Color TEXT_DIM     = new Color(90,  120, 165);
    private static final Color BORDER_CLR   = new Color(30,   55, 105);
    private static final Color SEL_BG       = new Color(0,  140, 200, 120);
 
    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font FONT_SUB    = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  11);
    private static final Font FONT_CELL   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font FONT_BADGE  = new Font("Segoe UI", Font.BOLD,   9);
 
    // ── Business Logic Delegate ───────────────────────────────────────────────
    private final Admin admin = new Admin();   
 
    // ── State ─────────────────────────────────────────────────────────────────
    private JPanel      root, bgLayer;
    private JTabbedPane tabs;
    private float       orbAngle = 0f;
    private Timer       bgTimer;
 
    // Bugs tab
    private DefaultTableModel bugModel;
    private JLabel totalBugLbl, openLbl, closedLbl, progressLbl;
 
    // Users tab
    private DefaultTableModel userModel;
    private List<String[]> userList = new ArrayList<>(); // [id, name, email, password, role]
 
    // ── Constructors ──────────────────────────────────────────────────────────
    /*public AdminFrame() {
        initWindow();
        buildUI();
        loadBugs();
        loadUsers();
        setVisible(true);
    }*/
 
    public AdminFrame(String name) {
        initWindow();
        buildUI();
        loadBugs();
        loadUsers();
        setVisible(true);
    }
 
    // ── Window ────────────────────────────────────────────────────────────────
    private void initWindow() {
        setTitle("Bug Tracker — Admin Dashboard");
        setSize(1150, 700);
        setMinimumSize(new Dimension(950, 580));
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
 
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildTabs(),   BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
 
        bgLayer.setBounds(0, 0, getWidth(), getHeight());
        root.setBounds(0, 0, getWidth(), getHeight());
 
        layered.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                Dimension s = layered.getSize();
                bgLayer.setBounds(0, 0, s.width, s.height);
                root.setBounds(0, 0, s.width, s.height);
            }
        });
 
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
                g2.setPaint(new GradientPaint(0, getHeight()-1, COLOR_DANGER,
                    getWidth(), getHeight()-1, ACCENT2));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 70));
 
        JLabel av = buildAvatar("AD");
        av.setBounds(20, 14, 42, 42);
        header.add(av);
 
        JLabel title = new JLabel("Administrator") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, COLOR_DANGER, getWidth(), 0, ACCENT2));
                g2.setFont(getFont());
                g2.drawString(getText(), 0, g2.getFontMetrics().getAscent());
                g2.dispose();
            }
        };
        title.setFont(FONT_TITLE);
        title.setBounds(72, 12, 320, 28);
        header.add(title);
 
        JLabel sub = new JLabel("Dashboard  ·  Bug Management & User Control");
        sub.setFont(FONT_SUB);
        sub.setForeground(TEXT_DIM);
        sub.setBounds(73, 42, 400, 16);
        header.add(sub);
 
        String today = LocalDate.now().format(
            DateTimeFormatter.ofPattern("EEE, MMM dd yyyy"));
        JLabel dateLabel = new JLabel(today);
        dateLabel.setFont(FONT_SUB);
        dateLabel.setForeground(TEXT_DIM);
        dateLabel.setBounds(640, 27, 200, 16);
        header.add(dateLabel);
 
        JButton umBtn = buildGlassButton("👥  User Management");
        umBtn.setBounds(855, 17, 182, 36);
        umBtn.addActionListener(e -> new UserManagementFrame("Admin"));
        header.add(umBtn);
 
        return header;
    }
 
    private JLabel buildAvatar(String initials) {
        JLabel av = new JLabel(initials, SwingConstants.CENTER) {
            private float ring = 2f, ralpha = 0.3f;
            {
                Timer t = new Timer(50, e -> {
                    ring   = (ring >= 5f)     ? 2f   : ring   + 0.05f;
                    ralpha = (ralpha <= 0.1f) ? 0.3f : ralpha - 0.004f;
                    repaint();
                });
                t.start();
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(1f, 0.23f, 0.31f, ralpha));
                int r = (int)(ring * 2);
                g2.fillOval(-r/2, -r/2, getWidth()+r, getHeight()+r);
                g2.setPaint(new GradientPaint(0, 0, COLOR_DANGER,
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
 
    // ── Tabs ──────────────────────────────────────────────────────────────────
    private JTabbedPane buildTabs() {
        tabs = new JTabbedPane() {
            @Override protected void paintComponent(Graphics g) { }
        };
        tabs.setOpaque(false);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.setForeground(TEXT_MAIN);
        tabs.setBackground(new Color(0, 0, 0, 0));
 
        tabs.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override protected void paintTabBackground(Graphics g, int tp, int ti,
                    int x, int y, int w, int h, boolean sel) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                if (sel) {
                    g2.setPaint(new GradientPaint(x, y,
                        new Color(COLOR_DANGER.getRed(), COLOR_DANGER.getGreen(),
                            COLOR_DANGER.getBlue(), 180),
                        x+w, y,
                        new Color(ACCENT2.getRed(), ACCENT2.getGreen(),
                            ACCENT2.getBlue(), 180)));
                } else {
                    g2.setColor(BG_CARD);
                }
                g2.fillRoundRect(x, y, w, h, 8, 8);
                g2.dispose();
            }
            @Override protected void paintTabBorder(Graphics g, int tp, int ti,
                    int x, int y, int w, int h, boolean sel) {}
            @Override protected void paintContentBorder(Graphics g, int tp, int si) {}
            @Override protected int calculateTabHeight(int tp, int ti, int fh) { return 36; }
        });
 
        tabs.setBorder(BorderFactory.createEmptyBorder(8, 14, 0, 14));
        tabs.addTab("  🐛  All Bugs  ",         buildBugsPanel());
        tabs.addTab("  👥  User Management  ",  buildUsersPanel());
        return tabs;
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  TAB 1 — BUGS
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildBugsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(buildBugStatCards(), BorderLayout.NORTH);
        panel.add(buildBugTableCard(), BorderLayout.CENTER);
        return panel;
    }
 
    private JPanel buildBugStatCards() {
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
 
        totalBugLbl = statLabel("0", ACCENT);
        openLbl     = statLabel("0", COLOR_OPEN);
        progressLbl = statLabel("0", COLOR_PROG);
        closedLbl   = statLabel("0", COLOR_CLOS);
 
        row.add(wrapStatCard(totalBugLbl, "Total Bugs",  ACCENT));
        row.add(wrapStatCard(openLbl,     "Open",        COLOR_OPEN));
        row.add(wrapStatCard(progressLbl, "In Progress", COLOR_PROG));
        row.add(wrapStatCard(closedLbl,   "Closed",      COLOR_CLOS));
        return row;
    }
 
    private JLabel statLabel(String val, Color c) {
        JLabel l = new JLabel(val, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 26));
        l.setForeground(c);
        return l;
    }
 
    private JPanel wrapStatCard(JLabel valLbl, String title, Color accent) {
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
                    new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 80)));
                g2.fillRoundRect(0, 0, getWidth(), 3, 3, 3);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(0, 72));
 
        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(FONT_BADGE);
        titleLbl.setForeground(TEXT_DIM);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
 
        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valLbl,   BorderLayout.CENTER);
        return card;
    }
 
    private JPanel buildBugTableCard() {
        bugModel = new DefaultTableModel(
            new String[]{"ID","Title","Description","Priority","Project",
                         "Reporter","Date","Status","Tester","Assigned To"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
 
        JTable table = buildStyledTable(bugModel);
 
        // Status column
        table.getColumnModel().getColumn(7).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean s, boolean f, int r, int c) {
                    JLabel lbl = new JLabel(v == null ? "" : v.toString());
                    String val = v == null ? "" : v.toString().toLowerCase();
                    lbl.setForeground(val.contains("closed")   ? COLOR_CLOS
                                    : val.contains("open")     ? COLOR_OPEN
                                    : val.contains("progress") ? COLOR_PROG
                                    : TEXT_DIM);
                    lbl.setBackground(s ? SEL_BG : r%2==0 ? BG_CARD : BG_ROW_ALT);
                    lbl.setOpaque(true);
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    lbl.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                    return lbl;
                }
            });
 
        // Priority column
        table.getColumnModel().getColumn(3).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean s, boolean f, int r, int c) {
                    JLabel lbl = new JLabel(v == null ? "" : v.toString());
                    String val = v == null ? "" : v.toString().toLowerCase();
                    lbl.setForeground(val.contains("high")   ? COLOR_DANGER
                                    : val.contains("medium") ? COLOR_WARN
                                    : COLOR_CLOS);
                    lbl.setBackground(s ? SEL_BG : r%2==0 ? BG_CARD : BG_ROW_ALT);
                    lbl.setOpaque(true);
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    lbl.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                    return lbl;
                }
            });
 
        int[] bugColWidths = {40, 140, 160, 65, 80, 80, 75, 80, 80, 90};
        for (int i = 0; i < bugColWidths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(bugColWidths[i]);
 
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(BorderFactory.createEmptyBorder());
 
        JPanel card = buildRoundCard();
        card.setLayout(new BorderLayout());
 
        JLabel heading = new JLabel("  🐛  All Project Bugs");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 13));
        heading.setForeground(TEXT_MAIN);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 8, 8, 8));
 
        JButton refreshBtn = buildSmallButton("↻ Refresh", ACCENT);
        refreshBtn.addActionListener(e -> { bugModel.setRowCount(0); loadBugs(); });
 
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.add(heading, BorderLayout.WEST);
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        btnWrap.setOpaque(false);
        btnWrap.add(refreshBtn);
        topBar.add(btnWrap, BorderLayout.EAST);
 
        card.add(topBar, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  TAB 2 — USERS
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
 
        JPanel split = new JPanel(new GridLayout(1, 2, 12, 0));
        split.setOpaque(false);
        split.add(buildUserTableCard());
        split.add(buildUserFormCard());
        panel.add(split, BorderLayout.CENTER);
 
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        btnPanel.setOpaque(false);
        JButton openUMBtn = buildGlassButton("↗  Open Full User Management");
        openUMBtn.addActionListener(e -> new UserManagementFrame("Admin"));
        btnPanel.add(openUMBtn);
        panel.add(btnPanel, BorderLayout.NORTH);
 
        return panel;
    }
 
    // User table
    private JPanel buildUserTableCard() {
        userModel = new DefaultTableModel(
            new String[]{"#", "Name", "Email", "Role"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
 
        JTable table = buildStyledTable(userModel);
 
        table.getColumnModel().getColumn(3).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean s, boolean f, int r, int c) {
                    JLabel lbl = new JLabel(v == null ? "" : v.toString());
                    String val = v == null ? "" : v.toString().toLowerCase();
                    Color roleColor = val.contains("admin") ? COLOR_DANGER
                                    : val.contains("pm")    ? ACCENT2
                                    : val.contains("dev")   ? ACCENT
                                    : val.contains("test")  ? COLOR_OPEN
                                    : COLOR_CLOS;
                    lbl.setForeground(roleColor);
                    lbl.setBackground(s ? SEL_BG : r%2==0 ? BG_CARD : BG_ROW_ALT);
                    lbl.setOpaque(true);
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    lbl.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                    return lbl;
                }
            });
 
        int[] widths = {30, 130, 180, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
 
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                if (row < userList.size()) populateForm(userList.get(row));
            }
        });
 
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(BorderFactory.createEmptyBorder());
 
        JPanel card = buildRoundCard();
        card.setLayout(new BorderLayout());
 
        JLabel heading = new JLabel("  👥  Users");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 13));
        heading.setForeground(TEXT_MAIN);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 8, 8, 8));
 
        card.add(heading, BorderLayout.NORTH);
        card.add(scroll,  BorderLayout.CENTER);
        return card;
    }
 
    // ── Form fields ───────────────────────────────────────────────────────────
    private JTextField       fldUsername, fldPassword;
    private JComboBox<String> fldRole;
 
    private void populateForm(String[] user) {
        // user = [id, name, email, password, role]
        fldUsername.setText(user.length > 1 ? user[1] : user[0]);
        fldPassword.setText(user.length > 3 ? user[3] : "");
        String role = user.length > 4 ? user[4] : "developer";
        for (int i = 0; i < fldRole.getItemCount(); i++) {
            if (fldRole.getItemAt(i).equalsIgnoreCase(role)) {
                fldRole.setSelectedIndex(i);
                break;
            }
        }
    }
 
    private JPanel buildUserFormCard() {
        JPanel card = buildRoundCard();
        card.setLayout(new BorderLayout());
 
        JLabel heading = new JLabel("  ✏️  Add / Edit User");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 13));
        heading.setForeground(TEXT_MAIN);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 8, 6, 8));
        card.add(heading, BorderLayout.NORTH);
 
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
 
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 4, 6, 4);
 
        gc.gridx=0; gc.gridy=0; gc.weightx=0.35;
        form.add(formLabel("Username"), gc);
        gc.gridx=1; gc.weightx=0.65;
        fldUsername = buildField("Enter username...");
        form.add(fldUsername, gc);
 
        gc.gridx=0; gc.gridy=1; gc.weightx=0.35;
        form.add(formLabel("Password"), gc);
        gc.gridx=1; gc.weightx=0.65;
        fldPassword = buildField("Enter password...");
        form.add(fldPassword, gc);
 
        gc.gridx=0; gc.gridy=2; gc.weightx=0.35;
        form.add(formLabel("Role"), gc);
        gc.gridx=1; gc.weightx=0.65;
        fldRole = new JComboBox<>(new String[]{"admin", "pm", "developer", "tester"});
        styleCombo(fldRole);
        form.add(fldRole, gc);
 
        gc.gridx=0; gc.gridy=3; gc.gridwidth=2;
        gc.weighty=1.0; gc.fill=GridBagConstraints.BOTH;
        form.add(Box.createVerticalGlue(), gc);
        gc.weighty=0; gc.fill=GridBagConstraints.HORIZONTAL;
 
        gc.gridx=0; gc.gridy=4; gc.gridwidth=2;
        JPanel btnRow = new JPanel(new GridLayout(1, 3, 8, 0));
        btnRow.setOpaque(false);
 
        JButton addBtn    = buildSmallButton("＋ Add",    COLOR_CLOS);
        JButton updateBtn = buildSmallButton("✎ Update",  ACCENT);
        JButton deleteBtn = buildSmallButton("✕ Delete",  COLOR_DANGER);
 
        addBtn.addActionListener(e    -> handleAddUser());
        updateBtn.addActionListener(e -> handleUpdateUser());
        deleteBtn.addActionListener(e -> handleDeleteUser());
 
        btnRow.add(addBtn);
        btnRow.add(updateBtn);
        btnRow.add(deleteBtn);
        form.add(btnRow, gc);
 
        gc.gridy=5; gc.insets=new Insets(4, 4, 10, 4);
        JButton clearBtn = buildSmallButton("↺ Clear Form", TEXT_DIM);
        clearBtn.addActionListener(e -> clearForm());
        form.add(clearBtn, gc);
 
        card.add(form, BorderLayout.CENTER);
        return card;
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  USER CRUD — delegated to Admin
    // ══════════════════════════════════════════════════════════════════════════
 
    private void handleAddUser() {
        String user = fldUsername.getText().trim();
        String pass = fldPassword.getText().trim();
        String role = (String) fldRole.getSelectedItem();
 
        if (user.isEmpty() || pass.isEmpty()) {
            showToast("Username and Password are required!", COLOR_WARN); return;
        }
        for (String[] u : userList) {
            if (u[1].equalsIgnoreCase(user)) {
                showToast("Username already exists!", COLOR_WARN); return;
            }
        }
 
        int nextId = userList.stream()
            .mapToInt(u -> { try { return Integer.parseInt(u[0]); }
                             catch (Exception ex) { return 0; } })
            .max().orElse(0) + 1;
 
        String[] newUser = {
            String.valueOf(nextId), user,
            user + "@system.com", pass, role
        };
 
        // ✅ delegation → Admin.addUser()
        boolean ok = admin.addUser(
            newUser[0], newUser[1], newUser[2], newUser[3], newUser[4]);
 
        if (ok) {
            userList.add(newUser);
            refreshUserTable();
            clearForm();
            showToast("User added: " + user, COLOR_CLOS);
        } else {
            showToast("Error adding user!", COLOR_DANGER);
        }
    }
 
    private void handleUpdateUser() {
        String user = fldUsername.getText().trim();
        String pass = fldPassword.getText().trim();
        String role = (String) fldRole.getSelectedItem();
 
        if (user.isEmpty()) { showToast("Select a user to update!", COLOR_WARN); return; }
 
        String[] target = null;
        for (String[] u : userList) {
            if (u[1].equalsIgnoreCase(user)) { target = u; break; }
        }
        if (target == null) { showToast("User not found!", COLOR_WARN); return; }
 
        if (!pass.isEmpty()) target[3] = pass;
        target[4] = role;
 
        // ✅ delegation → Admin.updateUser()
        boolean ok = admin.updateUser(user, target.clone());
 
        if (ok) {
            refreshUserTable();
            showToast("User updated: " + user, ACCENT);
        } else {
            showToast("Error updating user!", COLOR_DANGER);
        }
    }
 
    private void handleDeleteUser() {
        String user = fldUsername.getText().trim();
        if (user.isEmpty()) { showToast("Select a user to delete!", COLOR_WARN); return; }
 
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete user \"" + user + "\"?", "Confirm Delete",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
 
        // ✅ delegation → Admin.deleteUser()
        boolean ok = admin.deleteUser(user);
        if (!ok) { showToast("User not found!", COLOR_WARN); return; }
 
        userList.removeIf(u -> u[1].equalsIgnoreCase(user));
        refreshUserTable();
        clearForm();
        showToast("User deleted: " + user, COLOR_DANGER);
    }
 
    private void clearForm() {
        fldUsername.setText("");
        fldPassword.setText("");
        fldRole.setSelectedIndex(2);
    }
 
    private void refreshUserTable() {
        userModel.setRowCount(0);
        for (int i = 0; i < userList.size(); i++) {
            String[] u = userList.get(i);
            userModel.addRow(new Object[]{
                i + 1,
                u.length > 1 ? u[1] : u[0],
                u.length > 2 ? u[2] : "",
                u.length > 4 ? u[4] : (u.length > 2 ? u[2] : "developer")
            });
        }
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  LOAD / SAVE — delegated to Admin
    // ══════════════════════════════════════════════════════════════════════════
 
    /** Loads bugs via Admin.getAllBugs() and updates the stat cards. */
    private void loadBugs() {
        int open = 0, closed = 0, inProg = 0;
 
        // ✅ delegation → Admin.getAllBugs()
        List<String[]> bugs = admin.getAllBugs();
 
        if (bugs.isEmpty()) {
            showToast("No bugs found (check bug.txt)", COLOR_WARN);
        }
 
        for (String[] d : bugs) {
            bugModel.addRow(new Object[]{
                d[0].trim(), d[1].trim(), d[2].trim(), d[3].trim(),
                d[4].trim(), d[5].trim(), d[6].trim(), d[7].trim(),
                d[8].trim(), d[9].trim()
            });
            switch (d[7].trim().toLowerCase()) {
                case "open"        -> open++;
                case "closed"      -> closed++;
                case "in progress" -> inProg++;
            }
        }
 
        final int t = bugs.size(), o = open, c = closed, p = inProg;
        SwingUtilities.invokeLater(() -> {
            totalBugLbl.setText(String.valueOf(t));
            openLbl    .setText(String.valueOf(o));
            closedLbl  .setText(String.valueOf(c));
            progressLbl.setText(String.valueOf(p));
        });
    }
 
    /** Loads users via Admin.getAllUsers(). */
    private void loadUsers() {
        // ✅ delegation → Admin.getAllUsers()
        userList = admin.getAllUsers();
        refreshUserTable();
    }
 
    /** Saves all users via Admin.saveAllUsers(). */
    private void saveUsers() {
        // ✅ delegation → Admin.saveAllUsers()
        boolean ok = admin.saveAllUsers(userList);
        if (!ok) showToast("Error saving users!", COLOR_DANGER);
    }
 
    // ── Footer ────────────────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
 
        JButton logoutBtn = buildButton("⏻  Logout", COLOR_WARN);
        JButton umBtn     = buildGlassButton("👥  Users Page");
 
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
 
        umBtn.addActionListener(e -> new UserManagementFrame("Admin"));
 
        footer.add(logoutBtn);
        footer.add(umBtn);
 
        JPanel fp = new JPanel(new BorderLayout());
        fp.setOpaque(false);
        fp.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR));
 
        JLabel status = new JLabel(
            "🔴  Bug Tracker Pro  ·  Admin View", SwingConstants.RIGHT);
        status.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        status.setForeground(TEXT_DIM);
        status.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 18));
 
        fp.add(footer, BorderLayout.WEST);
        fp.add(status, BorderLayout.EAST);
        return fp;
    }
 
    // ── Shared Helpers ────────────────────────────────────────────────────────
 
    /** Glassmorphism button — shared across header, tab, and footer. */
    private JButton buildGlassButton(String text) {
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
                    hov += (t - hov) * 0.18f;
                    if (Math.abs(hov - t) < 0.01f) { hov = t; ht.stop(); }
                    repaint();
                });
                ht.start();
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 210, 255, (int)(20 + hov * 40)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                if (hov > 0) {
                    g2.setPaint(new GradientPaint(0, 0,
                        new Color(255,255,255,(int)(hov*15)),
                        getWidth(), getHeight(), new Color(255,255,255,0)));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                }
                g2.setPaint(new GradientPaint(0, 0, ACCENT, getWidth(), 0, ACCENT2));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
                g2.setPaint(new GradientPaint(0, 0, ACCENT, getWidth(), 0, ACCENT2));
                g2.setFont(FONT_BTN);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(230, 32));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
 
    private JTable buildStyledTable(DefaultTableModel mdl) {
        JTable table = new JTable(mdl) {
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
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean s, boolean f, int r, int c) {
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
 
        DefaultTableCellRenderer def = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean s, boolean f, int r, int c) {
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
            table.getColumnModel().getColumn(i).setCellRenderer(def);
 
        return table;
    }
 
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
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
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
        cb.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object v, int i, boolean sel, boolean foc) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                    list, v, i, sel, foc);
                lbl.setBackground(sel ? SEL_BG : new Color(8, 16, 38));
                lbl.setForeground(TEXT_MAIN);
                lbl.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
                return lbl;
            }
        });
    }
 
    private static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int   radius;
        RoundBorder(Color c, int r) { color = c; radius = r; }
        @Override public void paintBorder(Component c, Graphics g,
                int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w-1, h-1, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) {
            return new Insets(2, 2, 2, 2);
        }
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
        p.setPreferredSize(new Dimension(340, 42));
        JLabel l = new JLabel("  " + msg);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(accent);
        l.setBounds(14, 0, 310, 42);
        p.add(l);
        toast.setContentPane(p);
        toast.pack();
        Point loc = getLocation();
        toast.setLocation(loc.x + (getWidth()-340)/2, loc.y + getHeight() - 70);
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
                    new Color(accent.getRed()/2,
                        accent.getGreen()/2,
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
 
    private JButton buildSmallButton(String text, Color accent) {
        JButton b = buildButton(text, accent);
        b.setPreferredSize(new Dimension(110, 30));
        return b;
    }
 
    private void addWindowControls() {
        JPanel glass = new JPanel(null);
        glass.setOpaque(false);
        setGlassPane(glass);
        glass.setVisible(true);
 
        JButton closeBtn = winBtn("✕");
        closeBtn.setBounds(1112, 6, 28, 22);
        closeBtn.addActionListener(e -> { if (bgTimer != null) bgTimer.stop(); dispose(); });
 
        JButton miniBtn = winBtn("─");
        miniBtn.setBounds(1080, 6, 28, 22);
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
        for (int x = 0; x < getWidth(); x  += 32) g2.drawLine(x, 0, x, getHeight());
        for (int y = 0; y < getHeight(); y += 32) g2.drawLine(0, y, getWidth(), y);
        double r = Math.toRadians(orbAngle);
        drawOrb(g2,
            (int)(getWidth()  * 0.06 + Math.cos(r) * 50),
            (int)(getHeight() * 0.12 + Math.sin(r) * 35),
            220, COLOR_DANGER, 28);
        drawOrb(g2,
            (int)(getWidth()  * 0.94 + Math.sin(r) * 40),
            (int)(getHeight() * 0.88 + Math.cos(r) * 30),
            200, ACCENT2, 26);
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
            bgLayer.repaint();
        });
        bgTimer.start();
    }
}