package bugtrackerprojectsh.GUI;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import bugtrackerprojectsh.UserService;
import bugtrackerprojectsh.EmailService;

public class AddBugFrame extends JFrame {

    // ── Fields ───────────────────────────────────────────────────────────────
    private JTextField nameField, typeField, projectField, testerIdField, screenshotField;
    private JComboBox<String> priorityBox, levelBox, statusBox, devBox;
    private JLabel imagePreviewLabel;
    private File   selectedImageFile;
    private final String testerName;
    private String currentUser;

    // ── Palette ──────────────────────────────────────────────────────────────
    private static final Color BG_DEEP    = new Color(8,  14,  28);
    private static final Color BG_PANEL   = new Color(13, 22,  44);
    private static final Color BG_CARD    = new Color(18, 30,  58);
    private static final Color BG_FIELD   = new Color(22, 36,  68);
    private static final Color ACCENT     = new Color(0,  210, 255);
    private static final Color ACCENT2    = new Color(120, 80, 255);
    private static final Color SUCCESS    = new Color(0,  230, 140);
    private static final Color DANGER     = new Color(255, 70, 100);
    private static final Color TEXT_MAIN  = new Color(220, 235, 255);
    private static final Color TEXT_DIM   = new Color(100, 130, 170);
    private static final Color BORDER_CLR = new Color(35,  60, 110);

