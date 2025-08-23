package de.extio.lm_launcher;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.Window;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalIconFactory;

/**
 * Utility class for scaling UI elements based on screen resolution.
 * Reference resolution is 1920x1080 (1080p).
 */
public class UIScaler {
    
    private static final int REFERENCE_WIDTH = 1920;
    private static final int REFERENCE_HEIGHT = 1080;
    
    private static final double scaleX;
    private static final double scaleY;
    private static final double scale;
    private static final GraphicsDevice primaryDevice;
    
    static {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        primaryDevice = ge.getDefaultScreenDevice();
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        scaleX = (double) screenSize.width / REFERENCE_WIDTH;
        scaleY = (double) screenSize.height / REFERENCE_HEIGHT;
        // Use the smaller scale to maintain aspect ratio, then dampen by 0.8
        double baseScale = Math.min(scaleX, scaleY);
        scale = 1.0 + (baseScale - 1.0) * 0.8;
        
        // Apply global font scaling to UI defaults
        scaleUIDefaults();
    }
    
    /**
     * Scale UI default fonts globally
     */
    private static void scaleUIDefaults() {
        UIManager.getDefaults().entrySet().forEach(entry -> {
            if (entry.getValue() instanceof Font) {
                Font font = (Font) entry.getValue();
                UIManager.put(entry.getKey(), scaleFont(font));
            }
        });
        
        // Scale checkbox icon size
        if (scale != 1.0) {
            try {
                // Create scaled checkbox icons
                Icon checkBoxIcon = new ScaledCheckBoxIcon(false);
                Icon checkBoxSelectedIcon = new ScaledCheckBoxIcon(true);
                
                UIManager.put("CheckBox.icon", checkBoxIcon);
                UIManager.put("CheckBox.selectedIcon", checkBoxSelectedIcon);
            } catch (Exception e) {
                // Fallback - continue without custom icons
                System.err.println("Could not set scaled checkbox icons: " + e.getMessage());
            }
        }
    }
    
    /**
     * Apply scaling to a checkbox component
     * @param checkBox The checkbox to scale
     */
    public static void scaleCheckBox(JCheckBox checkBox) {
        if (checkBox == null) return;
        
        // Scale font
        checkBox.setFont(scaleFont(checkBox.getFont()));
        
        // Scale component size
        Dimension prefSize = checkBox.getPreferredSize();
        if (prefSize != null) {
            checkBox.setPreferredSize(scale(prefSize));
        }
        
        // Apply scaled icons if available
        if (scale != 1.0) {
            try {
                checkBox.setIcon(new ScaledCheckBoxIcon(false));
                checkBox.setSelectedIcon(new ScaledCheckBoxIcon(true));
            } catch (Exception e) {
                // Continue without custom icons
            }
        }
    }
    
    /**
     * Custom scaled checkbox icon
     */
    private static class ScaledCheckBoxIcon implements Icon {
        private final boolean selected;
        private final int size;
        
        public ScaledCheckBoxIcon(boolean selected) {
            this.selected = selected;
            this.size = scale(13); // Default checkbox icon size is typically 13x13
        }
        
        @Override
        public void paintIcon(Component c, java.awt.Graphics g, int x, int y) {
            // Create a graphics context with scaling
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            
            // Apply scaling transform
            g2.scale(scale, scale);
            
            // Get the original icon and paint it
            Icon originalIcon;
            if (selected) {
                originalIcon = MetalIconFactory.getCheckBoxIcon();
                // Simulate selected state
                g2.translate(x / scale, y / scale);
                originalIcon.paintIcon(c, g2, 0, 0);
            } else {
                originalIcon = MetalIconFactory.getCheckBoxIcon();
                g2.translate(x / scale, y / scale);
                originalIcon.paintIcon(c, g2, 0, 0);
            }
            
            g2.dispose();
        }
        
        @Override
        public int getIconWidth() {
            return size;
        }
        
        @Override
        public int getIconHeight() {
            return size;
        }
    }
    
    /**
     * Scale all fonts in a component hierarchy recursively
     * @param component The root component to scale
     */
    public static void scaleComponentFonts(Component component) {
        if (component == null) return;
        
        // Scale the component's font
        Font font = component.getFont();
        if (font != null) {
            component.setFont(scaleFont(font));
        }
        
        // Recursively scale child components
        if (component instanceof Container) {
            Container container = (Container) component;
            for (Component child : container.getComponents()) {
                scaleComponentFonts(child);
            }
        }
    }
    
    /**
     * Scale a dimension value (width or height) based on screen resolution.
     * @param value The original value at reference resolution
     * @return The scaled value
     */
    public static int scale(int value) {
        return (int) Math.round(value * scale);
    }
    
    /**
     * Scale a dimension value using a custom scale factor.
     * @param value The original value
     * @param customScale The custom scale factor
     * @return The scaled value
     */
    public static int scale(int value, double customScale) {
        return (int) Math.round(value * customScale);
    }
    
    /**
     * Scale a Dimension object.
     * @param dimension The original dimension
     * @return A new scaled Dimension
     */
    public static Dimension scale(Dimension dimension) {
        return new Dimension(scale(dimension.width), scale(dimension.height));
    }
    
    /**
     * Scale a font size.
     * @param fontSize The original font size
     * @return The scaled font size
     */
    public static int scaleFontSize(int fontSize) {
        return Math.max(8, scale(fontSize)); // Minimum font size of 8
    }
    
    /**
     * Scale a font.
     * @param font The original font
     * @return A new font with scaled size
     */
    public static Font scaleFont(Font font) {
        return font.deriveFont((float) scaleFontSize(font.getSize()));
    }
    
    /**
     * Get the current scale factor.
     * @return The scale factor relative to reference resolution
     */
    public static double getScale() {
        return scale;
    }
    
    /**
     * Get the horizontal scale factor.
     * @return The horizontal scale factor
     */
    public static double getScaleX() {
        return scaleX;
    }
    
    /**
     * Get the vertical scale factor.
     * @return The vertical scale factor
     */
    public static double getScaleY() {
        return scaleY;
    }
    
    /**
     * Center a window on the primary display.
     * @param window The window to center
     */
    public static void centerOnPrimaryDisplay(Window window) {
        window.setLocationRelativeTo(null);
        // Ensure it's on the primary display
        if (primaryDevice != null) {
            window.setLocation(
                primaryDevice.getDefaultConfiguration().getBounds().x + 
                (primaryDevice.getDefaultConfiguration().getBounds().width - window.getWidth()) / 2,
                primaryDevice.getDefaultConfiguration().getBounds().y +
                (primaryDevice.getDefaultConfiguration().getBounds().height - window.getHeight()) / 2
            );
        }
    }
    
    /**
     * Get information about current scaling for debugging.
     * @return A string with scaling information
     */
    public static String getScalingInfo() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return String.format("Screen: %dx%d, Reference: %dx%d, Scale: %.2f (X: %.2f, Y: %.2f)",
            screenSize.width, screenSize.height, 
            REFERENCE_WIDTH, REFERENCE_HEIGHT,
            scale, scaleX, scaleY);
    }
}