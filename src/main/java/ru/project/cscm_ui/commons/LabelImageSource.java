package ru.project.cscm_ui.commons;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.vaadin.server.StreamResource.StreamSource;

public class LabelImageSource implements StreamSource {

	private static final long serialVersionUID = 1203128308462371280L;

	private ByteArrayOutputStream imagebuffer;

	@Override
	public InputStream getStream() {
		try {
			final BufferedImage image = ImageIO.read(LabelImageSource.class.getClassLoader()
					.getResourceAsStream("images/bpc_white.png"));

			imagebuffer = new ByteArrayOutputStream();
			ImageIO.write(image, "png", imagebuffer);
			return new ByteArrayInputStream(imagebuffer.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}