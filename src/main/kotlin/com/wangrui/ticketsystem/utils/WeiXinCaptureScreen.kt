package com.wangrui.ticketsystem.utils

import java.awt.Rectangle
import java.awt.Robot
import java.io.File
import javax.imageio.ImageIO

object WeiXinCaptureScreen {
    fun captureScreen(x: Int, y: Int, width: Int, height: Int, filePath: String) {
        val screenRect = Rectangle(x, y, width, height)
        val capture = Robot().createScreenCapture(screenRect)
        ImageIO.write(capture, "jpg", File(filePath))
    }

}