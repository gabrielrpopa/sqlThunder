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

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Csv {


    private String fileName = "";


    public Csv( final String  fileName)	{
        this.fileName = fileName;

    }

    public List<List<String>> read() {
        List<List<String>> ret = new ArrayList<List<String>>();

        try (CSVReader csvReader = new CSVReader(new FileReader(fileName));) {
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                ret.add(Arrays.asList(values));
            }
        } catch (CsvValidationException | IOException ignored) {

        }
        return ret;
    }
}
