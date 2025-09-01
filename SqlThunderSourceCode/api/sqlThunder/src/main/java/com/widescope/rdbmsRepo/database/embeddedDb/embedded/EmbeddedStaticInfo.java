/*
 * Copyright 2022-present Infinite Loop Corporation Limited, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.widescope.rdbmsRepo.database.embeddedDb.embedded;

import com.google.gson.Gson;
import com.widescope.sqlThunder.rest.RestInterface;


public class EmbeddedStaticInfo implements RestInterface {

    private EmbeddedDbCommands dbCommands;
    private EmbeddedDbTypes embeddedDbTypes;

    public EmbeddedStaticInfo() {
        this.setDbCommands(new EmbeddedDbCommands());
        this.setEmbeddedDbTypes(new EmbeddedDbTypes());
    }

    public EmbeddedDbCommands getDbCommands() { return dbCommands; }
    public void setDbCommands(EmbeddedDbCommands dbCommands) { this.dbCommands = dbCommands; }

    public EmbeddedDbTypes getEmbeddedDbTypes() { return embeddedDbTypes; }
    public void setEmbeddedDbTypes(EmbeddedDbTypes embeddedDbTypes) { this.embeddedDbTypes = embeddedDbTypes; }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
