package com.jehad.progen;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;


@Command(name="project-generator", mixinStandardHelpOptions = true, version = "1.0",
description = "generate a basic java project with auth")
public class ProjectGenerator implements Runnable {

    @Option(names={"-n","--name"},defaultValue = "test cli",description = "Project name", required = true)
    private String projectName;

    @Option(names={"-d","destination"}, description = "Destination of project")
    private File destination;

    @Override
    public void run() {

        Scanner scanner =new Scanner(System.in);
        try {
            System.out.println("where want you to create the project. (ex:/home/user/projects) ?");
            String destinationPath = scanner.nextLine();
            File destination = new File(destinationPath);

            if (!destination.exists() || !destination.isDirectory()) {
                System.err.println("Error: Project directory not exists yet");
                return;
            }

            System.out.println("what is the name of project?");
            String projectName =scanner.nextLine();

            File projectDir= new File(destination,projectName);
            if (projectDir.exists()) {
                System.err.println("Error: Project directory already exists" + projectDir);
                return;
            }
            if (!projectDir.mkdirs()) {
                System.err.println("Error: Could not create project directory" + projectDir);
                return;
            }

            generateProjectStructure(projectDir);
            generateJavaFile(projectDir);
            generatePomFile(projectDir, projectName);

            System.out.println("Project successful created: " + projectDir.getAbsolutePath());

            System.out.println("Project '" + projectName + "' created successfully!");
        }catch (IOException e){

            e.printStackTrace();
        } finally {
            scanner.close();
        }

    }

    private void generateJavaFile(File projectDir) throws IOException {
        File mainJava = new File(projectDir, "src/main/java/Main.java");
        String template = Files.readString(Path.of("src/main/resources/template/main-template.java"));


        try (FileWriter appWriter = new FileWriter(mainJava)) {
            appWriter.write(template);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void generatePomFile(File projectDir,String projectName) throws IOException {
        File pomFile = new File(projectDir, "pom.xml");
        String template = Files.readString(Path.of("src/main/resources/template/pom-template.xml"));
        String content = template.replace("${projectName}",projectName);

        try(FileWriter writer = new FileWriter(pomFile)){
            writer.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateProjectStructure(File projectDir) throws IOException{
        Files.createDirectories(new File(projectDir, "src/main/java").toPath());
        Files.createDirectories(new File(projectDir, "src/main/resources").toPath());
        Files.createDirectories(new File(projectDir, "src/test/java").toPath());
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ProjectGenerator()).execute(args);
        System.exit(exitCode);
    }

}
