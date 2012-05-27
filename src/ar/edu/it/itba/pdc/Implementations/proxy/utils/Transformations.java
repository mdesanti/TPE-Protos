package ar.edu.it.itba.pdc.Implementations.proxy.utils;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.imageio.ImageIO;

public class Transformations {

	public synchronized byte[] rotate(String fileName, Integer degrees) {
		File file = new File(fileName);
		BufferedImage originalImage = null;
		try {
			originalImage = ImageIO.read(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AffineTransform affineTransform = new AffineTransform();
		// You could use Math.PI / 2, depends on your input.
		affineTransform.rotate(Math.toRadians(degrees),
				originalImage.getWidth() / 2, originalImage.getHeight() / 2);

		// Now lets make that transform an operation, and we use it.
		AffineTransformOp opRotated = new AffineTransformOp(affineTransform,
				AffineTransformOp.TYPE_BILINEAR);
		BufferedImage newImage = null;
		// try{
		if (originalImage.getHeight() == 1 || originalImage.getWidth() == 1) {
			newImage = originalImage;
		} else
			newImage = opRotated.filter(originalImage, null);
		// }catch( java.awt.image.RasterFormatException e){
		//
		// System.out.println("TIRO EXCEPCTION EN ARCHIVO:"+fileName);
		// }
		// System.out.println(originalImage.getWidth());
		// System.out.println(originalImage.getHeight());
		// System.out.println(originalImage.getMinX());
		// System.out.println(originalImage.getMinTileY());

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// Save the image.
		try {
			ImageIO.write(newImage, "png", out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out.toByteArray();
		// BufferedImage originalImage = null;
		// ByteArrayOutputStream out = new ByteArrayOutputStream();
		// try {
		// originalImage = ImageIO.read(original);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		//
		// AffineTransform affineTransform = new AffineTransform();
		// affineTransform.rotate(Math.toRadians(degrees),
		// originalImage.getWidth() / 2, originalImage.getHeight() / 2);
		//
		// AffineTransformOp opRotated = new AffineTransformOp(affineTransform,
		// AffineTransformOp.TYPE_BILINEAR);
		// BufferedImage newImage = null;
		// newImage = opRotated.filter(originalImage, newImage);
		//
		// try {
		// ImageIO.write(newImage, "png", out);
		//
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// return out.toByteArray();
	}

	public synchronized byte[] transformL33t(InputStream original)
			throws UnsupportedEncodingException {
		BufferedReader br = new BufferedReader(new InputStreamReader(original));

		StringBuilder sb = new StringBuilder();

		String line;
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String data = new String(sb);
		String data1 = data.replace('a', '4');
		String data2 = data1.replace('e', '3');
		String data3 = data2.replace('i', '1');
		String data4 = data3.replace('o', '0');
		return data4.getBytes();
	}
}
