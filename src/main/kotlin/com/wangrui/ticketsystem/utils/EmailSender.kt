package com.wangrui.ticketsystem.utils

import com.wangrui.ticketsystem.extensions.slf4k
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailSender {
    private val logger=slf4k()
    fun sendEmail(to: List<String>, subject: String, content: String) {
        val from = "oryjk@qq.com"  // 你的QQ邮箱
        val host = "smtp.qq.com"  // QQ的SMTP服务器

        val properties = Properties().apply {
            put("mail.smtp.host", host)
            put("mail.smtp.port", "587")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")  // QQ邮箱需要开启这个

        }
        properties.setProperty("mail.transport.protocol", "smtp")
        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication() =
                PasswordAuthentication(from, "pzdduayopvywgjig")  // 使用QQ邮箱和SMTP生成的授权码
        })

        try {
            to.forEach {
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(from))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(it))
                    setSubject(subject)  // 邮件标题
                    setText(content)  // 邮件内容
                }

                Transport.send(message)
                logger.info("发送提醒邮件给 $to")
            }



        } catch (messagingException: MessagingException) {
            messagingException.printStackTrace()
        }
    }
}