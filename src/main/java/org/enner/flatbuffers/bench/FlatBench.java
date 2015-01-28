package org.enner.flatbuffers.bench;

import benchfb.*;
import benchfb.Enum;
import benchpb.Bench;
import com.google.flatbuffers.FlatBufferBuilder;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.nio.ByteBuffer;

/**
 * @author Florian Enner < florian @ hebirobotics.com >
 * @since 27 Jan 2015
 */
@State(Scope.Thread)
public class FlatBench {

    public int encode(ByteBuffer buffer) {
        FlatBufferBuilder builder = new FlatBufferBuilder(buffer);

        int[] fooBars = new int[vecLen];
        for (int i = 0; i < vecLen; i++) {

            int name = builder.createString("Hello, World!");

            FooBar.startFooBar(builder);
            FooBar.addName(builder, name);
            FooBar.addRating(builder, 3.1415432432445543543 + i);
            FooBar.addPostfix(builder, (byte) ('!' + i));

            int bar = Bar.createBar(builder,
                    // Foo fields (nested struct)
                    0xABADCAFEABADCAFEL + i,
                    (short) (10000 + i),
                    (byte) ('@' + i),
                    1000000 + i,
                    // Bar fields
                    123456 + i,
                    3.14159f + i,
                    (short) (10000 + i));

            FooBar.addSibling(builder, bar);
            int fooBar = FooBar.endFooBar(builder);
            fooBars[i] = fooBar;

        }

        int list = FooBarContainer.createListVector(builder, fooBars);
        int loc = builder.createString(location);

        FooBarContainer.startFooBarContainer(builder);
        FooBarContainer.addLocation(builder, loc);
        FooBarContainer.addInitialized(builder, initialized);
        FooBarContainer.addFruit(builder, anEnum);
        FooBarContainer.addLocation(builder, loc);
        FooBarContainer.addList(builder, list);
        int fooBarContainer = FooBarContainer.endFooBarContainer(builder);
        builder.finish(fooBarContainer);

        return buffer.position();
    }

    public FooBarContainer decode(ByteBuffer buffer) {
        return FooBarContainer.getRootAsFooBarContainer(buffer);
    }

    public long use(ByteBuffer buffer) {
        // The root object really should be reusable
        FooBarContainer fooBarContainer = FooBarContainer.getRootAsFooBarContainer(buffer);

        long sum = 0;
        sum += fooBarContainer.initialized() ? 1 : 0;
        sum += fooBarContainer.location().length();
        sum += fooBarContainer.fruit();

        int length = fooBarContainer.listLength();
        for (int i = 0; i < length; i++) {

            fooBarContainer.list(fooBar, i);
            sum += fooBar.name().length();
            sum += fooBar.postfix();
            sum += (long) fooBar.rating();

            fooBar.sibling(bar);
            sum += bar.size();
            sum += bar.time();

            bar.parent(foo);
            sum += foo.count();
            sum += foo.id();
            sum += foo.length();
            sum += foo.prefix();

        }

        return sum;

    }

    // reusable read objects
    FooBar fooBar = new FooBar();
    Bar bar = new Bar();
    Foo foo = new Foo();

    int vecLen = 3;
    String location = "http://google.com/flatbuffers/";
    boolean initialized = true;
    short anEnum = Enum.Bananas;

}
