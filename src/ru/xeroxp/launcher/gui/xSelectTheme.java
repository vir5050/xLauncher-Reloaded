package ru.xeroxp.launcher.gui;

import ru.xeroxp.launcher.config.xSettings;
import ru.xeroxp.launcher.config.xSettingsOfTheme;
import ru.xeroxp.launcher.gui.elements.xServer;
import ru.xeroxp.launcher.utils.xDebug;
import ru.xeroxp.launcher.xLauncher;
import ru.xeroxp.launcher.xMain;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class xSelectTheme extends JPanel {
    private final JLabel[] serverOnline = new JLabel[xServer.getSize()];
    private BufferedImage background;
    private BufferedImage onlineBar;
    private BufferedImage onlineBarBg;
    private BufferedImage offlineBar;
    private final BufferedImage image = new BufferedImage(xSettingsOfTheme.ServersInScrollPanelSize[0], xSettingsOfTheme.ServersInScrollPanelSize[1], BufferedImage.TYPE_INT_ARGB);
    private final JScrollPane scrollPane;
    private final JPanel serverPanel = new JPanel();
    private final JPanel serversBackground = new BgPanel();

    private boolean sendReady = false;
    private Clip clip = null;
    private Font serverFont = null;
    private Font serverFont2 = null;

    public xSelectTheme() {
        xDebug.infoMessage("Выбор сервера");

        setLayout(null);
        setMinimumSize(new Dimension(xSettingsOfTheme.LauncherSize[0], xSettingsOfTheme.LauncherSize[1]));
        setSize(xSettingsOfTheme.LauncherSize[0], xSettingsOfTheme.LauncherSize[1]);

        serverPanel.setLayout(null);
        serverPanel.setBorder(null);
        serverPanel.setOpaque(false);
        serverPanel.setPreferredSize(new Dimension(xSettingsOfTheme.ServersInScrollPanelSize[0], xSettingsOfTheme.ServersInScrollPanelSize[1]));
        scrollPane = new JScrollPane(serverPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        JScrollBar s_bar = new JScrollBar();
        JScrollPane sp = this.scrollPane;
        s_bar.setUI(new xScrollBar.MyScrollbarUI());
        Dimension dim = new Dimension(xSettingsOfTheme.ServersScrollBarSize[0], xSettingsOfTheme.ServersScrollBarSize[1]);
        s_bar.setPreferredSize(dim);
        s_bar.setBackground(new Color(0, 0, 0, 0));
        s_bar.setForeground(new Color(0, 0, 0, 0));
        s_bar.setOpaque(false);
        sp.setVerticalScrollBar(s_bar);
        scrollPane.setBounds(xSettingsOfTheme.ServersScrollPanelBounds[0], xSettingsOfTheme.ServersScrollPanelBounds[1], xSettingsOfTheme.ServersScrollPanelBounds[2], xSettingsOfTheme.ServersScrollPanelBounds[3]);
        serversBackground.setSize(new Dimension(xSettingsOfTheme.ServersInScrollPanelSize[0], xSettingsOfTheme.ServersInScrollPanelSize[1]));

        InputStream is = xTheme.class.getResourceAsStream("/font/" + xSettingsOfTheme.FontFile1);
        InputStream is2 = xTheme.class.getResourceAsStream("/font/" + xSettingsOfTheme.FontFile2);
        Font arial = null;

        try {
            arial = Font.createFont(0, is);
            arial = arial.deriveFont(Font.BOLD, xSettingsOfTheme.ServersFonts[0]);
            this.serverFont = Font.createFont(0, is2);
            this.serverFont = this.serverFont.deriveFont(Font.BOLD, xSettingsOfTheme.ServersFonts[1]);
            this.serverFont2 = this.serverFont.deriveFont(Font.BOLD, xSettingsOfTheme.ServersFonts[2]);
        } catch (FontFormatException e2) {
            xDebug.errorMessage("Failed load font: " + e2.getMessage());
        } catch (IOException e2) {
            xDebug.errorMessage("Failed load font: " + e2.getMessage());
        }

        if (xLauncher.getLauncher().getSound()) {
            try {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(xTheme.class.getResource("/sound/" + xSettingsOfTheme.ClickButtonSound));
                this.clip = AudioSystem.getClip();
                this.clip.open(audioIn);
            } catch (UnsupportedAudioFileException e2) {
                xDebug.errorMessage("Unsupported Audio Format: " + e2.getMessage());
            } catch (IOException e2) {
                xDebug.errorMessage("Failed load sound: " + e2.getMessage());
            } catch (LineUnavailableException e) {
                xDebug.errorMessage("Failed load clip: " + e.getMessage());
            }
        }

        try {
            this.background = ImageIO.read(xSelectTheme.class.getResource("/images/" + xSettingsOfTheme.ServersPanelBackgroundImage));
            this.onlineBar = ImageIO.read(xSelectTheme.class.getResource("/images/" + xSettingsOfTheme.ServersBarImages[0]));
            this.offlineBar = ImageIO.read(xSelectTheme.class.getResource("/images/" + xSettingsOfTheme.ServersBarImages[1]));
            this.onlineBarBg = ImageIO.read(xSelectTheme.class.getResource("/images/" + xSettingsOfTheme.ServersBarImages[2]));
        } catch (IOException e) {
            xDebug.errorMessage("Failed load select server theme images: " + e.getMessage());
        }

        JLabel header = new JLabel(xSettings.LauncherName + " v" + xMain.getVersion());
        header.setForeground(xSettingsOfTheme.HeaderColor);
        header.setBounds(xSettingsOfTheme.HeaderBounds[0], xSettingsOfTheme.HeaderBounds[1], xSettingsOfTheme.HeaderBounds[2], xSettingsOfTheme.HeaderBounds[3]);
        header.setFont(arial);

        add(header);
        xHeaderButton.loadButtons();
        addHeaderButtons();
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(this.background, 0, 0, this);
    }

    public void updateOnline(String[] args) {
        Graphics g2 = this.image.getGraphics();

        for (int i = 0; i < xServer.getSize(); i++) {
            int online = 0;

            try {
                online = Integer.parseInt(args[i]);
            } catch (NumberFormatException e) {
                xDebug.errorMessage("Failed to parse server online");
            }

            xServer server = xServer.getServer(i);
            int maxOnline = server.getOnline();

            if (online > 0) {
                this.serverOnline[i].setText(online + "/" + maxOnline);
                g2.drawImage(this.onlineBarBg, server.getBarX(), server.getBarY(), this);

                if (online > maxOnline) {
                    online = maxOnline;
                }

                g2.drawImage(this.onlineBar.getSubimage(0, 0, (int) (server.getBarSizeX() / server.getOnline() * online), server.getBarSizeY()), server.getBarX(), server.getBarY(), null);
            } else if (online == -1) {
                this.serverOnline[i].setText("OFF");
                g2.drawImage(this.offlineBar, server.getBarX(), server.getBarY(), this);
            } else {
                this.serverOnline[i].setText(online + "/" + maxOnline);
                g2.drawImage(this.onlineBarBg, server.getBarX(), server.getBarY(), this);
            }
        }

        g2.dispose();
        repaint();
    }

    void addHeaderButtons() {
        for (final xHeaderButton headerButton : xHeaderButton.getButtons()) {
            final JLabel headerButtons = new JLabel();
            headerButtons.setBounds(headerButton.getImageX(), headerButton.getImageY(), headerButton.getImageSizeX(), headerButton.getImageSizeY());
            headerButtons.setIcon(new ImageIcon(xTheme.class.getResource("/images/" + headerButton.getImage())));
            headerButtons.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (headerButton.getButtonName().equals("exit")) {
                        System.exit(0);
                    } else if (headerButton.getButtonName().equals("minimize")) {
                        xLauncher.getLauncher().iconified();
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    headerButtons.setIcon(new ImageIcon(xTheme.class.getResource("/images/" + headerButton.getOnMouseImage())));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    headerButtons.setIcon(new ImageIcon(xTheme.class.getResource("/images/" + headerButton.getImage())));
                }
            });
        }
    }

    public void addServers() {
        for (final xServer server : xServer.getServers()) {
            final JLabel serverIcon = new JLabel();
            serverIcon.setBounds(server.getIconX(), server.getIconY(), server.getIconSizeX(), server.getIconSizeY());
            serverIcon.setIcon(new ImageIcon(xSelectTheme.class.getResource("/images/" + server.getName().toLowerCase() + ".png")));
            serverIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

            final JLabel serverImage = new JLabel();
            serverImage.setBounds(server.getImageX(), server.getImageY(), server.getImageSizeX(), server.getImageSizeY());
            serverImage.setIcon(new ImageIcon(xSelectTheme.class.getResource("/images/" + xSettingsOfTheme.ServersImages[0])));
            serverImage.setCursor(new Cursor(Cursor.HAND_CURSOR));

            serverImage.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!xSelectTheme.this.sendReady) {
                        if (xSelectTheme.this.clip != null) {
                            xSelectTheme.this.clip.start();
                        }

                        xLauncher.getLauncher().drawMinecraft(server.getIp(), server.getPort(), server.getFolder(), server.getJar(), server.getVersion());
                        xSelectTheme.this.sendReady = true;
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    serverImage.setIcon(new ImageIcon(xSelectTheme.class.getResource("/images/" + xSettingsOfTheme.ServersImages[1])));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    serverImage.setIcon(new ImageIcon(xSelectTheme.class.getResource("/images/" + xSettingsOfTheme.ServersImages[0])));
                }
            });

            serverIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!xSelectTheme.this.sendReady) {
                        if (xSelectTheme.this.clip != null) {
                            xSelectTheme.this.clip.start();
                        }

                        xLauncher.getLauncher().drawMinecraft(server.getIp(), server.getPort(), server.getFolder(), server.getJar(), server.getVersion());
                        xSelectTheme.this.sendReady = true;
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    serverImage.setIcon(new ImageIcon(xSelectTheme.class.getResource("/images/" + xSettingsOfTheme.ServersImages[1])));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    serverImage.setIcon(new ImageIcon(xSelectTheme.class.getResource("/images/" + xSettingsOfTheme.ServersImages[0])));
                }
            });

            JLabel serverTitle = new JLabel(server.getName().toUpperCase());
            serverTitle.setHorizontalTextPosition(JLabel.RIGHT);
            serverTitle.setHorizontalAlignment(JLabel.RIGHT);
            serverTitle.setFont(this.serverFont2);
            serverTitle.setBounds(server.getTitleX(), server.getTitleY(), server.getTitleSizeX(), server.getTitleSizeY());
            serverTitle.setForeground(server.getTitleColor());

            int id = server.getId();

            this.serverOnline[id] = new JLabel();
            this.serverOnline[id].setFont(this.serverFont);
            this.serverOnline[id].setBounds(server.getOnlineX(), server.getOnlineY(), server.getOnlineSizeX(), server.getOnlineSizeY());
            this.serverOnline[id].setForeground(server.getOnlineColor());

            serverPanel.add(serverTitle);
            serverPanel.add(this.serverOnline[id]);
            serverPanel.add(serverImage);
            serverPanel.add(serverIcon);
        }

        add(scrollPane);
        serverPanel.add(serversBackground);
    }

    private class BgPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}