# fn

fn is a Java Library that helps utilize some rudimentary functional concepts with more nounlar objects.

The target audience is for developers who spend most of their time getting things done instead of learning complicated systems and
intricate implementations of extractors.

    <dependencies>
      ....
      <dependency>
        <groupId>com.nthalk</groupId>
        <artifactId>fn</artifactId>
        <version>1.0.3</version>
      </dependency
      ....
    </dependencies

### Java Support
Fn will also always support Java 1.6, however, Java 1.8 hygiene is also a design goal.

### Repository

As fn is not yet hosted in the central repository, it is currently hosted on a public repo here:
    
    <repositories>
        <repository>
            <id>iodesystems</id>
            <url>http://nexus.iodesystems.com/nexus/content/groups/public</url>
        </repository>
    </repositories>

## Why not X?

There are a few libraries that are rumored to be nicer, fuller featured, and more tested than this, why not just use X?

### Why not LambdaJ?

First off, LambdaJ uses some interesting proxy work to make some functional concepts into oneliners, but this requires
knowing where and when to use `Lambda.on` or how to type your `Map<KEY, Object>` indexes. I would rather have my editor generate 
two more lines of boilerplate than to get a runtime exception for invalid class casts, or passing a non matcher object.

Also, it requires hamcrest matchers for anything fancy/useful.

I have seen junior developers struggle with this library, and they deserve something better.

### Why not Op4j?

My major complaint for using Opt4j is the uses of things like `FnString` that require understanding a large library before being 
able to get the job done. Simply finding out what can be done in `Op4j` can be daunting, and that's not what busy developers need.

## Common usages

### Indexes

Creating an index:

    Map<String, Integer> index = Fn.index(source, new From<Integer, String>() {
        @Override
        public String from(Integer integer) {
            return integer.toString();
        }
    });

### Routes

Composable route functions:

    Route<String, Integer> five = Fn.partial(new From<String, Option<Integer>>() {
        @Override
        public Option<Integer> from(String s) {
            if ("five".equalsIgnoreCase(s)) {
                return Option.of(5);
            } else {
                return Option.empty();
            }
        }
    });

    Route<String, Integer> six = Fn.partial(new From<String, Option<Integer>>() {
        @Override
        public Option<Integer> from(String s) {
            if ("six".equalsIgnoreCase(s)) {
                return Option.of(6);
            } else {
                return Option.empty();
            }
        }
    });

    Route<String, Integer> fiveOrSix = five.or(six);
    assertEquals(fiveOrSix.from("five"), Option.of(5));
    assertEquals(fiveOrSix.from("six"), Option.of(6));
    assertEquals(fiveOrSix.from("seven"), Option.<Integer>empty());

### Filters

Filters:

    List<Integer> source = Arrays.asList(1, 2, 3, 4, 5);
    List<Integer> notTwo = Fn.filter(source, Fn.not(2));
    List<Integer> isTwo = Fn.filter(source, Fn.is(2));
    List<Integer> isEven = Fn.filter(source, new Where<Integer>() {
        @Override
        public boolean is(Integer integer) {
            return integer % 2 == 0;
        }
    });

## Terminology

Some of the terms used in this project do not map up to any functional concepts (like map), however, the intentional audience
is not experienced functional programmers, but Java developers who could use some better tools.

The main motivators for naming were how Java method signatures are read, as well as describing what is actually happening, without
any surprising magic.

### From&lt;A, B&gt;

Because Java, the method signature `B map(A)` means little to people outside of math and functional programming, 
`B from(A)` has been chosen for producing a `B` from an `A`.

### Where&lt;A&gt; is

The term `filter` has been misused in a few areas which lends confusion to the question if it is filtering out or preserving items,
To clear up this confusion, `boolean is(A)` was chosen for determining if `A` should be considered.

### Merge&lt;A, A, A&gt; and Combine&lt;A&gt;

Whether you are reducing, folding, fold righting, fold lefting, extracting, combining or whatever other psychotically random term one 
might use to express combining two &lt;A&gt;'s into a single &lt;A&gt;
, `<A> from(A a, A a2)` is the right signature.

### Option&lt;A&gt;.Present &amp; Option&lt;A&gt;.Empty

Option has been added mostly for the ability to make it an `Iterable<A>`, this allows us to use options in some fancy ways:

    for(String item : Option.of("present")) return item;

### Async&lt;A&gt;

Asynchronous operations in Java has been a pain in developers side for quite a while, systems like `Observable`s and `Bus`es have left
their mark, however, they do not match up to the simplicity of JavaScript's `Promise`.

I understand that `JDeferred` is a great project, but in working with android, being able to switch executors in a then block is required,
and not using Java8, multiple anonymous classes per handler wasn't clean enough.

Any async process segment can be spawned on any `Executor`, and one can use a callable or create a `Deferred`.


    



