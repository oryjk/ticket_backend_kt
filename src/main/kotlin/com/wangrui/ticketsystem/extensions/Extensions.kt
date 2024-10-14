package com.wangrui.ticketsystem.extensions

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created on 2024/4/6 09:13.
 * @author wangrui
 * @since 0.0.1
 */

inline fun <reified T> T.slf4k(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}