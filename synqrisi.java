//usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS com.github.siom79.japicmp:japicmp:0.14.3
//DEPS org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api:3.1.4
//DEPS org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-maven:3.1.4
//DEPS org.slf4j:slf4j-nop:1.7.25
//DEPS info.picocli:picocli:4.2.0

import static java.lang.System.*;

import japicmp.JApiCmp;
import japicmp.cli.JApiCli;
import org.apache.maven.settings.Settings;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Unmatched;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Command(name = "{baseName}", version = "{baseName} 0.1",
        description = "{baseName} made with jbang")
public class synqrisi implements Callable<Integer> {

    @Option(names={ "--old", "-o" })
    String oldGAV;

    @Option(names={ "--new", "-n"})
    String newGAV;

    @Unmatched
    List<String> unmatched = new ArrayList<String>();

    static File fetchJar(String gav) {

        ConfigurableMavenResolverSystem resolver = Maven.configureResolver()
                .withMavenCentralRepo(true);

        return resolver.resolve(gav).withoutTransitivity()
                .asSingleFile();
    }

    public static void main(String... args) {
        new CommandLine(new synqrisi()).execute(args);
    }

    @Override
    public Integer call() throws Exception {
        List<String> args = new ArrayList<>();
        if(oldGAV!=null) {
            File oldjar = fetchJar(oldGAV);
            args.add("-o");
            args.add(oldjar.getAbsolutePath());
        }

        if(newGAV!=null) {
            File newjar = fetchJar(newGAV);
            args.add("-n");
            args.add(newjar.getAbsolutePath());
        }

        args.addAll(unmatched);
        JApiCmp.main(args.toArray(new String[0]));

        return 0;
    }
}
