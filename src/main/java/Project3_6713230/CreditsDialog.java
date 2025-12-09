/**
 *
 * @author 6713230_Natwarit Chuboonsub
 * @author 6713229_Nutthapat Techapornhiran
 * @author 6713235_Teetath Prapasanon
 * @author 6713239_Nitich Uanjityanon
 * @author 6713243_Puttipong Chutipongwanit
 */
package Project3_6713230;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CreditsDialog extends JDialog {

    public CreditsDialog(JFrame parent) {
        super(parent, "Credits", true);

        setSize(600, 500);
        setLocationRelativeTo(parent);
        setUndecorated(true);

        CreditsBackgroundPanel mainPanel = new CreditsBackgroundPanel();
        mainPanel.setLayout(null);
        setContentPane(mainPanel);

        createComponents();
    }

    private void createComponents() {
        // Title - Stone gray matching menu
        JLabel titleLabel = new JLabel("CREDITS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48));
        titleLabel.setForeground(new Color(210, 205, 195)); // Stone gray-white
        titleLabel.setBounds(0, 30, 600, 70);
        add(titleLabel);

        // Credits Background Panel
        CreditsPanel bgPanel = new CreditsPanel();
        bgPanel.setLayout(null);
        bgPanel.setBounds(50, 110, 500, 310);

        // Credits Text - Light stone color
        JTextArea creditsText = new JTextArea();
        creditsText.setText(
                "\n\n"
                + "                     Developed by:\n\n"
                + "  6713230 - Natwarit Chuboonsub\n"
                + "  6713229 - Nutthapat Techapornhiran\n"
                + "  6713235 - Teetath Prapasanon\n"
                + "  6713239 - Nitich Uanjityanon\n"
                + "  6713243 - Puttipong Chutipongwanit\n\n"
        );
        creditsText.setFont(new Font("Monospaced", Font.PLAIN, 16));
        creditsText.setForeground(new Color(200, 195, 185)); // Light stone
        creditsText.setBackground(new Color(0, 0, 0, 0));
        creditsText.setEditable(false);
        creditsText.setFocusable(false);
        creditsText.setOpaque(false);
        creditsText.setBounds(0, 0, 500, 310);
        bgPanel.add(creditsText);
        
        add(bgPanel);

        // Back Button - Dark stone theme
        CreditsButton backButton = new CreditsButton("BACK", new Color(60, 58, 62));
        backButton.setBounds(200, 430, 200, 50);
        backButton.addActionListener(e -> dispose());
        add(backButton);
    }
}

// Dark atmospheric background matching game menu
class CreditsBackgroundPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Very dark gradient - matching pixel art atmosphere
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(8, 8, 12),
            0, getHeight(), new Color(18, 16, 20)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Stone-like border
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(new Color(80, 75, 70, 180));
        g2d.drawRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 15, 15);
        
        // Subtle inner highlight
        g2d.setColor(new Color(120, 110, 100, 30));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(13, 13, getWidth() - 26, getHeight() - 26, 12, 12);
    }
}

// Dark atmospheric panel
class CreditsPanel extends JPanel {
    public CreditsPanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // background
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(20, 19, 23),
            0, getHeight(), new Color(28, 26, 30)
        );
        g2d.setPaint(gradient);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

        // Stone border
        g2d.setColor(new Color(90, 85, 80));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
        
        // Subtle inner highlight
        g2d.setColor(new Color(120, 110, 100, 40));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 13, 13);
    }
}

class CreditsButton extends JButton {
    private Color baseColor;
    private boolean isHovered = false;

    public CreditsButton(String text, Color color) {
        super(text);
        this.baseColor = color;
        setFont(new Font("Serif", Font.BOLD, 24));
        setForeground(new Color(210, 205, 195)); // Stone white text
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Button background
        if (isHovered) {
            g2d.setColor(baseColor.brighter());
        } else {
            g2d.setColor(baseColor);
        }
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

        //Button border
        if (isHovered) {
            g2d.setColor(new Color(150, 145, 135));
        } else {
            g2d.setColor(new Color(100, 95, 85));
        }
        g2d.setStroke(new BasicStroke(isHovered ? 3 : 2));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

        super.paintComponent(g);
    }
}