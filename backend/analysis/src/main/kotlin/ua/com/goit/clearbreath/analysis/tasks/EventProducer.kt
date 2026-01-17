package ua.com.goit.clearbreath.analysis.tasks

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class EventProducer(private val publisher: ApplicationEventPublisher) {

    fun publishEvent(event: Any) {
        publisher.publishEvent(event)
    }

}