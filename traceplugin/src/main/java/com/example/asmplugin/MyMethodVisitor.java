package com.example.asmplugin;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

class MyMethodVisitor extends AdviceAdapter {
    MyMethodVisitor(MethodVisitor mv,int access, String name, String desc) {
        super(ASM7, mv, access, name, desc);
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();

    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String methodName, String descriptor, boolean isInterface) {
        System.out.println("Visiting method: " + owner + "." + methodName + descriptor);
        // 检测是否调用了 Toast.show 方法
        if (owner.equals("android/widget/Toast") && methodName.equals("show")) {
            System.out.println("Skipping Toast.show()");
            // 直接跳过 Toast.show() 方法，因为 TraceUtil.showToast 已经包含了 show 逻辑
            // 替换 Toast.show 为 TraceUtil.showToast
            super.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "com/ajiang/example/traceutils/TraceUtil",
                    "showToast",
                    "(Landroid/widget/Toast;)V",
                    false
            );
        }
        else {
            super.visitMethodInsn(opcode, owner, methodName, descriptor, isInterface);
        }
    }
}