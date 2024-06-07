import java.io.File;

public class Test {
    File dir = new File(FileServer.UPLOAD_DIR);
    String[] files = dir.list();
//        System.out.print(files.length);
//        for (String file : files) {
//        dataOutput.writeUTF(file);
    public static void main(String[] args) {
        File dir = new File(FileServer.UPLOAD_DIR);
        String[] files = dir.list();
        System.out.println(files.length);
        for (String file : files) {
            System.out.println(file);
        }
    }
}
