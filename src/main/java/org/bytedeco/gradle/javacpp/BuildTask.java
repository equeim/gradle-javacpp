/*
 * Copyright (C) 2020 Samuel Audet
 *
 * Licensed either under the Apache License, Version 2.0, or (at your option)
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation (subject to the "Classpath" exception),
 * either version 2, or any later version (collectively, the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     http://www.gnu.org/licenses/
 *     http://www.gnu.org/software/classpath/license.html
 *
 * or as provided in the LICENSE.txt file that accompanied this code.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bytedeco.gradle.javacpp;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.tools.Builder;
import org.bytedeco.javacpp.tools.Logger;
import org.bytedeco.javacpp.tools.ParserException;
import org.bytedeco.javacpp.tools.Slf4jLogger;
import org.gradle.api.DefaultTask;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

/**
 * A Gradle task that wraps {@link Builder}.
 *
 * @author Samuel Audet
 */
public class BuildTask extends DefaultTask {
    /** Load user classes from classPath. */
    String[] classPath = null;

    /** Add the path to the "platform.includepath" property. */
    String[] includePath = null;

    /** Add the path to the "platform.includeresource" property. */
    String[] includeResource = null;

    /** Add the path to the "platform.buildpath" property. */
    String[] buildPath = null;

    /** Add the path to the "platform.buildresource" property. */
    String[] buildResource = null;

    /** Add the path to the "platform.linkpath" property. */
    String[] linkPath = null;

    /** Add the path to the "platform.linkresource" property. */
    String[] linkResource = null;

    /** Add the path to the "platform.preloadpath" property. */
    String[] preloadPath = null;

    /** Add the path to the "platform.preloadresource" property. */
    String[] preloadResource = null;

    /** Add the path to the "platform.resourcepath" property. */
    String[] resourcePath = null;

    /** Add the path to the "platform.executablepath" property. */
    String[] executablePath = null;

    /** Specify the character encoding used for input and output. */
    String encoding = null;

    /** Output all generated files to outputDirectory. */
    File outputDirectory = null;

    /** Output everything in a file named after given outputName. */
    String outputName = null;

    /** Delete all files from {@link #outputDirectory} before generating anything in it. */
    boolean clean = false;

    /** Generate .cpp files from Java interfaces if found, parsing from header files if not. */
    boolean generate = true;

    /** Compile and delete the generated .cpp files. */
    boolean compile = true;

    /** Delete generated C++ JNI files after compilation */
    boolean deleteJniFiles = true;

    /** Generate header file with declarations of callbacks functions. */
    boolean header = false;

    /** Copy to output directory dependent libraries (link and preload). */
    boolean copyLibs = false;

    /** Copy to output directory resources listed in properties. */
    boolean copyResources = false;

    /** Also create config files for GraalVM native-image in directory. */
    File configDirectory = null;

    /** Also create a JAR file named {@code <jarPrefix>-<platform>.jar}. */
    String jarPrefix = null;

    /** Load all properties from resource. */
    String properties = null;

    /** Load all properties from file. */
    File propertyFile = null;

    /** Set property keys to values. */
    Properties propertyKeysAndValues = null;

    /** Process only these classes or packages (suffixed with .* or .**). */
    String[] classOrPackageNames = null;

    /** Execute a build command instead of JavaCPP itself, and return. */
    String[] buildCommand = null;

    /** Add to Maven project source directory of Java files generated by buildCommand. */
    String[] targetDirectory = null;

    /** Set the working directory of the build subprocess. */
    File workingDirectory = null;

    /** Add environment variables to the compiler subprocess. */
    Map<String,String> environmentVariables = null;

    /** Pass compilerOptions directly to compiler. */
    String[] compilerOptions = null;

    /** Skip the execution. */
    boolean skip = false;

    public BuildTask() {
        // disable incremental builds until we get proper support for them
        getOutputs().upToDateWhen(t -> false);
    }

