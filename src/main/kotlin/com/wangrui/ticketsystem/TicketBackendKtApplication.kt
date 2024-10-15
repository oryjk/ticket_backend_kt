package com.wangrui.ticketsystem

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class TicketBackendKtApplication

fun main(args: Array<String>) {
    runApplication<TicketBackendKtApplication>(*args)
}
