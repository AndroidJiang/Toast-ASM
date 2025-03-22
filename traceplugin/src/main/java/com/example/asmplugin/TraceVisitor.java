package com.example.asmplugin;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * 对继承自AppCompatActivity的Activity进行插桩
 */

public class TraceVisitor extends ClassVisitor {

    /**
     * 类名
     */
    private String className;

    /**
     * 父类名
     */
    private String superName;

    /**
     * 该类实现的接口
     */
    private String[] interfaces;

    public TraceVisitor(String className, ClassVisitor classVisitor) {
        super(Opcodes.ASM7, classVisitor);
    }

    /**
     * ASM进入到类的方法时进行回调
     *
     * @param access
     * @param name       方法名
     * @param desc
     * @param signature
     * @param exceptions
     * @return
     */
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature,
                                     String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);

        methodVisitor = new AdviceAdapter(Opcodes.ASM5, methodVisitor, access, name, desc) {

            private boolean isInject() {
                //如果父类名是AppCompatActivity则拦截这个方法,实际应用中可以换成自己的父类例如BaseActivity
                if (superName.contains("AppCompatActivity")) {
                    return true;
                }
                return false;
            }


            @Override
            public void visitCode() {
                super.visitCode();

            }

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                return super.visitAnnotation(desc, visible);
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                super.visitFieldInsn(opcode, owner, name, desc);
            }


            /**
             * 方法开始之前回调
             */
            @Override
            protected void onMethodEnter() {
                if (isInject()) {
                    if ("onCreate".equals(name)) {
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitMethodInsn(INVOKESTATIC,
                                "com/xuexuan/androidaop/traceutils/TraceUtil",
                                "onActivityCreate", "(Landroid/app/Activity;)V",
                                false);
                    } else if ("onDestroy".equals(name)) {
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitMethodInsn(INVOKESTATIC, "com/xuexuan/androidaop/traceutils/TraceUtil"
                                , "onActivityDestroy", "(Landroid/app/Activity;)V", false);
                    }
                }
            }

            /**
             * 方法结束时回调
             * @param i
             */
            @Override
            protected void onMethodExit(int i) {
                super.onMethodExit(i);
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
        };
        return methodVisitor;

    }

    /**
     * 当ASM进入类时回调
     *
     * @param version
     * @param access
     * @param name       类名
     * @param signature
     * @param superName  父类名
     * @param interfaces 实现的接口名
     */
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
        this.superName = superName;
        this.interfaces = interfaces;
    }
}
