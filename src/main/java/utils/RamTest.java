package utils;

public class RamTest {

    public static void main(String[] args) {
        Runtime rt = Runtime.getRuntime();

        System.out.println(rt.availableProcessors());
        System.out.println((rt.freeMemory()/1024/1024));
        System.out.println((rt.totalMemory()/1024/1024));
    }

    public static void printUsingRAM(){
        Runtime rt = Runtime.getRuntime();
        System.out.println("Free RAM (mb): "+(rt.freeMemory()/1024/1024));
        System.out.println("Total RAM (mb): "+ (rt.totalMemory()/1024/1024));
        System.out.println("Using RAM (mb): "+ ((rt.totalMemory()/1024/1024) - (rt.freeMemory()/1024/1024)));

    }

    public static String getUsingRAM(){
        Runtime rt = Runtime.getRuntime();
        return "|Total RAM (mb): "+ (rt.totalMemory()/1024/1024)+"| Free RAM (mb): "+(rt.freeMemory()/1024/1024) +"|"
                +" Using RAM (mb): "+ ((rt.totalMemory()/1024/1024) - (rt.freeMemory()/1024/1024))+"|";
    }
}
