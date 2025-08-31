package com.github.professornik.attempts

import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.core.MatOfDouble
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import com.github.professornik.renderGlyph
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.sign

fun huMoments(text1: String, text2: String): Double {
    val imgColor1 = renderGlyph(text1).toMat()
    val imgColor2 = renderGlyph(text2).toMat()

    if (imgColor1.empty() || imgColor2.empty()) {
       throw IllegalStateException("Не удалось загрузить одно из изображений.")
    }

    val img1 = Mat()
    val img2 = Mat()
    Imgproc.cvtColor(imgColor1, img1, Imgproc.COLOR_BGR2GRAY)
    Imgproc.cvtColor(imgColor2, img2, Imgproc.COLOR_BGR2GRAY)

    val bin1 = Mat()
    val bin2 = Mat()
    Imgproc.threshold(img1, bin1, 0.0, 255.0, Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU)
    Imgproc.threshold(img2, bin2, 0.0, 255.0, Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU)
    
    return huDistance(getHuMoments(bin1), getHuMoments(bin2))
}

fun getHuMoments(image: Mat): DoubleArray {
    val moments = Imgproc.moments(image)
    val hu = MatOfDouble()
    Imgproc.HuMoments(moments, hu)
    return hu.toArray()
}

fun huDistance(hu1: DoubleArray, hu2: DoubleArray) =
    (0..<hu1.size).fold(0.0) { sum, i ->
        val v1 = sign(hu1[i]) * log10(abs(hu1[i]))
        val v2 = sign(hu2[i]) * log10(abs(hu2[i]))
        sum + abs(v1 - v2)
    }


fun BufferedImage.toMat(): Mat {
    val stream = ByteArrayOutputStream()
    ImageIO.write(this, "jpg", stream)
    stream.flush()
    return Imgcodecs.imdecode(MatOfByte(*stream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED)
}

