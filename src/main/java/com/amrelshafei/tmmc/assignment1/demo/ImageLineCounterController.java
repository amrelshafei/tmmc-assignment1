package com.amrelshafei.tmmc.assignment1.demo;

import static com.amrelshafei.tmmc.assignment1.cli.utils.ImageLineCounter.countVerticalLines;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api")
public class ImageLineCounterController {

    @PostMapping(value = "/count", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Integer>> count(@RequestPart("image") FilePart filePart) {

        return DataBufferUtils.join(filePart.content())
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return Mono.fromCallable(() -> {
                        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
                        if (image == null) throw new IllegalArgumentException("Not a valid image file.");
                        return countVerticalLines(image);
                    }).subscribeOn(Schedulers.boundedElastic());
                })
                .map(ResponseEntity::ok)
                .onErrorResume(ex -> Mono.just(ResponseEntity.badRequest().body(-1)));
    }
}
