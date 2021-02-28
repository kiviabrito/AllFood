package com.kiviabrito.allfood.utils

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey

/**
 * Class for image caching.
 * The AppGlideModule implementation acts as a signal that allows Glide’s annotation processor...
 * ...to generate a single combined class with with all discovered LibraryGlideModules.
 * @see [Glide](https://bumptech.github.io/glide/doc/configuration.html)
 */

@GlideModule
class AppGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        builder.apply {
            RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .signature(ObjectKey(System.currentTimeMillis().toShort()))
        }
    }

}