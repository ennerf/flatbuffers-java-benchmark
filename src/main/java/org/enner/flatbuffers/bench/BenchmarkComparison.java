package org.enner.flatbuffers.bench;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

/**
 * @author Florian Enner < florian @ hebirobotics.com >
 * @since 23 Jan 2015
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(2)
@Warmup(iterations = 10)
@Measurement(iterations = 10)
@State(Scope.Thread)
public class BenchmarkComparison {

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(".*" + BenchmarkComparison.class.getSimpleName() + ".*")
                .verbosity(VerboseMode.NORMAL)
                .build();
        new Runner(options).run();
    }

    /**
     * Benchmark                                   Mode  Samples   Score  Score error   Units
     * o.e.f.b.BenchmarkComparison.protoDecode    thrpt       20  17.098        0.199  ops/us
     * o.e.f.b.BenchmarkComparison.protoEncode    thrpt       20   0.621        0.013  ops/us
     * o.e.f.b.BenchmarkComparison.protoUse       thrpt       20  27.186        0.499  ops/us
     */

    public BenchmarkComparison() {
        flatBench.encode(heapReadBuffer);
        flatBench.encode(directReadBuffer);
        this.position = heapReadBuffer.position();
    }

    ProtoBench protoBench = new ProtoBench();
    FlatBench flatBench = new FlatBench();
    ByteBuffer heapReadBuffer = ByteBuffer.allocate(1024);
    ByteBuffer directReadBuffer = ByteBuffer.allocateDirect(1024);
    int position;
    ByteBuffer heapWriteBuffer = ByteBuffer.allocate(1024);
    ByteBuffer directWriteBuffer = ByteBuffer.allocateDirect(1024);

    @Benchmark
    public Object protoEncode() {
        return protoBench.encode();
    }

    @Benchmark
    public Object protoDecode() {
        return protoBench.decode();
    }

    @Benchmark
    public long protoUse() {
        return protoBench.use();
    }

    @Benchmark
    public Object flatEncodeHeap() {
        return flatBench.encode(heapWriteBuffer);
    }

    @Benchmark
    public Object flatDecodeHeap() {
        heapReadBuffer.position(position);
        return flatBench.decode(heapReadBuffer);
    }

    @Benchmark
    public long flatUseHeap() {
        heapReadBuffer.position(position);
        return flatBench.use(heapReadBuffer);
    }

    @Benchmark
    public Object flatEncodeDirect() {
        return flatBench.encode(directWriteBuffer);
    }

    @Benchmark
    public Object flatDecodeDirect() {
        directReadBuffer.position(position);
        return flatBench.decode(directReadBuffer);
    }

    @Benchmark
    public long flatUseDirect() {
        directReadBuffer.position(position);
        return flatBench.use(directReadBuffer);
    }

}
