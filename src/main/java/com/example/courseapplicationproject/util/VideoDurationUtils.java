package com.example.courseapplicationproject.util;

import org.mp4parser.IsoFile;

import java.io.File;
import java.io.IOException;

public class VideoDurationUtils {
    public static double getDurationInSeconds(File videoFile) throws IOException {
        IsoFile isoFile = new IsoFile(videoFile.getAbsolutePath());
        double duration = (double)
                isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
                isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
        return duration;
    }
}
