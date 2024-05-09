package id.slava.nt.cabifymobilechallengeapp.presentation.resource

import android.content.Context
import androidx.annotation.StringRes

interface ResourceProvider {
    fun getString(@StringRes id: Int, vararg formatArgs: Any): String
}

class AndroidResourceProvider(private val context: Context) : ResourceProvider {
    override fun getString(@StringRes id: Int, vararg formatArgs: Any): String {
        return context.getString(id, *formatArgs)
    }
}
