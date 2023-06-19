package ru.dl.checklist

import org.junit.Test
import org.junit.jupiter.api.Assertions
import ru.dl.checklist.domain.model.HouseCheckDomain
import java.util.Stack


class ExampleUnitTest {
    val list = listOf<HouseCheckDomain>(
        HouseCheckDomain(1, "1", "name1", true, false, "3", false, false),
        HouseCheckDomain(2, "2", "name2", false, false, "5", false, false),
        HouseCheckDomain(3, "3", "name3", false, false, "2", false, false),
        HouseCheckDomain(4, "4", "name4", false, false, "6", false, false),
        HouseCheckDomain(5, "5", "name5", false, false, "4", false, false),
        HouseCheckDomain(6, "6", "name6", false, false, "7", false, false),
        HouseCheckDomain(7, "7", "name7", false, false, "8", false, false),
        HouseCheckDomain(8, "8", "name8", false, true, "", false, false),

        )

    @Test
    fun `test for shuffle`() {
        val checklist = list.shuffled()
        println(checklist.toString())
        val newList = mutableListOf<HouseCheckDomain>()
        val startItem = checklist.firstOrNull { it.start } ?: return Assertions.assertFalse(true)
        val endItem = checklist.firstOrNull { it.end } ?: return Assertions.assertFalse(true)

        val stack = Stack<HouseCheckDomain>()
        stack.push(startItem)
        val visited = mutableSetOf<HouseCheckDomain>()
        // Perform depth-first search
        while (stack.isNotEmpty()) {
            val currentItem = stack.pop()

            // Check if we have already visited this item
            if (currentItem in visited) {
                continue
            }
            visited.add(currentItem)

            // Check if we have reached the end item
            if (currentItem == endItem) {
                continue
            }

            // Add the next item(s) to the stack for further traversal
            val nextUuid = currentItem.next
            if (nextUuid != null) {
                val nextItem = checklist.find { it.uuid == nextUuid } ?: continue
                stack.push(nextItem)
            }
        }
        visited.forEach {
            newList.add(it)
        }
        println("visited size = ${visited.size}")
        println("Новый список")
        println(newList.toString())
        val endItemNew = newList.take(1).firstOrNull { it.end==false }
        println(endItem.toString())
        Assertions.assertNull(endItemNew)
        //?: return Assertions.assertFalse(true)
        //  Assertions.assertEquals(true, true)
    }
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