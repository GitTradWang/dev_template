package com.tradwang.centre.entry

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import com.tradwang.common.getTimeSpan

/**
 *  Project Name : CommonStudy
 *  Package Name : com.tradwang.homemodule.bean
 *  @since 2018/2/27 10: 25
 *  @author : TradWang
 *  @email : trad_wang@sina.com
 *  @version :
 *  @describe :
 */

data class ArticleListBean(
        val curPage: Int,
        val datas: MutableList<ArticleBean>,
        val offset: Int,
        val over: Boolean,
        val pageCount: Int,
        val size: Int,
        val total: Int
)

@Entity(tableName = "article", indices = [Index(value = ["id"], unique = true)])
data class ArticleBean(
        @PrimaryKey
        val id: Int,
        val apkLink: String,
        val author: String,
        val chapterId: Int,
        val chapterName: String,
        val collect: Boolean,
        val courseId: Int,
        val desc: String,
        val envelopePic: String,
        val link: String,
        val niceDate: String,
        val origin: String,
        val projectLink: String,
        val publishTime: Long,
        val title: String,
        val visible: Int,
        val zan: Int
) {
    fun getFormatPublishTime(): String {
        return publishTime.getTimeSpan()
    }
}


data class TreeRootBean(
        val children: List<Children>,
        val courseId: Int,
        val id: Int,
        val name: String,
        val order: Int,
        val parentChapterId: Int,
        val visible: Int
)

data class Children(
        val children: List<Children>,
        val courseId: Int,
        val id: Int,
        val name: String,
        val order: Int,
        val parentChapterId: Int,
        val visible: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.createTypedArrayList(CREATOR),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(children)
        parcel.writeInt(courseId)
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(order)
        parcel.writeInt(parentChapterId)
        parcel.writeInt(visible)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Children> {
        override fun createFromParcel(parcel: Parcel): Children {
            return Children(parcel)
        }

        override fun newArray(size: Int): Array<Children?> {
            return arrayOfNulls(size)
        }
    }
}

data class BannerBean(
        val desc: String,
        val id: Int,
        val imagePath: String,
        val isVisible: Int,
        val order: Int,
        val title: String,
        val type: Int,
        val url: String
)


data class LoginBean(
        val collectIds: List<Int>,
        val id: Int,
        val type: Int,
        val icon: String,
        val email: String,
        val password: String,
        val username: String
)