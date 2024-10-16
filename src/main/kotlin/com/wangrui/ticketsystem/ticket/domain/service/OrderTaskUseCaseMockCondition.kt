package com.wangrui.ticketsystem.ticket.domain.service

import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.core.type.AnnotatedTypeMetadata

class OrderTaskUseCaseMockCondition : Condition {
    override fun matches(context: ConditionContext, metadata: AnnotatedTypeMetadata): Boolean {
        val environment = context.environment
        return environment.getProperty("order.type") == "mock"
    }
}

class OrderTaskUseCaseCondition : Condition {
    override fun matches(context: ConditionContext, metadata: AnnotatedTypeMetadata): Boolean {
        val environment = context.environment
        return environment.getProperty("order.type") != "mock"
    }

}