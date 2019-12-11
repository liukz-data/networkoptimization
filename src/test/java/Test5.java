public class Test5 {
    public static void main(String[] args) {
        String a = "aaa";
        String b = "aaa";
        System.out.println(a==b);
        String c = new String("aaa").intern();
        System.out.println(a==c);
        String d = new String("aaa").intern();
        String e = new String("aaa").intern();
        System.out.println(d==e);
    }
}
