/*
 * Copyright 2018-2023 Monkey, Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.monkey.quickstarters.resteasy;

import io.ebean.annotation.Platform;

/**
 * @author Michael
 * Created at: 2019/1/6 12:18
 */
public class DbMigration {

    public static void main(String[] args) throws Exception {

//        System.setProperty("ddl.migration.version", "1.1");
//        System.setProperty("ddl.migration.name", "support end dating");
//        System.setProperty("ddl.migration.pendingDropsFor", "1.1");

        io.ebean.dbmigration.DbMigration migration = io.ebean.dbmigration.DbMigration.create();

        migration.setPlatform(Platform.H2);
        migration.setApplyPrefix("V");

        migration.generateMigration();
    }
}
