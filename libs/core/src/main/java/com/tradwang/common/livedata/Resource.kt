package com.tradwang.common.livedata

/**
 * ProjectName   CommonStudy
 * PackageName  com.tradwang.centre
 * @author  tradwang
 * @since   18-9-11   上午9:24.
 * @email  wangxiaojun@bianla.cn
 * @version  0.1
 * @describe  LiveData数据封装类
 */
class Resource<D> private constructor(val status: STATUS, val data: D?, val message: String?) {

    companion object {
        /**
         * 成功
         * @param data D  数据源
         * @return Resource<D> LiveData数据封装类
         */
        fun <D> success(data: D?): Resource<D> {
            return Resource(STATUS.SUCCESS, data, null)
        }

        /**
         * 失败
         * @param data D?  数据源 ,可能为空
         * @param message String 失败的消息
         * @return Resource<D>  LiveData数据封装类
         */
        fun <D> error(message: String, data: D? = null): Resource<D> {
            return Resource(STATUS.ERROR, data, message)
        }

        /**
         * 加载中
         * @param data D? 数据源 ,可能为空
         * @return Resource<D> LiveData数据封装类
         */
        fun <D> loading(data: D? = null): Resource<D> {
            return Resource(STATUS.LOADING, data, null)
        }

        /**
         * 空数据
         * @param data D? 数据源 ,可能为空
         * @return Resource<D> Resource<D> LiveData数据封装类
         */
        fun <D> empty(data: D? = null): Resource<D> {
            return Resource(STATUS.EMPTY, data, null)
        }
    }

    enum class STATUS {
        LOADING,
        SUCCESS,
        ERROR,
        EMPTY;
    }
}

