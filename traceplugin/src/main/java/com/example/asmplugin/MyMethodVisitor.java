package com.example.asmplugin;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

class MyMethodVisitor extends AdviceAdapter {
    MyMethodVisitor(MethodVisitor mv,int access, String name, String desc) {
        super(ASM7, mv, access, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String methodName, String descriptor, boolean isInterface) {
        System.out.println("Visiting method: " + owner + "." + methodName + descriptor);

        // 检测是否调用了 Toast.makeText 方法
        if (owner.equals("android/widget/Toast") && methodName.equals("makeText")) {
            if (descriptor.equals("(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;")) {
                System.out.println("Replacing Toast.makeText with TraceUtil.showToast");
                // 调用 TraceUtil.showToast
                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "com/xuexuan/androidaop/traceutils/TraceUtil",
                        "showToast",
                        "(Landroid/content/Context;Ljava/lang/CharSequence;I)V",
                        false);
                // 在栈上压入一个 null，以匹配 Toast.makeText 的返回值
                mv.visitInsn(Opcodes.ACONST_NULL);
            } else if (descriptor.equals("(Landroid/content/Context;II)Landroid/widget/Toast;")) {
                System.out.println("Replacing Toast.makeText with TraceUtil.showToast (int resId)");
                // 调用 TraceUtil.showToast
                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "com/xuexuan/androidaop/traceutils/TraceUtil",
                        "showToast",
                        "(Landroid/content/Context;II)V",
                        false);
                // 在栈上压入一个 null，以匹配 Toast.makeText 的返回值
                mv.visitInsn(Opcodes.ACONST_NULL);
            } else {
                super.visitMethodInsn(opcode, owner, methodName, descriptor, isInterface);
            }
        }
        // 检测是否调用了 Toast.show 方法
        else if (owner.equals("android/widget/Toast") && methodName.equals("show")) {
            System.out.println("Skipping Toast.show()");
            // 直接跳过 Toast.show() 方法，因为 TraceUtil.showToast 已经包含了 show 逻辑
            mv.visitInsn(Opcodes.POP); // 弹出栈顶的 Toast 对象
        } else {
            super.visitMethodInsn(opcode, owner, methodName, descriptor, isInterface);
        }
    }
}