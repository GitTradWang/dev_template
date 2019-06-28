package com.tradwang.centre.base.api

import com.tradwang.centre.entry.*
import com.tradwang.common.base.net.ApiFactory
import io.reactivex.Observable
import retrofit2.http.*

/**
 *  Project Name : CommonStudy
 *  Package Name : com.tradwang.homemodule.api
 *  @since 2018/2/27 10: 06
 *  @author : TradWang
 *  @email : trad_wang@sina.com
 *  @version :
 *  @describe :
 */
object WebService {

    val netApi = ApiFactory.getRetrofit().create(NetApi::class.java)!!

    fun getHomeArticleList(page: Int, cId: Int?): Observable<BaseBean<ArticleListBean>> {
        return if (cId == null) {
            netApi.getHomeArticleList(page)
        } else {
            netApi.getHomeArticleList(page, cId)
        }
    }

    fun getHomeArticleBanner(): Observable<BaseBean<MutableList<BannerBean>>> {
        return netApi.getHomeArticleBanner()
    }

    fun getCategoryTree(): Observable<BaseBean<List<TreeRootBean>>> {
        return netApi.getCategoryTree()
    }

    interface NetApi {
        /**
         * 首页文章列表
         * @param page ") page: Int 分页数
         * @return Observable<BaseBean<ArticleListBean>> 文章列表
         */
        @GET("article/list/{page}/json")
        fun getHomeArticleList(@Path("page") page: Int): Observable<BaseBean<ArticleListBean>>

        /**
         * 首页banner
         * @return Observable<BaseBean<MutableList<BannerBean>>> banner
         */
        @GET("banner/json")
        fun getHomeArticleBanner(): Observable<BaseBean<MutableList<BannerBean>>>

        /**
         * 体系结构
         * @return Observable<BaseBean<List<TreeRootBean>>>
         */
        @GET("tree/json")
        fun getCategoryTree(): Observable<BaseBean<List<TreeRootBean>>>

        /**
         * 体系下的文章列表
         * @param page ") page: Int 分页数
         * @param cId Int 体系Id
         * @return Observable<BaseBean<ArticleListBean>> 文章列表
         */
        @GET("article/list/{page}/json")
        fun getHomeArticleList(@Path("page") page: Int, @Query("cid") cId: Int): Observable<BaseBean<ArticleListBean>>

        /**
         * 收藏站内文章
         * @param id:Int 文章的id
         * @return Observable<BaseBean<Any>> 返回数据结果
         */
        @POST("lg/collect/{id}/json")
        fun collectArticle(@Path("id") id: Int): Observable<BaseBean<Any>>

        /**
         *取消收藏站内文章
         * @param id: Int 文章的id 文章的id
         * @return Observable<BaseBean<Any>> 返回数据结果
         */
        @POST("lg/uncollect_originId/{id}/json")
        fun uncollectArticle(@Path("id") id: Int): Observable<BaseBean<Any>>

        /**
         * 收藏自己的文章
         * @param title: String
         * @param author: String
         * @param  link: String
         * @return Observable<BaseBean<Any>>
         */
        @POST("lg/collect/add/json")
        @FormUrlEncoded
        fun collectUserArticle(@Field("title") title: String, @Field("author") author: String, @Field("link") link: String): Observable<BaseBean<Any>>

        /**
         * 登录接口
         * @param userName String 用户名
         * @param password ") password: String 密码
         * @return Observable<BaseBean<LoginBean>> 登录结果
         */
        @POST("user/login")
        @FormUrlEncoded
        fun login(@Field("username") userName: String, @Field("password") password: String): Observable<BaseBean<LoginBean>>

        /**
         *注册接口
         * @param userName String 用户名
         * @param password ") password: String 密码
         * @param repassword ") repassword: String 重复的密码
         */
        @POST("user/register")
        @FormUrlEncoded
        fun register(@Field("username") userName: String, @Field("password") password: String, @Field("repassword") repassword: String): Observable<BaseBean<LoginBean>>
    }
}