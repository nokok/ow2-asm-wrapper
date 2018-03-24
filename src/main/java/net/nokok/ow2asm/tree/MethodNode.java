// ASM: a very small and fast Java bytecode manipulation framework
// Copyright (c) 2000-2011 INRIA, France Telecom
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// 1. Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
// 3. Neither the name of the copyright holders nor the names of its
//    contributors may be used to endorse or promote products derived from
//    this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
// THE POSSIBILITY OF SUCH DAMAGE.
package net.nokok.ow2asm.tree;

import net.nokok.ow2asm.AnnotationVisitor;
import net.nokok.ow2asm.Attribute;
import net.nokok.ow2asm.ClassVisitor;
import net.nokok.ow2asm.Handle;
import net.nokok.ow2asm.Label;
import net.nokok.ow2asm.MethodVisitor;
import net.nokok.ow2asm.Opcodes;
import net.nokok.ow2asm.Type;
import net.nokok.ow2asm.TypePath;

import java.util.ArrayList;
import java.util.List;

/**
 * A node that represents a method.
 *
 * @author Eric Bruneton
 */
public class MethodNode extends MethodVisitor {

    /**
     * The method's access flags (see {@link Opcodes}). This field also indicates if the method is
     * synthetic and/or deprecated.
     */
    public int access;

    /**
     * The method's name.
     */
    public String name;

    /**
     * The method's descriptor (see {@link Type}).
     */
    public String desc;

    /**
     * The method's signature. May be <tt>null</tt>.
     */
    public String signature;

    /**
     * The internal names of the method's exception classes (see {@link Type#getInternalName()}).
     */
    public List<String> exceptions;

    /**
     * The method parameter info (access flags and name)
     */
    public List<ParameterNode> parameters;

    /**
     * The runtime visible annotations of this method. May be <tt>null</tt>.
     */
    public List<AnnotationNode> visibleAnnotations;

    /**
     * The runtime invisible annotations of this method. May be <tt>null</tt>.
     */
    public List<AnnotationNode> invisibleAnnotations;

    /**
     * The runtime visible type annotations of this method. May be <tt>null</tt>.
     */
    public List<TypeAnnotationNode> visibleTypeAnnotations;

    /**
     * The runtime invisible type annotations of this method. May be <tt>null</tt>.
     */
    public List<TypeAnnotationNode> invisibleTypeAnnotations;

    /**
     * The non standard attributes of this method. May be <tt>null</tt>.
     */
    public List<Attribute> attrs;

    /**
     * The default value of this annotation interface method. This field must be a {@link Byte},
     * {@link Boolean}, {@link Character}, {@link Short}, {@link Integer}, {@link Long}, {@link
     * Float}, {@link Double}, {@link String} or {@link Type}, or an two elements String array (for
     * enumeration values), a {@link AnnotationNode}, or a {@link List} of values of one of the
     * preceding types. May be <tt>null</tt>.
     */
    public Object annotationDefault;

    /**
     * The number of method parameters than can have runtime visible annotations. This number must be
     * less or equal than the number of parameter types in the method descriptor (the default value 0
     * indicates that all the parameters described in the method descriptor can have annotations). It
     * can be strictly less when a method has synthetic parameters and when these parameters are
     * ignored when computing parameter indices for the purpose of parameter annotations (see
     * https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7.18).
     */
    public int visibleAnnotableParameterCount;

    /**
     * The runtime visible parameter annotations of this method. These lists are lists of {@link
     * AnnotationNode} objects. May be <tt>null</tt>.
     */
    public List<AnnotationNode>[] visibleParameterAnnotations;

    /**
     * The number of method parameters than can have runtime invisible annotations. This number must
     * be less or equal than the number of parameter types in the method descriptor (the default value
     * 0 indicates that all the parameters described in the method descriptor can have annotations).
     * It can be strictly less when a method has synthetic parameters and when these parameters are
     * ignored when computing parameter indices for the purpose of parameter annotations (see
     * https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7.18).
     */
    public int invisibleAnnotableParameterCount;