    // ── Fonts ────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD,  11);
    private static final Font FONT_FIELD  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 10);

    // ────────────────────────────────────────────────────────────────────────
    public AddBugFrame(String testerName) {
        this.testerName  = testerName;
        this.currentUser = testerName;

        setTitle("Bug Tracker  ·  New Bug Report");
        setSize(560, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setBackground(BG_DEEP);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 55, 100, 18));
                for (int x = 0; x < getWidth();  x += 28) g2.drawLine(x, 0, x, getHeight());
                for (int y = 0; y < getHeight(); y += 28) g2.drawLine(0, y, getWidth(), y);
                GradientPaint gp = new GradientPaint(0, 0,
                    new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 40),
                    0, 160, new Color(0, 0, 0, 0));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), 160);
                g2.setColor(BORDER_CLR);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 18, 18);
                g2.dispose();
            }
        };
        root.setBackground(BG_DEEP);

        root.add(buildTitleBar(), BorderLayout.NORTH);
        root.add(buildForm(),     BorderLayout.CENTER);
        root.add(buildFooter(),   BorderLayout.SOUTH);

        setContentPane(root);
        makeDraggable(root);
        setVisible(true);
    }

    // ── Title Bar ─────────────────────────────────────────────────────────────
    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(),
                    ACCENT.getBlue(), 18));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 18, 18, 18);
                g2.setColor(BORDER_CLR);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 54));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 12));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        JLabel icon  = new JLabel("🐛");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        JLabel title = new JLabel("New Bug Report");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_MAIN);
        JLabel badge = pillLabel("REPORT", ACCENT);
        left.add(icon); left.add(title); left.add(badge);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);
        JLabel dateLabel = new JLabel(LocalDate.now().format(
            DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        dateLabel.setFont(FONT_SMALL);
        dateLabel.setForeground(TEXT_DIM);
        JButton closeBtn = iconButton("✕", DANGER);
        closeBtn.addActionListener(e -> dispose());
        right.add(dateLabel); right.add(closeBtn);

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Form ──────────────────────────────────────────────────────────────────
    private JScrollPane buildForm() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(18, 24, 10, 24));

        // ── Section: Bug Info ──
        form.add(sectionHeader("Bug Details", "🔍"));
        form.add(Box.createVerticalStrut(8));

        // ── Row 1: Bug Name + Bug Type (no digits allowed) ──
        nameField = styledField("e.g. Login crash on timeout");
        typeField = styledField("e.g. Functional");
        blockDigits(nameField);
        blockDigits(typeField);

        JPanel row1 = twoCol(
            fieldGroup("Bug Name *", nameField),
            fieldGroup("Bug Type *", typeField)
        );
        form.add(row1);
        form.add(Box.createVerticalStrut(10));

        JPanel row2 = twoCol(
            fieldGroup("Priority",       priorityBox = styledCombo("Low","Medium","High")),
            fieldGroup("Severity Level", levelBox    = styledCombo("Minor","Major","Critical"))
        );
        form.add(row2);
        form.add(Box.createVerticalStrut(10));

        // ── Row 3: Project Name (no digits allowed) + Status ──
        projectField = styledField("e.g. Alpha Release");
        blockDigits(projectField);

        JPanel row3 = twoCol(
            fieldGroup("Project Name", projectField),
            fieldGroup("Status",       statusBox = styledCombo("Open","In Progress","Closed"))
        );
        form.add(row3);
        form.add(Box.createVerticalStrut(20));

        // ── Section: Assignment ──
        form.add(sectionHeader("Assignment", "👤"));
        form.add(Box.createVerticalStrut(8));

        devBox = styledCombo();
        for (String dev : UserService.getDevelopers()) devBox.addItem(dev);

        testerIdField = styledField(testerName);
        testerIdField.setText(testerName);
        testerIdField.setEditable(false);

        JPanel row4 = twoCol(
            fieldGroup("Tester Name", testerIdField),
            fieldGroup("Assign Dev",  devBox)
        );
        form.add(row4);
        form.add(Box.createVerticalStrut(20));

        // ── Section: Evidence ──
        form.add(sectionHeader("Evidence", "📎"));
        form.add(Box.createVerticalStrut(8));

        screenshotField = styledField("Click Browse to select an image...");
        screenshotField.setEditable(false);
        screenshotField.setBackground(new Color(16, 26, 52));

        JButton browseBtn = buildBrowseButton();

        JPanel screenshotRow = new JPanel(new BorderLayout(8, 0));
        screenshotRow.setOpaque(false);
        screenshotRow.add(screenshotField, BorderLayout.CENTER);
        screenshotRow.add(browseBtn,       BorderLayout.EAST);

        form.add(fieldGroup("Screenshot", screenshotRow));
        form.add(Box.createVerticalStrut(10));

        imagePreviewLabel = new JLabel("No image selected", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_FIELD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDER_CLR);
                g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_ROUND, 0, new float[]{6, 4}, 0));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        imagePreviewLabel.setFont(FONT_SMALL);
        imagePreviewLabel.setForeground(TEXT_DIM);
        imagePreviewLabel.setPreferredSize(new Dimension(0, 140));
        imagePreviewLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        imagePreviewLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imagePreviewLabel.setOpaque(false);

        form.add(imagePreviewLabel);
        form.add(Box.createVerticalStrut(6));

        JScrollPane scroll = new JScrollPane(form);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
        return scroll;
    }

    // ── Digit Blocker ─────────────────────────────────────────────────────────
    private void blockDigits(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset,
                    String string, AttributeSet attr) throws BadLocationException {
                if (string != null && string.matches(".*\\d.*")) return;
                super.insertString(fb, offset, string, attr);
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length,
                    String string, AttributeSet attr) throws BadLocationException {
                if (string != null && string.matches(".*\\d.*")) return;
                super.replace(fb, offset, length, string, attr);
            }
        });
    }

    // ── Browse Button ─────────────────────────────────────────────────────────
    private JButton buildBrowseButton() {
        JButton btn = new JButton("Browse") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0,
                    new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 180),
                    getWidth(), getHeight(),
                    new Color(ACCENT2.getRed(), ACCENT2.getGreen(), ACCENT2.getBlue(), 180)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(FONT_BTN);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(90, 40));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> openImageChooser());
        return btn;
    }

    // ── File Chooser Logic ────────────────────────────────────────────────────
    private void openImageChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose a screenshot image");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image Files (*.png, *.jpg, *.jpeg, *.gif, *.bmp)",
            "png", "jpg", "jpeg", "gif", "bmp"
        ));
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = chooser.getSelectedFile();
            screenshotField.setText(selectedImageFile.getAbsolutePath());
            loadImagePreview(selectedImageFile);
        }
    }

    // ── Image Preview ─────────────────────────────────────────────────────────
    private void loadImagePreview(File imgFile) {
        try {
            BufferedImage img = ImageIO.read(imgFile);
            if (img == null) {
                imagePreviewLabel.setIcon(null);
                imagePreviewLabel.setText("❌ Can't read this file");
                return;
            }
            int maxW = 480, maxH = 130;
            double scale = Math.min((double) maxW / img.getWidth(),
                                    (double) maxH / img.getHeight());
            Image scaled = img.getScaledInstance(
                (int)(img.getWidth()  * scale),
                (int)(img.getHeight() * scale),
                Image.SCALE_SMOOTH);
            imagePreviewLabel.setIcon(new ImageIcon(scaled));
            imagePreviewLabel.setText("");
        } catch (IOException ex) {
            imagePreviewLabel.setIcon(null);
            imagePreviewLabel.setText("❌ Error loading image");
        }
    }

    // ── Footer ────────────────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(BORDER_CLR);
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
            }
        };
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(14, 24, 20, 24));

        JLabel hint = new JLabel("* Required fields");
        hint.setFont(FONT_SMALL);
        hint.setForeground(TEXT_DIM);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton cancelBtn = actionButton("Cancel", BG_FIELD, TEXT_DIM);
        JButton saveBtn   = glowButton("Save Bug  →", ACCENT, BG_DEEP);

        cancelBtn.addActionListener(e -> dispose());
        saveBtn.addActionListener(e -> saveBug());

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);

        footer.add(hint,     BorderLayout.WEST);
        footer.add(btnPanel, BorderLayout.EAST);
        return footer;
    }

    // ── Save Logic ────────────────────────────────────────────────────────────
    private void saveBug() {
        String name = nameField.getText().trim();
        String type = typeField.getText().trim();

        if (name.isEmpty() || type.isEmpty()) {
            showToast("Please fill in all required fields ❌", DANGER);
            shake(name.isEmpty() ? nameField : typeField);
            return;
        }

        int    id   = (int)(Math.random() * 90000) + 10000;
        String date = LocalDate.now().toString();

        String savedPath = "";
        if (selectedImageFile != null && selectedImageFile.exists()) {
            try {
                String home = System.getProperty("user.home");
                File screenshotsDir = new File(home, "BugTracker/screenshots");
                if (!screenshotsDir.exists()) screenshotsDir.mkdirs();
                String ext      = getExtension(selectedImageFile.getName());
                String destName = "bug_" + id + (ext.isEmpty() ? "" : "." + ext);
                File   destFile = new File(screenshotsDir, destName);
                Files.copy(selectedImageFile.toPath(), destFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
                savedPath = destFile.getPath();
            } catch (IOException ex) {
                showToast("⚠ Couldn't copy image: " + ex.getMessage(), DANGER);
            }
        }

        String line = String.join(",",
            String.valueOf(id),
            name,
            type,
            String.valueOf(priorityBox.getSelectedItem()),
            String.valueOf(levelBox   .getSelectedItem()),
            projectField.getText().trim(),
            date,
            String.valueOf(statusBox.getSelectedItem()),
            testerIdField.getText().trim(),
            String.valueOf(devBox.getSelectedItem()),
            savedPath
        );

        try (PrintWriter out = new PrintWriter(new FileWriter("bug.txt", true))) {
            out.println(line);
            showToast("Bug #" + id + " saved successfully ✅", SUCCESS);
            Timer t = new Timer(1400, e -> { new TesterFrame(testerName); dispose(); });
            t.setRepeats(false);
            t.start();
        } catch (IOException ex) {
            showToast("Failed to save bug: " + ex.getMessage(), DANGER);
        }

        String devName = devBox.getSelectedItem().toString();
        EmailService.sendEmail(
            currentUser,
            devName + "@gmail.com",
            "New Bug Assigned",
            "A new bug has been assigned to you: " + nameField.getText()
        );
    }

    // ── Helper: get file extension ────────────────────────────────────────────
    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot >= 0 && dot < filename.length() - 1)
               ? filename.substring(dot + 1).toLowerCase() : "";
    }

    // ── UI Helpers ────────────────────────────────────────────────────────────

    private JPanel fieldGroup(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_DIM);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        p.add(lbl,  BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JPanel twoCol(JPanel a, JPanel b) {
        JPanel p = new JPanel(new GridLayout(1, 2, 12, 0));
        p.setOpaque(false);
        p.add(a); p.add(b);
        return p;
    }

    private JPanel sectionHeader(String title, String emoji) {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(),
                    ACCENT.getBlue(), 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(ACCENT);
                g2.fillRoundRect(0, 0, 3, getHeight(), 3, 3);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        p.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        JLabel lbl = new JLabel(emoji + "  " + title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(ACCENT);
        p.add(lbl);
        return p;
    }

    private JTextField styledField(String placeholder) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                String txt = getText();
                if ((txt == null || txt.isEmpty()) && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(TEXT_DIM);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    FontMetrics fm = g2.getFontMetrics();
                    Insets ins = getInsets();
                    int y = ins.top + (getHeight() - ins.top - ins.bottom
                                       + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(placeholder, ins.left + 2, y);
                    g2.dispose();
                }
            }
        };
        f.setFont(FONT_FIELD);
        f.setForeground(TEXT_MAIN);
        f.setBackground(BG_FIELD);
        f.setCaretColor(ACCENT);
        f.setBorder(new CompoundBorder(
            new RoundedBorder(BORDER_CLR, 8, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        f.setPreferredSize(new Dimension(0, 40));
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                f.setBorder(new CompoundBorder(new RoundedBorder(ACCENT, 8, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                f.repaint();
            }
            @Override public void focusLost(FocusEvent e) {
                f.setBorder(new CompoundBorder(new RoundedBorder(BORDER_CLR, 8, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                f.repaint();
            }
        });
        return f;
    }

    @SuppressWarnings("unchecked")
    private JComboBox<String> styledCombo(String... items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(FONT_FIELD);
        cb.setForeground(TEXT_MAIN);
        cb.setBackground(BG_FIELD);
        cb.setBorder(new CompoundBorder(
            new RoundedBorder(BORDER_CLR, 8, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        cb.setPreferredSize(new Dimension(0, 40));
        cb.setMaximumRowCount(6);
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object val, int idx, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, val, idx, sel, focus);
                setBackground(sel ? ACCENT.darker() : BG_CARD);
                setForeground(sel ? Color.WHITE : TEXT_MAIN);
                setFont(FONT_FIELD);
                setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
                return this;
            }
        });
        return cb;
    }

    private JLabel pillLabel(String text, Color color) {
        JLabel l = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(),
                    color.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                    getHeight(), getHeight());
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1,
                    getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(new Font("Segoe UI", Font.BOLD, 9));
        l.setForeground(color);
        l.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        l.setOpaque(false);
        return l;
    }

    private JButton iconButton(String text, Color hoverColor) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(TEXT_DIM);
        b.setBackground(new Color(0, 0, 0, 0));
        b.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setForeground(hoverColor); }
            @Override public void mouseExited (MouseEvent e) { b.setForeground(TEXT_DIM);   }
        });
        return b;
    }

    private JButton actionButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(FONT_BTN);
        b.setForeground(fg);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorder(new CompoundBorder(
            new RoundedBorder(BORDER_CLR, 10, 1),
            BorderFactory.createEmptyBorder(10, 22, 10, 22)));
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setForeground(TEXT_MAIN); }
            @Override public void mouseExited (MouseEvent e) { b.setForeground(fg);        }
        });
        return b;
    }

    private JButton glowButton(String text, Color accent, Color bg) {
        JButton b = new JButton(text) {
            private float alpha = 0f;
            private Timer rollTimer;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { animAlpha(1f); }
                    @Override public void mouseExited (MouseEvent e) { animAlpha(0f); }
                });
            }
            private void animAlpha(float target) {
                if (rollTimer != null) rollTimer.stop();
                rollTimer = new Timer(16, ev -> {
                    alpha += (target - alpha) * 0.18f;
                    if (Math.abs(alpha - target) < 0.01f) {
                        alpha = target; rollTimer.stop();
                    }
                    repaint();
                });
                rollTimer.start();
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, accent, getWidth(), getHeight(),
                    new Color(ACCENT2.getRed(), ACCENT2.getGreen(), ACCENT2.getBlue())));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(255, 255, 255, (int)(alpha * 30)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(FONT_BTN);
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 26, 10, 26));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void showToast(String msg, Color color) {
        JWindow toast = new JWindow(this);
        toast.setBackground(new Color(0, 0, 0, 0));
        JLabel lbl = new JLabel("  " + msg + "  ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(12, 22, 44, 240));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(color);
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        toast.add(lbl);
        toast.pack();
        Point loc = getLocation();
        toast.setLocation(loc.x + (getWidth() - toast.getWidth()) / 2,
                          loc.y + getHeight() - 70);
        toast.setVisible(true);
        Timer t = new Timer(2000, e -> toast.dispose());
        t.setRepeats(false);
        t.start();
    }

    private void shake(JComponent comp) {
        Point orig = comp.getLocation();
        Timer t = new Timer(30, null);
        int[] steps = {-6, 6, -5, 5, -3, 3, -1, 1, 0};
        final int[] idx = {0};
        t.addActionListener(e -> {
            if (idx[0] >= steps.length) { t.stop(); comp.setLocation(orig); return; }
            comp.setLocation(orig.x + steps[idx[0]++], orig.y);
        });
        t.start();
    }

    private void makeDraggable(JComponent comp) {
        final Point[] drag = {null};
        comp.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e)  { drag[0] = e.getPoint(); }
            @Override public void mouseReleased(MouseEvent e) { drag[0] = null; }
        });
        comp.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (drag[0] == null) return;
                Point loc = getLocation();
                setLocation(loc.x + e.getX() - drag[0].x,
                            loc.y + e.getY() - drag[0].y);
            }
        });
    }

    // ── Rounded border ────────────────────────────────────────────────────────
    private static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int   radius, thickness;
        RoundedBorder(Color c, int r, int t) { color = c; radius = r; thickness = t; }
        @Override public void paintBorder(Component c, Graphics g,
                int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, w-1, h-1, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) {
            return new Insets(radius/2, radius/2, radius/2, radius/2);
        }
        @Override public boolean isBorderOpaque() { return false; }
    }
}
/*package bugtrackerprojectsh.GUI;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import bugtrackerprojectsh.UserService;
import bugtrackerprojectsh.EmailService;
 
public class AddBugFrame extends JFrame {
 
    // ── Fields ───────────────────────────────────────────────────────────────
    private JTextField nameField, typeField, projectField, testerIdField, screenshotField;
    private JComboBox<String> priorityBox, levelBox, statusBox, devBox;
    private JLabel imagePreviewLabel;
    private File   selectedImageFile;
    private final String testerName;
    private String currentUser;
 
    // ── Palette ──────────────────────────────────────────────────────────────
    private static final Color BG_DEEP    = new Color(8,  14,  28);
    private static final Color BG_PANEL   = new Color(13, 22,  44);
    private static final Color BG_CARD    = new Color(18, 30,  58);
    private static final Color BG_FIELD   = new Color(22, 36,  68);
    private static final Color ACCENT     = new Color(0,  210, 255);
    private static final Color ACCENT2    = new Color(120, 80, 255);
    private static final Color SUCCESS    = new Color(0,  230, 140);
    private static final Color DANGER     = new Color(255, 70, 100);
    private static final Color TEXT_MAIN  = new Color(220, 235, 255);
    private static final Color TEXT_DIM   = new Color(100, 130, 170);
    private static final Color BORDER_CLR = new Color(35,  60, 110);
 
    // ── Fonts ────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD,  11);
    private static final Font FONT_FIELD  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 10);
 
    // ────────────────────────────────────────────────────────────────────────
    public AddBugFrame(String testerName) {
        this.testerName  = testerName;
        this.currentUser = testerName;
 
        setTitle("Bug Tracker  ·  New Bug Report");
        setSize(560, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setBackground(BG_DEEP);
 
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 55, 100, 18));
                for (int x = 0; x < getWidth();  x += 28) g2.drawLine(x, 0, x, getHeight());
                for (int y = 0; y < getHeight(); y += 28) g2.drawLine(0, y, getWidth(), y);
                GradientPaint gp = new GradientPaint(0, 0,
                    new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 40),
                    0, 160, new Color(0, 0, 0, 0));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), 160);
                g2.setColor(BORDER_CLR);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 18, 18);
                g2.dispose();
            }
        };
        root.setBackground(BG_DEEP);
 
        root.add(buildTitleBar(), BorderLayout.NORTH);
        root.add(buildForm(),     BorderLayout.CENTER);
        root.add(buildFooter(),   BorderLayout.SOUTH);
 
        setContentPane(root);
        makeDraggable(root);
        setVisible(true);
    }
 
    // ── Title Bar ─────────────────────────────────────────────────────────────
    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(),
                    ACCENT.getBlue(), 18));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 18, 18, 18);
                g2.setColor(BORDER_CLR);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 54));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 12));
 
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        JLabel icon  = new JLabel("🐛");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        JLabel title = new JLabel("New Bug Report");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_MAIN);
        JLabel badge = pillLabel("REPORT", ACCENT);
        left.add(icon); left.add(title); left.add(badge);
 
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);
        JLabel dateLabel = new JLabel(LocalDate.now().format(
            DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        dateLabel.setFont(FONT_SMALL);
        dateLabel.setForeground(TEXT_DIM);
        JButton closeBtn = iconButton("✕", DANGER);
        closeBtn.addActionListener(e -> dispose());
        right.add(dateLabel); right.add(closeBtn);
 
        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }
 
    // ── Form ──────────────────────────────────────────────────────────────────
    private JScrollPane buildForm() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(18, 24, 10, 24));
 
        // ── Section: Bug Info ──
        form.add(sectionHeader("Bug Details", "🔍"));
        form.add(Box.createVerticalStrut(8));
 
        JPanel row1 = twoCol(
            fieldGroup("Bug Name *", nameField = styledField("e.g. Login crash on timeout")),
            fieldGroup("Bug Type *", typeField = styledField("e.g. Functional"))
        );
        form.add(row1);
        form.add(Box.createVerticalStrut(10));
 
        JPanel row2 = twoCol(
            fieldGroup("Priority",       priorityBox = styledCombo("Low","Medium","High")),
            fieldGroup("Severity Level", levelBox    = styledCombo("Minor","Major","Critical"))
        );
        form.add(row2);
        form.add(Box.createVerticalStrut(10));
 
        JPanel row3 = twoCol(
            fieldGroup("Project Name", projectField = styledField("e.g. Alpha Release")),
            fieldGroup("Status",       statusBox    = styledCombo("Open","In Progress","Closed"))
        );
        form.add(row3);
        form.add(Box.createVerticalStrut(20));
 
        // ── Section: Assignment ──
        form.add(sectionHeader("Assignment", "👤"));
        form.add(Box.createVerticalStrut(8));
 
        devBox = styledCombo();
        for (String dev : UserService.getDevelopers()) devBox.addItem(dev);
 
        testerIdField = styledField(testerName);
        testerIdField.setText(testerName);
        testerIdField.setEditable(false);
 
        JPanel row4 = twoCol(
            fieldGroup("Tester Name", testerIdField),
            fieldGroup("Assign Dev",  devBox)
        );
        form.add(row4);
        form.add(Box.createVerticalStrut(20));
 
        // ── Section: Evidence ──
        form.add(sectionHeader("Evidence", "📎"));
        form.add(Box.createVerticalStrut(8));
 
        screenshotField = styledField("Click Browse to select an image...");
        screenshotField.setEditable(false);
        screenshotField.setBackground(new Color(16, 26, 52));
 
        JButton browseBtn = buildBrowseButton();
 
        JPanel screenshotRow = new JPanel(new BorderLayout(8, 0));
        screenshotRow.setOpaque(false);
        screenshotRow.add(screenshotField, BorderLayout.CENTER);
        screenshotRow.add(browseBtn,       BorderLayout.EAST);
 
        form.add(fieldGroup("Screenshot", screenshotRow));
        form.add(Box.createVerticalStrut(10));
 
        imagePreviewLabel = new JLabel("No image selected", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_FIELD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDER_CLR);
                g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_ROUND, 0, new float[]{6, 4}, 0));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        imagePreviewLabel.setFont(FONT_SMALL);
        imagePreviewLabel.setForeground(TEXT_DIM);
        imagePreviewLabel.setPreferredSize(new Dimension(0, 140));
        imagePreviewLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        imagePreviewLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imagePreviewLabel.setOpaque(false);
 
        form.add(imagePreviewLabel);
        form.add(Box.createVerticalStrut(6));
 
        JScrollPane scroll = new JScrollPane(form);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
        return scroll;
    }
 
    // ── Browse Button ─────────────────────────────────────────────────────────
    private JButton buildBrowseButton() {
        JButton btn = new JButton("Browse") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0,
                    new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 180),
                    getWidth(), getHeight(),
                    new Color(ACCENT2.getRed(), ACCENT2.getGreen(), ACCENT2.getBlue(), 180)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(FONT_BTN);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(90, 40));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> openImageChooser());
        return btn;
    }
 
    // ── File Chooser Logic ────────────────────────────────────────────────────
    private void openImageChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose a screenshot image");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image Files (*.png, *.jpg, *.jpeg, *.gif, *.bmp)",
            "png", "jpg", "jpeg", "gif", "bmp"
        ));
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = chooser.getSelectedFile();
            screenshotField.setText(selectedImageFile.getAbsolutePath());
            loadImagePreview(selectedImageFile);
        }
    }
 
    // ── Image Preview ─────────────────────────────────────────────────────────
    private void loadImagePreview(File imgFile) {
        try {
            BufferedImage img = ImageIO.read(imgFile);
            if (img == null) {
                imagePreviewLabel.setIcon(null);
                imagePreviewLabel.setText("❌ Can't read this file");
                return;
            }
            int maxW = 480, maxH = 130;
            double scale = Math.min((double) maxW / img.getWidth(),
                                    (double) maxH / img.getHeight());
            Image scaled = img.getScaledInstance(
                (int)(img.getWidth()  * scale),
                (int)(img.getHeight() * scale),
                Image.SCALE_SMOOTH);
            imagePreviewLabel.setIcon(new ImageIcon(scaled));
            imagePreviewLabel.setText("");
        } catch (IOException ex) {
            imagePreviewLabel.setIcon(null);
            imagePreviewLabel.setText("❌ Error loading image");
        }
    }
 
    // ── Footer ────────────────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(BORDER_CLR);
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
            }
        };
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(14, 24, 20, 24));
 
        JLabel hint = new JLabel("* Required fields");
        hint.setFont(FONT_SMALL);
        hint.setForeground(TEXT_DIM);
 
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
 
        JButton cancelBtn = actionButton("Cancel", BG_FIELD, TEXT_DIM);
        JButton saveBtn   = glowButton("Save Bug  →", ACCENT, BG_DEEP);
 
        cancelBtn.addActionListener(e -> dispose());
        saveBtn.addActionListener(e -> saveBug());
 
        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
 
        footer.add(hint,     BorderLayout.WEST);
        footer.add(btnPanel, BorderLayout.EAST);
        return footer;
    }
 
    // ── Save Logic ────────────────────────────────────────────────────────────
    private void saveBug() {
        String name = nameField.getText().trim();
        String type = typeField.getText().trim();
 
        if (name.isEmpty() || type.isEmpty()) {
            showToast("Please fill in all required fields ❌", DANGER);
            shake(name.isEmpty() ? nameField : typeField);
            return;
        }
 
        int    id   = (int)(Math.random() * 90000) + 10000;
        String date = LocalDate.now().toString();
 
        String savedPath = "";
        if (selectedImageFile != null && selectedImageFile.exists()) {
            try {
                String home = System.getProperty("user.home");
                File screenshotsDir = new File(home, "BugTracker/screenshots");
                if (!screenshotsDir.exists()) screenshotsDir.mkdirs();
                String ext      = getExtension(selectedImageFile.getName());
                String destName = "bug_" + id + (ext.isEmpty() ? "" : "." + ext);
                File   destFile = new File(screenshotsDir, destName);
                Files.copy(selectedImageFile.toPath(), destFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
                savedPath = destFile.getPath();
            } catch (IOException ex) {
                showToast("⚠ Couldn't copy image: " + ex.getMessage(), DANGER);
            }
        }
 
        String line = String.join(",",
            String.valueOf(id),
            name,
            type,
            String.valueOf(priorityBox.getSelectedItem()),
            String.valueOf(levelBox   .getSelectedItem()),
            projectField.getText().trim(),
            date,
            String.valueOf(statusBox.getSelectedItem()),
            testerIdField.getText().trim(),
            String.valueOf(devBox.getSelectedItem()),
            savedPath
        );
 
        try (PrintWriter out = new PrintWriter(new FileWriter("bug.txt", true))) {
            out.println(line);
            showToast("Bug #" + id + " saved successfully ✅", SUCCESS);
            Timer t = new Timer(1400, e -> { new TesterFrame(testerName); dispose(); });
            t.setRepeats(false);
            t.start();
        } catch (IOException ex) {
            showToast("Failed to save bug: " + ex.getMessage(), DANGER);
        }
 
        String devName = devBox.getSelectedItem().toString();
        EmailService.sendEmail(
            currentUser,
            devName + "@gmail.com",
            "New Bug Assigned",
            "A new bug has been assigned to you: " + nameField.getText()
        );
    }
 
    // ── Helper: get file extension ────────────────────────────────────────────
    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot >= 0 && dot < filename.length() - 1)
               ? filename.substring(dot + 1).toLowerCase() : "";
    }
 
    // ── UI Helpers ────────────────────────────────────────────────────────────
 
    private JPanel fieldGroup(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_DIM);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        p.add(lbl,  BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }
 
    private JPanel twoCol(JPanel a, JPanel b) {
        JPanel p = new JPanel(new GridLayout(1, 2, 12, 0));
        p.setOpaque(false);
        p.add(a); p.add(b);
        return p;
    }
 
    private JPanel sectionHeader(String title, String emoji) {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(),
                    ACCENT.getBlue(), 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(ACCENT);
                g2.fillRoundRect(0, 0, 3, getHeight(), 3, 3);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        p.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        JLabel lbl = new JLabel(emoji + "  " + title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(ACCENT);
        p.add(lbl);
        return p;
    }
 
    // ✅ التعديل الأساسي: الـ placeholder بيظهر بس لو getText() فعلاً فاضي
    private JTextField styledField(String placeholder) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // ✅ نرسم الـ placeholder بس لو الـ field فاضي ومش focus
                String txt = getText();
                if ((txt == null || txt.isEmpty()) && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(TEXT_DIM);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    FontMetrics fm = g2.getFontMetrics();
                    Insets ins = getInsets();
                    // ✅ حساب Y من الـ insets الفعلية لا من getHeight() مباشرة
                    int y = ins.top + (getHeight() - ins.top - ins.bottom
                                       + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(placeholder, ins.left + 2, y);
                    g2.dispose();
                }
            }
        };
        f.setFont(FONT_FIELD);
        f.setForeground(TEXT_MAIN);
        f.setBackground(BG_FIELD);
        f.setCaretColor(ACCENT);
        f.setBorder(new CompoundBorder(
            new RoundedBorder(BORDER_CLR, 8, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        f.setPreferredSize(new Dimension(0, 40));
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                f.setBorder(new CompoundBorder(new RoundedBorder(ACCENT, 8, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                f.repaint();
            }
            @Override public void focusLost(FocusEvent e) {
                f.setBorder(new CompoundBorder(new RoundedBorder(BORDER_CLR, 8, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                f.repaint();
            }
        });
        return f;
    }
 
    @SuppressWarnings("unchecked")
    private JComboBox<String> styledCombo(String... items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(FONT_FIELD);
        cb.setForeground(TEXT_MAIN);
        cb.setBackground(BG_FIELD);
        cb.setBorder(new CompoundBorder(
            new RoundedBorder(BORDER_CLR, 8, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        cb.setPreferredSize(new Dimension(0, 40));
        cb.setMaximumRowCount(6);
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object val, int idx, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, val, idx, sel, focus);
                setBackground(sel ? ACCENT.darker() : BG_CARD);
                setForeground(sel ? Color.WHITE : TEXT_MAIN);
                setFont(FONT_FIELD);
                setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
                return this;
            }
        });
        return cb;
    }
 
    private JLabel pillLabel(String text, Color color) {
        JLabel l = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(),
                    color.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                    getHeight(), getHeight());
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1,
                    getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(new Font("Segoe UI", Font.BOLD, 9));
        l.setForeground(color);
        l.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        l.setOpaque(false);
        return l;
    }
 
    private JButton iconButton(String text, Color hoverColor) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(TEXT_DIM);
        b.setBackground(new Color(0, 0, 0, 0));
        b.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setForeground(hoverColor); }
            @Override public void mouseExited (MouseEvent e) { b.setForeground(TEXT_DIM);   }
        });
        return b;
    }
 
    private JButton actionButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(FONT_BTN);
        b.setForeground(fg);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorder(new CompoundBorder(
            new RoundedBorder(BORDER_CLR, 10, 1),
            BorderFactory.createEmptyBorder(10, 22, 10, 22)));
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setForeground(TEXT_MAIN); }
            @Override public void mouseExited (MouseEvent e) { b.setForeground(fg);        }
        });
        return b;
    }
 
    private JButton glowButton(String text, Color accent, Color bg) {
        JButton b = new JButton(text) {
            private float alpha = 0f;
            private Timer rollTimer;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { animAlpha(1f); }
                    @Override public void mouseExited (MouseEvent e) { animAlpha(0f); }
                });
            }
            private void animAlpha(float target) {
                if (rollTimer != null) rollTimer.stop();
                rollTimer = new Timer(16, ev -> {
                    alpha += (target - alpha) * 0.18f;
                    if (Math.abs(alpha - target) < 0.01f) {
                        alpha = target; rollTimer.stop();
                    }
                    repaint();
                });
                rollTimer.start();
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, accent, getWidth(), getHeight(),
                    new Color(ACCENT2.getRed(), ACCENT2.getGreen(), ACCENT2.getBlue())));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(255, 255, 255, (int)(alpha * 30)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(FONT_BTN);
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 26, 10, 26));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
 
    private void showToast(String msg, Color color) {
        JWindow toast = new JWindow(this);
        toast.setBackground(new Color(0, 0, 0, 0));
        JLabel lbl = new JLabel("  " + msg + "  ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(12, 22, 44, 240));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(color);
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        toast.add(lbl);
        toast.pack();
        Point loc = getLocation();
        toast.setLocation(loc.x + (getWidth() - toast.getWidth()) / 2,
                          loc.y + getHeight() - 70);
        toast.setVisible(true);
        Timer t = new Timer(2000, e -> toast.dispose());
        t.setRepeats(false);
        t.start();
    }
 
    private void shake(JComponent comp) {
        Point orig = comp.getLocation();
        Timer t = new Timer(30, null);
        int[] steps = {-6, 6, -5, 5, -3, 3, -1, 1, 0};
        final int[] idx = {0};
        t.addActionListener(e -> {
            if (idx[0] >= steps.length) { t.stop(); comp.setLocation(orig); return; }
            comp.setLocation(orig.x + steps[idx[0]++], orig.y);
        });
        t.start();
    }
 
    private void makeDraggable(JComponent comp) {
        final Point[] drag = {null};
        comp.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e)  { drag[0] = e.getPoint(); }
            @Override public void mouseReleased(MouseEvent e) { drag[0] = null; }
        });
        comp.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (drag[0] == null) return;
                Point loc = getLocation();
                setLocation(loc.x + e.getX() - drag[0].x,
                            loc.y + e.getY() - drag[0].y);
            }
        });
    }
 
    // ── Rounded border ────────────────────────────────────────────────────────
    private static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int   radius, thickness;
        RoundedBorder(Color c, int r, int t) { color = c; radius = r; thickness = t; }
        @Override public void paintBorder(Component c, Graphics g,
                int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, w-1, h-1, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) {
            return new Insets(radius/2, radius/2, radius/2, radius/2);
        }
        @Override public boolean isBorderOpaque() { return false; }
    }
}
*/