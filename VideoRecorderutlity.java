package reusable;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.Registry;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.monte.media.AudioFormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class VideoRecorderutlity extends ScreenRecorder 
{
	private static final Logger logger =LoggerFactory.getLogger(VideoRecorderutlity.class.getName());
	private static ScreenRecorder screenRecorder;
	private String name;

public VideoRecorderutlity(GraphicsConfiguration cfg, Rectangle captureArea, Format[] formats, File movieFolder, String name)
		throws IOException, AWTException {
	super(cfg, captureArea, formats[0], formats[1], formats[2], formats[3], movieFolder);
	this.name = name;
}

	@Override
	protected File createMovieFile(Format fileFormat) throws IOException {

		if (!movieFolder.exists()) {
			movieFolder.mkdirs();
		} else if (!movieFolder.isDirectory()) {
			throw new IOException("\"" + movieFolder + "\" is not a directory.");
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
		return new File(movieFolder,
				name + "-" + dateFormat.format(new Date()) + "." + Registry.getInstance().getExtension(fileFormat));
	}

	public static void startRecord(String methodName) throws IOException, AWTException{
		File file = new File(""); //Path where vidoe recording would be stored inside project
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = screenSize.width;
		int height = screenSize.height;

		Rectangle captureSize = new Rectangle(0, 0, width, height);

		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().
				getDefaultScreenDevice()
				.getDefaultConfiguration();

		// Define Format objects
		Format fileFormat = new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI);
		Format screenFormat = new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
				CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24, FrameRateKey,
				Rational.valueOf(15), QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60);
		Format mouseFormat = new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30));
		Format audioFormat = null; // Assuming audioFormat is not needed or null


		Format[] formats = { fileFormat, screenFormat, mouseFormat, audioFormat };

		screenRecorder = new VideoRecorderutlity(gc, captureSize, formats, file, methodName);
		screenRecorder.start();
	}

	public static void stopRecord() {
	    try {
	        screenRecorder.stop();
	    } catch (Exception e) {
	    	logger.info("Stop recording:{} ", e.getMessage());
	    }
	}

}