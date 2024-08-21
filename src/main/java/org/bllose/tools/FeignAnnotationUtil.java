package org.bllose.tools;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FeignAnnotationUtil {

    private static final Logger log = LoggerFactory.getLogger(FeignAnnotationUtil.class);

    /**
     *
     * @param classFile
     * @param constPool
     * @param annotations
     * @param url
     * @return
     */
    public static boolean putUrlInThisAnnotation(ClassFile classFile, ConstPool constPool, String annotations, String url) {
        if (classFile == null || constPool == null) {
            log.warn("removeAnnotations: classFile or constPool is null.");
            return false;
        }

        AnnotationsAttribute annotationsAttribute = getVisibleAnnotationsAttribute(classFile).orElse(null);
        if (annotationsAttribute == null) {
            log.warn("removeAnnotations: annotationsAttribute is null.");
            return false;
        }

        boolean isUpdate = false;

        AnnotationsAttribute updateAnnotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);

        Set<String> doneSet = new HashSet<>();

        for (Annotation annotation : annotationsAttribute.getAnnotations()) {
            String name = annotation.getTypeName();
            if(name.endsWith("FeignClient")) {
                String serverName = annotation.getMemberValue("name").toString().replace("\"", "");

                MemberValue mv = new StringMemberValue("http://localhost:8070", constPool);
                Annotation curAnnotation = new Annotation(annotations, constPool);
                curAnnotation.addMemberValue("url", mv);

                annotation = curAnnotation;
                isUpdate = true;
            }

            updateAnnotationsAttribute.addAnnotation(annotation);
        }

        // 更新注解
        if (isUpdate) {
            classFile.addAttribute(updateAnnotationsAttribute);
        }

        log.info("removeAnnotations: successfully, {}, {}", classFile.getName(), String.join(",", doneSet));

        return isUpdate;
    }


    /**
     *
     * @param method
     * @param targetAnnotation org.springframework.cloud.openfeign.FeignClient
     * @param url
     * @return
     */
    public static boolean putUrlInThisAnnotation(CtMethod method, String targetAnnotation, String url) {
        if (method == null) {
            log.warn("addAnnotations: method is null.");
            return false;
        }

        if (StringUtils.isBlank(targetAnnotation)) {
            targetAnnotation = "org/springframework/cloud/openfeign/FeignClient";
        }

        ConstPool constPool = method.getMethodInfo().getConstPool();

        if (constPool == null) {
            log.warn("addAnnotations: method {}'s constPool is null.", method.getName());
            return false;
        }

        AnnotationsAttribute annotationsAttribute = getVisibleAnnotationsAttribute(method)
                .orElse(new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag));
        if (annotationsAttribute == null) {
            log.warn("addAnnotations: method {}'s annotationsAttribute is null.", method.getName());
            return false;
        }

        AnnotationsAttribute updateAnnotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);

        for (Annotation annotation : annotationsAttribute.getAnnotations()) {
            String name = annotation.getTypeName();
            System.out.println(name);
        }
        return false;
    }

    public static Optional<AnnotationsAttribute> getVisibleAnnotationsAttribute(CtMethod method) {
        if (method == null || method.getMethodInfo() == null) {
            return Optional.empty();
        }

        AttributeInfo attributeInfo = method.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
        if (attributeInfo == null || !(attributeInfo instanceof AnnotationsAttribute)) {
            return Optional.empty();
        }

        return Optional.of((AnnotationsAttribute) attributeInfo);
    }

    public static Optional<AnnotationsAttribute> getVisibleAnnotationsAttribute(ClassFile classFile) {
        if (classFile == null) {
            return Optional.empty();
        }

        AttributeInfo attributeInfo = classFile.getAttribute(AnnotationsAttribute.visibleTag);
        if (attributeInfo == null || !(attributeInfo instanceof AnnotationsAttribute)) {
            return Optional.empty();
        }

        return Optional.of((AnnotationsAttribute) attributeInfo);
    }
}
