package io.sunflower.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class EnumsTest {

    enum VideoFormat {
        OGG,
        MPEG_DASH,
        H_264,
        FFMPEG,
        HDMOV {
            @Override
            public String toString() {
                return "QuickTime";
            }
        };

        // Factory methods are not handled
        public static VideoFormat fromString(String s) {
            return valueOf(s.replace("[", "").replace("]", ""));
        }
    }

    @Parameterized.Parameters(name = "Source:{0}, Guess:{1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"OGG", VideoFormat.OGG},
                {"ogg", VideoFormat.OGG},
                {"FFmpeg", VideoFormat.FFMPEG},
                {" FFmpeg ", VideoFormat.FFMPEG},
                {"MPEG-DASH", VideoFormat.MPEG_DASH},
                {"h.264", VideoFormat.H_264},
                {"QuickTime", VideoFormat.HDMOV},
                {"[OGG]", null},
                {"FLV", null},
        });
    }

    private final String sourceText;
    private final VideoFormat guessedFormat;

    public EnumsTest(String text, VideoFormat result) {
        this.sourceText = text;
        this.guessedFormat = result;
    }

    @Test
    public void canGuess() {
        assertThat(Enums.fromStringFuzzy(sourceText, VideoFormat.values())).isEqualTo(guessedFormat);
    }
}
