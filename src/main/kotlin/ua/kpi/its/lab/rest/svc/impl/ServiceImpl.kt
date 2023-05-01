package ua.kpi.its.lab.rest.svc.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ua.kpi.its.lab.rest.dto.JournalRequest
import ua.kpi.its.lab.rest.dto.JournalResponse
import ua.kpi.its.lab.rest.dto.ScientificArticleRequest
import ua.kpi.its.lab.rest.dto.ScientificArticleResponse
import ua.kpi.its.lab.rest.entity.Journal
import ua.kpi.its.lab.rest.entity.ScientificArticle
import ua.kpi.its.lab.rest.repository.JournalRepository
import ua.kpi.its.lab.rest.repository.ScientificArticleRepository
import ua.kpi.its.lab.rest.svc.JournalService
import ua.kpi.its.lab.rest.svc.ScientificArticleService

@Service
class JournalServiceImpl @Autowired constructor (
    private val journalRepository: JournalRepository,
    private val scientificArticleRepository: ScientificArticleRepository
): JournalService {
    @Transactional
    override fun addJournal(request: JournalRequest): JournalResponse {
        val list = mutableListOf<ScientificArticle>()
        val entity = Journal(name = request.name, foundingDate = request.foundingDate, topic = request.topic, language = request.language, issn = request.issn, price = request.price, isPeriodic = request.isPeriodic, articlesList = list)
        val result = journalRepository.save(entity)
        return JournalResponse(result.id, result.name, result.topic, result.price)
    }
    @Transactional
    override fun getAllJournals(): MutableList<Journal> {
        return journalRepository.findAll()
    }
    @Transactional
    override fun deleteJournalById(id: Long) {
        journalRepository.deleteById(id)
    }
    @Transactional
    override fun addArticleToJournal(journalId: Long, articleID: Long): Journal? {
        val journal = journalRepository.findById(journalId).orElse(null)
        return if (journal != null) {
            val article = scientificArticleRepository.findById(articleID).orElse(null)
            if (article != null) {
                journal.articlesList.add(article)
                article.journal = journal
                scientificArticleRepository.save(article)
                journalRepository.save(journal)
            } else {
                null
            }
        } else {
            null
        }
    }
}

@Service
class ScientificArticleServiceImpl @Autowired constructor (
    private val scientificArticleRepository: ScientificArticleRepository
): ScientificArticleService {

    override fun addScientificArticle(request: ScientificArticleRequest): ScientificArticleResponse {
        val entity = ScientificArticle(title = request.title, author = request.author, dateWritten = request.dateWritten, wordCount = request.wordCount, referenceCount = request.referenceCount, originalLanguage = request.originalLanguage)
        val result = scientificArticleRepository.save(entity)
        return ScientificArticleResponse(result.id, result.title, result.author)
    }

    override fun getAllScientificArticles(): MutableList<ScientificArticle> {
        return scientificArticleRepository.findAll()
    }

    override fun deleteScientificArticleById(id: Long) {
        scientificArticleRepository.deleteById(id)
    }

    override fun getScientificArticleById(id: Long): ScientificArticleResponse {
        val result = scientificArticleRepository.findById(id).orElse(null)
        return ScientificArticleResponse(result.id, result.title, result.author)
    }
}