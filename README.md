# Kotlin Compiler Plugin Sample

## Summary
This is a sample project using the kotlin compiler plugin. I used the [Tracer-Kotlin-Compiler-Plugin](https://github.com/acejingbo/Tracer-Kotlin-Compiler-Plugin) project as a reference. It consists of the following two projects. You can add `@HelloWorld` annotation to a function, `Hello World` will be printed. 

- HelloWorldPlugin
  - HelloWorldCompilerPlugin.kt
  - HelloWorldGradlePlugin.kt
  - HelloWorldTreeVisitor.kt
- HelloWorldPluginSample
  - Main.kt
    
## Usage
First, run the publish task of HelloWorldPlugin.<br>
<img src="https://raw.githubusercontent.com/tommykw/kotlin-compiler-plugin-sample/main/images/hello-world-plugin-publish.png" width="320">

A `maven-repo` will be generated in the root directory.<br>
<img src="https://raw.githubusercontent.com/tommykw/kotlin-compiler-plugin-sample/main/images/hello-world-plugin-maven-repo.png" width="320">

Next, run the `build` task of HelloWorldPluginSample.<br>
<img src="https://raw.githubusercontent.com/tommykw/kotlin-compiler-plugin-sample/main/images/hello-world-plugin-build.png" width="320">

Finally, run the Main.kt of HelloWorldPluginSample will output `Hello World`.<br>
<img src="https://raw.githubusercontent.com/tommykw/kotlin-compiler-plugin-sample/main/images/hello-world-plugin-sample-main.png" width="480">

The files generated by HelloWorldPlugin are located under `HelloWorldPluginSample/build/generated/ktPlugin/`.
You can see that `println("Hello World")` has been added to `foo()` in the generated Main.kt.<br>
<img src="https://raw.githubusercontent.com/tommykw/kotlin-compiler-plugin-sample/main/images/hello-world-plugin-sample-generated-main.png" width="480">
