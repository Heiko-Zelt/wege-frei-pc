package de.heikozelt.wegefrei

import de.heikozelt.wegefrei.db.DatabaseRepo
import de.heikozelt.wegefrei.db.entities.NoticeEntity
import de.heikozelt.wegefrei.model.Photo
import de.heikozelt.wegefrei.model.PhotoLoader
import de.heikozelt.wegefrei.model.PhotoLoaderObserver
import de.heikozelt.wegefrei.model.VehicleColor.Companion.COLORS
import de.heikozelt.wegefrei.model.VehicleMakesComboBoxModel.Companion.VEHICLE_MAKES
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.Thread.sleep
import java.nio.file.Path
import java.nio.file.Paths
import java.time.ZonedDateTime
import java.util.concurrent.Executors

class TestDataGenerator {
    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java.canonicalName)

        private val repo = DatabaseRepo.fromDirectory("/home/heiko")
        private val dirPath = Paths.get("/home/heiko/Pictures/wegefrei")
        private val exe = Executors.newFixedThreadPool(4)
        private val filenames = listOf(
            "20220701_182441.jpg",
            "20220701_182452.jpg",
            "20220701_182457.jpg",
            "20220701_182508.jpg",
            "20220701_182539.jpg"
        )
        private val paths = filenames.map { Paths.get(dirPath.toString(), it) }

        @JvmStatic
        fun main(args: Array<String>) {
            try {
                LOG.info("TestDataGenerator.main()")
                //generatePhotos()
                generateNotices()
                //generateManyToMany1()
                //generateManyToMany2()

                // wait for threads in thread pool to complete
                sleep(5000)
                repo.logStatistics()
            } finally {
                LOG.debug("closing repo")
                repo.close()
                LOG.debug("repo closed")
            }
        }

        private fun generateManyToMany1() {
            val photoEntity0 = repo.findPhotoByPath(paths[0])
            val photoEntity1 = repo.findPhotoByPath(paths[1])
            val photoEntity2 = repo.findPhotoByPath(paths[2])
            val noticeEntity1 = repo.findNoticeById(1)
            val noticeEntity2 = repo.findNoticeById(2)

            photoEntity0?.let {ph0 ->
                photoEntity1?.let { ph1 ->
                    noticeEntity1?.photoEntities = mutableSetOf(ph0, ph1)
                }
            }
            noticeEntity1?.let {
                repo.updateNotice(it)
            }

            photoEntity1?.let {ph1 ->
                photoEntity2?.let { ph2 ->
                    noticeEntity2?.photoEntities = mutableSetOf(ph1, ph2)
                }
            }
            noticeEntity2?.let {
                repo.updateNotice(it)
            }
        }

        /*
        fun generateManyToMany2LoadedCallback(photoFile: PhotoFile) {
            LOG.debug("generateManyToMany2LoadedCallback")
            photoFile.getPhotoData()?.let {
                val photoEntity = PhotoEntity(
                    photoFile.getPath().toString(),
                    it.hash,
                    it.latitude,
                    it.longitude,
                    it.date
                )
                LOG.debug("instantiated photoEntity")
                val noticeEntity2 = repo.findNoticeById(2)
                LOG.debug("found noticeEntity")
                /*
                noticeEntity0?.let {
                    photoEntity0?.noticeEntities = setOf(it)
                }
                */
                noticeEntity2?.photoEntities = mutableSetOf(photoEntity)

                noticeEntity2?.let {
                    repo.updateNotice(noticeEntity2)
                    LOG.debug("updated Notice")
                }
            }
        }

        fun generateManyToMany2() {
            val photoFile2 = PhotoFile(paths[2])
            photoFile2.loadPhotoData(exe) { generateManyToMany2LoadedCallback(it) }
        }

        fun generatePhotosLoadedCallback(photoFile: PhotoFile) {
            photoFile.getPhotoData()?.let {
                val photoEntity = PhotoEntity(
                    photoFile.getPath().toString(),
                    it.hash,
                    it.latitude,
                    it.longitude,
                    it.date
                )
                repo.insertPhoto(photoEntity)
            }
        }

         */

        class Observer : PhotoLoaderObserver {
            private val log = LoggerFactory.getLogger("Observer")

            override fun doneLoadingFile(photo: Photo) {
                photo.copyMetaDataFromFileToEntity()
                photo.getPhotoEntity()?.let {
                    repo.insertPhoto(it)
                }
            }

            override fun doneLoadingEntity(photo: Photo) {
                TODO("Not yet implemented")
            }
        }

        private fun generatePhotos() {
            val filenames = mutableListOf<Path>()
            val dir = File(dirPath.toString())
            if (!dir.isDirectory) {
                LOG.error("$dirPath ist kein Verzeichnis.")
            }
            val unsortedFilenames = dir.list(ImageFilenameFilter()) ?: return
            filenames.clear()
            unsortedFilenames.sorted().forEach {
                filenames.add(Paths.get(it))
            }
            LOG.debug("filenames.size=${filenames.size}")

            val photos = filenames.map { Photo(Paths.get(dirPath.toString(), it.toString())) }
            /*val photos = listOf(
                Photo(Paths.get(dirPath.toString(), "20220701_182441.jpg")),
                Photo(Paths.get(dirPath.toString(), "20220701_182452.jpg")),
                Photo(Paths.get(dirPath.toString(), "20220701_182457.jpg")),
                Photo(Paths.get(dirPath.toString(), "20220701_182508.jpg")),
                Photo(Paths.get(dirPath.toString(), "20220701_182539.jpg"))
            )

             */

            val photoLoader = PhotoLoader(repo)
            val observer = Observer()
            photoLoader.registerObserver(observer)
            photos.forEach {
                photoLoader.loadPhotoFile(it)
            }
            LOG.debug("sleep")
            sleep(3000)
            LOG.debug("shutting down")
            photoLoader.shutdown()
            LOG.debug("shutdown complete")
        }

        fun generateNotices() {
            for (i in 1..10000) {
                val noticeEntity = NoticeEntity()
                noticeEntity.apply {
                    observationTime = ZonedDateTime.now()
                    licensePlate = "AA XX 00$i"
                    vehicleMake = VEHICLE_MAKES[i % VEHICLE_MAKES.size].toString()
                    color = COLORS[i % COLORS.size].colorName
                    latitude = 49 + i.toDouble() / 11
                    longitude = 8 + i.toDouble() / 13
                }
                repo.insertNotice(noticeEntity)
            }
        }
    }
}


