/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import gradlebuild.startscript.tasks.GradleStartScriptGenerator

plugins {
    java
}

val manifestClasspath by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
    isTransitive = false

    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named<Usage>(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named<Category>(Category.LIBRARY))
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named<LibraryElements>(LibraryElements.JAR))
    }
}

tasks.jar.configure {
    val classpath = manifestClasspath.elements.map { classpathDependency ->
        classpathDependency.joinToString(" ") {
            it.asFile.name
        }
    }
    manifest.attributes("Class-Path" to classpath)
    manifest.attributes("Main-Class" to "org.gradle.launcher.GradleMain")
}


val startScripts = tasks.register<GradleStartScriptGenerator>("startScripts") {
    startScriptsDir.set(layout.buildDirectory.dir("startScripts"))
    launcherJar.from(tasks.jar)
}

configurations {
    create("gradleScriptsElements") {
        isVisible = false
        isCanBeResolved = false
        isCanBeConsumed = true
        attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named<Usage>("start-scripts"))
        outgoing.artifact(startScripts)
    }
}
