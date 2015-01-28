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
     * OS: Windows 8.1
     * JDK: Oracle JDK 1.8.0_20-b26
     * CPU: Intel i5-3427U @ 1.80 Ghz
     *
     * Benchmark                                       Mode  Samples  Score  Score error  Units
     * o.e.f.b.BenchmarkComparison.flatEncodeDirect    avgt       20  1.137        0.036  us/op
     * o.e.f.b.BenchmarkComparison.flatUseDirect       avgt       20  0.639        0.013  us/op
     * o.e.f.b.BenchmarkComparison.flatDecodeDirect    avgt       20  0.009        0.001  us/op (included in use)
     * <p/>
     * o.e.f.b.BenchmarkComparison.flatEncodeHeap      avgt       20  1.576        0.030  us/op
     * o.e.f.b.BenchmarkComparison.flatUseHeap         avgt       20  0.732        0.029  us/op (included in use)
     * o.e.f.b.BenchmarkComparison.flatDecodeHeap      avgt       20  0.011        0.000  us/op
     * <p/>
     * o.e.f.b.BenchmarkComparison.protoEncode         avgt       20  1.652        0.049  us/op
     * o.e.f.b.BenchmarkComparison.protoUse            avgt       20  0.037        0.001  us/op
     * o.e.f.b.BenchmarkComparison.protoDecode         avgt       20  6.903        0.084  us/op
     *
     * o.e.f.b.BenchmarkComparison.protoNanoEncode     avgt       20  1.379        0.058  us/op
     * o.e.f.b.BenchmarkComparison.protoNanoUse        avgt       20  0.024        0.001  us/op
     * o.e.f.b.BenchmarkComparison.protoNanoDecode     avgt       20  1.101        0.048  us/op
     * o.e.f.b.BenchmarkComparison.protoNanoMerge      avgt       20  358.972       49.421  us/op
     */

    /**
     * http://google.github.io/flatbuffers/md__benchmarks.html
     * in micro sec per operation (= sec for 1M operations)
     * <p/>
     * Decode + Traverse
     * (FlatBuf direct) 0     / 0.639 = 0.639
     * (FlatBuf heap)   0     / 0.732 = 0.732
     * (ProtoBuf)       6.903 / 0.037  = 6.94
     * <p/>
     * Encode
     * (FlatBuf direct) 1.137
     * (FlatBuf heap)   1.576
     * (ProtoBuf)       1.652
     */

    public BenchmarkComparison() {
        flatBench.encode(heapReadBuffer);
        flatBench.encode(directReadBuffer);
        this.position = heapReadBuffer.position();
    }

    ProtoBench protoBench = new ProtoBench();
    ProtoNanoBench protoNanoBench = new ProtoNanoBench();
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
    public Object protoNanoEncode() {
        return protoNanoBench.encode();
    }

    @Benchmark
    public Object protoNanoDecode() {
        return protoNanoBench.decode();
    }

    @Benchmark
    public Object protoNanoMerge() {
        return protoNanoBench.merge();
    }

    @Benchmark
    public long protoNanoUse() {
        return protoNanoBench.use();
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
