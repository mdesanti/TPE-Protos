package ar.edu.it.itba.pdc.Implementations.proxy.utils;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.imageio.ImageIO;

public class Transformations {

	public static void main(String[] args) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(
				"/Users/egpintos17/Pictures/Celular/DSC00002.JPG"));
		Transformations test = new Transformations();
		byte[] byteArray = test.rotate(is, 180);

		// Probando si se forma bien.
		String fileName = "/Users/egpintos17/Pictures/Celular/DSC00002_R.JPG";
		FileOutputStream fw = new FileOutputStream(fileName, true);
		fw.write(byteArray);
		fw.close();

		String data = "aeio hola chau como estas lala hola lala lele";
		System.out.println(data);
		System.out.println(new String(test.transformL33t(data.getBytes())));

	}

	public synchronized byte[]  rotate(InputStream original, Integer degrees) {
		BufferedImage originalImage = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			originalImage = ImageIO.read(original);
		} catch (IOException e) {
			e.printStackTrace();
		}

		AffineTransform affineTransform = new AffineTransform();
		affineTransform.rotate(Math.toRadians(degrees),
				originalImage.getWidth() / 2, originalImage.getHeight() / 2);

		AffineTransformOp opRotated = new AffineTransformOp(affineTransform,
				AffineTransformOp.TYPE_BILINEAR);
		BufferedImage newImage = opRotated.filter(originalImage, null);

		try {
			ImageIO.write(newImage, "png", out);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	public byte[] transformL33t(byte[] bytes)
			throws UnsupportedEncodingException {
		String data = new String(bytes);
		String data1 = data.replace('a', '4');
		String data2 = data1.replace('e', '3');
		String data3 = data2.replace('i', '1');
		String data4 = data3.replace('o', '0');
		return data4.getBytes();
	}
}
