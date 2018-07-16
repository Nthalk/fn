# Fn

Fn is a lazy Java Library that helps utilize some rudimentary functional concepts with more nounular objects.

The target audience is for developers who spend most of their time getting things done instead of learning 
complicated systems and intricate implementations of extractors and shying away from magical proxies and
reflection.

    <dependencies>
      ....
      <dependency>
        <groupId>com.iodesystems</groupId>
        <artifactId>fn</artifactId>
        <version>1.2.4</version>
      </dependency
      ....
    </dependencies

### Java Support

Fn supports Java 1.6, with an effort for Java 1.8 hygiene.


### The Fn Object

Almost all of the operations can be used independently, or with a `Fn` helper. 

    Fn.of(1,2,3).filter(Fn.is(2)); // <-- Helper
    Fn.where(Arrays.asList(1,2,3), Fn.is(2)); // <-- No helper
    
Because complicated actions can often be written cleaner in plain old java, the `Fn` object is `Iterable`,
so multiple dispatching operations can be performed with ease:

    for(Integer value : Fn.of(1,2,3).not(2).repeat(2)){
        System.out.println(value);
    }

### Common usages

When writing code, often we need to index or group our data

    Map<String, Integer> indexed = Fn.of(1,2,3,2,3).index(new From<Integer, String>(){
        public String from(Integer integer){
            return integer.toString();
        }
    });
    
However, this will yield a `Map` of size 3. If you want to preserve your groups

    Map<String, List<Integer>> grouped = Fn.of(1,2,3,2,3).group(new From<Integer, String>(){
       public String from(Integer integer){
           return integer.toString();
       }
    });

### Iterate all the things

Enumerations, sized lists, and chained sequences are all very obnoxious when you just want to iterate across them:

    Enumeration<Integer> enumeration = Collections.enumeration(Fn.list(1, 2, 3));
    System.out.println("Enumerations!");
    for (Integer value : Fn.of(enumeration)) {
        System.out.print(value);
    }
    System.out.println();

    System.out.println("Sized Buckets!");
    SizedIndexAccessor sizedBucket = new SizedIndexAccessor(Fn.list(1, 2, 3));
    for (Integer value : Fn.of(sizedBucket).multiplySizedContents(SizedIndexAccessor::getSize, SizedIndexAccessor::get)) {
        System.out.print(value);
    }
    System.out.println();

    System.out.println("Next Containers!");
    HasNext linkedItemsRoot = Fn.ofRange(1, 3).reverse().combine(null, HasNext::new);
    for (HasNext hasNext : Fn.of(linkedItemsRoot).withNext(HasNext::getNext)) {
        System.out.print(hasNext.getValue());
    }
    System.out.println();

### Generic utilities

When getting things done, often you need to read OutputStreams into Strings, or iterate into lines:

    String contents = Fn.readFully(inputStream);
    for(String line : Fn.lines(inputStream);
    
It's also useful to know when Strings are blank or null:

    Fn.isBlank("") || Fn.isBlank(null); // true
     
And it's also useful to provide an if null (and ifBlank):

    Fn.ifNull(null, 3); // 3
    Fn.ifBlank("", "default"); // "default"

As well as a string splitting:

    assertEquals(Fn.list("a", "b", "c", ""), Fn.split("a_0b_0c_0", "_0").toList());
    
### Tree Support

Fn includes `breadth`, `depth`, and `breadthPaths`, which make working on nested objects easier.

    Fn.of(node).breadth(new From<Node, Iterable<Node>(){
        public String from(Node node){
            return node.getChildren();
        }
    }

### Async

Async code on Java has historically been a pain, however, Fn offers `Async<A>` and `Deferred<A>` objects.

    final String[] result = new String[]{null};
    Fn.async(new Callable<String>() {
        @Override
        public String call() throws Exception {
            return "Hello world!";
        }
    }).then(new Async.Result<String>() {
        @Override
        public String onResult(String message) throws Exception {
            // It did it
            result[0] = message;
            return null;
        }
    });
    assertEquals("Hello World!", result[0]);

The deferred example is just as simple (this example also shows progress tracking):

    final String[] result = new String[]{null};
    final Integer[] progress = new Integer[]{null, null};
    Async.Deferred<String> defer = Fn.defer();
    defer.then(new Async.Result<String>() {
        @Override
        public String onResult(String o) throws Exception {
            result[0] = o;
            return null;
        }

        @Override
        public int onProgress(int deferredProgress) {
            progress[deferredProgress - 1] = deferredProgress;
            return deferredProgress;
        }
    });

    defer.progress(1);
    defer.progress(2);
    defer.result("Hello World!");
    assertEquals(result[0], "Hello World!");
    assertEquals(progress[0], new Integer(1));
    assertEquals(progress[1], new Integer(2));

Each async is branchable, repeatable, and any segment can run on any `Executor`.
 
When a handler uses the same `Executor` as it's parent, or the `INLINE` executor, the handler will on the same thread as what triggered it.

When not specifying an `Executor` for a down stream handler, the parent's executor will be used.
