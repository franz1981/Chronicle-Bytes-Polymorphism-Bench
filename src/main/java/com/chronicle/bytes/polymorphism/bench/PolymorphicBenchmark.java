/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chronicle.bytes.polymorphism.bench;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.MappedBytes;
import net.openhft.chronicle.bytes.NativeBytes;
import net.openhft.chronicle.bytes.UncheckedBytes;
import net.openhft.chronicle.bytes.UncheckedNativeBytes;
import net.openhft.chronicle.bytes.VanillaBytes;
import net.openhft.chronicle.core.OS;

/**
 *
 * @author developer
 */
public class PolymorphicBenchmark {
    private static final int ITERATIONS = 50 * 1000 * 1000;
 
    public static void main(final String[] args)
        throws Exception
    {
        final Bytes[] operations = new Bytes[5];
        int index = 0;
        final Path tempFile = Files.createTempFile("test", "bench");
        final File file = tempFile.toFile();
        file.deleteOnExit();
        operations[index++] = MappedBytes.mappedBytes(file, OS.pageSize());
        operations[index++] = NativeBytes.nativeBytes();
        operations[index++] = VanillaBytes.vanillaBytes();
        operations[index++] = new UncheckedBytes(Bytes.elasticByteBuffer());
        operations[index++] = new UncheckedNativeBytes(Bytes.elasticByteBuffer());
        
 
        boolean value = false;
        for (int i = 0; i < 3; i++)
        {
            System.out.println("*** Run each method in turn: loop " + i);
 
            for (final Bytes operation : operations)
            {
                System.out.println(operation.getClass().getName()); 
                value = runTests(operation, value);
            }
        } 
 
        System.out.println("value = " + value);
    } 
 
    private static boolean runTests(final Bytes bytes, boolean value)
    {
        for (int i = 0; i < 10; i++)
        {
            final long start = System.nanoTime();
 
            value = value | opRun(bytes, value);
 
            final long duration = System.nanoTime() - start;
            final long opsPerSec = 
                (ITERATIONS * 1000L * 1000L * 1000L) / duration;
            System.out.printf("    %,d ops/sec\n", opsPerSec);
        }
 
        return value;
    } 
 
    private static boolean opRun(final Bytes bytes, boolean value)
    {
        for (int i = 0; i < ITERATIONS; i++)
        {
            value = value | bytes.isElastic();
        }  
        return value;
    } 
}
