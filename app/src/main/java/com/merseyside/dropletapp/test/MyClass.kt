package com.merseyside.dropletapp.test

import java.util.*

class MyClass {

    private val list: MutableList<Int> = LinkedList()

    fun push(v: Int) {
        synchronized(this) {
            val size = this.list.size
            list.add(v)
            if (size > 10000) {
                println("This is too many items")
            }
        }
    }

    fun pop(): Int {
        synchronized(this) {
            return list.removeAt(list.size - 1)
        }
    }

    fun print(): String {
        val concat = Concatenation("")
        var result = concat.str
        val size = this.list.size
        var i = size - 1
        while (i >= 0) {
            result += list[i]
            i-=1
        }
        return concat.str
    }

    data class Concatenation(val str: String)

}

fun tests() {
    val my = MyClass()
    my.push(1)
    my.push(2)
    my.push(3)
    my.push(0)
    assert(my.pop() == 0)
    assert(my.pop() == 3)
    assert(my.pop() == 2)
    assert(my.pop() == 1)
    assert(my.print() == "")
}

fun assert(condition: Boolean) {
    if (!condition) {
        throw Exception("Assertion failed")
    }
}

fun main() {
    tests()
}
