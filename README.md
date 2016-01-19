# fn

fn is a Java Library that helps utilize some rudimentary functional concepts with more nounlar objects.

The target audience is for developers who spend most of their time getting things done instead of learning complicated systems and
intricate implementations of extractors.

    <dependencies>
      ....
      <dependency>
        <groupId>com.nthalk</groupId>
        <artifactId>fn</artifactId>
        <version>1.0.0</version>
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

### Why not Op4j?

My major complaint for using Opt4j is the uses of things like `FnString` that require understanding a large library before being 
able to get the job done. Simply finding out what can be done in `Op4j` can be daunting, and that's not what busy developers need.

## Common usages

Creating an index:

    Map<String, Integer> index = Fn.index(source, new From<Integer, String>() {
        @Override
        public String from(Integer integer) {
            return integer.toString();
        }
    });

Composable router functions:

    Thunk<String, Integer, Option<Integer>> fiveOrSix = Fn.thunk(new From<String, Option<Integer>>() {
        @Override
        public Option<Integer> from(String s) {
            if ("five".equalsIgnoreCase(s)) {
                return Option.of(5);
            } else {
                return Option.empty();
            }
        }
    }).or(Fn.thunk(new From<String, Option<Integer>>() {
        @Override
        public Option<Integer> from(String s) {
            if ("six".equalsIgnoreCase(s)) {
                return Option.of(6);
            } else {
                return Option.empty();
            }
        }
    }));

Filters and selecters:

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

Some of the terms used in this project do not map up to any functional concepts (like map), however, again, the intentional audience
is not experienced functional programmers, but Java developers who could use some better tools.

The main motivators for naming were how Java method signatures are read, as well as describing what is actually happening.

### From&lt;A, B&gt;

Because Java, the method signature `B map(A)` means little to people outside of math and functional programming, 
`B from(A)` has been chosen for producing a `B` from an `A`.

### Where&lt;A&gt; is

The term `filter` has been misused in a few areas which lends confusion to the question if it is filtering out or preserving items,
T`boolean is(A)` was chosen for determining if `A` should be considered.

### Merge&lt;A, A, A&gt; and Merge.Simple&lt;A&gt;

Whether you are reducing, folding, fold righting, fold lefting, or extracting, or whatever other term one might do to combine two &lt;A&gt;'s into a single &lt;A&gt;, `<A> from(A a, A a2)` is the right signature.

### Option&lt;A&gt;.Present Option&lt;A&gt;.Empty

Option has been added mostly for the ability to make it an `Iterable<A>`, this allows us to use options in some fancy ways.


