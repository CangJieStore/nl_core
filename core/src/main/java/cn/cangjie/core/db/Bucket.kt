package cn.cangjie.core.db

class Bucket(val name: String, val relativePath: String? = null)

object BucketFactory {
    val Global = Bucket("global")
}