    @Optional @Classpath
         @InputFiles public String[] getClassPath()       { return classPath;       } public void setClassPath      (String[] s) { classPath       = s; }
    @Optional @Input public String[] getIncludePath()     { return includePath;     } public void setIncludePath    (String[] s) { includePath     = s; }
    @Optional @Input public String[] getIncludeResource() { return includeResource; } public void setIncludeResource(String[] s) { includeResource = s; }
    @Optional @Input public String[] getBuildPath()       { return buildPath;       } public void setBuildPath      (String[] s) { buildPath       = s; }
    @Optional @Input public String[] getBuildResource()   { return buildResource;   } public void setBuildResource  (String[] s) { buildResource   = s; }
    @Optional @Input public String[] getLinkPath()        { return linkPath;        } public void setLinkPath       (String[] s) { linkPath        = s; }
    @Optional @Input public String[] getLinkResource()    { return linkResource;    } public void setLinkResource   (String[] s) { linkResource    = s; }
    @Optional @Input public String[] getPreloadPath()     { return preloadPath;     } public void setPreloadPath    (String[] s) { preloadPath     = s; }
    @Optional @Input public String[] getPreloadResource() { return preloadResource; } public void setPreloadResource(String[] s) { preloadResource = s; }
    @Optional @Input public String[] getResourcePath()    { return resourcePath;    } public void setResourcePath   (String[] s) { resourcePath    = s; }
    @Optional @Input public String[] getExecutablePath()  { return executablePath;  } public void setExecutablePath (String[] s) { executablePath  = s; }
    @Optional @Input public String   getEncoding()        { return encoding;        } public void setEncoding       (String   s) { encoding        = s; }
    @Optional
    @OutputDirectory public File     getOutputDirectory() { return outputDirectory; } public void setOutputDirectory(File f)     { outputDirectory = f; }
    @Optional @Input public String   getOutputName()      { return outputName;      } public void setOutputName     (String s)   { outputName      = s; }
              @Input public boolean  getClean()           { return clean;           } public void setClean          (boolean b)  { clean           = b; }
              @Input public boolean  getGenerate()        { return generate;        } public void setGenerate       (boolean b)  { generate        = b; }
              @Input public boolean  getCompile()         { return compile;         } public void setCompile        (boolean b)  { compile         = b; }
              @Input public boolean  getDeleteJniFiles()  { return deleteJniFiles;  } public void setDeleteJniFiles (boolean b)  { deleteJniFiles  = b; }
              @Input public boolean  getHeader()          { return header;          } public void setHeader         (boolean b)  { header          = b; }
              @Input public boolean  getCopyLibs()        { return copyLibs;        } public void setCopyLibs       (boolean b)  { copyLibs        = b; }
              @Input public boolean  getCopyResources()   { return copyResources;   } public void setCopyResources  (boolean b)  { copyResources   = b; }
    @Optional
    @OutputDirectory public File     getConfigDirectory() { return configDirectory; } public void setConfigDirectory(File f)     { configDirectory = f; }
    @Optional @Input public String   getJarPrefix()       { return jarPrefix;       } public void setJarPrefix      (String s)   { jarPrefix       = s; }
    @Optional @Input public String   getProperties()      { return properties;      } public void setProperties     (String s)   { properties      = s; }
    @Optional @InputFile public File getPropertyFile()    { return propertyFile;    } public void setPropertyFile   (File f)     { propertyFile    = f; }
    @Optional @Input public Properties getPropertyKeysAndValues() { return propertyKeysAndValues; } public void setPropertyKeysAndValues(Properties p) { propertyKeysAndValues = p; }
    @Optional @Input public String[]   getClassOrPackageNames()   { return classOrPackageNames;   } public void setClassOrPackageNames  (String[] s)   { classOrPackageNames   = s; }
    @Optional @Input public String[]   getBuildCommand()          { return buildCommand;          } public void setBuildCommand         (String[] s)   { buildCommand          = s; }
    @Optional @Input public String[]   getTargetDirectory()       { return targetDirectory;       } public void setTargetDirectory      (String[] s)   { targetDirectory       = s; }
    @Optional
    @InputDirectory  public File       getWorkingDirectory()      { return workingDirectory;      } public void setWorkingDirectory     (File f)       { workingDirectory      = f; }
    @Optional @Input public Map        getEnvironmentVariables()  { return environmentVariables;  } public void setEnvironmentVariables (Map m)        { environmentVariables  = m; }
    @Optional @Input public String[]   getCompilerOptions()       { return compilerOptions;       } public void setCompilerOptions      (String[] s)   { compilerOptions       = s; }
              @Input public boolean    getSkip()                  { return skip;                  } public void setSkip                 (boolean b)    { skip                  = b; }

    @TaskAction public void build() throws IOException, ClassNotFoundException, NoClassDefFoundError, InterruptedException, ParserException {
        Logger logger = new Slf4jLogger(Builder.class);

        if (getSkip()) {
            logger.info("Skipping execution of JavaCPP Builder");
            return;
        }

        Builder builder = new Builder(logger)
                .classPaths(getClassPath())
                .encoding(getEncoding())
                .outputDirectory(getOutputDirectory())
                .outputName(getOutputName())
                .clean(getClean())
                .generate(getGenerate())
                .compile(getCompile())
                .deleteJniFiles(getDeleteJniFiles())
                .header(getHeader())
                .copyLibs(getCopyLibs())
                .copyResources(getCopyResources())
                .configDirectory(getConfigDirectory())
                .jarPrefix(getJarPrefix())
                .properties(getProperties())
                .propertyFile(getPropertyFile())
                .properties(getPropertyKeysAndValues())
                .classesOrPackages(getClassOrPackageNames())
                .buildCommand(getBuildCommand())
                .workingDirectory(getWorkingDirectory())
                .environmentVariables(getEnvironmentVariables())
                .compilerOptions(getCompilerOptions())
                .commandExecutor(new BuildExecutor(logger))

                .addProperty("platform.buildpath", getBuildPath())
                .addProperty("platform.buildresource", getBuildResource())
                .addProperty("platform.includepath", getIncludePath())
                .addProperty("platform.includeresource", getIncludeResource())
                .addProperty("platform.linkpath", getLinkPath())
                .addProperty("platform.linkresource", getLinkResource())
                .addProperty("platform.preloadpath", getPreloadPath())
                .addProperty("platform.preloadresource", getPreloadResource())
                .addProperty("platform.resourcepath", getResourcePath())
                .addProperty("platform.executablepath", getExecutablePath());

        String extension = builder.getProperty("platform.extension");
        getLogger().info("Detected platform \"" + Loader.Detector.getPlatform() + "\"");
        getLogger().info("Building platform \"" + builder.getProperty("platform") + "\""
                + (extension != null && extension.length() > 0 ? " with extension \"" + extension + "\"" : ""));

        ExtraPropertiesExtension projectProperties = getProject().getExtensions().getExtraProperties();
        for (String key : builder.getProperties().stringPropertyNames()) {
            projectProperties.set("javacpp." + key, builder.getProperties().getProperty(key));
        }

        File[] outputFiles = builder.build();

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("outputFiles: " + Arrays.deepToString(outputFiles));
        }
    }
}
