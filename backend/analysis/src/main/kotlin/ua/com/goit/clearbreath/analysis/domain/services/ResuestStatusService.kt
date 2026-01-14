package ua.com.goit.clearbreath.analysis.domain.services

import ua.com.goit.clearbreath.analysis.model.RequestStatus

interface ResuestStatusService {
    fun setStatus(requestId: String, status: RequestStatus)
}