package render.mesh;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ImageLoader {

	public static BufferedImage loadImage(String path) throws IOException {
		InputStream in = ImageLoader.class.getResourceAsStream(path);
		BufferedImage img;
		try {
			img = ImageIO.read(in);
		} catch (IllegalArgumentException e){
			throw new IllegalArgumentException("Could not find " + path);
		}
		
		return img;
	}
	
}
