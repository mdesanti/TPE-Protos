package ar.edu.it.itba.pdc.proxy.implementations.utils;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.imageio.ImageIO;

public class Transformations {

	public synchronized byte[] rotate(String fileName, Integer degrees)
			throws IOException {
		File file = null;
		try {
			file = new File(fileName);
			BufferedImage originalImage = null;
			originalImage = ImageIO.read(file);
			AffineTransform affineTransform = new AffineTransform();
			affineTransform
					.rotate(Math.toRadians(degrees),
							originalImage.getWidth() / 2,
							originalImage.getHeight() / 2);

			AffineTransformOp opRotated = new AffineTransformOp(
					affineTransform, AffineTransformOp.TYPE_BILINEAR);
			BufferedImage newImage = null;

			// If 0x0 image
			if (originalImage.getHeight() == 1 || originalImage.getWidth() == 1) {
				newImage = originalImage;
			} else
				newImage = opRotated.filter(originalImage, null);

			String[] path = fileName.split("/");
			File fileNew = new File("/tmp/prueba/R" + path[3]);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(newImage, "png", fileNew);
			ImageIO.write(newImage, "png", out);
			return out.toByteArray();
		} catch (Exception e) {
			return null;
		}

	}

	public synchronized byte[] transformL33t(InputStream original)
			throws UnsupportedEncodingException {
		BufferedReader br = new BufferedReader(new InputStreamReader(original));
		StringBuilder sb = new StringBuilder();
		char[] buf = new char[1024];
		int numRead=0;
		try {
			while((numRead=br.read(buf)) != -1){
			String readData = String.valueOf(buf, 0, numRead);
			sb.append(readData);
			buf = new char[1024];
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String data = sb.toString();
		String data1 = data.replace('a', '4');
		String data2 = data1.replace('e', '3');
		String data3 = data2.replace('i', '1');
		String data4 = data3.replace('o', '0');
		return data4.getBytes();
	}
}
