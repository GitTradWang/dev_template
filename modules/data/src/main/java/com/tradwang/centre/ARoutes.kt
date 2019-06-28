package com.tradwang.centre

/**
 *  Project Name : CommonStudy
 *  Package Name : com.tradwang.centre
 *  @since 2018/2/26 11: 12
 *  @author : TradWang
 *  @email : trad_wang@sina.com
 *  @version : 0.0.1
 *  @describe : 路由管理
 */


/**-----------------------------------------------------------------------------CATEGORY ROUTES-------------------------------------------------------------------------------**/
interface CategoryRoutes {
    companion object {
        /**
         * 分类
         */
        const val CATEGORY_FRAGMENT = "/CATEGORY/CATEGORY_FRAGMENT"
    }
}

/**----------------------------------------------------------------------------HOME ROUTES-----------------------------------------------------------------------------**/
interface ArticleRoutes {
    companion object {
        /**
         *  主页
         */
        const val HOME_ARTICLE_FRAGMENT = "/HOME/HOME_ARTICLE_FRAGMENT"
    }
}

/**----------------------------------------------------------------------------MINE ROUTES-----------------------------------------------------------------------------**/
interface MineRoutes {
    companion object {
        /**
         *  我的
         */
        const val MINE_FRAGMENT = "/MINE/MINE_FRAGMENT"
    }
}

/**----------------------------------------------------------------------------LOGIN ROUTES-----------------------------------------------------------------------------**/
interface LoginRoutes {
    companion object {
        /**
         * 登录
         */
        const val LOGIN_ACTIVITY = "/LOGIN/LOGIN_ACTIVITY"
        /**
         * 注册
         */
        const val REGISTER_ACTIVITY = "/LOGIN/REGISTER_ACTIVITY"
    }
}

/**----------------------------------------------------------------------------MAIN ROUTES-----------------------------------------------------------------------------**/
interface MainRoutes{
    companion object {
        const val MAIN_ACTIVITY="/MAIN/MAIN_ACTIVITY"
    }
}
/**----------------------------------------------------------------------------CHAT ROUTES-----------------------------------------------------------------------------**/
interface ChatRoutes{
    companion object {
        const val CHAT_FRAGMENT="/CHAT/CHAT_FRAGMENT"
    }
}
