# R8 rules for the release build. The libraries (Compose, Firebase, GitLive, kotlinx.serialization,
# Room, Ads) ship their own consumer rules; this file holds only what the app itself needs.

# Keeps file names and line numbers in Crashlytics stack traces (it de-obfuscates the rest).
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
