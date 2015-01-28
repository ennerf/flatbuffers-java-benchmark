package org.enner.flatbuffers.bench;

import benchpb.Bench;
import benchpb.Bench.Bar;
import benchpb.Bench.FooBar;
import benchpb.Bench.FooBarContainer;
import com.google.protobuf.InvalidProtocolBufferException;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Florian Enner < florian @ hebirobotics.com >
 * @since 27 Jan 2015
 */
@State(Scope.Thread)
public class ProtoBench {

    public FooBarContainer encode() {

        FooBarContainer.Builder fooBarContainer = FooBarContainer.newBuilder()
                .setLocation("http://google.com/flatbuffers/")
                .setInitialized(initialized)
                .setFruit(anEnum);

        for (int i = 0; i < vecLen; i++) {

            Bench.Foo.Builder foo = Bench.Foo.newBuilder()
                    .setId(0xABADCAFEABADCAFEL + i)
                    .setCount(10000 + i)
                    .setPrefix('@' + i)
                    .setLength(1000000 + i);

            Bar.Builder bar = Bar.newBuilder()
                    .setTime(123456 + i)
                    .setRatio(3.14159f + i)
                    .setSize(10000 + i)
                    .setParent(foo);

            FooBar.Builder fooBar = FooBar.newBuilder()
                    .setName("Hello, World!")
                    .setRating(3.1415432432445543543 + i)
                    .setPostfix('!' + i)
                    .setSibling(bar);

            fooBarContainer.addList(fooBar);

        }

        FooBarContainer out = fooBarContainer.build();
        outputStream.reset();
        try {
            out.writeTo(outputStream);
        } catch (IOException e) {
            // can't happen
            throw new RuntimeException(e.getMessage());
        }
        return out;

    }

    public FooBarContainer decode() {
        inputStream.reset();
        try {
            return FooBarContainer.PARSER.parseFrom(inputStream);
        } catch (InvalidProtocolBufferException e) {
            // shouldn't happen
            throw new RuntimeException(e.getMessage());
        }
    }

    public long use() {
        long sum = 0;
        sum += fooBarContainer.getInitialized() ? 1 : 0;
        sum += fooBarContainer.getLocation().length();
        sum += fooBarContainer.getFruit().getNumber();

        for (FooBar fooBar : fooBarContainer.getListList()) {

            sum += fooBar.getName().length();
            sum += fooBar.getPostfix();
            sum += (long) fooBar.getRating();

            Bar bar = fooBar.getSibling();
            sum += bar.getSize();
            sum += bar.getTime();

            Bench.Foo foo = bar.getParent();
            sum += foo.getCount();
            sum += foo.getId();
            sum += foo.getLength();
            sum += foo.getPrefix();

        }

        return sum;

    }

    int vecLen = 3;
    String location = "http://google.com/flatbuffers/";
    boolean initialized = true;
    Bench.Enum anEnum = Bench.Enum.Bananas;
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
    Bench.FooBarContainer fooBarContainer = encode();
    ByteArrayInputStream inputStream = new ByteArrayInputStream(fooBarContainer.toByteArray());

}
