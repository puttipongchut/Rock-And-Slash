package Project3_6713230;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.awt.Image;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.ImageIcon;
import java.net.URL;

public class AssetLoader {
    public static Image loadImage(String path) {
        InputStream stream = AssetLoader.class.getClassLoader().getResourceAsStream(path);

        if (stream == null) {
            System.err.println("Image Asset not found: " + path);
            return null;
        }

        try {
            return ImageIO.read(stream);
        } catch (java.io.IOException e) {
            System.err.println("Error reading image asset: " + path);
            e.printStackTrace();
            return null;
        }
    }

    public static ImageIcon loadGifIcon(String path) {
        URL imageUrl = AssetLoader.class.getResource(path);

        if (imageUrl == null) {
            System.err.println("GIF Asset not found at URL: " + path);
            return null;
        }

        try {
            return new ImageIcon(imageUrl);
        } catch (Exception e) {
            System.err.println("Error creating ImageIcon for GIF: " + path);
            e.printStackTrace();
            return null;
        }
    }

    public static Clip loadClip(String path) {
        try {
            InputStream audioStream = AssetLoader.class.getClassLoader().getResourceAsStream(path);

            if (audioStream == null) {
                System.err.println("Sound Asset not found: " + path);
                return null;
            }

            BufferedInputStream bis = new BufferedInputStream(audioStream);

            AudioInputStream ais = AudioSystem.getAudioInputStream(bis);

            Clip clip = AudioSystem.getClip();
            clip.open(ais);

            return clip;

        } catch (UnsupportedAudioFileException | LineUnavailableException | java.io.IOException e) {
            System.err.println("Error loading sound asset: " + path);
            e.printStackTrace();
            return null;
        }
    }
}