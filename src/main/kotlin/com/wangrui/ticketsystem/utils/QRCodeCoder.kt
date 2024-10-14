package com.wangrui.ticketsystem.utils

import com.google.zxing.*
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO

object QRCodeCoder {

    fun clickQRCode(x: Int, y: Int) {
        val robot = Robot()

        robot.mouseMove(x, y)

        // 模拟鼠标点击
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
    }

    fun decodeQRCode(file: File): String {
        val image = ImageIO.read(file)
        val source: LuminanceSource = BufferedImageLuminanceSource(image)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        val hints = Hashtable<DecodeHintType, String>()
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8") // 编码设置
        val result = MultiFormatReader().decode(bitmap, hints) // 解析
        return result.text // 返回解析出的字符串
    }

    fun createQRCode(text: String, filePath: String) {
        val hintMap = mapOf(EncodeHintType.CHARACTER_SET to "UTF-8")
        val qrCodeWriter = QRCodeWriter()
        val byteMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200, hintMap)
        val image = BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB)
        val graphics = image.createGraphics()
        graphics.drawImage(image, 0, 0, null)
        for (i in 0 until byteMatrix.width) {
            for (j in 0 until byteMatrix.height) {
                val grayValue = if (byteMatrix[i, j]) 0 else 1
                image.setRGB(i, j, (255 * grayValue shl 16) or (255 * grayValue shl 8) or (255 * grayValue))
            }
        }
        ImageIO.write(image, "png", File(filePath))
    }
}