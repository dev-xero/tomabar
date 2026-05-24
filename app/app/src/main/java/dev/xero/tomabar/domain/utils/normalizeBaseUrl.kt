package dev.xero.tomabar.domain.utils

fun normalizeBaseUrl(input: String, defaultPort: Int = 2118): String? {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) return null

    var s = if (trimmed.startsWith("http://") || trimmed.startsWith("https://"))
        trimmed else "http://$trimmed"

    val afterScheme = s.substringAfter("://")
    if (!afterScheme.substringBefore("/").contains(":")) {
        val host = afterScheme.substringBefore("/")
        s = s.replaceFirst(host, "$host:$defaultPort")
    }

    if (!s.endsWith("/")) s += "/"

    return s
}