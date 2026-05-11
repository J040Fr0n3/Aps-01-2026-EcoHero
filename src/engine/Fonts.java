package engine;

import java.awt.Font;
import java.io.InputStream;

public class Fonts {

    private static Font pixelFont;

    static {

        try {

            InputStream is = Fonts.class.getResourceAsStream(
                    "/textures/PixelifySans-Regular.ttf"
            );

            pixelFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    is
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Font getPixelFont(float size) {

        return pixelFont.deriveFont(size);
    }
}
