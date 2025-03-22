package com.example.asmplugin;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class MyClassVisitor extends ClassVisitor {
    MyClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM7, cv); // 使用ASM7版本
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        System.out.println("3");
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new MyMethodVisitor(mv,access,name,desc); // 返回自定义MethodVisitor
    }
}