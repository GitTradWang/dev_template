package com.tradwang.common.seachutils

/**
 * @author : TradWang
 * @version :
 * @email : trad_wang@sina.com
 * @describe : 通过汉字获得拼音， 数字和字母不变
 * @since 2018/1/29 14: 53
 */

internal object PinYin {

    fun getPinYin(input: String): String {
        val tokens = HanziToPinyin.instance.get(input)
        val sb = StringBuilder()
        if (tokens.size > 0) {
            for (token in tokens) {
                if (HanziToPinyin.Token.PINYIN == token.type) {
                    sb.append(token.target)
                } else {
                    sb.append(token.source)
                }
            }
        }
        return sb.toString().toLowerCase()
    }
}
