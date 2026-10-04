package com.top10.deals.domain.usecase

import com.top10.deals.domain.model.Store
import com.top10.deals.domain.model.hasEnded
import com.top10.deals.domain.repository.PublishedTopLists
import kotlinx.datetime.LocalDate

/**
 * Reads every published top list and deletes, one by one, those that have ended on [today]'s
 * date. Gives the stores whose list was deleted.
 */
class DeleteEndedTopListsUseCase(
    private val lists: PublishedTopLists,
    private val today: () -> LocalDate,
) {
    suspend operator fun invoke(): List<Store> {
        val today = today()
        return lists.all()
            .filter { it.hasEnded(today) }
            .map { top ->
                lists.delete(top.store.id)
                top.store
            }
    }
}
