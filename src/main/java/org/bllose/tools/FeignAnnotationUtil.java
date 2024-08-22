package org.bllose.tools;

import com.alibaba.fastjson.JSONObject;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import org.apache.commons.lang3.StringUtils;
import org.bllose.discovery.ServerDiscover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class FeignAnnotationUtil {

    private static final Logger log = LoggerFactory.getLogger(FeignAnnotationUtil.class);

    /**
     *
     * @param classFile
     * @param constPool
     * @param annotations
     * @return
     */
    public static boolean putUrlInThisAnnotationOnClassFile(ClassFile classFile, ConstPool constPool, String annotations) {
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

        for (Annotation annotation : annotationsAttribute.getAnnotations()) {
            String name = annotation.getTypeName();
            if(name.endsWith("FeignClient")) {
                MemberValue nameValue = annotation.getMemberValue("name");
                if(Objects.nonNull(nameValue)) {
                    String serverName = nameValue.toString().replace("\"", "");
                    String curUrl = ServerDiscover.fetchByServerName(serverName);

                    if(StringUtils.isNotBlank(curUrl)) {
                        MemberValue mv = new StringMemberValue(curUrl, constPool);
                        MemberValue mvName = new StringMemberValue(serverName, constPool);

                        Annotation curAnnotation = new Annotation(annotations, constPool);

                        curAnnotation.addMemberValue("url", mv);
                        curAnnotation.addMemberValue("name", mvName);

                        annotation = curAnnotation;
                        isUpdate = true;

                        log.info("Feign : {} -> {}", classFile.getName(), curUrl);
                    } else {
                        log.warn("Feign : {} CAN'T FETCH URL", serverName);
                    }
                }
            }

            updateAnnotationsAttribute.addAnnotation(annotation);
        }

        // 更新注解
        if (isUpdate) {
            classFile.addAttribute(updateAnnotationsAttribute);
//            log.info("classFile need to by update! {} {}", JSONObject.toJSONString(classFile), classFile);
        }

        return isUpdate;
    }


    /**
     *
     * @param method
     * @param targetAnnotation org.springframework.cloud.openfeign.FeignClient
     * @param url
     * @return
     */
    public static boolean putUrlInThisAnnotationOnMethod(CtMethod method, String targetAnnotation, String url) {
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
