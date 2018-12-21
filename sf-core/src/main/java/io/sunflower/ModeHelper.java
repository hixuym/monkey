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

package io.sunflower;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @author michael
 */
public class ModeHelper {

    private static Logger logger = LoggerFactory.getLogger(ModeHelper.class);

    /**
     * returns an empty Optional<Mode> if no mode is set. Or the valid mode set via a System Property
     * called "sf.mode".
     * <p>
     * E.g. under mvn you can use mvn ... -Dsf.mode=prod or so. valid values for sf.mode are
     * "prod", "dev", "test".
     *
     * @return The valid mode set via a System Property called "sf.mode" or Optional absent if we
     * cannot get one.
     */
    public static Optional<Mode> determineModeFromSystemProperties() {

        Mode mode = null;

        // Get mode possibly set via a system property
        String modeFromGetSystemProperty = System.getProperty("sf.mode");

        // If the user specified a mode we set the mode accordingly:
        if (modeFromGetSystemProperty != null) {

            if ("test".equalsIgnoreCase(modeFromGetSystemProperty)) {

                mode = Mode.test;

            } else if ("dev".equalsIgnoreCase(modeFromGetSystemProperty)) {

                mode = Mode.dev;

            } else if ("prod".equalsIgnoreCase(modeFromGetSystemProperty)) {

                mode = Mode.prod;

            }

        }

        return Optional.ofNullable(mode);

    }

    /**
     * returns Mode.dev if no mode is set. Or the valid mode set via a System Property called
     * "sf.mode".
     * <p>
     * E.g. under mvn you can use mvn ... -Dsf.mode=prod or so. valid values for sf.mode are
     * "prod", "dev", "test".
     *
     * @return The valid mode set via a System Property called "sf.mode" or Mode.dev if it is not
     * set.
     */
    public static Mode determineModeFromSystemPropertiesOrDevIfNotSet() {

        Optional<Mode> modeOptional = determineModeFromSystemProperties();

        Mode mode;

        mode = modeOptional.orElse(Mode.dev);

        logger.info("Sunflower is running in mode {}", mode.toString());

        return mode;

    }

}
