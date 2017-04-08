import java.net.UnknownHostException;

/**
 * Created by ebarsallo on 3/18/17.
 */

public class Test {

    // Enum version
    enum HelloWorld {
        HW0("Hello World0"),
        HW1("Hello World1"),
        HW2("Hello World2"),
        HW3("Hello World3"),
        HW4("Hello World4"),
        HW5("Hello World5"),
        HW6("Hello World6"),
        HW7("Hello World7"),
        HW8("Hello World8"),
        HW9("Hello World9"),
        HW10("Hello World10"),
        HW11("Hello World11"),
        HW12("Hello World12"),
        HW13("Hello World13"),
        HW14("Hello World4"),
        HW15("Hello World15");

        private String name = null;

        private HelloWorld(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void execute(String doSomething){
            doSomething = "Hello World0";
        }

        public static HelloWorld fromString(String input) {
            for (HelloWorld hw : HelloWorld.values()) {
                if (input.equals(hw.getName())) {
                    return hw;
                }
            }
            return null;
        }

    }

    //Enum version for betterment on coding format compare to interface ExecutableClass
    enum HelloWorld1 {
        HW0("Hello World0") {
            public void execute(String doSomething){
                doSomething = "Hello World0";
            }
        },
        HW1("Hello World1"){
            public void execute(String doSomething){
                doSomething = "Hello World0";
            }
        };
        private String name = null;

        private HelloWorld1(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void execute(String doSomething){
            //  super call, nothing here
            //System.out.println(doSomething);
        }
    }

    // https://www.mkyong.com/java/java-enum-example/
    enum some {
        ONE("uno"),
        TWO("dos"),
        THREE("tres");

        private String number;

        some(String number) {
            this.number = number;
        }

        public String number() {
            return number;
        }
    }

    public static void main(String args[])  {

        System.out.println("hi there");

        System.out.println(some.THREE.number());

        System.out.println(HelloWorld1.HW1.getName());

        System.out.println(HelloWorld.HW0.getName());

        HelloWorld1.HW1.execute("some");
    }

}