    /**
     * The runtime invisible parameter annotations of this method. These lists are lists of {@link
     * AnnotationNode} objects. May be <tt>null</tt>.
     */
    public List<AnnotationNode>[] invisibleParameterAnnotations;

    /**
     * The instructions of this method.
     */
    public InsnList instructions;

    /**
     * The try catch blocks of this method.
     */
    public List<TryCatchBlockNode> tryCatchBlocks;

    /**
     * The maximum stack size of this method.
     */
    public int maxStack;

    /**
     * The maximum number of local variables of this method.
     */
    public int maxLocals;

    /**
     * The local variables of this method. May be <tt>null</tt>
     */
    public List<LocalVariableNode> localVariables;

    /**
     * The visible local variable annotations of this method. May be <tt>null</tt>
     */
    public List<LocalVariableAnnotationNode> visibleLocalVariableAnnotations;

    /**
     * The invisible local variable annotations of this method. May be <tt>null</tt>
     */
    public List<LocalVariableAnnotationNode> invisibleLocalVariableAnnotations;

    /**
     * Whether the accept method has been called on this object.
     */
    private boolean visited;

    /**
     * Constructs an uninitialized {@link MethodNode}. <i>Subclasses must not use this
     * constructor</i>. Instead, they must use the {@link #MethodNode(int)} version.
     *
     * @throws IllegalStateException If a subclass calls this constructor.
     */
    public MethodNode() {
        this(Opcodes.ASM6);
        if (getClass() != MethodNode.class) {
            throw new IllegalStateException();
        }
    }

    /**
     * Constructs an uninitialized {@link MethodNode}.
     *
     * @param api the ASM API version implemented by this visitor. Must be one of {@link
     *            Opcodes#ASM4}, {@link Opcodes#ASM5} or {@link Opcodes#ASM6}.
     */
    public MethodNode(final int api) {
        super(api);
        this.instructions = new InsnList();
    }

    /**
     * Constructs a new {@link MethodNode}. <i>Subclasses must not use this constructor</i>. Instead,
     * they must use the {@link #MethodNode(int, int, String, String, String, String[])} version.
     *
     * @param access     the method's access flags (see {@link Opcodes}). This parameter also indicates if
     *                   the method is synthetic and/or deprecated.
     * @param name       the method's name.
     * @param descriptor the method's descriptor (see {@link Type}).
     * @param signature  the method's signature. May be <tt>null</tt>.
     * @param exceptions the internal names of the method's exception classes (see {@link
     *                   Type#getInternalName()}). May be <tt>null</tt>.
     * @throws IllegalStateException If a subclass calls this constructor.
     */
    public MethodNode(
            final int access,
            final String name,
            final String descriptor,
            final String signature,
            final String[] exceptions) {
        this(Opcodes.ASM6, access, name, descriptor, signature, exceptions);
        if (getClass() != MethodNode.class) {
            throw new IllegalStateException();
        }
    }

    /**
     * Constructs a new {@link MethodNode}.
     *
     * @param api        the ASM API version implemented by this visitor. Must be one of {@link
     *                   Opcodes#ASM4}, {@link Opcodes#ASM5} or {@link Opcodes#ASM6}.
     * @param access     the method's access flags (see {@link Opcodes}). This parameter also indicates if
     *                   the method is synthetic and/or deprecated.
     * @param name       the method's name.
     * @param descriptor the method's descriptor (see {@link Type}).
     * @param signature  the method's signature. May be <tt>null</tt>.
     * @param exceptions the internal names of the method's exception classes (see {@link
     *                   Type#getInternalName()}). May be <tt>null</tt>.
     */
    public MethodNode(
            final int api,
            final int access,
            final String name,
            final String descriptor,
            final String signature,
            final String[] exceptions) {
        super(api);
        this.access = access;
        this.name = name;
        this.desc = descriptor;
        this.signature = signature;
        this.exceptions = Util.asArrayList(exceptions);
        if ((access & Opcodes.ACC_ABSTRACT) == 0) {
            this.localVariables = new ArrayList<LocalVariableNode>(5);
        }
        this.tryCatchBlocks = new ArrayList<TryCatchBlockNode>();
        this.instructions = new InsnList();
    }

