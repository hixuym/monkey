/*
 * Copyright (C) 2017. the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sunflower.util;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

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
            return valueOf(CharMatcher.anyOf("[]").removeFrom(s));
        }
    }

    @Parameterized.Parameters(name = "Source:{0}, Guess:{1}")
    public static Iterable<Object[]> data() {
        return ImmutableList.copyOf(new Object[][]{
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
