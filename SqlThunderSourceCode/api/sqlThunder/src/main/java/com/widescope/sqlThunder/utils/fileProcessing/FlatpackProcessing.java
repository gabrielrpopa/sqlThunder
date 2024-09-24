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


package com.widescope.sqlThunder.utils.fileProcessing;

import java.io.FileReader;

import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.utils.StaticUtils;

import net.objectlab.kit.console.ConsoleMenu;
import net.sf.flatpack.DataError;
import net.sf.flatpack.DataSet;
import net.sf.flatpack.DefaultParserFactory;
import net.sf.flatpack.Parser;
import net.sf.flatpack.examples.createsamplecsv.CSVTestFileCreator;
import net.sf.flatpack.examples.csvheaderandtrailer.CSVHeaderAndTrailer;
import net.sf.flatpack.examples.csvperformancetest.CSVPerformanceTest;
import net.sf.flatpack.examples.delimiteddynamiccolumns.DelimitedWithPZMap;
import net.sf.flatpack.examples.delimiteddynamiccolumnswitherrors.DelimitedWithPZMapErrors;
import net.sf.flatpack.examples.exporttoexcel.DelimitedFileExportToExcel;
import net.sf.flatpack.examples.fixedlengthdynamiccolumns.FixedLengthWithPZMap;
import net.sf.flatpack.examples.fixedlengthheaderandtrailer.FixedLengthHeaderAndTrailer;
import net.sf.flatpack.examples.largedataset.delimiteddynamiccolumns.LargeDelimitedWithPZMap;
import net.sf.flatpack.examples.largedataset.fixedlengthdynamiccolumns.LargeFixedLengthWithPZMap;
import net.sf.flatpack.examples.largedataset.largecsvperformancetest.CSVLarge;
import net.sf.flatpack.examples.lowlevelparse.LowLevelParse;
import net.sf.flatpack.examples.multilinedelimitedrecord.DelimitedMultiLine;
import net.sf.flatpack.examples.numericsanddates.NumericsAndDates;
import net.sf.flatpack.util.FPConstants;
import net.sf.flatpack.util.ParserUtils;

public class FlatpackProcessing {

    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();
	
