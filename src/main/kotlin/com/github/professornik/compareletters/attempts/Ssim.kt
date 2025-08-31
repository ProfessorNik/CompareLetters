package com.github.professornik.compareletters.attempts

import io.github.t12y.ssim.SSIM
import io.github.t12y.ssim.models.Matrix
import io.github.t12y.ssim.models.Options
import com.github.professornik.compareletters.renderGlyph
import java.awt.image.BufferedImage

fun ssim(text1: String, text2: String): Double {
    val image1 = renderGlyph(text1)
    val image2 = renderGlyph(text2)

    val ssimMatrix = SSIM.ssim(
        image1.toMatrix(),
        image2.toMatrix(),
        options()
    )
    return ssimMatrix.mssim
}

fun BufferedImage.toMatrix() = Matrix(
    this.height,
    this.width,
    unpackPixels(this.getRGB(0, 0, width, height, null, 0, width))
)


fun unpackPixels(packed: IntArray): DoubleArray {
    val packedLength = packed.size
    val unpacked = DoubleArray(packedLength * 4)
    var unpackedIndex: Int
    var packedPixel: Int

    for (i in 0..<packedLength) {
        packedPixel = packed[i]
        unpackedIndex = i * 4

        unpacked[unpackedIndex] = (0xff and (packedPixel shr 16)).toDouble()
        unpacked[unpackedIndex + 1] = (0xff and (packedPixel shr 8)).toDouble()
        unpacked[unpackedIndex + 2] = (0xff and packedPixel).toDouble()
        unpacked[unpackedIndex + 3] = (0xff and (packedPixel ushr 24)).toDouble()
    }

    return unpacked
}

fun options(): Options = Options.Defaults().apply {
    this.ssim = Options.SSIMImpl.WEBER
    this.rgb2grayVersion = Options.RGB2Gray.ORIGINAL
}