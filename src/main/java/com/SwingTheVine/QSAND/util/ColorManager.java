package com.SwingTheVine.QSAND.util;

import java.awt.Color;

/** Converts between different color types.
 * 
 * @since <b>0.29.0</b>
 * @author <b>SwingTheVine</b> - 1.8.9 source code
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
public class ColorManager {
	
	// Converts color RGB to color Long
	public static int colorRGBToLong(final int[] colorRGB) {
		
		return (colorRGB[0] * 65536) + (colorRGB[1] * 256) + colorRGB[2];
	}
	
	// Converts color RGB to color hex (without "#")
	public static String colorRGBToHex(final int[] colorRGB) {
		
		final Color rgb = new Color(colorRGB[0], colorRGB[1], colorRGB[2]);
		return new String(Integer.toHexString(rgb.getRGB()).substring(2).toUpperCase());
	}
	
	// Converts color Long to color RGB
	public static int[] colorLongToRGB(final long colorLong) {
		
		return new int[] {(int) ((colorLong >> 16) & 0xFF), (int) ((colorLong >> 8) & 0xFF), (int) (colorLong & 0xFF)};
	}
	
	// Converts color Long to color hex (without "#")
	public static String colorLongToHex(final long colorLong) {
		
		return colorRGBToHex(colorLongToRGB(colorLong));
	}
	
	// Converts color hex (without "#") to color RGB
	public static int[] colorHexToRGB(final String colorHex) {
		
		final Color rgb = Color.decode("#" + colorHex);
		return new int[] {rgb.getRed(), rgb.getGreen(), rgb.getBlue()};
	}
	
	// Converts color hex (without "#") to color Long
	public static int colorHexToLong(final String colorHex) {
		
		return colorRGBToLong(colorHexToRGB(colorHex));
	}
}
