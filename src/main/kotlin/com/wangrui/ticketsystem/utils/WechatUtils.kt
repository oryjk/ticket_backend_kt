package com.wangrui.ticketsystem.utils

import java.io.IOException

object WechatUtils {

    fun open(path: String = "C:\\Program Files (x86)\\Tencent\\WeChat\\WeChat.exe"): String {
        return try {
            Runtime.getRuntime().exec(path)
            Runtime.getRuntime().exec(path)
            Runtime.getRuntime().exec(path)
            Runtime.getRuntime().exec(path)
            Runtime.getRuntime().exec(path)
            "WeChat opened"
        } catch (e: IOException) {
            e.printStackTrace()
            "Failed to open WeChat"
        }
    }

    fun kill(){
        try {
            val pb = ProcessBuilder("taskkill", "/F", "/IM", "WeChat.exe")
            val process = pb.start()
            process.waitFor()
        } catch (e: IOException) {
            // Handle exception
        } catch (e: InterruptedException) {
            // Handle exception
        }
    }
}