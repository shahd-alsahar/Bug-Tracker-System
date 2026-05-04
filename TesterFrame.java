package bugtrackerprojectsh.GUI;
 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.LinearGradientPaint;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
 

import bugtrackerprojectsh.Tester;
 
public class TesterFrame extends JFrame {
 
    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color BG_DEEP    = new Color(6,  11,  24);
    private static final Color BG_CARD    = new Color(13, 22,  44);
    private static final Color ACCENT     = new Color(0,  210, 255);
    private static final Color ACCENT2    = new Color(100, 60, 255);
    private static final Color SUCCESS    = new Color(0,  210, 140);
    private static final Color WARN       = new Color(255, 180,  0);
    private static final Color EMAIL_CLR  = new Color(255, 120, 60);
    private static final Color EMAIL_CLR2 = new Color(220, 60, 180);
    private static final Color TEXT_MAIN  = new Color(220, 235, 255);
    private static final Color TEXT_DIM   = new Color(90,  120, 165);
    private static final Color BORDER_CLR = new Color(30,  55, 105);
 
    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font FONT_WELCOME = new Font("Segoe UI", Font.BOLD,  28);
    private static final Font FONT_LABEL   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 10);
 
    // ✅ الـ Tester object — مصدر كل البيانات
    private final Tester tester;
 
    private float orbAngle = 0f;
    private Timer bgTimer;
 
    // ─────────────────────────────────────────────────────────────────────────
    public TesterFrame(String name) {
        
        this.tester = new Tester(0, name);
 
        setTitle("Bug Tracker Pro — Tester Dashboard");
        setSize(700, 570);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setBackground(BG_DEEP);
 
        JPanel root = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBackground(g);
            }
        };
        root.setBackground(BG_DEEP);
        setContentPane(root);
 
        buildTopBar(root);
        buildWelcomeCard(root);
        buildActionCards(root);
        buildStatusBar(root);
 
        JButton closeBtn = windowBtn("✕", new Color(255, 70, 100));
        closeBtn.setBounds(660, 10, 28, 22);
        closeBtn.addActionListener(e -> System.exit(0));
        root.add(closeBtn);
 
        JButton miniBtn = windowBtn("─", TEXT_DIM);
        miniBtn.setBounds(628, 10, 28, 22);
        miniBtn.addActionListener(e -> setState(Frame.ICONIFIED));
        root.add(miniBtn);
 
        makeDraggable(root);
        setVisible(true);
        startBgAnim(root);
    }
 
    // ── Top Bar ───────────────────────────────────────────────────────────────
    private void buildTopBar(JPanel root) {
        JPanel bar = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(13, 22, 44, 200));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BORDER_CLR);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setBounds(0, 0, 700, 50);
        root.add(bar);
 
        JLabel icon = new JLabel("🐛");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        icon.setBounds(18, 14, 26, 22);
        bar.add(icon);
 
        JLabel appTitle = new JLabel("BugTracker Pro");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        appTitle.setForeground(TEXT_MAIN);
        appTitle.setBounds(46, 15, 140, 20);
        bar.add(appTitle);
 
        JLabel roleBadge = pillLabel("TESTER", ACCENT);
        roleBadge.setBounds(190, 16, 60, 18);
        bar.add(roleBadge);
 
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, MMM dd yyyy"));
        JLabel dateLabel = new JLabel(today);
        dateLabel.setFont(FONT_SMALL);
        dateLabel.setForeground(TEXT_DIM);
        dateLabel.setBounds(340, 17, 160, 16);
        bar.add(dateLabel);
 
        JButton logout = new JButton("⏻  Log Out");
        logout.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        logout.setForeground(TEXT_DIM);
        logout.setContentAreaFilled(false);
        logout.setBorderPainted(false);
        logout.setFocusPainted(false);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.setBounds(508, 13, 100, 24);
        logout.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { logout.setForeground(new Color(255,70,100)); }
            public void mouseExited (MouseEvent e) { logout.setForeground(TEXT_DIM); }
        });
        logout.addActionListener(e -> { new LoginFrame(); dispose(); });
        bar.add(logout);
    }
 
    // ── Welcome Card ──────────────────────────────────────────────────────────
    private void buildWelcomeCard(JPanel root) {
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(13, 22, 44, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                LinearGradientPaint lp = new LinearGradientPaint(0, 0, 0, getHeight(),
                    new float[]{0f, 1f}, new Color[]{ACCENT, ACCENT2});
                g2.setPaint(lp);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.setColor(BORDER_CLR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBounds(30, 68, 640, 100);
        root.add(card);
 
        JLabel hi = new JLabel("👋  Welcome back,");
        hi.setFont(FONT_LABEL);
        hi.setForeground(TEXT_DIM);
        hi.setBounds(20, 18, 300, 18);
        card.add(hi);
 
        
        JLabel nameLabel = new JLabel(tester.getTesterName()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                LinearGradientPaint gp = new LinearGradientPaint(0, 0, getWidth(), 0,
                    new float[]{0f, 1f}, new Color[]{ACCENT, ACCENT2});
                g2.setPaint(gp);
                g2.setFont(getFont());
                g2.drawString(getText(), 0, g2.getFontMetrics().getAscent());
                g2.dispose();
            }
        };
        nameLabel.setFont(FONT_WELCOME);
        nameLabel.setForeground(ACCENT);
        nameLabel.setBounds(20, 38, 460, 40);
        card.add(nameLabel);
 
        JLabel sub = new JLabel("Ready to track some bugs today?");
        sub.setFont(FONT_SMALL);
        sub.setForeground(TEXT_DIM);
        sub.setBounds(22, 76, 300, 14);
        card.add(sub);
 
        
        int openCount   = tester.countMyOpenBugs();
        int closedCount = tester.countMyClosedBugs();
 
        JLabel statA = statPill("My Open",   String.valueOf(openCount),   WARN);
        statA.setBounds(460, 15, 80, 56);
        card.add(statA);
 
        JLabel statB = statPill("My Closed", String.valueOf(closedCount), SUCCESS);
        statB.setBounds(554, 15, 80, 56);
        card.add(statB);
    }
 
    // ── Action Cards ──────────────────────────────────────────────────────────
    private void buildActionCards(JPanel root) {
        // ✅ الاسم من الـ Tester object
        String name = tester.getTesterName();
 
        // Card 1: Add Bug
        JPanel addCard = actionCard("➕", "Add a Bug",
            "Log a new bug with full details,\npriority, and assignment.",
            ACCENT, ACCENT2);
        addCard.setBounds(30, 190, 200, 240);
        root.add(addCard);
        addCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { new AddBugFrame(name); }
        });
 
        // Card 2: View Bug Reports
        JPanel viewCard = actionCard("📋", "View Bug Reports",
            "Browse, filter, and manage all\nexisting bug reports.",
            SUCCESS, new Color(0, 150, 100));
        viewCard.setBounds(250, 190, 200, 240);
        root.add(viewCard);
        viewCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { new BugsTableFrame(name); dispose(); }
        });
 
        // Card 3: Email Inbox — ✅ العدد من tester.countInboxEmails()
        int emailCount = tester.countInboxEmails();
        JPanel emailCard = actionCard("📧", "Email Inbox",
            "View messages sent\nto you by the team.",
            EMAIL_CLR, EMAIL_CLR2);
        emailCard.setBounds(470, 190, 200, 240);
        root.add(emailCard);
        emailCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { new EmailInboxFrameTester(name); }
        });
 
        // Badge عدد الإيميلات
        if (emailCount > 0) {
            JLabel badge = new JLabel(String.valueOf(emailCount), SwingConstants.CENTER) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 70, 100));
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
            badge.setForeground(Color.WHITE);
            badge.setOpaque(false);
            badge.setBounds(648, 190, 22, 22);
            root.add(badge);
            root.setComponentZOrder(badge, 0);
        }
    }
 
    // ── Status Bar ────────────────────────────────────────────────────────────
    private void buildStatusBar(JPanel root) {
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
        bar.setBounds(0, 528, 700, 42);
        root.add(bar);
 
        JLabel left = new JLabel("🟢  System Online  ·  Bug Tracker v2.0");
        left.setFont(FONT_SMALL);
        left.setForeground(TEXT_DIM);
        left.setBounds(18, 13, 300, 16);
        bar.add(left);
 
        JLabel right = new JLabel("Tester Portal  ·  " +
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")));
        right.setFont(FONT_SMALL);
        right.setForeground(TEXT_DIM);
        right.setBounds(500, 13, 180, 16);
        bar.add(right);
    }
 
    // ── Background ────────────────────────────────────────────────────────────
    private void drawBackground(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BG_DEEP);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(new Color(25, 45, 90, 12));
        for (int x = 0; x < getWidth(); x += 32) g2.drawLine(x, 0, x, getHeight());
        for (int y = 0; y < getHeight(); y += 32) g2.drawLine(0, y, getWidth(), y);
        double r = Math.toRadians(orbAngle);
        drawOrb(g2, (int)(getWidth()*0.12 + Math.cos(r)*50), (int)(getHeight()*0.25 + Math.sin(r)*30), 200, ACCENT, 28);
        drawOrb(g2, (int)(getWidth()*0.88 + Math.sin(r)*40), (int)(getHeight()*0.75 + Math.cos(r)*25), 180, ACCENT2, 25);
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
 
    private void startBgAnim(JPanel root) {
        bgTimer = new Timer(30, e -> { orbAngle = (orbAngle + 0.6f) % 360; root.repaint(); });
        bgTimer.start();
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
        l.setFont(new Font("Segoe UI", Font.BOLD, 9));
        l.setForeground(color);
        l.setOpaque(false);
        return l;
    }
 
    private JLabel statPill(String label, String value, Color color) {
        JLabel l = new JLabel("", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.setColor(color);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(value, (getWidth()-fm.stringWidth(value))/2, 24);
                g2.setFont(FONT_SMALL);
                g2.setColor(TEXT_DIM);
                fm = g2.getFontMetrics();
                g2.drawString(label, (getWidth()-fm.stringWidth(label))/2, 42);
                g2.dispose();
            }
        };
        l.setOpaque(false);
        return l;
    }
 
    private JPanel actionCard(String emoji, String title, String desc, Color c1, Color c2) {
        JPanel card = new JPanel(null) {
            private float hover = 0f;
            private Timer ht;
            {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { animHover(1f); }
                    public void mouseExited (MouseEvent e) { animHover(0f); }
                });
            }
            private void animHover(float t) {
                if (ht != null) ht.stop();
                ht = new Timer(16, ev -> {
                    hover += (t - hover) * 0.18f;
                    if (Math.abs(hover - t) < 0.01f) { hover = t; ht.stop(); }
                    repaint();
                });
                ht.start();
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = new Color(
                    (int)(BG_CARD.getRed()   + (c1.getRed()   - BG_CARD.getRed())   * hover * 0.12f),
                    (int)(BG_CARD.getGreen() + (c1.getGreen() - BG_CARD.getGreen()) * hover * 0.12f),
                    (int)(BG_CARD.getBlue()  + (c1.getBlue()  - BG_CARD.getBlue())  * hover * 0.12f)
                );
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                LinearGradientPaint lp = new LinearGradientPaint(0, 0, getWidth(), 0,
                    new float[]{0f, 1f}, new Color[]{c1, c2});
                g2.setPaint(lp);
                g2.fillRoundRect(0, 0, getWidth(), 5, 5, 5);
                if (hover > 0.01f) {
                    g2.setColor(new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), (int)(hover * 30)));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 18, 18);
                }
                g2.setColor(new Color(BORDER_CLR.getRed(), BORDER_CLR.getGreen(),
                        BORDER_CLR.getBlue(), (int)(80 + hover * 100)));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
 
        JLabel ico = new JLabel(emoji, SwingConstants.CENTER);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 38));
        ico.setBounds(0, 30, 200, 50);
        card.add(ico);
 
        JLabel ttl = new JLabel(title, SwingConstants.CENTER);
        ttl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        ttl.setForeground(TEXT_MAIN);
        ttl.setBounds(0, 88, 200, 22);
        card.add(ttl);
 
        String[] lines = desc.split("\n");
        for (int i = 0; i < lines.length; i++) {
            JLabel dl = new JLabel(lines[i], SwingConstants.CENTER);
            dl.setFont(FONT_SMALL);
            dl.setForeground(TEXT_DIM);
            dl.setBounds(10, 116 + i * 16, 180, 15);
            card.add(dl);
        }
 
        JLabel cta = new JLabel("Click to open  →", SwingConstants.CENTER);
        cta.setFont(new Font("Segoe UI", Font.BOLD, 11));
        cta.setForeground(c1);
        cta.setBounds(0, 196, 200, 20);
        card.add(cta);
 
        return card;
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
            public void mouseExited (MouseEvent e) { b.setForeground(TEXT_DIM);   }
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