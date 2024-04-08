package com.whx.router.compiler.processor;

import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.whx.router.compiler.utils.Constants;
import com.whx.router.annotation.Param;
import com.whx.router.annotation.Route;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

@AutoService(Processor.class)
public class ParamProcessor extends BaseProcessor {

    private final Map<TypeElement, List<Element>> paramList = new HashMap<>();
    TypeElement mIntent;
    TypeElement mBundle;
    TypeElement mParamException;
    TypeElement mIJsonService;
    TypeElement mGoRouter;
    TypeElement mRouterException;
    TypeElement mTypeWrapper;
    TypeMirror activityType;
    TypeMirror fragmentType;
    TypeMirror serializableType;
    TypeMirror parcelableType;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<>();
        set.add(Param.class.getCanonicalName());
        return set;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        logger.info(moduleName + " >>> ParamProcessor init. <<<");
        mIntent = elementUtils.getTypeElement(Constants.INTENT);
        mBundle = elementUtils.getTypeElement(Constants.BUNDLE);
        mParamException = elementUtils.getTypeElement(Constants.PARAM_EXCEPTION);
        mIJsonService = elementUtils.getTypeElement(Constants.I_JSON_SERVICE);
        mGoRouter = elementUtils.getTypeElement(Constants.MROUTER);
        mRouterException = elementUtils.getTypeElement(Constants.ROUTER_EXCEPTION);
        mTypeWrapper = elementUtils.getTypeElement(Constants.TYPE_WRAPPER);
        activityType = elementUtils.getTypeElement(Constants.ACTIVITY).asType();
        fragmentType = elementUtils.getTypeElement(Constants.FRAGMENT).asType();
        serializableType = elementUtils.getTypeElement(Constants.SERIALIZABLE_PACKAGE).asType();
        parcelableType = elementUtils.getTypeElement(Constants.PARCELABLE_PACKAGE).asType();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isEmpty(set))
            return false;

        try {
            categories(roundEnvironment.getElementsAnnotatedWith(Param.class));
        } catch (IllegalAccessException e) {
            logger.error(moduleName + " " + e.getMessage());
        }

        if (MapUtils.isNotEmpty(paramList)) {
            for (Map.Entry<TypeElement, List<Element>> entry : paramList.entrySet()) {

                TypeElement parent = entry.getKey();
                List<Element> childs = entry.getValue();

                String qualifiedName = parent.getQualifiedName().toString();
                String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
                String className = parent.getSimpleName() + Constants.PARAM_CLASS_NAME_SUFFIX;

                LinkedHashSet<MethodSpec> injectMethods = addInjectMethod(parent, childs, false);
                LinkedHashSet<MethodSpec> injectCheckMethods = addInjectMethod(parent, childs, true);

                try {
                    JavaFile.builder(packageName,
                                    TypeSpec.classBuilder(className)
                                            .addModifiers(PUBLIC)
                                            .addJavadoc(Constants.WARNING_TIPS)
                                            .addMethods(injectMethods)
                                            .addMethods(injectCheckMethods)
                                            .build())
                            .indent("    ")
                            .build()
                            .writeTo(mFiler);
                } catch (IOException e) {
                    logger.error(moduleName + " Failed to generate [" + className + "] class!");
                    logger.error(e);
                }
            }
        }
        logger.info(moduleName + " >>> ParamProcessor over. <<<");

        return true;
    }

    private LinkedHashSet<MethodSpec> addInjectMethod(TypeElement parent, List<Element> childs, boolean isCheck) {
        LinkedHashSet<MethodSpec> methodList = new LinkedHashSet<>();
        MethodSpec.Builder method = MethodSpec.methodBuilder(isCheck ? Constants.METHOD_NAME_INJECT_CHECK : Constants.METHOD_NAME_INJECT)
                .addParameter(ClassName.get(parent), "self")
                .addModifiers(PUBLIC, STATIC);

        if (isCheck) {
            method.addException(ClassName.get(mParamException));
        }

        if (types.isSubtype(parent.asType(), activityType)) {
            MethodSpec.Builder method1 = method.build().toBuilder();
            method1.addStatement("$L(self, self.getIntent().getExtras())", method.build().name);
            methodList.add(method1.build());

            MethodSpec.Builder method2 = method.build().toBuilder().addParameter(ClassName.get(mIntent), "intent");
            method2.addStatement("$L(self, intent.getExtras())", method.build().name);
            methodList.add(method2.build());
        } else if (types.isSubtype(parent.asType(), fragmentType)) {
            MethodSpec.Builder method1 = method.build().toBuilder();
            method1.addStatement("$L(self, self.getArguments())", method.build().name);
            methodList.add(method1.build());
        } else {
            throw new RuntimeException(Constants.PREFIX_OF_LOGGER + moduleName + " @Param can only be used in activity and fragment, Current type [" + parent.asType().toString() + "].");
        }
        method.addParameter(ClassName.get(mBundle), "bundle");

        method.beginControlFlow("if (bundle == null)");
        if (isCheck) {
            String requiredName = null;
            for (Element field : childs) {
                Param param = field.getAnnotation(Param.class);
                if (param.required()) {
                    String key = field.getSimpleName().toString();
                    requiredName = !StringUtils.isEmpty(param.name()) ? param.name() : key;
                    break;
                }
            }
            if (requiredName != null) {
                method.addStatement("throw new ParamException($S)", requiredName);
            } else {
                method.addStatement("return");
            }
        } else {
            method.addStatement("return");
        }
        method.endControlFlow();

        boolean isJsonService = false;

        for (Element field : childs) {
            Param param = field.getAnnotation(Param.class);

            method.addCode("// $L\n", param.remark());
            String key = field.getSimpleName().toString();
            String name = !StringUtils.isEmpty(param.name()) ? param.name() : key;

            CodeBlock.Builder itemCode = CodeBlock.builder();
            TypeMirror typeMirror = field.asType();
            String typeStr = typeMirror.toString();
            boolean isSelfHandleRequired = false;
            switch (typeStr) {
                case Constants.BYTE_PACKAGE, Constants.BYTE_PRIMITIVE -> itemCode.addStatement("self.$N = bundle.getByte($S, self.$N)", key, name, key);
                case Constants.SHORT_PACKAGE, Constants.SHORT_PRIMITIVE -> itemCode.addStatement("self.$N = bundle.getShort($S, self.$N)", key, name, key);
                case Constants.INTEGER_PACKAGE, Constants.INTEGER_PRIMITIVE -> itemCode.addStatement("self.$N = bundle.getInt($S, self.$N)", key, name, key);
                case Constants.LONG_PACKAGE, Constants.LONG_PRIMITIVE -> itemCode.addStatement("self.$N = bundle.getLong($S, self.$N)", key, name, key);
                case Constants.FLOAT_PACKAGE, Constants.FLOAT_PRIMITIVE -> itemCode.addStatement("self.$N = bundle.getFloat($S, self.$N)", key, name, key);
                case Constants.DOUBLE_PACKAGE, Constants.DOUBLE_PRIMITIVE -> itemCode.addStatement("self.$N = bundle.getDouble($S, self.$N)", key, name, key);
                case Constants.BOOLEAN_PACKAGE, Constants.BOOLEAN_PRIMITIVE -> itemCode.addStatement("self.$N = bundle.getBoolean($S, self.$N)", key, name, key);
                case Constants.CHAR_PACKAGE, Constants.CHAR_PRIMITIVE -> itemCode.addStatement("self.$N = bundle.getChar($S, self.$N)", key, name, key);
                case Constants.STRING_PACKAGE -> itemCode.addStatement("self.$N = bundle.getString($S, self.$N)", key, name, key);
                default -> {
                    if (types.isSubtype(typeMirror, parcelableType)) {
                        itemCode.beginControlFlow("if (bundle.containsKey($S))", name);
                        itemCode.addStatement("self.$N = bundle.getParcelable($S)", key, name);

                        isSelfHandleRequired = true;
                        if (param.required() && isCheck) {
                            itemCode.nextControlFlow("else");
                            itemCode.addStatement("throw new ParamException($S)", name);
                            itemCode.endControlFlow();
                        } else {
                            itemCode.endControlFlow();
                        }
                    } else if (types.isSubtype(typeMirror, serializableType)) {
                        itemCode.beginControlFlow("if (bundle.containsKey($S))", name);
                        itemCode.addStatement("self.$N = ($L) bundle.getSerializable($S)", key, typeStr, name);

                        isSelfHandleRequired = true;
                        if (param.required() && isCheck) {
                            itemCode.nextControlFlow("else");
                            itemCode.addStatement("throw new ParamException($S)", name);
                            itemCode.endControlFlow();
                        } else {
                            itemCode.endControlFlow();
                        }
                    } else {
                        if (!isJsonService) {
                            isJsonService = true;
                            method.addStatement("$T jsonService = $T.getInstance().getService($T.class)", mIJsonService, mGoRouter, mIJsonService);
                        }
                        itemCode.beginControlFlow("if (jsonService != null)");
                        itemCode.addStatement("self.$N = jsonService.parseObject(bundle.getString($S), new $L<$L>() {}.getType())", key, name, mTypeWrapper, typeStr);
                        itemCode.nextControlFlow("else");
                        itemCode.addStatement("throw new $T($S)", mRouterException, "To use withObject() method, you need to implement IJsonService");
                        itemCode.endControlFlow();
                    }
                }
            }

            // required=true的情况
            if (param.required() && isCheck && !isSelfHandleRequired) {
                method.beginControlFlow("if (bundle.containsKey($S))", name);
                method.addCode(itemCode.build());
                method.nextControlFlow("else");
                method.addStatement("throw new ParamException($S)", name);
                method.endControlFlow();
            } else {
                method.addCode(itemCode.build());
            }
        }
        methodList.add(method.build());
        return methodList;
    }

    /**
     * Categories field, find his papa.
     *
     * @param elements
     * @throws IllegalAccessException Field need @Param
     */
    private void categories(Set<? extends Element> elements) throws IllegalAccessException {
        if (CollectionUtils.isNotEmpty(elements)) {
            Map<TypeElement, List<Element>> parentParamList = new HashMap<>();
            for (Element element : elements) {
                TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

                if (element.getModifiers().contains(Modifier.PRIVATE)) {
                    throw new IllegalAccessException("The inject fields CAN NOT BE 'private'!!! please check field [" + element.getSimpleName() + "] in class [" + enclosingElement.getQualifiedName() + "]");
                }

                if (enclosingElement.getAnnotation(Route.class) != null) {
                    if (paramList.containsKey(enclosingElement)) {
                        paramList.get(enclosingElement).add(element);
                    } else {
                        List<Element> childs = new ArrayList<>();
                        childs.add(element);
                        paramList.put(enclosingElement, childs);
                    }
                } else {
                    if (parentParamList.containsKey(enclosingElement)) {
                        parentParamList.get(enclosingElement).add(element);
                    } else {
                        List<Element> childs = new ArrayList<>();
                        childs.add(element);
                        parentParamList.put(enclosingElement, childs);
                    }
                }
            }
            // 处理父类里的参数
            for (Map.Entry<TypeElement, List<Element>> parentEntry : parentParamList.entrySet()) {
                for (Map.Entry<TypeElement, List<Element>> childEntry : paramList.entrySet()) {
                    handleParentParam(parentEntry.getKey(), parentEntry.getValue(), childEntry.getKey(), childEntry.getValue());
                }
            }
            logger.info(moduleName + " categories finished.");
        }
    }

    private static void handleParentParam(TypeElement parentKey, List<Element> parentValue, Element childKey, List<Element> childValue) {
        TypeMirror parent = ((TypeElement) childKey).getSuperclass();
        if (StringUtils.equals(parent.toString(), parentKey.getQualifiedName().toString())) {
            childValue.addAll(parentValue);
        } else {
            if (parent instanceof DeclaredType) {
                Element parentElement = ((DeclaredType) parent).asElement();
                if (parentElement instanceof TypeElement && !((TypeElement) parentElement).getQualifiedName().toString().startsWith("android")) {
                    handleParentParam(parentKey, parentValue, parentElement, childValue);
                }
            }
        }
    }

}
