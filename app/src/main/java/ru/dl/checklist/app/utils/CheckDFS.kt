package ru.dl.checklist.app.utils

import ru.dl.checklist.domain.model.HouseCheckDomain
import java.util.Stack

fun hasOnlyOnePath(checklist: List<HouseCheckDomain>): Boolean {
    val startItem = checklist.firstOrNull { it.start } ?: return false
    val endItem = checklist.firstOrNull { it.end } ?: return false

    // Use a set to keep track of visited items
    val visited = mutableSetOf<HouseCheckDomain>()

    // Use a stack to perform depth-first search
    val stack = Stack<HouseCheckDomain>()
    stack.push(startItem)

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

    // Check if we have visited all items in the list
    return visited.size == checklist.size
}