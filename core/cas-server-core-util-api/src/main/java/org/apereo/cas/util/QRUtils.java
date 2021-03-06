package org.apereo.cas.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * This is {@link QRUtils}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
@UtilityClass
public class QRUtils {

    /**
     * Large width size.
     */
    public static final int WIDTH_LARGE = 250;

    /**
     * Medium width size.
     */
    public static final int WIDTH_MEDIUM = 125;

    /**
     * Generate qr code.
     *
     * @param stream the stream
     * @param key    the key
     * @param width  the width
     * @param height the height
     */
    @SneakyThrows
    public static void generateQRCode(final OutputStream stream, final String key,
                                      final int width, final int height) {
        final Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
        hintMap.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
        hintMap.put(EncodeHintType.MARGIN, 2);
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        final QRCodeWriter qrCodeWriter = new QRCodeWriter();
        final BitMatrix byteMatrix = qrCodeWriter.encode(key, BarcodeFormat.QR_CODE, width, height, hintMap);
        final int byteMatrixWidth = byteMatrix.getWidth();
        final BufferedImage image = new BufferedImage(byteMatrixWidth, byteMatrixWidth, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        @Cleanup("dispose")
        final Graphics2D graphics = (Graphics2D) image.getGraphics();

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, byteMatrixWidth, byteMatrixWidth);
        graphics.setColor(Color.BLACK);

        IntStream.range(0, byteMatrixWidth)
            .forEach(i -> IntStream.range(0, byteMatrixWidth)
                .filter(j -> byteMatrix.get(i, j))
                .forEach(j -> graphics.fillRect(i, j, 1, 1)));

        ImageIO.write(image, "png", stream);
    }

}
