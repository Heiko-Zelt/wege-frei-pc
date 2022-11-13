package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.DatabaseRepo
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Subjekt im Sinne des Observer-Design-Patterns
 */
class PhotoLoader(private val databaseRepo: DatabaseRepo) {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val executorService = Executors.newFixedThreadPool(4)
    private val observers = HashSet<PhotoLoaderObserver>()

    fun loadPhotoFile(photo: Photo) {
        if(photo.getFileState() == Photo.Companion.States.UNINITIALIZED) {
            photo.startedLoadingFile()
            val task = LoadPhotoFileTask(this, photo)
            executorService.submit(task)
        }
    }

    fun loadPhotoEntity(photo: Photo) {
        if(photo.getEntityState() == Photo.Companion.States.UNINITIALIZED) {
            photo.startedLoadingEntity()
            val task = LoadPhotoEntityTask(this, databaseRepo, photo)
            executorService.submit(task)
        }
    }

    fun doneLoadingFile(photo: Photo) {
        observers.forEach { it.doneLoadingFile(photo) }
    }

    fun doneLoadingEntity(photo: Photo) {
        observers.forEach { it.doneLoadingEntity(photo) }
    }

    fun registerObserver(observer: PhotoLoaderObserver) {
        observers.add(observer)
    }

    /**
     * Wichtig, um Memory-Leaks zu vermeiden
     */
    fun unregisterObserver(observer: PhotoLoaderObserver) {
        observers.remove(observer)
    }

    fun shutdown() {
        executorService.shutdown()
        executorService.awaitTermination(5, TimeUnit.SECONDS)
    }

}