package hello

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Controller
class MyController {

    @PostMapping("/process")
    fun process(
            @RequestParam(required = true) name: String,
            @RequestParam(required = true) file: MultipartFile
    ) {
        LOG.info("Processing")
        LOG.info("Name: $name")
        LOG.info("File original name: ${file.originalFilename}")
        LOG.info("File name: ${file.name}")
        LOG.info("File size: ${file.size}")
        LOG.info("File content type: ${file.contentType}")
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(MyController::class.java)
    }
}