    // -----------------------------------------------------------------------------------------------
    // Implementation of the MethodVisitor abstract class
    // -----------------------------------------------------------------------------------------------

    @Override
    public void visitParameter(final String name, final int access) {
        if (parameters == null) {
            parameters = new ArrayList<ParameterNode>(5);
        }
        parameters.add(new ParameterNode(name, access));
    }

    @Override
    @SuppressWarnings("serial")
    public AnnotationVisitor visitAnnotationDefault() {
        return new AnnotationNode(
                new ArrayList<Object>(0) {
                    @Override
                    public boolean add(final Object o) {
                        annotationDefault = o;
                        return super.add(o);
                    }
                });
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        AnnotationNode annotation = new AnnotationNode(descriptor);
        if (visible) {
            if (visibleAnnotations == null) {
                visibleAnnotations = new ArrayList<AnnotationNode>(1);
            }
            visibleAnnotations.add(annotation);
        } else {
            if (invisibleAnnotations == null) {
                invisibleAnnotations = new ArrayList<AnnotationNode>(1);
            }
            invisibleAnnotations.add(annotation);
        }
        return annotation;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(
            final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
        TypeAnnotationNode typeAnnotation = new TypeAnnotationNode(typeRef, typePath, descriptor);
        if (visible) {
            if (visibleTypeAnnotations == null) {
                visibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
            }
            visibleTypeAnnotations.add(typeAnnotation);
        } else {
            if (invisibleTypeAnnotations == null) {
                invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
            }
            invisibleTypeAnnotations.add(typeAnnotation);
        }
        return typeAnnotation;
    }

    @Override
    public void visitAnnotableParameterCount(final int parameterCount, final boolean visible) {
        if (visible) {
            visibleAnnotableParameterCount = parameterCount;
        } else {
            invisibleAnnotableParameterCount = parameterCount;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public AnnotationVisitor visitParameterAnnotation(
            final int parameter, final String descriptor, final boolean visible) {
        AnnotationNode annotation = new AnnotationNode(descriptor);
        if (visible) {
            if (visibleParameterAnnotations == null) {
                int params = Type.getArgumentTypes(desc).length;
                visibleParameterAnnotations = (List<AnnotationNode>[]) new List<?>[params];
            }
            if (visibleParameterAnnotations[parameter] == null) {
                visibleParameterAnnotations[parameter] = new ArrayList<AnnotationNode>(1);
            }
            visibleParameterAnnotations[parameter].add(annotation);
        } else {
            if (invisibleParameterAnnotations == null) {
                int params = Type.getArgumentTypes(desc).length;
                invisibleParameterAnnotations = (List<AnnotationNode>[]) new List<?>[params];
            }
            if (invisibleParameterAnnotations[parameter] == null) {
                invisibleParameterAnnotations[parameter] = new ArrayList<AnnotationNode>(1);
            }
            invisibleParameterAnnotations[parameter].add(annotation);
        }
        return annotation;
    }

    @Override
    public void visitAttribute(final Attribute attribute) {
        if (attrs == null) {
            attrs = new ArrayList<Attribute>(1);
        }
        attrs.add(attribute);
    }

    @Override
    public void visitCode() {
        // Nothing to do.
    }

    @Override
    public void visitFrame(
            final int type,
            final int nLocal,
            final Object[] local,
            final int nStack,
            final Object[] stack) {
        instructions.add(
                new FrameNode(
                        type,
                        nLocal,
                        local == null ? null : getLabelNodes(local),
                        nStack,
                        stack == null ? null : getLabelNodes(stack)));
    }

    @Override
    public void visitInsn(final int opcode) {
        instructions.add(new InsnNode(opcode));
    }

    @Override
    public void visitIntInsn(final int opcode, final int operand) {
        instructions.add(new IntInsnNode(opcode, operand));
    }

    @Override
    public void visitVarInsn(final int opcode, final int var) {
        instructions.add(new VarInsnNode(opcode, var));
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        instructions.add(new TypeInsnNode(opcode, type));
    }

    @Override
    public void visitFieldInsn(
            final int opcode, final String owner, final String name, final String descriptor) {
        instructions.add(new FieldInsnNode(opcode, owner, name, descriptor));
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Override
    public void visitMethodInsn(
            final int opcode, final String owner, final String name, final String descriptor) {
        if (api >= Opcodes.ASM5) {
            super.visitMethodInsn(opcode, owner, name, descriptor);
            return;
        }
        instructions.add(new MethodInsnNode(opcode, owner, name, descriptor));
    }

    @Override
    public void visitMethodInsn(
            final int opcode,
            final String owner,
            final String name,
            final String descriptor,
            final boolean isInterface) {
        if (api < Opcodes.ASM5) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            return;
        }
        instructions.add(new MethodInsnNode(opcode, owner, name, descriptor, isInterface));
    }

    @Override
    public void visitInvokeDynamicInsn(
            final String name,
            final String descriptor,
            final Handle bootstrapMethodHandle,
            final Object... bootstrapMethodArguments) {
        instructions.add(
                new InvokeDynamicInsnNode(
                        name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments));
    }

    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        instructions.add(new JumpInsnNode(opcode, getLabelNode(label)));
    }

    @Override
    public void visitLabel(final Label label) {
        instructions.add(getLabelNode(label));
    }

    @Override
    public void visitLdcInsn(final Object value) {
        instructions.add(new LdcInsnNode(value));
    }

    @Override
    public void visitIincInsn(final int var, final int increment) {
        instructions.add(new IincInsnNode(var, increment));
    }

    @Override
    public void visitTableSwitchInsn(
            final int min, final int max, final Label dflt, final Label... labels) {
        instructions.add(new TableSwitchInsnNode(min, max, getLabelNode(dflt), getLabelNodes(labels)));
    }

    @Override
    public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
        instructions.add(new LookupSwitchInsnNode(getLabelNode(dflt), keys, getLabelNodes(labels)));
    }

    @Override
    public void visitMultiANewArrayInsn(final String descriptor, final int numDimensions) {
        instructions.add(new MultiANewArrayInsnNode(descriptor, numDimensions));
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(
            final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
        // Find the last real instruction, i.e. the instruction targeted by this annotation.
        AbstractInsnNode currentInsn = instructions.getLast();
        while (currentInsn.getOpcode() == -1) {
            currentInsn = currentInsn.getPrevious();
        }
        // Add the annotation to this instruction.
        TypeAnnotationNode typeAnnotation = new TypeAnnotationNode(typeRef, typePath, descriptor);
        if (visible) {
            if (currentInsn.visibleTypeAnnotations == null) {
                currentInsn.visibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
            }
            currentInsn.visibleTypeAnnotations.add(typeAnnotation);
        } else {
            if (currentInsn.invisibleTypeAnnotations == null) {
                currentInsn.invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>(1);
            }
            currentInsn.invisibleTypeAnnotations.add(typeAnnotation);
        }
        return typeAnnotation;
    }

    @Override
    public void visitTryCatchBlock(
            final Label start, final Label end, final Label handler, final String type) {
        tryCatchBlocks.add(
                new TryCatchBlockNode(getLabelNode(start), getLabelNode(end), getLabelNode(handler), type));
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(
            final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
        TryCatchBlockNode tryCatchBlock = tryCatchBlocks.get((typeRef & 0x00FFFF00) >> 8);
        TypeAnnotationNode typeAnnotation = new TypeAnnotationNode(typeRef, typePath, descriptor);
        if (visible) {
            if (tryCatchBlock.getVisibleTypeAnnotations() == null) {
                tryCatchBlock.setVisibleTypeAnnotations(new ArrayList<TypeAnnotationNode>(1));
            }
            tryCatchBlock.getVisibleTypeAnnotations().add(typeAnnotation);
        } else {
            if (tryCatchBlock.getInvisibleTypeAnnotations() == null) {
                tryCatchBlock.setInvisibleTypeAnnotations(new ArrayList<TypeAnnotationNode>(1));
            }
            tryCatchBlock.getInvisibleTypeAnnotations().add(typeAnnotation);
        }
        return typeAnnotation;
    }

    @Override
    public void visitLocalVariable(
            final String name,
            final String descriptor,
            final String signature,
            final Label start,
            final Label end,
            final int index) {
        localVariables.add(
                new LocalVariableNode(
                        name, descriptor, signature, getLabelNode(start), getLabelNode(end), index));
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(
            final int typeRef,
            final TypePath typePath,
            final Label[] start,
            final Label[] end,
            final int[] index,
            final String descriptor,
            final boolean visible) {
        LocalVariableAnnotationNode localVariableAnnotation =
                new LocalVariableAnnotationNode(
                        typeRef, typePath, getLabelNodes(start), getLabelNodes(end), index, descriptor);
        if (visible) {
            if (visibleLocalVariableAnnotations == null) {
                visibleLocalVariableAnnotations = new ArrayList<LocalVariableAnnotationNode>(1);
            }
            visibleLocalVariableAnnotations.add(localVariableAnnotation);
        } else {
            if (invisibleLocalVariableAnnotations == null) {
                invisibleLocalVariableAnnotations = new ArrayList<LocalVariableAnnotationNode>(1);
            }
            invisibleLocalVariableAnnotations.add(localVariableAnnotation);
        }
        return localVariableAnnotation;
    }

    @Override
    public void visitLineNumber(final int line, final Label start) {
        instructions.add(new LineNumberNode(line, getLabelNode(start)));
    }

    @Override
    public void visitMaxs(final int maxStack, final int maxLocals) {
        this.maxStack = maxStack;
        this.maxLocals = maxLocals;
    }

    @Override
    public void visitEnd() {
        // Nothing to do.
    }

    /**
     * Returns the LabelNode corresponding to the given Label. Creates a new LabelNode if necessary.
     * The default implementation of this method uses the {@link Label#info} field to store
     * associations between labels and label nodes.
     *
     * @param label a Label.
     * @return the LabelNode corresponding to label.
     */
    protected LabelNode getLabelNode(final Label label) {
        if (!(label.info instanceof LabelNode)) {
            label.info = new LabelNode();
        }
        return (LabelNode) label.info;
    }

    private LabelNode[] getLabelNodes(final Label[] labels) {
        LabelNode[] labelNodes = new LabelNode[labels.length];
        for (int i = 0, n = labels.length; i < n; ++i) {
            labelNodes[i] = getLabelNode(labels[i]);
        }
        return labelNodes;
    }

    private Object[] getLabelNodes(final Object[] objects) {
        Object[] labelNodes = new Object[objects.length];
        for (int i = 0, n = objects.length; i < n; ++i) {
            Object o = objects[i];
            if (o instanceof Label) {
                o = getLabelNode((Label) o);
            }
            labelNodes[i] = o;
        }
        return labelNodes;
    }

    // -----------------------------------------------------------------------------------------------
    // Accept method
    // -----------------------------------------------------------------------------------------------

    /**
     * Checks that this method node is compatible with the given ASM API version. This method checks
     * that this node, and all its children recursively, do not contain elements that were introduced
     * in more recent versions of the ASM API than the given version.
     *
     * @param api an ASM API version. Must be one of {@link Opcodes#ASM4}, {@link Opcodes#ASM5} or
     *            {@link Opcodes#ASM6}.
     */
    public void check(final int api) {
        if (api == Opcodes.ASM4) {
            if (parameters != null && !parameters.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
            if (visibleTypeAnnotations != null && !visibleTypeAnnotations.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
            if (invisibleTypeAnnotations != null && !invisibleTypeAnnotations.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
            if (tryCatchBlocks != null) {
                for (int i = tryCatchBlocks.size() - 1; i >= 0; --i) {
                    TryCatchBlockNode tryCatchBlock = tryCatchBlocks.get(i);
                    if (tryCatchBlock.getVisibleTypeAnnotations() != null
                            && !tryCatchBlock.getVisibleTypeAnnotations().isEmpty()) {
                        throw new UnsupportedClassVersionException();
                    }
                    if (tryCatchBlock.getInvisibleTypeAnnotations() != null
                            && !tryCatchBlock.getInvisibleTypeAnnotations().isEmpty()) {
                        throw new UnsupportedClassVersionException();
                    }
                }
            }
            for (int i = instructions.size() - 1; i >= 0; --i) {
                AbstractInsnNode insn = instructions.get(i);
                if (insn.visibleTypeAnnotations != null && !insn.visibleTypeAnnotations.isEmpty()) {
                    throw new UnsupportedClassVersionException();
                }
                if (insn.invisibleTypeAnnotations != null && !insn.invisibleTypeAnnotations.isEmpty()) {
                    throw new UnsupportedClassVersionException();
                }
                if (insn instanceof MethodInsnNode) {
                    boolean isInterface = ((MethodInsnNode) insn).isItf();
                    if (isInterface != (insn.opcode == Opcodes.INVOKEINTERFACE)) {
                        throw new UnsupportedClassVersionException();
                    }
                } else if (insn instanceof LdcInsnNode) {
                    Object value = ((LdcInsnNode) insn).getCst();
                    if (value instanceof Handle
                            || (value instanceof Type && ((Type) value).getSort() == Type.METHOD)) {
                        throw new UnsupportedClassVersionException();
                    }
                }
            }
            if (visibleLocalVariableAnnotations != null && !visibleLocalVariableAnnotations.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
            if (invisibleLocalVariableAnnotations != null
                    && !invisibleLocalVariableAnnotations.isEmpty()) {
                throw new UnsupportedClassVersionException();
            }
        }
    }

    /**
     * Makes the given class visitor visit this method.
     *
     * @param classVisitor a class visitor.
     */
    public void accept(final ClassVisitor classVisitor) {
        String[] exceptionsArray = new String[this.exceptions.size()];
        this.exceptions.toArray(exceptionsArray);
        MethodVisitor methodVisitor =
                classVisitor.visitMethod(access, name, desc, signature, exceptionsArray);
        if (methodVisitor != null) {
            accept(methodVisitor);
        }
    }

    /**
     * Makes the given method visitor visit this method.
     *
     * @param methodVisitor a method visitor.
     */
    public void accept(final MethodVisitor methodVisitor) {
        // Visit the parameters.
        if (parameters != null) {
            for (int i = 0, n = parameters.size(); i < n; i++) {
                parameters.get(i).accept(methodVisitor);
            }
        }
        // Visit the annotations.
        if (annotationDefault != null) {
            AnnotationVisitor annotationVisitor = methodVisitor.visitAnnotationDefault();
            AnnotationNode.accept(annotationVisitor, null, annotationDefault);
            if (annotationVisitor != null) {
                annotationVisitor.visitEnd();
            }
        }
        if (visibleAnnotations != null) {
            for (int i = 0, n = visibleAnnotations.size(); i < n; ++i) {
                AnnotationNode annotation = visibleAnnotations.get(i);
                annotation.accept(methodVisitor.visitAnnotation(annotation.getDesc(), true));
            }
        }
        if (invisibleAnnotations != null) {
            for (int i = 0, n = invisibleAnnotations.size(); i < n; ++i) {
                AnnotationNode annotation = invisibleAnnotations.get(i);
                annotation.accept(methodVisitor.visitAnnotation(annotation.getDesc(), false));
            }
        }
        if (visibleTypeAnnotations != null) {
            for (int i = 0, n = visibleTypeAnnotations.size(); i < n; ++i) {
                TypeAnnotationNode typeAnnotation = visibleTypeAnnotations.get(i);
                typeAnnotation.accept(
                        methodVisitor.visitTypeAnnotation(
                                typeAnnotation.getTypeRef(), typeAnnotation.getTypePath(), typeAnnotation.getDesc(), true));
            }
        }
        if (invisibleTypeAnnotations != null) {
            for (int i = 0, n = invisibleTypeAnnotations.size(); i < n; ++i) {
                TypeAnnotationNode typeAnnotation = invisibleTypeAnnotations.get(i);
                typeAnnotation.accept(
                        methodVisitor.visitTypeAnnotation(
                                typeAnnotation.getTypeRef(), typeAnnotation.getTypePath(), typeAnnotation.getDesc(), false));
            }
        }
        if (visibleAnnotableParameterCount > 0) {
            methodVisitor.visitAnnotableParameterCount(visibleAnnotableParameterCount, true);
        }
        if (visibleParameterAnnotations != null) {
            for (int i = 0, n = visibleParameterAnnotations.length; i < n; ++i) {
                List<AnnotationNode> parameterAnnotations = visibleParameterAnnotations[i];
                if (parameterAnnotations == null) {
                    continue;
                }
                for (int j = 0, m = parameterAnnotations.size(); j < m; ++j) {
                    AnnotationNode annotation = parameterAnnotations.get(j);
                    annotation.accept(methodVisitor.visitParameterAnnotation(i, annotation.getDesc(), true));
                }
            }
        }
        if (invisibleAnnotableParameterCount > 0) {
            methodVisitor.visitAnnotableParameterCount(invisibleAnnotableParameterCount, false);
        }
        if (invisibleParameterAnnotations != null) {
            for (int i = 0, n = invisibleParameterAnnotations.length; i < n; ++i) {
                List<AnnotationNode> parameterAnnotations = invisibleParameterAnnotations[i];
                if (parameterAnnotations == null) {
                    continue;
                }
                for (int j = 0, m = parameterAnnotations.size(); j < m; ++j) {
                    AnnotationNode annotation = parameterAnnotations.get(j);
                    annotation.accept(methodVisitor.visitParameterAnnotation(i, annotation.getDesc(), false));
                }
            }
        }
        // Visit the non standard attributes.
        if (visited) {
            instructions.resetLabels();
        }
        if (attrs != null) {
            for (int i = 0, n = attrs.size(); i < n; ++i) {
                methodVisitor.visitAttribute(attrs.get(i));
            }
        }
        // Visit the code.
        if (instructions.size() > 0) {
            methodVisitor.visitCode();
            // Visits the try catch blocks.
            if (tryCatchBlocks != null) {
                for (int i = 0, n = tryCatchBlocks.size(); i < n; ++i) {
                    tryCatchBlocks.get(i).updateIndex(i);
                    tryCatchBlocks.get(i).accept(methodVisitor);
                }
            }
            // Visit the instructions.
            instructions.accept(methodVisitor);
            // Visits the local variables.
            if (localVariables != null) {
                for (int i = 0, n = localVariables.size(); i < n; ++i) {
                    localVariables.get(i).accept(methodVisitor);
                }
            }
            // Visits the local variable annotations.
            if (visibleLocalVariableAnnotations != null) {
                for (int i = 0, n = visibleLocalVariableAnnotations.size(); i < n; ++i) {
                    visibleLocalVariableAnnotations.get(i).accept(methodVisitor, true);
                }
            }
            if (invisibleLocalVariableAnnotations != null) {
                for (int i = 0, n = invisibleLocalVariableAnnotations.size(); i < n; ++i) {
                    invisibleLocalVariableAnnotations.get(i).accept(methodVisitor, false);
                }
            }
            methodVisitor.visitMaxs(maxStack, maxLocals);
            visited = true;
        }
        methodVisitor.visitEnd();
    }
}
