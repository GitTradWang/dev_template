package com.tradwang.common.seachutils

import android.text.TextUtils
import java.util.*
import java.util.regex.Pattern

/**
 * @since 2018/1/29 14: 53
 * @author : TradWang
 * @email : trad_wang@sina.com
 * @version :
 * @describe :
 */
object SearchUtils {

    /**
     * 按数字-拼音搜索联系人
     *
     * @param str        the 目标字符串
     * @param strings the 目标数据集
     * @return the array list  匹配结果
     */
    fun search(str: String, strings: ArrayList<String>): ArrayList<String> {
        val contactList = ArrayList<String>()
        // 如果搜索条件以0 1 +开头则按号码搜索
        if (str.startsWith("+") || str.startsWith("1") || str.startsWith("2") || str.startsWith("3") || str.startsWith("4") || str.startsWith("5") || str.startsWith("6") || str.startsWith("7") || str.startsWith("8") || str.startsWith("9") || str.startsWith("0")) {
            strings.filterTo(contactList) { it.contains(str) }
            return contactList
        }

        val result = PinYin.getPinYin(str)
        strings.filterTo(contactList) { contains(it, result) }
        return contactList
    }

    /**
     * 根据拼音搜索
     *
     * @param target 匹配目标
     * @param search 目标字段
     * @return boolean
     */
    private fun contains(target: String, search: String): Boolean {

        if (TextUtils.isEmpty(target) || TextUtils.isEmpty(search)) {
            return false
        }
        var flag = false

        // 简拼匹配,如果输入在字符串长度大于6就不按首字母匹配了
        if (search.length < 6) {
            // 获得首字母字符串
            val firstLetters = UnicodeGBK2Alpha.getSimpleCharsOfStringByTrim(target)
            // 不区分大小写
            val firstLetterMatcher = Pattern.compile("^" + search, Pattern.CASE_INSENSITIVE)
            flag = firstLetterMatcher.matcher(firstLetters).find()
        }
        if (!flag) { // 如果简拼已经找到了，就不使用全拼了
            // 不区分大小写
            val pattern2 = Pattern.compile(search, Pattern.CASE_INSENSITIVE)
            val matcher2 = pattern2.matcher(PinYin.getPinYin(target))
            flag = matcher2.find()
        }
        return flag
    }
}
