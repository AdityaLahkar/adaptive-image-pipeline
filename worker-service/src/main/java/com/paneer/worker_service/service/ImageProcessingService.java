package com.paneer.worker_service.service;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class ImageProcessingService {

    private static final String INPUT_DIR = "/uploads/";

    private static final String OUTPUT_DIR = "/processed/";

    public String resize(
            String filename,
            Integer width,
            Integer height
    ) throws IOException {

        createOutputDirectory();

        File inputFile =
                new File(INPUT_DIR + filename);
        String outputName =
                "resized_" + filename;
        File outputFile =
                new File(
                        OUTPUT_DIR
                                + outputName
                );

        Thumbnails.of(inputFile)
                .size(width, height)
                .toFile(outputFile);
        return outputName;
    }

    public String compress(
            String filename,
            Float quality
    ) throws IOException {

        createOutputDirectory();

        File inputFile =
                new File(INPUT_DIR + filename);
        String outputName =
                "compressed_" + filename;

        File outputFile =
                new File(
                        OUTPUT_DIR
                                + "compressed_"
                                + filename
                );

        Thumbnails.of(inputFile)
                .scale(1.0)
                .outputQuality(quality)
                .toFile(outputFile);
        return outputName;
    }

    public String convertToJpg(
            String filename
    ) throws IOException {

        createOutputDirectory();

        File inputFile =
                new File(INPUT_DIR + filename);

        String outputName =
                filename.substring(
                        0,
                        filename.lastIndexOf('.')
                ) + ".jpg";


        File outputFile =
                new File(
                        OUTPUT_DIR
                                + "converted_"
                                + outputName
                );

        Thumbnails.of(inputFile)
                .scale(1.0)
                .outputFormat("jpg")
                .toFile(outputFile);
        return "converted_"  + outputName;
    }

    private void createOutputDirectory() {

        File dir = new File(OUTPUT_DIR);

        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}