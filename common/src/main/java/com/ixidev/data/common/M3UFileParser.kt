package com.ixidev.data.common

import android.util.Patterns
import com.ixidev.data.model.MovieItem
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * Author  : ABDELMAJID ID ALI
 * on      : 24/09/2020
 * Email   : abdelmajid.idali@gmail.com
 * Github  : https://github.com/ixiDev
 */


@Suppress("unused")
object M3UFileParser {

    @Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
    annotation class M3UAttribute

    private const val INFO_PREFIX = "#EXTINF"
    private val META_DATA_PATTERN = "((\\S+?)=\"(.+?)\")".toRegex()
    private val URL_REGEX = Patterns.WEB_URL.toRegex()

    @M3UAttribute
    const val TVG_NAME = "tvg-name"

    @M3UAttribute
    const val GROUP_TITLE = "group-title"

    @M3UAttribute
    const val TVG_LOGO = "tvg-logo"

    @M3UAttribute
    const val TVG_URL = "tvg-url"

/*

    fun parseItems(file: File, onParseItem: (item: M3UEntry) -> Unit) {
        var tmpItem = M3UEntry()
        file.forEachLine { line ->
            if (line.isNotEmpty()) {
                when {
                    line.startsWith(INFO_PREFIX) -> {
                        // pars attributes
                        tmpItem.attributes = parseAttributes(line)
                    }
                    line.trim().matches(URL_REGEX) -> {
                        // parse url
                        tmpItem.url = line.trim()
                        onParseItem(tmpItem) // item parsed
                        tmpItem = M3UEntry()
                    }
                }

            }
        }
    }
*/

    /*   suspend fun parseItemsAsync(file: File, onParseItem: suspend (item: M3UEntry) -> Unit) =
           withContext(Dispatchers.IO) {
               var tmpItem = M3UEntry()
               val lines = withContext(Dispatchers.Default) { file.readLines() }
               lines.forEach { line ->
                   if (line.isNotEmpty()) {
                       when {
                           line.startsWith(INFO_PREFIX) -> {
                               // pars attributes
                               tmpItem.attributes = parseAttributes(line)
                           }
                           line.trim().matches(URL_REGEX) -> {
                               // parse url
                               tmpItem.url = line.trim()
                               onParseItem(tmpItem) // item parsed
                               tmpItem = M3UEntry()
                           }
                       }

                   }
               }
           }

   */
     fun parseMovies(fileId: Int, file: File): List<MovieItem> {
        val items = ArrayList<MovieItem>()
        var tmpItem = MovieItem()
        file.forEachLine { line ->
            if (line.isNotEmpty()) {
                when {
                    line.startsWith(INFO_PREFIX) -> {
                        // pars attributes
                        val attributes = parseAttributes(line)
                        tmpItem.title = attributes[TVG_NAME]
                        tmpItem.categorie = attributes[GROUP_TITLE]
                        tmpItem.thumbnail = attributes[TVG_LOGO]
                    }
                    line.trim().matches(URL_REGEX) -> {
                        // parse url
                        tmpItem.sourceUrl = line.trim()
                        tmpItem.listId = fileId
                        items.add(tmpItem) // item parsed
                        tmpItem = MovieItem(0)
                    }
                }

            }
        }
        return items
    }


    fun parseMovies(
        fileId: Int,
        file: File,
        onParseMovie: (movie: MovieItem) -> Unit
    ) {
        var tmpItem = MovieItem()
        file.forEachLine { line ->
            if (line.isNotEmpty()) {
                when {
                    line.startsWith(INFO_PREFIX) -> {
                        // pars attributes
                        val attributes = parseAttributes(line)
                        tmpItem.title = attributes[TVG_NAME]
                        tmpItem.categorie = attributes[GROUP_TITLE]
                        tmpItem.thumbnail = attributes[TVG_LOGO]
                    }
                    line.trim().matches(URL_REGEX) -> {
                        // parse url
                        tmpItem.sourceUrl = line.trim()
                        tmpItem.listId = fileId
                        // items.add(tmpItem) // item parsed
                        onParseMovie(tmpItem)
                        tmpItem = MovieItem(0)
                    }
                }

            }
        }

    }

//
//    suspend fun parseChannelsAsync(fileid: Int, file: File): List<ChannelItem> =
//        withContext(Dispatchers.IO) {
//            val items = ArrayList<ChannelItem>()
//            var tmpItem = ChannelItem(0, fileid, false)
//            val lines = withContext(Dispatchers.IO) { file.readLines() }
//            lines.forEach { line ->
//                if (line.isNotEmpty()) {
//                    when {
//                        line.startsWith(INFO_PREFIX) -> {
//                            // pars attributes
//                            val attributes = parseAttributes(line)
//                            tmpItem.channelName = attributes[TVG_NAME]
//                            tmpItem.groupTitle = attributes[GROUP_TITLE]
//                            tmpItem.logoURL = attributes[TVG_LOGO]
//                        }
//                        line.trim().matches(URL_REGEX) -> {
//                            // parse url
//                            tmpItem.streamURL = line.trim()
//                            items.add(tmpItem) // item parsed
//                            tmpItem = ChannelItem(0, fileid, false)
//                        }
//                    }
//
//                }
//            }
//            items
//        }
//
//    suspend fun parseChannelsItems(
//        fileid: Int,
//        file: File,
//        onParseItem: suspend (item: ChannelItem) -> Unit
//    ) = withContext(Dispatchers.IO) {
//        var tmpItem = ChannelItem(0, fileid, false)
//        val lines = withContext(Dispatchers.IO) { file.readLines() }
//        lines.forEach { line ->
//            if (line.isNotEmpty()) {
//                when {
//                    line.startsWith(INFO_PREFIX) -> {
//                        // pars attributes
//                        val attributes = parseAttributes(line)
//                        tmpItem.channelName = attributes[TVG_NAME]
//                        tmpItem.groupTitle = attributes[GROUP_TITLE]
//                        tmpItem.logoURL = attributes[TVG_LOGO]
//                    }
//                    line.trim().matches(URL_REGEX) -> {
//                        // parse url
//                        tmpItem.streamURL = line.trim()
//                        onParseItem(tmpItem) // item parsed
//                        tmpItem = ChannelItem(0, fileid, false)
//                    }
//                }
//
//            }
//        }
//    }

    private fun parseAttributes(line: String): HashMap<String?, String?> {
        val attributes = hashMapOf<String?, String?>()
        META_DATA_PATTERN.findAll(line).forEach { matched ->
            try {
                val attr = matched.value.replace("\"", "").split("=")
                val (key, value) = attr
                attributes[key] = value
            } catch (e: Exception) {

            }
        }
        // try to get name again if is it null
        if (attributes[TVG_NAME].isNullOrEmpty() && line.contains(",")) {
            val name = line.split(",").last()
            attributes[TVG_NAME] = name
        }
        return attributes
    }

/*    fun parseFile(file: File): List<M3UEntry> {
        val items = ArrayList<M3UEntry>()
        parseItems(file) { item ->
            items.add(item)
        }
        return items
    }

    suspend fun parseFileAsync(file: File): List<M3UEntry> = withContext(Dispatchers.IO) {
        val items = ArrayList<M3UEntry>()
        parseItemsAsync(file) { item ->
            items.add(item)
        }
        items
    }*/

}