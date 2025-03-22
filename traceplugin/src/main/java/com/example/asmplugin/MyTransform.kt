package com.example.asmplugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class MyTransform : Transform() {
    override fun getName() = "MyTransform" // Transform名称

    override fun getInputTypes() = TransformManager.CONTENT_CLASS // 处理.class文件

    override fun getScopes() = TransformManager.SCOPE_FULL_PROJECT // 作用范围：全项目

    override fun isIncremental() = false // 是否支持增量编译

    override fun transform(transformInvocation: TransformInvocation) {
        transformInvocation.inputs.forEach { input ->
            input.jarInputs.forEach { jarInput ->
                processJar(jarInput, transformInvocation.outputProvider) // 处理Jar/AAR文件
            }
            input.directoryInputs.forEach { dirInput ->
                processDirectory(dirInput, transformInvocation.outputProvider) // 处理目录中的.class文件
            }
        }
    }

    private fun processJar(jarInput: JarInput, outputProvider: TransformOutputProvider) {
        val outputJar = outputProvider.getContentLocation(
            jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR
        )
        val tempDir = createTempDir() // 创建一个临时目录

        if (jarInput.file.extension == "aar") {
            // 处理AAR文件
            processAar(jarInput.file, tempDir, outputJar)
        } else {
            // 处理普通Jar文件
            processRegularJar(jarInput.file, tempDir, outputJar)
        }

        // 删除临时目录
        tempDir.deleteRecursively()
    }

    private fun processAar(aarFile: File, tempDir: File, outputAar: File) {
        // 1. 解压AAR文件到临时目录
        unzipFile(aarFile, tempDir)

        // 2. 处理AAR中的classes.jar
        val classesJar = File(tempDir, "classes.jar")
        if (classesJar.exists()) {
            val classesTempDir = createTempDir()
            unzipFile(classesJar, classesTempDir) // 解压classes.jar

            // 修改classes.jar中的字节码
            classesTempDir.walk().forEach { file ->
                if (file.isClassFile()) {
                    modifyClassFile(file) // 使用ASM修改字节码
                }
            }

            // 重新打包classes.jar
            zipToJar(classesTempDir, classesJar)
            classesTempDir.deleteRecursively()
        }

        // 3. 重新打包AAR文件
        zipToAar(tempDir, outputAar)
    }

    private fun processRegularJar(jarFile: File, tempDir: File, outputJar: File) {
        // 1. 解压Jar文件到临时目录
        unzipFile(jarFile, tempDir)

        // 2. 遍历临时目录中的.class文件，逐个修改
        tempDir.walk().forEach { file ->
            if (file.isClassFile()) {
                modifyClassFile(file) // 使用ASM修改字节码
            }
        }

        // 3. 将修改后的文件重新打包成Jar
        zipToJar(tempDir, outputJar)
    }

    private fun processDirectory(dirInput: DirectoryInput, outputProvider: TransformOutputProvider) {
        val outputDir = outputProvider.getContentLocation(
            dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY
        )
        dirInput.file.walk().forEach { file ->
            if (file.isClassFile()) {
                modifyClassFile(file) // 修改.class文件
            }
        }
        FileUtils.copyDirectory(dirInput.file, outputDir) // 将目录复制到目标目录
    }
    fun modifyClassFile(classFile: File) {
        val classReader = ClassReader(classFile.readBytes()) // 读取.class文件
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS) // 创建ClassWriter
        val classVisitor = MyClassVisitor(classWriter) // 自定义ClassVisitor
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES) // 访问并修改字节码

        val modifiedBytes = classWriter.toByteArray() // 获取修改后的字节码
        classFile.writeBytes(modifiedBytes) // 写回文件
    }
    fun unzipFile(inputFile: File, outputDir: File) {
        ZipFile(inputFile).use { zip ->
            zip.entries().iterator().forEach { entry ->
                val outputFile = File(outputDir, entry.name)
                if (entry.isDirectory) {
                    outputFile.mkdirs()
                } else {
                    outputFile.parentFile.mkdirs()
                    zip.getInputStream(entry).use { input ->
                        outputFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }
        }
    }
    fun zipToJar(inputDir: File, outputJar: File) {
        ZipOutputStream(outputJar.outputStream()).use { zip ->
            inputDir.walk().forEach { file ->
                if (file.isFile) {
                    val entryName = file.relativeTo(inputDir).path
                    zip.putNextEntry(ZipEntry(entryName))
                    file.inputStream().use { input ->
                        input.copyTo(zip)
                    }
                    zip.closeEntry()
                }
            }
        }
    }
    fun zipToAar(inputDir: File, outputAar: File) {
        ZipOutputStream(outputAar.outputStream()).use { zip ->
            inputDir.walk().forEach { file ->
                if (file.isFile) {
                    val entryName = file.relativeTo(inputDir).path
                    zip.putNextEntry(ZipEntry(entryName))
                    file.inputStream().use { input ->
                        input.copyTo(zip)
                    }
                    zip.closeEntry()
                }
            }
        }
    }
}