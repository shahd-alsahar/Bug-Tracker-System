package bugtrackerprojectsh.GUI;
 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import bugtrackerprojectsh.GUI.DeveloperFrame;
import bugtrackerprojectsh.login;
 
 
public class LoginFrame extends JFrame {
 
    // ── Palette ──────────────────────────────────────────────────────────────
    private static final Color BG_DEEP    = new Color(6,  11,  24);
    private static final Color BG_CARD    = new Color(13, 22,  44);
    private static final Color BG_FIELD   = new Color(20, 32,  62);
    private static final Color ACCENT     = new Color(0,  210, 255);
    private static final Color ACCENT2    = new Color(100, 60, 255);
    private static final Color TEXT_MAIN  = new Color(220, 235, 255);
    private static final Color TEXT_DIM   = new Color(90,  120, 165);
    private static final Color BORDER_CLR = new Color(30,  55, 105);
    private static final Color DANGER     = new Color(255, 70, 100);
 
    // ── Fonts ────────────────────────────────────────────────────────────────
    private static final Font FONT_LOGO   = new Font("Segoe UI", Font.BOLD,  32);
    private static final Font FONT_SUB    = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD,  10);
    private static final Font FONT_FIELD  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD,  14);
    private static final Font FONT_LINK   = new Font("Segoe UI", Font.PLAIN, 11);
 
    // ── Components ───────────────────────────────────────────────────────────
    private JTextField     emailField;
    private JPasswordField passField;
    private JButton        loginBtn;
    private JLabel         errorLabel;
 
    // ── Animation ────────────────────────────────────────────────────────────
    private float cardAlpha  = 0f;
    private float cardSlide  = 40f;
    private float orb1Angle  = 0f;
    private Timer bgTimer;
 
    // ─────────────────────────────────────────────────────────────────────────
    public LoginFrame() {
        setTitle("Bug Tracker  — Sign In");
        setSize(440, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setBackground(BG_DEEP);
 
        JPanel root = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 
                // خلفية
                g2.setColor(BG_DEEP);
                g2.fillRect(0, 0, getWidth(), getHeight());
 
                // grid خفيف
                g2.setColor(new Color(25, 45, 90, 15));
                for (int x = 0; x < getWidth(); x += 32) g2.drawLine(x, 0, x, getHeight());
                for (int y = 0; y < getHeight(); y += 32) g2.drawLine(0, y, getWidth(), y);
 
                // orb 1
                double r1 = Math.toRadians(orb1Angle);
                int ox1 = (int)(getWidth()*0.15 + Math.cos(r1)*40);
                int oy1 = (int)(getHeight()*0.2  + Math.sin(r1)*30);
                drawOrb(g2, ox1, oy1, 180, ACCENT, 35);
 
                // orb 2
                int ox2 = (int)(getWidth()*0.82 + Math.sin(r1)*35);
                int oy2 = (int)(getHeight()*0.78 + Math.cos(r1)*25);
                drawOrb(g2, ox2, oy2, 160, ACCENT2, 30);
 
                // رسم الكارد
                int cx = (440 - 360) / 2;
                int cy = (560 - 460) / 2 + (int) cardSlide;
 
                AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1f, cardAlpha));
                g2.setComposite(ac);
 
                // glass fill
                g2.setColor(new Color(13, 22, 44, 235));
                g2.fillRoundRect(cx, cy, 360, 460, 24, 24);
 
                // top accent line
                GradientPaint line = new GradientPaint(cx, cy, ACCENT, cx + 360, cy, ACCENT2);
                g2.setPaint(line);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(cx + 40, cy, cx + 320, cy);
 
                // border
                g2.setPaint(null);
                g2.setColor(new Color(40, 70, 130, 120));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(cx, cy, 359, 459, 24, 24);
 
                g2.dispose();
            }
        };
        root.setBackground(BG_DEEP);
        setContentPane(root);
 
        buildUI(root);
        makeDraggable(root);
 
        // زرار الإغلاق
        JButton close = windowBtn("✕");
        close.setBounds(400, 8, 32, 24);
        close.addActionListener(e -> System.exit(0));
        root.add(close);
 
        // زرار التصغير
        JButton mini = windowBtn("─");
        mini.setBounds(364, 8, 32, 24);
        mini.addActionListener(e -> setState(Frame.ICONIFIED));
        root.add(mini);
 
        setVisible(true);
        startAnimations(root);
    }
 
    // ── بناء محتوى الـ UI ─────────────────────────────────────────────────
    private void buildUI(JPanel root) {
        int CX = 40;
        int CY = 50;
        int W  = 360;
 
        // ── أيقونة الBUG ──
        JLabel bugIcon = new JLabel("🐛", SwingConstants.CENTER);
        bugIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        bugIcon.setBounds(CX, CY + 30, W, 42);
        root.add(bugIcon);
 
        // ── العنوان ──
        JLabel title = new JLabel("BugTracker", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ACCENT, getWidth(), 0, ACCENT2);
                g2.setPaint(gp);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                g2.drawString(getText(), x, fm.getAscent());
                g2.dispose();
            }
        };
        title.setFont(FONT_LOGO);
        title.setForeground(ACCENT);
        title.setBounds(CX, CY + 78, W, 40);
        root.add(title);
 
        // ── الـ subtitle ──
        JLabel sub = new JLabel("Professional Bug Management System", SwingConstants.CENTER);
        sub.setFont(FONT_SUB);
        sub.setForeground(TEXT_DIM);
        sub.setBounds(CX, CY + 122, W, 18);
        root.add(sub);
 
        // ── فاصل ──
        JSeparator sep = new JSeparator() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                LinearGradientPaint gp = new LinearGradientPaint(
                    0, 0, getWidth(), 0,
                    new float[]{0f, 0.5f, 1f},
                    new Color[]{new Color(0,0,0,0), BORDER_CLR, new Color(0,0,0,0)}
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
            }
        };
        sep.setBounds(CX + 30, CY + 152, W - 60, 2);
        root.add(sep);
 
        // ── Email ──
        root.add(fieldLabel("EMAIL ADDRESS", CX, CY + 172, W));
        emailField = buildTextField("your@email.com");
        emailField.setBounds(CX + 30, CY + 190, W - 60, 44);
        root.add(emailField);
 
        // ── Password ──
        root.add(fieldLabel("PASSWORD", CX, CY + 250, W));
        passField = buildPasswordField("Enter password");
        passField.setBounds(CX + 30, CY + 268, W - 60, 44);
        root.add(passField);
 
        // ── زرار العين (Graphics2D) — لازم يتضاف بعد passField عشان يطلع فوقيه ──
        final boolean[] passVisible = {false};
 
        JButton eyeBtn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 
                // مفيش خلفية — شفاف تماماً عشان متغطيش الحقل
                int ecx = getWidth() / 2;
                int ecy = getHeight() / 2;
 
                // لون العين
                Color eyeColor = passVisible[0] ? ACCENT : TEXT_DIM;
                g2.setColor(eyeColor);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
 
                // شكل العين (قوسين)
                g2.drawArc(ecx - 8, ecy - 5, 16, 10,   0, 180);
                g2.drawArc(ecx - 8, ecy - 5, 16, 10, 180, 180);
 
                // البؤبؤ
                g2.fillOval(ecx - 3, ecy - 3, 6, 6);
 
                // خط مائل لو الباسورد مخفي
                if (!passVisible[0]) {
                    g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(TEXT_DIM);
                    g2.drawLine(ecx - 8, ecy + 6, ecx + 8, ecy - 6);
                }
 
                g2.dispose();
            }
        };
        eyeBtn.setOpaque(false);
        eyeBtn.setContentAreaFilled(false);
        eyeBtn.setBorderPainted(false);
        eyeBtn.setFocusPainted(false);
        eyeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // نحطه داخل حدود الـ passField من الجهة اليمين
        eyeBtn.setBounds(CX + W - 70, CY + 268, 40, 44);
        eyeBtn.addActionListener(e -> {
            passVisible[0] = !passVisible[0];
            passField.setEchoChar(passVisible[0] ? (char) 0 : '●');
            eyeBtn.repaint();
        });
        root.add(eyeBtn);
        // رفعه فوق الـ passField في الـ Z-order
        root.setComponentZOrder(eyeBtn, 0);
 
        // ── رسالة الخطأ ──
        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        errorLabel.setForeground(DANGER);
        errorLabel.setBounds(CX + 30, CY + 320, W - 60, 18);
        root.add(errorLabel);
 
        // ── زرار Sign In ──
        loginBtn = buildLoginButton();
        loginBtn.setBounds(CX + 30, CY + 346, W - 60, 48);
        loginBtn.addActionListener(e -> doLogin());
        root.add(loginBtn);
 
        // ── Footer ──
        JLabel footer = new JLabel("Secure Sign In  ·  Bug Tracker v2.0", SwingConstants.CENTER);
        footer.setFont(FONT_LINK);
        footer.setForeground(TEXT_DIM);
        footer.setBounds(CX, CY + 406, W, 20);
        root.add(footer);
    }
 
    // ── Login Logic ──────────────────────────────────────────────────────────
    private void doLogin() {
        String emailText = emailField.getText().trim();
        String passText  = new String(passField.getPassword());
 
        if (emailText.isEmpty() || emailText.equals("your@email.com") ||
            passText.isEmpty()  || passText.equals("Enter password")) {
            showError("Please enter your email and password");
            shake(emailText.isEmpty() ? emailField : passField);
            return;
        }
 
        loginBtn.setEnabled(false);
        loginBtn.putClientProperty("loading", true);
        loginBtn.repaint();
 
        SwingWorker<String[], Void> worker = new SwingWorker<>() {
            @Override protected String[] doInBackground() {
                return login.login(emailText, passText);
            }
            @Override protected void done() {
                try {
                    String[] user = get();
                    loginBtn.setEnabled(true);
                    loginBtn.putClientProperty("loading", false);
                    loginBtn.repaint();
 
                    if (user != null) {
                        errorLabel.setForeground(new Color(0, 230, 140));
                        errorLabel.setText("Welcome back, " + user[0] + " 👋");
                        
                        Timer t = new Timer(900, ev -> {
                            String name = user[0];
                            String role = user[1];
 
                            if (role.equalsIgnoreCase("tester")) {
                                new TesterFrame(name);
                            } else if (role.equalsIgnoreCase("developer")) {
                                new DeveloperFrame(name);
                            } else if (role.equalsIgnoreCase("pm") || role.equalsIgnoreCase("project manager")) {
                                new PmFrame();
                            } else if (role.equalsIgnoreCase("admin")) {
                                new AdminFrame(name);
                            }
                            dispose();
                        });
                        t.setRepeats(false);
                        t.start();
                    } else {
                        showError("Invalid email or password ❌");
                        shake(passField);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }
 
    private void showError(String msg) {
        errorLabel.setForeground(DANGER);
        errorLabel.setText(msg);
        Timer t = new Timer(3000, e -> errorLabel.setText(""));
        t.setRepeats(false); t.start();
    }
 
    // ── Builders ─────────────────────────────────────────────────────────────
    private JLabel fieldLabel(String text, int x, int y, int W) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_DIM);
        l.setBounds(x + 32, y, W, 16);
        return l;
    }
 
    private JTextField buildTextField(String placeholder) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_FIELD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (isFocusOwner()) {
                    g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 80));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 12, 12);
                } else {
                    g2.setColor(BORDER_CLR);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setOpaque(false);
        f.setFont(FONT_FIELD);
        f.setForeground(TEXT_DIM);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        f.setText(placeholder);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) { f.setText(""); f.setForeground(TEXT_MAIN); }
                f.repaint();
            }
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) { f.setText(placeholder); f.setForeground(TEXT_DIM); }
                f.repaint();
            }
        });
        return f;
    }
 
    private JPasswordField buildPasswordField(String placeholder) {
        JPasswordField f = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_FIELD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (isFocusOwner()) {
                    g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 80));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 12, 12);
                } else {
                    g2.setColor(BORDER_CLR);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setOpaque(false);
        f.setFont(FONT_FIELD);
        f.setForeground(TEXT_DIM);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 44));
        f.setText(placeholder);
        f.setEchoChar((char) 0);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(f.getPassword()).equals(placeholder)) {
                    f.setText(""); f.setEchoChar('●'); f.setForeground(TEXT_MAIN);
                }
                f.repaint();
            }
            public void focusLost(FocusEvent e) {
                if (f.getPassword().length == 0) {
                    f.setText(placeholder); f.setEchoChar((char) 0); f.setForeground(TEXT_DIM);
                }
                f.repaint();
            }
        });
        return f;
    }
 
    private JButton buildLoginButton() {
        JButton b = new JButton("Sign In") {
            private float hoverAlpha = 0f;
            private Timer hoverTimer;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { animHover(1f); }
                    public void mouseExited (MouseEvent e) { animHover(0f); }
                });
            }
            private void animHover(float t) {
                if (hoverTimer != null) hoverTimer.stop();
                hoverTimer = new Timer(16, ev -> {
                    hoverAlpha += (t - hoverAlpha) * 0.2f;
                    if (Math.abs(hoverAlpha - t) < 0.01f) { hoverAlpha = t; hoverTimer.stop(); }
                    repaint();
                });
                hoverTimer.start();
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean loading = Boolean.TRUE.equals(getClientProperty("loading"));
                GradientPaint gp = new GradientPaint(0, 0, ACCENT, getWidth(), getHeight(), ACCENT2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                if (hoverAlpha > 0) {
                    g2.setColor(new Color(255, 255, 255, (int)(hoverAlpha * 35)));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                }
                if (loading) {
                    long t = System.currentTimeMillis();
                    for (int i = 0; i < 3; i++) {
                        float phase = ((t / 300f + i * 0.33f) % 1f);
                        int alpha = (int)(50 + 205 * (float)Math.sin(phase * Math.PI));
                        g2.setColor(new Color(255, 255, 255, alpha));
                        int x = getWidth()/2 - 18 + i*18;
                        int y = getHeight()/2 - 4;
                        g2.fillOval(x, y, 8, 8);
                    }
                    repaint();
                } else {
                    g2.setColor(Color.WHITE);
                    g2.setFont(FONT_BTN);
                    FontMetrics fm = g2.getFontMetrics();
                    String txt = getText();
                    g2.drawString(txt,
                            (getWidth() - fm.stringWidth(txt)) / 2,
                            (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                }
                g2.dispose();
            }
        };
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
 
    // ── Orb helper ───────────────────────────────────────────────────────────
    private void drawOrb(Graphics2D g2, int cx, int cy, int r, Color c, int maxAlpha) {
        for (int i = r; i > 0; i -= 12) {
            int alpha = (int)(maxAlpha * (1 - (float)i / r));
            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
            g2.fillOval(cx - i, cy - i, i*2, i*2);
        }
    }
 
    // ── Animations ───────────────────────────────────────────────────────────
    private void startAnimations(JPanel root) {
        Timer enterTimer = new Timer(16, null);
        enterTimer.addActionListener(e -> {
            cardAlpha += (1f - cardAlpha) * 0.08f;
            cardSlide += (0f - cardSlide) * 0.10f;
            root.repaint();
            if (cardAlpha > 0.995f && Math.abs(cardSlide) < 0.5f) {
                cardAlpha = 1f; cardSlide = 0f;
                enterTimer.stop();
            }
        });
        enterTimer.start();
 
        bgTimer = new Timer(30, e -> {
            orb1Angle = (orb1Angle + 0.8f) % 360;
            root.repaint();
        });
        bgTimer.start();
    }
 
    // ── Utils ────────────────────────────────────────────────────────────────
    private JButton windowBtn(String text) {
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
 
    private void shake(JComponent comp) {
        Point orig = comp.getLocation();
        int[] offsets = {-7, 7, -5, 5, -3, 3, -1, 1, 0};
        Timer t = new Timer(28, null);
        int[] idx = {0};
        t.addActionListener(e -> {
            if (idx[0] >= offsets.length) { t.stop(); comp.setLocation(orig); return; }
            comp.setLocation(orig.x + offsets[idx[0]++], orig.y);
        });
        t.start();
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
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}