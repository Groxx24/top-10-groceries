package com.top10.deals.domain.model

/**
 * A text in each of the app's languages. For a deal, French and Dutch are what the store's folders
 * print where they were available, and English is translated, since Belgian folders have none.
 */
data class LocalizedText(val en: String, val fr: String, val nl: String) {

    /** The text for a [language] code such as "fr" or "nl"; English for any other language. */
    fun inLanguage(language: String): String = when (language) {
        "fr" -> fr
        "nl" -> nl
        else -> en
    }

    companion object {
        /** A text that reads the same in every language, such as "650 g" or a brand. */
        fun same(text: String) = LocalizedText(en = text, fr = text, nl = text)
    }
}