	private static final String ISSUE = "Issue";
    private static final String DATA2 = "Data   ";
    private static final String MAPPING2 = "Mapping ";

    
	public void doCSVTestFileCreator() {
        final int cols = ConsoleMenu.getInt("Number of cols", 10);
        final int rows = ConsoleMenu.getInt("Number of rows", 100);
        CSVTestFileCreator.createFile(cols, rows);
    }
	
	
	public void doCSVHeaderAndTrailer() throws Exception{
        try {
            final String mapping = ConsoleMenu.getString(MAPPING2, CSVHeaderAndTrailer.getDefaultMapping());
            final String data = ConsoleMenu.getString(DATA2, CSVHeaderAndTrailer.getDefaultDataFile());
            CSVHeaderAndTrailer.call(mapping, data);
        } catch (final Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.ctrl)) ;
        }
    }

    public void doCSVPerformanceTest() throws Exception {
        try {
            final String mapping = ConsoleMenu.getString("CSV File ", "SampleCSV.csv");
            final boolean data = ConsoleMenu.getBoolean("Traverse the entire parsed file", true);
            final boolean verbose = ConsoleMenu.getBoolean("Verbose", false);
            CSVPerformanceTest.call(mapping, verbose, data);
        } catch (final Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.ctrl)) ;
        }
    }

    public void doDelimitedWithPZMap() throws Exception {
        try {
            final String mapping = ConsoleMenu.getString(MAPPING2, DelimitedWithPZMap.getDefaultMapping());
            final String data = ConsoleMenu.getString(DATA2, DelimitedWithPZMap.getDefaultDataFile());
            DelimitedWithPZMap.call(mapping, data);
        } catch (final Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.ctrl)) ;
        }
    }

    public void doDelimitedWithPZMapErrors() throws Exception {
        try {
            final String mapping = ConsoleMenu.getString(MAPPING2, DelimitedWithPZMapErrors.getDefaultMapping());
            final String data = ConsoleMenu.getString(DATA2, DelimitedWithPZMapErrors.getDefaultDataFile());
            DelimitedWithPZMapErrors.call(mapping, data);
        } catch (final Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.ctrl)) ;
        }
    }

    public void doDelimitedFileExportToExcel() throws Exception {
        try {
            final String mapping = ConsoleMenu.getString(MAPPING2, DelimitedFileExportToExcel.getDefaultMapping());
            final String data = ConsoleMenu.getString(DATA2, DelimitedFileExportToExcel.getDefaultDataFile());
            DelimitedFileExportToExcel.call(mapping, data);
        } catch (final Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.ctrl)) ;
        }
    }

    public void doFixedLengthWithPZMap() throws Exception {
        try {
            final String mapping = ConsoleMenu.getString(MAPPING2, FixedLengthWithPZMap.getDefaultMapping());
            final String data = ConsoleMenu.getString(DATA2, FixedLengthWithPZMap.getDefaultDataFile());
            FixedLengthWithPZMap.call(mapping, data);
        } catch (final Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.ctrl)) ;
        }
    }

    public void doFixedLengthHeaderAndTrailer() throws Exception {
        try {
            final String mapping = ConsoleMenu.getString(MAPPING2, FixedLengthHeaderAndTrailer.getDefaultMapping());
            final String data = ConsoleMenu.getString(DATA2, FixedLengthHeaderAndTrailer.getDefaultDataFile());
            FixedLengthHeaderAndTrailer.call(mapping, data);
        } catch (final Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.ctrl)) ;
        }
    }

    public void doLargeDelimitedWithPZMap() throws Exception {
        try {
            final String mapping = ConsoleMenu.getString(MAPPING2, LargeDelimitedWithPZMap.getDefaultMapping());
            final String data = ConsoleMenu.getString(DATA2, LargeDelimitedWithPZMap.getDefaultDataFile());
            LargeDelimitedWithPZMap.call(mapping, data);
        } catch (final Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.ctrl)) ;
        }
    }

    public void doLargeFixedLengthWithPZMap() throws Exception {
        try {
            final String mapping = ConsoleMenu.getString(MAPPING2, LargeFixedLengthWithPZMap.getDefaultMapping());
            final String data = ConsoleMenu.getString(DATA2, LargeFixedLengthWithPZMap.getDefaultDataFile());
            LargeFixedLengthWithPZMap.call(mapping, data);
        } catch (final Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.ctrl)) ;
        }
    }

    public void doCSVLarge() throws Exception {
        try {
            final int cols = ConsoleMenu.getInt("Number of cols", 10);
            final int rows = ConsoleMenu.getInt("Number of rows", 2000000);
            final String filename = "LargeSampleCSV.csv";
            CSVTestFileCreator.createFile(cols, rows, filename);

            System.err.println("Large file created");

            CSVLarge.call(filename);
        } catch (final Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.ctrl)) ;
        }
    }

    public void doLowLevelParse() throws Exception {
        try {
            final String data = ConsoleMenu.getString(DATA2, LowLevelParse.getDefaultDataFile());
            LowLevelParse.call(data);
        } catch (final Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.ctrl)) ;
        }
    }

    public void doDelimitedMultiLine() throws Exception {
        try {
            final String data = ConsoleMenu.getString(DATA2, DelimitedMultiLine.getDefaultDataFile());
            DelimitedMultiLine.call(data);
        } catch (final Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.ctrl)) ;
        }
    }

    public void doNumericsAndDates() throws Exception {
        try {
            final String mapping = ConsoleMenu.getString(MAPPING2, NumericsAndDates.getDefaultMapping());
            final String data = ConsoleMenu.getString(DATA2, NumericsAndDates.getDefaultDataFile());
            NumericsAndDates.call(mapping, data);
        } catch (final Exception e) {
            throw new Exception(AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.ctrl)) ;
        }
    }

    public void doStringBuffer() {
        final int repeat = ConsoleMenu.getInt("How many times?", 100000);
        final int characters = ConsoleMenu.getInt("How many char?", 20);

        long start = System.currentTimeMillis();
        for (int i = 0; i < repeat; i++) {
            final StringBuilder sb = new StringBuilder();
            sb.append("h".repeat(1000));
        }
        long stop = System.currentTimeMillis();

        System.err.println("Creating new SB " + (stop - start) + " ms.");

        start = System.currentTimeMillis();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < repeat; i++) {
            sb.append("h".repeat(Math.max(0, characters)));
            sb.delete(0, sb.length());
        }
        stop = System.currentTimeMillis();

        System.err.println("Deleting existing SB " + (stop - start) + " ms.");

    }

    public void doTestParsers() {
        final int repeat = ConsoleMenu.getInt("How many Rows?", 1000);
        final int numberOfCols = ConsoleMenu.getInt("How many columns?", 100);
        final boolean qualif = ConsoleMenu.getBoolean("With qualifier?", true);

        final StringBuilder aRow = new StringBuilder();
        for (int i = 0; i < numberOfCols; i++) {
            if (qualif) {
                aRow.append("\"");
            }
            aRow.append("Column ").append(i);
            if (qualif) {
                aRow.append("\"");
            }
        }

        final String line = aRow.toString();

        final long start = System.currentTimeMillis();
        for (int i = 0; i < repeat; i++) {
            ParserUtils.splitLine(line, ',', '\"', FPConstants.SPLITLINE_SIZE_INIT, false, false);
        }
        final long stop = System.currentTimeMillis();

        System.err.println("ParserUtil " + (stop - start) + " ms.");
    }
    
    
    
    public static void parseCsv(final String filenameMapping,
    							final String filenameData,
    							final boolean ignoreFirstRow, 
    							final char delimiter,  // ','
    							final char qualifier, // '"'
    							final boolean verbose, 
    							final boolean traverse) throws Exception, InterruptedException {
        String[] colNames = null;
        // delimited by a comma
        // text qualified by double quotes
        // ignore first record
        System.out.println("Parsing....");
        Parser pzparser = null;
        if( StaticUtils.isStringNullOrEmpty(filenameMapping) )
        	pzparser = DefaultParserFactory.getInstance().newDelimitedParser(new FileReader(filenameData), delimiter, qualifier);
        else
        	pzparser = DefaultParserFactory.getInstance().newDelimitedParser(new FileReader(filenameMapping), new FileReader(filenameData), delimiter, qualifier, ignoreFirstRow);
        
        long timeStarted = System.currentTimeMillis();
        final DataSet ds = pzparser.parse();
        long timeFinished = System.currentTimeMillis();

        String timeMessage = "";

        if (timeFinished - timeStarted < 1000) {
            timeMessage = timeFinished - timeStarted + " Milleseconds...";
        } else {
            timeMessage = (float) ((timeFinished - timeStarted) / 1000.0) + " Seconds...";
        }

        System.out.println();
        System.out.println("********FILE PARSED IN: " + timeMessage + " ******");

        if (traverse) {
            if (verbose) {
                Thread.sleep(2000); // sleep for a couple seconds to the message
                // above can be read
            }
            timeStarted = System.currentTimeMillis();
            colNames = ds.getColumns();
            int rowCount = 0;
            final int colCount = colNames.length;
            while (ds.next()) {
                rowCount++;
                for (final String colName : colNames) {
                    final String string = ds.getString(colName);

                    if (verbose) {
                        System.out.println("COLUMN NAME: " + colName + " VALUE: " + string);
                    }
                }

                if (verbose) {
                    System.out.println("===========================================================================");
                }
            }
            timeFinished = System.currentTimeMillis();

            if (timeFinished - timeStarted < 1000) {
                timeMessage = timeFinished - timeStarted + " Milleseconds...";
            } else {
                timeMessage = (float) ((timeFinished - timeStarted) / 1000.0) + " Seconds...";
            }

            System.out.println("");
            System.out.println("********Traversed Data In: " + timeMessage + " (rows: " + rowCount + " Col:" + colCount + ") ******");

        }

        if (ds.getErrors() != null && !ds.getErrors().isEmpty()) {
            System.out.println("FOUND ERRORS IN FILE....");
            for (int i = 0; i < ds.getErrors().size(); i++) {
                final DataError de = ds.getErrors().get(i);
                System.out.println("Error: " + de.getErrorDesc() + " Line: " + de.getLineNo());
            }
        }

    }
    
    
}
