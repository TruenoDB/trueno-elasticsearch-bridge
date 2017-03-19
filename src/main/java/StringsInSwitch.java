public class StringsInSwitch {
    public static void main(String[] args) {
        String doSomething = null;


        //METHOD_1 : SWITCH
        long start = System.currentTimeMillis();
        for (int i = 0; i < 99999999; i++) {
            String input = "Hello World" + (i & 0xF);

            switch (input) {
                case "Hello World0":
                    doSomething = "Hello World0";
                    break;
                case "Hello World1":
                    doSomething = "Hello World0";
                    break;
                case "Hello World2":
                    doSomething = "Hello World0";
                    break;
                case "Hello World3":
                    doSomething = "Hello World0";
                    break;
                case "Hello World4":
                    doSomething = "Hello World0";
                    break;
                case "Hello World5":
                    doSomething = "Hello World0";
                    break;
                case "Hello World6":
                    doSomething = "Hello World0";
                    break;
                case "Hello World7":
                    doSomething = "Hello World0";
                    break;
                case "Hello World8":
                    doSomething = "Hello World0";
                    break;
                case "Hello World9":
                    doSomething = "Hello World0";
                    break;
                case "Hello World10":
                    doSomething = "Hello World0";
                    break;
                case "Hello World11":
                    doSomething = "Hello World0";
                    break;
                case "Hello World12":
                    doSomething = "Hello World0";
                    break;
                case "Hello World13":
                    doSomething = "Hello World0";
                    break;
                case "Hello World14":
                    doSomething = "Hello World0";
                    break;
                case "Hello World15":
                    doSomething = "Hello World0";
                    break;
            }
        }

        System.out.println("Time taken for String in Switch :"+ (System.currentTimeMillis() - start));




        //METHOD_2 : IF/ELSE IF
        start = System.currentTimeMillis();

        for (int i = 0; i < 99999999; i++) {
            String input = "Hello World" + (i & 0xF);

            if(input.equals("Hello World0")){
                doSomething = "Hello World0";
            } else if(input.equals("Hello World1")){
                doSomething = "Hello World0";

            } else if(input.equals("Hello World2")){
                doSomething = "Hello World0";

            } else if(input.equals("Hello World3")){
                doSomething = "Hello World0";

            } else if(input.equals("Hello World4")){
                doSomething = "Hello World0";

            } else if(input.equals("Hello World5")){
                doSomething = "Hello World0";

            } else if(input.equals("Hello World6")){
                doSomething = "Hello World0";

            } else if(input.equals("Hello World7")){
                doSomething = "Hello World0";

            } else if(input.equals("Hello World8")){
                doSomething = "Hello World0";

            } else if(input.equals("Hello World9")){
                doSomething = "Hello World0";

            } else if(input.equals("Hello World10")){
                doSomething = "Hello World0";

            } else if(input.equals("Hello World11")){
                doSomething = "Hello World0";

            } else if(input.equals("Hello World12")){
                doSomething = "Hello World0";

            } else if(input.equals("Hello World13")){
                doSomething = "Hello World0";

            } else if(input.equals("Hello World14")){
                doSomething = "Hello World0";

            } else if(input.equals("Hello World15")){
                doSomething = "Hello World0";

            }
        }
        System.out.println("Time taken for String in if/else if :"+ (System.currentTimeMillis() - start));









        //METHOD_3 : MAP
        //Create and build Map
        Map<String, ExecutableClass> map = new HashMap<String, ExecutableClass>();
        for (int i = 0; i <= 15; i++) {
            String input = "Hello World" + (i & 0xF);
            map.put(input, new ExecutableClass(){
                public void execute(String doSomething){
                    doSomething = "Hello World0";
                }
            });
        }


        //Start test map
        start = System.currentTimeMillis();
        for (int i = 0; i < 99999999; i++) {
            String input = "Hello World" + (i & 0xF);
            map.get(input).execute(doSomething);
        }
        System.out.println("Time taken for String in Map :"+ (System.currentTimeMillis() - start));






        //METHOD_4 : ENUM (This doesn't use muliple string with space.)
        start = System.currentTimeMillis();
        for (int i = 0; i < 99999999; i++) {
            String input = "HW" + (i & 0xF);
            HelloWorld.valueOf(input).execute(doSomething);
        }
        System.out.println("Time taken for String in ENUM :"+ (System.currentTimeMillis() - start));


    }

}

interface ExecutableClass
{
    public void execute(String doSomething);
}



// Enum version
enum HelloWorld {
    HW0("Hello World0"), HW1("Hello World1"), HW2("Hello World2"), HW3(
            "Hello World3"), HW4("Hello World4"), HW5("Hello World5"), HW6(
            "Hello World6"), HW7("Hello World7"), HW8("Hello World8"), HW9(
            "Hello World9"), HW10("Hello World10"), HW11("Hello World11"), HW12(
            "Hello World12"), HW13("Hello World13"), HW14("Hello World4"), HW15(
            "Hello World15");

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
    }
}


/*
 * http://stackoverflow.com/questions/338206/why-cant-i-switch-on-a-string
 * https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-3.html#jvms-3.10
 * http://forums.xkcd.com/viewtopic.php?f=11&t=33524
 */
