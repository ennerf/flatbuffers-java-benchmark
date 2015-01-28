package org.enner.flatbuffers.bench;

import benchpbnano.Bench;
import benchpbnano.Bench.Bar;
import benchpbnano.Bench.Foo;
import benchpbnano.Bench.FooBar;
import benchpbnano.Bench.FooBarContainer;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;

/**
 * @author Florian Enner < florian @ hebirobotics.com >
 * @since 28 Jan 2015
 */
@State(Scope.Thread)
public class ProtoNanoBench {

    public FooBarContainer encode() {

        FooBarContainer fooBarContainer = new FooBarContainer();
        fooBarContainer.location = "http://google.com/flatbuffers/";
        fooBarContainer.initialized = initialized;
        fooBarContainer.fruit = fruit;
        fooBarContainer.list = new FooBar[vecLen];

        for (int i = 0; i < vecLen; i++) {

            Foo foo = new Foo();
            foo.id = 0xABADCAFEABADCAFEL + i;
            foo.count = 10000 + i;
            foo.prefix = '@' + i;
            foo.length = 1000000 + i;

            Bar bar = new Bar();
            bar.time = 123456 + i;
            bar.ratio = 3.14159f + i;
            bar.size = 10000 + i;
            bar.parent = foo;

            FooBar fooBar = new FooBar();
            fooBar.name = "Hello, World!";
            fooBar.rating = 3.1415432432445543543 + i;
            fooBar.postfix = '!' + i;
            fooBar.sibling = bar;

            fooBarContainer.list[i] = fooBar;

        }

        CodedOutputByteBufferNano out = CodedOutputByteBufferNano.newInstance(outputBytes);

        try {
            fooBarContainer.writeTo(out);
        } catch (IOException e) {
            // can't happen
            throw new RuntimeException(e.getMessage());
        }
        return fooBarContainer;

    }

    public FooBarContainer decode() {
        input.rewindToPosition(0);
        try {
            return FooBarContainer.parseFrom(input);
        } catch (IOException e) {
            // shouldn't happen
            throw new RuntimeException(e.getMessage());
        }
    }

    public FooBarContainer merge() {
        input.rewindToPosition(0);
        try {
            readContainer.clear();
            return readContainer.mergeFrom(input);
        } catch (IOException e) {
            // shouldn't happen
            throw new RuntimeException(e.getMessage());
        }
    }

    public long use() {
        long sum = 0;
        sum += fooBarContainer.initialized ? 1 : 0;
        sum += fooBarContainer.location.length();
        sum += fooBarContainer.fruit;

        for (FooBar fooBar : fooBarContainer.list) {

            sum += fooBar.name.length();
            sum += fooBar.postfix;
            sum += (long) fooBar.rating;

            Bar bar = fooBar.sibling;
            sum += bar.size;
            sum += bar.time;

            Foo foo = bar.parent;
            sum += foo.count;
            sum += foo.id;
            sum += foo.length;
            sum += foo.prefix;

        }

        return sum;

    }

    public static void main(String[] args) {
        ProtoNanoBench bench = new ProtoNanoBench();
        bench.decode();
    }

    public ProtoNanoBench() {
        // Somehow there is an issue with deserializing data that has been
        // serialized with nano. It throws an exception about an invalid tag zero.
        // TODO: debug further and report
        inputBytes = new ProtoBench().fooBarContainer.toByteArray();
        input = CodedInputByteBufferNano.newInstance(inputBytes);
    }

    int vecLen = 3;
    String location = "http://google.com/flatbuffers/";
    boolean initialized = true;
    int fruit = Bench.Bananas;
    byte[] outputBytes = new byte[1024];
    byte[] inputBytes;
    CodedInputByteBufferNano input;
    FooBarContainer fooBarContainer = encode();
    FooBarContainer readContainer = new FooBarContainer();

}
