package org.example;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.zip.ZipException;

/**
 * @author Yanni
 */
public class ExceptionTest {

    /**
     * 不管try语句块内是否抛出异常，资源都会自动关闭
     *
     * @param path
     * @return
     * @throws IOException
     */
    static String readFirstLineFromFile(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return br.readLine();
        }
    }

    /**
     * 如果try语句块和finally语句块都有异常抛出，那么方法最终抛出的是finally语句块的异常信息。
     * 如果try语句块和try-with-resources语句都抛出异常，最终抛出的是try语句块内都异常信息。
     * 从jdk1.7开始，被屏蔽的异常被附加到被抛出的异常中
     *
     * @param path
     * @return
     * @throws IOException
     */
    static String readFirstLineFromFileWithFinallyBlock(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            return br.readLine();
        } finally {
            br.close();
        }
    }


    /**
     * 多个resource时，程序会按照resource声明的顺序去关闭资源。
     *
     * @param zipFileName
     * @param outputFileName
     * @throws IOException
     */
    public static void writeToFileZipFileContents(String zipFileName, String outputFileName) throws IOException {

        java.nio.charset.Charset charset =
            java.nio.charset.StandardCharsets.US_ASCII;
        java.nio.file.Path outputFilePath =
            java.nio.file.Paths.get(outputFileName);

        // Open zip file and create output file with
        // try-with-resources statement

        try (java.util.zip.ZipFile zf = new java.util.zip.ZipFile(zipFileName);
             java.io.BufferedWriter writer = java.nio.file.Files.newBufferedWriter(outputFilePath, charset)) {
            // Enumerate each entry
            for (java.util.Enumeration entries =
                 zf.entries(); entries.hasMoreElements(); ) {
                // Get the entry name and write it to the output file
                String newLine = System.getProperty("line.separator");
                String zipEntryName =
                    ((java.util.zip.ZipEntry) entries.nextElement()).getName() +
                        newLine;
                writer.write(zipEntryName, 0, zipEntryName.length());
            }
        }
    }

   /* public static void viewTable(Connection con) throws SQLException {

        String query = "select COF_NAME, SUP_ID, PRICE, SALES, TOTAL from COFFEES";

        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String coffeeName = rs.getString("COF_NAME");
                int supplierID = rs.getInt("SUP_ID");
                float price = rs.getFloat("PRICE");
                int sales = rs.getInt("SALES");
                int total = rs.getInt("TOTAL");

                System.out.println(coffeeName + ", " + supplierID + ", " +
                    price + ", " + sales + ", " + total);
            }
        } catch (SQLException e) {
            JDBCTutorialUtilities.printSQLException(e);
        }
    }
*/

    @Test
    public void catchTryWithResourcesException() {
        try (BufferedReader br = new BufferedReader(new FileReader(""))) {
            System.out.println("try block");
        } catch (Exception e) {
            System.out.println("catch block");
            if (e instanceof FileNotFoundException) {
                System.out.println("try-with-resources exception can be catch");
            }
            e.printStackTrace();
        }
    }

    @Test
    public void catchFinallyException() {
        Exception exception = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader("path"));
        } catch (Exception e) {
            System.out.println("catch block");
            exception = e;
        } finally {
            exception = new FinallyException();
        }

        if (exception instanceof FileNotFoundException) {
            System.out.println("catch block exception");
        } else if (exception instanceof FinallyException) {
            System.out.println("finally block exception");
        } else {
            System.out.println("other block exception");
        }
    }

    @Test
    public void catchMoreException() {
        try (BufferedReader br = new BufferedReader(new FileReader(""))) {
            System.out.println("try block");
        } catch (Exception e) {
            System.out.println("catch block");
            if (e instanceof FileNotFoundException) {
                System.out.println("try-with-resources exception can be catch");
            }
            e.printStackTrace();
        }

    }

    class FinallyException extends IOException {
        public void print() {
            System.out.println("this is my defined finally exception");
        }
    }
}
