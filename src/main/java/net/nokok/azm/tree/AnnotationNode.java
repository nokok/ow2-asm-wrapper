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
package net.nokok.azm.tree;

import net.nokok.azm.AnnotationVisitor;
import net.nokok.azm.Opcodes;
import net.nokok.azm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A node that represents an annotation.
 *
 * @author Eric Bruneton
 */
public class AnnotationNode extends AnnotationVisitor {

    private String desc;

    private List<Object> values;

    /**
     * Constructs a new {@link AnnotationNode}. <i>Subclasses must not use this constructor</i>.
     * Instead, they must use the {@link #AnnotationNode(int, String)} version.
     *
     * @param descriptor the class descriptor of the annotation class.
     * @throws IllegalStateException If a subclass calls this constructor.
     */
    public AnnotationNode(final String descriptor) {
        this(Opcodes.ASM6, descriptor);
        if (getClass() != AnnotationNode.class) {
            throw new IllegalStateException();
        }
    }

    /**
     * Constructs a new {@link AnnotationNode}.
     *
     * @param api        the ASM API version implemented by this visitor. Must be one of {@link
     *                   Opcodes#ASM4}, {@link Opcodes#ASM5} or {@link Opcodes#ASM6}.
     * @param descriptor the class descriptor of the annotation class.
     */
    public AnnotationNode(final int api, final String descriptor) {
        super(api);
        this.setDesc(descriptor);
    }

    /**
     * Constructs a new {@link AnnotationNode} to visit an array value.
     *
     * @param values where the visited values must be stored.
     */
    AnnotationNode(final List<Object> values) {
        super(Opcodes.ASM6);
        this.setValues(values);
    }

    // ------------------------------------------------------------------------
    // Implementation of the AnnotationVisitor abstract class
    // ------------------------------------------------------------------------

    @Override
    public void visit(final String name, final Object value) {
        if (getValues() == null) {
            setValues(new ArrayList<Object>(this.getDesc() != null ? 2 : 1));
        }
        if (this.getDesc() != null) {
            getValues().add(name);
        }
        if (value instanceof byte[]) {
            getValues().add(Util.asArrayList((byte[]) value));
        } else if (value instanceof boolean[]) {
            getValues().add(Util.asArrayList((boolean[]) value));
        } else if (value instanceof short[]) {
            getValues().add(Util.asArrayList((short[]) value));
        } else if (value instanceof char[]) {
            getValues().add(Util.asArrayList((char[]) value));
        } else if (value instanceof int[]) {
            getValues().add(Util.asArrayList((int[]) value));
        } else if (value instanceof long[]) {
            getValues().add(Util.asArrayList((long[]) value));
        } else if (value instanceof float[]) {
            getValues().add(Util.asArrayList((float[]) value));
        } else if (value instanceof double[]) {
            getValues().add(Util.asArrayList((double[]) value));
        } else {
            getValues().add(value);
        }
    }

    @Override
    public void visitEnum(final String name, final String descriptor, final String value) {
        if (getValues() == null) {
            setValues(new ArrayList<Object>(this.getDesc() != null ? 2 : 1));
        }
        if (this.getDesc() != null) {
            getValues().add(name);
        }
        getValues().add(new String[]{descriptor, value});
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String name, final String descriptor) {
        if (getValues() == null) {
            setValues(new ArrayList<Object>(this.getDesc() != null ? 2 : 1));
        }
        if (this.getDesc() != null) {
            getValues().add(name);
        }
        AnnotationNode annotation = new AnnotationNode(descriptor);
        getValues().add(annotation);
        return annotation;
    }

    @Override
    public AnnotationVisitor visitArray(final String name) {
        if (getValues() == null) {
            setValues(new ArrayList<Object>(this.getDesc() != null ? 2 : 1));
        }
        if (this.getDesc() != null) {
            getValues().add(name);
        }
        List<Object> array = new ArrayList<Object>();
        getValues().add(array);
        return new AnnotationNode(array);
    }

    @Override
    public void visitEnd() {
        // Nothing to do.
    }

    // ------------------------------------------------------------------------
    // Accept methods
    // ------------------------------------------------------------------------

    /**
     * Checks that this annotation node is compatible with the given ASM API version. This method
     * checks that this node, and all its children recursively, do not contain elements that were
     * introduced in more recent versions of the ASM API than the given version.
     *
     * @param api an ASM API version. Must be one of {@link Opcodes#ASM4}, {@link Opcodes#ASM5} or
     *            {@link Opcodes#ASM6}.
     */
    public void check(final int api) {
        // nothing to do
    }

    /**
     * Makes the given visitor visit this annotation.
     *
     * @param annotationVisitor an annotation visitor. Maybe <tt>null</tt>.
     */
    public void accept(final AnnotationVisitor annotationVisitor) {
        if (annotationVisitor != null) {
            if (getValues() != null) {
                for (int i = 0, n = getValues().size(); i < n; i += 2) {
                    String name = (String) getValues().get(i);
                    Object value = getValues().get(i + 1);
                    accept(annotationVisitor, name, value);
                }
            }
            annotationVisitor.visitEnd();
        }
    }

    /**
     * Makes the given visitor visit a given annotation value.
     *
     * @param annotationVisitor an annotation visitor. Maybe <tt>null</tt>.
     * @param name              the value name.
     * @param value             the actual value.
     */
    static void accept(
            final AnnotationVisitor annotationVisitor, final String name, final Object value) {
        if (annotationVisitor != null) {
            if (value instanceof String[]) {
                String[] typeValue = (String[]) value;
                annotationVisitor.visitEnum(name, typeValue[0], typeValue[1]);
            } else if (value instanceof AnnotationNode) {
                AnnotationNode annotationValue = (AnnotationNode) value;
                annotationValue.accept(annotationVisitor.visitAnnotation(name, annotationValue.getDesc()));
            } else if (value instanceof List) {
                AnnotationVisitor arrayAnnotationVisitor = annotationVisitor.visitArray(name);
                if (arrayAnnotationVisitor != null) {
                    List<?> arrayValue = (List<?>) value;
                    for (int i = 0, n = arrayValue.size(); i < n; ++i) {
                        accept(arrayAnnotationVisitor, null, arrayValue.get(i));
                    }
                    arrayAnnotationVisitor.visitEnd();
                }
            } else {
                annotationVisitor.visit(name, value);
            }
        }
    }

    /**
     * The class descriptor of the annotation class.
     */
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * The name value pairs of this annotation. Each name value pair is stored as two consecutive
     * elements in the list. The name is a {@link String}, and the value may be a {@link Byte}, {@link
     * Boolean}, {@link Character}, {@link Short}, {@link Integer}, {@link Long}, {@link Float},
     * {@link Double}, {@link String} or {@link Type}, or a two elements String
     * array (for enumeration values), an {@link AnnotationNode}, or a {@link List} of values of one
     * of the preceding types. The list may be <tt>null</tt> if there is no name value pair.
     */
    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnotationNode that = (AnnotationNode) o;
        return Objects.equals(getDesc(), that.getDesc()) &&
                Objects.equals(getValues(), that.getValues());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getDesc(), getValues());
    }

    @Override
    public String toString() {
        return "AnnotationNode{" +
                "desc='" + desc + '\'' +
                ", values=" + values +
                '}';
    }
}
