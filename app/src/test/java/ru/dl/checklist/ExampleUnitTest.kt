package ru.dl.checklist

class ExampleUnitTest {
   /* @Test
    fun `mark mapping`() {
        val entity = MarkEntity(
            id = 0,
            zoneId = 0,
            points = 0,
            title = "This is a title",
            answer = Answer.YES,
            comment = "Some comment",
            image = null
        )
        val domain = entity.toDomain()
        Timber.i(domain.toString())
        val answerValue = (Answer::value findBy domain.answer.value) ?: Answer.UNDEFINED

        assertAll(
            { assertEquals(true, entity.answer) },
            { assertEquals(Answer.YES, answerValue) }
        )
    }

    @Test
    fun `entity false to model`() {
        val answerDB = false
        val answerModel = (Answer::value findBy answerDB) ?: Answer.UNDEFINED
        Timber.Forest.i(answerModel.toString())
        assertEquals(Answer.NO, answerModel)
    }

    @Test
    fun `entity true to model`() {
        val answerDB = true
        val answerModel = (Answer::value findBy answerDB) ?: Answer.UNDEFINED
        Timber.Forest.i(answerModel.toString())
        assertEquals(Answer.YES, answerModel)
    }

    @Test
    fun `entity null to model`() {
        val answerDB = null
        val answerModel = (Answer::value findBy answerDB) ?: Answer.UNDEFINED
        Timber.Forest.i(answerModel.toString())
        assertEquals(Answer.UNDEFINED, answerModel)
    }*/
}