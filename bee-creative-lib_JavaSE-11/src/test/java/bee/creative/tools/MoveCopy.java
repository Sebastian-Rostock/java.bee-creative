// package bee.creative.tools;
//
// import java.io.File;
// import java.io.IOException;
// import java.nio.file.FileVisitResult;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.SimpleFileVisitor;
// import java.nio.file.StandardCopyOption;
// import java.nio.file.attribute.BasicFileAttributes;
// import java.nio.file.attribute.FileAttribute;
//
// public class MoveCopy {
//
// public MoveCopy(final Path sourcePath, final Path targetPath) throws IOException {
// System.out.println(sourcePath);
// System.out.println(targetPath);
// Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
//
// @Override
// public FileVisitResult visitFile(final Path source, final BasicFileAttributes attrs) throws IOException {
// Path other = sourcePath.relativize(source);
// Path target = targetPath.resolve(other);
// System.out.println(source);
// System.out.println(other);
// System.out.println(target);
// Path parent = target.getParent();
// if (parent != null) Files.createDirectories(parent);
// Files.copy(source, target, StandardCopyOption.COPY_ATTRIBUTES);
// Files.deleteIfExists(source);
//
// return FileVisitResult.CONTINUE;
// }
//
// });
//
// }
//
// public static void main(final String[] args) throws IOException {
//
// new MoveCopy(new File("D:\\projects\\java\\bee-creative\\src\\test\\resources\\mc\\from").toPath(),
// new File("D:\\projects\\java\\bee-creative\\src\\test\\resources\\mc\\to").toPath());
//
// }
//
// }
