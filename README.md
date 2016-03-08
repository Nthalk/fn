# fn

fn is a lazy Java Library that helps utilize some rudimentary functional concepts with more nounular objects.

The target audience is for developers who spend most of their time getting things done instead of learning 
complicated systems and intricate implementations of extractors and shying away from magical proxies and
reflection.

    <dependencies>
      ....
      <dependency>
        <groupId>com.iodesystems</groupId>
        <artifactId>fn</artifactId>
        <version>1.0.6</version>
      </dependency
      ....
    </dependencies

### Java Support
Fn supports Java 1.6, with an effort for Java 1.8 hygiene.

### Repository

As fn is not yet hosted in the central repository, it is currently hosted on a public repo here:
    
    <repositories>
        <repository>
            <id>iodesystems</id>
            <url>http://nexus.iodesystems.com/nexus/content/groups/public</url>
        </repository>
    </repositories>

### The Fn Object

Almost all of the operations can be used independently, or with a `Fn` helper. 

    Fn.of(1,2,3).filter(Fn.is(2)); // <-- Helper
    Fn.filter(Arrays.asList(1,2,3), Fn.is(2)); // <-- No helper
    
Because complicated actions can be written cleaner in plain old java, the `Fn` object is `Iterable`,
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

