package com.dji.drone.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;


public class Detection {
    public enum status{CRACK, NON_CRACK};

    private final Bitmap image;
    private final Segment[][] segments;

    public Detection(@NonNull Bitmap image, @NonNull Segment[][] segments) {
        this.image = image;
        this.segments = segments;
    }

    public Bitmap getImage() {
        return image;
    }

    public Segment[][] getSegments() {
        return segments;
    }

    @Override
    public String toString() {

        return "Detection{" +
                "image=" + image +
                ", segments=\n" + Arrays.deepToString(segments) +
                '}';
    }

    public static class Segment{
        public int type;
        public int score;

        public Segment(int type, int score) {
            this.type = type;
            this.score = score;
        }

        @Override
        public String toString() {
            return "Segment{" +
                    "type=" + type +
                    ", score=" + score +
                    "}\n";
        }
    }
}
