# WorkManager stores its queue in a Room database and instantiates the generated WorkDatabase_Impl
# reflectively; the rule shipped with room-runtime keeps the class but, under R8 full mode, not its
# no-argument constructor.
-keep class * extends androidx.room.RoomDatabase { <init>(); }
