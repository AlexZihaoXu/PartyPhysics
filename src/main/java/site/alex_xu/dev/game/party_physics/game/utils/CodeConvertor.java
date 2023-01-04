package site.alex_xu.dev.game.party_physics.game.utils;

public class CodeConvertor {
    public static String ip2code(String ip) {

        if (ip.equalsIgnoreCase("localhost")) {
            ip = "127.0.0.1";
        }

        String code1 = int2b260(ip2num(ip));
        StringBuilder front = new StringBuilder();
        StringBuilder end = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            front.append(code1.charAt(i * 2));
            end.append(code1.charAt(i * 2 + 1));
        }

        front.append(' ').append(end);

        return front.toString().toUpperCase();
    }

    public static String code2ip(String code) {
        code = code.replace(" ", "");
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            s.append(code.charAt(i));
            s.append(code.charAt(i + 4));
        }
        return num2ip(b2602int(s.toString()));
    }

    private static long ip2num(String ip) {
        String[] nums = ip.split("\\.");
        return ip2num(Integer.parseInt(nums[0]), Integer.parseInt(nums[1]), Integer.parseInt(nums[2]), Integer.parseInt(nums[3]));
    }

    private static long ip2num(int a, int b, int c, int d) {
        return (((a * 255L) + b) * 255 + c) * 255 + d;
    }

    private static String num2ip(long num) {
        int[] ip = new int[4];
        for (int i = 3; i >= 0; i--) {
            ip[i] = (int) (num % 255);
            num /= 255;
        }
        return ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3];
    }

    private static char intMapChar(long num) {
        return (char) (num + 'a');
    }

    private static int charMapInt(char c) {
        if ('A' <= c && c <= 'Z') {
            c += 'a' - 'A';
        }
        return c - 'a';
    }

    private static String intMapB260(long num) {
        return "" + (num / 26) % 10 + intMapChar(num % 26);
    }

    private static int b260MapInt(String b260) {
        int cnum = charMapInt(b260.charAt(1));
        int num = b260.charAt(0) - '0';
        return num * 26 + cnum;
    }

    private static String int2b260(long num) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            result.insert(0, intMapB260(num));
            num /= 260;
        }
        return result.toString();
    }

    private static long b2602int(String b260) {
        long result = 0;
        for (int i = 0; i < b260.length(); i += 2) {
            String s = b260.substring(i, i + 2);
            result *= 260;
            result += b260MapInt(s);
        }
        return result;
    }
}
