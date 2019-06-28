package com.tradwang.commonstudy

import org.junit.Test

/**
 * ProjectName   CommonStudy
 * PackageName  com.tradwang.commonstudy
 * @author  tradwang
 * @since   18-10-11   下午6:49.
 * @email  wangxiaojun@bianla.cn
 * @version  0.1
 * @describe  TODO
 */
class KotlinReflect {
    @Test
    fun test() {
        Demo(Demo::class).test()
    }

}

class Demo(private val any: Any) {
    fun test() {
        ::any.parameters.forEach { println(it) }
        ::any.typeParameters.forEach { println(it) }
        println(::any.returnType)
        ::any.annotations.forEach { println(it) }
    }
}